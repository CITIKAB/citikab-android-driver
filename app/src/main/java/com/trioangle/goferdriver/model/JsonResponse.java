package com.trioangle.goferdriver.model;

/**
 * Created by trioangle on 9/7/18.
 */

public class JsonResponse {
    private String url;



    private String statusCode;
    private String method;
    private int responseCode;
    private int requestCode;
    private String errorMsg;
    private boolean isOnline;
    private String statusMsg;
    private boolean isSuccess;
    private String strResponse;
    private String requestData;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isOnline() {
        return isOnline;
    }



    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getStrResponse() {
        return strResponse;
    }

    public void setStrResponse(String strResponse) {
        this.strResponse = strResponse;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public void clearAll() {
        this.url = "";
        this.method = "";
        this.errorMsg = "";
        this.statusMsg = "";
        this.strResponse = "";
        this.requestData = "";
        this.requestCode = 0;
        this.responseCode = 0;
        this.isOnline = false;
        this.isSuccess = false;
    }
}

