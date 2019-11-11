package com.trioangle.goferdriver.firebaseChat;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.goferdriver.R;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.interfaces.ApiService;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.pushnotification.NotificationUtils;
import com.trioangle.goferdriver.util.CommonKeys;
import com.trioangle.goferdriver.util.CommonMethods;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.goferdriver.util.CommonMethods.startFirebaseChatListenerService;
import static com.trioangle.goferdriver.util.CommonMethods.stopFirebaseChatListenerService;

public class ActivityChat extends AppCompatActivity implements FirebaseChatHandler.FirebaseChatHandlerInterface {

    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    Gson gson;

    public @InjectView(R.id.imgvu_rider_profile)
    ImageView riderProfileImageView;

    public @InjectView(R.id.tv_profile_name)
    TextView profileName;

    public @InjectView(R.id.tv_profile_rating)
    TextView profileRating;

    public @InjectView(R.id.edt_new_msg)
    EditText newMessage;

    public @InjectView(R.id.rv_chat)
    RecyclerView rv;

    public @InjectView(R.id.imgvu_emptychat)
    ImageView noChats;

    @OnClick(R.id.imgvu_back)
    public void backIconClickEvent() {
        onBackPressed();
    }

    AdapterFirebaseRecylcerview adapterFirebaseRecylcerview;
    FirebaseChatHandler firebaseChatHandler;
    int sourceActivityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        sourceActivityCode = getIntent().getIntExtra(CommonKeys.FIREBASE_CHAT_ACTIVITY_SOURCE_ACTIVITY_TYPE_CODE, CommonKeys.FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_NOTIFICATION);

        updateRiderProfileOnHeader();
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapterFirebaseRecylcerview = new AdapterFirebaseRecylcerview(this);
        rv.setAdapter(adapterFirebaseRecylcerview);
        firebaseChatHandler = new FirebaseChatHandler(this,CommonKeys.FirebaseChatserviceTriggeredFrom.chatActivity);
        rv.setVisibility(View.GONE);
        noChats.setVisibility(View.VISIBLE);


    }

    @Override
    public void pushMessage(FirebaseChatModelClass firebaseChatModelClass) {
        adapterFirebaseRecylcerview.updateChat(firebaseChatModelClass);
        rv.scrollToPosition(adapterFirebaseRecylcerview.getItemCount() - 1);
        rv.setVisibility(View.VISIBLE);
        noChats.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_send)
    public void sendMessage() {
        firebaseChatHandler.addMessage(newMessage.getText().toString().trim());
        newMessage.getText().clear();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseChatHandler.unRegister();
    }

    @Override
    public void onBackPressed() {
        /*if (sourceActivityCode == CommonKeys.FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_RIDER_OR_DRIVER_PROFILE) {
            super.onBackPressed();
        } else {
            CommonMethods.gotoMainActivityFromChatActivity(this);
        }*/
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopFirebaseChatListenerService(this);
        NotificationUtils.clearNotifications(this);
    }

    @Override
    protected void onPause() {

        firebaseChatHandler.unRegister();
        if(!TextUtils.isEmpty(sessionManager.getTripId()) && sessionManager.isDriverAndRiderAbleToChat()){
            startFirebaseChatListenerService(this);
        }
        super.onPause();
    }

    private void updateRiderProfileOnHeader() {
        String riderProfilePic = sessionManager.getRiderProfilePic(), riderName = sessionManager.getRiderName(), riderRating = sessionManager.getRiderRating();
        if (!TextUtils.isEmpty(riderProfilePic)) {
            Picasso.with(getApplicationContext()).load(sessionManager.getRiderProfilePic()).error(R.drawable.car)
                    .into(riderProfileImageView);
        }

        if (!TextUtils.isEmpty(riderName)) {
            profileName.setText(riderName);
        }else{
        profileName.setText(getResources().getString(R.string.rider));
        }



        if (!sessionManager.getRiderRating().equals("")) {
            profileRating.setText(sessionManager.getRiderRating());
        } else {
            profileRating.setVisibility(View.GONE);
        }
    }
}
