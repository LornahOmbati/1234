package com.example.secucapture.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secucapture.R;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.models.quizesAnswersModel;

import java.util.List;

//public class driverQuestionsAnswersAdapter extends ArrayAdapter<quizesAnswersModel> {
public class driverQuestionsAnswersAdapter extends RecyclerView.Adapter<driverQuestionsAnswersAdapter.ViewHolder> {

    private final Context context;
    private List<quizesAnswersModel> answersList;
    private final List<quizesAnswersModel> origquizesList;
    public static DbHelper sd;

    public driverQuestionsAnswersAdapter(List<quizesAnswersModel> answersList, Context ctx) {
        this.answersList = answersList;
        this.context = ctx;
        this.origquizesList = answersList;
        sd = new DbHelper(context, (Activity) context);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return answersList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_lyt, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        quizesAnswersModel currentAnswer = answersList.get(position);
        holder.answer_checkbox.setText(""+currentAnswer.answers);

        holder.answer_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                currentAnswer.setChecked(b);


            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox answer_checkbox;

        public ViewHolder(@NonNull View v) {
            super(v);

            answer_checkbox = (CheckBox) v.findViewById(R.id.answer_checkbox);

        }
    }



//    public driverQuestionsAnswersAdapter(Context ctx, List<quizesAnswersModel> answersList) {
//        super(ctx, R.layout.checkbox_lyt, answersList);
//
//        this.answersList = answersList;
//        this.context = ctx;
//        this.origquizesList = answersList;
//        sd = new DbHelper(context,context);
//    }
//    public int getCount() {
//        return answersList.size();
//    }
//
//    public quizesAnswersModel getItem(int position) {
//        return answersList.get(position);
//    }
//
//    public long getItemId(int position) {
//        return answersList.get(position).hashCode();
//    }
//
//
//    public View getView(int position, View convertView, ViewGroup parent){
//        View v = convertView;
//
//        answersHolder holder = new answersHolder();
//
//        if (convertView == null){
//
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(R.layout.checkbox_lyt, null);
//
//            CheckBox answer_checkbox = (CheckBox) v.findViewById(R.id.answer_checkbox);
//
//            holder.answer_checkbox = answer_checkbox;
//
//            v.setTag(holder);
//
//            //if checked, log sid
//            holder.answer_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                    if(isChecked) {
//
//                        lessonsQuizesModel item_ = GlobalVariables.questions.get(position);
//                        item_.setIs_answered(answersList.get(position).getAnswers());
//
//                        Log.e("checkbox_name", "getView: "+ answersList.get(position).getAnswers());
//                        Log.e("checkbox_id", "getView: "+ answersList.get(position).getSid());
//
//                    }
//                }
//            });
//
//        }
//        else
//            holder = (answersHolder) v.getTag();
//
//        quizesAnswersModel lm = answersList.get(position);
//
//        holder.answer_checkbox.setText(""+lm.getAnswers());
//
//        return v;
//
//    }
//
//    private static class answersHolder{
//        public CheckBox answer_checkbox;
//    }
//
//    public void resetData() {
//        answersList = origquizesList;
//    }

}
