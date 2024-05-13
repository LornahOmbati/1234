package com.example.secucapture.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.secucapture.models.lessonsModel;
import com.example.secucapture.models.lessonsQuizesModel;
import com.example.secucapture.models.quizesAnswersModel;
import com.example.secucapture.models.users;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class DbHelper {

    @SuppressLint("StaticFieldLeak")
    static Context ctx;

    static String TAG = DbHelper.class.getSimpleName();
    static Context act;
    public static boolean loadeddb = false;
    public static SQLiteDatabase db = null;

    public DbHelper(Context ct) {
        DbHelper.ctx = ct;
        net.sqlcipher.database.SQLiteDatabase.loadLibs(ctx.getApplicationContext());

        //try setting up the db and catch exception if otherwise
        if (!loadeddb) {
            try {
                setupdb();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SharedPreferences sharedPref = ct.getApplicationContext().getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("version_code", "" + getAppVersion(ct));
        editor.apply();
        String dbpath = ctx.getExternalFilesDir(null).getAbsolutePath() + "/" + "secucapture.db";

        SQLiteDatabase.loadLibs(ctx);
    }

    private int getAppVersion(Context cont) {

        PackageInfo pinfo = null;
         try {
         pinfo = cont.getPackageManager().getPackageInfo(cont.getPackageName(), 0);
         } catch (PackageManager.NameNotFoundException e) {
         e.printStackTrace();
         }

         return pinfo.versionCode;
    }

    public DbHelper(Context ctx, Activity act) {
        String dbpath = ctx.getExternalFilesDir(null).getAbsolutePath() + "/" + "   secucapture.db";
        SQLiteDatabase.loadLibs(ctx);
        act = act;
        if (db == null) {
            db = SQLiteDatabase.openOrCreateDatabase(dbpath, "", null);
        }
    }

    private void setupdb() {

        Log.d("in helper", "db setup: " + "success");
        String dbpath = ctx.getExternalFilesDir(null).getAbsolutePath() + "/" + "secucapture.db";
        SQLiteDatabase.loadLibs(ctx);
        db = SQLiteDatabase.openOrCreateDatabase(dbpath, "", null);
        createtables();

    }

    public void updateDBAppVersion(String version_code) {
        ContentValues ctx = new ContentValues();
        ctx.put("version_code", version_code);
        ctx.put("version_name", version_code);
        db.insert("app_version", null, ctx);
        Log.d("in helper", "Version update: " + "success");
    }

    public String getVersionCode() {
        try {
            Cursor cursor1 = db.rawQuery("SELECT MAX(id), version_code FROM app_version", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                do {
                    return cursor1.getString(1);
                } while (cursor1.moveToNext());
            }
            cursor1.close();
        } catch (Exception e) {

        }
        return "0";
    }

    private void createtables() {

        Log.e("in helper", "create tables: " + "success");
        //TO DO add columns

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (_id INTEGER PRIMARY KEY  NOT NULL ,username VARCHAR,password VARCHAR,about VARCHAR,agent_id INTEGER,status_id VARCHAR DEFAULT (null) ,sync_datetime LONG,email_address VARCHAR,phone_no VARCHAR,account_id INTEGER,employee_id INTEGER,account_name VARCHAR,rid INTEGER UNIQUE , branch VARCHAR, branch_id INTEGER, name VARCHAR, is_active VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS logs (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , user_id INTEGER, sync_datetime LONG DEFAULT CURRENT_TIMESTAMP, description VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS app_version  (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, version_code VARCHAR, version_name VARCHAR, datetime VARCHAR, status VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS vehicles  (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, truckNo  VARCHAR, transporter VARCHAR, inspectionNo VARCHAR, insuranceNo VARCHAR, speedGovernorLicense VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS drivers (_id INTEGER PRIMARY KEY  NOT NULL , fullname VARCHAR, transporter_name VARCHAR, transporter_id VARCHAR, email_address VARCHAR, phone_no VARCHAR, id_no VARCHAR, drivers_licence_no VARCHAR, datecomparer VARCHAR, branch VARCHAR, branch_id INTEGER, sync_status VARCHAR, is_active VARCHAR,  sid VARCHAR, member_no VARCHAR, transacting_branch_id VARCHAR, comment VARCHAR,user_id VARCHAR, memberShipTypeId VARCHAR, membership_type_id VARCHAR, membership_sub_type_id VARCHAR, sync_datetime VARCHAR, reg_date VARCHAR, status VARCHAR, employee_category_id VARCHAR, member_type VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS vehicle_test (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, numberPlate VARCHAR, question1 VARCHAR, question2 VARCHAR,question3 VARCHAR, question4 VARCHAR, question5 VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS drivers_pics (_id INTEGER PRIMARY KEY  NOT NULL, fullname VARCHAR, id_no VARCHAR, photo_path VARCHAR, photo_name VARCHAR, photo_category VARCHAR, sync_status VARCHAR, is_active VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS vehicle_pics (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, truck_no VARCHAR, sync_status VARCHAR, datecomparer VARCHAR, photo_category VARCHAR, photo_path VARCHAR)");


        db.execSQL(
                "CREATE TABLE IF NOT EXISTS lessons (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, sid VARCHAR, fullname VARCHAR, id_no VARCHAR, reg_date VARCHAR, datecomparer VARCHAR, is_active VARCHAR, lesson_id VARCHAR, lesson_link VARCHAR, lesson_name VARCHAR, sync_status VARCHAR)");


        db.execSQL(
                "CREATE TABLE IF NOT EXISTS lessons_quizes (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, sid VARCHAR, lesson_name VARCHAR, lesson_id VARCHAR, lesson_link VARCHAR, quiz VARCHAR, quiz_id VARCHAR, user_id VARCHAR, status VARCHAR, Answer VARCHAR, Marks VARCHAR, datecomparer VARCHAR, is_active VARCHAR, is_correct VARCHAR, sync_status VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS quizes_answers (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, sid VARCHAR, quiz_id VARCHAR, answers VARCHAR, datecomparer VARCHAR, is_active VARCHAR, sync_status VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS user_answers_given (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, sid VARCHAR, quiz_id VARCHAR, answer_id VARCHAR, transaction_no VARCHAR, answer_option VARCHAR, sync_status VARCHAR,  datetime VARCHAR,  status VARCHAR,  nat_id VARCHAR,  lessonId VARCHAR, datecomparer VARCHAR)");

        db.execSQL(
                " CREATE TABLE IF NOT EXISTS drivers_organizations (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,orgId INTEGER,orgName VARCHAR,departmentName VARCHAR,transactingBranchId INTEGER,membershipSubTypeId INTEGER,syncDatetime VARCHAR,memberShipTypeId INTEGER,natId VARCHAR,regDate VARCHAR,dateComparer VARCHAR,status INTEGER,isActive INTEGER,medicalChecked INTEGER,dlNumber VARCHAR,employeeCategoryId INTEGER,memberType VARCHAR,isMedicallyAssessed INTEGER)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS courses (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,id INT,title VARCHAR,description VARCHAR,banner VARCHAR,course_content VARCHAR,status BOOLEAN,datecomparer BIGINT,local_insert_time TIMESTAMP,transaction_no VARCHAR)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS tutorials (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,id INT,course_id INT,title VARCHAR,description VARCHAR,video_url VARCHAR,created_by VARCHAR,status BOOLEAN,time_bound BOOLEAN,passmark INT,attempts_limit INT,tutorial_start_date TIMESTAMP,tutorial_expiry_date TIMESTAMP,transaction_no VARCHAR,datecomparer BIGINT)");

        db.execSQL(
                " CREATE TABLE IF NOT EXISTS questions(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,id INT,assessment_id INT,tutorial_id INT,questions VARCHAR,description VARCHAR,Marks INT,IsActive BOOLEAN,transaction_no VARCHAR,datecomparer BIGINT)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS choices (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,id INT,question_id INT,choice VARCHAR,description VARCHAR,marks INT,IsCorrect BOOLEAN,IsActive BOOLEAN,transaction_no VARCHAR,datecomparer BIGINT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS assessments (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, id INT,course_tutorial_id INT,name VARCHAR,description VARCHAR,passmark INT,max_attempts INT,transaction_no VARCHAR,datecomparer BIGINT)");


        /**----------------- DB TABLES ALTERATIONS -----------------------**/

        try {

            android.database.Cursor cursor1 = db.rawQuery("SELECT count(ntsa_inspection_no) FROM vehicles", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                do {

                } while (cursor1.moveToNext());
            }
            cursor1.close();

        } catch (Exception e) {

            db.execSQL("ALTER TABLE vehicles ADD COLUMN ntsa_inspection_no VARCHAR");
            db.execSQL("ALTER TABLE vehicles ADD COLUMN truck_insurance_no VARCHAR");
            db.execSQL("ALTER TABLE vehicles ADD COLUMN speed_gov_licence_no VARCHAR");

        }

        try {
            android.database.Cursor cursor1 = db.rawQuery("SELECT count(is_answered) FROM lessons_quizes", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                do {

                } while (cursor1.moveToNext());
            }
            cursor1.close();
        } catch (Exception e) {
            db.execSQL("ALTER TABLE lessons_quizes ADD COLUMN is_answered VARCHAR");
        }

        try {
            android.database.Cursor cursor1 = db.rawQuery("SELECT count(status) FROM lessons", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                do {

                } while (cursor1.moveToNext());
            }
            cursor1.close();
        } catch (Exception e) {
            db.execSQL("ALTER TABLE lessons ADD COLUMN status VARCHAR");
        }

        try {
            android.database.Cursor cursor1 = db.rawQuery("SELECT count(driver_id) FROM user_answers_given", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                do {

                } while (cursor1.moveToNext());
            }
            cursor1.close();
        } catch (Exception e) {
            db.execSQL("ALTER TABLE user_answers_given ADD COLUMN driver_id VARCHAR");
            db.execSQL("ALTER TABLE user_answers_given ADD COLUMN score VARCHAR");
        }


        try {
            Cursor cursor1 = db.rawQuery("SELECT count(vehicle_test) FROM secucapture.db", null);
            cursor1.moveToFirst();
            if (!cursor1.isAfterLast()) {
                do {

                } while (cursor1.moveToNext());
            }
            cursor1.close();
        } catch (Exception e) {
            db.execSQL("DROP TABLE IF EXISTS vehicle_test");
        }
    }

    public Boolean VehicleRegInputData(String truckNo, String transporter, String inspectionNo, String insuranceNo, String speedGovernorLicense) {

        ContentValues ctx = new ContentValues();
        ctx.put("truckNo", truckNo);
        ctx.put("transporter", transporter);
        ctx.put("inspectionNo", inspectionNo);
        ctx.put("insuranceNo", insuranceNo);
        ctx.put("speedGovernorLicense", speedGovernorLicense);

        long newRowId = db.insert("vehicles",null,ctx);

        Log.d("in helper", "vehicles data update: " + "success");

        return newRowId != -1;
    }

    public Boolean DriverRegInputData(String fullname, String transporter_name, String phone_no, String id_no, String drivers_license_no) {
        ContentValues ctx = new ContentValues();
        ctx.put("fullname", fullname);
        ctx.put("transporter_name", transporter_name);
        ctx.put(" phone_no", phone_no);
        ctx.put("id_no", id_no);
        ctx.put("drivers_license_no", drivers_license_no);

        long newRowId = db.insert("drivers",null,ctx);

        Log.d("in helper", "drivers data update: " + "success");

        return newRowId != -1;
    }

    public Boolean UserRegInputData(String username, String password) {
        ContentValues ctx = new ContentValues();
        ctx.put("username", username);
        ctx.put("password", password);

        long newRowId = db.insert("users",null,ctx);

        Log.d("in helper", "users data update: " + "success");

        return newRowId != -1;
    }

    public boolean VehicleAssessInputData(String numberPlate, String question1, String question2,
                                        String question3, String question4, String question5) {
        ContentValues ctx = new ContentValues();
        ctx.put("numberPlate", numberPlate);
        ctx.put("question1", question1);
        ctx.put("question2", question2);
        ctx.put("question3", question3);
        ctx.put("question4", question4);
        ctx.put("question5", question5);

        long result = db.insert("vehicle_test",null,ctx);

        Log.d("in helper", "vehicle_test data update: " + "success");

        return result != -1;
    }

    public Boolean checkusername(String username) {
        Log.d("DbHelper", "Checking if user with username exists: " + username);

        Cursor cursor1 = null;
        try {
            cursor1 = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

            return cursor1.getCount() > 0;
        } finally {
            if (cursor1 != null) {
                cursor1.close();
            }
        }
    }

    public void log_event(Context act, String data) {
        try {
            String prefix = GlobalVariables.timeNow() + " : " + GlobalVariables.code + " uid "
                    + getUSERID() + " =>";


            String root = Environment.getExternalStorageDirectory().toString() + "/SecuCapture/.LOGS/";
            File file = new File(root);
            file.mkdirs();

            File gpxfile = new File(file, GlobalVariables.logDate() + "" + GlobalVariables.Log_file_name);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(prefix + data + "\n");
            writer.flush();
            writer.close();
        } catch (Exception ex) {

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String encrypt(String textRaw) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String text = textRaw;
        // Change this to UTF-16 if needed
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        String str = new String(digest, StandardCharsets.UTF_8);
        String s = new String(digest);
        return hex;
    }

    public String EncryptTextMD5(String RawText) {
        String EncText = "";
        byte[] keyArray = new byte[24];
        byte[] temporaryKey;

        byte[] toEncryptArray = null;

        try {

            toEncryptArray = RawText.getBytes(StandardCharsets.UTF_8);
            MessageDigest m = MessageDigest.getInstance("MD5");
            temporaryKey = m.digest(getkey().getBytes(StandardCharsets.UTF_8));
            Cipher c = Cipher.getInstance("DESede/ECB/PKCS7Padding");
            KeyGenerator k = KeyGenerator.getInstance("DESede");
            SecretKey key1;
            key1 = new SecretKeySpec(temporaryKey, k.getAlgorithm());
            c.init(Cipher.ENCRYPT_MODE, key1);
            byte[] encrypted = c.doFinal(toEncryptArray);
            EncText = encodeBase64String(encrypted);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                 | InvalidKeyException | BadPaddingException NoEx) {
            Log.v("##################", NoEx.getMessage());
        }

        return EncText;
    }

    private String encodeBase64String(byte[] encrypted) {

        return encodeBase64String(encrypted);
    }

    private String getkey() {
        return act.getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE).getString("move",
                "");
    }

    public long getMaxDateComparer(String TBL){
        String query = "SELECT MAX(datecomparer) AS maxTime FROM " + TBL + " ";
        android.database.Cursor c = db.rawQuery(query, null);

        long id = 0;

        if (c.moveToFirst()){
            do {
                id = c.getLong(0);
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    private void InsertLog(ContentValues cv) {

        try {
            db.insert("logs", null, cv);
        } catch (Exception e) {

        }
    }
    public boolean loginUser(String username, String passcode) {
        boolean login_success = false;
        try {
            android.database.Cursor UsersCursor = db
                    .query("users",
                            new String[]{"_id", "username", "password",
                                    "account_name", "branch", "rid"},
                            "username = ? AND password = ? ",
                            new String[]{username.trim(), passcode.trim()},
                            null, null, "_id");
            UsersCursor.moveToFirst();

            if (!UsersCursor.isAfterLast()) {
                do {
                    String name = UsersCursor.getString(1);
                    login_success = true;
                    ContentValues cv = new ContentValues();
                    cv.put("user_id", UsersCursor.getInt(5));
                    InsertLog(cv);
                    GlobalVariables.userid = UsersCursor.getString(0);
                } while (UsersCursor.moveToNext());
            } else {
                login_success = false;
            }
            UsersCursor.close();

            return login_success;
        } catch (Exception e) {
            e.printStackTrace();
            login_success = false;
        }

        return false;
    }


    public users getUserLoggedIn() {
        int p_id = 0;
        int refID = userLoggedIn();
        String[] columns = {"_id", "username", "password", "account_name", "branch"};
        String selection = "rid = ?";
        String[] args = {"" + refID};
        users uz = new users();
        android.database.Cursor c = db.query("users", columns, selection, args, null,
                null, null);
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                do {

                    uz.setAccount_name(c.getString(3));
                    uz.setBranch(c.getString(4));
                    uz.setUsername(c.getString(1));

                } while (c.moveToNext());
            } else {
                uz.setAccount_name("");
                uz.setBranch("");
                uz.setUsername("");
            }
            c.close();

        }

        return uz;
    }

    public int userLoggedIn() {
        int p_id = 0;
        int refID = (int) getMaxId("logs");
        String[] columns = {"COALESCE(user_id, 0)"}; // columns to select
        String selection = "_id = ?";
        String[] args = {"" + refID}; // value added into where clause -->
        android.database.Cursor c = db.query("logs", columns, selection, args, null, null, null);
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                do {
                    p_id = c.getInt(0);
                } while (c.moveToNext());
            }
            c.close();
        }
        return p_id;
    }

    public void updateUser(ContentValues memberMap, int user_id, ContentValues logMap) {
        int counter = 0;
        android.database.Cursor user = db.rawQuery("SELECT count(*) FROM users WHERE rid = ?", new String[]{"" + user_id});
        user.moveToFirst();
        if (!user.isAfterLast()) {
            do {
                counter = user.getInt(0);
            } while (user.moveToNext());
        }
        user.close();
        if (counter <= 0) {
            Log.v("LLLLLLLLLL", "KKKKKKKKKKKKKKKKK inserting");
            try {
                db.insert("users", null, memberMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String WhereClause = "rid = ?";
            String[] WhereArgs = {"" + user_id};
            try {
                Log.v("LLLLLLLLLL", "KKKKKKKKKKKKKKKKK updating");
                db.update("users", memberMap, WhereClause, WhereArgs);
            } catch (Exception ep) {
                ep.printStackTrace();
            }
        }
        // UPDATE LOG TBL
        try {
            db.insert("logs", null, logMap);
            // userModel.onCreate(null);
        } catch (Exception e) {
            Log.v("%%%%%%%%%%%% ", "" + e);
        }

    }

    public int getUSERID() {
        String query = "SELECT MAX(_id), user_id FROM logs";

        int res_id = 0;

        android.database.Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            do {
                res_id = c.getInt(1);
            } while (c.moveToNext());
        }
        c.close();

        return res_id;
    }

    private long getMaxId(String TBL) {
        int id = 0;
        String[] columns = {"MAX(_id)"}; // columns to select
        String selection = "_id = ?";
        android.database.Cursor cursor = db.query("logs", columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }

    public void InsertRegisteredDrivers(JSONArray jRegisteredDrivers, Context context) throws JSONException {

        JSONArray jRegisteredDriver = jRegisteredDrivers;
        int drivers_length = jRegisteredDriver.length();

        new Helper().ConsoleLog("RESULTS", "RegisteredDriversFromServer--------- length " +drivers_length );

        db.beginTransaction();

        String sql_insert = "insert into drivers (fullname, transporter_name , transporter_id , email_address , phone_no , id_no , drivers_licence_no , datecomparer, branch , branch_id , sync_status , is_active ,  sid , member_no , transacting_branch_id , comment ,user_id , memberShipTypeId , membership_type_id , membership_sub_type_id , sync_datetime , reg_date , status , employee_category_id , member_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        //String sql_update = "update drivers set phone_no=?, fullname=?, member_no=?, transacting_branch_id=?, comment=?, user_id=?, membership_sub_type_id=?, sync_datetime=?, memberShipTypeId=?, membership_type_id=?, id_no=?, reg_date=?, datecomparer=?, status=?, is_active=?, medical_status=?, drivers_licence_no=?, employee_category_id=?, member_type=?, sync_status=? where sid = ?";

        net.sqlcipher.database.SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
        //net.sqlcipher.database.SQLiteStatement stmt_update = db.compileStatement(sql_update);

//

        for (int i = 0; i < jRegisteredDriver.length(); i++) {


            stmt_insert.bindString(1, jRegisteredDriver.getJSONObject(i).getString("fullname"));
            stmt_insert.bindString(2, jRegisteredDriver.getJSONObject(i).getString("transporter_name"));
            stmt_insert.bindString(3, jRegisteredDriver.getJSONObject(i).getString("transporter_id"));
            stmt_insert.bindString(4, jRegisteredDriver.getJSONObject(i).getString("email_address"));
            stmt_insert.bindString(5, jRegisteredDriver.getJSONObject(i).getString("phone_no"));
            stmt_insert.bindString(6, jRegisteredDriver.getJSONObject(i).getString("id_no"));
            stmt_insert.bindString(7, jRegisteredDriver.getJSONObject(i).getString("drivers_license_no"));
            stmt_insert.bindString(8, jRegisteredDriver.getJSONObject(i).getString("datecomparer"));
            stmt_insert.bindString(9, jRegisteredDriver.getJSONObject(i).getString("branch"));
            stmt_insert.bindString(10, jRegisteredDriver.getJSONObject(i).getString("branch_id"));
            stmt_insert.bindString(11, jRegisteredDriver.getJSONObject(i).getString("sync_status"));
            stmt_insert.bindString(12, jRegisteredDriver.getJSONObject(i).getString("is_active"));
            stmt_insert.bindString(13, jRegisteredDriver.getJSONObject(i).getString("sid"));
            stmt_insert.bindString(14, jRegisteredDriver.getJSONObject(i).getString("member_no"));
            stmt_insert.bindString(15, jRegisteredDriver.getJSONObject(i).getString("transaction_branch_id"));
            stmt_insert.bindString(16, jRegisteredDriver.getJSONObject(i).getString("comment"));
            stmt_insert.bindString(17, jRegisteredDriver.getJSONObject(i).getString("user_id"));
            stmt_insert.bindString(18, jRegisteredDriver.getJSONObject(i).getString("memberShipTypeId"));
            stmt_insert.bindString(19, jRegisteredDriver.getJSONObject(i).getString("membership_type_id"));
            stmt_insert.bindString(20, jRegisteredDriver.getJSONObject(i).getString("membership_sub_type_id"));
            stmt_insert.bindString(21, jRegisteredDriver.getJSONObject(i).getString("sync_datetime"));
            stmt_insert.bindString(22, jRegisteredDriver.getJSONObject(i).getString("reg_date"));
            stmt_insert.bindString(23, jRegisteredDriver.getJSONObject(i).getString("status"));
            stmt_insert.bindString(24, jRegisteredDriver.getJSONObject(i).getString("employee_category_id"));
            stmt_insert.bindString(25, jRegisteredDriver.getJSONObject(i).getString("member_type"));
            stmt_insert.bindString(26, "i");

            /**stmt_update.bindString(1, jRegisteredDriver.getJSONObject(i).getString("phone1"));
            stmt_update.bindString(2, jRegisteredDriver.getJSONObject(i).getString("full_name"));
            stmt_update.bindString(3, jRegisteredDriver.getJSONObject(i).getString("member_no"));
            stmt_update.bindString(4, jRegisteredDriver.getJSONObject(i).getString("transacting_branch_id"));
            stmt_update.bindString(5, jRegisteredDriver.getJSONObject(i).getString("comment"));
            stmt_update.bindString(6, jRegisteredDriver.getJSONObject(i).getString("user_id"));
            stmt_update.bindString(7, jRegisteredDriver.getJSONObject(i).getString("membership_sub_type_id"));
            stmt_update.bindString(8, jRegisteredDriver.getJSONObject(i).getString("sync_datetime"));
            stmt_update.bindString(9, jRegisteredDriver.getJSONObject(i).getString("memberShipTypeId"));
            stmt_update.bindString(10, jRegisteredDriver.getJSONObject(i).getString("membership_type_id"));
            stmt_update.bindString(11, jRegisteredDriver.getJSONObject(i).getString("nat_id"));
            stmt_update.bindString(12, jRegisteredDriver.getJSONObject(i).getString("reg_date"));
            stmt_update.bindString(13, jRegisteredDriver.getJSONObject(i).getString("datecomparer"));
            stmt_update.bindString(14, jRegisteredDriver.getJSONObject(i).getString("status"));
            stmt_update.bindString(15, jRegisteredDriver.getJSONObject(i).getString("Is_active"));
            stmt_update.bindString(16, jRegisteredDriver.getJSONObject(i).getString("medical_checked"));
            stmt_update.bindString(17, jRegisteredDriver.getJSONObject(i).getString("dl_Number"));
            stmt_update.bindString(18, jRegisteredDriver.getJSONObject(i).getString("employee_category_id"));
            stmt_update.bindString(19, jRegisteredDriver.getJSONObject(i).getString("member_type"));
            stmt_update.bindString(20, "i");
            stmt_update.bindString(21, jRegisteredDriver.getJSONObject(i).getString("id"));**/

            String server_id = null;
            String[] whereArgs = {""+ jRegisteredDriver.getJSONObject(i).getString("id")};
            android.database.Cursor c = db.rawQuery("SELECT sid FROM drivers WHERE sid = ?", whereArgs);
            c.moveToFirst();
            if (!c.isAfterLast()){
                do {
                    server_id = c.getString(0);
                } while (c.moveToNext());
            }
            c.close();

            if (server_id == null){
                long entryID = stmt_insert.executeInsert();
            } else {
                //long entryID = stmt_update.executeUpdateDelete();
            }

            stmt_insert.clearBindings();
            //stmt_update.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void InsertDriverOrganizations(JSONArray jsonArray, Context applicationContext) {

        try {


            JSONArray jDriverOrganisations = jsonArray;
            int driverOrganizations_length = jsonArray.length();

            new Helper().ConsoleLog("RESULTS", "DriverOrganizationsFromServer--------- length " +driverOrganizations_length );
            // Begin transaction
            db.beginTransaction();

            // Define SQL statements for insertion and update
            String sql_insert = "INSERT INTO drivers_organizations (orgId, orgName, departmentName, transactingBranchId, membershipSubTypeId, syncDatetime, memberShipTypeId, natId, regDate, dateComparer, status, isActive, medicalChecked, dlNumber, employeeCategoryId, memberType, isMedicallyAssessed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //String sql_insert = "INSERT INTO driver_organizations (orgId, orgName,departmentName ,transactingBranchId ,membershipSubTypeId ,syncDatetime,membershipTypeId ,natId ,regDate ,dateComparer ,status ,isActive ,medicalChecked ,dlNumber ,employeeCategoryId ,memberType ,isMedicallyAssessed ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)";
            //String sql_update = "UPDATE driver_organizations SET orgName = ? WHERE orgId = ?";

            // Compile SQL statements
            SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
            //SQLiteStatement stmt_update = db.compileStatement(sql_update);

            // Process each JSONObject in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Bind values to SQL statement parameters
                stmt_insert.bindString(1, jsonObject.getString("orgId"));
                stmt_insert.bindString(2, jsonObject.getString("orgName"));
                stmt_insert.bindString(3, jsonObject.getString("departmentName"));
                stmt_insert.bindString(4, jsonObject.getString("transactionBranchId"));
                stmt_insert.bindString(5, jsonObject.getString("membershipSubTypeId"));
                stmt_insert.bindString(6, jsonObject.getString("syncDateTime"));
                stmt_insert.bindString(7, jsonObject.getString("membershipTypeId"));
                stmt_insert.bindString(8, jsonObject.getString("natId"));
                stmt_insert.bindString(9, jsonObject.getString("regDate"));
                stmt_insert.bindString(10, jsonObject.getString("dateComparer"));
                stmt_insert.bindString(11, jsonObject.getString("status"));
                stmt_insert.bindString(12, jsonObject.getString("isActive"));
                stmt_insert.bindString(13, jsonObject.getString("medicalChecked"));
                stmt_insert.bindString(14, jsonObject.getString("dlNumber"));
                stmt_insert.bindString(15, jsonObject.getString("employeeCategoryId"));
                stmt_insert.bindString(16, jsonObject.getString("memberType"));
                stmt_insert.bindString(17, jsonObject.getString("isMedicallyAssessed"));

                String server_id = null;
                String[] whereArgs = {""+ jsonArray.getJSONObject(i).getString("id")};
                android.database.Cursor c = db.rawQuery("SELECT orgId FROM driver_organizations WHERE orgId = ?", whereArgs);
                c.moveToFirst();
                if (!c.isAfterLast()){
                    do {
                        server_id = c.getString(0);
                    } while (c.moveToNext());
                }
                c.close();

                if (server_id == null){
                    long entryID = stmt_insert.executeInsert();
                } else {
                    //long entryID = stmt_update.executeUpdateDelete();
                }

                stmt_insert.clearBindings();
                //stmt_update.clearBindings();
            }

            // Set transaction as successful and end it
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void InsertQuestions(JSONArray jsonArray, Context applicationContext) {

        try {


            JSONArray jQuestions = jsonArray;
            int questions_length = jsonArray.length();

            new Helper().ConsoleLog("RESULTS", "QuestionsFromServer--------- length " + questions_length );
            // Begin transaction
            db.beginTransaction();

            // Define SQL statements for insertion and update
            String sql_insert = "INSERT INTO questions (id,assessment_id,tutorial_id,questions ,description ,Marks ,IsActive ,transaction_no,datecomparer ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //String sql_update = "UPDATE courses SET orgNa = ? WHERE orgId = ?";

            // Compile SQL statements
            SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
            //SQLiteStatement stmt_update = db.compileStatement(sql_update);

            // Process each JSONObject in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Bind values to SQL statement parameters
                stmt_insert.bindString(1, jsonObject.getString("id"));
                stmt_insert.bindString(2, jsonObject.getString("assessment_id"));
                stmt_insert.bindString(3, jsonObject.getString("tutorial_id"));
                stmt_insert.bindString(4, jsonObject.getString("questions"));
                stmt_insert.bindString(5, jsonObject.getString("description"));
                stmt_insert.bindString(6, jsonObject.getString("Marks"));
                stmt_insert.bindString(7, jsonObject.getString("IsActive"));
                stmt_insert.bindString(8, jsonObject.getString("transaction_no"));
                stmt_insert.bindString(9, jsonObject.getString("datecomparer"));


                String server_id = null;
                String[] whereArgs = {""+ jsonArray.getJSONObject(i).getString("id")};
                android.database.Cursor c = db.rawQuery("SELECT id FROM questions WHERE id = ?", whereArgs);
                c.moveToFirst();
                if (!c.isAfterLast()){
                    do {
                        server_id = c.getString(0);
                    } while (c.moveToNext());
                }
                c.close();

                if (server_id == null){
                    long entryID = stmt_insert.executeInsert();
                } else {
                    //long entryID = stmt_update.executeUpdateDelete();
                }

                stmt_insert.clearBindings();
                //stmt_update.clearBindings();
            }

            // Set transaction as successful and end it
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void InsertAssessments(JSONArray jsonArray, Context applicationContext) {


        try {

            JSONArray jAssessments = jsonArray;
            int assessments_length = jsonArray.length();

            new Helper().ConsoleLog("RESULTS", "AssessmentsFromServer--------- length " + assessments_length );
            // Begin transaction
            db.beginTransaction();

            // Define SQL statements for insertion and update
            String sql_insert = "INSERT INTO assessments (id,course_tutorial_id ,name ,description,passmark ,max_attempts,transaction_no ,datecomparer ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            //String sql_update = "UPDATE courses SET orgNa = ? WHERE orgId = ?";

            // Compile SQL statements
            SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
            //SQLiteStatement stmt_update = db.compileStatement(sql_update);

            // Process each JSONObject in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Bind values to SQL statement parameters
                stmt_insert.bindString(1, jsonObject.getString("id"));
                stmt_insert.bindString(2, jsonObject.getString("course_tutorial_id"));
                stmt_insert.bindString(3, jsonObject.getString("name"));
                stmt_insert.bindString(4, jsonObject.getString("description"));
                stmt_insert.bindString(5, jsonObject.getString("passmark"));
                stmt_insert.bindString(6, jsonObject.getString("max_attempts"));
                stmt_insert.bindString(7, jsonObject.getString("transaction_no"));
                stmt_insert.bindString(8, jsonObject.getString("datecomparer"));

                String server_id = null;
                String[] whereArgs = {""+ jsonArray.getJSONObject(i).getString("id")};
                android.database.Cursor c = db.rawQuery("SELECT id FROM assessments WHERE id = ?", whereArgs);
                c.moveToFirst();
                if (!c.isAfterLast()){
                    do {
                        server_id = c.getString(0);
                    } while (c.moveToNext());
                }
                c.close();

                if (server_id == null){
                    long entryID = stmt_insert.executeInsert();
                } else {
                    //long entryID = stmt_update.executeUpdateDelete();
                }

                stmt_insert.clearBindings();
                //stmt_update.clearBindings();
            }

            // Set transaction as successful and end it
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void InsertTutorials(JSONArray jsonArray, Context applicationContext) {

        try {


            JSONArray jTutorials = jsonArray;
            int tutorials_length = jsonArray.length();

            new Helper().ConsoleLog("RESULTS", "TutorialsFromServer--------- length " +tutorials_length );
            // Begin transaction
            db.beginTransaction();

            // Define SQL statements for insertion and update
            String sql_insert = "INSERT INTO tutorials (id ,course_id ,title ,description ,video_url ,created_by ,status ,time_bound ,passmark ,attempts_limit ,tutorial_start_date ,tutorial_expiry_date ,transaction_no ,datecomparer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //String sql_update = "UPDATE courses SET orgNa = ? WHERE orgId = ?";

            // Compile SQL statements
            SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
            //SQLiteStatement stmt_update = db.compileStatement(sql_update);

            // Process each JSONObject in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Bind values to SQL statement parameters
                stmt_insert.bindString(1, jsonObject.getString("id"));
                stmt_insert.bindString(2, jsonObject.getString("course_id"));
                stmt_insert.bindString(3, jsonObject.getString("title"));
                stmt_insert.bindString(4, jsonObject.getString("description"));
                stmt_insert.bindString(5, jsonObject.getString("video_url"));
                stmt_insert.bindString(6, jsonObject.getString("created_by"));
                stmt_insert.bindString(7, jsonObject.getString("status"));
                stmt_insert.bindString(8, jsonObject.getString("time_bound"));
                stmt_insert.bindString(9, jsonObject.getString("passmark"));
                stmt_insert.bindString(10, jsonObject.getString("attempts_limit"));
                stmt_insert.bindString(11, jsonObject.getString("tutorial_start_date"));
                stmt_insert.bindString(12, jsonObject.getString("tutorial_expiry_date"));
                stmt_insert.bindString(13, jsonObject.getString("transaction_no"));
                stmt_insert.bindString(14, jsonObject.getString("datecomparer"));


                String server_id = null;
                String[] whereArgs = {""+ jsonArray.getJSONObject(i).getString("id")};
                android.database.Cursor c = db.rawQuery("SELECT id FROM tutorials WHERE id = ?", whereArgs);
                c.moveToFirst();
                if (!c.isAfterLast()){
                    do {
                        server_id = c.getString(0);
                    } while (c.moveToNext());
                }
                c.close();

                if (server_id == null){
                    long entryID = stmt_insert.executeInsert();
                } else {
                    //long entryID = stmt_update.executeUpdateDelete();
                }

                stmt_insert.clearBindings();
                //stmt_update.clearBindings();
            }

            // Set transaction as successful and end it
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void InsertCourses(JSONArray jsonArray, Context applicationContext) {

        try {


            JSONArray jCourses = jsonArray;
            int courses_length = jsonArray.length();

            new Helper().ConsoleLog("RESULTS", "CoursesFromServer--------- length " + courses_length );
            // Begin transaction
            db.beginTransaction();

            // Define SQL statements for insertion and update
            String sql_insert = "INSERT INTO courses (id ,title ,description ,banner ,course_content ,status ,datecomparer ,local_insert_time ,transaction_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //String sql_update = "UPDATE courses SET orgNa = ? WHERE orgId = ?";

            // Compile SQL statements
            SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
            //SQLiteStatement stmt_update = db.compileStatement(sql_update);

            // Process each JSONObject in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Bind values to SQL statement parameters
                stmt_insert.bindString(1, jsonObject.getString("id"));
                stmt_insert.bindString(2, jsonObject.getString("title"));
                stmt_insert.bindString(3, jsonObject.getString("description"));
                stmt_insert.bindString(4, jsonObject.getString("banner"));
                stmt_insert.bindString(5, jsonObject.getString("course_content"));
                stmt_insert.bindString(6, jsonObject.getString("status"));
                stmt_insert.bindString(7, jsonObject.getString("datecomparer"));
                stmt_insert.bindString(8, jsonObject.getString("local_insert_time"));
                stmt_insert.bindString(9, jsonObject.getString("transaction_no"));


                String server_id = null;
                String[] whereArgs = {""+ jsonArray.getJSONObject(i).getString("id")};
                android.database.Cursor c = db.rawQuery("SELECT id FROM courses WHERE id = ?", whereArgs);
                c.moveToFirst();
                if (!c.isAfterLast()){
                    do {
                        server_id = c.getString(0);
                    } while (c.moveToNext());
                }
                c.close();

                if (server_id == null){
                    long entryID = stmt_insert.executeInsert();
                } else {
                    //long entryID = stmt_update.executeUpdateDelete();
                }

                stmt_insert.clearBindings();
                //stmt_update.clearBindings();
            }

            // Set transaction as successful and end it
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void InsertChoices(JSONArray jsonArray, Context applicationContext) {

        try {


            JSONArray jChoices = jsonArray;
            int choices_length = jsonArray.length();

            new Helper().ConsoleLog("RESULTS", "ChoicesFromServer--------- length " + choices_length );
            // Begin transaction
            db.beginTransaction();

            // Define SQL statements for insertion and update
            String sql_insert = "INSERT INTO choices (id,question_id,choice ,description,marks ,IsCorrect ,IsActive,transaction_no ,datecomparer ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //String sql_update = "UPDATE courses SET orgNa = ? WHERE orgId = ?";

            // Compile SQL statements
            SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
            //SQLiteStatement stmt_update = db.compileStatement(sql_update);

            // Process each JSONObject in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Bind values to SQL statement parameters
                stmt_insert.bindString(1, jsonObject.getString("id"));
                stmt_insert.bindString(2, jsonObject.getString("question_id"));
                stmt_insert.bindString(3, jsonObject.getString("choice"));
                stmt_insert.bindString(4, jsonObject.getString("description"));
                stmt_insert.bindString(5, jsonObject.getString("marks"));
                stmt_insert.bindString(6, jsonObject.getString("IsCorrect"));
                stmt_insert.bindString(7, jsonObject.getString("IsActive"));
                stmt_insert.bindString(8, jsonObject.getString("transaction_no"));
                stmt_insert.bindString(9, jsonObject.getString("datecomparer"));


               String server_id = null;
                String[] whereArgs = {""+ jsonArray.getJSONObject(i).getString("id")};
                android.database.Cursor c = db.rawQuery("SELECT id FROM choices WHERE id = ?", whereArgs);
                c.moveToFirst();
                if (!c.isAfterLast()){
                    do {
                        server_id = c.getString(0);
                    } while (c.moveToNext());
                }
                c.close();

                if (server_id == null){
                    long entryID = stmt_insert.executeInsert();
                } else {
                    //long entryID = stmt_update.executeUpdateDelete();
                }

                stmt_insert.clearBindings();
                //stmt_update.clearBindings();
            }

            // Set transaction as successful and end it
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONArray registeredLessonsAnswersToSync() throws JSONException {

//[
//        {
//            "driverId": "",
//             "lessonId": ,
//            "quizId": ,
//            "driver_quiz_marksheet_id":""
//    }
//]

        JSONArray mainArr = new JSONArray();
        JSONObject mainObject = new JSONObject();
        android.database.Cursor c = db.rawQuery("SELECT * FROM user_answers_given WHERE sync_status = ? LIMIT 1", new String[]{"p"});
        if (!c.isAfterLast()){
            c.moveToFirst();

            do {
                mainObject.put("driverId", c.getString(c.getColumnIndexOrThrow("nat_id")));
                mainObject.put("lessonId", c.getString(c.getColumnIndexOrThrow("lessonId")));
                mainObject.put("quizId", c.getString(c.getColumnIndexOrThrow("quiz_id")));
                mainObject.put("driver_quiz_marksheet_id", c.getString(c.getColumnIndexOrThrow("answer_id")));
            }while (c.moveToNext());
        }
        c.close();

        mainArr.put(mainObject);

        return mainArr;
    }

    public int countPendingLessonsAnswers() {
        int res = 0;
        android.database.Cursor c = db.rawQuery("SELECT count(*) FROM user_answers_given WHERE sync_status = ?", new String[]{"p"});
        if (!c.isAfterLast()){
            c.moveToFirst();
            do {
                res = c.getInt(0);
            }while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public JSONObject registeredDriversToSync() throws JSONException {

//        {
//            "full_name" : "",
//                "nat_id" : "",
//                "phone1" : "",
//                "dl_Number":""
//        }

        JSONObject mainObject = new JSONObject();
        android.database.Cursor c = db.rawQuery("SELECT * FROM drivers WHERE sync_status = ? LIMIT 1", new String[]{"new_driver"});
        if (!c.isAfterLast()){
            c.moveToFirst();

            do {
                mainObject.put("full_name", c.getString(c.getColumnIndexOrThrow("fullname")));
                mainObject.put("nat_id", c.getString(c.getColumnIndexOrThrow("id_no")));
                mainObject.put("phone1", c.getString(c.getColumnIndexOrThrow("phone_no")));
                mainObject.put("dl_Number", c.getString(c.getColumnIndexOrThrow("drivers_licence_no")));
            }while (c.moveToNext());
        }
        c.close();

        return mainObject;
    }


    public int countPendingRegisteredDrivers() {
        int res = 0;
        android.database.Cursor c = db.rawQuery("SELECT count(*) FROM drivers WHERE sync_status = ?", new String[]{"new_driver"});
        if (!c.isAfterLast()){
            c.moveToFirst();
            do {
                res = c.getInt(0);
            }while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public void InsertIndividualLessons(ContentValues cv){

        long insertedLesson = db.insertOrThrow("lessons", null, cv);
        Log.e(TAG, "InsertIndividualLessons: "+insertedLesson);

    }

    public void updateExistingLesson(ContentValues cv, String whereClause, String[] whereArgs) {

        int updated_lesson = db.update("lessons", cv, whereClause, whereArgs);
        Log.e(TAG, "UpdatedIndividualLessons: "+updated_lesson);

    }

    public boolean checkIfLessonExists(String lesson_id, String idno) { //fullname to be replaced with nat_id

        int number = 0;
//        Cursor cursor = database.rawQuery("SELECT count(lesson_name) FROM lessons WHERE lesson_id = ? AND fullname = ? ", new String[]{""+lesson_id, ""+fullname});
        android.database.Cursor cursor = db.rawQuery("SELECT count(lesson_name) FROM lessons WHERE lesson_id = ? AND id_no = ? ", new String[]{""+lesson_id, ""+idno});

        if (!cursor.isAfterLast()) {
            cursor.moveToFirst();
            do {
                number = cursor.getInt(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        if (number > 0) {
            return true;
        } else
            return false;
    }

    public void InsertLessonsQuestionsAnswers(JSONArray jLessonsQuizesAnswers, Context context) throws JSONException {

        JSONArray jLessonsQuizesAnswer = jLessonsQuizesAnswers;
        int data_length = jLessonsQuizesAnswer.length();

        new Helper().ConsoleLog("RESULTS", "LessonsAnswersFromServer--------- length " +data_length );

        db.beginTransaction();

//        "CREATE TABLE IF NOT EXISTS quizes_answers (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, sid VARCHAR, quiz_id VARCHAR, answers VARCHAR, datecomparer VARCHAR, is_active VARCHAR, sync_status VARCHAR)");

        String sql_insert = "insert into quizes_answers (sid, quiz_id, answers, datecomparer, is_active, sync_status) values (?,?,?,?,?,?)";
        String sql_update = "update quizes_answers set quiz_id=?, answers=?, datecomparer=?, is_active=?, sync_status=? where sid = ?";

        net.sqlcipher.database.SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
        net.sqlcipher.database.SQLiteStatement stmt_update = db.compileStatement(sql_update);

//        {
//            "$id": "2",
//                "id": 5,
//                "quiz_id": 7,
//                "answers": "No",
//                "IsActive": true,
//                "datecomparer": 16992808894903919
//        }

        for (int i = 0; i < jLessonsQuizesAnswer.length(); i++){


            stmt_insert.bindString(1, jLessonsQuizesAnswer.getJSONObject(i).getString("id"));
            stmt_insert.bindString(2, jLessonsQuizesAnswer.getJSONObject(i).getString("quiz_id"));
            stmt_insert.bindString(3, jLessonsQuizesAnswer.getJSONObject(i).getString("answers"));
            stmt_insert.bindString(4, jLessonsQuizesAnswer.getJSONObject(i).getString("datecomparer"));
            stmt_insert.bindString(5, jLessonsQuizesAnswer.getJSONObject(i).getString("IsActive"));
            stmt_insert.bindString(6, "i");


            stmt_update.bindString(1, jLessonsQuizesAnswer.getJSONObject(i).getString("quiz_id"));
            stmt_update.bindString(2, jLessonsQuizesAnswer.getJSONObject(i).getString("answers"));
            stmt_update.bindString(3, jLessonsQuizesAnswer.getJSONObject(i).getString("datecomparer"));
            stmt_update.bindString(4, jLessonsQuizesAnswer.getJSONObject(i).getString("id"));
            stmt_update.bindString(5, jLessonsQuizesAnswer.getJSONObject(i).getString("IsActive"));
            stmt_insert.bindString(6,"i");


            String server_id = null;
            String[] whereArgs = {""+ jLessonsQuizesAnswer.getJSONObject(i).getString("id")};
            android.database.Cursor c = db.rawQuery("SELECT sid FROM quizes_answers WHERE sid = ?", whereArgs);
            c.moveToFirst();
            if (!c.isAfterLast()){
                do {
                    server_id = c.getString(0);
                } while (c.moveToNext());
            }
            c.close();

            if (server_id == null){
                long entryID = stmt_insert.executeInsert();
            } else {
                long entryID = stmt_update.executeUpdateDelete();
            }

            stmt_insert.clearBindings();
            stmt_update.clearBindings();

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void InsertLessonsQuestions(JSONArray jLessonsQuizes, Context context) throws JSONException {

        JSONArray jLessonsQuiz = jLessonsQuizes;
        int data_length = jLessonsQuiz.length();

        new Helper().ConsoleLog("RESULTS", "LessonsQuizesFromServer--------- length " +data_length );

        db.beginTransaction();

        String sql_insert = "insert into lessons_quizes (sid, lesson_name, lesson_id, quiz, quiz_id, Marks, datecomparer, is_active, sync_status) values (?,?,?,?,?,?,?,?,?)";
        String sql_update = "update lessons_quizes set lesson_name=?, lesson_id=?, quiz=?, quiz_id=?, Marks=?, datecomparer=?, is_active=?, sync_status=? where sid = ?";

        net.sqlcipher.database.SQLiteStatement stmt_insert = db.compileStatement(sql_insert);
        net.sqlcipher.database.SQLiteStatement stmt_update = db.compileStatement(sql_update);


//        {
//            "$id": "2",
//                "id": 6,
//                "lesson_name": "Lesson 1",
//                "lesson_id": 6,
//                "quiz1": "Is drunk driving allowed?",
//                "Marks": 10,
//                "IsActive": true,
//                "datecomparer": 16992808102301033
//        }

        for (int i = 0; i < jLessonsQuiz.length(); i++){


            stmt_insert.bindString(1, jLessonsQuiz.getJSONObject(i).getString("id"));
            stmt_insert.bindString(2, jLessonsQuiz.getJSONObject(i).getString("lesson_name"));
            stmt_insert.bindString(3, jLessonsQuiz.getJSONObject(i).getString("lesson_id"));
            stmt_insert.bindString(4, jLessonsQuiz.getJSONObject(i).getString("quiz1"));
            stmt_insert.bindString(5, jLessonsQuiz.getJSONObject(i).getString("id"));
            stmt_insert.bindString(6, jLessonsQuiz.getJSONObject(i).getString("Marks"));
            stmt_insert.bindString(7, jLessonsQuiz.getJSONObject(i).getString("datecomparer"));
            stmt_insert.bindString(8, jLessonsQuiz.getJSONObject(i).getString("IsActive"));
            stmt_insert.bindString(9, "i");


            stmt_update.bindString(1, jLessonsQuiz.getJSONObject(i).getString("lesson_name"));
            stmt_update.bindString(2, jLessonsQuiz.getJSONObject(i).getString("lesson_id"));
            stmt_update.bindString(3, jLessonsQuiz.getJSONObject(i).getString("quiz1"));
            stmt_update.bindString(4, jLessonsQuiz.getJSONObject(i).getString("id"));
            stmt_update.bindString(5, jLessonsQuiz.getJSONObject(i).getString("Marks"));
            stmt_update.bindString(6, jLessonsQuiz.getJSONObject(i).getString("datecomparer"));
            stmt_update.bindString(7, jLessonsQuiz.getJSONObject(i).getString("IsActive"));
            stmt_update.bindString(8, "i");
            stmt_update.bindString(9, jLessonsQuiz.getJSONObject(i).getString("id"));


            String server_id = null;
            String[] whereArgs = {""+ jLessonsQuiz.getJSONObject(i).getString("id")};
            android.database.Cursor c = db.rawQuery("SELECT sid FROM lessons_quizes WHERE sid = ?", whereArgs);
            c.moveToFirst();
            if (!c.isAfterLast()){
                do {
                    server_id = c.getString(0);
                } while (c.moveToNext());
            }
            c.close();

            if (server_id == null){
                long entryID = stmt_insert.executeInsert();
            } else {
                long entryID = stmt_update.executeUpdateDelete();
            }

            stmt_insert.clearBindings();
            stmt_update.clearBindings();

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public String getLessonStatus(String lesson_id){
        String lessonStatus = "";
        android.database.Cursor c = db.rawQuery("SELECT DISTINCT status FROM lessons WHERE lesson_id = ?", new String[]{lesson_id});
        if (c.moveToFirst()){
            do {
                lessonStatus = c.getString(c.getColumnIndexOrThrow("status"));
            } while (c.moveToNext());
        }
        c.close();

        return lessonStatus;
    }

    public List<lessonsModel> loadIndividualLessons(String idno) {
        List<lessonsModel> lessons = new ArrayList<>();

        android.database.Cursor c = null;
        c = db.rawQuery("SELECT * FROM lessons WHERE id_no = ?", new String[]{idno});
        c.moveToFirst();

        if (!c.isAfterLast()){
            do {
                lessons.add(new lessonsModel(
                        c.getString(c.getColumnIndexOrThrow("sid")),
                        c.getString(c.getColumnIndexOrThrow("fullname")),
                        c.getString(c.getColumnIndexOrThrow("id_no")),
                        c.getString(c.getColumnIndexOrThrow("lesson_id")),
                        c.getString(c.getColumnIndexOrThrow("lesson_link")),
                        c.getString(c.getColumnIndexOrThrow("lesson_name"))
                ));
            }while (c.moveToNext());
        }
        c.close();

        return lessons;
    }

    public int countLessonsQuizes() {
        int res = 0;
        android.database.Cursor c = db.rawQuery("SELECT count(*) FROM lessons_quizes", null);
        if (!c.isAfterLast()){
            c.moveToFirst();
            do {
                res = c.getInt(0);
            }while (c.moveToNext());
        }
        c.close();

        return res;
    }

    public int countSyncPending() {

        int total = 0;

        int count_registered_drivers = 0;
        android.database.Cursor c1 = db.rawQuery("SELECT count(*) FROM drivers WHERE sync_status = ?", new String[]{"new_driver"});
        c1.moveToFirst();
        if (!c1.isAfterLast()){
            do {
                count_registered_drivers = c1.getInt(0);
            } while (c1.moveToNext());
        }
        c1.close();


        int count_answered_questions = 0;
        android.database.Cursor c2 = db.rawQuery("SELECT count(*) FROM user_answers_given WHERE sync_status = ?", new String[]{"p"});
        c2.moveToFirst();
        if (!c2.isAfterLast()){
            do {
                count_answered_questions = c2.getInt(0);
            } while (c2.moveToNext());
        }
        c2.close();

        total = count_registered_drivers+count_answered_questions;

        return total;
    }

    public int sync_period() {


        try {

            if (GlobalVariables.sync_time(act) == null) {
                return GlobalVariables.sync_interval_mins;
            }

            Date time = null;
            try {
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(GlobalVariables.sync_time(act));

                Log.e("LAST SYNC TIME =>", "" + GlobalVariables.sync_time(act));
            } catch (Exception ex) {
                Log.e("Time Error =>", ex.getMessage());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);

            long diffference = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();

            String minutes = "" + ((diffference / 1000) / 30);
            int min = Integer.parseInt(minutes);

            Log.e("Minutes", "=> " + min);

            return min;

        } catch (Exception ex) {
            return GlobalVariables.sync_interval_mins;
        }

    }

    public String checkIfIdExists(String id_no){
        String id_no_ = null;
        android.database.Cursor c = db.rawQuery("SELECT id_no FROM drivers WHERE id_no = ?", new String[]{id_no});
        c.moveToFirst();
        if (!c.isAfterLast()){
            do {
                id_no_ = c.getString(0);
            }while (c.moveToNext());
        } c.close();

        return id_no_;
    }

    public String getFullName(String id_no){
        String name = null;
        android.database.Cursor c = db.rawQuery("SELECT fullname FROM drivers WHERE id_no = ?", new String[]{id_no});
        c.moveToFirst();
        if (!c.isAfterLast()){
            do {
                name = c.getString(0);
            }while (c.moveToNext());
        } c.close();

        return name;
    }

    public boolean checkMedicalStatus(String id_no){
        android.database.Cursor c = db.rawQuery("SELECT medical_status FROM drivers WHERE id_no = ?", new String[]{id_no});
        c.moveToFirst();
        if (!c.isAfterLast()){
            do {
                if (c.getString(0) != null && c.getString(0).equalsIgnoreCase("true")){
                    return true;
                }
            }while (c.moveToNext());
        } c.close();

        return false;
    }

    public String checkIfMedicalStatus(String nat_id) {

        Log.e(TAG, "checkIfMedicalStatus: "+nat_id );
        String medicalStatus = "";
        android.database.Cursor c = db.rawQuery("SELECT DISTINCT medical_status FROM drivers WHERE id_no = ?", new String[]{nat_id});
        if (c.moveToFirst()){
            do {
                medicalStatus = c.getString(c.getColumnIndexOrThrow("medical_status"));
            } while (c.moveToNext());
        }
        c.close();

        return medicalStatus;
    }

    public String getDriverId(String nat_id) {

        Log.e(TAG, "driverid: "+nat_id );

        String medicalStatus = "";
        android.database.Cursor c = db.rawQuery("SELECT DISTINCT sid FROM drivers WHERE id_no = ?", new String[]{nat_id});
        if (c.moveToFirst()){
            do {
                medicalStatus = c.getString(c.getColumnIndexOrThrow("sid"));
            } while (c.moveToNext());
        }
        c.close();

        return medicalStatus;
    }

    public List<lessonsQuizesModel> loadLessonsQuizes(String lesson_id) {

        Log.e(TAG, "loadLessonsQuizes: "+lesson_id );

        List<lessonsQuizesModel> lessonsquizes = new ArrayList<>();

        android.database.Cursor c = null;
//        c = database.rawQuery("SELECT * FROM lessons_quizes WHERE lesson_id = ?", new String[]{lesson_id});
        c = db.rawQuery("SELECT lesson_id, quiz, quiz_id, COALESCE(is_answered,'not selected') as is_answered FROM lessons_quizes WHERE lesson_id = ?", new String[]{lesson_id});
        c.moveToFirst();

        if (!c.isAfterLast()){
            do {


                lessonsQuizesModel lmm = new lessonsQuizesModel(
                        c.getString(c.getColumnIndexOrThrow("lesson_id")),
                        c.getString(c.getColumnIndexOrThrow("quiz")),
                        c.getString(c.getColumnIndexOrThrow("quiz_id")),
                        c.getString(c.getColumnIndexOrThrow("is_answered"))
                );

                lessonsquizes.add(lmm);

            }while (c.moveToNext());
        }
        c.close();

        return lessonsquizes;
    }

    public String getQuestionAnswersSid(String quiz_id, String answer_option) {

        String answ_id = "";
        android.database.Cursor c = db.rawQuery("SELECT sid FROM quizes_answers WHERE quiz_id = ? AND answers = ?", new String[]{quiz_id, answer_option});
        if (c.moveToFirst()){
            do {
                answ_id = c.getString(c.getColumnIndexOrThrow("sid"));
            } while (c.moveToNext());
        }
        c.close();

        return answ_id;
    }

    public int checkIfAnswerGiven(String quiz_id, String id_no, String lesson_id) {

        int counting = 0;

        android.database.Cursor cursor1 = db.rawQuery("SELECT count(answer_option) FROM user_answers_given WHERE quiz_id = ? AND nat_id = ? AND lessonId = ? ", new String[]{quiz_id, id_no, lesson_id});
        cursor1.moveToFirst();
        if (!cursor1.isAfterLast()) {
            do {
                counting = cursor1.getInt(0);
            } while (cursor1.moveToNext());
        }
        cursor1.close();

        return counting;
    }

    public List<quizesAnswersModel> getQuestionAnswers(String quiz_id) {

        List<quizesAnswersModel> answers = new ArrayList<>();

        android.database.Cursor c = db.rawQuery("SELECT DISTINCT answers, sid, quiz_id FROM quizes_answers WHERE quiz_id = ? ", new String[]{quiz_id});
        c.moveToFirst();
        if (!c.isAfterLast()) {
            do {

                quizesAnswersModel tm = new quizesAnswersModel();
                tm.answers = c.getString(c.getColumnIndexOrThrow("answers"));
                tm.quiz_id = c.getString(c.getColumnIndexOrThrow("quiz_id"));
                tm.sid = c.getString(c.getColumnIndexOrThrow("sid"));

                answers.add(tm);

//                answers.add(new quizesAnswersModel(
//                        c.getString(c.getColumnIndexOrThrow("sid")),
//                        c.getString(c.getColumnIndexOrThrow("answers")),
//                        c.getString(c.getColumnIndexOrThrow("quiz_id"))
//                ));

//                String ans_name, ans_id;
//
//                ans_id = c.getString(c.getColumnIndexOrThrow("sid"));
//                ans_name = c.getString(c.getColumnIndexOrThrow("answers"));
//
//                if (ans_name.equalsIgnoreCase("null")) {
//                    ans_name = "";
//                }

//                answers.add(ans_name);
            } while (c.moveToNext());
        }
        c.close();

        return answers;
    }


}
