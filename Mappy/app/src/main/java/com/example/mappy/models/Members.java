package com.example.realmtwo.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;


@DynamicClass(table_name = "member_info")
//@SyncDescription(service_name = "members", download_link = "GatePass/Mobileservices/GetallGatepassRequests", is_ok_position = "JO:IsOkay", download_array_position = "JO:Result;JO:Result",service_type = SyncDescription.service_type.Download)
public class Members extends RealmModel implements Serializable {

    @DynamicProperty(json_key = "department_id", column_name = "department")
    public String departmentId;

    @DynamicProperty(json_key = "asset_description")
    public String assetDescription;

    @DynamicProperty(json_key = "asset_category")
    public String assetCategory;

    @DynamicProperty(json_key = "reason")
    public String Reason;

    @DynamicProperty(json_key = "dep_name")
    public String name;


    public Members(){


    }
}
