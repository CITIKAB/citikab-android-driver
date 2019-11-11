package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category DocumentHome Activity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.fragments.payment.PaymentPage;
import com.trioangle.goferdriver.network.AppController;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                DocumentHome Activity
Its used to driver details information upload Home screen function
*************************************************************** */
public class DocHomeActivity extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;

    public @InjectView(R.id.continue_lay)
    RelativeLayout continue_lay;
    public @InjectView(R.id.uploaddocumentstext)
    LinearLayout uploaddocumentstext;
    public Boolean isRegister = true;

    @OnClick(R.id.dochome_back)
    public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.doc_driverlicence_back)
    public void onback() {
        callActivity(1, getResources().getString(R.string.driverlicense_back));
    }

    @OnClick(R.id.doc_driverlicence_front)
    public void onfront() {
        callActivity(2, getResources().getString(R.string.driverlicense_front));
    }

    @OnClick(R.id.doc_insurance)
    public void onInsure() {
        callActivity(3, getResources().getString(R.string.motor_insurance));
    }

    @OnClick(R.id.doc_registration)
    public void onRegist() {
        callActivity(4, getResources().getString(R.string.registeration));
    }

    @OnClick(R.id.doc_carriage)
    public void onCarriage() {
        callActivity(5, getResources().getString(R.string.contract_carriage));
    }

    @OnClick(R.id.continue_lay)
    public void onContinue() {
        sessionManager.setDriverSignupStatus("Pending");
        if (sessionManager.getPaypalEmail().length() > 0) {
            Intent x = new Intent(getApplicationContext(), MainActivity.class);
            x.putExtra("signinup", true);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
            startActivity(x, bndlanimation);
            finish();
        } else {
            Intent signin = new Intent(getApplicationContext(), PaymentPage.class);
            startActivity(signin);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        }
    }

    /**
     * @param type  1 for licence back,
     *              2 for licence front,
     *              3 for insurance
     *              4 for registeration
     *              5 for carriage
     * @param title Document title
     */
    public void callActivity(int type, String title) {
        Intent documentUpload = new Intent(getApplicationContext(), DocumentUploadActivity.class);
        documentUpload.putExtra("type", type);
        documentUpload.putExtra("title", title);
        startActivity(documentUpload);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    private int isFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_home);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        isFinish=getIntent().getIntExtra("finish",0);

        isRegister = sessionManager.getIsRegister();
        if (isRegister) {
            continue_lay.setVisibility(View.VISIBLE);
        } else {
            continue_lay.setVisibility(View.GONE);
            uploaddocumentstext.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBackPressed() {

        if (isFinish==1){
            finishAffinity();
        } else if (isFinish==2){
            Intent signin = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
            startActivity(signin);
            overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
            finish();
        } else{
            super.onBackPressed();
            overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
        }
    }

    /*
     *   While on Resume this page get driver uploaded document count
     */
    @Override
    protected void onResume() {
        super.onResume();
        isRegister = sessionManager.getIsRegister();
        if (isRegister) {
            String count = sessionManager.getDocCount();
            if (count != null && count.equals("5")) {
                continue_lay.setVisibility(View.VISIBLE);
            } else {
                continue_lay.setVisibility(View.GONE);
            }
        } else {
            continue_lay.setVisibility(View.GONE);
            uploaddocumentstext.setVisibility(View.GONE);

        }
    }
}
