package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category SigninActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.LoginDetails;
import com.trioangle.goferdriver.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.OnSwipeTouchListener;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.goferdriver.util.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;

/* ************************************************************
                SigninActivity
Its used to  get the signin detail function
*************************************************************** */
public class SigninActivity extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;

    Context context;


    public @InjectView(R.id.input_layout_username)
    TextInputLayout input_layout_username;
    public @InjectView(R.id.input_layout_mobile)
    TextInputLayout input_layout_mobile;
    public @InjectView(R.id.input_layout_passsword)
    TextInputLayout input_layout_passsword;
    public @InjectView(R.id.user_edit)
    EditText user_edit;
    public @InjectView(R.id.phone)
    EditText phone;
    public @InjectView(R.id.ccp)
    CountryCodePicker ccp;
    public @InjectView(R.id.password_edit)
    EditText password_edit;
    public @InjectView(R.id.sigin_button)
    Button sigin_button;
    public @InjectView(R.id.forgot_password)
    Button forgot_password;
    public @InjectView(R.id.dochome_back)
    ImageView dochome_back;
    protected boolean isInternetAvailable;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT && resultCode == RESULT_OK) {
           /* if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER) {
                commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));
            } else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER) {
                openPasswordResetActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
            }*/

            openPasswordResetActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
        }


    }

    private void openPasswordResetActivity(String phoneNumber, String countryCode) {

        Intent signin = new Intent(getApplicationContext(), ResetPassword.class);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, phoneNumber);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, countryCode);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        finish();

    }

    @OnClick(R.id.forgot_password)
    public void forgetPassword() {
        /*Intent intent = new Intent(getApplicationContext(), MobileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this,1);
    }

    @OnClick(R.id.dochome_back)
    public void backPressed() {
        onBackPressed();
    }

    @OnClick(R.id.sigin_button)
    public void signIn() {
        String input_password_str = password_edit.getText().toString();

        isInternetAvailable = commonMethods.isOnline(this);
        String phonenumber_str = phone.getText().toString();

        try {
            input_password_str = URLEncoder.encode(input_password_str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sessionManager.setPassword(input_password_str);
        sessionManager.setPhoneNumber(phonenumber_str);
        sessionManager.setCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));

        isInternetAvailable = commonMethods.isOnline(this);
        if (isInternetAvailable) {


            if (!validateMobile("check")) {
                input_layout_mobile.setError(getString(R.string.error_msg_mobilenumber));
            }
            getUserProfile();
            // new SigninSignup().execute(url);
        } else {
            commonMethods.showMessage(getApplicationContext(), dialog, getResources().getString(R.string.no_connection));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);
        ccp.setAutoDetectedCountry(true);
        if (Locale.getDefault().getLanguage().equals("fa") || Locale.getDefault().getLanguage().equals("ar")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        }

        context = this;

        sigin_button.setEnabled(false);
        user_edit.addTextChangedListener(new NameTextWatcher(user_edit));
        password_edit.addTextChangedListener(new NameTextWatcher(password_edit));
        phone.addTextChangedListener(new NameTextWatcher(phone));




    }



    public void getUserProfile() {
        commonMethods.showProgressDialog(this, customDialog);

        String input_password_str = password_edit.getText().toString().trim();
        sessionManager.setPassword(input_password_str);


        apiService.login(sessionManager.getPhoneNumber(), sessionManager.getType(), sessionManager.getCountryCode(), input_password_str, sessionManager.getDeviceId(), sessionManager.getDeviceType(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent signin = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
        startActivity(signin);
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
                onSuccessLogin(jsonResp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }


    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }


    private void onSuccessLogin(JsonResponse jsonResp) throws JSONException {

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
            sessionManager.setCountryCode(signInUpResultModel.getCountryCode());
            sessionManager.setPhoneNumber(signInUpResultModel.getMobileNumber());
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
                x.putExtra("finish",2);//Redirect to SigninSignUpHome
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();
            } else if (driverStatus != null && driverStatus.equals("Document_details")) {
                // If driver status is document_details then redirect to document upload page
                Intent x = new Intent(getApplicationContext(), DocHomeActivity.class);
                x.putExtra("finish",2);//Redirect to SigninSignUpHome
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

                startMainActivity();
            } else if (driverStatus != null && driverStatus.equals("Active")) {
                // If driver status is active check paypal email is exists then redirect to home page otherwise redirect to paypal email address page
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

                startMainActivity();
            } else {
                // Redirect to sign in signup home page
                Intent x = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
                startActivity(x, bndlanimation);
                finish();

            }


        }

    }

    private void startMainActivity() {
        Intent x = new Intent(getApplicationContext(), MainActivity.class);
        x.putExtra("signinup", true);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
        startActivity(x, bndlanimation);
        finish();
    }

    /*
     *   Validate phone number
     */
    private boolean validateMobile(String type) {
        if (phone.getText().toString().trim().isEmpty() || phone.getText().length() < 6) {
            if ("check".equals(type)) {
                //input_layout_mobile.setError(getString(R.string.error_msg_mobilenumber));
                requestFocus(phone);
            }
            return false;
        } else {
            input_layout_mobile.setErrorEnabled(false);
        }


        return true;
    }

    /*
     *   validate password
     */
    private boolean validateLast(String type) {
        if (password_edit.getText().toString().trim().isEmpty() || password_edit.getText().length() < 6) {
            if ("check".equals(type)) {
                //input_layout_passsword.setError(getString(R.string.error_msg_password));
                requestFocus(password_edit);
            }
            return false;
        } else {
            input_layout_passsword.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Validate user name
     */
    private boolean validateFirst() {
        if (user_edit.getText().toString().trim().isEmpty()) {
            input_layout_username.setError(getString(R.string.error_msg_firstname));
            requestFocus(user_edit);
            return false;
        } else {
            input_layout_username.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   focus edit text
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    /*
     *   Text watcher for validate signin fields
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (validateLast("validate") && validateMobile("validate")) {
                sigin_button.setEnabled(true);
                sigin_button.setBackground(getResources().getDrawable(R.drawable.driverloginboarderblue));
            } else {

                sigin_button.setEnabled(false);
                sigin_button.setBackground(getResources().getDrawable(R.drawable.driverloginboarder));
            }
            if (phone.getText().toString().startsWith("0")) {
                phone.setText("");
            }
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.user_edit:
                    validateFirst();
                    break;
                case R.id.password_edit:
                    validateLast("check");
                    break;
                case R.id.phone:
                    validateMobile("check");
                    break;
            }
        }
    }


}
