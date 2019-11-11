package com.trioangle.goferdriver.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.widget.TextView;

import com.trioangle.goferdriver.MainActivity;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.util.CommonKeys;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ManualBookingDialog extends Activity {

    public @InjectView(R.id.tv_manual_booking_status_header)
    TextView tvManualBookingStatus;
    public @InjectView(R.id.tv_rider_name)
    TextView tvRiderName;
    public @InjectView(R.id.tv_rider_contact_number)
    TextView tvRiderContactNumber;
    public @InjectView(R.id.tv_rider_pickup_location)
    TextView tvRiderPickupLocation;
    public @InjectView(R.id.tv_rider_pickup_time)
    TextView tvRiderPickupDateAndTime;
    int type = 0;
    String riderName = "", riderContactNumber = "*****", riderPickupLocation = "", riderPickupDateAndTime = "";
    MediaPlayer mPlayer;

    @OnClick(R.id.btn_manual_booking_ok)
    public void okButtonPressed() {
        mPlayer.release();
        if (type == CommonKeys.ManualBookingPopupType.cancel) {
            Intent requestaccept = new Intent(getApplicationContext(), MainActivity.class);
            requestaccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(requestaccept);
        }
        this.finish();

    }


    @OnClick(R.id.cv_rider_contact_number)
    public void contactCardPressed() {
        try {
            if (type != CommonKeys.ManualBookingPopupType.cancel) {
                String uri = "tel:" + tvRiderContactNumber.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setWindowAnimations(R.style.activity_popup_animation);
        setContentView(R.layout.activity_manual_booking_dialog);
        ButterKnife.inject(this);
        this.setFinishOnTouchOutside(false);


        playNotificationSoundAndVibrate();
        try {
            type = getIntent().getIntExtra(CommonKeys.KEY_TYPE, CommonKeys.ManualBookingPopupType.cancel);
            switch (type) {
                case CommonKeys.ManualBookingPopupType.bookedInfo: {
                    tvManualBookingStatus.setText(getString(R.string.manually_booked));
                    break;
                }
                case CommonKeys.ManualBookingPopupType.reminder: {
                    tvManualBookingStatus.setText(getString(R.string.manual_booking_reminder));
                    break;
                }
                case CommonKeys.ManualBookingPopupType.cancel: {
                    tvManualBookingStatus.setText(getString(R.string.manual_booking_cancelled));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            riderName = getIntent().getStringExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (type != CommonKeys.ManualBookingPopupType.cancel) {
                riderContactNumber = getIntent().getStringExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_CONTACT_NUMBER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            riderPickupLocation = getIntent().getStringExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_PICKU_LOCATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            riderPickupDateAndTime = getIntent().getStringExtra(CommonKeys.KEY_MANUAL_BOOKED_RIDER_PICKU_DATE_AND_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            tvRiderName.setText(riderName);
            tvRiderContactNumber.setText(riderContactNumber);
            tvRiderPickupLocation.setText(riderPickupLocation);
            tvRiderPickupDateAndTime.setText(riderPickupDateAndTime);
        } catch (Exception e) {

        }


    }

    private void playNotificationSoundAndVibrate() {
        try {
            mPlayer = MediaPlayer.create(this, R.raw.manual_booking_notification_sound);
            mPlayer.start();


        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (v != null) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                //deprecated in API 26
                assert v != null;
                v.vibrate(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
