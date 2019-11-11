package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 9/12/18.
 */

public class RiderFeedBackModel implements Serializable {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("rider_feedback")
    @Expose
    private ArrayList<RiderFeedBackArrayModel> riderFeedBack;

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

    public ArrayList<RiderFeedBackArrayModel> getRiderFeedBack() {
        return riderFeedBack;
    }

    public void setRiderFeedBack(ArrayList<RiderFeedBackArrayModel> riderFeedBack) {
        this.riderFeedBack = riderFeedBack;
    }
}
