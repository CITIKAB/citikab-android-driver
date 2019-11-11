package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 9/12/18.
 */

public class YourTripModel implements Serializable {


    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("pending_trips")
    @Expose
    private ArrayList<PastTripModel> todayTrips;
    @SerializedName("completed_trips")
    @Expose
    private ArrayList<PastTripModel> pastTripsModel;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public ArrayList<PastTripModel> getTodayTrips() {
        return todayTrips;
    }

    public void setTodayTrips(ArrayList<PastTripModel> todayTrips) {
        this.todayTrips = todayTrips;
    }

    public ArrayList<PastTripModel> getPastTripsModel() {
        return pastTripsModel;
    }

    public void setPastTripsModel(ArrayList<PastTripModel> pastTripsModel) {
        this.pastTripsModel = pastTripsModel;
    }
}
