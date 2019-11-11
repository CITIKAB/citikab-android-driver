package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category DailyEarningDetails
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                DailyEarningDetails
Its used to view the list dailyearningdetails
*************************************************************** */
public class DailyEarningDetails extends AppCompatActivity {

    public @InjectView(R.id.tripearning_list)
    RecyclerView listView;
    public TripEarnListAdapter adapter;
    public String Paydate[] = {"5.36 AM", "6.10 AM", "7.05 AM", "9.00AM", "1.00PM", "4.35PM", "6.00PM"};
    public String Payamount[] = {"$92.88", "$82.88", "$72.88", "$9002.88", "$902.88", "$92.88", "$892.88"};
    private List<PayModel> searchlist;

    @OnClick(R.id.back)
    public void onBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_earning_details);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


        searchlist = new ArrayList<>();
        adapter = new TripEarnListAdapter(searchlist);

                /*
                *  Driver earning page list
                */
        listView.setHasFixedSize(false);
        listView.setNestedScrollingEnabled(true);
        listView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter);

                /*
                * Driver earning details data
                */
        loadData();

        listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //countries.get(position)
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    Intent paystatement_details = new Intent(getApplicationContext(), TripEarningDetails.class);
                    startActivity(paystatement_details);
                    overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {


            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    /*
   *  Driver earning data
   */
    public void loadData() {
        for (int i = 0; i < Paydate.length; i++) {
            PayModel listdata = new PayModel();
            listdata.setDailyTrip(Paydate[i]);
            listdata.setTripAmount(Payamount[i]);
            searchlist.add(listdata);
            CommonMethods.DebuggableLogD("search list.length", String.valueOf(searchlist.size()));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

}
