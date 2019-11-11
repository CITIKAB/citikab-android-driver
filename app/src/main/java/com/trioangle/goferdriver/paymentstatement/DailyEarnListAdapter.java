package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category DailyEarnListAdapter
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
                DailyEarnListAdapter
Its used to view the list dailyearnlistadapter details
*************************************************************** */
public class DailyEarnListAdapter extends RecyclerView.Adapter<DailyEarnListAdapter.ViewHolder> {
    private List<PayModel> modelItems;

    public DailyEarnListAdapter(List<PayModel> Items) {
        this.modelItems = Items;
    }

    @Override
    public DailyEarnListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.daily_earning_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /*
    * Driver earning bind data
    */
    @Override
    public void onBindViewHolder(DailyEarnListAdapter.ViewHolder viewHolder, int i) {
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
