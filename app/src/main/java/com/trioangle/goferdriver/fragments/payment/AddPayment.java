package com.trioangle.goferdriver.fragments.payment;
/**
 * @package com.trioangle.goferdriver
 * @subpackage fragments.payment
 * @category AddPayment
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.VehicleDetails;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                      AddPayment
Its used get Add the payment
*************************************************************** */
public class AddPayment extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;

    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;


    public @InjectView(R.id.payment_msg)
    TextView payment_msg;
    public @InjectView(R.id.emailName)
    TextInputLayout emailName;
    public @InjectView(R.id.emaitext)
    EditText emaitext;
    protected boolean isInternetAvailable;

    /*
     *  Check is email valid or not
     **/
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @OnClick(R.id.emailclose)
    public void payment() {
        emaitext.setText("");
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.save)
    public void save() {
        addPaymentApi();
    }

    @OnClick(R.id.arrow)
    public void arrow() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

         /*
          *  Common loader and internet check
          **/
        isInternetAvailable = commonMethods.isOnline(this);


        String appName = getResources().getString(R.string.app_name);
        payment_msg.setText(getResources().getString(R.string.addpayment_msg));

        if (!sessionManager.getPaypalEmail().equals("")) {
            emaitext.setText(sessionManager.getPaypalEmail());
        }


    }

    private void addPaymentApi() {

        isInternetAvailable = commonMethods.isOnline(this);

        if (!validateEmail()) {
            return;
        }

        if (isInternetAvailable) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.addPayout(emaitext.getText().toString(), sessionManager.getType(), sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data);
            return;
        }

        if (jsonResp.isSuccess()) {
            onSuccessPayment(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

    }

    public void onSuccessPayment(JsonResponse jsonResp) {

        VehicleDetails vehicleResultModel = gson.fromJson(jsonResp.getStrResponse(), VehicleDetails.class);
        if (vehicleResultModel != null) {


            if (sessionManager.getPaypalEmail().length() > 0) {
                sessionManager.setPaypalEmail(emaitext.getText().toString());
                commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                onBackPressed();
            } else {
                sessionManager.setPaypalEmail(emaitext.getText().toString());
                Intent x = new Intent(getApplicationContext(), MainActivity.class);
                x.putExtra("signinup", true);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            }


        }
    }

    /*
     *   Validate email address
     **/
    private boolean validateEmail() {
        String email = emaitext.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailName.setError(getString(R.string.error_msg_email));
            return false;
        } else {
            emailName.setErrorEnabled(false);
        }

        return true;
    }


}
