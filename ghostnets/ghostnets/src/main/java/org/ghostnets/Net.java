package org.ghostnets;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Net implements Serializable
{
    public enum RecoveryStatus {
        VERSCHOLLEN,
        GEBORGEN,
        GEMELDET,
        BERGUNG_BEVORSTEHEND
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int size;
    private RecoveryStatus recoveryStatus;
    @OneToMany(mappedBy = "net", orphanRemoval = true)
    private List<Sighting> sightings;
    @ManyToOne
    @JoinColumn(name = "recovered_by_id")
    private Recoverer recoveredBy;

    public Net(){}

    public Net(int size, RecoveryStatus recoveryStatus)
    {
        this.size = size;
        this.recoveryStatus = recoveryStatus;
    }

    public Sighting getMostRecentSighting(){
        List<Sighting> validSightings = this.getSightings().stream()
                .filter(s -> s.getTimestamp() != null)
                .collect(Collectors.toList());

        if (validSightings.isEmpty()) {
            return null;
        }

        return Collections.max(
                validSightings,
                Comparator.comparing(Sighting::getTimestamp)
        );
    }

    public double getMostRecentLat(){ return getMostRecentSighting().getLatitude(); }

    public double getMostRecentLong(){
        return getMostRecentSighting().getLongitude();
    }

    public void addSighting(Sighting sighting) {
        if (this.sightings == null){
            this.sightings = new ArrayList<Sighting>();
        }
        this.sightings.add(sighting);
        sighting.setNet(this);
    }

    public String toString() {
        return ("Id: "+id+" Size: "+size+" Recovered:  "+recoveryStatus);
    }

    public Recoverer getRecoveredBy() {
        return recoveredBy;
    }

    public void setRecoveredBy(Recoverer recoveredBy) {this.recoveredBy = recoveredBy; }

    public long getId() {return id;}

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setRecoveryStatus(RecoveryStatus recoveryStatus) {
        this.recoveryStatus = recoveryStatus;
    }

    public RecoveryStatus getRecoveryStatus() {
        return recoveryStatus;
    }

    public void setRecoverer(Recoverer recovererBy) {this.recoveredBy = recovererBy;}

    public List<Sighting> getSightings() {
        return sightings;
    }

    public void setSightings(List<Sighting> sightings) {
        this.sightings = sightings;
    }
}
