package com.trioangle.goferdriver.home;
/**
 * @package com.trioangle.goferdriver.home
 * @subpackage home
 * @category RiderContactActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.firebaseChat.ActivityChat;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
                      RiderContactActivity
Its used to get RiderContactActivity details
*************************************************************** */
public class RiderContactActivity extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;

    public static final Integer CALL = 0x2;
    public @InjectView(R.id.mobilenumbertext)
    TextView mobilenumbertext;
    public @InjectView(R.id.ridername)
    TextView ridername;

    public @InjectView(R.id.ll_message)
    LinearLayout llMessage;

    @OnClick(R.id.back)
    public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.callbutton)
    public void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobilenumbertext.getText().toString()));
        startActivity(intent);
    }

    @OnClick(R.id.ll_message)
    public void startChatActivity(){
        startActivity(new Intent(this,ActivityChat.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_contact);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        ridername.setText(getIntent().getStringExtra("ridername"));
        mobilenumbertext.setText(getIntent().getStringExtra("riderno"));
        if(sessionManager.getBookingType().equals(CommonKeys.RideBookedType.manualBooking)){
            llMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }
}
