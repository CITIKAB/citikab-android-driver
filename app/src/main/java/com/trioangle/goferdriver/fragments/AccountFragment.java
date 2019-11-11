/*
 * Copyright (c) 2017. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.trioangle.goferdriver.fragments;
/**
 * @package com.trioangle.goferdriver.fragments
 * @subpackage fragments
 * @category AccountFragment
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.datamodel.BankDetailsModel;
import com.trioangle.goferdriver.datamodel.CurrencyDetailsModel;
import com.trioangle.goferdriver.datamodel.CurreneyListModel;
import com.trioangle.goferdriver.datamodel.DriverProfileModel;
import com.trioangle.goferdriver.fragments.currency.CurrencyListAdapter;
import com.trioangle.goferdriver.fragments.currency.CurrencyModel;
import com.trioangle.goferdriver.fragments.language.LanguageAdapter;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.map.GpsService;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.payouts.BankDetailsActivity;
import com.trioangle.goferdriver.payouts.PayoutBankDetailsActivity;
import com.trioangle.goferdriver.payouts.PayoutEmailListActivity;
import com.trioangle.goferdriver.profile.DriverProfile;
import com.trioangle.goferdriver.profile.VehiclInformation;
import com.trioangle.goferdriver.service.WorkerUtils;
import com.trioangle.goferdriver.signinsignup.DocHomeActivity;
import com.trioangle.goferdriver.signinsignup.SigninSignupHomeActivity;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.RequestCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.trioangle.goferdriver.MainActivity.selectedFrag;
import static com.trioangle.goferdriver.util.Enums.REQ_CURRENCY;
import static com.trioangle.goferdriver.util.Enums.REQ_DRIVER_PROFILE;
import static com.trioangle.goferdriver.util.Enums.REQ_LANGUAGE;
import static com.trioangle.goferdriver.util.Enums.REQ_LOGOUT;

/* ************************************************************
                      AccountFragment
Its used get home screen account fragment details
*************************************************************** */

public class AccountFragment extends Fragment implements ServiceListener {

    public static Boolean langclick = false;
    public static Boolean currencyclick = false;
    public static android.app.AlertDialog alertDialogStores1;
    public static android.app.AlertDialog alertDialogStores2;
    public static ArrayList<CurreneyListModel> curreneyListModels = new ArrayList<>();

    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;

    public @InjectView(R.id.imglatout1)
    RelativeLayout imglatout1;
    public @InjectView(R.id.rlt_bank_layout)
    RelativeLayout rltBankLayout;
    public @InjectView(R.id.imglatout2)
    RelativeLayout imglatout2;
    public @InjectView(R.id.documentlayout)
    RelativeLayout documentlayout;
    public @InjectView(R.id.signlayout)
    RelativeLayout signlayout;
    public @InjectView(R.id.paymentlayout)
    RelativeLayout paymentlayout;
    public @InjectView(R.id.currencylayout)
    RelativeLayout currencylayout;
    public @InjectView(R.id.languagelayout)
    RelativeLayout languagelayout;
    public @InjectView(R.id.profile_image1)
    CircleImageView profile_image1;
    public @InjectView(R.id.profilename)
    TextView profilename;
    public @InjectView(R.id.carno)
    TextView carno;
    public @InjectView(R.id.carname)
    TextView carname;
    public @InjectView(R.id.language)
    TextView language;
    public @InjectView(R.id.currency_code)
    TextView currency_code;

    public @InjectView(R.id.car_image)
    ImageView carImage;

    public @InjectView(R.id.tv_view)
    TextView tvView;

    public @InjectView(R.id.pb_loader)
    ProgressBar pbLoader;


