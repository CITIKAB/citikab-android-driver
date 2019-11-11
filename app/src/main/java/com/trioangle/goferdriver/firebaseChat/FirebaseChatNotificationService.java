package com.trioangle.goferdriver.firebaseChat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.pushnotification.NotificationUtils;
import com.trioangle.goferdriver.util.CommonKeys;

import javax.inject.Inject;

import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogD;

public class FirebaseChatNotificationService extends Service implements FirebaseChatHandler.FirebaseChatHandlerInterface {

    FirebaseChatHandler firebaseChatHandler;
    NotificationUtils notificationUtils;

    public FirebaseChatNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DebuggableLogD("chat handler notification", "started");
        // initializing firebaseChatHandler
        firebaseChatHandler = new FirebaseChatHandler(this,CommonKeys.FirebaseChatserviceTriggeredFrom.backgroundService);
        notificationUtils = new NotificationUtils(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseChatHandler.unRegister();
    }

    @Override
    public void pushMessage(FirebaseChatModelClass firebaseChatModelClass) {
        if(firebaseChatModelClass.type.equals(CommonKeys.FIREBASE_CHAT_TYPE_RIDER))
        notificationUtils.generateFirebaseChatNotification(this,firebaseChatModelClass.message);
        DebuggableLogD("rider message", firebaseChatModelClass.message);
    }
}
