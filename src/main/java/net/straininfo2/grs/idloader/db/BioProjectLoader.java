package net.straininfo2.grs.idloader.db;

import net.straininfo2.grs.idloader.bioproject.domain.AdminBioProject;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.SubmissionBioProject;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainHandler;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BioProjectLoader implements DomainHandler {

    // TODO: ADD CLEANUP OF ORGANISM ONE-TO-ONEs (elsewhere)
    /*
    OrganismMorphology
    OrganismEnvironment
    OrganismPhenotype
    OrganismSample
     */
    @Autowired
    SessionFactory sessionFactory;

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

    private void updateCount() {
        if (count++ % 30 == 0) {
            sessionFactory.getCurrentSession().flush();
        }
    }

    @Override
    public void processBioProject(BioProject project) {
        logger.info("Saving project with ID {}", project.getProjectId());
        sessionFactory.getCurrentSession().merge(project);
        updateCount();
    }

    @Override
    public void processAdminBioProject(AdminBioProject project) {
        logger.info("Saving project with ID {}", project.getProjectId());
        sessionFactory.getCurrentSession().merge(project);
        updateCount();
    }

    @Override
    public void processSubmissionBioProject(SubmissionBioProject project) {
        logger.info("Saving project with ID {}", project.getProjectId());
        sessionFactory.getCurrentSession().merge(project);
        updateCount();
    }
}
