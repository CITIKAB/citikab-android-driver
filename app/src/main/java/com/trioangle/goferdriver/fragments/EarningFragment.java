/*
 * Copyright (c) 2017. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.trioangle.goferdriver.fragments;
/**
 * @package com.trioangle.goferdriver.fragments
 * @subpackage fragments
 * @category EarningFragment
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.EarningModel;
import com.trioangle.goferdriver.earning.BarView;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.paymentstatement.PaymentStatementActivity;
import com.trioangle.goferdriver.tripsdetails.YourTrips;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.MainActivity.selectedFrag;

/* ************************************************************
                      EarningFragment
Its used get home screen earning fragment details
*************************************************************** */
public class EarningFragment extends Fragment implements ServiceListener {


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

    public @InjectView(R.id.bar_view)
    BarView barView;
    public @InjectView(R.id.triphistorylayout)
    RelativeLayout triphistorylayout;
    public @InjectView(R.id.paystatementlayout)
    RelativeLayout paystatementlayout;
    public @InjectView(R.id.horizontalScrollView)
    RelativeLayout horizontalScrollView;
    public @InjectView(R.id.weekly_fare)
    TextView weekly_fare;
    public @InjectView(R.id.last_trip_amount)
    TextView last_trip_amount;
    public @InjectView(R.id.most_resent_payout)
    TextView most_resent_payout;
    public @InjectView(R.id.show_date)
    TextView show_date;
    public @InjectView(R.id.value_mid)
    TextView value_mid;
    public @InjectView(R.id.value_bottom)
    TextView value_bottom;
    public @InjectView(R.id.value_top)
    TextView value_top;
    public @InjectView(R.id.chat_empty)
    TextView chat_empty;
    public @InjectView(R.id.before_search)
    TextView before_search;
    public @InjectView(R.id.next_search)
    TextView next_search;
    public @InjectView(R.id.tv_total_pay)
    TextView tvTotalPay;


    public String pagenumber;
    public String current_date;
    public ArrayList<Integer> farelist = new ArrayList<Integer>();
    public String[] day;
    public String dates[];
    public String[] days = new String[7];
    public double fare[];
    public double max;
    public String currency_code;
    public String currency_symbol;
    public String last_trip;
    public String recent_payout;
    public String total_week_amount;
    public Calendar now;
    public String currencysymbol;
    public Context mContext;
    protected boolean isInternetAvailable;

    /*
     *   Fragment to show driver weekly earning
     **/
    public static EarningFragment newInstance() {
        EarningFragment fragment = new EarningFragment();
        selectedFrag=2;
        return fragment;
    }

