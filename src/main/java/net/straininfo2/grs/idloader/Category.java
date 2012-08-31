package net.straininfo2.grs.idloader;

import java.io.Serializable;

/**
 * Category of a URL reference, part of a standard LinkOut record.
 * Category is not actually all that useful, but it is in the data and
 * thus we save it anyway..
 *
 * @see Mapping
 */
public class Category implements Serializable {
    final String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Compares two categories case-sensitively.
     *
     * @param o the other object to compare with
     * @return true if both categories have exactly the same name
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        else {
            return o instanceof Category &&
                    this.name.equals(((Category) o).name);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}