package com.example.realmapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luxand.FSDK;
import com.realm.annotations.sync_service_description;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.Services.SynchronizationManager;
import sparta.realm.spartautils.svars;


public class SyncOverride implements SynchronizationManager.SynchronizationHandler {
    public SynchronizationManager sm;
    String logTag = "SyncOverride";

    public SyncOverride() {
        sm = new SynchronizationManager();
        sm.OverrideSynchronization(this);
//        sm.setSynchronizationStatusHandler(this);
        sm.InitialiseAutosync();

    }
    public void setupsyncstatusreport(SynchronizationManager.SynchronizationStatusHandler ssd) {
        sm.setSynchronizationStatusHandler(ssd);
    }
    public void sync() {
        sm.sync_now();
        Log.e("sync", "=> synceeeeeeeed");
    }

    boolean done = false;

    @Override
    public ArrayList<JSONObject> OnAboutToUploadObjects(sync_service_description ssd, ArrayList<JSONObject> objects) {


        return objects;
    }
    @Override
    public JSONObject OnUploadingObject(sync_service_description ssd, JSONObject object) {


        return object;
    }
    @Override
    public JSONObject OnUploadedObject(sync_service_description ssd, JSONObject object, JSONObject response) {


        return response;
    }
    @Override
    public JSONObject OnAboutToDownload(sync_service_description ssd, JSONObject filter) {


        return filter;
    }
    @Override
    public JSONObject OnDownloadedObject(sync_service_description ssd, JSONObject object, JSONObject response) {


        return response;
    }
    @Override
    public JSONObject OnAuthenticated(String token, JSONObject response) {
        return SynchronizationManager.SynchronizationHandler.super.OnAuthenticated(token, response);
    }
}
