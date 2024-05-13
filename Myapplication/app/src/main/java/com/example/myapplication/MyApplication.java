package com.example.realmapp;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.example.realmapp.models.RealmDynamics.spartaDynamics;

import sparta.realm.Realm;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;


public class MyApplication extends Application {

    private static Context baseContext;
    private static Context appContext;
    public static AppConfig RealmApp = null;
    public static String logTag = "Application";
    public static SyncOverride syncOverride;
    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;
    public static float batteryPct;
    AppConfig liveAppConfig;
    String faceKey;
    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        baseContext = getBaseContext();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        faceKey = "ihdBXU3FcZAawN2qayK9PG3kGz1BocN0EjHOe6hn2LhCubiwJYP7XsbIxildd0hfE9Tio36fGMdwoH4kC0HJNjzs5GbdWchRPmn5O/omstCi37+w7VNFkOgWxDhQSiDn4Apb77g0FwoNvyhVgE7lBx9DxcSnqvniTyKidXlHCak=";

        RealmApp = new AppConfig("https://tabiztest.cs4africa.com/selfservice",
                null,
                "Capture Solutions",
                "TEST ACCOUNT",
                "/Authentication/Login/Submit", false

        );
        liveAppConfig = RealmApp;
        liveAppConfig.app_folder_path = Environment.getExternalStorageDirectory().toString()+"/Realm" + BuildConfig.APPLICATION_ID+"/";
        liveAppConfig.databaseFolder = Environment.getExternalStorageDirectory().toString() + "/Realm/" + BuildConfig.APPLICATION_ID + "/.DB/";
        liveAppConfig.crashReportsFolder = Environment.getExternalStorageDirectory().toString() + "/Realm/" + BuildConfig.APPLICATION_ID + "/.CrashReports/";
        liveAppConfig.logsFolder = Environment.getExternalStorageDirectory().toString() + "/Realm/" + BuildConfig.APPLICATION_ID + "/.Logs/";
        liveAppConfig.appDataFolder = Environment.getExternalStorageDirectory().toString() + "/Realm/" + BuildConfig.APPLICATION_ID + "/.RAW_APP_DATA/";
        liveAppConfig.file_path_employee_data = Environment.getExternalStorageDirectory().toString() + "/Realm/" + BuildConfig.APPLICATION_ID + "/.RAW_APP_DATA/";

//        Realm.Initialize(this, new spartaDynamics(), BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID, liveAppConfig,
//                "ihdBXU3FcZAawN2qayK9PG3kGz1BocN0EjHOe6hn2LhCubiwJYP7XsbIxildd0hfE9Tio36fGMdwoH4kC0HJNjzs5GbdWchRPmn5O/omstCi37+w7VNFkOgWxDhQSiDn4Apb77g0FwoNvyhVgE7lBx9DxcSnqvniTyKidXlHCak=");


        Realm.Initialize(this, new spartaDynamics(), BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID, liveAppConfig,faceKey);
        Log.e("iniiiiit","after iniiiit");

        svars.set_sync_interval_mins(this, 30);
//        setupAlarm();
    }

    public static boolean isServiceRunning(Context act,Class<?> serviceClass) {
//        Class<?> serviceClass=App_updates.class;

        ActivityManager manager;
        manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);p
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    void setupAlarm(Context context)
//    {
//        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        // service to be fired every 15 seconds
//        intent = new Intent(context, PatrolCallService.class);
//        pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        setNextAlarm(10000);
//    }
   public  void setNextAlarm(int durationInMills)
    {
        alarmMgr.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + durationInMills), pendingIntent);

    }


    public static Context getAppContext() {
        return appContext;
    }
}
