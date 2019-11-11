package com.trioangle.goferdriver.rating;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category Riderrating
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.InvoiceModel;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.datamodel.TripRatingResult;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
/* ************************************************************
                Riderrating
Its used to get the rider rating details
*************************************************************** */

public class Riderrating extends AppCompatActivity implements ServiceListener {

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

    public @InjectView(R.id.driver_comments)
    EditText ridercomments;
    public @InjectView(R.id.profile_image1)
    CircleImageView profile_image1;


    protected boolean isInternetAvailable;
    private SimpleRatingBar riderrate;


    @OnClick(R.id.toplayout)
    public void onToplay() {
        onBackPressed();
    }

    @OnClick(R.id.tvskip)
    public void skip() {
        onBackPressed();
    }


    @OnClick(R.id.rate_submit)
    public void onSubmit() {
        float rating = riderrate.getRating();
        String ratingstr = String.valueOf(rating);
        if (!"0.0".equals(ratingstr)) {
            String ridercomment = ridercomments.getText().toString();
            ridercomment = ridercomment.replaceAll("[\\t\\n\\r]", " ");

            if (isInternetAvailable) {
                submitRating(ratingstr, ridercomment);
            } else {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
            }
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.error_msg_rating));
        }
    }

    String user_thumb_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riderrating);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

                    /*
                   *   Internet connection check
                   */
        isInternetAvailable = commonMethods.isOnline(this);

        riderrate = (SimpleRatingBar) findViewById(R.id.driver_rating);
        String laydir = getString(R.string.layout_direction);
        if ("1".equals(laydir)) {
            riderrate.setGravity(SimpleRatingBar.Gravity.Right);
        }

        ridercomments.clearFocus();

                    /*
                   *   Rider pofile image
                   */
        Intent intent = getIntent();
        if (intent.getStringExtra("imgprofile")!=null&&!TextUtils.isEmpty(intent.getStringExtra("imgprofile"))) {
            user_thumb_image = intent.getStringExtra("imgprofile");
        }else {
            user_thumb_image=sessionManager.getRiderProfilePic();
        }

        Picasso.with(getApplicationContext()).load(user_thumb_image)
                .into(profile_image1);


    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            TripRatingResult earningModel = gson.fromJson(jsonResp.getStrResponse(), TripRatingResult.class);
            ArrayList<InvoiceModel> invoiceModels = earningModel.getInvoice();
            Bundle bundle = new Bundle();
            bundle.putSerializable("invoiceModels", invoiceModels);
            /*Intent main = new Intent(getApplicationContext(), PaymentAmountPage.class);
            main.putExtras(bundle);
            main.putExtra("AmountDetails", jsonResp.getStrResponse());
            startActivity(main);*/
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            //onBackPressed();
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {


            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }

    /*
      *   Submit rating API called
      */
    public void submitRating(String ratingstr, String comments) {
        commonMethods.showProgressDialog(Riderrating.this, customDialog);

        apiService.tripRating(sessionManager.getTripId(), ratingstr, comments, sessionManager.getType(), sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
    }


    @Override
    public void onBackPressed() {

        if (getIntent().getIntExtra("back",0)==1){
            super.onBackPressed();
        }else{
            sessionManager.clearTripID();
            sessionManager.clearTripStatus();
            sessionManager.setDriverStatus(CommonKeys.DriverStatus.Online);
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            riderrate.setRating(0);
            ridercomments.setText("");
        }

    }
}

