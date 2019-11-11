package com.trioangle.goferdriver.fragments.currency;

/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.currency
 * @category CurrencyListAdapter
 * @author Trioangle Product Team
 * @version 1.1
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;

import java.util.List;

import javax.inject.Inject;

import static com.trioangle.goferdriver.fragments.AccountFragment.alertDialogStores1;
import static com.trioangle.goferdriver.fragments.AccountFragment.currencyclick;


@SuppressLint("ViewHolder")
public class CurrencyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final String TAG = null;
    public static Context context;
    public static int lastCheckedPosition = -1;
    public static String currency;
    private static List<CurrencyModel> modelItems;
    public final int TYPE_Explore = 0;
    public final int TYPE_LOAD = 1;
    public @Inject
    SessionManager sessionManager;
    public OnLoadMoreListener loadMoreListener;
    public boolean isLoading;
    public boolean isMoreDataAvailable;


    public CurrencyListAdapter(Context context, List<CurrencyModel> Items) {
        this.context = context;
        this.modelItems = Items;
        isMoreDataAvailable = true;
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_Explore) {
            AppController.getAppComponent().inject(this);
            return new MovieHolder(inflater.inflate(R.layout.currency_item_view, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.row_load, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_Explore) {
            ((MovieHolder) holder).bindData(modelItems.get(position), position);
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if (modelItems.get(position).getCurrencyName().equals("load")) {
            return TYPE_LOAD;
        } else {
            return TYPE_Explore;
        }
    }

    @Override
    public int getItemCount() {
        return modelItems.size();
    }

    /* VIEW HOLDERS */

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    class MovieHolder extends RecyclerView.ViewHolder {


        public TextView currencyname;
        public TextView currencysymbol;
        public RadioButton radiobutton;
        public RelativeLayout selectcurrency;

        public MovieHolder(View itemView) {
            super(itemView);
            currencyname = (TextView) itemView.findViewById(R.id.currencyname_txt);
            currencysymbol = (TextView) itemView.findViewById(R.id.currencysymbol_txt);
            radiobutton = (RadioButton) itemView.findViewById(R.id.radioButton1);
            selectcurrency = (RelativeLayout) itemView.findViewById(R.id.selectcurrency);
        }

        public void bindData(CurrencyModel movieModel, int position) {

            String currencycode;
            currencycode = sessionManager.getCurrencyCode();


            currencyname.setText(movieModel.getCurrencyName());
            currencysymbol.setText(movieModel.getCurrencySymbol());

            //currency=movieModel.getCurrencyName()+""+movieModel.getCurrencySymbol();

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{

                            context.getResources().getColor(R.color.ub__uber_black_60)
                            , context.getResources().getColor(R.color.rb_blue_button),
                    }
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radiobutton.setButtonTintList(colorStateList);
            }


            radiobutton.setChecked(false);

            if (movieModel.getCurrencyName().equals(currencycode)) {
                radiobutton.setChecked(true);
            }
            if (lastCheckedPosition == position) {



            }

            selectcurrency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    currency = currencyname.getText().toString() + " (" + currencysymbol.getText().toString() + ")";

                    sessionManager.setCurrencyCode(currencyname.getText().toString());
                    sessionManager.setCurrencySymbol(currencysymbol.getText().toString());


                    lastCheckedPosition = getAdapterPosition();
                    radiobutton.setChecked(true);

                    //new SettingActivity.Updatecurrency().execute();
/*
                    if(alertDialogStores!=null) {
                        alertDialogStores.cancel();
                    }*/
                    currencyclick = true;
                    if (alertDialogStores1 != null) {
                        alertDialogStores1.cancel();
                    }
                   /* if(alertDialogStores2!=null) {
                        alertDialogStores2.cancel();
                    }*/

                }
            });
        }
    }


}
