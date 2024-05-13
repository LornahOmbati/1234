package com.example.realmapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.realmapp.R;
import com.example.realmapp.databinding.ActivityDatabaseBinding;
import com.example.realmapp.models.Members;

import org.json.JSONObject;

import java.lang.reflect.Member;
import java.util.ArrayList;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;

public class DatabaseActivity extends AppCompatActivity {
    ActivityDatabaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    void insertObject(){
        Members members = new Members();
        members.name = "Victoria";
        members.member_number = "1";
        members.system_user_id = "12";

        boolean memberInserted = Realm.databaseManager.insertObject(members);
        Log.e("new member", "=> "+memberInserted);
    }

    void retrieveObjects(){
        //will retrieve everything in table
        ArrayList<Members> members = Realm.databaseManager.loadObjectArray(Members.class, new Query());
        //will retrieve with specific parameters
        Members membersWithXsid = Realm.databaseManager.loadObject(Members.class,
                new Query()
                        .setTableFilters("sid=?")
                        .setQueryParams("1"));
        Members membersWithXsid2 = Realm.databaseManager.loadObject(Members.class, new Query().setTableFilters("sid='1'"));
        //will retrieve with more than one specific parameter
        Members membersWithXsidandName = Realm.databaseManager.loadObject(Members.class, new Query().setTableFilters("sid='1","name LIKE %Ed%"));
        ArrayList<Members> membersWithNameLikeX = Realm.databaseManager.loadObjectArray(Members.class,
                new Query()
                        .setTableFilters("name LIKE %?%")
                        .setQueryParams("a")
                        .setLimit(5)
                        .setOffset(2)
                        .addOrderFilters("name", false)
                        .setColumns("_id","sid","name"));
        //will retrieve data based on parameters given -> INNER JOIN LEFT JOIN etc
        ArrayList<Members> membersFromCustomQuery= Realm.databaseManager.loadObjectArray(Members.class,
                new Query().setCustomQuery("select * from bla inner join ble on ......"));
    }
    void updateObject(){
        Realm.databaseManager.executeQuery("UPDATE member SET sync_status ='p' WHERE sync_status = 'i'");
        Realm.databaseManager.executeQuery("DELETE FROM members");
        Realm.databaseManager.executeQuery("DROP table member");
        Realm.databaseManager.executeQuery("VACUUM()");

    }

//    void etc(){
//        Member member = Realm.realm.getObjectFromJson(Members.class, new JSONObject());
//        JSONObject memberJson = Realm.realm.getJsonFromObject(member);
//    }
}