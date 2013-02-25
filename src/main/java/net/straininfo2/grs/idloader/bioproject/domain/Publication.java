package net.straininfo2.grs.idloader.bioproject.domain;

import java.util.Calendar;
import java.util.Collection;
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
public class Publication {

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

    private List<Author> authors;

    public enum PublicationDB {
        PMC,
        PUBMED,
        DOI,
        NOT_AVAILABLE
    }

    /* not mapped as a boolean, in case new fields are added later */
    public enum PublicationStatus {
        PUBLISHED,
        UNPUBLISHED
    }

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getPagesFrom() {
        return pagesFrom;
    }

    public void setPagesFrom(String pagesFrom) {
        this.pagesFrom = pagesFrom;
    }

    public String getPagesTo() {
        return pagesTo;
    }

    public void setPagesTo(String pagesTo) {
        this.pagesTo = pagesTo;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
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

}
