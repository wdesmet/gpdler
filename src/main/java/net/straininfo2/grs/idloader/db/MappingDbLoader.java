package net.straininfo2.grs.idloader.db;

import net.straininfo2.grs.idloader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MappingDbLoader {

	private final static Logger logger = LoggerFactory.getLogger(MappingDbLoader.class);
	
	private SimpleJdbcTemplate template;
	
	private TransactionTemplate txTemplate;
	
	private String namespace;

    private ConcurrentHashMap<Category, Category> categoryMap;

    private ConcurrentHashMap<Provider, Provider> providerMap;

    public MappingDbLoader() {
        this.categoryMap = new ConcurrentHashMap<Category, Category>();
        this.providerMap = new ConcurrentHashMap<Provider, Provider>();
    }
	
	public void setDataSource(DataSource source) {
		this.template = new SimpleJdbcTemplate(source);
	}
	
	public SimpleJdbcTemplate getTemplate() {
		return this.template;
	}
	
	public void setTransactionManager(PlatformTransactionManager manager) {
		this.txTemplate = new TransactionTemplate(manager);
		this.txTemplate.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	public TransactionTemplate getTxTemplate() {
		return this.txTemplate;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

    public void setCategoryMap(ConcurrentHashMap<Category, Category> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public void setProviderMap(ConcurrentHashMap<Provider, Provider> providerMap) {
        this.providerMap = providerMap;
    }

    protected abstract String insertMappingQuery();

    /**
     * Creates and initialises tables in targer DB.
     */
    public abstract void configureTables();
	
    static void addTargetIds(List<Mapping> mappings, Map<Provider, TargetIdExtractor> extractors) {
        for (Mapping mapping : mappings) {
            mapping.computeTargetId(extractors.get(mapping.getProvider()));
        }
    }

	public void updateIfChanged(final Map<Integer, List<Mapping>> grsMapping, final Map<Provider, TargetIdExtractor> extractors) {
		final Set<Provider> allProviders = new HashSet<Provider>(
				getTemplate().query("SELECT id, name, abbr, url FROM " + namespace + ".providers", 
				new RowMapper<Provider>() {

					@Override
					public Provider mapRow(ResultSet rs, int rowNum)
							throws SQLException {
                        Provider provider = new Provider(rs.getString("name"), rs.getString("abbr"),
                                rs.getInt("id"), rs.getString("url"));
						return Loader.dedupKey(providerMap, provider);
					}

				}));
		getTxTemplate().execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for (Entry<Integer, List<Mapping>> entry : grsMapping.entrySet()) {
					Integer id = entry.getKey();
					try {
						getTemplate().update("INSERT INTO " + namespace + ".genome_projects (id) VALUES (?)", id);
					} catch (DataAccessException e) {
						// value already exists, shouldn't be fatal
						logger.debug("Caught access exception inserting id value, continuing");
					}
					List<Mapping> newMapping = entry.getValue();
                    addTargetIds(newMapping, extractors);
					List<Mapping> curMapping = queryMapping(id);
					if (Mapping.differentMapping(curMapping, newMapping)) {
						logger.debug("Found a different mapping for id {}, clearing and inserting", id);
						getTemplate().update("DELETE FROM " + namespace + ".mappings WHERE project_id=?", id);
						Set<Provider> providers = Mapping.listProviders(newMapping);
						providers.removeAll(allProviders);
						if (!providers.isEmpty()) {
							try {
								getTemplate()
										.batchUpdate(
												"INSERT INTO " + namespace + ".providers(id, name, abbr, url) "
														+ "VALUES (:id, :name, :abbr, :url)",
												SqlParameterSourceUtils
														.createBatch(providers
																.toArray()));
                                allProviders.addAll(providers);
							} catch (DataAccessException e) {
								logger.error(
										"Cannot insert providers {} into db, rethrowing error {}",
										providers, e);
                                throw new RuntimeException("Could not insert provider.");
							}
						}
						// now update mappings
						for (Mapping mapping : newMapping) {
                            logger.debug("Inserting mapping for id {}", id);
							getTemplate().update(insertMappingQuery(),
									mapping.getUrl(), mapping.getSubjectType(), mapping.getLinkName(),
									mapping.getCategory().toString(), mapping.getProvider().getId(), id,
                                    mapping.getTargetId());
						}
					}
					else {
						logger.debug("Mappings for project id {} haven't changed, continuing.", id);
					}
			}
		}});
	}

    List<Mapping> queryMapping(int id) {
		return getTemplate().query("SELECT m.url AS m_url, m.subject_type, m.link_name, m.category, m.target_id, " +
				"pr.id, pr.name, pr.abbr, pr.url AS pr_url " +
				"FROM " + namespace + ".mappings m " +
				"INNER JOIN " + namespace + ".providers pr " +
				"ON m.provider_id = pr.id " +
				"WHERE m.project_id=?", 
				new RowMapper<Mapping>() {

					@Override
					public Mapping mapRow(ResultSet rs, int rowNum)
							throws SQLException {
                        Category c = new Category(rs.getString("category"));
                        Provider p = new Provider(rs.getString("name"), rs.getString("abbr"),
                                rs.getInt("id"), rs.getString("pr_url"));
						return new Mapping(rs.getString("m_url"), rs.getString("subject_type"), 
								rs.getString("link_name"), rs.getString("target_id"),
                                Loader.dedupKey(categoryMap, c), Loader.dedupKey(providerMap, p));
					}
				}, id);
	}

}
