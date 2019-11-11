package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category TripEarningDetails
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.network.AppController;

import butterknife.ButterKnife;
import butterknife.OnClick;

/* ************************************************************
                TripEarningDetails
Its used to view the trip earning details function
*************************************************************** */
public class TripEarningDetails extends AppCompatActivity {
    @OnClick(R.id.back)
    public void onpBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_earning_details);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }
}
