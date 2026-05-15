package com.example.stylematchia;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class StyleMatchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignored) {
        }
    }
}
