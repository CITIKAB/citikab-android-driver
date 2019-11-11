package com.trioangle.goferdriver.signinsignup;
/**
 * @package com.trioangle.goferdriver.signinsignup
 * @subpackage signinsignup model
 * @category RegisterCarDetailsActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                RegisterCarDetailsActivity
Its used to get the register car details function
*************************************************************** */
public class RegisterCarDetailsActivity extends AppCompatActivity implements ServiceListener {


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

    public @InjectView(R.id.vehicle_type)
    Spinner vehicle_type;
    public @InjectView(R.id.vehicle_name_edit)
    EditText vehicle_name_edit;
    public @InjectView(R.id.vehicle_number_edit)
    EditText vehicle_number_edit;
    public @InjectView(R.id.vehicle_name_lay)
    TextInputLayout vehicle_name_lay;
    public @InjectView(R.id.vehicle_number_lay)
    TextInputLayout vehicle_number_lay;
    public @InjectView(R.id.dochome_back)
    ImageView dochome_back;
    public @InjectView(R.id.btn_continue)
    Button btn_continue;
    protected boolean isInternetAvailable;

    @OnClick(R.id.btn_continue)
    public void btnContinue() {
        updateVehicleDetails();
    }

    @OnClick(R.id.dochome_back)
    public void dochomeBack() {
        onBackPressed();
    }

    private int isFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car_details);


        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);

        isInternetAvailable = commonMethods.isOnline(this);

        vehicle_name_edit.addTextChangedListener(new NameTextWatcher(vehicle_name_edit));
        vehicle_number_edit.addTextChangedListener(new NameTextWatcher(vehicle_number_edit));

        isFinish=getIntent().getIntExtra("finish",0);
        String carType = sessionManager.getCarType();
        String[] cartypes = carType.split(",");
        // Assume this list is from server
        List<String> carTypeList = Arrays.asList(cartypes);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                carTypeList);


        vehicle_type.setAdapter(arrayAdapter);
    }


    public void updateVehicleDetails() {
        isInternetAvailable = commonMethods.isOnline(this);
        if (!validateText(vehicle_name_lay, vehicle_name_edit)) {
            return;
        }
        if (!validateText(vehicle_number_lay, vehicle_number_edit)) {
            return;
        }

        if (vehicle_type.getSelectedItemId() != 0) {
            sessionManager.setVehicleId(String.valueOf(vehicle_type.getSelectedItemId()));

            String vehicle_name_editstr = vehicle_name_edit.getText().toString();
            String vehicle_number_editstr = vehicle_number_edit.getText().toString();
            try {
                vehicle_name_editstr = URLEncoder.encode(vehicle_name_editstr, "UTF-8");
                vehicle_number_editstr = URLEncoder.encode(vehicle_number_editstr, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            if (isInternetAvailable) {
                commonMethods.showProgressDialog(this, customDialog);

                apiService.vehicleDetails(vehicle_type.getSelectedItemId(), vehicle_name_editstr, vehicle_type.getSelectedItem().toString(), vehicle_number_editstr, sessionManager.getAccessToken()).enqueue(new RequestCallback(this));

            } else {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.Interneterror));
            }
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.error_msg_vehicletype));
        }


    }


    private boolean validateText(TextInputLayout inputLayout, EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            if (editText.getId() == R.id.vehicle_name_edit)
                inputLayout.setError(getString(R.string.error_msg_vehiclename));
            else
                inputLayout.setError(getString(R.string.error_msg_vehiclenumber));
            requestFocus(editText);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
            onSuccessCarDetails(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());

        }
    }

    @Override
    public void onFailure(JsonResponse onFailure, String data) {

        CommonMethods.DebuggableLogI("onFailure ", "");
    }


    private void onSuccessCarDetails(JsonResponse jsonResp) {

        VehicleDetails vehicleResultModel = gson.fromJson(jsonResp.getStrResponse(), VehicleDetails.class);
        if (vehicleResultModel != null) {
            sessionManager.setDriverSignupStatus("Document_details");
            sessionManager.setIsRegister(true);
            Intent signin = new Intent(getApplicationContext(), DocHomeActivity.class);
            startActivity(signin);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

        }

    }

    @Override
    public void onBackPressed() {
        if (isFinish==1) {
            finishAffinity();
        } else if (isFinish==2){
            Intent signin = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
            startActivity(signin);
            overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
            finish();
        }else {
            super.onBackPressed();
            Intent signin = new Intent(getApplicationContext(), SigninSignupHomeActivity.class);
            startActivity(signin);
            overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
        }
    }


    /*
     *   Text watcher for validate vehicle name and number field
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CommonMethods.DebuggableLogI("onFailure ", Integer.toString(i));
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CommonMethods.DebuggableLogI("onFailure ", Integer.toString(i));
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.vehicle_name_edit:
                    validateText(vehicle_name_lay, vehicle_name_edit);
                    break;
                case R.id.vehicle_number_edit:
                    validateText(vehicle_number_lay, vehicle_number_edit);
                    break;
                default:
                    break;

            }
        }
    }


}
