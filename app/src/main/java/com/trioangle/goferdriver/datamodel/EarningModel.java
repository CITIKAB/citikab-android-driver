package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 9/11/18.
 */

public class EarningModel implements Serializable {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("last_trip")
    @Expose
    private String lastTrip;
    @SerializedName("recent_payout")
    @Expose
    private String recentPayout;
    @SerializedName("total_week_amount")
    @Expose
    private String totalWeekAmount;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("trip_details")
    @Expose
    private ArrayList<TripDetailsModel> tripDetails;

    public ArrayList<TripDetailsModel> getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(ArrayList<TripDetailsModel> tripDetails) {
        this.tripDetails = tripDetails;
    }

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

    public String getLastTrip() {
        return lastTrip;
    }

    public void setLastTrip(String lastTrip) {
        this.lastTrip = lastTrip;
    }

    public String getRecentPayout() {
        return recentPayout;
    }

    public void setRecentPayout(String recentPayout) {
        this.recentPayout = recentPayout;
    }

    public String getTotalWeekAmount() {
        return totalWeekAmount;
    }

    public void setTotalWeekAmount(String totalWeekAmount) {
        this.totalWeekAmount = totalWeekAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}

