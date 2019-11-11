package com.trioangle.goferdriver.tripsdetails;
/**
 * @package com.trioangle.goferdriver.tripsdetails
 * @subpackage tripsdetails model
 * @category TripsDetails
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.InvoiceModel;
import com.trioangle.goferdriver.datamodel.PastTripModel;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.rating.PriceRecycleAdapter;
import com.trioangle.goferdriver.rating.Riderrating;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                TripsDetails
Its used to show all the trips details information to view the page
*************************************************************** */
public class TripDetails extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;

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
    public @InjectView(R.id.cashcollectamount)
    TextView cashcollectamount;
    public @InjectView(R.id.cashcollectamount_txt)
    TextView cashcollectamount_txt;
    public @InjectView(R.id.oweamount)
    TextView oweamount;
    public @InjectView(R.id.driverpayout)
    TextView driverpayout;
    public @InjectView(R.id.adminamount)
    TextView adminamount;
    public @InjectView(R.id.trip_amount)
    TextView trip_amount;
    public @InjectView(R.id.trip_km)
    TextView trip_km;
    public @InjectView(R.id.trip_duration)
    TextView trip_duration;
    public @InjectView(R.id.drop_address)
    TextView drop_address;
    public @InjectView(R.id.pickup_address)
    TextView pickup_address;
    public @InjectView(R.id.trip_date)
    TextView trip_date;
    public @InjectView(R.id.route_image)
    ImageView route_image;
    public String payment_method;
    public int position;
    public String currencysymbol;
    public @InjectView(R.id.rvPrice)
    RecyclerView recyclerView;

    @OnClick(R.id.back)
    public void backPressed() {
        onBackPressed();
    }
    ArrayList<InvoiceModel> invoiceModels = new ArrayList<>();

    public @InjectView(R.id.btnrate)
    Button btnrate;
    @OnClick(R.id.btnrate)
    public void rate() {
        sessionManager.setTripId(list.get(position).getId());
        Intent rating = new Intent(this, Riderrating.class);
        rating.putExtra("imgprofile", list.get(position).getRiderImage());
        rating.putExtra("back", 1);
        startActivity(rating);
    }

    List<PastTripModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this);

        currencysymbol = sessionManager.getCurrencySymbol();
        Intent intent = getIntent();
        position = intent.getIntExtra("postion", 0);
        list = (List<PastTripModel>) intent
                .getSerializableExtra("list");
        Bundle bundle = intent.getExtras();
        invoiceModels.addAll((ArrayList<InvoiceModel>) bundle.getSerializable("invoiceModels"));
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new PriceRecycleAdapter(this,invoiceModels);
        recyclerView.setAdapter(adapter);
        trip_km.setText(list.get(position).getTotalKm() + " KM");
        trip_duration.setText(list.get(position).getTotalTime() + " Mins");
        pickup_address.setText(list.get(position).getPickupLocation());
        drop_address.setText(list.get(position).getDropLocation());
        //trip_amount.setText(sessionManager.getCurrencySymbol() + list.get(position).getDriverPayout());

        if (sessionManager.getUserType()!=null&&!TextUtils.isEmpty(sessionManager.getUserType())&&!sessionManager.getUserType().equalsIgnoreCase("0")&&!sessionManager.getUserType().equalsIgnoreCase("1")){
            // Company
            trip_amount.setText(/*sessionManager.getCurrencySymbol() +*/ Html.fromHtml(list.get(position).getSubTotalFare()).toString());
        }else{
            // Normal Driver
            trip_amount.setText(sessionManager.getCurrencySymbol() + list.get(position).getDriverPayout());
        }

        if (list.get(position).getStatus().equalsIgnoreCase("Rating")){
            btnrate.setVisibility(View.VISIBLE);
        }else {
            btnrate.setVisibility(View.GONE);
        }
        /*
        drop_address.setText(list.get(position).getDropLocation());
        pickup_address.setText(list.get(position).getPickupLocation());*///397.44

        String total_fare = "";
        String access_fee = "";
        String promo_amount = "";
        String base_fare = "";
        String total_km_fare = "";
        String total_time_fare = "";
        String driver_payout = "";
        String wallet_amount = "";
        String applied_owe_amount = "";
        String admin_amount = "";
        String driver_total_fare = "";

        /*total_fare = list.get(position).getTotalFare();
        access_fee = list.get(position).getAccessFee();
        base_fare = list.get(position).getBaseFare();
        total_km_fare = list.get(position).getDistanceFare();
        total_time_fare = list.get(position).getTimeFare();
        driver_payout = list.get(position).getDriverPayout();*/
        //payment_method = list.get(position).getPaymentMethod();

       //
