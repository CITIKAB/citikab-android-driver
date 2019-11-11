package com.trioangle.goferdriver.rating;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category Comments
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.RiderFeedBackModel;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/* ************************************************************
                Comment
Its used to view the comments with rider screen page function
*************************************************************** */
public class Comments extends Fragment implements ServiceListener {


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

    public @InjectView(R.id.my_recycler_view2)
    RecyclerView recyclerView;
    public @InjectView(R.id.norating)
    TextView Def_rating_text;

    protected boolean isInternetAvailable;
    private ArrayList<HashMap<String, String>> feedbackarraylist = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_comments, container, false);

        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this, rootView);

        dialog = commonMethods.getAlertDialog(getContext());

        isInternetAvailable = commonMethods.isOnline(getContext());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                CommonMethods.DebuggableLogI("Character sequence ", " Checkins");

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                CommonMethods.DebuggableLogI("Character sequence ", " Checkins");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                CommonMethods.DebuggableLogI("Character sequence ", " Checkins");
            }
        });


        if (isInternetAvailable) {
            updateUserComments();
        } else {
            dialogfunction();
        }
        return rootView;

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

            onSuccessComments(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            commonMethods.showMessage(getContext(), dialog, jsonResp.getStatusMsg());


        }
    }

    /**
     * success response for comments
     *
     * @param jsonResp
     */

    private void onSuccessComments(JsonResponse jsonResp) {
        RiderFeedBackModel riderFeedBackModel = gson.fromJson(jsonResp.getStrResponse(), RiderFeedBackModel.class);
        if (riderFeedBackModel != null) {
            if (riderFeedBackModel.getRiderFeedBack().size() >= 1) {
                Def_rating_text.setText(getString(R.string.ratingsncomment));
                Def_rating_text.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {

                Def_rating_text.setText(getString(R.string.noratings));
                Def_rating_text.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            for (int i = 0; i < riderFeedBackModel.getRiderFeedBack().size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();

                String date = riderFeedBackModel.getRiderFeedBack().get(i).getDate();
                String rating = riderFeedBackModel.getRiderFeedBack().get(i).getRiderRating();
                String rating_comments = riderFeedBackModel.getRiderFeedBack().get(i).getRiderComments();
                String user_id = riderFeedBackModel.getRiderFeedBack().get(i).getTripId();

                map.put("date", date);
                map.put("rating", rating);
                map.put("rating_comments", rating_comments);
                map.put("user_id", user_id);
                feedbackarraylist.add(map);

            }


            RecyclerView.Adapter adapter = new CommentsRecycleAdapter(feedbackarraylist);
            recyclerView.setAdapter(adapter);

        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

    }

    /**
     * User Comments Api Call
     */

    public void updateUserComments() {

        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.updateRiderFeedBack(getUserComments()).enqueue(new RequestCallback(this));

    }


    /**
     * Hash map for user comments api
     *
     * @return
     */

    public HashMap<String, String> getUserComments() {
        HashMap<String, String> userRatingHashMap = new HashMap<>();
        userRatingHashMap.put("user_type", sessionManager.getType());
        userRatingHashMap.put("token", sessionManager.getAccessToken());

        return userRatingHashMap;
    }


    /*
   * Show internet not available
   */
    public void dialogfunction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
