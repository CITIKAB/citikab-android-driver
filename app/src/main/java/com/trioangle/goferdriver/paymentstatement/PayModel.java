package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category PayModel
 * @author Trioangle Product Team
 * @version 1.5
 */

import java.io.Serializable;
/* ************************************************************
                PayModel
Its used to  PayModel get method
*************************************************************** */

public class PayModel implements Serializable {
    public String type;
    private String tripdatetime;
    private String tripamount;
    private String tripdaily;

    public PayModel() {

    }

    /*
    *  Driver pay list getter and setter
    */
    public PayModel(String tripdatetime, String tripamount, String tripdaily) {
        this.tripdatetime = tripdatetime;
        this.tripamount = tripamount;
        this.tripdaily = tripdaily;
    }

    //Detailed list space

    public String getTripDateTime() {
        return tripdatetime;
    }

    public void setTripDateTime(String tripdatetime) {
        this.tripdatetime = tripdatetime;
    }

    public String getDailyTrip() {
        return tripdaily;
    }

    public void setDailyTrip(String tripdaily) {
        this.tripdaily = tripdaily;
    }

    public String getTripAmount() {
        return tripamount;
    }

    public void setTripAmount(String tripamount) {
        this.tripamount = tripamount;
    }

}
