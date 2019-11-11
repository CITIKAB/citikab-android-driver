package com.trioangle.goferdriver.payouts.adapter;


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
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.payouts.payout_model_classed.CountryModel;
import com.trioangle.goferdriver.payouts.payout_model_classed.Header;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.trioangle.goferdriver.payouts.PayoutAddressDetailsActivity.alertDialogStores;


@SuppressLint("ViewHolder")
public class PayoutCountryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int TYPE_Explore = 1;
    public final int TYPE_LOAD = 2;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Header header;

    static Context context;

    protected static final String TAG = null;
    private Activity activity;
    private LayoutInflater inflater;
    public android.app.AlertDialog alertDialog;
    private ArrayList<CountryModel> modelItems;
    int oldposition = 1;

    private boolean[] mIsItemClicked;

    public @Inject
    SessionManager sessionManager;


    public PayoutCountryListAdapter(Context context, ArrayList<CountryModel> Items) {
        this.header = header;
        this.context = context;
        this.modelItems = Items;
        mIsItemClicked = new boolean[240];
        System.out.println("modelItems" + mIsItemClicked.length);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        System.out.println("View Type" + viewType);
        //mIsItemClicked = new boolean[modelItems.size()];
        //System.out.println("modelItems1"+mIsItemClicked.length);
        if (viewType == TYPE_Explore) {
            return new MovieHolder(inflater.inflate(R.layout.payout_country_list, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.row_load, parent, false));
        }
        //throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MovieHolder) {
            System.out.println("MovieHolder position" + (position));
            final CountryModel currentItem = getItem(position);

            final MovieHolder mainholder = (MovieHolder) holder;

            System.out.println("MovieHolder getCountryName" + currentItem.getCountryName());
            mainholder.countryname.setText(currentItem.getCountryName());


            mainholder.countryname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (int i = 0; i < mIsItemClicked.length; i++) {

                        if (i != position) {
                            mIsItemClicked[i] = false;
                        } else {
                            mIsItemClicked[position] = true;
                        }

                    }
                    mainholder.countryname.setTextColor(context.getResources().getColor(R.color.white));
                    sessionManager.setCountryName(currentItem.getCountryName().toString());
                    sessionManager.setPayPalCountryCode(currentItem.getCountryName().toString());
//					localSharedPreferences.saveSharedPreferences(Constants.CountryName,currentItem.getCountryName().toString());
//					localSharedPreferences.saveSharedPreferences(Constants.PayPalCountryCode,currentItem.getCountryCode().toString());

                    if (alertDialogStores != null) {
                        alertDialogStores.cancel();
                    }

                    // this below line copied from macent and for the file LYS_Step4_AddressDetails.java,
                    // hence we not imported this LYS_Step4_AddressDetails file here, so commented
					/*if(alertDialogStorestwo!=null) {
						alertDialogStorestwo.cancel();
					}*/


					/*System.out.println("Position "+position+" Current Value"+currentItem.getCountryName().toString());
					System.out.println("Position "+position+" Current Position"+currentItem.getCountryId());
					notifyItemChanged(position);
					notifyItemChanged(oldposition);
					oldposition=position;*/

                }
            });

        }

    }


    private CountryModel getItem(int position) {
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

    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;

        public HeaderHolder(View itemView) {
            super(itemView);
            this.txtTitle = (TextView) itemView.findViewById(R.id.header);
            this.txtTitle.setText(context.getResources().getString(R.string.selectcountry));
            this.txtTitle.setTextSize(context.getResources().getDimension(R.dimen.midb));
            this.txtTitle.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    static class MovieHolder extends RecyclerView.ViewHolder {
        TextView countryname;
        RelativeLayout country_layout;

        public MovieHolder(View itemView) {
            super(itemView);
            countryname = (TextView) itemView.findViewById(R.id.countryname);
            country_layout = (RelativeLayout) itemView.findViewById(R.id.country_layout);
        }

        void bindData(CountryModel movieModel, final int position) {

            countryname.setText(movieModel.getCountryName());


            countryname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //	this.notifyDataChanged();
                    System.out.println("Position" + position + "Country name" + countryname.getText().toString());

                    countryname.setTextColor(context.getResources().getColor(R.color.white));
                    Toast.makeText(context, "Position" + position + "Country name" + countryname.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
    }


}