package com.trioangle.goferdriver.payouts.payout_model_classed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  Stripe Countries Model
 */
public class StripeCountriesModel implements Serializable {
    @SerializedName("success_message")
    @Expose
    private String successMessage;

    @SerializedName("status_code")
    @Expose
    private String statusCode;

    @SerializedName("country_list")
    @Expose
    private ArrayList<StripeCountryDetails> countryList=new ArrayList<>();

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public ArrayList<StripeCountryDetails> getCountryList() {
        return countryList;
    }

    public void setCountryList(ArrayList<StripeCountryDetails> countryList) {
        this.countryList = countryList;
    }
}
