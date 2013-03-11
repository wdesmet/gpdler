package net.straininfo2.grs.idloader.bioproject.domain.mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Category of a URL reference, part of a standard LinkOut record.
 * Category is not actually all that useful, but it is in the data and
 * thus we save it anyway..
 *
 * @see Mapping
 */
@Entity
public class Category implements Serializable {

    private String name;

    public Category(String name) {
        this.name = name;
    }

    public Category() {

    }

    @Id
    @Column(length = 64)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                    this.getName().equals(((Category) o).getName());
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