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
 * @category RatingFragment
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.RatingModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.rating.RiderFeedBack;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.MainActivity.selectedFrag;

/* ************************************************************
                      RatingFragment
Its used get home screen rating fragment details
*************************************************************** */
public class RatingFragment extends Fragment implements ServiceListener {

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

    public @InjectView(R.id.feedbackhistorylayout)
    RelativeLayout feedbackhistorylayout;
    public @InjectView(R.id.rating_lay)
    RelativeLayout rating_lay;
    public @InjectView(R.id.lifetime)
    TextView lifetime;
    public @InjectView(R.id.ratingtrips)
    TextView ratingtrips;
    public @InjectView(R.id.fivestar)
    TextView fivestar;
    public @InjectView(R.id.textView2)
    TextView textView2;
    public @InjectView(R.id.tv_rating_content)
    TextView tvRatingContent;
    public Context mContext;
    protected boolean isInternetAvailable;

    public static RatingFragment newInstance() {
        RatingFragment fragment = new RatingFragment();
        selectedFrag=3;
        return fragment;
    }

    @OnClick(R.id.feedbackhistorylayout)
    public void feedbackHistoryLayout() {
        Intent intent = new Intent(mContext, RiderFeedBack.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        /*
         *  Common loader and internet check
         **/

        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this, view);
        dialog = commonMethods.getAlertDialog(mContext);
        isInternetAvailable = commonMethods.isOnline(getContext());
        textView2.setVisibility(View.GONE);
        tvRatingContent.setVisibility(View.GONE);

        if (isInternetAvailable) {
        /*
         *  Get driver rating and feed back details API
         **/
            updateEarningChart();

        } else {
            dialogfunction();
        }
        return view;
    }

    public void updateEarningChart() {

        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.updateDriverRating(getUserRating()).enqueue(new RequestCallback(this));

    }

    public HashMap<String, String> getUserRating() {
        HashMap<String, String> userRatingHashMap = new HashMap<>();
        userRatingHashMap.put("user_type", sessionManager.getType());
        userRatingHashMap.put("token", sessionManager.getAccessToken());

        return userRatingHashMap;
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

            onSuccessRating(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            commonMethods.showMessage(mContext, dialog, jsonResp.getStatusMsg());

        }
    }

    private void onSuccessRating(JsonResponse jsonResp) {

        RatingModel ratingModel = gson.fromJson(jsonResp.getStrResponse(), RatingModel.class);
        if (ratingModel != null) {
            String total_rating = ratingModel.getTotalRating();
            String total_rating_count = ratingModel.getTotalRatingCount();
            String five_rating_count = ratingModel.getFiveRatingCount();
            String driver_rating = ratingModel.getDriverRating();

            lifetime.setText(total_rating_count);
            ratingtrips.setText(total_rating);
            fivestar.setText(five_rating_count);

            if (driver_rating.equalsIgnoreCase("0.00")||driver_rating.equalsIgnoreCase("0")) {
                tvRatingContent.setVisibility(View.GONE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(getResources().getString(R.string.no_ratings_display));
                textView2.setTextSize(20);
                textView2.setCompoundDrawablesRelative(null,null,null,null);
            }else {
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(driver_rating);
                tvRatingContent.setVisibility(View.VISIBLE);
            }


        }


    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

    }


    /*
     *  show dialog for no internet available
     */
    public void dialogfunction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.turnoninternet))
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
