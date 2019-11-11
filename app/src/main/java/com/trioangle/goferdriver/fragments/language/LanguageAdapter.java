package com.trioangle.goferdriver.fragments.language;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.fragments.AccountFragment;
import com.trioangle.goferdriver.fragments.currency.CurrencyModel;
import com.trioangle.goferdriver.network.AppController;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.trioangle.goferdriver.fragments.AccountFragment.alertDialogStores2;
import static com.trioangle.goferdriver.signinsignup.SigninSignupHomeActivity.alertDialogStores;

/**
 * Created by trioangle on 31/5/18.
 */

public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static Context context;
    public static String language;
    public static int lastCheckedPosition = -1;
    private static List<CurrencyModel> modelItems;
    public final int TYPE_Explore = 0;
    public final int TYPE_LOAD = 1;
    public @Inject
    SessionManager sessionManager;


    public LanguageAdapter(Context context, List<CurrencyModel> Items) {
        this.context = context;
        this.modelItems = Items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
        return new MovieHolder(inflater.inflate(R.layout.currency_item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_Explore) {
            ((MovieHolder) holder).bindData(modelItems.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_Explore;

    }

    @Override
    public int getItemCount() {
        return modelItems.size();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        private TextView languagen;
        private TextView languagecode;
        private RadioButton radiobutton;
        private RelativeLayout selectlanguage;

        public MovieHolder(View itemView) {
            super(itemView);
            languagen = (TextView) itemView.findViewById(R.id.currencyname_txt);
            languagecode = (TextView) itemView.findViewById(R.id.currencysymbol_txt);
            radiobutton = (RadioButton) itemView.findViewById(R.id.radioButton1);
            selectlanguage = (RelativeLayout) itemView.findViewById(R.id.selectcurrency);
        }

        private void bindData(final CurrencyModel movieModel) {

            String currencycode;
            currencycode = sessionManager.getLanguage();


            languagen.setText(movieModel.getCurrencyName());
            languagecode.setText(movieModel.getCurrencySymbol());
            languagecode.setVisibility(View.GONE);

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


            selectlanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AccountFragment.langclick = true;
                    language = languagen.getText().toString() + " (" + languagecode.getText().toString() + ")";
                    sessionManager.setLanguage(languagen.getText().toString());
                    sessionManager.setLanguageCode(languagecode.getText().toString());


                    lastCheckedPosition = getAdapterPosition();
                    radiobutton.setChecked(true);
                    Locale myLocale = new Locale(movieModel.getCurrencySymbol());
                    Resources res = context.getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.setLocale(myLocale);
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);

                    if (alertDialogStores2 != null) {
                        alertDialogStores2.cancel();
                    }
                    if (alertDialogStores != null) {
                        alertDialogStores.cancel();
                    }

                }
            });
        }
    }
}
