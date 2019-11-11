package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category ResetPassword
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.LoginDetails;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;

/* ************************************************************
                ResetPassword
Its used to get the reset password detail function
*************************************************************** */
public class ResetPassword extends AppCompatActivity implements ServiceListener {


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


    public @InjectView(R.id.next)
    RelativeLayout next;
    public @InjectView(R.id.input_password)
    EditText input_password;
    public @InjectView(R.id.input_confirmpassword)
    EditText input_confirmpassword;
    public @InjectView(R.id.input_layout_password)
    TextInputLayout input_layout_password;
    public @InjectView(R.id.input_layout_confirmpassword)
    TextInputLayout input_layout_confirmpassword;
    public @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    protected boolean isInternetAvailable;

    public @InjectView(R.id.nextArrow)
    ImageView nextArrow;

    public String facebookKitVerifiedMobileNumber="", facebookVerifiedMobileNumberCountryCode="";

    @OnClick(R.id.next)
    public void forgetPassword() {
        forgotPwd();
    }

    @OnClick(R.id.backArrow)
    public void backPressed() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        getMobileNumerAndCountryCodeFromIntent();
        dialog = commonMethods.getAlertDialog(this);

        isInternetAvailable = commonMethods.isOnline(this);


    }

    private void getMobileNumerAndCountryCodeFromIntent() {
        if (getIntent() != null) {
            facebookKitVerifiedMobileNumber = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY);
            facebookVerifiedMobileNumberCountryCode = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY);
        }


    }

    private void forgotPwd() {

        isInternetAvailable = commonMethods.isOnline(this);


        if (!validateFirst()) {
            return;
        } else if (!validateconfrom()) {
            return;
        } else {
            String input_password_str = input_password.getText().toString().trim();
            String input_password_confirmstr = input_confirmpassword.getText().toString().trim();
            if (input_password_str.length() > 5 && input_password_confirmstr.length() > 5 && input_password_confirmstr.equals(input_password_str)) {
                sessionManager.setPassword(input_password_str);


                if (isInternetAvailable) {
                    progressBar.setVisibility(View.VISIBLE);
                    nextArrow.setVisibility(View.GONE);

                    apiService.forgotpassword(facebookKitVerifiedMobileNumber, sessionManager.getType(), facebookVerifiedMobileNumberCountryCode, input_password_str, sessionManager.getDeviceType(), sessionManager.getDeviceId(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(this));

                } else {
                    commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
                }

            } else {
                if (!input_password_confirmstr.equals(input_password_str)) {
                    commonMethods.showMessage(this, dialog, getResources().getString(R.string.Passwordmismatch));
                } else {
                    commonMethods.showMessage(this, dialog, getResources().getString(R.string.InvalidPassword));
                }
            }
        }
    }

    /*
     *   Validate password field
     */
    private boolean validateFirst() {
        if (input_password.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getString(R.string.Enteryourpassword));
            requestFocus(input_password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Validate Confirm password field
     */
    private boolean validateconfrom() {
        if (input_confirmpassword.getText().toString().trim().isEmpty()) {
            input_layout_confirmpassword.setError(getString(R.string.Confirmyourpassword));
            requestFocus(input_confirmpassword);
            return false;
        } else {
            input_layout_confirmpassword.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Focus edit text field
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
            try {
                onSuccessResetPWd(jsonResp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            progressBar.setVisibility(View.GONE);
            nextArrow.setVisibility(View.VISIBLE);
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    private void onSuccessResetPWd(JsonResponse jsonResp) throws JSONException {
        progressBar.setVisibility(View.GONE);
        nextArrow.setVisibility(View.VISIBLE);
        LoginDetails signInUpResultModel = gson.fromJson(jsonResp.getStrResponse(), LoginDetails.class);

        if (signInUpResultModel != null) {


            String driverStatus = signInUpResultModel.getUserStatus();
            sessionManager.setCurrencySymbol(String.valueOf(Html.fromHtml(signInUpResultModel.getCurrencySymbol())));
            sessionManager.setCurrencyCode(signInUpResultModel.getCurrencyCode());
            sessionManager.setPaypalEmail(signInUpResultModel.getPayoutId());
            sessionManager.setDriverSignupStatus(signInUpResultModel.getUserStatus());
            sessionManager.setAcesssToken(signInUpResultModel.getToken());
            sessionManager.setGoogleMapKey(signInUpResultModel.getGoogleMapKey());
            sessionManager.setIsRegister(true);
            sessionManager.setUserType(signInUpResultModel.getCompanyId());


            if (driverStatus != null && driverStatus.equals("Car_details")) {

                String carDeailsModel = gson.toJson(signInUpResultModel.getCarDetailModel());

                JSONArray cardetails = new JSONArray(carDeailsModel);

                StringBuilder carType = new StringBuilder();
                carType.append(getResources().getString(R.string.vehicle_type)).append(",");
                for (int i = 0; i < cardetails.length(); i++) {
                    JSONObject cartype = cardetails.getJSONObject(i);

                    carType.append(cartype.getString("car_name")).append(",");
                }
                sessionManager.setCarType(carType.toString());
                Intent x = new Intent(getApplicationContext(), RegisterCarDetailsActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            } else if (driverStatus != null && driverStatus.equals("Document_details")) {
                // If driver status is document_details then redirect to document upload page
                Intent x = new Intent(getApplicationContext(), DocHomeActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            } else if (driverStatus != null && driverStatus.equals("Pending")) {

                // If driver status is pending check paypal email is exists then redirect to home page otherwise redirect to paypal email address page
                sessionManager.setVehicleId(signInUpResultModel.getVehicleId());
                /*if (sessionManager.getPaypalEmail().length() > 0) {

                    Intent x = new Intent(getApplicationContext(), MainActivity.class);
                    x.putExtra("signinup", true);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                    startActivity(x, bndlanimation);
                    finish();
                } else {
                    Intent signin = new Intent(getApplicationContext(), PaymentPage.class);
                    startActivity(signin);
                    overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                }*/
                openMainActivity();
            } else if (driverStatus != null && driverStatus.equals("Active")) {
                // If driver status is active check paypal email is exists then redirect to home page otherwise redirect to paypal email address page
                sessionManager.setVehicleId(signInUpResultModel.getVehicleId());
               /* if (sessionManager.getPaypalEmail().length() > 0) {
                    Intent x = new Intent(getApplicationContext(), MainActivity.class);
                    x.putExtra("signinup", true);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                    startActivity(x, bndlanimation);
                    finish();
                } else {
                    Intent signin = new Intent(getApplicationContext(), PaymentPage.class);
                    startActivity(signin);
                    overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                }*/openMainActivity();
            } else {
                // Redirect to sign in signup home page
                Intent x = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();

            }


        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        progressBar.setVisibility(View.GONE);
        nextArrow.setVisibility(View.VISIBLE);
    }

    public void openMainActivity(){
        Intent x = new Intent(getApplicationContext(), MainActivity.class);
        x.putExtra("signinup", true);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
        startActivity(x, bndlanimation);
        finish();
    }


}
