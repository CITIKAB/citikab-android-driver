package com.trioangle.goferdriver.fragments.payment;
/**
 * @package com.trioangle.goferdriver
 * @subpackage fragments.payment
 * @category PaymentPage
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                      PaymentPage
Its used get Add the PaymentPage details
*************************************************************** */
public class PaymentPage extends AppCompatActivity {

    public AlertDialog dialog;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    SessionManager sessionManager;

    public @InjectView(R.id.payment_email)
    TextView payment_email;
    public @InjectView(R.id.arrow)
    ImageView arrow;

    public @InjectView(R.id.payment)
    RelativeLayout payment;
    public @InjectView(R.id.back)
    RelativeLayout back;
    protected boolean isInternetAvailable;

    @OnClick(R.id.payment)
    public void payment() {
        Intent signin = new Intent(getApplicationContext(), AddPayment.class);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


        dialog = commonMethods.getAlertDialog(this);

        /*
         *  Driver payout paypal email address
         **/
        if (!sessionManager.getPaypalEmail().equals("")) {
            payment_email.setText(sessionManager.getPaypalEmail());
        } else {
            payment_email.setText(getResources().getString(R.string.addpayout));
            arrow.setVisibility(View.INVISIBLE);
        }

        isInternetAvailable = commonMethods.isOnline(this);
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        }
    }

    @Override
    public void onBackPressed() {
        if (!sessionManager.getPaypalEmail().equals("")) {
            super.onBackPressed();
            overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManager.getPaypalEmail().equals("")) {
            payment_email.setText(sessionManager.getPaypalEmail());
        } else {
            payment_email.setText(getResources().getString(R.string.addpayout));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
