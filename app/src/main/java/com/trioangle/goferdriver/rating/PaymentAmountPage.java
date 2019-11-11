package com.trioangle.goferdriver.rating;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category PaymentAmountPage
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.InvoiceModel;
import com.trioangle.goferdriver.datamodel.InvoicePaymentDetail;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.pushnotification.Config;
import com.trioangle.goferdriver.pushnotification.NotificationUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.CommonKeys.IS_ALREADY_IN_TRIP;
import static com.trioangle.goferdriver.util.Enums.REQ_CASH_COLLECTED;
import static com.trioangle.goferdriver.util.Enums.REQ_GET_INVOICE;

/* ************************************************************
                PaymentAmountPage
Its used to get rider payment screen page function
*************************************************************** */
public class PaymentAmountPage extends AppCompatActivity implements ServiceListener {

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
    public AlertDialog dialog;

    public @InjectView(R.id.payment_completed)
    Button payment_completed;
    public @InjectView(R.id.adminamountlayout)
    RelativeLayout adminamountlayout;
    public @InjectView(R.id.oweamountlayout)
    RelativeLayout oweamountlayout;
    public @InjectView(R.id.driverpayoutlayout)
    RelativeLayout driverpayoutlayout;
    public @InjectView(R.id.cashcollectamountlayout)
    RelativeLayout cashcollectamountlayout;
    public @InjectView(R.id.basefare_amount)
    TextView basefare_amount;
    public @InjectView(R.id.distance_fare)
    TextView distance_fare;
    public @InjectView(R.id.time_fare)
    TextView time_fare;
    public @InjectView(R.id.fee)
    TextView fee;
    public @InjectView(R.id.totalamount)
    TextView totalamount;
    public @InjectView(R.id.total_payouts)
    TextView total_payouts;
    public @InjectView(R.id.oweamount)
    TextView oweamount;
    public @InjectView(R.id.driverpayout)
    TextView driverpayout;
    public @InjectView(R.id.adminamount)
    TextView adminamount;
    public @InjectView(R.id.rvPrice)
    RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> priceList = new ArrayList<HashMap<String, String>>();
    public String payment_status;
    public String payment_method;
    protected boolean isInternetAvailable;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList<InvoiceModel> invoiceModels = new ArrayList<>();
    @OnClick(R.id.back)
    public void backPressed() {
        onBackPressed();
    }

