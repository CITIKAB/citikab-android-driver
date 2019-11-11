package com.trioangle.goferdriver.helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trioangle.goferdriver.util.CommonKeys;


public class FirebaseDatabaseAdder {
    private DatabaseReference mFirebaseDatabase;

    private ValueEventListener mDataUpateListners;

    String dbNodeName;
    int type;
    String data;
    String tripid;

    public FirebaseDatabaseAdder(String dbNodeName, int type, String data,String tripid) {
        this.dbNodeName = dbNodeName;
        this.type = type;
        this.data = data;
        this.tripid = tripid;
        addDataToDb();
    }

    private void addDataToDb(){
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(dbNodeName);
        if (type==1){
            mFirebaseDatabase.child(data).child(CommonKeys.TripId).setValue(tripid);
        }

        if (mDataUpateListners == null) {
            addLatLngChangeListener(); // Get Driver Lat Lng
        }
    }

    private void addLatLngChangeListener() {

        // User data change listener
        final Query query = mFirebaseDatabase.child(data);

        mDataUpateListners = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (data != null) {
                    System.out.println("DataBase Created");

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

}
