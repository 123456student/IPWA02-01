package org.ghostnets;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Sighting {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime timestamp;
    private double longitude;
    private double latitude;
    @ManyToOne
    @JoinColumn(name = "recoverer_id", nullable = true)
    private Recoverer reporter;
    @ManyToOne
    @JoinColumn(name = "net_id", nullable = false)
    private Net net;

    public Sighting() {}

    public Sighting(LocalDateTime timestamp, float longitude, float latitude, Recoverer reporter) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reporter = reporter;
    }

    public Sighting(LocalDateTime timestamp, float longitude, float latitude, Recoverer reporter, Net net) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reporter = reporter;
        this.net = net;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Recoverer getReporter() {
        return reporter;
    }

    public void setReporter(Recoverer reporter) {
        this.reporter = reporter;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }
}
