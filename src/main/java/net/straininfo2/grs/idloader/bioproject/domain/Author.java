package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.*;

/**
 * Models a publication-associated author.
 *
 * The author is not modeled as independent of the publication, since there
 * is no real way to deduplicate authors: authors might well have the same
 * name, but be different authors.
 *
 * Could be slightly improved by modeling the consortium separately, but it
 * is not really necessary here, since this information is seldom supplied.
 */
@Entity
public class Author {

    private long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String suffix;

    private String consortium;

    private Publication publication;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(length = 35)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(length = 65)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(length = 35)
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column(length = 35)
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getConsortium() {
        return consortium;
    }

    public void setConsortium(String consortium) {
        this.consortium = consortium;
    }

    @ManyToOne(optional = false)
    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }
}
