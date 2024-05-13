package com.example.secucapture.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.secucapture.Activities.DashboardActivity;
import com.example.secucapture.Activities.DriverAssessmentActivity;
import com.example.secucapture.Utils.AESHelper;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;
import com.example.secucapture.Utils.dataretriever;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyServiceIntent extends Service {

    Looper mServiceLooper;
    ServiceHandler mServiceHandler;
    HandlerThread thread;
    int startidserv;
    AESHelper aesHelper;
    DbHelper doa;
    private static final String surl = GlobalVariables.surl;
    JSONObject jsonjob = new JSONObject();
    JSONObject json1 = new JSONObject();

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String serverAuth;

    public static boolean is_passwordhashing = true;
    String url;
    String HTTPjob;
    String Params;
    long dComparer;
    int take = 1000;

    int take2 = 3000;
    int skip = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        //startForeground(1,new Notification());
        Log.v("BUILD VERSION ", Build.VERSION.SDK_INT + " SDK INT -- AND CODE " + Build.VERSION_CODES.O);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("JJJJJ", "JERE JERE");
            startMyOwnForeground();
        } else {
            Log.v("JJJJJ", "OTHER START JERE JERE");
            startForeground(1, new Notification());
        }
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }


    //handles foreground notifications to ensure it continues to run even when the app is in the background or closed.
    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "com.example.secucapture";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    //.setSmallIcon(AppSpecific.SMALL_ICON)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    // handles the service start. It obtains a message from the ServiceHandler and sends it for processing.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