    private InvoicePaymentDetail invoicePaymentDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_amount_page);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(this);

        if (isInternetAvailable){
            commonMethods.showProgressDialog(this,customDialog);
            apiService.getInvoice(sessionManager.getAccessToken(),sessionManager.getTripId(),sessionManager.getType()).enqueue(new RequestCallback(REQ_GET_INVOICE,this));
        }

        /*Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        invoiceModels.addAll((ArrayList<InvoiceModel>) bundle.getSerializable("invoiceModels"));*/
        //checkjsonkey();
                    /*
                    *  Common loader and internet connection
                    */


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        payment_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    /*
                    *  If trip is cash payment the driver confirm the payment
                    */
                if (payment_completed.getText().toString().equals(getResources().getString(R.string.cashcollected))) {
                     /*
                    *  Update driver cash collected in server
                    */
                    isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                    if (isInternetAvailable) {
                        cashCollected();
                    } else {
                        snackBar(getResources().getString(R.string.no_connection), getResources().getString(R.string.go_online), false, 2);
                    }
                } else {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                }
            }
        });

        //loaddata(getIntent().getStringExtra("AmountDetails"));

        System.out.println("Delete trip id : "+sessionManager.getTripId());
        deleteTripDb(sessionManager.getTripId());
        receivepushnotification();

    }

    public static void deleteTripDb(String tripID){
        try{
            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CommonKeys.LiveTrackingFirebaseDatabaseName).child(tripID);
            root.removeValue();
            System.out.println("Trip ID Removed  : "+tripID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        CommonKeys.IS_ALREADY_IN_TRIP=true;
        sessionManager.setDriverAndRiderAbleToChat(false);
        CommonMethods.stopFirebaseChatListenerService(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

    /*
   *   Load price data
   */
    public void loaddata(String amountdetails) {
        try {
            RecyclerView.Adapter adapter = new PriceRecycleAdapter(this,invoiceModels);
            recyclerView.setAdapter(adapter);
            String access_fee = "";
            String base_fare = "";
            String total_km_fare = "";
            String total_time_fare = "";
            String total_fare = "";

            String applied_owe_amount = "";
            String admin_amount = "";
            String driver_total_fare = "";

            JSONObject jObj = new JSONObject(amountdetails);
            if (jObj.has("payment_details")) {
                JSONObject jsonObject = jObj.getJSONObject("payment_details");
                payment_method = jsonObject.getString("payment_method");

                total_fare = jsonObject.getString("total_fare");
            }else{
                payment_status = jObj.getString("payment_status");
                payment_method = jObj.getString("payment_method");
                total_fare = jObj.getString("total_fare");
            }

            if ("Completed".equals(payment_status)) {
                payment_completed.setTextColor(getResources().getColor(R.color.ub__contact_resolved_green));
                payment_completed.setText(getResources().getString(R.string.paid));
            }



            if (payment_method.contains("Cash")) {
                if (Float.valueOf(total_fare) > 0) {
                    payment_completed.setBackgroundColor(getResources().getColor(R.color.app_button));
                    payment_completed.setTextColor(getResources().getColor(R.color.white));
                    payment_completed.setText(getResources().getString(R.string.cashcollected));
                } else {
                    payment_completed.setBackgroundColor(getResources().getColor(R.color.black_alpha_20));
                    payment_completed.setTextColor(getResources().getColor(R.color.white));
                    payment_completed.setText(getResources().getString(R.string.waitforrider));
                    payment_completed.setEnabled(false);
                }
            } else {

            }

            basefare_amount.setText(sessionManager.getCurrencySymbol() + base_fare);
            distance_fare.setText(sessionManager.getCurrencySymbol() + total_km_fare);
            time_fare.setText(sessionManager.getCurrencySymbol() + total_time_fare);
            fee.setText("-" + sessionManager.getCurrencySymbol() + access_fee);
            driver_total_fare = driver_total_fare.replaceAll(",", ".");
            totalamount.setText(sessionManager.getCurrencySymbol() + driver_total_fare);

            adminamount.setText(sessionManager.getCurrencySymbol() + admin_amount);
            oweamount.setText("- " + sessionManager.getCurrencySymbol() + applied_owe_amount);

            total_fare = total_fare.replaceAll(",", ".");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    *   Receive push notification
    */
    public void receivepushnotification() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received


                    String JSON_DATA = sessionManager.getPushJson();


                    if (JSON_DATA != null) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(JSON_DATA);
                            if (jsonObject.getJSONObject("custom").has("trip_payment")) {
                                sessionManager.setDriverAndRiderAbleToChat(false);
                                CommonMethods.stopFirebaseChatListenerService(getApplicationContext());
                                //payment_completed.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                                payment_completed.setTextColor(getResources().getColor(R.color.ub__contact_resolved_green));
                                payment_completed.setText(getResources().getString(R.string.payment_done));
                                // showDialog("Payment completed successfully");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();


        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /*
  *    Show dialog like arrive now push notification
  */
    public void showDialog(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.addphoto_header, null);
        TextView tit = (TextView) view.findViewById(R.id.header);
        tit.setText(getResources().getString(R.string.paymentcompleted));
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCustomTitle(view);
        builder.setTitle(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), Riderrating.class);
                        intent.putExtra("imgprofile",invoicePaymentDetail.getRiderImage());
                        startActivity(intent);
                        /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);*/
                    }
                })

                .show();
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
            switch (jsonResp.getRequestCode()){
                case REQ_GET_INVOICE:
                    if (jsonResp.isSuccess()) {
                        getInvoice(jsonResp);
                    } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                        commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    }
                    break;

                case REQ_CASH_COLLECTED:
                    if (jsonResp.isSuccess()) {
                        commonMethods.hideProgressDialog();
                        sessionManager.setDriverAndRiderAbleToChat(false);
                        CommonMethods.stopFirebaseChatListenerService(getApplicationContext());
                        showDialog(getResources().getString(R.string.paymentcompleted));
                    } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                        commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    }
                    break;
            }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    private void getInvoice(JsonResponse jsonResponse){
        invoicePaymentDetail=gson.fromJson(jsonResponse.getStrResponse(), InvoicePaymentDetail.class);
        invoiceModels.addAll(invoicePaymentDetail.getInvoice());
        System.out.println("invoiceRider "+invoicePaymentDetail.getRiderImage());
        loaddata(jsonResponse.getStrResponse());
    }


    /*
      *  Cash collected API called
      */
    public void cashCollected() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.cashCollected(sessionManager.getTripId(), sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_CASH_COLLECTED,this));
    }

    /*
      *   show error or information
      */
    public void snackBar(String message, String buttonmessage, boolean buttonvisible, int duration) {
        // Create the Snackbar
        Snackbar snackbar;
        RelativeLayout snackbar_background;
        TextView snack_button;
        TextView snack_message;

        // Snack bar visible duration
        if (duration == 1)
            snackbar = Snackbar.make(payment_completed, "", Snackbar.LENGTH_INDEFINITE);
        else if (duration == 2)
            snackbar = Snackbar.make(payment_completed, "", Snackbar.LENGTH_LONG);
        else
            snackbar = Snackbar.make(payment_completed, "", Snackbar.LENGTH_SHORT);

        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar, null);
        // Configure the view

        snackbar_background = (RelativeLayout) snackView.findViewById(R.id.snackbar);
        snack_button = (TextView) snackView.findViewById(R.id.snack_button);
        snack_message = (TextView) snackView.findViewById(R.id.snackbar_text);

        snackbar_background.setBackgroundColor(getResources().getColor(R.color.textblack)); // Background Color

        if (buttonvisible) // set Right side button visible or gone
            snack_button.setVisibility(View.VISIBLE);
        else
            snack_button.setVisibility(View.GONE);

        snack_button.setTextColor(getResources().getColor(R.color.ub__ui_core_warning)); // set right side button text color
        snack_button.setText(buttonmessage); // set right side button text


        snack_message.setTextColor(getResources().getColor(R.color.white)); // set left side main message text color
        snack_message.setText(message);  // set left side main message text

// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.textblack));
        snackbar.show();


    }


}
