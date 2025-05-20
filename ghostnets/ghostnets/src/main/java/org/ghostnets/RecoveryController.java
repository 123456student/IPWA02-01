package org.ghostnets;

import java.io.Serializable;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;

@Named("recoveryController")
@ViewScoped
public class RecoveryController implements Serializable
{
    private static EntityManagerFactory emf;
    private int netId;

    private Net net;

    private Recoverer recoverer;

    public RecoveryController(){
        recoverer = new Recoverer();
    }

    @PostConstruct
    public void initController() {
        System.out.println("Initializing RecoveryController");

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

        System.out.println("Finished initializing RecoveryController");
    }

    private static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("ghostnetspersistence");
        }
        return emf;
    }

    public void setRecoveryStatusInProgress() {
        System.out.println("Entering setRecoveryStatusInProgress");
        System.out.println(""+net);
        if (net != null) {
            net.setRecoverer(recoverer);

            net.setRecoveryStatus(Net.RecoveryStatus.BERGUNG_BEVORSTEHEND);
            EntityManager em = getEmf().createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(net.getRecoveredBy());
                em.merge(net);
                tx.commit();
            }
            finally {
                em.close();
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Thank you for participating!", null));
        System.out.println("Exiting setRecoveryStatusInProgress");
    }

    public int getNetId(){
        return netId;
    }

    public Net getNet(){
        return net;
    }

    public Recoverer getRecoverer() {
        return recoverer;
    }
}
