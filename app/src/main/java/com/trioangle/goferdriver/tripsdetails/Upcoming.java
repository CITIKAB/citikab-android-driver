package com.trioangle.goferdriver.tripsdetails;
/**
 * @package com.trioangle.goferdriver.tripsdetails
 * @subpackage tripsdetails model
 * @category Upcoming
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.InvoiceModel;
import com.trioangle.goferdriver.datamodel.PastTripModel;
import com.trioangle.goferdriver.datamodel.PaymentDetails;
import com.trioangle.goferdriver.datamodel.RiderDetailsModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.home.RequestAcceptActivity;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.rating.PaymentAmountPage;
import com.trioangle.goferdriver.rating.Riderrating;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/* ************************************************************
                Upcoming
Its used to show all the current trips details view page
*************************************************************** */
public class Upcoming extends Fragment implements ServiceListener,RecyclerView.OnItemTouchListener {

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
    @InjectView(R.id.listempty)
    public TextView emptylist;
    @InjectView(R.id.current_recycler_view)
    public RecyclerView recyclerView;
    protected boolean isInternetAvailable;
    private String tripStatus;
    private List<PastTripModel> countries;
    private TripsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_upcoming, container, false);

        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this, rootView);
        dialog = commonMethods.getAlertDialog(getContext());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        String laydir = getString(R.string.layout_direction);
        if ("1".equals(laydir))
            recyclerView.setRotationY(180);

        countries = new ArrayList<>();
        YourTrips activity = (YourTrips) getActivity();
        countries = activity.getTodayTripDetailsList();

        // Check data is zero then show empty list else show the list
        if (countries.size() <= 0) {
            emptylist.setVisibility(View.VISIBLE);
        } else {
            emptylist.setVisibility(View.GONE);
        }


        adapter = new TripsAdapter(countries, getContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemRatingClickListner(new TripsAdapter.onItemRatingClickListner() {
            @Override
            public void setRatingClick(int position) {
                tripStatus = countries.get(position).getStatus();
                ArrayList<InvoiceModel> invoiceModels = countries.get(position).getInvoice();
                // Check trip status for completed or cancelled to open the trips details page
                if ("Completed".equals(tripStatus) || "Cancelled".equals(tripStatus)|| "Rating".equals(tripStatus)) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("invoiceModels", invoiceModels);
                    Intent intent = new Intent(getActivity(), TripDetails.class);
                    intent.putExtra("postion", position);
                    intent.putExtra("list", (Serializable) countries);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                } else {
                    // Get driver incomplete details API
                    isInternetAvailable = commonMethods.isOnline(getContext());
                    if (!isInternetAvailable) {
                        commonMethods.showMessage(getContext(), dialog, getResources().getString(R.string.no_connection));
                    } else {
                        sessionManager.setTripId(countries.get(position).getId());
                        sessionManager.setDriverAndRiderAbleToChat(true);
                        CommonMethods.startFirebaseChatListenerService(getActivity());
                        getRiderApi();
                    }
                }
            }
        });

        recyclerView.addOnItemTouchListener(this);


        return rootView;
    }
    GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

    });

    public HashMap<String, String> getRiderDetails() {
        HashMap<String, String> locationHashMap = new HashMap<>();
        locationHashMap.put("trip_id", sessionManager.getTripId());
        locationHashMap.put("token", sessionManager.getAccessToken());

        return locationHashMap;
    }






    public void getRiderApi() {
        //commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.getRiderDetails(getRiderDetails()).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(getContext(), dialog, data);
            return;
        }

        if (jsonResp.isSuccess()) {

            onSuccessRiderProfile(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            commonMethods.showMessage(getContext(), dialog, jsonResp.getStatusMsg());

        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

    }


    private void onSuccessRiderProfile(JsonResponse jsonResp) {
        commonMethods.hideProgressDialog();
        RiderDetailsModel earningModel = gson.fromJson(jsonResp.getStrResponse(), RiderDetailsModel.class);
        ArrayList<InvoiceModel> invoiceModels = earningModel.getInvoice();

        PaymentDetails paymentDetails = earningModel.getPaymentDetails();
        sessionManager.setPaymentMethod(paymentDetails.getPaymentMethod());
        //acceptedDriverDetails =new AcceptedDriverDetails(ridername, mobilenumber, profileimg, ratingvalue, cartype, pickuplocation, droplocation, pickuplatitude, droplatitude, droplongitude,pickuplongitude);

        // Pass different data based on trip status
        if ("Scheduled".equals(tripStatus) || "Begin trip".equals(tripStatus) || "End trip".equals(tripStatus)) {
            Intent requstreceivepage = new Intent(getActivity(), RequestAcceptActivity.class);
            requstreceivepage.putExtra("riderDetails", earningModel);
            if ("Scheduled".equals(tripStatus)) {
                //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
                sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
                sessionManager.setSubTripStatus(getResources().getString(R.string.confirm_arrived));
                requstreceivepage.putExtra("isTripBegin", false);
                requstreceivepage.putExtra("tripstatus", getResources().getString(R.string.confirm_arrived));
            } else if ("Begin trip".equals(tripStatus)) {
                //sessionManager.setTripStatus("CONFIRM YOU'VE ARRIVED");
                sessionManager.setTripStatus(CommonKeys.TripStaus.ConfirmArrived);
                sessionManager.setSubTripStatus(getResources().getString(R.string.begin_trip));
                requstreceivepage.putExtra("isTripBegin", false);
                requstreceivepage.putExtra("tripstatus", getResources().getString(R.string.begin_trip));
            } else if ("End trip".equals(tripStatus)) {
                //sessionManager.setTripStatus("Begin Trip");
                sessionManager.setTripStatus(CommonKeys.TripStaus.BeginTrip);
                sessionManager.setSubTripStatus(getResources().getString(R.string.end_trip));
                requstreceivepage.putExtra("isTripBegin", true);
                requstreceivepage.putExtra("tripstatus", getResources().getString(R.string.end_trip));
            }
            startActivity(requstreceivepage);
        } else if ("Rating".equals(tripStatus)) {
            //sessionManager.setTripStatus("End Trip");
            sessionManager.setTripStatus(CommonKeys.TripStaus.EndTrip);
            Intent rating = new Intent(getActivity(), Riderrating.class);
            rating.putExtra("imgprofile", earningModel.getRiderThumbImage());
            rating.putExtra("back", 1);
            startActivity(rating);
        } else if ("Payment".equals(tripStatus)) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("invoiceModels", invoiceModels);
            Intent main = new Intent(getActivity(), PaymentAmountPage.class);
            main.putExtra("AmountDetails", jsonResp.getStrResponse().toString());
            main.putExtras(bundle);
            startActivity(main);

        }
        getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        recyclerView.addOnItemTouchListener(this);


    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        //View child = rv.findChildViewUnder(e.getX(), e.getY());
       /* if (child != null && gestureDetector.onTouchEvent(e)) {

            int position = rv.getChildAdapterPosition(child);

            tripStatus = countries.get(position).getStatus();
            ArrayList<InvoiceModel> invoiceModels = countries.get(position).getInvoice();
            // Check trip status for completed or cancelled to open the trips details page
            if ("Completed".equals(tripStatus) || "Cancelled".equals(tripStatus)|| "Rating".equals(tripStatus)) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("invoiceModels", invoiceModels);
                Intent intent = new Intent(getActivity(), TripDetails.class);
                intent.putExtra("postion", position);
                intent.putExtra("list", (Serializable) countries);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            } else {
                // Get driver incomplete details API
                isInternetAvailable = commonMethods.isOnline(getContext());
                if (!isInternetAvailable) {
                    commonMethods.showMessage(getContext(), dialog, getResources().getString(R.string.no_connection));
                } else {
                    sessionManager.setTripId(countries.get(position).getId());
                    recyclerView.removeOnItemTouchListener(this);
                    sessionManager.setDriverAndRiderAbleToChat(true);
                    CommonMethods.startFirebaseChatListenerService(getActivity());
                    getRiderApi();
                }
            }
        }*/
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
