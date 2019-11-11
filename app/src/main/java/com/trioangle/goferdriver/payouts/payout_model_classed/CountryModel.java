package com.trioangle.goferdriver.payouts.payout_model_classed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by trioangle on 3/8/18.
 */

public class CountryModel {

    @SerializedName("country_id")
    @Expose
    private String countryId;

    @SerializedName("country_name")
    @Expose
    private String countryName;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
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
}
