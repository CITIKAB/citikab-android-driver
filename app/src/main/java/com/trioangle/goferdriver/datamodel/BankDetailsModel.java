package com.trioangle.goferdriver.datamodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BankDetailsModel extends BaseObservable implements Serializable {

    @SerializedName("holder_name")
    @Expose
    private String accountHolderName;

    @SerializedName("account_number")
    @Expose
    private String accountnumber;

    @SerializedName("bank_name")
    @Expose
    private String bank_name;

    @SerializedName("bank_location")
    @Expose
    private String bank_location;

    @SerializedName("code")
    @Expose
    private String bank_code;

    @Bindable
    public String getAccount_holder_name() {
        return accountHolderName;
    }

    public void setAccount_holder_name(String accountHolderName) {
        this.accountHolderName = accountHolderName;
        notifyPropertyChanged(BR.account_holder_name);
    }

    @Bindable
    public String getAccount_number() {
        return accountnumber;
    }

    public void setAccount_number(String accountnumber) {
        this.accountnumber = accountnumber;
        notifyPropertyChanged(BR.account_number);
    }

    @Bindable
    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
        notifyPropertyChanged(BR.bank_name);
    }

    @Bindable
    public String getBank_location() {
        return bank_location;
    }

    public void setBank_location(String bank_location) {
        this.bank_location = bank_location;
        notifyPropertyChanged(BR.bank_location);
    }

    @Bindable
    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
        notifyPropertyChanged(BR.bank_code);

    }
}
