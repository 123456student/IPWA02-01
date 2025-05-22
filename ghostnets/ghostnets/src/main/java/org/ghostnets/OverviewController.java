package org.ghostnets;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jdk.vm.ci.meta.Local;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@Named("overviewController")
@ViewScoped
public class OverviewController implements Serializable
{
    private static EntityManagerFactory emf;
    private ArrayList<Net> nets;
    private MapModel mapModel;
    private Marker selectedMarker;
    private Net selectedNet;
    private Net.RecoveryStatus selectedStatus;

    public OverviewController(){}

    @PostConstruct
    public void initController(){
        System.out.println("Initializing nets in OverviewView");
        this.nets = OverviewController.loadNets();
        for (Net net : this.nets){
            System.out.println(net.toString());
        }
        System.out.println("Finished initializing nets in OverviewView");

        mapModel = generateMapModel();
    }

    public static ArrayList<Net> loadNets(){
        EntityManager em = getEmf().createEntityManager();
        try {
            return new ArrayList<>(em.createQuery("SELECT n FROM Net n LEFT JOIN FETCH n.sightings",
                    Net.class).getResultList());
        } finally {
            em.close();
        }
    }


    private MapModel generateMapModel(){
        mapModel = new DefaultMapModel();
        for (Net net : nets) {
            if ((net.getRecoveryStatus() == Net.RecoveryStatus.GEMELDET
                        || net.getRecoveryStatus() == Net.RecoveryStatus.BERGUNG_BEVORSTEHEND
                    ) && !net.getSightings().isEmpty()) {
                Sighting mostRecent = net.getMostRecentSighting();

                Marker marker = new Marker(
                        new LatLng(mostRecent.getLatitude(), mostRecent.getLongitude())
                );

                marker.setData(net);
                switch(net.getRecoveryStatus()){
                    case BERGUNG_BEVORSTEHEND:
                        marker.setIcon("https://maps.google.com/mapfiles/ms/icons/green-dot.png");
                        break;
                    case GEMELDET:
                        marker.setIcon("https://maps.google.com/mapfiles/ms/icons/yellow-dot.png");
                        break;
                }
                mapModel.addOverlay(marker);
            }
        }
        return mapModel;
    }
    public void onMarkerSelect(OverlaySelectEvent event) {
        selectedMarker = (Marker) event.getOverlay();
        selectedNet = (Net) selectedMarker.getData();
    }

    public void setRecoveryStatus(int statusCode) {
        Net.RecoveryStatus status = Net.RecoveryStatus.values()[statusCode];
        System.out.println("Entering setRecoveryStatus");
        System.out.println(""+status);
        System.out.println(""+selectedNet.getId());
        if (selectedNet != null) {
            selectedNet.setRecoveryStatus(status);
            EntityManager em = getEmf().createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(selectedNet);
                tx.commit();
            }
            finally {
                em.close();
            }
            reloadNets();
        }
        System.out.println("Exiting setRecoveryStatus");
    }

    public String getSightingUrl() {
        if(selectedNet != null) return "sighting.xhtml?faces-redirect=true&amp;netId=" + selectedNet.getId();
        return "";
    }

    private static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("ghostnetspersistence");
        }
        return emf;
    }

    public void addNet(Net net) {
        EntityManager em = getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(net);
            tx.commit();
        }
        finally {
            em.close();
        }
        reloadNets();
    }

    public String convertStatusToString(Net.RecoveryStatus recoveryStatus){
        if(recoveryStatus == null){
            return "null";
        }
        switch(recoveryStatus){
            case BERGUNG_BEVORSTEHEND:
                return "Being Recovered";
            case GEMELDET:
                return "Reported";
            case VERSCHOLLEN:
                return "Disappeared";
            case GEBORGEN:
                return "Recovered";
            default:
                return "";
        }

    }

    public void generateAndLoad(){
        generateRandNet();
        reloadNets();
    }

    public String getRecoveryUrl(){
        if(selectedNet != null)  return "recovery-progress.xhtml?faces-redirect=true&amp;netId=" + selectedNet.getId();
        return "";
    }

    public void generateRandNet(){
        System.out.println("Entered generateRandNet");
        EntityManager em =  getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        Random rnd = new Random();
        try {
            tx.begin();
            Net newNet = new Net(rnd.nextInt(101), Net.RecoveryStatus.GEMELDET);
            Recoverer recoverer = new Recoverer("Peter", "Gabriel", "pGabriel@gabrial.com");
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


    public String getNetMarkerText(Net net){
        String text = "ID: " + net.getId() + ". Status: " + net.getRecoveryStatus();
        if (net.getRecoveryStatus() == Net.RecoveryStatus.BERGUNG_BEVORSTEHEND && net.getRecoveredBy() != null){
            text = text.concat(". Being Recovered By: "+ net.getRecoveredBy().getFirstName() +" "+net.getRecoveredBy().getLastName()+".");
        }
        return text;
    }

    public void reloadNets(){
        this.nets = loadNets();
        mapModel = generateMapModel();
    }

    public Net getSelectedNet() {
        return selectedNet;
    }

    public void setSelectedNet(Net selectedNet) {
        this.selectedNet = selectedNet;
    }

    public Marker getSelectedMarker() {
        return selectedMarker;
    }

    public void setSelectedMarker(Marker selectedMarker) {
        this.selectedMarker = selectedMarker;
    }

    public MapModel getMapModel() {
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public ArrayList<Net> getNets() {
        return nets;
    }

    public void setSelectedStatus(Net.RecoveryStatus selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public Net.RecoveryStatus getSelectedStatus() {
        return selectedStatus;
    }

}
