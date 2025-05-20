package org.ghostnets;

import jakarta.inject.Named;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("report")
public class Report implements Serializable
{
    //Net data
    private int size;
    private Net.RecoveryStatus recoveryStatus;

    //Sighting data
    private LocalDateTime timestamp;
    private double longitude;
    private double latitude;

    //Reporter data
    private String firstName;
    private String lastName;
    private String mailAddress;

    public Report(){}

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Net.RecoveryStatus getRecoveryStatus() {
        return recoveryStatus;
    }

    public void setRecoveryStatus(Net.RecoveryStatus recoveryStatus) {
        this.recoveryStatus = recoveryStatus;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
    public String getMailAddress() {
        return mailAddress;
    }
}
