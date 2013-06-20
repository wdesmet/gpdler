package net.straininfo2.grs.idloader.db;

import net.straininfo2.grs.idloader.bioproject.domain.AdminBioProject;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.SubmissionBioProject;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BioProjectLoader implements DomainHandler {

    /*
    Manages its own running session, that way can implement committing the transaction
     every x elements.
     */
    // TODO: ADD CLEANUP OF ORGANISM ONE-TO-ONEs (elsewhere)
    /*
    OrganismMorphology
    OrganismEnvironment
    OrganismPhenotype
    OrganismSample
     */
    @Autowired
    SessionFactory sessionFactory;

    private Session currentSession;

    private int count = 0;

    private final static Logger logger = LoggerFactory.getLogger(DomainHandler.class);

    public BioProjectLoader() {
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private void checkTransaction() {
        count++;
        if (currentSession == null) {
            currentSession = sessionFactory.openSession();
            currentSession.beginTransaction();
        }
        if (count % 10000 == 0) {
            currentSession.getTransaction().commit();
            currentSession.close();
            currentSession = sessionFactory.openSession();
            currentSession.beginTransaction();
            count = 0;
        }
    }

    @SuppressWarnings("unchecked")
    private BioProject loadCurrent(long id) {
        return (BioProject)currentSession.get(BioProject.class, id);
    }

    /**
     * Copy mappings, if any, from the old project to the new one. This
     * method is null-safe.
     *
     * @param old project to copy mappings from
     * @param updated recipient of mappings
     */
    private static void copyMappings(BioProject old, BioProject updated) {
        if (old != null && updated != null) {
            updated.setMappings(old.getMappings());
        }
    }

    private void cloneAndDelete(BioProject from, BioProject to) {
        to.cloneMappings(from.getMappings());
        for (Mapping mapping : from.getMappings()) {
            if (mapping.getProvider() != null && mapping.getProvider().getMappings() != null) {
                mapping.getProvider().getMappings().remove(mapping);
            }
        }
        currentSession.delete(from);
        currentSession.persist(to);
    }

    @Override
    public void processBioProject(BioProject project) {
        checkTransaction();
        BioProject current = loadCurrent(project.getProjectId());
        copyMappings(current, project);
        logger.info("Saving project with ID {}", project.getProjectId());
        currentSession.merge(project);
    }

    @Override
    public void processAdminBioProject(AdminBioProject project) {
        checkTransaction();
        logger.info("Saving project with ID {}", project.getProjectId());
        BioProject current = loadCurrent(project.getProjectId());
        if (current != null && !(current instanceof AdminBioProject)) {
            // sometimes projects change type, replace the entry
            cloneAndDelete(current, project);
        }
        else{
            copyMappings(current, project);
            currentSession.merge(project);
        }
    }

    @Override
    public void processSubmissionBioProject(SubmissionBioProject project) {
        checkTransaction();
        logger.info("Saving project with ID {}", project.getProjectId());
        BioProject current = loadCurrent(project.getProjectId());
        if (current != null && !(current instanceof SubmissionBioProject)) {
            cloneAndDelete(current, project);
        }
        else{
            copyMappings(current, project);
            currentSession.merge(project);
        }
    }

    @Override
    public void endParsing() {
        try {
            currentSession.getTransaction().commit();
            currentSession.close();
        } catch (NullPointerException e) {
            // ignore, only happens when no input was given, which is exceptional
        }
    }
}
