package com.trioangle.goferdriver.home;
/**
 * @package com.trioangle.goferdriver.home
 * @subpackage home
 * @category CancelYourTripActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                      CancelYourTripActivity
Its used to get CancelYourTripActivity for rider
*************************************************************** */
public class CancelYourTripActivity extends AppCompatActivity implements ServiceListener {

    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    public AlertDialog dialog;

    @InjectView(R.id.spinner)
    public Spinner spinner;

    public String cancelreason;
    public String cancelmessage;
    @InjectView(R.id.cancel_reason)
    public EditText cancel_reason;
    protected boolean isInternetAvailable;

    @OnClick(R.id.cancel_close)
    public void onClickclose() {
        finish();
    }

    @OnClick(R.id.cancelreservation)
    public void onClickReserv() {
        /*
         *  Update cancel reason in server
         */
        isInternetAvailable = commonMethods.isOnline(this);
        String spinnerpos = String.valueOf(spinner.getSelectedItemPosition());
        if ("0".equals(spinnerpos)) {
            cancelreason = "";
        } else {
            cancelreason = spinner.getSelectedItem().toString();
        }
        cancelmessage = cancel_reason.getText().toString();


        if (isInternetAvailable) {
            cancelTrip();
        } else {
            commonMethods.showMessage(CancelYourTripActivity.this, dialog, getResources().getString(R.string.no_connection));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_your_trip);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        isInternetAvailable = commonMethods.isOnline(this);


        ArrayAdapter<CharSequence> canceladapter;

        canceladapter = ArrayAdapter.createFromResource(
                this, R.array.cancel_types, R.layout.spinner_layout);
        canceladapter.setDropDownViewResource(R.layout.spinner_layout);


        spinner.setAdapter(canceladapter);

        /*
         *  Cancel trip reasons in spinner
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });


    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessCancel();
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
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

    /*
     *  Cancel reason update API called
     */
    public void cancelTrip() {
        if (cancelreason.equals("")) {
            commonMethods.showMessage(CancelYourTripActivity.this, dialog, getResources().getString(R.string.cancelreason));
        } else {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.cancelTrip(sessionManager.getType(), cancelreason, cancelmessage, sessionManager.getTripId(), sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
        }
    }

    public void onSuccessCancel() {

        sessionManager.clearTripID();
        sessionManager.clearTripStatus();
        sessionManager.setDriverAndRiderAbleToChat(false);
        CommonMethods.stopFirebaseChatListenerService(this);
        Intent requestaccept = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(requestaccept);
        finish();
    }


}

