package com.example.secucapture.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secucapture.Adapters.lessonsAdapter;
import com.example.secucapture.R;
import com.example.secucapture.Services.MyServiceIntent;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;
import com.example.secucapture.models.lessonsModel;

import java.util.ArrayList;
import java.util.List;

public class DriverAssessmentActivity extends AppCompatActivity {


    public static Activity act;
    public static Context ctx;

    static ProgressDialog pd2;

    public TextView txtdrivername;


    static lessonsAdapter adpt;

    static List<lessonsModel> lessonsData = new ArrayList<>();


    static DbHelper doa;

    Button fetch_lesson_btn;

    public ListView lessonsLst;

    EditText edt_id_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_dashboard);

        /**lesson_1_txt = findViewById(R.id.lesson_1_txt);
        lesson_2_txt = findViewById(R.id.lesson_2_txt);
        lesson_3_txt = findViewById(R.id.lesson_3_txt);
        lesson_4_txt = findViewById(R.id.lesson_4_txt);
        lesson_5_txt = findViewById(R.id.lesson_5_txt);
        username = findViewById(R.id.username);**/

        lessonsLst = (ListView) findViewById(R.id.lessonsLst);
        edt_id_no = (EditText) findViewById(R.id.edt_id_no);
        txtdrivername = (TextView) findViewById(R.id.txtdrivername);
        fetch_lesson_btn = (Button) findViewById(R.id.fetch_lesson_btn);
        txtdrivername.setText(""+ GlobalVariables.fullname);

        pd2 = new ProgressDialog(this);

        lessonsLst.setBackgroundColor(Color.TRANSPARENT);
        adpt = new lessonsAdapter(this, lessonsData);
        lessonsLst.setAdapter(adpt);

        doa = new DbHelper(this);

        /**lesson_1_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        lesson_2_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        lesson_3_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        lesson_4_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        lesson_5_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });**/

        fetch_lesson_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fullname = doa.getFullName(edt_id_no.getText().toString().trim());
                GlobalVariables.fullname = fullname;

                if (edt_id_no.getText().toString().trim().isEmpty()){
                    edt_id_no.setError("Required!");
                    edt_id_no.requestFocus();
                    return;

                } else {

                    String isMedicallyApproved = "";
                    isMedicallyApproved = doa.checkIfMedicalStatus(edt_id_no.getText().toString().trim());

                    Log.e("isMedicallyApproved", "onClick: "+isMedicallyApproved );
                    //if(isMedicallyApproved.equalsIgnoreCase("true")){
                    if(isMedicallyApproved != null && isMedicallyApproved.equalsIgnoreCase("true")){


                            GlobalVariables.id_no = edt_id_no.getText().toString().trim();

                        try {
                            pd2.setMessage(getResources().getString(R.string.wait));
                            pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd2.setIndeterminate(true);
                            pd2.setCanceledOnTouchOutside(true);
                            pd2.show();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                GlobalVariables.service_start_mode = "manual_fetch_lessons";
                                GlobalVariables.setSafety_sync_service("fetch_lessons");
                                act.startForegroundService(new Intent(act, MyServiceIntent.class));

                            } else {

                                GlobalVariables.service_start_mode = "manual_fetch_lessons";
                                GlobalVariables.setSafety_sync_service("fetch_lessons");
                                act.startService(new Intent(act, MyServiceIntent.class));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (isMedicallyApproved != null && isMedicallyApproved.equalsIgnoreCase("false")) {


                        _showNotApprovedDialog();

                    } else {
                        Toast.makeText(DriverAssessmentActivity.this, "ID number does not exist!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        lessonsLst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lessonsModel m = adpt.getItem(i);

                String lesson_id = m.getLesson_id();
                String lesson_link = m.getLesson_link();

                GlobalVariables.lesson_id = lesson_id;

                Intent intent = new Intent(DriverAssessmentActivity.this, DriversLessonsVideosActivity.class);
                intent.putExtra("lesson_id", lesson_id);
                intent.putExtra("lesson_link", lesson_link);
                startActivity(intent);
                finish();


            }
        });
    }

   void _showNotApprovedDialog() {

       android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DriverAssessmentActivity.this);
       LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
       View view_ = layoutInflaterAndroid.inflate(R.layout.not_approved_dialog, null);
       builder.setView(view_);
       builder.setCancelable(true);

       final android.app.AlertDialog alertDialog = builder.create();
       alertDialog.show();

       TextView warning_text = (TextView) view_.findViewById(R.id.warning_text);
       Button exitButton = (Button) view_.findViewById(R.id.exitButton);

       exitButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
               DriverAssessmentActivity.this.finish();
               startActivity(intent);

               alertDialog.dismiss();
           }
       });
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

        new LongOperation().execute("");

    }

    public static class LongOperation extends AsyncTask<String, Void, DriverAssessmentActivity.WrapperPostExecute> {
        @Override
        protected DriverAssessmentActivity.WrapperPostExecute doInBackground(String... params) {

            lessonsData = doa.loadIndividualLessons(""+GlobalVariables.id_no);

            DriverAssessmentActivity.WrapperPostExecute w = new WrapperPostExecute();

            return w;
        }

        @Override
        protected void onPostExecute(DriverAssessmentActivity.WrapperPostExecute result) {

            adpt.clear();
            adpt.addAll(lessonsData);
            adpt.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }
    public static class WrapperPostExecute
    {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        DriverAssessmentActivity.this.finish();
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalVariables.id_no == null){
            edt_id_no.setText("");
        } else {
            edt_id_no.setText("" + GlobalVariables.id_no);
        }
    }


}