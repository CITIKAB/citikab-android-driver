package com.trioangle.goferdriver.payouts;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.goferdriver.BuildConfig;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.helper.Constants;
import com.trioangle.goferdriver.helper.CustomDialog;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.interfaces.ServiceListener;
import com.trioangle.goferdriver.model.JsonResponse;
import com.trioangle.goferdriver.payouts.payout_model_classed.Makent_model;
import com.trioangle.goferdriver.payouts.payout_model_classed.StripeCountriesModel;
import com.trioangle.goferdriver.payouts.payout_model_classed.StripeCountryDetails;

import com.trioangle.goferdriver.util.CommonMethods;
import com.trioangle.goferdriver.util.ConnectionDetector;
import com.trioangle.goferdriver.util.RequestCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.trioangle.goferdriver.util.Enums.REQ_GET_STRIPE;
import static com.trioangle.goferdriver.util.Enums.REQ_UPLOAD_PAYOUT;


public class PayoutBankDetailsActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener {


    public @Inject
    ApiService apiService;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;

    public @Inject
    SessionManager sessionManager;


    RecyclerView recyclerView1;
    ArrayList<Makent_model> countryList = new ArrayList<>();
    String image = null;
    PayoutCoutryListAdapter2 countryListAdapter;
    String imagePath = "";
    private final int SELECT_FILE = 1, REQUEST_CAMERA = 0;
    JSONArray ja;
    protected boolean isInternetAvailable;
    Bitmap thumbnail = null;
    public String baseurl;
    public static boolean flag = false;
    public int i = 0;
    private AlertDialog dialog;
    public StripeCountriesModel stripeCountriesModel;
    public static ArrayList<StripeCountryDetails> sCountryDetails = new ArrayList<>();

