package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 9/12/18.
 */

public class RiderFeedBackArrayModel implements Serializable {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("rider_rating")
    @Expose
    private String riderRating;

    @SerializedName("rider_comments")
    @Expose
    private String riderComments;

    @SerializedName("trip_id")
    @Expose
    private String tripId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRiderRating() {
        return riderRating;
    }

    public void setRiderRating(String riderRating) {
        this.riderRating = riderRating;
    }

    public String getRiderComments() {
        return riderComments;
    }

    public void setRiderComments(String riderComments) {
        this.riderComments = riderComments;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
