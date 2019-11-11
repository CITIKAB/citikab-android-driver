package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category MobileActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.trioangle.goferdriver.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.ForgetPwdModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.ApiSessionAppConstants;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
/* ************************************************************
                MobileActivity
Its used to get the mobile number detail function
*************************************************************** */

public class MobileActivity extends AppCompatActivity implements ServiceListener {


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

    public @InjectView(R.id.ccp)
    CountryCodePicker ccp;
    public @InjectView(R.id.next)
    RelativeLayout next;
    public @InjectView(R.id.back)
    RelativeLayout back;
    public @InjectView(R.id.mobilelayout)
    LinearLayout mobilelayout;
    public @InjectView(R.id.entermobileno)
    TextView entermobileno;
    public @InjectView(R.id.phone)
    EditText phone;
    public @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    protected boolean isInternetAvailable;

    @OnClick(R.id.next)
    public void next() {
        getUserProfile();
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilenumber);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(this);

        ccp.detectLocaleCountry(true);
        if (Locale.getDefault().getLanguage().equals("fa")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        } else if (Locale.getDefault().getLanguage().equals("es")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.SPANISH);
        } else if (Locale.getDefault().getLanguage().equals("ar")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        } else if (Locale.getDefault().getLanguage().equals("en")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().getSharedElementEnterTransition().setDuration(600);
            getWindow().getSharedElementReturnTransition().setDuration(600)
                    .setInterpolator(new DecelerateInterpolator());
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            entermobileno.setTransitionName("mobilenumber");
            mobilelayout.setTransitionName("mobilelayout");
        }


        //Text listner
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                CommonMethods.DebuggableLogI("Character sequence ", " Check");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (phone.getText().toString().startsWith("0")) {
                    phone.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                CommonMethods.DebuggableLogI("Character sequence ", " Checkins");

            }
        });

        sessionManager.setCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));


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

            onSuccessForgetPwd(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            onSuccessForgetPwd(jsonResp);

        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        progressBar.setVisibility(View.GONE);
        backArrow.setVisibility(View.VISIBLE);
    }


    private void onSuccessForgetPwd(JsonResponse jsonResp) {

        progressBar.setVisibility(View.GONE);
        backArrow.setVisibility(View.VISIBLE);

        ForgetPwdModel forgetPwdModel = gson.fromJson(jsonResp.getStrResponse(), ForgetPwdModel.class);
        if (forgetPwdModel != null) {


            if (forgetPwdModel.getStatusCode().matches("1")) {
                progressBar.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);
                sessionManager.setTemporaryPhonenumber(phone.getText().toString());
                sessionManager.setTemporaryCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));

                /*String otp = forgetPwdModel.getOtp();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Your OTP IS " + forgetPwdModel.getOtp(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();*/
                /*Intent intent = new Intent(getApplicationContext(), RegisterOTPActivity.class);
                intent.putExtra("otp", otp);
                intent.putExtra("resetpassword", true);
                if (sessionManager.getisEdit())
                    intent.putExtra("phone_number", phone.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                if (sessionManager.getisEdit()) {
                    finish();
                }*/

                FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
            } else {
                progressBar.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);
                if (forgetPwdModel.getStatusMessage().equals("Message sending Failed,please try again..")) {

                    /*String otp = forgetPwdModel.getOtp();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Your OTP IS " + forgetPwdModel.getOtp(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();*/
                    /*Intent intent = new Intent(getApplicationContext(), RegisterOTPActivity.class);
                    intent.putExtra("otp", otp);
                    intent.putExtra("resetpassword", true);
                    if (sessionManager.getisEdit())
                        intent.putExtra("phone_number", phone.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                    if (sessionManager.getisEdit()) {
                        finish();
                    }*/
                    sessionManager.setTemporaryPhonenumber(phone.getText().toString());
                    sessionManager.setTemporaryCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
                    FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
                } else {
                    commonMethods.showMessage(this, dialog, forgetPwdModel.getStatusMessage());

                }
            }
        }
    }


    public void getUserProfile() {

        isInternetAvailable = commonMethods.isOnline(this);
        String phonestr = phone.getText().toString();


        sessionManager.setCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
        if (phonestr.length() == 0) {

            commonMethods.showMessage(this, dialog, getString(R.string.pleaseentermobile));


        } else if (phonestr.length() > 5) {
            if (isInternetAvailable) {


                progressBar.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.GONE);

// isEdit is set from Driver profile page
                if (!sessionManager.getisEdit()) {
                    // this is from forgot password

                    //apiService.numberValidation(sessionManager.getType(), phone.getText().toString(), sessionManager.getCountryCode(), "1", sessionManager.getLanguageCode()).enqueue(new RequestCallback(this));
                    // here, phone number is stored to retrive from facebook account kit
                    sessionManager.setPhoneNumber(phone.getText().toString());
                } else {

                    //apiService.numberValidation(sessionManager.getType(), phone.getText().toString(), sessionManager.getCountryCode(), "", sessionManager.getLanguageCode()).enqueue(new RequestCallback(this));


                }


            } else {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
            }

        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.InvalidMobileNumber));
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) { // confirm that this response matches your request
            if (resultCode == ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_SUCCESS) {
                if(sessionManager.getisEdit())
                {
                    this.finish();
                }
                else{
                    callResetPasswordAPI();
                }
            }
        }
    }

    private void callResetPasswordAPI() {
        Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

}
