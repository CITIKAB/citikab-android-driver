package com.trioangle.goferdriver.payouts.payout_model_classed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by trioangle on 3/8/18.
 */

public class CountryResult {

    @SerializedName("success_message")
    @Expose
    private String statusMessage;

    @SerializedName("status_code")
    @Expose
    private String statusCode;

    @SerializedName("country_list")
    @Expose
    private ArrayList<CountryModel> countryList;

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

    public ArrayList<CountryModel> getCountryList() {
        return countryList;
    }

    public void setCountryList(ArrayList<CountryModel> countryList) {
        this.countryList = countryList;
    }
}
