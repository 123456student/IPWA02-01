package org.ghostnets;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

@Named("reportController")
@ViewScoped
public class ReportController implements Serializable
{
    private static EntityManagerFactory emf;

    private Report report;

    public ReportController(){}

    @PostConstruct
    public void initController(){
        System.out.println("Initializing nets in ReportController");
        this.report = new Report();
        System.out.println("Finished initializing nets in ReportController");
    }

    private static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("ghostnetspersistence");
        }
        return emf;
    }

    public void saveNewNet() {
        System.out.println("Entered saveNewNet");
        Net net = new Net();
        Sighting sighting = new Sighting();
        Recoverer reporter = new Recoverer();

        reporter.setFirstName(report.getFirstName());
        reporter.setLastName(report.getLastName());
        reporter.setMailAddress(report.getMailAddress());

        sighting.setReporter(reporter);
        sighting.setNet(net);
        sighting.setLatitude(report.getLatitude());
        sighting.setLongitude(report.getLongitude());
        sighting.setTimestamp(report.getTimestamp());

        net.setRecoverer(reporter);
        net.addSighting(sighting);
        net.setSize(report.getSize());
        net.setRecoveryStatus(Net.RecoveryStatus.GEMELDET);

        EntityManager em = getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(reporter);
            em.persist(net);
            em.persist(sighting);
            tx.commit();
        }
        finally {
            em.close();
        }
        System.out.println("Finished saveNewNet");
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Thank you for the report!", null));
    }

    public static ArrayList<Net> loadNets(){
        EntityManager em = getEmf().createEntityManager();
        try {
            return new ArrayList<>(em.createQuery("SELECT n FROM Net n LEFT JOIN FETCH n.sightings", Net.class).getResultList());
        } finally {
            em.close();
        }
    }

    public void generateRandNet(){
        System.out.println("Entered generateRandNet");
        EntityManager em =  getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        Random rnd = new Random();
        try {
            tx.begin();
            Net newNet = new Net(rnd.nextInt(101), Net.RecoveryStatus.values()[rnd.nextInt(4)]);
            Recoverer recoverer = new Recoverer("Peter", "Gabriel", "pGabriel@gabriel.com");
            Sighting newSighting = new Sighting(
                    LocalDateTime.now(),
                    rnd.nextFloat(90),
                    rnd.nextFloat(90),
                    recoverer);
            newNet.addSighting(newSighting);
            em.persist(recoverer);
            em.persist(newNet);
            em.persist(newSighting);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.out.println(e.getMessage());
        } finally {
            em.close();
        }

        Long count =  getEmf().createEntityManager()
                .createQuery("SELECT COUNT(n) FROM Net n", Long.class)
                .getSingleResult();
        System.out.println("Nets in DB: " + count);
    }
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
