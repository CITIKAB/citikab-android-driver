package com.trioangle.goferdriver.map;

/**
 * Created by bowshulsheikrahaman on 29/01/18.
 */

public class DriverLocation {
    public String lat;
    public String lng;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public DriverLocation() {
    }

    public DriverLocation(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
