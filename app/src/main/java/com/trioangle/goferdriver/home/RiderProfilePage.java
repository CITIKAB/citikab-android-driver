package com.trioangle.goferdriver.home;
/**
 * @package com.trioangle.goferdriver.home
 * @subpackage home
 * @category RiderProfilePage
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                      RiderProfilePage
Its used to get RiderProfilePage details
*************************************************************** */
public class RiderProfilePage extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;

    public @InjectView(R.id.profileimage)
    ImageView profileimage;
    public @InjectView(R.id.imgv_rider_accepted_cartypeImage)
    ImageView riderAcceptedCartypeImage;
    public @InjectView(R.id.cancel_lay)
    RelativeLayout cancel_lay;
    public @InjectView(R.id.rating_layout)
    RelativeLayout rating_layout;
    public @InjectView(R.id.nametext)
    TextView ridername;
    public @InjectView(R.id.ratingtext)
    TextView ratingtext;
    public @InjectView(R.id.adresstext)
    TextView adresstext;
    public @InjectView(R.id.droplocation)
    TextView droplocation;
    public @InjectView(R.id.cartype)
    TextView cartype;
    public @InjectView(R.id.cancelicon)
    TextView cancelicon;
    public @InjectView(R.id.cancel_txt)
    TextView cancel_txt;
    //AcceptedDriverDetails riderDetailsModel;
    public RiderDetailsModel riderDetailsModel;

    @OnClick(R.id.back)
    public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.contact_lay)
    public void contact() {
        Intent requstreceivepage = new Intent(getApplicationContext(), RiderContactActivity.class);
        requstreceivepage.putExtra("ridername", riderDetailsModel.getRiderName());
        requstreceivepage.putExtra("riderno", riderDetailsModel.getMobileNumber());
        startActivity(requstreceivepage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_profile_page);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            riderDetailsModel = (RiderDetailsModel) getIntent().getSerializableExtra("riderDetails"); //Obtaining data
        }

                    /*
                    *  Request rider details
                    */
        ridername.setText(riderDetailsModel.getRiderName());
        insertRiderInfoToSession();
        if (riderDetailsModel.getRatingValue().equals("0.0") || riderDetailsModel.getRatingValue().equals("")) {
            rating_layout.setVisibility(View.GONE);
        } else {
            ratingtext.setText(riderDetailsModel.getRatingValue());
        }
        adresstext.setText(riderDetailsModel.getPickupLocation());
        String imageUr = riderDetailsModel.getRiderThumbImage();
        droplocation.setText(riderDetailsModel.getDropLocation());
        cartype.setText(riderDetailsModel.getCarType());

        Picasso.with(getApplicationContext()).load(imageUr)
                .into(profileimage);

        Picasso.with(getApplicationContext()).load(riderDetailsModel.getCarActiveImage()).error(R.drawable.car)
                .into(riderAcceptedCartypeImage);

        if (sessionManager.getTripStatus() != null) {

            if (sessionManager.getTripStatus().equals(CommonKeys.TripStaus.BeginTrip) || sessionManager.getTripStatus().equals(CommonKeys.TripStaus.EndTrip)) {
                cancel_lay.setEnabled(false);
                cancel_lay.setClickable(false);
                cancelicon.setTextColor(getResources().getColor(R.color.cancel_disable_grey));
                cancel_txt.setTextColor(getResources().getColor(R.color.cancel_disable_grey));
            } else {
                cancel_lay.setEnabled(true);
                cancel_lay.setClickable(true);
                cancelicon.setTextColor(getResources().getColor(R.color.app_continue));
                cancel_txt.setTextColor(getResources().getColor(R.color.app_continue));
            }
        } else {
            cancel_lay.setEnabled(true);
            cancel_lay.setClickable(true);
            cancelicon.setTextColor(getResources().getColor(R.color.app_continue));
            cancel_txt.setTextColor(getResources().getColor(R.color.app_continue));
        }
                    /*
                    *  Redirect to trip cancel
                    */
        cancel_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requstreceivepage = new Intent(getApplicationContext(), CancelYourTripActivity.class);
                startActivity(requstreceivepage);
            }
        });

    }

    private void insertRiderInfoToSession() {
        sessionManager.setRiderProfilePic(riderDetailsModel.getRiderThumbImage());
        sessionManager.setRiderRating(riderDetailsModel.getRatingValue());
        sessionManager.setRiderName(riderDetailsModel.getRiderName());
    }
}