    BankDetailsModel bankDetailsModel;
    public String vehicle_name;
    public String vehicle_number;
    public String car_type, car_image;
    public RecyclerView recyclerView1;
    public RecyclerView languageView;
    public ArrayList<CurrencyModel> currencyList;
    public List<CurrencyModel> languagelist;
    public CurrencyListAdapter currencyListAdapter;
    public LanguageAdapter LanguageAdapter;
    public String symbol[];
    public String currencycode[];
    public String currency;
    public String Language;
    public String LanguageCode;
    public DriverProfileModel driverProfileModel;
    public String langocde;
    private AlertDialog dialog;
    private String companyName;
    private int company_id;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        selectedFrag = 4;
        return fragment;
    }

    /*
     *  Delete app local cache data
     **/
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }

        return dir.delete();
    }

    @OnClick(R.id.currencylayout)
    public void currency() {
        currencylayout.setClickable(false);
        currency_list();
    }

    /**
     * Driver Profile
     */
    @OnClick(R.id.imglatout1)
    public void profile() {
        Intent intent = new Intent(getActivity(), DriverProfile.class);
        startActivity(intent);
    }

    /**
     * Bank Details
     */
    @OnClick(R.id.rlt_bank_layout)
    public void bankDetails() {
        Intent intent = new Intent(getActivity(), BankDetailsActivity.class);
        intent.putExtra("bankDetailsModel", bankDetailsModel);
        startActivity(intent);
    }

    /**
     * Driver Vehicle Profile
     */
    @OnClick(R.id.imglatout2)
    public void vehicleProfile() {
        Intent intent = new Intent(getActivity(), VehiclInformation.class);
        intent.putExtra("vehiclename", vehicle_name);
        intent.putExtra("vehiclenumber", vehicle_number);
        intent.putExtra("car_type", car_type);
        intent.putExtra("companyname",companyName);
        intent.putExtra("companyid",company_id);
        intent.putExtra("car_image", car_image);
        startActivity(intent);
    }

    /**
     * Language List
     */
    @OnClick(R.id.languagelayout)
    public void language() {
        languagelist();
        languagelayout.setClickable(false);
    }

    /**
     * Edit Document
     */
    @OnClick(R.id.documentlayout)
    public void document() {
        sessionManager.setIsRegister(false);
        Intent signin = new Intent(getActivity(), DocHomeActivity.class);
        startActivity(signin);
        getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    /**
     * Payout
     */
    @OnClick(R.id.paymentlayout)
    public void payout() {
        Intent signin = new Intent(getActivity(), PayoutEmailListActivity.class);
        startActivity(signin);
        getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.signlayout)
    public void logoutpopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        // set the custom dialog components - text, image and button
        TextView cancel = (TextView) dialog.findViewById(R.id.signout_cancel);
        TextView signout = (TextView) dialog.findViewById(R.id.signout_signout);
        // if button is clicked, close the custom dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this, view);

        dialog = commonMethods.getAlertDialog(getActivity());
        pbLoader.setVisibility(View.VISIBLE);
        currency = sessionManager.getCurrencyCode();
        System.out.print("currency" + currency);
        currency_code.setText(currency);

        return view;
    }

    // Load currency list deatils in dialog
    public void currency_list() {

        getCurrency();

        recyclerView1 = new RecyclerView(getActivity());
        currencyList = new ArrayList<>();

        currencyListAdapter = new CurrencyListAdapter(getActivity(), currencyList);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView1.setAdapter(currencyListAdapter);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.currency_header, null);
        alertDialogStores1 = new android.app.AlertDialog.Builder(getActivity()).setCustomTitle(view).setView(recyclerView1).setCancelable(true).show();
        currencylayout.setClickable(true);

        alertDialogStores1.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (currencyclick) {
                    currencyclick = false;
                    currency = sessionManager.getCurrencyCode();
                    // Toast.makeText(getApplicationContext(),"Dismiss dialog "+currency_codes,Toast.LENGTH_SHORT).show();
                    System.out.print("currency" + currency);
                    if (currency != null) {
                        currency_code.setText(currency);
                    } else {
                        currency_code.setText(getResources().getString(R.string.usd));
                    }
                    updateCurrency();
                }
            }
        });

    }

    /**
     * Language List
     */
    public void languagelist() {

        languageView = new RecyclerView(getActivity());
        languagelist = new ArrayList<>();
        loadlang();

        LanguageAdapter = new LanguageAdapter(getActivity(), languagelist);
        languageView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        languageView.setAdapter(LanguageAdapter);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.currency_header, null);
        TextView T = (TextView) view.findViewById(R.id.header);
        T.setText(getString(R.string.selectlanguage));
        alertDialogStores2 = new android.app.AlertDialog.Builder(getActivity()).setCustomTitle(view).setView(languageView).setCancelable(true).show();
        languagelayout.setClickable(true);

        alertDialogStores2.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (langclick) {
                    langclick = false;


                    langocde = sessionManager.getLanguageCode();
                    String lang = sessionManager.getLanguage();
                    language.setText(lang);
                    updateLanguage();
                }
                languagelayout.setClickable(true);

            }
        });
    }

    public void loadlang() {

        String[] languages;
        String[] langCode;
        languages = getResources().getStringArray(R.array.language);
        langCode = getResources().getStringArray(R.array.languageCode);
        for (int i = 0; i < languages.length; i++) {
            CurrencyModel listdata = new CurrencyModel();
            listdata.setCurrencyName(languages[i]);
            listdata.setCurrencySymbol(langCode[i]);
            languagelist.add(listdata);

        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * Driver Datas
     */
    private void getDriverProfile() {
        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.getDriverProfile(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_DRIVER_PROFILE, this));
    }

    /**
     * Driver Logout
     */
    private void logout() {
        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.logout(sessionManager.getType(), sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_LOGOUT, this));
    }

    /**
     * get Currency List
     */
    public void getCurrency() {
        apiService.getCurrency(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_CURRENCY, this));
    }

    /**
     * Update Language
     */
    public void updateLanguage() {
        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.language(sessionManager.getLanguageCode(), sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_LANGUAGE, this));
    }

    /**
     * Update Currency
     */
    private void updateCurrency() {
        commonMethods.showProgressDialog((AppCompatActivity) getActivity(), customDialog);
        apiService.updateCurrency(currency, sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_CURRENCY, this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(getActivity(), dialog, data);
            return;
        }

        switch (jsonResp.getRequestCode()) {
            case REQ_LOGOUT:
                if (jsonResp.isSuccess()) {
                    onSuccessLogOut();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(getActivity(), dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_DRIVER_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessDriverProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(getActivity(), dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_CURRENCY:
                if (jsonResp.isSuccess()) {
                    onSuccessgetCurrency(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(getActivity(), dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_LANGUAGE:
                if (jsonResp.isSuccess()) {
                    setLocale(langocde);
                    getActivity().recreate();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(getActivity(), dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }
    }

    private void onSuccessgetCurrency(JsonResponse jsonResp) {
        CurrencyDetailsModel currencyDetailsModel = gson.fromJson(jsonResp.getStrResponse(), CurrencyDetailsModel.class);
        if (currencyDetailsModel.getCurreneyListModels() != null) {
            curreneyListModels.clear();
            curreneyListModels.addAll(currencyDetailsModel.getCurreneyListModels());

            currencycode = new String[curreneyListModels.size()];
            symbol = new String[curreneyListModels.size()];

            for (int i = 0; i < curreneyListModels.size(); i++) {

                currencycode[i] = curreneyListModels.get(i).getCode();
                symbol[i] = curreneyListModels.get(i).getSymbol();

                symbol[i] = Html.fromHtml(symbol[i]).toString();


                CurrencyModel listdata = new CurrencyModel();
                listdata.setCurrencyName(currencycode[i]);
                listdata.setCurrencySymbol(symbol[i]);

                currencyList.add(listdata);
            }
            currencyListAdapter.notifyDataChanged();

        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

        CommonMethods.DebuggableLogI("datacheck", "datacheck");
    }

    /**
     * SuccessFully Log out
     */
    private void onSuccessLogOut() {

        String lang = sessionManager.getLanguage();
        String langCode = sessionManager.getLanguageCode();

        sessionManager.clearAll();
        clearApplicationData(); // Clear cache data

        sessionManager.setLanguage(lang);
        sessionManager.setLanguageCode(langCode);

        /*Intent GPSservice = new Intent(getContext(), GpsService.class);
        getContext().stopService(GPSservice);*/

        if(WorkerUtils.isWorkRunning(CommonKeys.WorkTagForUpdateGPS))
            WorkerUtils.cancelWorkByTag(CommonKeys.WorkTagForUpdateGPS);
        Intent intent = new Intent(getActivity(), SigninSignupHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();

    }

    public void onSuccessDriverProfile(JsonResponse jsonResponse) {
        driverProfileModel = gson.fromJson(jsonResponse.getStrResponse(), DriverProfileModel.class);
        sessionManager.setProfileDetail(jsonResponse.getStrResponse());
        loadData(driverProfileModel);
        String carid = String.valueOf(driverProfileModel.getCarId());
        sessionManager.setVehicleId(carid);
        CommonMethods.DebuggableLogV("localshared", "Carid==" + sessionManager.getVehicleId());
    }

    /*
     *  Load Driver profile details
     **/
    public void loadData(DriverProfileModel driverProfileModel) {

        String first_name = driverProfileModel.getFirstName();
        String last_name = driverProfileModel.getLastName();
        String user_thumb_image = driverProfileModel.getProfileImage();
        company_id = driverProfileModel.getCompanyId();
        companyName = driverProfileModel.getCompanyName();
        bankDetailsModel = driverProfileModel.getBank_detail();

        sessionManager.setDoc1(driverProfileModel.getLicenseBack());
        sessionManager.setDoc2(driverProfileModel.getLicenseFront());
        sessionManager.setDoc3(driverProfileModel.getInsurance());
        sessionManager.setDoc4(driverProfileModel.getRc());
        sessionManager.setDoc5(driverProfileModel.getPermit());

        vehicle_name = driverProfileModel.getVehicleName();
        vehicle_number = driverProfileModel.getVehicleNumber();
        car_type = driverProfileModel.getCarType();
        car_image = driverProfileModel.getCarActiveImage();

        profilename.setText(first_name + " " + last_name);
        if (isAdded()) {
            Picasso.with(getActivity().getApplicationContext()).load(user_thumb_image).into(profile_image1);
        }

       /* if (company_id > 1) {
            rltBankLayout.setVisibility(View.VISIBLE);
            paymentlayout.setVisibility(View.GONE);
        } else {
            rltBankLayout.setVisibility(View.GONE);
            paymentlayout.setVisibility(View.VISIBLE);
        }*/

        if (!TextUtils.isEmpty(vehicle_name)&&!TextUtils.isEmpty(vehicle_name)) {
            carno.setText(vehicle_number);
            carname.setText(vehicle_name);
        }else {
            pbLoader.setVisibility(View.GONE);
            imglatout2.setEnabled(false);
            tvView.setVisibility(View.GONE);
            carImage.setVisibility(View.VISIBLE);
            carno.setVisibility(View.GONE);
            carname.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            carname.setText(getResources().getString(R.string.no_vehicle_assigned));
        }
        try {
            Picasso.with(getContext()).load(car_image).error(R.drawable.car).into(carImage, new Callback() {
                @Override
                public void onSuccess() {
                    pbLoader.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pbLoader.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     *  Clear app cache data
     **/
    public void clearApplicationData() {
        File cache = getActivity().getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!"lib".equals(s)) {
                    deleteDir(new File(appDir, s));
                    CommonMethods.DebuggableLogI("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");

                    // clearApplicationData();
                }
            }
        }

    }

    /*
     *  Get Driver profile while open the page
     **/
    @Override
    public void onResume() {
        super.onResume();
        Language = sessionManager.getLanguage();
        if (Language != null) {
            language.setText(Language);
        } else {
            language.setText("English");
        }

        getDriverProfile();

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