//        if (GlobalVariabless.is_new_system) {
//            WeightCAPTURE = "WeightCAPTURE/";
//        } else {
//            WeightCAPTURE = "";
//        }
        return START_NOT_STICKY;
    }

    //handles cleanup tasks before the service is destroyed.
    @Override
    public void onDestroy() {

    }
    //extends Handler and is responsible for handling messages sent to the service.
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            super.handleMessage(msg);
            Log.e("KKKKKKK", "KKKKKKKKKKKKKK HERE ServiceHandler");
            startidserv = msg.arg1;
            aesHelper = new AESHelper(getApplicationContext());

            String DB_PATH = getExternalFilesDir(null).getAbsolutePath() + "/";

            String path_encrypted = DB_PATH + "secucapture.db";

            SQLiteDatabase.loadLibs(getApplicationContext());
            doa = new DbHelper(getApplicationContext());

            String url = surl + "SystemAccounts/Authentication/Login/Submit";
            try {
                postData(url, Login().toString(), "JobLogin");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //responsible for preparing JSON data
    public JSONObject Login() {

        String url = surl + "SystemAccounts/Authentication/Login/Submit";
        JSONObject json = new JSONObject();
        JSONObject json1 = new JSONObject();
        JSONObject jsonurl = new JSONObject();

        try {
            dataretriever data = new dataretriever();

//            data.password = getApplicationContext().getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE).getString("pass", "");
//            data.username = getApplicationContext().getSharedPreferences("com.example.secucapture", Context.MODE_PRIVATE).getString("user_model", "");
//
            data.password = "admin123";
            data.username = "test_driver";


            if (GlobalVariables.is_pps.equalsIgnoreCase("Yes")) {

                json.put("PassWord", data.password);
                json.put("UserName", data.username);
                json.put("Language", "English");
                jsonurl.put("url", url);
                jsonjob.put("httpjob", "JobLogin");
                json1.put("CurrentUser", json);
                json1.put("Language", "English");
                json1.put("IsRenewalPasswordRequest", false);

            } else {

                json.put("PassWord", data.password);
                json.put("UserName", data.username);
                json.put("AccountName", data.account_name);
                json.put("Branch", data.branch);
                json.put("Language", "English");
                jsonurl.put("url", url);
                jsonjob.put("httpjob", "JobLogin");
                json1.put("CurrentUser", json);
                json1.put("Language", "English");
                json1.put("IsRenewalPasswordRequest", false);

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        JSONObject[] job = {json1, jsonurl, jsonjob};

        return json1;

    }

    //makes HTTP POST requests using OkHttp client. It sends JSON data to the specified URL and handles response asynchronously using callbacks.
    public void postData(String url, String json, String http_job) throws IOException {
        Log.v(http_job + "====", "MyIntentService POST OBJECT DATA " + json);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        client = builder.build();

        RequestBody body = RequestBody.create(json, JSON);
        Request request = null;
        request = new Request.Builder()
                .url(url)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Content-Type", "application/json;")
                .post(body)
                .build();
        if (!http_job.matches("JobLogin")) {
            request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Content-Type", "application/json;")
                    .addHeader("Authorization", serverAuth)
                    .post(body)
                    .build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    jobDone("error", "error");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();

            }


            /**@Override
            public void onResponse(Call call, Response response) throws IOException {
                //try (ResponseBody responseBody = response.body()) {
                try {
                    if (!response.isSuccessful()) {
                        // Handle unsuccessful response
                        ResponseBody errorResponseBody = response.peekBody(Long.MAX_VALUE);
                        jobDone(errorResponseBody.string(), http_job);
                        throw new IOException("Unexpected code " + response);
                    }

                    // Check content type
                    String contentType = response.header("Content-Type");
                    if (contentType != null && contentType.contains("application/json")) {
                        // Response is JSON
                        JSONObject responseBodyJson = new JSONObject(response.body().string());
                        serverAuth = response.header("Authorization");
                        jobDone(responseBodyJson.toString(), http_job);
                    } else {
                        // Response is not JSON
                        String responseBody = response.body().string();
                        Log.e("Response Body", responseBody);
                        // Handle non-JSON response here, for example:
                        // jobDone(responseBody, http_job);
                    }
                } catch (Exception e) {
                    Log.e("FFFFFFFFF", "kkkkkkkkkkkkkkk" + e.getMessage());
                    e.printStackTrace();
                }
            }**/
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //try (ResponseBody responseBody = response.body()) {
                try {
                    if (!response.isSuccessful()) {

                        ResponseBody error_responce = response.peekBody(Long.MAX_VALUE);

                        jobDone(error_responce.string(), http_job);
                        throw new IOException("Unexpected code " + response);
                    }


                    serverAuth = response.header("Authorization");

                    ResponseBody responseBodyCopy = response.peekBody(Long.MAX_VALUE);

                    jobDone(responseBodyCopy.string(), http_job);
                } catch (Exception e) {
                    Log.e("FFFFFFFFF", "kkkkkkkkkkkkkkk" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    //processes responses received from network requests. It parses JSON responses, performs data insertion or updates in the local database (DbHelper), and triggers subsequent actions based on the response.
    @SuppressLint("NewApi")
    public void jobDone(String httpjobresponse, String httpjob) throws JSONException, IOException{
        String SW = httpjob;

        try {
            Log.v(SW, new JSONObject(httpjobresponse).toString());
        } catch (JSONException e) {
            endService("ERROR");
            Log.v(SW, httpjobresponse);
            return;
        }

        JSONObject jObj = new JSONObject(httpjobresponse);

        Log.v(SW, jObj.toString());

        switch (SW) {
            case "JobLogin":

                Log.e("service", "jobDone: "+GlobalVariables.getSafety_sync_service());

//                fetchAssessments();
                fetchRegisteredDrivers();

//                if (GlobalVariables.getSafety_sync_service().equalsIgnoreCase("fetch_lessons")){
//                    fetchIndividualLessons();
//                }else {
//                    fetchRegisteredDrivers(); //original
//                }

            break;

            case "JobGetRegisteredDrivers":

                int registered_drivers = 0;

                Log.e("REGISTERED DRIVERS OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")){

//                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            registered_drivers = jObj.getJSONObject("Result").getJSONArray("Result").length(); // extracts result from jobj and gets it length.

                            doa.InsertRegisteredDrivers(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext()); //insert result array  into db usong method InsertRegisteredDrivers.

                            Log.e("+++++++", "registered drivers: " + registered_drivers);

//                        }
                    }
                }catch (Exception e){
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }

                fetchDriverOrganization();

            break;


            case "JobGetDriverOrganization":

                int driverOrgCount = 0;

                Log.e("Driver Organization OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")) {

                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            driverOrgCount = jObj.getJSONObject("Result").getJSONArray("Result").length();

                            doa.InsertDriverOrganizations(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext());

                            Log.e("+++++++", "driver organizations: " + driverOrgCount);

                        }


                    }
                } catch (Exception e) {
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }
                fetchCourses();

                break;


            case "JobGetCourses":
                int courseCount = 0;

                Log.e("COURSES OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")) {

                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            courseCount = jObj.getJSONObject("Result").getJSONArray("Result").length();

                            doa.InsertCourses(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext());

                            Log.e("+++++++", "courses: " + courseCount);

                        }
                    }
                } catch (Exception e) {
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }
                fetchTutorials();

                break;


            case "JobGetTutorials":

                int tutorialCount = 0;

                Log.e("TUTORIALS OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")) {

                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            tutorialCount = jObj.getJSONObject("Result").getJSONArray("Result").length();

                            doa.InsertTutorials(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext());

                            Log.e("+++++++", "tutorials: " + tutorialCount);

                        }
                    }
                } catch (Exception e) {
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }

                fetchQuestions();

                break;

            case "JobGetQuestions":
                int questionCount = 0;

                Log.e("QUESTIONS OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")) {

                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            questionCount = jObj.getJSONObject("Result").getJSONArray("Result").length();

                            doa.InsertQuestions(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext());

                            Log.e("+++++++", "questions: " + questionCount);

                        }
                    }
                } catch (Exception e) {
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }

                fetchChoices();

                break;

            case "JobGetChoices":
                int choiceCount = 0;

                Log.e("CHOICES OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")) {

                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            choiceCount = jObj.getJSONObject("Result").getJSONArray("Result").length();

                            doa.InsertChoices(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext());

                            Log.e("+++++++", "choices: " + choiceCount);

                        }
                    }
                } catch (Exception e) {
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }

                fetchAssessments();

                break;

            case "JobGetAssessments":
                int assessmentCount = 0;

                Log.e("ASSESSMENTS OBJ==>>", ": "+ jObj.toString());

                try {
                    if (!jObj.getString("StatusCode").endsWith("603")) {

                        if (!jObj.getJSONObject("Result").getString("Result").equals("null")) {

                            assessmentCount = jObj.getJSONObject("Result").getJSONArray("Result").length();

                              doa.InsertAssessments(jObj.getJSONObject("Result").getJSONArray("Result"), getApplicationContext());

                            Log.e("+++++++", "assessments: " + assessmentCount);

                        }
                    }
                } catch (Exception e) {
                    Log.e("Catch:: ", "ERR--->>>" + e.getMessage());
                    e.printStackTrace();
                }

                if (assessmentCount > 0) {
                    endService("All Data Fetched Successfully");
                } else {
                    endService("No Data");
                }

                break;

        }
    }

    //handles the termination of the service. It deletes cached data, stops any progress dialogs, and stops the service itself based on the service start mode.
    public void endService(final String message) {
        deleteCache(getApplicationContext());
        Log.e("hehehehehehe", "jjjjjjjjjjjjjjj" + GlobalVariables.service_start_mode);

        if (GlobalVariables.service_start_mode == null) {

        } else if (GlobalVariables.service_start_mode.equalsIgnoreCase("manual")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    DashboardActivity.stopPD();
                    GlobalVariables.the_service_start = "stop";

                }
            });


        } else if (GlobalVariables.service_start_mode.equalsIgnoreCase("manual_fetch_lessons")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    DriverAssessmentActivity.stopPD2(message);
                    GlobalVariables.the_service_start = "stop";

                }
            });


        }

        stopSelf();
    }
    private void fetchRegisteredDrivers() {
        url = surl + "SafetyCapture/Mobileservices/GetRegisteredDrivers";
        dComparer = doa.getMaxDateComparer("drivers");

        try {
            JSONObject jfilters = new JSONObject();
            jfilters.put("field", "datecomparer");
            jfilters.put("operator", "gt");
            jfilters.put("value", dComparer);
//            jfilters.put("value", 0);

            JSONArray filtersArray = new JSONArray();
            filtersArray.put(jfilters);

            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            new JSONObject();
            json1.put("filter", filterObject);
            json1.put("take", take);
            json1.put("skip", skip);

            postData(url, json1.toString(), "JobGetRegisteredDrivers");

        } catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }

    private void  fetchDriverOrganization(){

        url = surl + "SafetyCapture/Mobileservices/DriverOrganization";
        dComparer = doa.getMaxDateComparer("drivers_organizations");

        try {
            JSONObject jfilters = new JSONObject();
            jfilters.put("field", "datecomparer");
            jfilters.put("operator", "gt");
            jfilters.put("value", dComparer);
//            jfilters.put("value", 0);

            JSONArray filtersArray = new JSONArray();
            filtersArray.put(jfilters);

            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            new JSONObject();
            json1.put("filter", filterObject);
            json1.put("take", take2);
            json1.put("skip", skip);

            postData(url, json1.toString(), "JobGetDriverOrganization");

        } catch (JSONException | IOException e){
            e.printStackTrace();
        }

    }

    private void fetchCourses() {
        // Set the URL for the API endpoint
        String url = surl + "SafetyCapture/Mobileservices/getCoursesList";
        dComparer = doa.getMaxDateComparer("courses");


        try {
            // Construct the JSON request body
            JSONObject requestBody = new JSONObject();

            // Construct filters object
            JSONObject filters = new JSONObject();
            filters.put("field", "id");
            filters.put("operator", "gt");
            filters.put("value", 0);

            // Construct filters array
            JSONArray filtersArray = new JSONArray();
            filtersArray.put(filters);

            // Construct filterObject
            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            // Add filterObject, take, and skip to the request body
            requestBody.put("filter", filterObject);
            requestBody.put("take", take2);
            requestBody.put("skip", skip);

            // Convert the JSON request body to a string and make the POST request
            postData(url, requestBody.toString(), "JobGetCourses");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            // Handle the error here
            // For example, you can call endService method with an appropriate error message
            endService("Error fetching courses: " + e.getMessage());
        }
    }


    private  void fetchTutorials(){

        url = surl + "SafetyCapture/Mobileservices/getTutorialsList";
        dComparer = doa.getMaxDateComparer("tutorials");

        try {
            JSONObject jfilters = new JSONObject();
            jfilters.put("field", "datecomparer");
            jfilters.put("operator", "gt");
            jfilters.put("value", dComparer);
//            jfilters.put("value", 0);

            JSONArray filtersArray = new JSONArray();
            filtersArray.put(jfilters);

            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            new JSONObject();
            json1.put("filter", filterObject);
            json1.put("take", take2);
            json1.put("skip", skip);

            postData(url, json1.toString(), "JobGetTutorials");

        } catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }

    private void fetchAssessments(){
        url = surl + "SafetyCapture/Mobileservices/getTutotialAssessment";
//        url = surl + "SafetyCapture/Mobileservices/getTutorialAssessment";
        dComparer = doa.getMaxDateComparer("assessments");

        try {
            JSONObject jfilters = new JSONObject();
            jfilters.put("field", "datecomparer");
            jfilters.put("operator", "gt");
            jfilters.put("value", dComparer);
           // jfilters.put("value", 0);

            JSONArray filtersArray = new JSONArray();
            filtersArray.put(jfilters);

            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            new JSONObject();
            json1.put("filter", filterObject);
            json1.put("take", take2);
            json1.put("skip", skip);

            postData(url, json1.toString(), "JobGetAssessments");

        } catch (JSONException | IOException e){
            e.printStackTrace();
        }

    }

    private  void fetchQuestions(){


        url = surl + "SafetyCapture/Mobileservices/GetTutorialAssessmentQuestions";
        dComparer = doa.getMaxDateComparer("questions");

        try {
            JSONObject jfilters = new JSONObject();
            jfilters.put("field", "datecomparer");
            jfilters.put("operator", "gt");
            jfilters.put("value", dComparer);
//            jfilters.put("value", 0);

            JSONArray filtersArray = new JSONArray();
            filtersArray.put(jfilters);

            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            new JSONObject();
            json1.put("filter", filterObject);
            json1.put("take", take2);
            json1.put("skip", skip);

            postData(url, json1.toString(), "JobGetQuestions");

        } catch (JSONException | IOException e){
            e.printStackTrace();
        }

    }

    private void fetchChoices(){


        url = surl + "SafetyCapture/Mobileservices/GetTutorialAssessmentQuestionsChoices";
        dComparer = doa.getMaxDateComparer("choices");

        try {
            JSONObject jfilters = new JSONObject();
            jfilters.put("field", "datecomparer");
            jfilters.put("operator", "gt");
            jfilters.put("value", dComparer);
//            jfilters.put("value", 0);

            JSONArray filtersArray = new JSONArray();
            filtersArray.put(jfilters);

            JSONObject filterObject = new JSONObject();
            filterObject.put("filters", filtersArray);
            filterObject.put("logic", "AND");

            new JSONObject();
            json1.put("filter", filterObject);
            json1.put("take", take2);
            json1.put("skip", skip);

            postData(url, json1.toString(), "JobGetChoices");

        } catch (JSONException | IOException e){
            e.printStackTrace();
        }

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
