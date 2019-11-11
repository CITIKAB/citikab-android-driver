package com.trioangle.goferdriver.payouts.payout_model_classed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 9/18/18.
 */

public class StripeCountryDetails implements Serializable {
    @SerializedName("country_id")
    @Expose
    private int countryId;

    @SerializedName("country_name")
    @Expose
    private String countryName;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("currency_code")
    @Expose
    private String[] currencyCode;


    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String[] getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String[] currencyCode) {
        this.currencyCode = currencyCode;
    }
}
