package com.example.secucapture.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secucapture.Adapters.lessonsQuizesAdapter;
import com.example.secucapture.R;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;
import com.example.secucapture.models.lessonsQuizesModel;

import java.util.ArrayList;
import java.util.List;

public class DriversLessonsQuizesActivity extends AppCompatActivity {

    public static Activity act;
    public static Context ctx;
    public static DbHelper doa;
    static ProgressDialog pd2;

    public Button btnSubmitAnswers;
    public RecyclerView lessonsQuizesLst;
    private lessonsQuizesAdapter adpt;

    static List<lessonsQuizesModel> lessonsQuizData = new ArrayList<>();

    public String answer_sid = "";

    public String driver_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_lessons_quizes);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        act = this;
        ctx = this;
        doa = new DbHelper(ctx, act);
        pd2 = new ProgressDialog(ctx);

        driver_id = doa.getDriverId( GlobalVariables.id_no);

        btnSubmitAnswers = findViewById(R.id.btnSubmitAnswers);
        lessonsQuizesLst = (RecyclerView) findViewById(R.id.lessonsQuizesLst);

        DividerItemDecoration verticalDecoration = new DividerItemDecoration(lessonsQuizesLst.getContext(), DividerItemDecoration.HORIZONTAL);
        Drawable verticalDivider = ContextCompat.getDrawable(getApplicationContext(), R.drawable.verticle_separator);
        verticalDecoration.setDrawable(verticalDivider);
        lessonsQuizesLst.addItemDecoration(verticalDecoration);
        lessonsQuizesLst.addItemDecoration(new DividerItemDecoration(lessonsQuizesLst.getContext(), DividerItemDecoration.VERTICAL));
        lessonsQuizesLst.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        GlobalVariables.questions = doa.loadLessonsQuizes(""+GlobalVariables.lesson_id);

        adpt = new lessonsQuizesAdapter(GlobalVariables.questions,getApplicationContext());
        lessonsQuizesLst.setAdapter(adpt);

        btnSubmitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                for (int x = 0; x < GlobalVariables.questions.size(); x++) {

                    String transaction_no = "ANS_T" + "_" + System.currentTimeMillis();

                    answer_sid = doa.getQuestionAnswersSid(GlobalVariables.questions.get(x).getQuiz_id(), GlobalVariables.questions.get(x).getIs_answered());

                    //save and update in answers table
                    ContentValues cv = new ContentValues();

                    cv.put("answer_id", answer_sid);
                    cv.put("transaction_no", transaction_no);
                    cv.put("answer_option", GlobalVariables.questions.get(x).getIs_answered());
                    cv.put("quiz_id", GlobalVariables.questions.get(x).getQuiz_id());
                    cv.put("sync_status", "p");
                    cv.put("datetime", "" + GlobalVariables.dateNow());
                    cv.put("status", "Closed");
                    cv.put("nat_id", "" + GlobalVariables.id_no);
                    cv.put("driver_id", "" + driver_id);
                    cv.put("lessonId", "" + GlobalVariables.lesson_id);

                    if (doa.checkIfAnswerGiven(GlobalVariables.questions.get(x).getQuiz_id(), GlobalVariables.id_no, GlobalVariables.lesson_id) > 0) {

                        String WhereClause = "quiz_id= ? AND nat_id = ? AND lessonId = ?";
                        String[] WhereArgs = {"" + GlobalVariables.questions.get(x).getQuiz_id(), "" + GlobalVariables.id_no, "" + GlobalVariables.lesson_id};

                        int updated = doa.db.update("user_answers_given", cv, WhereClause, WhereArgs);

                        Log.e("updating answered quiz", "onClick: " + updated);

                    } else {

                        Long inserting_answers = doa.db.insertOrThrow("user_answers_given", null, cv);

                        Log.e("inserting answered quiz", "onClick: " + inserting_answers);

                    }

                    try {
                        //update in lessons table
                        ContentValues cv2 = new ContentValues();
                        cv2.put("status", "closed");

//                            String WhereClause = "nat_id = ? AND lessonId = ?";//use this
                        String WhereClause2 = "lesson_id = ?";//use this
                        String[] WhereArgs2 = {"" + GlobalVariables.lesson_id};

                        int updated_lesson = doa.db.update("lessons", cv2, WhereClause2, WhereArgs2);

                        Log.e("updated lesson", "onClick: " + updated_lesson);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //run service to populate score

                    Intent intent = new Intent(getApplicationContext(), DriverAssessmentActivity.class);
                    startActivity(intent);
                    DriversLessonsQuizesActivity.this.finish();
                }

                GlobalVariables.questions.clear();

//                int count_unselected = 0;
//                for(int x = 0;  x<GlobalVariables.questions.size();  x++) {
//
//                    Log.e("sid", "onClick: "+GlobalVariables.questions.get(x).getQuiz_id());
//                    Log.v(" jjjjj "+GlobalVariables.questions.get(x).getQuiz_id(), GlobalVariables.questions.get(x).getIs_answered());
//
//                    if(GlobalVariables.questions.get(x).getIs_answered().equalsIgnoreCase("not selected"))
//                    {
//                        count_unselected+=1;
//                    }
//
//                }
//
//                if(count_unselected > 0) {
//
//                    Toast.makeText(getApplicationContext(), "You must fill All Options", Toast.LENGTH_LONG).show();
//                    return;
//
//                } else {
//
//                    for (int x = 0; x < GlobalVariables.questions.size(); x++) {
//
//                        String transaction_no = "ANS_T" + "_" + System.currentTimeMillis();
//
//                        answer_sid = doa.getQuestionAnswersSid(GlobalVariables.questions.get(x).getQuiz_id(), GlobalVariables.questions.get(x).getIs_answered());
//
//                        //save and update in answers table
//                        ContentValues cv = new ContentValues();
//
//                        cv.put("answer_id", answer_sid);
//                        cv.put("transaction_no", transaction_no);
//                        cv.put("answer_option", GlobalVariables.questions.get(x).getIs_answered());
//                        cv.put("quiz_id", GlobalVariables.questions.get(x).getQuiz_id());
//                        cv.put("sync_status", "p");
//                        cv.put("datetime", "" + GlobalVariables.dateNow());
//                        cv.put("status", "Closed");
//                        cv.put("nat_id", "" + GlobalVariables.id_no);
//                        cv.put("driver_id", "" + driver_id);
//                        cv.put("lessonId", "" + GlobalVariables.lesson_id);
//
//                        if (doa.checkIfAnswerGiven(GlobalVariables.questions.get(x).getQuiz_id(), GlobalVariables.id_no, GlobalVariables.lesson_id) > 0) {
//
//                            String WhereClause = "quiz_id= ? AND nat_id = ? AND lessonId = ?";
//                            String[] WhereArgs = {"" + GlobalVariables.questions.get(x).getQuiz_id(), "" + GlobalVariables.id_no, "" + GlobalVariables.lesson_id};
//
//                            int updated = doa.database.update("user_answers_given", cv, WhereClause, WhereArgs);
//
//                            Log.e("updating answered quiz", "onClick: " + updated);
//
//                        } else {
//
//                            Long inserting_answers = doa.database.insertOrThrow("user_answers_given", null, cv);
//
//                            Log.e("inserting answered quiz", "onClick: " + inserting_answers);
//
//                        }
//
//                        try {
//                            //update in lessons table
//                            ContentValues cv2 = new ContentValues();
//                            cv2.put("status", "closed");
//
////                            String WhereClause = "nat_id = ? AND lessonId = ?";//use this
//                            String WhereClause2 = "lesson_id = ?";//use this
//                            String[] WhereArgs2 = {"" + GlobalVariables.lesson_id};
//
//                            int updated_lesson = doa.database.update("lessons", cv2, WhereClause2, WhereArgs2);
//
//                            Log.e("updated lesson", "onClick: " + updated_lesson);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //run service to populate score
//
//                        Intent intent = new Intent(getApplicationContext(), LessonsDashboardActivity.class);
//                        startActivity(intent);
//                        DriversLessonsQuizesActivity.this.finish();
//
//
//                    }
//                }
//                GlobalVariables.questions.clear();

            }
        });

