package com.example.mytest;

import android.app.Application;
import android.content.Context;

import sparta.realm.spartautils.svars_;

public class MyApp extends Application {

    Context my_application_context;

    @Override
    public void onCreate() {
        super.onCreate();
        svars_.SPARTA_APP UIPA_APP = new svars_.SPARTA_APP("https://cs01test.cs4africa.com/etsecu/",
                "https://cs01test.cs4africa.com/etsecu/SafetyCapture/",
                "U.I.P.A",
                "MAIN CAMPUS",
                "/Authentication/Login/Submit", false);

    }

}





