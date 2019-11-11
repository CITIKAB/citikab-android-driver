package com.trioangle.goferdriver.fragments.currency;

import java.io.Serializable;

public class CurrencyModel implements Serializable {

    private String currencysymbol, currencyname;

    public CurrencyModel() {

    }

    public CurrencyModel(String currencyname, String currencysymbol) {
        this.currencyname = currencyname;
        this.currencysymbol = currencysymbol;
    }

    public String getCurrencyName() {
        return currencyname;
    }

    public void setCurrencyName(String currencyname) {
        this.currencyname = currencyname;
    }

    public String getCurrencySymbol() {
        return currencysymbol;
    }

    public void setCurrencySymbol(String currencysymbol) {
        this.currencysymbol = currencysymbol;
    }

}
