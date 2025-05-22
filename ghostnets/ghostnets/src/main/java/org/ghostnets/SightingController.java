package org.ghostnets;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Named("sightingController")
@ViewScoped
public class SightingController implements Serializable
{
    private static EntityManagerFactory emf;
    private int netId;
    private Net net;
    private Report report;
    public SightingController(){}

    @PostConstruct
    public void initController() {
        System.out.println("Initializing SightingController");

        this.report = new Report();
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, String> params = ec.getRequestParameterMap();
            String netIdParam = params.get("netId");

            System.out.println("netIdParam = " + netIdParam);

            if (netIdParam != null) {
                int netIdInt = Integer.parseInt(netIdParam);
                this.netId = netIdInt;

                EntityManager em = getEmf().createEntityManager();
                EntityTransaction tx = em.getTransaction();

                try {
                    tx.begin();
                    net = em.createQuery(
                                    "SELECT n FROM Net n LEFT JOIN FETCH n.sightings WHERE n.id = :id", Net.class)
                            .setParameter("id", this.netId)
                            .getSingleResult();
                    tx.commit();

                    System.out.println("Assigned net with ID = " + this.netId);
                } catch (NoResultException e) {
                    System.out.println("No net found with id = " + this.netId);
                } finally {
                    em.close();
                }
            } else {
                System.out.println("netId parameter is missing.");
            }
        } catch (Exception e) {
            System.out.println("Exception during initialization: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Finished initializing SightingController");
    }

    private static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("ghostnetspersistence");
        }
        return emf;
    }

    public void registerNewSighting() {
        System.out.println("Entering registerNewSighting");
        System.out.println(""+net);
        if (net != null) {
            System.out.println("Lat=" + report.getLatitude()
                    + ", Long=" + report.getLongitude()
                    + ", Timestamp=" + report.getTimestamp());
            System.out.println("netId=" + netId);
            Sighting sighting = new Sighting();
            sighting.setLongitude(report.getLongitude());
            sighting.setLatitude(report.getLatitude());
            sighting.setTimestamp(report.getTimestamp());
            sighting.setNet(net);

            Recoverer recoverer = new Recoverer();
            recoverer.setFirstName(report.getFirstName());
            recoverer.setLastName(report.getLastName());
            recoverer.setMailAddress(report.getMailAddress());

            sighting.setReporter(recoverer);
            net.addSighting(sighting);


            EntityManager em = getEmf().createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(recoverer);
                em.persist(sighting);
                em.merge(net);
                tx.commit();
            }
            finally {
                em.close();
            }
        }
        else {
            System.out.println("net is null");
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Thank you for participating!", null));
        System.out.println("Exiting registerNewSighting");
    }

    public int getNetId(){
        return netId;
    }

    public Net getNet(){
        return net;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
