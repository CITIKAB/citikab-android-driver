package com.trioangle.goferdriver.firebaseChat;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class FirebaseChatModelClass {
    public String type;
    public String message;


    FirebaseChatModelClass(String message, String type) {
        this.type = type;
        this.message = message;
    }

    public FirebaseChatModelClass() {
    }
}
