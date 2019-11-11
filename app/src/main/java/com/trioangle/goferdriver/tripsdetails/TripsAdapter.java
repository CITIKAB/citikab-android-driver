package com.trioangle.goferdriver.tripsdetails;
/**
 * @package com.trioangle.goferdriver.tripsdetails
 * @subpackage tripsdetails model
 * @category TripsAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.PastTripModel;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.rating.Riderrating;

import java.util.List;

import javax.inject.Inject;

/* ************************************************************
                TripsAdapter
Its used to show all the trips details view page
*************************************************************** */
public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    public @Inject
    SessionManager sessionManager;
    private List<PastTripModel> tripdetailarraylist;
    private Context context;

    public int MANUAL = 1;
    public int OTHER = 0;
    public onItemRatingClickListner onItemRatingClickListner;


    public TripsAdapter(List<PastTripModel> tripdetailarraylist, Context context) {
        this.tripdetailarraylist = tripdetailarraylist;
        this.context = context;
    }

    @Override
    public TripsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view;
        //if view type is self
        if (i == MANUAL) {
            //Inflating the layout self
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trips_manual_booking_lay, viewGroup, false);

        } else {
            //Inflating the layout self
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trips_item_layout, viewGroup, false);

        }

        AppController.getAppComponent().inject(this);
        return new ViewHolder(view);
    }


    public void setOnItemRatingClickListner(onItemRatingClickListner onItemRatingClickListner) {
        this.onItemRatingClickListner = onItemRatingClickListner;
    }

    public interface onItemRatingClickListner{
        void setRatingClick(int position);
    }
    
    
    /*
     *  Bind data for driver details list
     */
    @Override
    public void onBindViewHolder(final TripsAdapter.ViewHolder viewHolder, final int i) {


        if (viewHolder.getItemViewType() == MANUAL) {

            System.out.println("Exxxxxx : ");
            viewHolder.date_and_time.setText(tripdetailarraylist.get(i).getScheduleDate() + " " + tripdetailarraylist.get(i).getScheduleTime());
            viewHolder.trip_status.setText(tripdetailarraylist.get(i).getStatus());
            viewHolder.pickupaddress.setText(tripdetailarraylist.get(i).getPickupLocation());
            viewHolder.destadddress.setText(tripdetailarraylist.get(i).getDropLocation());
            viewHolder.trip_id_button.setText(context.getString(R.string.trip_id) + tripdetailarraylist.get(i).getId());

        } else {
            String currencysymbol = sessionManager.getCurrencySymbol();
            viewHolder.tv_country.setText(context.getResources().getString(R.string.trip_id) + "" + tripdetailarraylist.get(i).getId());
            viewHolder.carname.setText(tripdetailarraylist.get(i).getVehicleName());
            viewHolder.btnrate.setVisibility(View.GONE);
            viewHolder.status.setVisibility(View.VISIBLE);
            if (tripdetailarraylist.get(i).getStatus().equals("Rating")) {
                viewHolder.status.setText(context.getString(R.string.Rating));
                viewHolder.status.setVisibility(View.GONE);
                viewHolder.btnrate.setVisibility(View.VISIBLE);
            } else if (tripdetailarraylist.get(i).getStatus().equals("Cancelled")) {
                viewHolder.status.setText(context.getString(R.string.Cancelled));
            } else if (tripdetailarraylist.get(i).getStatus().equals("Completed")) {
                viewHolder.status.setText(context.getString(R.string.completed));
            } else if (tripdetailarraylist.get(i).getStatus().equals("Payment")) {
                viewHolder.status.setText(context.getString(R.string.payment));
            } else if (tripdetailarraylist.get(i).getStatus().equals("Begin trip")) {
                viewHolder.status.setText(context.getString(R.string.begin_trip_text));
            } else if (tripdetailarraylist.get(i).getStatus().equals("End trip")) {
                viewHolder.status.setText(context.getString(R.string.end_trip_text));
            } else if (tripdetailarraylist.get(i).getStatus().equals("Scheduled")) {
                viewHolder.status.setText(context.getString(R.string.scheduled));
            } else {
                viewHolder.status.setText(tripdetailarraylist.get(i).getVehicleName());
            }
            if (sessionManager.getUserType()!=null&&!TextUtils.isEmpty(sessionManager.getUserType())&&!sessionManager.getUserType().equalsIgnoreCase("0")&&!sessionManager.getUserType().equalsIgnoreCase("1")){
                // Company
                //String driver_total_fare = String.valueOf(Float.valueOf(tripdetailarraylist.get(i).getSubTotalFare()));
                viewHolder.amountcard.setText(Html.fromHtml(tripdetailarraylist.get(i).getSubTotalFare()).toString());
            }else{
                // Normal Driver
                if (Float.valueOf(tripdetailarraylist.get(i).getDriverPayout()) > 0 )
                    viewHolder.amountcard.setText(currencysymbol + tripdetailarraylist.get(i).getDriverPayout());
                else {
                    String driver_total_fare = String.valueOf(Float.valueOf(tripdetailarraylist.get(i).getTotalFare()));
                    viewHolder.amountcard.setText(currencysymbol + driver_total_fare);
                }
            }




            Picasso.with(context).load(tripdetailarraylist.get(i).getMapPath())
                    .into(viewHolder.imageView);

            viewHolder.btnrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionManager.setTripId(tripdetailarraylist.get(i).getId());
                    Intent rating = new Intent(context, Riderrating.class);
                    rating.putExtra("imgprofile", tripdetailarraylist.get(i).getRiderImage());
                    rating.putExtra("back", 1);
                    context.startActivity(rating);

                }
            });

            viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemRatingClickListner.setRatingClick(i);

                }
            });



        }


    }

    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        PastTripModel manualBookingViewTypeUi = tripdetailarraylist.get(position);


        //If its owner  id is  equals to the logged in user id
        if (manualBookingViewTypeUi.getManualBookingUi() == 1) {
            //Returning manual
            return MANUAL;
        } else {

            //else returning other
            return OTHER;
        }
    }

    @Override
    public int getItemCount() {
        return tripdetailarraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_country;
        private TextView carname;
        private TextView status;
        private TextView amountcard;
        private ImageView imageView;
        private TextView manual_booking;

        private TextView date_and_time;
        private TextView trip_status;
        private TextView pickupaddress;
        private TextView destadddress;
        private Button trip_id_button;
        private Button btnrate;
        private CardView card_view;


        public ViewHolder(View view) {
            super(view);

            tv_country = (TextView) view.findViewById(R.id.datetime);
            carname = (TextView) view.findViewById(R.id.carname);
            status = (TextView) view.findViewById(R.id.status);
            amountcard = (TextView) view.findViewById(R.id.amountcard);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            manual_booking = (TextView) view.findViewById(R.id.manual_booking);
            date_and_time = (TextView) view.findViewById(R.id.date_and_time);
            trip_status = (TextView) view.findViewById(R.id.trip_status);
            pickupaddress = (TextView) view.findViewById(R.id.pickupaddress);
            destadddress = (TextView) view.findViewById(R.id.destadddress);
            trip_id_button = (Button) view.findViewById(R.id.trip_id_button);
            btnrate = (Button) view.findViewById(R.id.btnrate);
            card_view = (CardView) view.findViewById(R.id.card_view);
        }
    }

}
