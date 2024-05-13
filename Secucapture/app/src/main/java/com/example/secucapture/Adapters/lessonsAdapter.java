package com.example.secucapture.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.secucapture.R;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.models.lessonsModel;

import java.util.List;

public class lessonsAdapter extends ArrayAdapter<lessonsModel> {
    private final Context context;
    private List<lessonsModel> lessonsList;
    private final List<lessonsModel> origlessonsList;

    public static DbHelper doa;

    public lessonsAdapter(Context ctx, List<lessonsModel> lessonsList) {
        super(ctx, R.layout.lessons_list, lessonsList);

        this.lessonsList = lessonsList;
        this.context = ctx;
        this.origlessonsList = lessonsList;
        doa = new DbHelper(context, (Activity) context);
    }

    public int getCount() {
        return lessonsList.size();
    }

    public lessonsModel getItem(int position) {
        return lessonsList.get(position);
    }

    public long getItemId(int position) {
        return lessonsList.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;

        LessonsHolder holder = new LessonsHolder();

        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.lessons_list, null);

            TextView lesson_name = (TextView) v.findViewById(R.id.lesson_name);
            TextView lesson_status = (TextView) v.findViewById(R.id.lesson_status);

            holder.lesson_name = lesson_name;
            holder.lesson_status = lesson_status;

            v.setTag(holder);
        }
        else
            holder = (LessonsHolder) v.getTag();

        lessonsModel lm = lessonsList.get(position);

        holder.lesson_name.setText(""+lm.getLesson_name());
        String lessonStatus = doa.getLessonStatus(""+lm.getLesson_id());
        Log.e("lessonStatus", "getView: "+lessonStatus );
        if (lessonStatus.equalsIgnoreCase("open")){
            holder.lesson_status.setText( "Not Done");
            holder.lesson_status.setTextColor(Color.parseColor("#f20707"));
        } else if(lessonStatus.equalsIgnoreCase("closed")){
            holder.lesson_status.setText( "Done");
            holder.lesson_status.setTextColor(Color.parseColor("#006400"));
        }
//        holder.lesson_status.setText( "Not Done");

//        if (lessonStatus.equalsIgnoreCase("open")) {
//            holder.lesson_status.setText("Status:" + "Not Done");
//            holder.lesson_status.setTextColor(Color.parseColor("#f20707"));
//        } else if (lessonStatus.equalsIgnoreCase("closed")){
//            holder.lesson_status.setText("Status:" + "Done");
//            holder.lesson_status.setTextColor(Color.parseColor("#07f259"));
//        }

        return v;
    }

    private static class LessonsHolder{

        public TextView lesson_name;
        public TextView lesson_status;
    }

    public void resetData() {
        lessonsList = origlessonsList;
    }

}
