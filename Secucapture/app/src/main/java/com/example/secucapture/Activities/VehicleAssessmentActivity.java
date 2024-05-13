package com.example.secucapture.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secucapture.R;
import com.example.secucapture.Utils.AESHelper;
import com.example.secucapture.Utils.DbHelper;

public class VehicleAssessmentActivity extends AppCompatActivity {

    EditText plate_number;

    TextView submit;

    public static Activity act;

    Context ctx;

    DbHelper doa;


    static AESHelper aesHelper;

    RadioGroup question1RadioGroup, question2RadioGroup, question3RadioGroup,question4RadioGroup, question5RadioGroup;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_assessment);

        submit = findViewById(R.id.submit);
        plate_number = findViewById(R.id.plate_number);

        // Initialize RadioGroups
        question1RadioGroup = findViewById(R.id.question_1_radio_group);
        question2RadioGroup = findViewById(R.id.question_2_radio_group);
        question3RadioGroup = findViewById(R.id.question_3_radio_group);
        question4RadioGroup = findViewById(R.id.question_4_radio_group);
        question5RadioGroup = findViewById(R.id.question_5_radio_group);


        doa = new DbHelper(this);


        question1RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.question_1_yes) {
                    // "Yes" option selected
                } else if (checkedId == R.id.question_1_no) {
                    // "No" option selected
                }
            }
        });

        question2RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.question_2_yes) {
                    // "Yes" option selected
                } else if (checkedId == R.id.question_2_no) {
                    // "No" option selected
                }
            }
        });

        question3RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.question_3_yes) {
                    // "Yes" option selected
                } else if (checkedId == R.id.question_3_no) {
                    // "No" option selected
                }
            }
        });

        question4RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.question_4_yes) {
                    // "Yes" option selected
                } else if (checkedId == R.id.question_4_no) {
                    // "No" option selected
                }
            }
        });

        question5RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.question_5_yes) {
                    // "Yes" option selected
                } else if (checkedId == R.id.question_5_no) {
                    // "No" option selected
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String plateNumber = plate_number.getText().toString();
                String question1 = getSelectedRadioButtonText(question1RadioGroup);
                String question2 = getSelectedRadioButtonText(question2RadioGroup);
                String question3 = getSelectedRadioButtonText(question3RadioGroup);
                String question4 = getSelectedRadioButtonText(question4RadioGroup);
                String question5 = getSelectedRadioButtonText(question5RadioGroup);

                boolean inserted = doa.VehicleAssessInputData(plateNumber, question1, question2, question3, question4, question5);

                if (inserted) {
                    Toast.makeText(VehicleAssessmentActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VehicleAssessmentActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        return selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";
    }
}
