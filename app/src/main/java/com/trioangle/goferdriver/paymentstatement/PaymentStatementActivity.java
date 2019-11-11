package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category PaymentStatementActivity
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

import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogD;

/* ************************************************************
                PaymentStatementActivity
Its used to view the payment statement all the details
*************************************************************** */
public class PaymentStatementActivity extends AppCompatActivity {

    public @InjectView(R.id.paystatementlist)
    RecyclerView listView;

    private List<PayModel> searchlist;
    private PayAdapter adapter;
    private String Paydate[] = {"27 Feb - 13 Mar", "20 - 27 Feb", "20 - 27 Mar", "20 - 27 Apr", "20 - 27 May", "20 - 27 Jun", "20 - 27 Jul", "20 - 27 Aug", "20 - 27 Sep", "20 - 27 Oct"};
    private String Payamount[] = {"$92.88", "$82.88", "$72.88", "$9002.88", "$902.88", "$92.88", "$892.88", "$8892.88", "$45492.88", "$1292.88"};

    @OnClick(R.id.pay_back_lay)
    public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.pay_back)
    public void onpBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_statement);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


        listView = (RecyclerView) findViewById(R.id.paystatementlist);
        searchlist = new ArrayList<>();
        adapter = new PayAdapter(searchlist);

                /*
                *  Driver payment list
                */
        listView.setHasFixedSize(false);
        listView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter);
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
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    Intent paystatement_details = new Intent(getApplicationContext(), PayStatementDetails.class);
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

    public void loadData() {
        for (int i = 0; i < Paydate.length; i++) {
            PayModel listdata = new PayModel();
            listdata.setTripDateTime(Paydate[i]);
            listdata.setTripAmount(Payamount[i]);
            searchlist.add(listdata);
            DebuggableLogD("search list.length", String.valueOf(searchlist.size()));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }
}
