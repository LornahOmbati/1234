package com.example.secucapture.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secucapture.R;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;
import com.example.secucapture.models.lessonsQuizesModel;
import com.example.secucapture.models.quizesAnswersModel;

import java.util.List;

public class lessonsQuizesAdapter extends RecyclerView.Adapter<lessonsQuizesAdapter.View_Holder> implements Filterable {

    private final Context context;
    private List<lessonsQuizesModel> lessonsQuizesList;
    private final List<lessonsQuizesModel> origlessonsquizesList;

    private driverQuestionsAnswersAdapter adpt;
    ArrayAdapter adapter;
    public static DbHelper sd;

    public lessonsQuizesAdapter(List<lessonsQuizesModel> lessonsQuizesList, Context ctx) {
        this.lessonsQuizesList = lessonsQuizesList;
        this.context = ctx;
        this.origlessonsquizesList = lessonsQuizesList;
        sd = new DbHelper(context, (Activity) context);

    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lessons_quizes, parent, false);
        View_Holder holder = new View_Holder(v);
        holder.setIsRecyclable(false);
        //setHasStableIds(true);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder holder, final int position) {

        holder.bind(position);

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
        //returns the number of elements the RecyclerView will display
        return lessonsQuizesList.size();
    }
    @Override
    public Filter getFilter() {
        return null;
    }

    class View_Holder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView lesson_quiz_name;
        public RecyclerView QuizesAnswersLst;

        View_Holder(View itemView){
            super(itemView);

            lesson_quiz_name = (TextView) itemView.findViewById(R.id.lesson_quiz_name);
            QuizesAnswersLst = (RecyclerView) itemView.findViewById(R.id.QuizesAnswersLst);

        }

        void bind(int position) {

            String question_name = lessonsQuizesList.get(position).quiz;
            String question_id = lessonsQuizesList.get(position).quiz_id;

            lesson_quiz_name.setText("Qn. "+question_name);

            DividerItemDecoration verticalDecoration = new DividerItemDecoration(QuizesAnswersLst.getContext(), DividerItemDecoration.HORIZONTAL);
            Drawable verticalDivider = ContextCompat.getDrawable(context, R.drawable.verticle_separator);
            verticalDecoration.setDrawable(verticalDivider);
            QuizesAnswersLst.addItemDecoration(verticalDecoration);
            QuizesAnswersLst.addItemDecoration(new DividerItemDecoration(QuizesAnswersLst.getContext(), DividerItemDecoration.VERTICAL));

            QuizesAnswersLst.setLayoutManager(new LinearLayoutManager(context));

            List<quizesAnswersModel> data = sd.getQuestionAnswers(""+question_id);

            adpt = new driverQuestionsAnswersAdapter(data,context);
            QuizesAnswersLst.setAdapter(adpt);

            for (quizesAnswersModel item : data) {

                Log.d("CheckboxState", item.getAnswers() + ": " + item.isChecked() + ": "+item.getSid());

                lessonsQuizesModel item_ = GlobalVariables.questions.get(position);
                item_.setIs_answered(item.getAnswers());

            }


            //////////////////////
//            List<quizesAnswersModel> data = sd.getQuestionAnswers(""+question_id);
//            QuizesAnswersLst.setBackgroundColor(Color.TRANSPARENT);
//            adpt = new driverQuestionsAnswersAdapter(data,context);
//            QuizesAnswersLst.setAdapter(adpt);

//            for (quizesAnswersModel item : data) {
//
//                Log.d("CheckboxState", item.getAnswers() + ": " + item.isChecked() + ": "+item.getSid());
//
//                lessonsQuizesModel item_ = GlobalVariables.questions.get(position);
//                item_.setIs_answered(item.getAnswers());
//
//            }
//            QuizesAnswersLst.setDivider(null);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Log.v("KKK", "HHHHHHHHH "+adapterPosition);

        }
    }
}
