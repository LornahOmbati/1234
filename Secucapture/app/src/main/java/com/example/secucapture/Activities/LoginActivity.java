package com.example.secucapture.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.secucapture.R;
import com.example.secucapture.Services.MyServiceLogin;
import com.example.secucapture.Utils.AESHelper;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;
import com.example.secucapture.models.users;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static Activity act;
    public static Context ctx;

    CheckBox checkbox_password;
    DbHelper doa;
    static AESHelper aesHelper;

    Boolean UserRegData, checkuser;

    public EditText username_edt, password_edt;
    public TextView txtversioncode;

    public Button login_btn;

    public static String user1;
    public static String password1;
    private static ProgressDialog pd;
    private Boolean login_success = false;

    users usER = new users();

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;
        ctx = this;
        aesHelper = new AESHelper(getApplicationContext());
        pd = new ProgressDialog(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
        }

        checkPermissions();
        checkAndroid11StoragePermision();

        username_edt = (EditText) findViewById(R.id.username_edt);
        password_edt = (EditText) findViewById(R.id.password_edt);
        login_btn = (Button) findViewById(R.id.login_btn);
        txtversioncode = findViewById(R.id.txtversioncode);
        checkbox_password = findViewById(R.id.checkbox_password);

        /***launch db and check if needs updates**/
        String version_code = getApplicationContext().getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE).getString("version_code", "0");
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

        //code for checking the show password checkbox and un hiding the password
        password_edt.setTransformationMethod(new PasswordTransformationMethod());

        checkbox_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle password visibility based on CheckBox state
                if (isChecked) {
                    // Show password
                    password_edt.setTransformationMethod(null);
                } else {
                    // Hide password
                    password_edt.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        //pre_save logged in user
        usER = doa.getUserLoggedIn();
        username_edt.setText(""+usER.getUsername());

        //version control
        PackageInfo pinfo = null;
        try {
            pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        txtversioncode.setText("V"+versionName);
        /***-----------------------------------**/

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(version_code.equals(current_version)){
            //db is up to date
            Log.v("DB",current_version+"-NO UPDATES - VERSION - "+version_code);

            doa = new DbHelper(getApplicationContext(), this);

            //check db version in database
            String version_code_db  = doa.getVersionCode();
            if(!version_code.equals(version_code_db)){
                doa = new DbHelper(getApplicationContext());
                doa.updateDBAppVersion(version_code);

                // Save the updated version code in SharedPreferences
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("version_code", version_code); // Update version code
                editor.apply(); // Save changes
            }
        } else {
            //perform db updates
            Log.v("DB",current_version+" -UPDATED - VERSION - "+version_code);
            doa = new DbHelper(getApplicationContext());
        }


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!verified()){
                    return;
                }

                user1 = username_edt.getText().toString().trim();
                password1 = password_edt.getText().toString().trim();

                doa.log_event(act,"User Tries Auth with "+user1+" | "+password1);

                try {
                    if (GlobalVariables.is_passwordhashing){
                        if (checkPermissions() && checkAndroid11StoragePermision()){

                            //the new password hashing
                            Auth_User_Details(user1, doa.encrypt(password1));

                        }
                    } else {
                        if (checkPermissions() && checkAndroid11StoragePermision()){

                            //Old password encryption
                            Auth_User_Details(user1, doa.EncryptTextMD5(password1));

                        }
                    }
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }



                if (user1.equals("") || password1.equals("")) { //checks if any of the edt are empty
                    Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();}
                checkuser = doa.checkusername(user1);
                if(!checkuser) {//check if user exists in the database.
                    UserRegData = doa.UserRegInputData(user1, password1);
                    if (UserRegData) {//if the new details are able to be inserted
                        Toast.makeText(LoginActivity.this, "submitted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "submission failed", Toast.LENGTH_SHORT).show();
                    }
                }else {//if user already exists
                    Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    boolean verified(){
        boolean valid = true;

        if (username_edt.getText().toString().trim().isEmpty() || username_edt.getText().toString().equalsIgnoreCase("null")){
            username_edt.setError("Required");
            username_edt.requestFocus();
            valid = false;
        }

        if (password_edt.getText().toString().trim().isEmpty() || password_edt.getText().toString().equalsIgnoreCase("null")){
            password_edt.setError("Required");
            password_edt.requestFocus();
            valid = false;
        }

        return valid;
    }
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }
    private boolean checkAndroid11StoragePermision(){
        try {
            if(Build.VERSION.SDK_INT >= 30) {
                if (Environment.isExternalStorageManager()){
                    return true;
                }else{
                    Toast.makeText(this, "Allow App Storage Permission",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    return false;
                }
            }
        }catch (Exception ex){

        }

        return true;
    }

    private void Auth_User_Details(String un, String pwd) {

        if (getUsers(un, pwd) == true) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_ = sharedPref.edit();
            editor_ = sharedPref.edit();
            editor_.putString("pass", password_edt.getText().toString());
            editor_.putString("user_model", username_edt.getText().toString());

            editor_.apply();

            doa.log_event(act,"User locally authenticated");

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        } else {
            firstTimeLogin();
        }
    }

    public void firstTimeLogin(){
        Log.v("JJJJJHH", "JHHHHHHHHH firstTimeLogin");

        pd.setMessage("Please wait......");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        users userModel = new users();
        userModel.setUsername(user1);
        userModel.setPassword(password1);

        Intent is1 = new Intent(LoginActivity.this, MyServiceLogin.class);
        startService(is1);
    }

    private Boolean getUsers(String username, String passcode) {

        login_success = false;
        if(aesHelper.getkey2()==null || aesHelper.getkey2().equalsIgnoreCase("")){
            firstTimeLogin();
            return false;
        }else {
            login_success = doa.loginUser(username, passcode);
        }

        return login_success;
    }

    public static void stopspinning1(){

        pd.dismiss();
    }

    public static void stopPD()
    {
        pd.dismiss();

        Intent it_login;
        it_login = new Intent(ctx, LoginActivity.class);

        act.startActivity(it_login);
    }
}