package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a publication record.
 *
 * Normally the citation will be put into the structured citation fields if
 * possible, otherwise the freeFormCitation field should be used.
 *
 * Note that publications are only seldom filled in in the source XML, and
 * usually only contain a date and database ID.
 */
@Entity
public class Publication {

    private long id;

    private PublicationDB dbType;

    private String publicationId;

    private Calendar publicationDate;

    private PublicationStatus publicationStatus;

    private String freeFormCitation;

    private String title;

    private String journalTitle;

    private String year;

    private String volume;

    private String issue;

    private String pagesFrom;

    private String pagesTo;

    private List<Author> authors = new LinkedList<>();

    private BioProject bioProject;

    public enum PublicationDB {
        ePMC,
        ePubmed,
        eDOI,
        eNotAvailable
    }

    /* not mapped as a boolean, in case new fields are added later */
    public enum PublicationStatus {
        ePublished,
        eUnpublished
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Lob
    public String getFreeFormCitation() {
        return freeFormCitation;
    }

    public void setFreeFormCitation(String freeFormCitation) {
        this.freeFormCitation = freeFormCitation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    @Column(length = 10)
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Column(length = 10)
    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Column(length = 10)
    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Column(length = 10)
    public String getPagesFrom() {
        return pagesFrom;
    }

    public void setPagesFrom(String pagesFrom) {
        this.pagesFrom = pagesFrom;
    }

    @Column(length = 10)
    public String getPagesTo() {
        return pagesTo;
    }

    public void setPagesTo(String pagesTo) {
        this.pagesTo = pagesTo;
    }

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Author> getAuthors() {
        return authors;
    }

    protected void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        author.setPublication(this);
        this.getAuthors().add(author);
    }

    public PublicationDB getDbType() {
        return dbType;
    }

    public void setDbType(PublicationDB dbType) {
        this.dbType = dbType;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public Calendar getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Calendar publicationDate) {
        this.publicationDate = publicationDate;
    }

    public PublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(PublicationStatus publicationStatus) {
        this.publicationStatus = publicationStatus;
    }

    @ManyToOne(optional = false)
    public BioProject getBioProject() {
        return bioProject;
    }

    public void setBioProject(BioProject bioProject) {
        this.bioProject = bioProject;
    }
}
