package com.example.realmtwo;

import static javax.mail.Session.getDefaultInstance;
import static sparta.realm.Realm.*;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Transaction;

import com.example.realmtwo.models.Members;
import com.example.realmtwo.models.gates_table;

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

    //pull code
    public void fetchDataFromServer() {

        realm = getDefaultInstance();

        // Perform queries or CRUD operations
        // For example, fetching data
        realm.executeTransactionAsync(new Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<gates_table> gates= realm.where(gates_table.class).findAll();
                // Process fetched data
            }
        });
    }

    //push code
    public void pushDataToServer(final gates_table data) {
        realm = getDefaultInstance();

        // Perform data modification
        realm.executeTransactionAsync(new Transaction() {
            @Override
            public void execute(Realm realm) {
                // Modify data
                realm.copyToRealmOrUpdate(data);
            }
        });
    }


    void insertObject(){
        Members members = new Members();
        members.name = "lorna";
        //members.member_number = "1";
        //members.system_user_id = "12";

        boolean memberInserted = databaseManager.insertObject(members);
        Log.e("new member", "=> "+memberInserted);
    }

//    void  insertDrivers(){
//
//        DriverOrganisation drivers = new DriverOrganisation();
//        drivers.name = "Motors";
//
//        boolean driverInserted = Realm.databaseManager.insertObject(drivers);
//        Log.e("new organisation", "=> "+driverInserted);
//
//    }

    void retrieveObjects(){
        //will retrieve everything in table
        ArrayList<Members> members = databaseManager.loadObjectArray(Members.class, new Query());
//        ArrayList<DriverOrganisation> drivers = Realm.databaseManager.loadObjectArray(DriverOrganisation.class, new Query());


        //will retrieve with specific parameters
        Members membersWithXsid = databaseManager.loadObject(Members.class,
                new Query()
                        .setTableFilters("sid=?")
                        .setQueryParams("1"));
        Members membersWithXsid2 = databaseManager.loadObject(Members.class, new Query().setTableFilters("sid='1'"));


        //will retrieve with more than one specific parameter
        Members membersWithXsidandName = databaseManager.loadObject(Members.class, new Query().setTableFilters("sid='1","name LIKE %Ed%"));
        ArrayList<Members> membersWithNameLikeX = databaseManager.loadObjectArray(Members.class,
                new Query()
                        .setTableFilters("name LIKE %?%")
                        .setQueryParams("a")
                        .setLimit(5)
                        .setOffset(2)
                        .addOrderFilters("name", false)
                        .setColumns("_id","sid","name"));


        //will retrieve data based on parameters given -> INNER JOIN LEFT JOIN etc
        ArrayList<Members> membersFromCustomQuery= databaseManager.loadObjectArray(Members.class,
                new Query().setCustomQuery("select * from bla inner join ble on ......"));
    }
    void updateObject(){
        databaseManager.executeQuery("UPDATE member SET sync_status ='p' WHERE sync_status = 'i'");
        databaseManager.executeQuery("DELETE FROM members");
        databaseManager.executeQuery("DROP table member");
        databaseManager.executeQuery("VACUUM()");

    }

    // Method to manually trigger data upload to the server
    public void uploadDataToServer() {
        //realm = Realm.getDefaultInstance();

        // Get the sync description for the desired Realm model (e.g., Member)
        sparta.realm.SyncDescription syncDescription = Realm.realm.getSyncDescription(gates_table.class).get(0);

        // Call the upload method on SynchronizationManager to upload changes for the specified sync description
        sparta.realm.SynchronizationManager.upload_(syncDescription);
    }


//    void etc(){
//        Member member = Realm.realm.getObjectFromJson(Members.class, new JSONObject());
//        JSONObject memberJson = Realm.realm.getJsonFromObject(member);
//    }
}