    public LinearLayout addresskana_linear, addresskanji_linear;
    String addresskanji1names, addresskanji2names, kanjicitynames, kanjistatenames, kanjipostalcodenames, addresskana1names, addresskana2names, kanacitynames, kanastatenames, kanapostalcodenames, gendernames, Phonenumbernames, accountownernames, banknames, branchnames, branch_code_names, bank_code_names, transitnonames, routing_number_names, ssn_names, institutenonames, bsbnames, sort_codenames, clearingcodenames, accountnumbernames, CountryNames, currencynames, Ibannames, accountholdernmaes, address1names, address2names, citynames, statenames, postalcodenames;
    public static android.app.AlertDialog alertDialog;
    public TextView addresskana_msg, addresskanji_msg;
    public Button payout_submit;
    public RelativeLayout payoutaddress_title;
    public EditText addresskanji1, addresskanji2, kanjicity, kanjistate, kanjipostalcode, addresskana1, addresskana2, kanacity, kanastate, kanapostalcode, gender, bank_name, branch_name, legal_doc, Ac_owner_name, ph_no, payoutaddress_country, payoutaddress_currency, bsb, Accountnumber, transitno, instituteno, ssn, routing_number, clearing_code, bank_code, branch_code, sort_code, Iban_no, Ac_holder_name, address1, address2, city, state, postalcode;
    public String[] countryname /*={"Austria", "Australia", "Belgium", "Canada", "Denmark", "Finland", "France", "Germany", "Hong Kong", "Ireland", "Italy", "Japan", "Luxembourg", "Netherlands", "New Zealand", "Norway", "Portugal", "Singapore", "Spain", "Sweden", "Switzerland", "United Kingdom", "United States"}*/;
    public String[] currencyname /*= {"EUR", "DKK", "GBP", "NOK", "SEK", "USD", "CHF"}*/;
    public String[] currencynamecanada/* ={"CAD", "USD"}*/;
    public String[] genders = {"Male", "Female"};
    private String encodedImage;
    public String[] countryID;
    public String[] countryCode;
    public String CountryCodeNames;
    public int currencyPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_bank_details);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);
        payoutaddress_country = (EditText) findViewById(R.id.payoutaddress_country);
        payoutaddress_currency = (EditText) findViewById(R.id.payoutaddress_currency);
        bsb = (EditText) findViewById(R.id.bsb);
        Accountnumber = (EditText) findViewById(R.id.Ac_no);
        payoutaddress_title = (RelativeLayout) findViewById(R.id.payoutaddress_title);
        bank_name = (EditText) findViewById(R.id.bank_name);
        payout_submit = (Button) findViewById(R.id.payout_submit);
        gender = (EditText) findViewById(R.id.gender);
        branch_name = (EditText) findViewById(R.id.branch_name);
        Ac_owner_name = (EditText) findViewById(R.id.Ac_owner_name);
        addresskana1 = (EditText) findViewById(R.id.addresskana1);
        addresskana2 = (EditText) findViewById(R.id.addresskana2);
        kanacity = (EditText) findViewById(R.id.kanacity);
        kanastate = (EditText) findViewById(R.id.kanastate);
        kanapostalcode = (EditText) findViewById(R.id.kanapostalcode);
        ph_no = (EditText) findViewById(R.id.ph_no);
        legal_doc = (EditText) findViewById(R.id.legal_doc);
        transitno = (EditText) findViewById(R.id.transit_no);
        instituteno = (EditText) findViewById(R.id.institute_no);
        routing_number = (EditText) findViewById(R.id.routing_number);
        ssn = (EditText) findViewById(R.id.ssn_number);
        clearing_code = (EditText) findViewById(R.id.clearing_code);
        bank_code = (EditText) findViewById(R.id.bank_code);
        addresskana_msg = (TextView) findViewById(R.id.addresskana_msg);
        addresskanji_msg = (TextView) findViewById(R.id.addresskanji_msg);
        branch_code = (EditText) findViewById(R.id.branch_code);
        sort_code = (EditText) findViewById(R.id.sort_code);
        Iban_no = (EditText) findViewById(R.id.Iban_no);
        Ac_holder_name = (EditText) findViewById(R.id.Ac_holder_name);
        address1 = (EditText) findViewById(R.id.address1);
        address2 = (EditText) findViewById(R.id.address2);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        postalcode = (EditText) findViewById(R.id.postalcode);
        addresskana_linear = (LinearLayout) findViewById(R.id.addresskana_linear);
        addresskanji_linear = (LinearLayout) findViewById(R.id.addresskanji_linear);
        addresskanji1 = (EditText) findViewById(R.id.addresskanji1);
        addresskanji2 = (EditText) findViewById(R.id.addresskanji2);
        kanjicity = (EditText) findViewById(R.id.kanjicity);
        kanjistate = (EditText) findViewById(R.id.kanjistate);
        kanjipostalcode = (EditText) findViewById(R.id.kanjipostalcode);


        isInternetAvailable = getNetworkState().isConnectingToInternet();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (isInternetAvailable) {
            getStripeCountryList();
        } else {
            snackBar(getResources().getString(R.string.Interneterror));
        }

        payoutaddress_country.setOnClickListener(this);
        payoutaddress_currency.setOnClickListener(this);
        payout_submit.setOnClickListener(this);
        legal_doc.setOnClickListener(this);
        gender.setOnClickListener(this);
        payoutaddress_title.setOnClickListener(this);

    }

    private void getStripeCountryList() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.stripeSupportedCountry(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_GET_STRIPE, this));
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payoutaddress_country: {
                countryList("country");
            }

            break;

            case R.id.payoutaddress_currency: {
                countryList("currency");
                //countryList();
            }

            break;
            case R.id.payoutaddress_title: {

                onBackPressed();
            }

            break;

            case R.id.legal_doc: {
                selectImage();
            }

            break;
            case R.id.gender: {
                countryList("gender");
            }

            break;
            case R.id.payout_submit: {

                CountryNames = payoutaddress_country.getText().toString();
                if (CountryNames.equals("Austria") || CountryNames.equals("Belgium") || CountryNames.equals("Denmark") || CountryNames.equals("Finland") || CountryNames.equals("France") || CountryNames.equals("Germany") || CountryNames.equals("Ireland") || CountryNames.equals("Italy") || CountryNames.equals("Luxembourg") || CountryNames.equals("Norway") || CountryNames.equals("Portugal") || CountryNames.equals("Spain") || CountryNames.equals("Sweden") || CountryNames.equals("Switzerland") || CountryNames.equals("Belgium") || CountryNames.equals("Netherlands")) {
                    Ibannames = Iban_no.getText().toString();
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();


                    if (isInternetAvailable) {
                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("iban", Ibannames);
                        imageObject.put("document", image);

                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.choose_currency));
                        } else if (Ibannames.equals("")) {
                            snackBar(getResources().getString(R.string.iban_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.address_1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }


                    } else {
                        snackBar(getResources().getString(R.string.no_connection));
                    }
                } else if (CountryNames.equals("Australia")) {

                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();
                    bsbnames = bsb.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();

                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("bsb", bsbnames);
                        imageObject.put("document", image);
                        imageObject.put("account_number", accountnumbernames);

                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (bsbnames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_BSB));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }


                    } else {
                    }

                } else if (CountryNames.equals("Canada")) {
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();
                    transitnonames = transitno.getText().toString();
                    institutenonames = instituteno.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();


                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("document", image);
                        imageObject.put("account_number", accountnumbernames);
                        imageObject.put("transit_number", transitnonames);
                        imageObject.put("institution_number", institutenonames);

                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.choose_currency));
                        } else if (transitnonames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_transit_number));
                        } else if (institutenonames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_institution_number));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {
                            updateProfile(imageObject);
                        }

                    } else {
                    }


                } else if (CountryNames.equals("New Zealand")) {
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();
                    routing_number_names = routing_number.getText().toString();
                    ;
                    accountnumbernames = Accountnumber.getText().toString();

                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("routing_number", routing_number_names);
                        imageObject.put("document", image);
                        imageObject.put("account_number", accountnumbernames);

                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (routing_number_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_routing_number));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }
                    } else {
                    }


                } else if (CountryNames.equals("Singapore")) {
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();


                    bank_code_names = bank_code.getText().toString();
                    branch_code_names = branch_code.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();

                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("bank_code", bank_code_names);
                        imageObject.put("branch_code", branch_code_names);
                        imageObject.put("document", image);
                        imageObject.put("account_number", accountnumbernames);
                        imageObject.put("account_holder_name", accountholdernmaes);


                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (bank_code_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_bank_code));
                        } else if (branch_code_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_branch_code));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }


                    } else {
                    }


                } else if (CountryNames.equals("United Kingdom")) {
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();


                    sort_codenames = sort_code.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();


                    if (isInternetAvailable) {
                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("sort_code", sort_codenames);
                        imageObject.put("document", image);
                        imageObject.put("account_number", accountnumbernames);
                        imageObject.put("account_holder_name", accountholdernmaes);


                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (sort_codenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_sort_code));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }

                    } else {
                    }

                } else if (CountryNames.equals("United States")) {
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();
                    routing_number_names = routing_number.getText().toString();
                    ssn_names = ssn.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();

                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("routing_number", routing_number_names);
                        imageObject.put("ssn_last_4", ssn_names);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("document", image);
                        imageObject.put("account_number", accountnumbernames);


                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (ssn_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_SSN));
                        } else if (routing_number_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_routing_number));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }
                    } else {

                    }


                } else if (CountryNames.equals("Hong Kong")) {
                    currencynames = payoutaddress_currency.getText().toString();
                    accountholdernmaes = Ac_holder_name.getText().toString();
                    address1names = address1.getText().toString();
                    address2names = address2.getText().toString();
                    citynames = city.getText().toString();
                    statenames = state.getText().toString();
                    postalcodenames = postalcode.getText().toString();

                    clearingcodenames = clearing_code.getText().toString();
                    branch_code_names = branch_code.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();

                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("clearing_code", clearingcodenames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("branch_code", branch_code_names);
                        imageObject.put("document", image);//branch_code
                        imageObject.put("account_number", accountnumbernames);

                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (clearingcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_clearing_code));
                        } else if (branch_code_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_branch_code));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }

                    } else {
                    }


                } else if (CountryNames.equals("Japan")) {


                    currencynames = payoutaddress_currency.getText().toString();
                    banknames = bank_name.getText().toString();
                    branchnames = branch_name.getText().toString();
                    bank_code_names = bank_code.getText().toString();
                    branch_code_names = branch_code.getText().toString();
                    accountnumbernames = Accountnumber.getText().toString();
                    accountownernames = Ac_owner_name.getText().toString();
                    Phonenumbernames = ph_no.getText().toString();
                    gendernames = gender.getText().toString();


                    if (gendernames.equals("Male")) {
                        gendernames = "male";
                    } else {
                        gendernames = "female";
                    }
                    addresskana1names = addresskana1.getText().toString();

                    accountholdernmaes = Ac_holder_name.getText().toString();
                    addresskana2names = addresskana2.getText().toString();
                    kanacitynames = kanacity.getText().toString();

                    kanastatenames = kanastate.getText().toString();
                    kanapostalcodenames = kanapostalcode.getText().toString();
                    addresskanji1names = addresskanji1.getText().toString();
                    addresskanji2names = addresskanji2.getText().toString();
                    kanjicitynames = kanjicity.getText().toString();
                    kanjistatenames = kanjistate.getText().toString();
                    kanjipostalcodenames = kanjipostalcode.getText().toString();


                    if (isInternetAvailable) {

                        HashMap<String, String> imageObject = new HashMap<String, String>();
                        image = getStringImage(thumbnail);

                        imageObject.put("payout_method", "stripe");
                        imageObject.put("currency", currencynames);
                        imageObject.put("document", image);//branch_code
                        imageObject.put("account_number", accountnumbernames);
                        imageObject.put("address1", addresskana1names);
                        imageObject.put("address2", addresskana2names);
                        imageObject.put("city", kanacitynames);
                        imageObject.put("state", kanastatenames);
                        imageObject.put("token", sessionManager.getAccessToken());
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("postal_code", kanapostalcodenames);
                        imageObject.put("bank_code", bank_code_names);
                        imageObject.put("bank_name", banknames);
                        imageObject.put("branch_code", branch_code_names);
                        imageObject.put("branch_name", branchnames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("account_owner_name", accountownernames);
                        imageObject.put("phone_number", Phonenumbernames);
                        imageObject.put("kanji_address1", addresskanji1names);
                        imageObject.put("kanji_address2", addresskanji2names);
                        imageObject.put("kanji_city", kanjicitynames);
                        imageObject.put("kanji_state", kanjistatenames);
                        imageObject.put("kanji_postal_code", kanjipostalcodenames);
                        imageObject.put("gender", gendernames);


                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (banknames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_bank_name));
                        } else if (branchnames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_branch_name));
                        } else if (bank_code_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_bank_code));
                        } else if (branch_code_names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_branch_code));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountownernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_owner_name));
                        } else if (Phonenumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_phone_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (gender.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_gender ));
                        } else if (addresskana1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address_1_of_kana));
                        } else if (addresskana2names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address_2_of_kana));
                        } else if (kanacitynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city_of_kana ));
                        } else if (kanastatenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state_of_kana));
                        } else if (kanapostalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code_of_kana ));
                        } else if (addresskanji1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address_1_of_kanji));
                        } else if (addresskanji2names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address_2_of_kanji));
                        } else if (kanjicitynames.equals("")) {

                            snackBar(getResources().getString(R.string.please_enter_city_of_kanji ));
                        } else if (kanjistatenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state_of_kanji));
                        } else if (kanjipostalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code_of_kanji));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {

                            updateProfile(imageObject);
                        }

                    } else {
                        currencynames = payoutaddress_currency.getText().toString();
                        Ibannames = Iban_no.getText().toString();
                        accountholdernmaes = Ac_holder_name.getText().toString();
                        address1names = address1.getText().toString();
                        address2names = address2.getText().toString();
                        citynames = city.getText().toString();
                        statenames = state.getText().toString();
                        postalcodenames = postalcode.getText().toString();
                        HashMap<String, String> imageObject = new HashMap<String, String>();

                        imageObject.put("payout_method", "stripe");
                        imageObject.put("country", CountryCodeNames);
                        imageObject.put("currency", currencynames);
                        imageObject.put("iban", Ibannames);
                        imageObject.put("account_holder_name", accountholdernmaes);
                        imageObject.put("address1", address1names);
                        imageObject.put("address2", address2names);
                        imageObject.put("city", citynames);
                        imageObject.put("state", statenames);
                        imageObject.put("postal_code", postalcodenames);
                        imageObject.put("document", image);
                        imageObject.put("token", sessionManager.getAccessToken());


                        if (currencynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_choose_a_currency));
                        } else if (accountnumbernames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_number));
                        } else if (accountholdernmaes.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_account_holder_name));
                        } else if (address1names.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_address1));
                        } else if (citynames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_city));
                        } else if (statenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_state));
                        } else if (postalcodenames.equals("")) {
                            snackBar(getResources().getString(R.string.please_enter_postal_code));
                        } else if (legal_doc.getText().toString().equals("")) {
                            snackBar(getResources().getString(R.string.please_upload_legal_document));
                        } else {
                            updateProfile(imageObject);
                        }
                    }

                } else {
                    snackBar(getResources().getString(R.string.please_choose_country));

                }


            }

            break;
        }
    }

    private void selectImage() {
        //final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        final CharSequence[] items = {getResources().getString(R.string.takephoto_title), getResources().getString(R.string.choosefromlib), getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.addphoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result = Utility.checkPermission(PayoutBankDetailsActivity.this);

                if (items[item].equals(getResources().getString(R.string.takephoto_title))) {
                    cameraIntent();

                } else if (items[item].equals(getResources().getString(R.string.choosefromlib))) {
                    galleryIntent();

                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},SELECT_FILE);
        }else{
            startGallaryIntent();
        }
    }



    private void cameraIntent() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
        }else if((ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
        }else{
            startCameraIntent();
        }

    }

    private void startCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void startGallaryIntent(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_FILE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA:{

                if((ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
                    startCameraIntent();
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            showOpenSettingsDialog();
                        }
                    }
                }
                break;
            }
            case SELECT_FILE:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    startGallaryIntent();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            showOpenSettingsDialog();
                        }
                    }
                }
                break;
            }
            default:break;
        }
    }
    public void showOpenSettingsDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getResources().getString(R.string.enable_permission));
        alertBuilder.setMessage(getResources().getString(R.string.external_storage_permission_necessary));
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(data);
        }
    }

    /**
     * Getting image uri from bitmap
     *
     * @param inContext Activity
     * @param inImage   Image In BitMap
     * @return path
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        legal_doc.setText(System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri selectedImage = getImageUri(getApplicationContext(), thumbnail);
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        System.out.println("Column index 1 " + filePathColumn[0] + " " + filePathColumn + " " + selectedImage);

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        System.out.println("Column index 2 " + filePathColumn[0] + " " + cursor);
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        System.out.println("Column index 2 " + columnIndex);

        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        imagePath = picturePath;

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String picturePath = cursor.getString(columnIndex);
                File f = new File(picturePath);
                String imageName = f.getName();
                cursor.close();

                imagePath = picturePath;
                legal_doc.setText(imageName);

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                thumbnail = BitmapFactory.decodeStream(inputStream, null, options);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public String getStringImage(Bitmap bmp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } catch (Exception e) {

        }

        return encodedImage;
    }


    public void countryList(String type) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.currency_header, null);
        TextView title = (TextView) view.findViewById(R.id.header);

        recyclerView1 = new RecyclerView(PayoutBankDetailsActivity.this);
        countryList = new ArrayList<>();

        countryListAdapter = new PayoutCoutryListAdapter2(this, countryList, type);


        recyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView1.setAdapter(countryListAdapter);
        if (type.equals("country")) {
            countryListAdapter.setOnItemClickListener(new PayoutCoutryListAdapter2.onItemClickListener() {
                @Override
                public void onItemClickListener(int position) {
                    currencyPosition = position;
                    currencyname = new String[sCountryDetails.get(currencyPosition).getCurrencyCode().length];
                    //CountryCodeNames = sCountryDetails.get(position).getCountryName();
                    CountryCodeNames = sCountryDetails.get(position).getCountryCode();
                    System.out.println("Country COde names " + CountryCodeNames);
                    countryList.clear();
                    currencyname = sCountryDetails.get(position).getCurrencyCode();
                    for (int i = 0; i < currencyname.length; i++) {

                        //currencyname = sCountryDetails.get(i).getCurrencyCode();
                        Makent_model makent_model = new Makent_model();
                        makent_model.setCountryName(currencyname[i]);
                        countryList.add(makent_model);
                    }

                }
            });
        }
        if (type.equals("country")) {
            title.setText(getResources().getString(R.string.selectcountry));
            setCountry();
            currencyname = new String[sCountryDetails.get(currencyPosition).getCurrencyCode().length];
        } else if (type.equals("currency")) {
            title.setText(getResources().getString(R.string.selectcurrency));
            if (sessionManager.getCountryName2() != null) {
                String CountryName = sessionManager.getCountryName2();
                setCurrency(CountryName);
            }
        } else {
            setGender();
        }


       /* for (int i=0;i<currencyname.length;i++){
            currencyname[i]= sCountryDetails.get(i).getCurrencyCode().toString();
            Makent_model makent_model=new Makent_model();
            makent_model.setCountryName(currencyname[i]);
            countryList.add(makent_model);
        }*/

        alertDialog = new android.app.AlertDialog.Builder(PayoutBankDetailsActivity.this).setCustomTitle(view).setView(recyclerView1).setCancelable(true).show();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                String CountryName = sessionManager.getCountryName2();
                //String CountryName = localSharedPreferences.getSharedPreferences(Constants.StripeCountryCode);
                String Currencyname = sessionManager.getCurrencyName2();

                String Genders = sessionManager.getGender();
                if (CountryName != null && !CountryName.equals("")) {
                    setCurrency(CountryName);
                    payoutaddress_currency.setVisibility(View.VISIBLE);
                    payoutaddress_country.setText(CountryName);
                } else {
                    payoutaddress_currency.setVisibility(View.GONE);
                    payoutaddress_country.setText("");
                }

                if (Currencyname != null) {
                    payoutaddress_currency.setText(Currencyname);
                } else {
                    payoutaddress_currency.setText("");
                }


                if (Genders != null) {
                    gender.setText(Genders);
                } else {
                    gender.setText("");
                }

                String CountryCurrencyType = sessionManager.getCountryCurrencyType();


                if (CountryCurrencyType != null) {
                    if (CountryCurrencyType.equals("country")) {
                        payoutaddress_currency.setText("");
                    }
                }


                if (CountryName != "" && CountryName != null) {


                    if (CountryName.equals("Austria") || CountryName.equals("Belgium") || CountryName.equals("Denmark") || CountryName.equals("Finland") || CountryName.equals("France") || CountryName.equals("Germany") || CountryName.equals("Ireland") || CountryName.equals("Italy") || CountryName.equals("Luxembourg") || CountryName.equals("Norway") || CountryName.equals("Portugal") || CountryName.equals("Spain") || CountryName.equals("Sweden") || CountryName.equals("Switzerland") || CountryName.equals("Belgium") || CountryName.equals("Netherlands")) {

                        enableDefault();
                    } else if (CountryName.equals("Australia")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        bsb.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);

                    } else if (CountryName.equals("Canada")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        transitno.setVisibility(View.VISIBLE);
                        instituteno.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);


                    } else if (CountryName.equals("New Zealand")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        routing_number.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);


                    } else if (CountryName.equals("Singapore")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        bank_code.setVisibility(View.VISIBLE);
                        branch_code.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);

                    } else if (CountryName.equals("United Kingdom")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        sort_code.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);

                    } else if (CountryName.equals("United States")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        routing_number.setVisibility(View.VISIBLE);
                        ssn.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);

                    } else if (CountryName.equals("Hong Kong")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        clearing_code.setVisibility(View.VISIBLE);
                        branch_code.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);

                    } else if (CountryName.equals("Japan")) {
                        enableDefault();
                        Iban_no.setVisibility(View.GONE);
                        address1.setVisibility(View.GONE);
                        address2.setVisibility(View.GONE);
                        city.setVisibility(View.GONE);
                        state.setVisibility(View.GONE);
                        postalcode.setVisibility(View.GONE);

                        Ac_holder_name.setVisibility(View.VISIBLE);
                        bank_name.setVisibility(View.VISIBLE);
                        bank_code.setVisibility(View.VISIBLE);
                        branch_name.setVisibility(View.VISIBLE);
                        branch_code.setVisibility(View.VISIBLE);
                        Accountnumber.setVisibility(View.VISIBLE);
                        Ac_owner_name.setVisibility(View.VISIBLE);
                        ph_no.setVisibility(View.VISIBLE);
                        gender.setVisibility(View.VISIBLE);

                        addresskana_msg.setVisibility(View.VISIBLE);
                        addresskana_linear.setVisibility(View.VISIBLE);
                        addresskanji_msg.setVisibility(View.VISIBLE);
                        addresskanji_linear.setVisibility(View.VISIBLE);

                    } else {
                        payoutaddress_currency.setVisibility(View.VISIBLE);
                        Iban_no.setVisibility(View.VISIBLE);
                        Ac_holder_name.setVisibility(View.VISIBLE);
                        address1.setVisibility(View.VISIBLE);
                        address2.setVisibility(View.VISIBLE);
                        city.setVisibility(View.VISIBLE);
                        state.setVisibility(View.VISIBLE);
                        postalcode.setVisibility(View.VISIBLE);
                        legal_doc.setVisibility(View.VISIBLE);

                    }

                }
            }
        });


    }

    private void setGender() {
        for (int i = 0; i < genders.length; i++) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(i));
            listdata.setCountryName(genders[i]);
            countryList.add(listdata);
        }
        countryListAdapter.notifyDataChanged();
    }

    private void enableDefault() {


        Iban_no.setVisibility(View.VISIBLE);
        Ac_holder_name.setVisibility(View.VISIBLE);
        address1.setVisibility(View.VISIBLE);
        address2.setVisibility(View.VISIBLE);
        city.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        postalcode.setVisibility(View.VISIBLE);


        bsb.setVisibility(View.GONE);
        Accountnumber.setVisibility(View.GONE);
        transitno.setVisibility(View.GONE);
        instituteno.setVisibility(View.GONE);
        routing_number.setVisibility(View.GONE);
        ssn.setVisibility(View.GONE);
        clearing_code.setVisibility(View.GONE);
        bank_code.setVisibility(View.GONE);
        branch_code.setVisibility(View.GONE);
        sort_code.setVisibility(View.GONE);
        bank_name.setVisibility(View.GONE);
        branch_name.setVisibility(ViewPager.GONE);
        Ac_owner_name.setVisibility(View.GONE);
        ph_no.setVisibility(View.GONE);
        addresskana_msg.setVisibility(View.GONE);
        addresskanji_msg.setVisibility(View.GONE);
        addresskana_linear.setVisibility(View.GONE);
        addresskanji_linear.setVisibility(View.GONE);
        gender.setVisibility(View.GONE);


    }

    private void setCountry() {


        for (int i = 0; i < countryname.length; i++) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(i));
            listdata.setCountryName(countryname[i]);
            countryList.add(listdata);

        }
        countryListAdapter.notifyDataChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessionManager.setCountryName2("");
    }

    private void setCurrency(String CountryName) {

        if (CountryName.equals("Austria") || CountryName.equals("Belgium") || CountryName.equals("Denmark") || CountryName.equals("Finland") || CountryName.equals("France") || CountryName.equals("Germany") || CountryName.equals("Ireland") || CountryName.equals("Italy") || CountryName.equals("Luxembourg") || CountryName.equals("Norway") || CountryName.equals("Portugal") || CountryName.equals("Spain") || CountryName.equals("Sweden") || CountryName.equals("Switzerland") || CountryName.equals("Belgium") || CountryName.equals("Netherlands") || CountryName.equals("United Kingdom")) {
            for (int i = 0; i < currencyname.length; i++) {
                Makent_model listdata = new Makent_model();
                listdata.setCountryId(Integer.toString(i));
                listdata.setCountryName(currencyname[i]);
                countryList.add(listdata);

            }

            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("United States")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("USD");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("Australia")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("AUD");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("Canada")) {
            for (int i = 0; i < currencyname.length; i++) {
                Makent_model listdata = new Makent_model();
                listdata.setCountryId(Integer.toString(i));
                listdata.setCountryName(currencyname[i]);
                countryList.add(listdata);

            }

            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("New Zealand")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("NZD");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("Singapore")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("SGD");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("United States")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("USD");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("Hong Kong")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("HKD");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        } else if (CountryName.equals("Japan")) {
            Makent_model listdata = new Makent_model();
            listdata.setCountryId(Integer.toString(0));
            listdata.setCountryName("JPY");
            countryList.add(listdata);
            countryListAdapter.notifyDataChanged();
        }


    }

    /**
     * Api calling method based on country type
     *
     * @param imageObject hash Map Datas Based on Country Type
     */
    private void updateProfile(HashMap<String, String> imageObject) {

        commonMethods.showProgressDialog(this, customDialog);

        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);
        File file = null;
        try {
            file = new File(imagePath);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            if (imagePath.equals("") && CountryNames.equals("Other")) {

            } else {
                multipartBody.addFormDataPart("document", "IMG_" + timeStamp + ".jpg", RequestBody.create(MediaType.parse("image/png"), file));
            }

            for (String key : imageObject.keySet()) {
                CommonMethods.DebuggableLogI(key, imageObject.get(key));
                multipartBody.addFormDataPart(key, imageObject.get(key).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody formBody = multipartBody.build();
        apiService.uploadStripe(formBody, sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_UPLOAD_PAYOUT, this));

    }

    /**
     * Success Response For API
     *
     * @param jsonResp JsonResp FroM API
     * @param data     Request Data
     */
    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_GET_STRIPE:
                if (jsonResp.isSuccess()) {
                    onSuccessgetStripeList(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_UPLOAD_PAYOUT:
                if (jsonResp.isSuccess()) {
                    snackBar(jsonResp.getStatusMsg());

                    finish();
                } else {
                    snackBar(jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        System.out.println("Response checking error " + jsonResp + " " + data);
        commonMethods.hideProgressDialog();
        snackBar(data);
    }

    //Show network error and exception
    public void snackBar(String statusmessage) {
        // Create the Snackbar
        Snackbar snackbar = Snackbar.make(gender, "", Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        // Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar, null);
        // Configure the view

        RelativeLayout snackbar_background = (RelativeLayout) snackView.findViewById(R.id.snackbar);
        snackbar_background.setBackgroundColor(getResources().getColor(R.color.app_background));

        TextView actionButton =  snackView.findViewById(R.id.snack_button);
        actionButton.setVisibility(View.GONE);
        actionButton.setText(getResources().getString(R.string.showpassword));
        actionButton.setTextColor(getResources().getColor(R.color.app_background));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView textViewTop = (TextView) snackView.findViewById(R.id.snackbar_text);
        if (isInternetAvailable) {
            textViewTop.setText(statusmessage);
        } else {
            textViewTop.setText(getResources().getString(R.string.Interneterror));
        }

        // textViewTop.setTextSize(getResources().getDimension(R.dimen.midb));
        textViewTop.setTextColor(getResources().getColor(R.color.white));

// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.app_background));
        snackbar.show();

    }

    // Check network connection
    public ConnectionDetector getNetworkState() {
        ConnectionDetector connectionDetector = new ConnectionDetector(this);
        return connectionDetector;
    }

    private void onSuccessgetStripeList(JsonResponse jsonResponse) {
        stripeCountriesModel = gson.fromJson(jsonResponse.getStrResponse(), StripeCountriesModel.class);

        if (stripeCountriesModel.getCountryList() != null) {
            sCountryDetails.clear();
            sCountryDetails.addAll(stripeCountriesModel.getCountryList());

            countryname = new String[sCountryDetails.size()];
            countryID = new String[sCountryDetails.size()];
            countryCode = new String[sCountryDetails.size()];


            for (int i = 0; i < sCountryDetails.size(); i++) {

                countryname[i] = sCountryDetails.get(i).getCountryName();
                countryID[i] = String.valueOf(sCountryDetails.get(i).getCountryId());
                countryCode[i] = sCountryDetails.get(i).getCountryCode();

                Makent_model listdata = new Makent_model();
                listdata.setCountryId(countryID[i]);
                listdata.setCountryName(countryname[i]);
                listdata.setCountryCode(countryCode[i]);
                countryList.add(listdata);

            }

        }
    }

}
