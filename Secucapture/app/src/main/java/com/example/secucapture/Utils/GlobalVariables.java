package com.example.secucapture.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.example.secucapture.models.lessonsQuizesModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlobalVariables {

    public static final String pics_file_path = Environment.getExternalStorageDirectory().toString() + "/.WC/SecuCapturePhotos/";

    //public static final String pics_file_path = Environment.getExternalStorageDirectory().toString() + "/.WC/SecuCapturePhotos/";
    public static final String  id_pics = pics_file_path + "/SecuCaptureIdPics";

    public static final String  license_pics = pics_file_path + "/SecuCaptureLicensePics";

    public static final String drivers_pics_file_path = pics_file_path + "DriversPics/";
    public static final String trucks_pics_file_path = pics_file_path + "TrucksPics/";

    public static boolean is_encrypted = false;

    public static String code = "secucapture";

    public static String Log_file_name = "logs.cvtio";

    public static boolean is_passwordhashing = true;

    public static String userid;

    public static String surl = "https://cs01test.cs4africa.com/etsecu/";

    public static final String sharedprefsname = "com.example.secucapture";

    public static String is_pps = "no";

    public static String id_no;

    public static String methodTo;

    public static String fullname;

    public static String service_start_mode;
    public static String the_service_start;

    public static String safety_sync_service = "all";

    public static int sync_interval_mins = 30;

    public static String lesson_id;

    public static List<lessonsQuizesModel> questions;

    public static void setSafety_sync_service(String safety_sync_service) {

        GlobalVariables.safety_sync_service = safety_sync_service;
    }

    public static String getSafety_sync_service() {
        return safety_sync_service;
    }


    public static String sync_time(Context act) {
        SharedPreferences prefs = act.getSharedPreferences(GlobalVariables.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("sync_time_var", null);
    }
    public static String logDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return dateFormat.format(new Date());
    }
    public static String timeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static String dateNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }
}
