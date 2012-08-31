package net.straininfo2.grs.idloader;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One mapping associated with a particular project identifier. Each project
 * is related to a number of mappings. This is a simple value class storing
 * the particular details of such a mapping, most importantly the url and link
 * name.
 *
 * @see Provider
 * @see net.straininfo2.grs.idloader.db.ProjectInfoLoader
 */
public final class Mapping implements Serializable {

    private final String url;

    private final String subjectType;

    private final String linkName;

    private final Category category;

    private final Provider provider;

    public Mapping(String url, String subjectType, String linkName, Category category, Provider provider) {
        assert url != null;
        this.url = url;
        this.subjectType = subjectType;
        this.linkName = linkName;
        this.category = category;
        this.provider = provider;
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

    public Category getCategory() {
        return category;
    }

    public Provider getProvider() {
        return provider;
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
        return linkName == null ? url : linkName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Mapping) {
            Mapping other = (Mapping)o;
            return (this.url == null ? other.url == null : this.url.equals(other.url)) &&
                    (this.subjectType == null ? other.subjectType == null : this.subjectType.equals(other.subjectType)) &&
                    (this.linkName == null ? other.linkName == null : this.linkName.equals(other.linkName)) &&
                    (this.category == null ? other.category == null : this.category.equals(other.category)) &&
                    (this.provider == null ? other.provider == null : this.provider.equals(other.provider));
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
        return computeCode(this.url, this.subjectType,
                this.linkName, this.category, this.provider);
    }
}