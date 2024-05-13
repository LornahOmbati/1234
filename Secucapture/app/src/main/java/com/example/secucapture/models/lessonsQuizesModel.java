package com.example.secucapture.models;

public class lessonsQuizesModel {

    public String sid, lesson_id, quiz, quiz_id,is_answered;

    public lessonsQuizesModel() {
    }

    public lessonsQuizesModel(String lesson_id, String quiz, String quiz_id, String is_answered) {
        this.lesson_id = lesson_id;
        this.quiz = quiz;
        this.quiz_id = quiz_id;
        this.is_answered = is_answered;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getIs_answered() {
        return is_answered;
    }

    public void setIs_answered(String is_answered) {
        this.is_answered = is_answered;
    }
}
