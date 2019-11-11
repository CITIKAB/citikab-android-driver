package com.trioangle.goferdriver.payouts;

/**
 *
 * @package     com.makent.trioangle
 * @subpackage  Profile
 * @category    PayoutEmailListActivity
 * @author      Trioangle Product Team
 * @version     1.1
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.payouts.payout_model_classed.PayPalEmailAdapter;
import com.trioangle.goferdriver.network.AppController;


import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.payouts.payout_model_classed.PayoutDetail;
import com.trioangle.goferdriver.payouts.payout_model_classed.PayoutDetailResult;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.ConnectionDetector;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static com.trioangle.goferdriver.util.Enums.REQ_CURRENCYLIST;

/* ************************************************************
                   Payout Email list Page
Show list of PayPal email address for payout option and to change payout email delete, set default
*************************************************************** */
public class PayoutEmailListActivity extends AppCompatActivity implements View.OnClickListener,ServiceListener{

    public @Inject
    ApiService apiService;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    Gson gson;

    public  @Inject
    SessionManager sessionManager;

    RecyclerView recyclerView;

    PayPalEmailAdapter adapter;
    String paypalemaillist[];

    RelativeLayout payoutemaillist_title;
    Button payout_addpayout,payout_addstripe,payout_bank_details;
    String payoutid;
    int payoutoption;
    public String userid;

    public Context context;
    public TextView payoutmain_title;
    protected boolean isInternetAvailable;
    public @Inject
    CustomDialog customDialog;

    PayoutDetailResult payoutDetailResult;
    ArrayList<PayoutDetail> payoutDetails = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_email_list);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


        payoutmain_title = (TextView) findViewById(R.id.payoutmain_title);
        payoutemaillist_title=(RelativeLayout)findViewById(R.id.payoutemaillist_title);
        payout_addpayout=(Button)findViewById(R.id.payout_addpayout);
        payout_addstripe=(Button) findViewById(R.id.payout_addstripe);
        payout_bank_details=(Button) findViewById(R.id.payout_bank_details);
        payout_addpayout.setVisibility(View.GONE);
        payout_addstripe.setVisibility(View.GONE);
        payout_addpayout.setOnClickListener(this);
        payout_addstripe.setOnClickListener(this);
        payout_bank_details.setOnClickListener(this);
        payoutemaillist_title.setOnClickListener(this);



        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        adapter = new PayPalEmailAdapter(this,this, payoutDetails);

        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        recyclerView.setAdapter(adapter);
        //load(0);
        isInternetAvailable = getNetworkState().isConnectingToInternet();

    }


    @Override
    protected void onResume() {
        super.onResume();
        isInternetAvailable = getNetworkState().isConnectingToInternet();
        if (isInternetAvailable) {
            loadPayout();
           // getBankDetails();
        }else {
            snackBar(getResources().getString(R.string.Interneterror));
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            adapter = new PayPalEmailAdapter(this,this, makent_host_modelList);

        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.payoutemaillist_title: {
                onBackPressed();
            }
            break;

            case R.id.payout_addpayout: {
                // Get address details for add new payout option
                Intent x = new Intent(getApplicationContext(), PayoutAddressDetailsActivity.class);
                startActivity(x);
//                finish();
            }
            break;

            case R.id.payout_addstripe: {
                // Get address details for add new payout option

                Intent x = new Intent(getApplicationContext(), PayoutBankDetailsActivity.class);
                    startActivity(x);

            }
            break;
            case R.id.payout_bank_details:
            {
                Intent x = new Intent(getApplicationContext(), BankDetailsActivity.class);
                startActivity(x);
            }
            break;
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (jsonResp.isSuccess()) {
            onSuccessPayout(jsonResp); // onSuccess call method
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            payoutmain_title.setVisibility(View.VISIBLE);
           /* payout_addpayout.setVisibility(View.VISIBLE);
            payout_addstripe.setVisibility(View.VISIBLE);*/
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

        //snackBar();
    }
    // Get PayPal email address from API
    public void loadPayout(){
        commonMethods.showProgressDialog(this, customDialog);
        apiService.payoutDetails(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_CURRENCYLIST,this));
    }

    public void onSuccessPayout(JsonResponse jsonResp){
        payoutDetailResult = gson.fromJson(jsonResp.getStrResponse(), PayoutDetailResult.class);
        payoutDetails.clear();
        ArrayList <PayoutDetail> payDetail= payoutDetailResult.getPayout_details();
        payoutDetails.addAll(payDetail);
        adapter.notifyDataChanged();
        if (payoutDetails.size()>0){

            payoutmain_title.setVisibility(View.GONE);

          /*  payout_addpayout.setVisibility(View.VISIBLE);
            payout_addstripe.setVisibility(View.VISIBLE);*/
        }else{

            payoutmain_title.setVisibility(View.VISIBLE);
          /*  payout_addpayout.setVisibility(View.VISIBLE);
            payout_addstripe.setVisibility(View.VISIBLE);*/
        }
    }

    //Show network error and exception
    public void snackBar(String statusmessage) {
        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(recyclerView, "", Snackbar.LENGTH_LONG);
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
        if (isInternetAvailable) {
            textViewTop.setText(statusmessage);
        } else {
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

    // Check network available or not
    public ConnectionDetector getNetworkState() {
        ConnectionDetector connectionDetector = new ConnectionDetector(this);
        return connectionDetector;
    }
}
