package com.trioangle.goferdriver.payouts;

/**
 *
 * @package     com.makent.trioangle
 * @subpackage  adapter
 * @category    PayoutCountryListAdapter
 * @author      Trioangle Product Team
 * @version     1.1
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trioangle.goferdriver.R;

import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.payouts.payout_model_classed.Header;
import com.trioangle.goferdriver.payouts.payout_model_classed.Makent_model;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.trioangle.goferdriver.payouts.PayoutBankDetailsActivity.alertDialog;

@SuppressLint("ViewHolder")
 public class PayoutCoutryListAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public @Inject
    SessionManager sessionManager;
    public final int TYPE_Explore = 1;
    public final int TYPE_LOAD = 2;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Header header;

    static Context context;

    protected static final String TAG = null;
    private Activity activity;
    private LayoutInflater inflater;
    public String type;
    private ArrayList<Makent_model> modelItems;
    int oldposition=1;

    private boolean[] mIsItemClicked;

    private onItemClickListener mItemClickListener;

    public void setOnItemClickListener(onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClickListener(int position);
    }



    public PayoutCoutryListAdapter2(Context context, ArrayList<Makent_model> Items,String type) {
        this.header = header;
        this.context = context;
        this.modelItems = Items;
        this.type = type;
        mIsItemClicked = new boolean[240];
        AppController.getAppComponent().inject(this);
        System.out.println("modelItems"+mIsItemClicked.length);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        System.out.println("View Type"+viewType);
        //mIsItemClicked = new boolean[modelItems.size()];
        //System.out.println("modelItems1"+mIsItemClicked.length);
        if(viewType==TYPE_Explore){
            return new MovieHolder(inflater.inflate(R.layout.payout_country_list,parent,false));
        }else{
            return new LoadHolder(inflater.inflate(R.layout.row_load,parent,false));
        }
        //throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof MovieHolder)
        {
            System.out.println("MovieHolder position"+(position));
            final Makent_model currentItem = getItem(position);

            final 	MovieHolder mainholder=(MovieHolder)holder;

            System.out.println("MovieHolder position 2 "+currentItem.getCountryName());
            mainholder.countryname.setText(currentItem.getCountryName());


            mainholder.countryname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                  /*  for (int i = 0; i < mIsItemClicked.length; i++) {

                        if(i!=position) {
                            mIsItemClicked[i] = false;
                        }else
                        {
                            mIsItemClicked[position]=true;
                        }

                    }*/

                    //mainholder.countryname.setTextColor(context.getResources().getColor(R.color.white));

                    if (type.equals("country")) {
                        sessionManager.setCountryName2(getItem(position).getCountryName());
                        System.out.println("get Country name " + getItem(position).getCountryName());
                        sessionManager.setStripeCountryCode(getItem(position).getCountryCode());
                    } else if (type.equals("currency")) {
                        sessionManager.setCurrencyName2(getItem(position).getCountryName());
                    } else {
                        sessionManager.setGender(getItem(position).getCountryName());
                    }


                    if (alertDialog != null) {
                        sessionManager.setCountryCurrencyType(type);
                        alertDialog.cancel();
                    }
                    if (mItemClickListener!=null){
                        System.out.println("IS NOT NULL");
                        mItemClickListener.onItemClickListener(position);
                    }


					/*System.out.println("Position "+position+" Current Value"+currentItem.getCountryName().toString());
					System.out.println("Position "+position+" Current Position"+currentItem.getCountryId());
					notifyItemChanged(position);
					notifyItemChanged(oldposition);
					oldposition=position;*/

                }
            });

        }

    }


    private Makent_model getItem(int position)
    {
        return modelItems.get(position);
    }

    public int getItemViewType(int position) {

        return TYPE_Explore;
    }




    @Override
    public int getItemCount() {
        return modelItems.size();
    }



    /* VIEW HOLDERS */

    class HeaderHolder extends RecyclerView.ViewHolder{
        TextView txtTitle;
        public HeaderHolder(View itemView) {
            super(itemView);
            this.txtTitle = (TextView)itemView.findViewById(R.id.header);
            //this.txtTitle.setText(context.getResources().getString(R.string.selectcountry));
            this.txtTitle.setText(context.getResources().getString(R.string.select)+" "+type);
            this.txtTitle.setTextSize(context.getResources().getDimension(R.dimen.midb));
            this.txtTitle.setTextColor(context.getResources().getColor(R.color.white));
        }
    }
    static class MovieHolder extends RecyclerView.ViewHolder{
        TextView countryname;
        RelativeLayout country_layout;

        public MovieHolder(View itemView) {
            super(itemView);
            countryname=(TextView) itemView.findViewById(R.id.countryname);
            country_layout=(RelativeLayout) itemView.findViewById(R.id.country_layout);
        }

        void bindData(Makent_model movieModel,final int position){

            countryname.setText(movieModel.getCountryName());


            countryname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //	this.notifyDataChanged();
                    System.out.println("Position"+position+"Country name"+countryname.getText().toString());

                    countryname.setTextColor(context.getResources().getColor(R.color.white));
                    Toast.makeText(context,"Position"+position+"Country name"+countryname.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void notifyDataChanged(){
        notifyDataSetChanged();
    }


}