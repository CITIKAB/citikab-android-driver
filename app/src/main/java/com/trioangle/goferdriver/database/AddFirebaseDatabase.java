package com.trioangle.goferdriver.database;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trioangle.goferdriver.configs.SessionManager;
import com.trioangle.goferdriver.network.AppController;

import javax.inject.Inject;

import static com.trioangle.goferdriver.util.CommonMethods.DebuggableLogE;

public class AddFirebaseDatabase {
    @Inject
    SessionManager sessionManager;

    private DatabaseReference mFirebaseDatabase;
    private ValueEventListener mSearchedDriverReferenceListener;
    private Query query;
    private String TAG = "Android_Debug";
    private IFirebaseReqListener firebaseReqListener;

    public AddFirebaseDatabase() {
        AppController.getAppComponent().inject(this);
    }

    public void updateRequestTable(String riderId,String tripId) {
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(FirebaseDbKeys.Rider);
        mFirebaseDatabase.child(riderId).child(FirebaseDbKeys.TripId).setValue(tripId);
        query = mFirebaseDatabase.child(riderId);
    }

    public void removeRequestTable() {
        mFirebaseDatabase.getDatabase().getReference(FirebaseDbKeys.Rider).removeValue();
        query.removeEventListener(mSearchedDriverReferenceListener);
        mFirebaseDatabase.removeEventListener(mSearchedDriverReferenceListener);
        mSearchedDriverReferenceListener = null;
    }

    private void addRequestChangeListener() {
        mSearchedDriverReferenceListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DebuggableLogE(TAG, "Database Updated Successfully");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                DebuggableLogE(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
