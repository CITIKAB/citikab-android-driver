package com.trioangle.goferdriver.paymentstatement;
/**
 * @package com.trioangle.goferdriver.paymentstatement
 * @subpackage paymentstatement model
 * @category PayAdapter
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
                PayAdapter
Its used to view the list PayAdapter details
*************************************************************** */
public class PayAdapter extends RecyclerView.Adapter<PayAdapter.ViewHolder> {
    private List<PayModel> modelItems;

    public PayAdapter(List<PayModel> Items) {
        this.modelItems = Items;
    }

    @Override
    public PayAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pay_statement_itemlayout, viewGroup, false);
        return new ViewHolder(view);
    }

    /*
    *  Driver pay details bind data
    */
    @Override
    public void onBindViewHolder(PayAdapter.ViewHolder viewHolder, int i) {
        PayModel currentItem = getItem(i);
        viewHolder.tripdatetime.setText(currentItem.getTripDateTime());
        viewHolder.tripamount.setText(currentItem.getTripAmount());
    }

    @Override
    public int getItemCount() {
        return modelItems.size();
    }

    private PayModel getItem(int position) {
        return modelItems.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tripdatetime, tripamount;

        public ViewHolder(View view) {
            super(view);
            tripdatetime = (TextView) view.findViewById(R.id.tripdatetime);
            tripamount = (TextView) view.findViewById(R.id.tripamount);
        }
    }

}