//        try {
//            pd2.setMessage(getResources().getString(R.string.wait));
//            pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            pd2.setIndeterminate(true);
//            pd2.setCanceledOnTouchOutside(true);
//            pd2.show();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                GlobalVariables.service_start_mode = "manual_fetch_lessons_quizes";
//                GlobalVariables.setSafety_sync_service("fetch_lessons_quizes");
//                act.startForegroundService(new Intent(act, MyServiceIntent.class));
//
//            } else {
//
//                GlobalVariables.service_start_mode = "manual_fetch_lessons_quizes";
//                GlobalVariables.setSafety_sync_service("fetch_lessons_quizes");
//                act.startService(new Intent(act, MyServiceIntent.class));
//
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }


//        lessonsQuizesLst.setBackgroundColor(Color.TRANSPARENT);
//        adpt = new lessonsQuizesAdapter(ctx, lessonsQuizData);
//        lessonsQuizesLst.setAdapter(adpt);

//        Log.e("lessons", "onCreate: "+ lessonsQuizData.size());
//        new DriversLessonsQuizesActivity.LongOperation().execute("");
//        registerForContextMenu(lessonsQuizesLst);
    }

//    public static class LongOperation extends AsyncTask<String, Void, LessonsDashboardActivity.WrapperPostExecute> {
//        @Override
//        protected LessonsDashboardActivity.WrapperPostExecute doInBackground(String... params) {
//
//            lessonsQuizData = doa.loadLessonsQuizes(""+GlobalVariables.lesson_id);
//
//            LessonsDashboardActivity.WrapperPostExecute w = new LessonsDashboardActivity.WrapperPostExecute();
//
//            return w;
//        }
//
//        @Override
//        protected void onPostExecute(LessonsDashboardActivity.WrapperPostExecute result) {
//
//            adpt.clear();
//            adpt.addAll(lessonsQuizData);
//            adpt.notifyDataSetChanged();
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//    }
//
//    public static class WrapperPostExecute
//    {
//
//    }
//    public static void stopPD(String message) {
//        if(!message.equalsIgnoreCase("unauthorized"))
//        {
//            Toast.makeText(act, message, Toast.LENGTH_LONG).show();
//        }
//
//        GlobalVariables.the_service_start = "stop";
//
//        if(pd2.isShowing()) {
//            pd2.dismiss();
//        }
//
//
//        new DriversLessonsQuizesActivity.LongOperation().execute("");
//
//    }

}