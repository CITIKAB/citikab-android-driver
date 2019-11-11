package com.trioangle.goferdriver.payouts.payout_model_classed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by trioangle on 3/8/18.
 */

public class PayoutDetail {

   /*
    "id": 4,
            "user_id": 10002,
            "holder_name": "trrrr",
            "account_number": "111111111111",
            "bank_name": "namewwwwwwww",
            "bank_location": "hhhgggggggg",
            "is_default": "No"*/
    @SerializedName("id")
    @Expose
    private String payoutId;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("holder_name")
    @Expose
    private String holder_name;

    @SerializedName("account_number")
    @Expose
    private String account_number;

    @SerializedName("bank_name")
    @Expose
    private String bank_name;

    @SerializedName("bank_location")
    @Expose
    private String bank_location;

    @SerializedName("is_default")
    @Expose
    private String isDefault;

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHolder_name() {
        return holder_name;
    }

    public void setHolder_name(String holder_name) {
        this.holder_name = holder_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_location() {
        return bank_location;
    }

    public void setBank_location(String bank_location) {
        this.bank_location = bank_location;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
