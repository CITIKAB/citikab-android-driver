package com.trioangle.goferdriver.tripsdetails;

/**
 * @package com.trioangle.goferdriver.tripsdetails
 * @subpackage tripsdetails model
 * @category YourTrips
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.PastTripModel;
import com.trioangle.goferdriver.datamodel.YourTripModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                YourTrips page
Its used to your current all the trips to show the fuction
*************************************************************** */
public class YourTrips extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ServiceListener {


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
    //This is our tablayout
    @InjectView(R.id.tabLayout)
    public TabLayout tabLayout;
    //This is our viewPager
    @InjectView(R.id.pager)
    public ViewPager viewPager;
    public List<PastTripModel> pastTripLists = new ArrayList<>();
    public List<PastTripModel> todayTripLists = new ArrayList<>();
    protected boolean isInternetAvailable;

    @OnClick(R.id.back)
    public void backPressed() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_trips);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);

        isInternetAvailable = commonMethods.isOnline(this);


        /*
         *  Call driver trips history page
         */

        if (isInternetAvailable) {

            updateTripsApi();
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Upcoming(), getString(R.string.pending_trips));
        adapter.addFragment(new Past(), getString(R.string.completed_trips));
        viewPager.setAdapter(adapter);
        String laydir = getString(R.string.layout_direction);
        if ("1".equals(laydir))
            viewPager.setRotationY(180);
    }

    public void updateTripsApi() {

        commonMethods.showProgressDialog(this, customDialog);
        apiService.driverTripsHistory(getTripsHashMaps()).enqueue(new RequestCallback(this));

    }

    public HashMap<String, String> getTripsHashMaps() {
        HashMap<String, String> tripsHashMap = new HashMap<>();
        tripsHashMap.put("user_type", sessionManager.getType());
        tripsHashMap.put("token", sessionManager.getAccessToken());

        return tripsHashMap;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

            onSuccessLoadTrip(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    private void onSuccessLoadTrip(JsonResponse jsonResp) {
        YourTripModel tripResultModel = gson.fromJson(jsonResp.getStrResponse(), YourTripModel.class);
        if (tripResultModel != null) {
            loadPastData(tripResultModel, "past_trips");
            loadTodayData(tripResultModel, "today_trips");
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }


    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }


    private void loadPastData(YourTripModel tripResultModel, String type) {

        ArrayList<PastTripModel> yourTrip = tripResultModel.getPastTripsModel();
        if (yourTrip == null) {
            return;
        }

        for (int i = 0; i < yourTrip.size(); i++) {


            yourTrip.get(i).setType(type);

            String manualBookingUi = yourTrip.get(i).getBooking_type();
            String status = yourTrip.get(i).getStatus();

            if (manualBookingUi != null && manualBookingUi.equals(CommonKeys.RideBookedType.manualBooking) && status != null && status.equals(CommonKeys.TripStaus.Pending)) {
                yourTrip.get(i).setManualBookingUi(1);
            } else {
                yourTrip.get(i).setManualBookingUi(0);
            }


            String staticMapURL;
            if ((yourTrip.get(i).getStatus().equals("Completed")
                    || yourTrip.get(i).getStatus().equals("Rating")
                    || yourTrip.get(i).getStatus().equals("Payment")) && !yourTrip.get(i).getMapImage().equals("")) {
                staticMapURL = yourTrip.get(i).getMapImage();
            } else {
                LatLng pikcuplatlng = new LatLng(Double.valueOf(yourTrip.get(i).getPickupLatitude()), Double.valueOf(yourTrip.get(i).getPickupLongitude()));
                LatLng droplatlng = new LatLng(Double.valueOf(yourTrip.get(i).getDropLatitude()), Double.valueOf(yourTrip.get(i).getDropLongitude()));

                String pathString = "&path=color:0x000000ff%7Cweight:4%7Cenc:" + yourTrip.get(i).getTripPath();
                String pickupstr = pikcuplatlng.latitude + "," + pikcuplatlng.longitude;
                String dropstr = droplatlng.latitude + "," + droplatlng.longitude;
                String positionOnMap = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "pickup.png|" + pickupstr;
                String positionOnMap1 = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "drop.png|" + dropstr;
                https:
//maps.googleapis.com/maps/api/staticmap?size=640x250&28.535917,-81.382867,&path=color:0x000000ff|weight:4|enc:kldmDtavoNw@WiBm@kBUkCW


                if (yourTrip.get(i).getTripPath().equals("")) {
                    staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x250&" +
                            pikcuplatlng.latitude + "," + pikcuplatlng.longitude +
                            "" + positionOnMap + "" + positionOnMap1 + //"&zoom=14" +
                            "&key=" + sessionManager.getGoogleMapKey() + "&language=" + Locale.getDefault();
                } else {

                    staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x250&" +
                            pikcuplatlng.latitude + "," + pikcuplatlng.longitude +
                            pathString + "" + positionOnMap + "" + positionOnMap1 + //"&zoom=14" +
                            "&key=" + sessionManager.getGoogleMapKey() + "&language=" + Locale.getDefault();
                }


            }


            yourTrip.get(i).setMapPath(staticMapURL);

            pastTripLists.add(yourTrip.get(i));


        }

    }


    private void loadTodayData(YourTripModel tripResultModel, String type) {

        ArrayList<PastTripModel> yourTrip = tripResultModel.getTodayTrips();


        for (int i = 0; i < yourTrip.size(); i++) {


            yourTrip.get(i).setType(type);


            String manualBookingUi = yourTrip.get(i).getBooking_type();
            String status = yourTrip.get(i).getStatus();

            if (manualBookingUi != null && manualBookingUi.equals(CommonKeys.RideBookedType.manualBooking) && status != null && status.equals(CommonKeys.TripStaus.Pending)) {
                yourTrip.get(i).setManualBookingUi(1);
            } else {
                yourTrip.get(i).setManualBookingUi(0);
            }


            String staticMapURL;
            if ((yourTrip.get(i).getStatus().equals("Completed")
                    || yourTrip.get(i).getStatus().equals("Rating")
                    || yourTrip.get(i).getStatus().equals("Payment")) && !yourTrip.get(i).getMapImage().equals("")) {
                staticMapURL = yourTrip.get(i).getMapImage();
            } else {
                LatLng pikcuplatlng = new LatLng(Double.valueOf(yourTrip.get(i).getPickupLatitude()), Double.valueOf(yourTrip.get(i).getPickupLongitude()));
                LatLng droplatlng = new LatLng(Double.valueOf(yourTrip.get(i).getDropLatitude()), Double.valueOf(yourTrip.get(i).getDropLongitude()));

                String pathString = "&path=color:0x000000ff%7Cweight:4%7Cenc:" + yourTrip.get(i).getTripPath();
                String pickupstr = pikcuplatlng.latitude + "," + pikcuplatlng.longitude;
                String dropstr = droplatlng.latitude + "," + droplatlng.longitude;
                String positionOnMap = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "pickup.png|" + pickupstr;
                String positionOnMap1 = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "drop.png|" + dropstr;
                https:
//maps.googleapis.com/maps/api/staticmap?size=640x250&28.535917,-81.382867,&path=color:0x000000ff|weight:4|enc:kldmDtavoNw@WiBm@kBUkCW


                if (yourTrip.get(i).getTripPath().equals("")) {
                    staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x250&" +
                            pikcuplatlng.latitude + "," + pikcuplatlng.longitude +
                            "" + positionOnMap + "" + positionOnMap1 + //"&zoom=14" +
                            "&key=" + sessionManager.getGoogleMapKey() + "&language=" + Locale.getDefault();

                } else {

                    staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x250&" +
                            pikcuplatlng.latitude + "," + pikcuplatlng.longitude +
                            pathString + "" + positionOnMap + "" + positionOnMap1 + //"&zoom=14" +
                            "&key=" + sessionManager.getGoogleMapKey() + "&language=" + Locale.getDefault();

                }


            }

            yourTrip.get(i).setMapPath(staticMapURL);
            todayTripLists.add(yourTrip.get(i));
        }

    }


    /*
     *  Common loader initialize
     */
    public List<PastTripModel> getTodayTripDetailsList() {
        return todayTripLists;
    }

    public List<PastTripModel> getPastTripDetailsList() {
        return pastTripLists;
    }


    public LatLng getCenterCoordinate(LatLng pickup, LatLng drop) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickup);
        builder.include(drop);
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }
}
