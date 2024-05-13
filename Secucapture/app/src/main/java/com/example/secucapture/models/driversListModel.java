package com.example.secucapture.models;

public class driversListModel {

    public String sid, full_name, phone_no, id_no, drivers_licence_no;

    public driversListModel() {
    }

    public driversListModel(String full_name, String phone_no, String id_no, String drivers_licence_no) {
        this.full_name = full_name;
        this.phone_no = phone_no;
        this.id_no = id_no;
        this.drivers_licence_no = drivers_licence_no;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getDrivers_licence_no() {
        return drivers_licence_no;
    }

    public void setDrivers_licence_no(String drivers_licence_no) {
        this.drivers_licence_no = drivers_licence_no;
    }
}
