package net.straininfo2.grs.idloader.bioproject.domain.mappings;

import net.straininfo2.grs.idloader.TargetIdExtractor;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One mapping associated with a particular BioProject. Each project
 * is related to a number of mappings. This is a simple value class storing
 * the particular details of such a mapping, most importantly the url and link
 * name.
 *
 * @see Provider
 * @see net.straininfo2.grs.idloader.db.ProjectInfoLoader
 */
@Entity
public class Mapping implements Serializable {

    private long id;

    private String url;

    private String subjectType;

    private String linkName;

    private Category category;

    private Provider provider;

    private String targetId;

    private BioProject bioProject;

    public Mapping(String url, String subjectType, String linkName, Category category, Provider provider) {
        assert url != null;
        this.setUrl(url);
        this.setSubjectType(subjectType);
        this.setLinkName(linkName);
        this.setCategory(category);
        this.setProvider(provider);
    }

    public Mapping(String url, String subjectType, String linkName, String targetId, Category category, Provider provider) {
        this(url, subjectType, linkName, category, provider);
        this.setTargetId(targetId);
    }

    public Mapping() {

    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getLinkName() {
        return linkName;
    }

    @ManyToOne
    public Category getCategory() {
        return category;
    }

    @ManyToOne(optional = false)
    public Provider getProvider() {
        return provider;
    }

    public String getTargetId() {
        return targetId;
    }

    public void computeTargetId(TargetIdExtractor extractor) {
        this.setTargetId(extractor.extractTargetId(this.getUrl()));
    }

    @ManyToOne(optional = false)
    public BioProject getBioProject() {
        return bioProject;
    }

    public void setBioProject(BioProject bioProject) {
        this.bioProject = bioProject;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * Convenience function to create a set of all providers references by a
     * list of mappings.
     *
     * @param mappings a list of mappings
     * @return list of providers, each of which referenced at least once by one
     * of the mappings
     */
    public static Set<Provider> listProviders(List<Mapping> mappings) {
        Set<Provider> providers = new HashSet<Provider>();
        for (Mapping mapping : mappings) {
            if (!providers.contains(mapping.getProvider())) {
                providers.add(mapping.getProvider());
            }
        }
        return providers;
    }

    /**
     * Compares two lists of mappings and returns true if they do not contain the
     * same mappings. It expects both lists to exist.
     *
     * @param current the first list of mappings
     * @param other the mappings to compare it to
     * @return true if the lists contain different mappings (possibly in different order)
     * @throws NullPointerException if either of the mapping lists are null
     */
    public static boolean differentMapping(List<Mapping> current, List<Mapping> other) {
        if (current.size() != other.size()) {
            return true; // heuristic helper
        }
        else {
            for (Mapping mapping : current) {
                if (!other.contains(mapping)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return this.getLinkName() == null ? this.getUrl() : this.getLinkName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Mapping) {
            Mapping other = (Mapping)o;
            return (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl())) &&
                    (this.getSubjectType() == null ? other.getSubjectType() == null : this.getSubjectType().equals(other.getSubjectType())) &&
                    (this.getLinkName() == null ? other.getLinkName() == null : this.getLinkName().equals(other.getLinkName())) &&
                    (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory())) &&
                    (this.getProvider() == null ? other.getProvider() == null : this.getProvider().equals(other.getProvider())) &&
                    (this.getTargetId() == null ? other.getTargetId() == null : this.getTargetId().equals(other.getTargetId()));
        }
        else {
            return false;
        }
    }

    private int addToCode(int code, Object obj) {
        return 31 * code + (obj == null ? 0 : obj.hashCode());
    }

    private int computeCode(Object... objs) {
        int code = 3;
        for (Object obj : objs) {
            code = addToCode(code, obj);
        }
        return code;
    }

    @Override
    public int hashCode() {
        return computeCode(this.getUrl(), this.getSubjectType(),
                this.getLinkName(), this.getCategory(), this.getProvider());
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
