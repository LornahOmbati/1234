package com.example.mytest.models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;// creates other variables in the class.

import java.io.Serializable;

import sparta.realm.spartamodels.db_class;
@DynamicClass(table_name = "TBL_drivers")
@SyncDescription(service_name = "drivers", service_type = SyncDescription.service_type.Download, download_link = "/SafetyCapture/Mobileservices/GetRegisteredDrivers", chunk_size = 5000,use_download_filter = true, download_array_position = "JO:result;JA:0")
//@SyncDescription(service_name = "drivers", service_type = SyncDescription.service_type.Upload, upload_link = "/SafetyCapture/Mobileservices/GetRegisteredDrivers", chunk_size = 1, is_ok_position = "JO:result;JA:0", table_filters = {"fullname LIKE X%",""})
//@SyncDescription(service_name = "drivers", service_type = SyncDescription.service_type.Upload, upload_link = "/SafetyCapture/Mobileservices/GetRegisteredDrivers", chunk_size = 1, is_ok_position = "JO:result;JA:0", table_filters = {"fullname LIKE Y%",""})
public class Driver extends db_class_ implements Serializable {
    @DynamicProperty(column_name = "_id", json_key ="lid", column_data_type = "INTEGER",extra_params = "PRIMARY KEY AUTOINCREMENT")//an annotation in the annotation class to specify.
    public String id;

    @DynamicProperty(column_name = "fullname", json_key ="fullname")
    public String fullname;

    @DynamicProperty(column_name = "member_no", json_key ="member_no")
    public String member_no;

    public Driver(){

    }


}
