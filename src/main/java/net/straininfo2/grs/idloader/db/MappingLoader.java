package net.straininfo2.grs.idloader.db;

import net.straininfo2.grs.idloader.TargetIdExtractor;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;
import net.straininfo2.grs.idloader.bioproject.eutils.MappingHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@Transactional
public class MappingLoader implements MappingHandler {

    @Autowired
    SessionFactory factory;

    private final static Logger logger = LoggerFactory.getLogger(MappingHandler.class);

    @Override
    public void addMapping(long bioProjectId, Mapping mapping, TargetIdExtractor extractor) {
        Session session = factory.getCurrentSession();
        BioProject project = (BioProject)session.get(BioProject.class, bioProjectId);
        if (project == null) {
            // this should not happen, as it should always be in the XML file, but trust no-one
            logger.error("Bioproject with id {} not found, cannot load mapping.", bioProjectId);
        }
        else {
            logger.info("Saving mapping {} for bioproject {}", mapping.getLinkName(), bioProjectId);
            mapping.computeTargetId(extractor);
            project.getMappings().add(mapping);
            mapping.setBioProject(project);
            session.merge(project);
        }
    }

    @Override
    public void handleMappings(long bioProjectId, Collection<Mapping> mappings, Map<Provider, TargetIdExtractor> extractors) {
        Session session = factory.getCurrentSession();
        BioProject project = (BioProject)session.get(BioProject.class, bioProjectId);
        if (project == null) {
            logger.error("Bioproject with id {} not found, cannot load mapping.", bioProjectId);
        }
        else {
            logger.info("Saving mappings for bioproject {}", bioProjectId);
            for (Mapping mapping : mappings) {
                mapping.computeTargetId(extractors.get(mapping.getProvider()));
                mapping.setBioProject(project);
            }
            if (Mapping.differentMapping(project.getMappings(), mappings)) {
                // only do something if mappings changed
                project.setMappings(new HashSet<>(mappings));
                session.merge(project);
            }
        }
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }
}
