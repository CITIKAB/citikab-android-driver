package com.trioangle.goferdriver.datamodel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LatLngModel {

    public List<LatLng> latLngList;

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public int hour;

    public int min;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }
}
