package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category TripEarnListAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trioangle.goferdriver.R;

import java.util.List;

/* ************************************************************
                TripEarnListAdapter
Its used to view the trip earn list details function
*************************************************************** */
public class TripEarnListAdapter extends RecyclerView.Adapter<TripEarnListAdapter.ViewHolder> {
    private List<PayModel> modelItems;

    public TripEarnListAdapter(List<PayModel> Items) {
        this.modelItems = Items;
    }

    @Override
    public TripEarnListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_earning_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /*
    *  Driver earning list bind data
    */
    @Override
    public void onBindViewHolder(TripEarnListAdapter.ViewHolder viewHolder, int i) {
        PayModel currentItem = getItem(i);
        viewHolder.dailytrip.setText(currentItem.getDailyTrip());
        viewHolder.dailyamount.setText(currentItem.getTripAmount());
    }

    @Override
    public int getItemCount() {
        return modelItems.size();
    }

    private PayModel getItem(int position) {
        return modelItems.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dailytrip, dailyamount;

        public ViewHolder(View view) {
            super(view);
            dailytrip = (TextView) view.findViewById(R.id.dailytrip);
            dailyamount = (TextView) view.findViewById(R.id.dailyamount);
        }
    }

}
