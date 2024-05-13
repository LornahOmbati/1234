package com.example.secucapture.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.secucapture.R;
import com.example.secucapture.Services.MyServiceIntent;
import com.example.secucapture.Utils.AESHelper;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity {

    public static Activity act;
    public static Context ctx;
    public static DbHelper doa;
    static ProgressDialog pd;
    static ProgressDialog pd2;
    public static TextView sync;

    ImageView driver_reg_img,vehicle_assess_img, vehicle_reg_img, driver_assess_img;

    boolean is_medical_approved = false;
    String vehicle_no_input = null;
    final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH};

    static AESHelper aesHelper;

    int lessons_count = 0;

    static Timer syncTimer = null;

    public static void stopPD() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        act = this;
        ctx = this;
        doa = new DbHelper(ctx, act);
        pd = new ProgressDialog(ctx);
        pd2 = new ProgressDialog(ctx);
        aesHelper = new AESHelper(getApplicationContext());

        String version_code = getApplicationContext().getSharedPreferences("com.example.safety_cap", Context.MODE_PRIVATE).getString("version_code", "0");
        String current_version = aesHelper.getAppVersion(getApplicationContext());

        if(version_code.equals(current_version)){
            //db is up to date
            Log.v("DB",current_version+"-NO UPDATES - VERSION - "+version_code);

            doa = new DbHelper(getApplicationContext(), this);

            //check db version in database
            String version_code_db  = doa.getVersionCode();
            if(!version_code.equals(version_code_db)){
                doa = new DbHelper(getApplicationContext());
                doa.updateDBAppVersion(version_code);
            }
        } else {
            //perform db updates
            Log.v("DB",current_version+" -UPDATED - VERSION - "+version_code);
            doa = new DbHelper(getApplicationContext());
        }
        createFiles();

        lessons_count = doa.countLessonsQuizes();
        if (lessons_count == 0){
            _dialogSync();
        }

        sync = findViewById(R.id.sync);
        driver_reg_img = findViewById(R.id.driver_reg_img);
        driver_assess_img = findViewById(R.id.driver_assess_img);
        vehicle_reg_img = findViewById(R.id.vehicle_reg_img);
        vehicle_assess_img = findViewById(R.id.vehicle_assess_img);

        driver_reg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DashboardActivity.this, DriverRegistrationActivity.class);
                startActivity(intent);
            }
        });

        driver_assess_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DashboardActivity.this, DriverAssessmentActivity.class);
                startActivity(intent);
            }
        });

        vehicle_reg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DashboardActivity.this, VehicleRegistrationActivity.class);
                startActivity(intent);
            }
        });

        vehicle_assess_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DashboardActivity.this, VehicleAssessmentActivity.class);
                startActivity(intent);
            }
        });

        sync.setText("SYNC("+doa.countSyncPending()+")");
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage(getResources().getString(R.string.wait));
//                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCanceledOnTouchOutside(true);
                pd.show();

                GlobalVariables.service_start_mode = "manual";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getApplicationContext(), MyServiceIntent.class));
                } else {
                    startService(new Intent(getApplicationContext(), MyServiceIntent.class));
                }

            }
        });

    }

    void _dialogSync(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view_ = layoutInflaterAndroid.inflate(R.layout.promt_to_sync_dialog, null);
        builder.setView(view_);
        builder.setCancelable(false);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView firstTimeSync = (TextView) view_.findViewById(R.id.firstTimeSync);
        firstTimeSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd.setMessage(getResources().getString(R.string.wait));
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCanceledOnTouchOutside(true);
                pd.show();

                GlobalVariables.service_start_mode = "manual";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getApplicationContext(), MyServiceIntent.class));
                } else {
                    startService(new Intent(getApplicationContext(), MyServiceIntent.class));
                }

                alertDialog.dismiss();
            }
        });
    }

    public static void stopPD(String message) {
        if(!message.equalsIgnoreCase("unauthorized"))
        {
            Toast.makeText(act, message, Toast.LENGTH_LONG).show();
        }

        GlobalVariables.the_service_start = "stop";

        if(pd.isShowing())
        {
            pd.dismiss();
        }

        sync.setText("SYNC("+doa.countSyncPending()+")");

    }
    private void createFiles() {
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(act, permissions, 10);
        }
        else {
            try
            {
                ArrayList<String> files_needed = new ArrayList<>();

                files_needed.add(GlobalVariables.pics_file_path);
                files_needed.add(GlobalVariables.trucks_pics_file_path);
                files_needed.add(GlobalVariables.trucks_pics_file_path);

                for (String file : files_needed) {
                    File file_q = new File(file);
                    if (!file_q.exists()) {
                        Log.e("Creating data dir=>", "" + String.valueOf(file_q.mkdirs()));
                    } else {
                        Log.e("data dir exists=>", "" + file_q);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.e("dir not created err=>", "" + ex.getMessage());
            }

        }
    }

    void _showTypeIDdialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view_ = layoutInflaterAndroid.inflate(R.layout.type_id_no_dialog, null);
        builder.setView(view_);
        builder.setCancelable(true);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        EditText edt_id_no_dialog = (EditText) view_.findViewById(R.id.edt_id_no_dialog);
        TextView id_no_txt = (TextView) view_.findViewById(R.id.id_no_txt);
        Button nobutton = (Button) view_.findViewById(R.id.nobutton);
        Button yesButton = (Button) view_.findViewById(R.id.yesButton);

        edt_id_no_dialog.setInputType(InputType.TYPE_CLASS_NUMBER);
        id_no_txt.setText("ID No.");

        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }
        });


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_id_no_dialog.getText().toString().trim().isEmpty() || edt_id_no_dialog.getText().toString().equalsIgnoreCase("null") || edt_id_no_dialog.getText().toString().equalsIgnoreCase("0")){
                    edt_id_no_dialog.setError("Required");
                    edt_id_no_dialog.requestFocus();
                    return;
                }

                //provide function to check if is medical approved
                try {
                    String id_input = edt_id_no_dialog.getText().toString().trim();
                    String id_in_db = doa.checkIfIdExists(id_input);

                    is_medical_approved = doa.checkMedicalStatus(id_input);

                    if (is_medical_approved){ //has been approved

                        String fullname = doa.getFullName(edt_id_no_dialog.getText().toString().trim());
                        GlobalVariables.fullname = fullname;
                        GlobalVariables.id_no = edt_id_no_dialog.getText().toString().trim();
                        startActivity(new Intent(getApplicationContext(), DriverAssessmentActivity.class));
                        alertDialog.dismiss();

                    } else if(!id_input.equalsIgnoreCase(id_in_db)) { //id number not in system

                        alertDialog.dismiss();
                        _showIdNotRegisteredDialog();

                    } else {

                        alertDialog.dismiss();
                        _showNotApproveddialog();


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    void _showNotApproveddialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view_ = layoutInflaterAndroid.inflate(R.layout.not_approved_dialog, null);
        builder.setView(view_);
        builder.setCancelable(true);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView warning_text = (TextView) view_.findViewById(R.id.warning_text);
        Button exitButton = (Button) view_.findViewById(R.id.exitButton);

        if (GlobalVariables.methodTo.equalsIgnoreCase("VEHICLE ASSESSMENT")){

            warning_text.setText("VEHICLE NOT REGISTERED");

        } else if(GlobalVariables.methodTo.equalsIgnoreCase("DRIVER ASSESSMENT")){

            warning_text.setText("MEDICAL TEST NOT APPROVED\n CONTACT ADMIN");

        } else {

        }

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }
        });

    }

    void _showIdNotRegisteredDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DashboardActivity.this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view_ = layoutInflaterAndroid.inflate(R.layout.id_not_registered, null);
        builder.setView(view_);
        builder.setCancelable(true);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button exitButton_ = (Button) view_.findViewById(R.id.exitButton_);

        exitButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }
        });

    }

    private void InitialiseAutoSync() {
        if (syncTimer != null) {
            return;
        }

        syncTimer = new Timer();
        syncTimer.schedule(new TimerTask() {
            public void run() {
                if (doa.sync_period() >= (GlobalVariables.sync_interval_mins - 1) || doa.sync_period() < 0) {

                    Log.v("SYNC","STARTING AUTOSYNC");

                    GlobalVariables.service_start_mode = "manual";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(getApplicationContext(), MyServiceIntent.class));
                    } else {
                        startService(new Intent(getApplicationContext(), MyServiceIntent.class));
                    }

                }
            }
        }, 1000, GlobalVariables.sync_interval_mins * 30000);

    }

    public static void stopPD2(String message) {
        if(!message.equalsIgnoreCase("unauthorized"))
        {
            Toast.makeText(act, message, Toast.LENGTH_LONG).show();
        }

        GlobalVariables.the_service_start = "stop";

        if(pd2.isShowing()) {
            pd2.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sync.setText("SYNC("+doa.countSyncPending()+")");

    }


}