//        driver_total_fare = String.format(Locale.US, "%.2f", Float.valueOf(total_fare));

        //driverpayoutlayout.setVisibility(View.GONE);

        //applied_owe_amount = list.get(position).getAppliedOweAmount();

        // if applied owe amount is 0 then owe amount field is hide
       /* if (Float.valueOf(applied_owe_amount) <= 0)
            oweamountlayout.setVisibility(View.GONE);*/

     /*   wallet_amount = list.get(position).getWalletAmount();
        admin_amount = list.get(position).getOweAmount();
        promo_amount = list.get(position).getPromoAmount();*/

        // If admin amount is zero then admin amount field is hide
       /* if (Float.valueOf(admin_amount) <= 0)
            adminamountlayout.setVisibility(View.GONE);

        // Check owe amount and admin amount is zero or wallet amount and promo amount is greater then zero
        if ((Float.valueOf(applied_owe_amount) <= 0 && Float.valueOf(admin_amount) <= 0) || (Float.valueOf(wallet_amount) > 0 || Float.valueOf(promo_amount) > 0)) {
            // Check promo amount or wallet amount greater then zero
            if (Float.valueOf(wallet_amount) > 0 || Float.valueOf(promo_amount) > 0) {
                // Check payment method is cash or not
                if (payment_method.contains("Cash")) {
                    Float checkamount = Float.valueOf(wallet_amount) + Float.valueOf(promo_amount);
                    // Check amount for to show or hide driver payout field
                    if (checkamount < Float.valueOf(access_fee) || checkamount < Float.valueOf(admin_amount))
                        driverpayoutlayout.setVisibility(View.GONE);
                    else
                        driverpayoutlayout.setVisibility(View.VISIBLE);
                    Float dpf = (Float.valueOf(wallet_amount) + Float.valueOf(promo_amount)) - (Float.valueOf(access_fee) + Float.valueOf(applied_owe_amount));

                    String dp = String.format(Locale.US, "%.2f", Float.valueOf(dpf));
                    dp = dp.replaceAll(",", ".");
                    driverpayout.setText(sessionManager.getCurrencySymbol() + dp);
                    driver_payout = String.format(Locale.US, "%.2f", Float.valueOf(total_fare) - (Float.valueOf(wallet_amount) + Float.valueOf(promo_amount)));
                } else {
                    if (Float.valueOf(applied_owe_amount) > 0) {
                        driverpayoutlayout.setVisibility(View.GONE);
                    } else {
                        driverpayoutlayout.setVisibility(View.GONE);
                        cashcollectamountlayout.setVisibility(View.GONE);
                        total_payouts.setText(getResources().getString(R.string.totalpayout));
                    }
                }
            } else {
                if (Float.valueOf(applied_owe_amount) > 0) {
                    driverpayoutlayout.setVisibility(View.GONE);
                } else {
                    driverpayoutlayout.setVisibility(View.GONE);
                    cashcollectamountlayout.setVisibility(View.GONE);
                    total_payouts.setText(getResources().getString(R.string.totalpayout));
                }
            }
        }*/


       /* cashcollectamount_txt.setText(getResources().getString(R.string.totalpayout));
        cashcollectamount_txt.setTextColor(getResources().getColor(R.color.textblack));
        cashcollectamount.setTextColor(getResources().getColor(R.color.textblack));*/

        /*
         *  Show amount data for corresponding fields
         */
        //driver_total_fare = driver_total_fare.replaceAll(",", ".");
       /* if (Float.valueOf(list.get(position).getDriverPayout()) > 0)
            trip_amount.setText(sessionManager.getCurrencySymbol() + list.get(position).getDriverPayout());
        else
            trip_amount.setText(sessionManager.getCurrencySymbol() + driver_total_fare);

        basefare_amount.setText(sessionManager.getCurrencySymbol() + base_fare);
        distance_fare.setText(sessionManager.getCurrencySymbol() + total_km_fare);
        time_fare.setText(sessionManager.getCurrencySymbol() + total_time_fare);
        fee.setText("-" + sessionManager.getCurrencySymbol() + access_fee);

        totalamount.setText(sessionManager.getCurrencySymbol() + driver_total_fare);

        adminamount.setText(sessionManager.getCurrencySymbol() + admin_amount);
        oweamount.setText("- " + sessionManager.getCurrencySymbol() + applied_owe_amount);
        driver_payout = driver_payout.replaceAll(",", ".");
        cashcollectamount.setText(sessionManager.getCurrencySymbol() + driver_payout);*/



        String startdate = "";
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        try {
            Date date = originalFormat.parse(list.get(position).getCreatedAt());
            startdate = targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        trip_date.setText(startdate);

        Picasso.with(getApplicationContext()).load(list.get(position).getMapPath())
                .into(route_image);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

}
