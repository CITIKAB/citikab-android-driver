package com.trioangle.goferdriver.rating;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category CommentsRecycleAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.custompalette.FontCache;
import com.trioangle.goferdriver.datamodel.InvoiceModel;
import com.trioangle.goferdriver.util.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;

/* ************************************************************
                CommentsRecycleAdapter
Its used to view the feedback comments with rider screen page function
*************************************************************** */
public class PriceRecycleAdapter extends RecyclerView.Adapter<PriceRecycleAdapter.ViewHolder> {
    private ArrayList<InvoiceModel> feedbackarraylist;
    private Context context;


    public PriceRecycleAdapter(Context context,ArrayList<InvoiceModel> feedbackarraylist) {
        this.feedbackarraylist = feedbackarraylist;
        this.context=context;
    }

    @Override
    public PriceRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.price_layout, viewGroup, false);


        return new ViewHolder(view);
    }

    /*
   *  Get rider feedback list bind
   */
    @Override
    public void onBindViewHolder(PriceRecycleAdapter.ViewHolder viewHolder, int i) {
        CommonMethods.DebuggableLogI("key",feedbackarraylist.get(i).getKey());
        CommonMethods.DebuggableLogI("value",feedbackarraylist.get(i).getValue());

        viewHolder.faretxt.setText(feedbackarraylist.get(i).getKey());
        viewHolder.fareAmt.setText(feedbackarraylist.get(i).getValue().replace("\"",""));
        if (feedbackarraylist.get(i).getKey().equals("Base fare"))
        {
            viewHolder.isbase.setVisibility(View.GONE);
        }
        if (feedbackarraylist.get(i).getBar().equals("1")){

            viewHolder.rltprice.setBackground(context.getResources().getDrawable(R.drawable.d_topboarder));

            System.out.println("Key check feedback : "+feedbackarraylist.get(i).getKey());

        }else{
            viewHolder.rltprice.setBackgroundColor(context.getResources().getColor(R.color.white));
        }


        if (feedbackarraylist.get(i).getColour().equals("black")){
            viewHolder.fareAmt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.faretxt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.fareAmt.setTextColor(context.getResources().getColor(R.color.ub__black));
            viewHolder.faretxt.setTextColor(context.getResources().getColor(R.color.ub__black));

        }

        if (feedbackarraylist.get(i).getColour().equals("green")){
            viewHolder.fareAmt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.faretxt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.fareAmt.setTextColor(context.getResources().getColor(R.color.ub__green));
            viewHolder.faretxt.setTextColor(context.getResources().getColor(R.color.ub__green));

        }

    }

    @Override
    public int getItemCount() {
        return feedbackarraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView faretxt;
        private TextView fareAmt;
        private TextView isbase;
        private RelativeLayout rltprice;

        public ViewHolder(View view) {
            super(view);

            faretxt = (TextView) view.findViewById(R.id.faretxt);
            fareAmt = (TextView) view.findViewById(R.id.fareAmt);
            isbase = (TextView) view.findViewById(R.id.baseview);
            rltprice = (RelativeLayout) view.findViewById(R.id.rltprice);

        }
    }


}