    @OnClick(R.id.paystatementlayout)
    public void paystatementLayout() {
        Intent signin = new Intent(mContext, PaymentStatementActivity.class);
        startActivity(signin);
        getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.triphistorylayout)
    public void tripHistoryLayout() {
        Intent signin = new Intent(mContext, YourTrips.class);
        startActivity(signin);
        getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.next_search)
    public void nextSearch() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(days[days.length - 1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        now.setTime(date);
        now.add(Calendar.DATE, 1);
        String nextday = dateFormat.format(now.getTime());


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }



        if (isInternetAvailable) {

            updateEarningChart();
        } else {
            commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.go_online));
        }
    }

    @OnClick(R.id.before_search)
    public void beforeSearch() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(days[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        now.setTime(date);
        now.add(Calendar.DATE, -2);
        String yesterdayAsString = dateFormat.format(now.getTime());


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }


        if (isInternetAvailable) {

            updateEarningChart();

        } else {

            commonMethods.showMessage(mContext, dialog, getResources().getString(R.string.go_online));
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);
        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this, view);
        isInternetAvailable = commonMethods.isOnline(getContext());

        now = Calendar.getInstance();

        pagenumber = "2";

        currencysymbol = sessionManager.getCurrencySymbol();

        if (sessionManager.getUserType()!=null&&!TextUtils.isEmpty(sessionManager.getUserType())&&!sessionManager.getUserType().equalsIgnoreCase("0")&&!sessionManager.getUserType().equalsIgnoreCase("1")){
            tvTotalPay.setText(getResources().getString(R.string.total_trip_amount));
        }

        final RelativeLayout linearLayout = (RelativeLayout) view.findViewById(R.id.horizontalScrollView);
        ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);


            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            now.add(Calendar.DATE, -2);
        }

        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());

            now.add(Calendar.DAY_OF_MONTH, 1);
        }


        current_date = days[0];

        if (isInternetAvailable) {

            updateEarningChart();

        } else {

            dialogfunction();

        }
        return view;

    }

    public HashMap<String, String> getEarningsChart() {
        HashMap<String, String> locationHashMap = new HashMap<>();
        locationHashMap.put("user_type", sessionManager.getType());
        locationHashMap.put("start_date", days[0]);
        locationHashMap.put("end_date", days[days.length - 1]);
        locationHashMap.put("token", sessionManager.getAccessToken());

        return locationHashMap;
    }

    public void updateEarningChart() {

        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.updateEarningChart(getEarningsChart()).enqueue(new RequestCallback(this));

    }

    /*
    *  Set bar chart days
    **/
    private void randomSet(BarView barView) {
        ArrayList<String> test = new ArrayList<String>();
        for (int i = 0; i < 1; i++) {

            test.add("M");
            test.add("TU");
            test.add("W");
            test.add("TH");
            test.add("F");
            test.add("SA");
            test.add("SU");

            if (getResources().getString(R.string.layout_direction).equals("1"))
            Collections.reverse(test);

        }
        barView.setBottomTextList(test);
        barView.setDataList(farelist, (int) max);
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(mContext, dialog, data);
            return;
        }

        if (jsonResp.isSuccess()) {
            try {
                if (isAdded()){
                    onSuccessChartEarning(jsonResp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            chat_empty.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.INVISIBLE);
            commonMethods.showMessage(mContext, dialog, jsonResp.getStatusMsg());
            commonMethods.hideProgressDialog();

        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        chat_empty.setVisibility(View.VISIBLE);
        horizontalScrollView.setVisibility(View.INVISIBLE);
    }


    private void onSuccessChartEarning(JsonResponse jsonResp) throws JSONException {

        EarningModel earningModel = gson.fromJson(jsonResp.getStrResponse(), EarningModel.class);
        if (earningModel != null) {


            last_trip = earningModel.getLastTrip();

            try{
                DecimalFormat df = new DecimalFormat("0.00");
                last_trip=String.valueOf(df.format(Float.valueOf(last_trip)));
            }catch (Exception e){
                last_trip = "0.00";
            }

            currency_code = earningModel.getCurrencyCode();
            currency_symbol = earningModel.getCurrencySymbol();
            recent_payout = earningModel.getRecentPayout();
            total_week_amount = earningModel.getTotalWeekAmount();
            currency_symbol = Html.fromHtml(currency_symbol).toString();
            sessionManager.setCurrencyCode(currency_code);
            sessionManager.setCurrencySymbol(currency_symbol);
            if (Float.valueOf(earningModel.getTotalWeekAmount().replaceAll(",", "")) > 0) {

                chat_empty.setVisibility(View.INVISIBLE);
                horizontalScrollView.setVisibility(View.VISIBLE);
                String tripDetails = gson.toJson(earningModel.getTripDetails());

                JSONArray trip_details = new JSONArray(tripDetails);
                day = new String[trip_details.length()];
                dates = new String[trip_details.length()];
                fare = new double[trip_details.length()];

                farelist.clear();


                for (int i = 0; i < trip_details.length(); i++) {
                    JSONObject trip_obj = (JSONObject) trip_details.get(i);
                    day[i] = trip_obj.getString("day");
                    dates[i] = trip_obj.getString("created_at");
                    fare[i] = Double.valueOf(trip_obj.getString("daily_fare").replaceAll(",", ""));
                    farelist.add((int) Math.round(Double.valueOf(trip_obj.getString("daily_fare").replaceAll(",", ""))));
                }
                if (getResources().getString(R.string.layout_direction).equals("1")) {
                    Collections.reverse(farelist);
                }
                max = fare[0];

                for (int i = 1; i < fare.length; i++) {
                    if (fare[i] > max) {
                        max = fare[i];
                    }
                }

                int high = (int) ((max / 10) + max);
                int mid = high / 2;
                int bottom = mid / 2;
                value_mid.setText(currencysymbol + String.valueOf(mid));
                value_bottom.setText(currencysymbol + String.valueOf(bottom));
                value_top.setText(currencysymbol + String.valueOf(high));

                randomSet(barView);
                                   /* String laydir = getString(R.string.layout_direction);
                                    if (laydir.equals("1")){
                                        barView.setRotationX(180);
                                        barView.setRotationY(180);
                                    }*/
            } else {
                chat_empty.setVisibility(View.VISIBLE);
                horizontalScrollView.setVisibility(View.INVISIBLE);
            }

            weekly_fare.setText(currencysymbol + "" + total_week_amount);
            if (mContext != null) {
                last_trip_amount.setText(mContext.getResources().getString(R.string.last_trip) + "" + currencysymbol + "" + last_trip);
                most_resent_payout.setText(mContext.getResources().getString(R.string.most_recent) + "" + currencysymbol + "" + recent_payout);
            }


            if (days[0].equals(current_date)) {
                show_date.setText(mContext.getResources().getString(R.string.thisweek));
                next_search.setVisibility(View.GONE);
            } else {
                Date date = null;
                Date date1 = null;
                String startdate = "";
                String endate = "";
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                DateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                try {
                    date = originalFormat.parse(days[0]);
                    date1 = originalFormat.parse(days[days.length - 1]);
                    startdate = targetFormat.format(date);
                    endate = targetFormat.format(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                show_date.setText(startdate + " - " + endate);
                next_search.setVisibility(View.VISIBLE);
            }


            commonMethods.hideProgressDialog();


        }

    }


    /*
     *  Show dialog for no internet connection
     **/
    public void dialogfunction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.turnoninternet))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.setCancelable(true);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
