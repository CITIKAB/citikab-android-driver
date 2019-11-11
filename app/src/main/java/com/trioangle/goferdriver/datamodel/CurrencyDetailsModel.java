package com.trioangle.goferdriver.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 9/14/18.
 */

public class CurrencyDetailsModel implements Serializable {
    @SerializedName("success_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("currency_list")
    @Expose
    private ArrayList<CurreneyListModel> curreneyListModels = new ArrayList<>();

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

    public ArrayList<CurreneyListModel> getCurreneyListModels() {
        return curreneyListModels;
    }

    public void setCurreneyListModels(ArrayList<CurreneyListModel> curreneyListModels) {
        this.curreneyListModels = curreneyListModels;
    }
}
