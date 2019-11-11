package com.trioangle.goferdriver.payouts;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.databinding.ActivityBankDetailsBinding;
import com.trioangle.goferdriver.datamodel.BankDetailsModel;
import com.trioangle.goferdriver.datamodel.DriverProfileModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BankDetailsActivity extends AppCompatActivity implements ServiceListener {

    BankDetailsModel bankDetailsModel;

    public @InjectView(R.id.edt_acc_name)
    EditText edtAccName;
    public @InjectView(R.id.edt_acc_num)
    EditText edtAccNum;
    public @InjectView(R.id.edt_bank_acc)
    EditText edtBankAcc;
    public @InjectView(R.id.edt_bank_loc)
    EditText edtBankLoc;
    public @InjectView(R.id.edt_swift_code)
    EditText edtSwiftCode;




    public @Inject
    CommonMethods commonMethods;
    public @Inject
    SessionManager sessionManager;

    private AlertDialog dialog;


    public @Inject
    ApiService apiService;
    private MyClickHandlers handlers;

    public @Inject
    CustomDialog customDialog;
    private String accName, accNum, bankName, bankLoc, swiftCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityBankDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_bank_details);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);


        bankDetailsModel = (BankDetailsModel) getIntent().getSerializableExtra("bankDetailsModel");;
        binding.setBankDetails(bankDetailsModel);
        handlers = new MyClickHandlers(this);
        binding.setHandlers(handlers);



    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(this, dialog, data);
            return;
        }
        if (jsonResp.isSuccess()) {
            onSuccessUpdateBankDetails();
            commonMethods.showMessage(this, dialog, getString(R.string.bank_details_success_message));
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }
    private void onSuccessUpdateBankDetails() {
        onBackPressed();
      /*  startActivity(new Intent(BankDetailsActivity.this,PayoutEmailListActivity.class));
        finish();*/
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }


    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onButtonClick(View view) {

            if (!bankDetailsEmptyCheck()) {
                updateBankDetails();
            }

        }

        public void onBackClicked(View view) {

            onBackPressed();

        }
    }


    /**
     * To Check wheather Bank Details Empty or not
     *
     * @return
     */


    private boolean bankDetailsEmptyCheck() {

        accName = edtAccName.getText().toString();
        accNum = edtAccNum.getText().toString();
        bankName = edtBankAcc.getText().toString();
        bankLoc = edtBankLoc.getText().toString();
      //  swiftCode = edtSwiftCode.getText().toString();

        if (accName == null || accName.equals("")) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.account_holder_name)+" "+getString(R.string.required));
            return true;
        } else if (accNum == null || accNum.equals("")) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.account_number)+" "+getString(R.string.required));

            return true;
        } else if (bankName == null || bankName.equals("")) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.name_of_bank)+" "+getString(R.string.required));

            return true;
        } else if (bankLoc == null || bankLoc.equals("")) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.bank_location)+" "+getString(R.string.required));

            return true;
        } /*else if (swiftCode == null || swiftCode.equals("")) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.bic_swift_code)+" "+getString(R.string.required));

            return true;
        }
*/
        return false;
    }


    /*
     * To update Bank Details
     * */
    private void updateBankDetails() {
        commonMethods.showProgressDialog((AppCompatActivity) this, customDialog);
        apiService.updateBankDetails(getBankDetailsHaspMap()).enqueue(new RequestCallback(this));
    }


    /**
     * Bank Details params
     *
     * @return hash Map contains Bank Details
     */
    public HashMap<String, String> getBankDetailsHaspMap() {
        HashMap<String, String> bankHashMap = new HashMap<>();
        bankHashMap.put("token", sessionManager.getAccessToken());
        bankHashMap.put("account_holder_name", accName);
        bankHashMap.put("account_number", accNum);
        bankHashMap.put("bank_name", bankName);
        bankHashMap.put("bank_location", bankLoc);
        //bankHashMap.put("bank_code", swiftCode);


        return bankHashMap;
    }

}
