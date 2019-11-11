package com.trioangle.goferdriver.profile;
/**
 * @package com.trioangle.goferdriver.profile
 * @subpackage profile model
 * @category VehiclInformation
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.network.AppController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                VehiclInformation
Its used to view the vehicle information details
*************************************************************** */
public class VehiclInformation extends AppCompatActivity {

    public @InjectView(R.id.carname)
    TextView carname;
    public @InjectView(R.id.carnumber)
    TextView carnumber;
    public @InjectView(R.id.cartype)
    TextView cartype;
    public @InjectView(R.id.tv_company)
    TextView tvCompany;
    public @InjectView(R.id.rl_company_name)
    RelativeLayout rlCompanyName;


    public @InjectView(R.id.pb_loader)
    ProgressBar pbLoader;

    public @InjectView(R.id.carimage)
    ImageView carImage;
    private String companyName;
    private int companyId;

    @OnClick(R.id.back_lay)
    public void onpBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicl_information);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


                /*
                *  Driver vehicle information
                */
        carname.setText(getIntent().getStringExtra("vehiclename"));
        carnumber.setText(getIntent().getStringExtra("vehiclenumber"));
        cartype.setText(getIntent().getStringExtra("car_type"));
        companyName = getIntent().getStringExtra("companyname");
        companyId = getIntent().getIntExtra("companyid",1);


        if(companyName!=null&&!companyName.equals("")&&companyId > 1){
            tvCompany.setText(companyName);
            rlCompanyName.setVisibility(View.VISIBLE);
        }else{
            tvCompany.setText("");
            rlCompanyName.setVisibility(View.GONE);
        }


        Picasso.with(this).load(getIntent().getStringExtra("car_image")).error(R.drawable.car).into(carImage, new Callback() {
            @Override
            public void onSuccess() {
                pbLoader.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                pbLoader.setVisibility(View.GONE);
            }
        });

    }
}
