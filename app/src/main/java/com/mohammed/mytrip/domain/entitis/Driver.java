package com.mohammed.mytrip.domain.entitis;

import java.io.Serializable;

public class Driver implements Serializable {
    private String id;
    private String name;
    private String plateNumber;
    private String status;
    private String assignedTrip;

    public Driver() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTrip() {
        return assignedTrip;
    }

    public void setAssignedTrip(String assignedTrip) {
        this.assignedTrip = assignedTrip;
    }

    public enum Status {
        OFFLINE,
        AVAILABLE,
        ON_TRIP
    }
}
