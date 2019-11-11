package com.trioangle.goferdriver.payouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.ConnectionDetector;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;

/* ************************************************************
                   Payout Get Email Page
Get  PayPal email address for payout option
*************************************************************** */
public class PayoutEmailActivity extends AppCompatActivity implements View.OnClickListener,ServiceListener{

    public @Inject
    ApiService apiService;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    Gson gson;

    public @Inject
    SessionManager sessionManager;
    RelativeLayout payoutemail_title;
    Button payout_submit;
    EditText payoutemail_edittext;
    String payoutemail,address1,address2,city,state,pincode,country;

    public @Inject
    CustomDialog customDialog;

    protected boolean isInternetAvailable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_email);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        Intent x= getIntent();
        address1=x.getStringExtra("address1");
        address2=x.getStringExtra("address2");
        city=x.getStringExtra("city");
        state=x.getStringExtra("state");
        country=x.getStringExtra("country");
        pincode=x.getStringExtra("postal_code");


        payoutemail_title=(RelativeLayout)findViewById(R.id.payoutemail_title);
        payout_submit=(Button)findViewById(R.id.payout_submit);

        payout_submit.setOnClickListener(this);
        payoutemail_title.setOnClickListener(this);


        payoutemail_edittext=(EditText) findViewById(R.id.payoutemail_edittext);

        disableSubmitButton();
        payoutemail_edittext.addTextChangedListener(new EmailTextWatcher(payoutemail_edittext));

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();

            commonMethods.snackBar(jsonResp.getStatusMsg(),"",false,2,payoutemail_edittext,payoutemail_edittext,getResources(),this);


            finish();
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

        }
        }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.snackBar(jsonResp.getStatusMsg(),"",false,2,payoutemail_edittext,payoutemail_edittext,getResources(),this);
    }

    // Validate email field
    private class EmailTextWatcher implements TextWatcher {

        private View view;

        private EmailTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

               /* case R.id.signup_email:
                    validateEmail();
                    break;
*/
            }

            if(validateEmail())
            {
                enableSubmitButton();
            }else {
                disableSubmitButton();
            }
        }
    }

    private boolean validateEmail() {

        String email = payoutemail_edittext.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {

            return false;
        } else {
        }

        return true;
    }

    private void enableSubmitButton(){
        payout_submit.setEnabled(true);
        payout_submit.setBackgroundResource(R.drawable.black_button_background);
    }

    private void disableSubmitButton(){
        payout_submit.setEnabled(false);
        payout_submit.setBackgroundResource(R.color.gray_border);
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.payoutemail_title: {
                onBackPressed();
            }
            break;

            case R.id.payout_submit: {
                payoutemail=payoutemail_edittext.getText().toString();

                hideSoftKeyboard();  // Hide keyboard
                isInternetAvailable = getNetworkState().isConnectingToInternet();
                if (isInternetAvailable) {
                    Map<String, String> imageObject = new HashMap<String, String>();

                    imageObject.put("address1", address1);
                    imageObject.put("address2", address2);
                    imageObject.put("token", sessionManager.getAccessToken());
                    imageObject.put("email", payoutemail);
                    imageObject.put("city", city);
                    imageObject.put("state", state);
                    imageObject.put("country", country);
                    imageObject.put("postal_code", pincode);
                    imageObject.put("payout_method", "paypal");
                    updateProf();
                   // updateProfile(imageObject); // Call update API to update Email and address details
                }else {
                    snackBar(getResources().getString(R.string.Interneterror));
                    commonMethods.snackBar(getResources().getString(R.string.Interneterror),"",false,2,payoutemail_edittext,payoutemail_edittext,getResources(),this);
                }
            }
            break;
        }
    }

    // Hide keyboard function
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

public void updateProf(){
    CommonMethods.DebuggableLogI("Address 1", address1);
    CommonMethods.DebuggableLogI("Address 2", address2);
    CommonMethods.DebuggableLogI("email", payoutemail);
    CommonMethods.DebuggableLogI("city", city);
    CommonMethods.DebuggableLogI("state", state);
    CommonMethods.DebuggableLogI("Country", country);
    CommonMethods.DebuggableLogI("pincode", pincode);
    CommonMethods.DebuggableLogI("token", sessionManager.getAccessToken());
    apiService.addPayoutPreference(sessionManager.getAccessToken(), address1,address2,payoutemail,city,state,country,pincode,"paypal").enqueue(new RequestCallback( this));
    commonMethods.showProgressDialog(this, customDialog);
}




    // Show network error and exception
    public void snackBar(String statusmessage)
    {
        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(payout_submit, "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar, null);
        // Configure the view

        RelativeLayout snackbar_background = (RelativeLayout) snackView.findViewById(R.id.snackbar);
        snackbar_background.setBackgroundColor(getResources().getColor(R.color.app_background));

        Button button = (Button) snackView.findViewById(R.id.snackbar_action);
        button.setVisibility(View.GONE);
        button.setText(getResources().getString(R.string.showpassword));
        button.setTextColor(getResources().getColor(R.color.app_background));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView textViewTop = (TextView) snackView.findViewById(R.id.snackbar_text);
        if (isInternetAvailable){
            textViewTop.setText(statusmessage);
        }else {
            textViewTop.setText(getResources().getString(R.string.Interneterror));
        }

        // textViewTop.setTextSize(getResources().getDimension(R.dimen.midb));
        textViewTop.setTextColor(getResources().getColor(R.color.white));

// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.app_background));
        snackbar.show();

    }

    // Check network is avalable or not
    public ConnectionDetector getNetworkState() {
        ConnectionDetector connectionDetector = new ConnectionDetector(this);
        return connectionDetector;
    }
}
