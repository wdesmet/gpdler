package net.straininfo2.grs.idloader.db;

import net.straininfo2.grs.idloader.bioproject.domain.AdminBioProject;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.SubmissionBioProject;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
        if (count % 100 == 0) {
            currentSession.getTransaction().commit();
            currentSession.flush();
            currentSession.close();
            currentSession = sessionFactory.openSession();
            currentSession.beginTransaction();
        }
    }

    @Override
    public void processBioProject(BioProject project) {
        checkTransaction();
        logger.info("Saving project with ID {}", project.getProjectId());
        currentSession.merge(project);
    }

    @Override
    public void processAdminBioProject(AdminBioProject project) {
        checkTransaction();
        logger.info("Saving project with ID {}", project.getProjectId());
        currentSession.merge(project);
    }

    @Override
    public void processSubmissionBioProject(SubmissionBioProject project) {
        checkTransaction();
        logger.info("Saving project with ID {}", project.getProjectId());
        currentSession.merge(project);
    }

    @Override
    public void endParsing() {
        currentSession.getTransaction().commit();
        currentSession.flush();
        currentSession.close();
    }
}
