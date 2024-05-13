package com.example.secucapture.models;

public class lessonsModel {

    public String sid, fullname, id_no, lesson_id , lesson_link , lesson_name;

    public lessonsModel() {
    }

    public lessonsModel(String sid, String fullname, String id_no, String lesson_id, String lesson_link, String lesson_name) {
        this.sid = sid;
        this.fullname = fullname;
        this.id_no = id_no;
        this.lesson_id = lesson_id;
        this.lesson_link = lesson_link;
        this.lesson_name = lesson_name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getLesson_link() {
        return lesson_link;
    }

    public void setLesson_link(String lesson_link) {
        this.lesson_link = lesson_link;
    }

    public String getLesson_name() {
        return lesson_name;
    }

    public void setLesson_name(String lesson_name) {
        this.lesson_name = lesson_name;
    }
}
