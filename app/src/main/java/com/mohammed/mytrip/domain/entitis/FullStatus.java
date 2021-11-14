package com.mohammed.mytrip.domain.entitis;

import java.io.Serializable;

public class FullStatus implements Serializable {

    private Rider rider;
    private Driver driver;
    private Trip trip;

    public FullStatus() {
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }


}