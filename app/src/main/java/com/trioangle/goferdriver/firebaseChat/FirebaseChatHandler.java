package com.trioangle.goferdriver.firebaseChat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;
import com.trioangle.goferdriver.util.CommonKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;



public class FirebaseChatHandler {

    public @Inject
    SessionManager sessionManager;

    private FirebaseChatHandlerInterface callbackListener;

    private DatabaseReference root;
    @CommonKeys.FirebaseChatserviceTriggeredFrom
    private int firebaseChatserviceTriggeredFrom;

    private Boolean isChatTriggerable= false;

    private int childCount = 0;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(firebaseChatserviceTriggeredFrom == CommonKeys.FirebaseChatserviceTriggeredFrom.backgroundService){

                if(isChatTriggerable){
                    callbackListener.pushMessage(dataSnapshot.getValue(FirebaseChatModelClass.class));
                }
            }else{
                callbackListener.pushMessage(dataSnapshot.getValue(FirebaseChatModelClass.class));
            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener valueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            isChatTriggerable = true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public FirebaseChatHandler(FirebaseChatHandlerInterface mCallbackListener,@CommonKeys.FirebaseChatserviceTriggeredFrom int firebaseChatserviceTriggeredFrom) {
        this.firebaseChatserviceTriggeredFrom = firebaseChatserviceTriggeredFrom;
        callbackListener = mCallbackListener;
        AppController.getAppComponent().inject(this);
        root = FirebaseDatabase.getInstance().getReference().child(CommonKeys.chatFirebaseDatabaseName).child(sessionManager.getTripId());


        if(firebaseChatserviceTriggeredFrom == CommonKeys.FirebaseChatserviceTriggeredFrom.backgroundService) {
            root.addListenerForSingleValueEvent(valueEventListener);
            root.addChildEventListener(childEventListener);
            //root.addValueEventListener(valueEventListener);
        }else if(firebaseChatserviceTriggeredFrom == CommonKeys.FirebaseChatserviceTriggeredFrom.chatActivity) {
            root.addChildEventListener(childEventListener);
        }


    }

    void addMessage(String message){
        try{
            if(!TextUtils.isEmpty(message)){
                Map<String,Object> map = new HashMap<String, Object>();
                String temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put(CommonKeys.FIREBASE_CHAT_MESSAGE_KEY,message);
                map2.put(CommonKeys.FIREBASE_CHAT_TYPE_KEY,CommonKeys.FIREBASE_CHAT_TYPE_DRIVER);

                message_root.updateChildren(map2);
            }}catch (Exception e){
            e.printStackTrace();
        }
    }

    void unRegister() {

        root.removeEventListener(childEventListener);
    }

    public static void deleteChatNode(String tripID){
        try{
            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(CommonKeys.chatFirebaseDatabaseName).child(tripID);
            root.removeValue();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface FirebaseChatHandlerInterface {
        void pushMessage(FirebaseChatModelClass firebaseChatModelClass);
    }
}
