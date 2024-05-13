package com.example.secucapture.models;

public class quizesAnswersModel {

    public String sid, answers, quiz_id;
    private boolean isChecked;

    public quizesAnswersModel() {
    }

    public quizesAnswersModel(String sid, String answers, String quiz_id, boolean isChecked) {
        this.sid = sid;
        this.answers = answers;
        this.quiz_id = quiz_id;
        this.isChecked = isChecked;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAnswers() {
        return answers;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }
}
