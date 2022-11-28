package com.example.projectcovid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyAppointmentActivity extends AppCompatActivity {

    TextView dose1_date, dose1_label ,dose2_date, dose2_label;
    TextView info_label, no_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);

        dose1_date = findViewById(R.id.dose1_date);
        dose1_label = findViewById(R.id.dose1_label);
        dose2_date = findViewById(R.id.dose2_date);
        dose2_label = findViewById(R.id.dose2_label);

        info_label = findViewById(R.id.info_label);
        no_app = findViewById(R.id.no_app);

        //Take the dates of 2 vaccine doses from User Activity page.
        Bundle b = this.getIntent().getExtras();
        String[] dates = b.getStringArray("DOSES_DATES");

        //Display the dates of doses in the user.
        if (dates.length != 0){
            info_label.setVisibility(View.VISIBLE);
            dose1_label.setVisibility(View.VISIBLE);
            dose2_label.setVisibility(View.VISIBLE);
            dose1_date.setText(dates[0]);
            dose2_date.setText(dates[1]);
            no_app.setVisibility(View.INVISIBLE);
        }else{
            no_app.setVisibility(View.VISIBLE);
            info_label.setVisibility(View.INVISIBLE);
            dose1_label.setVisibility(View.INVISIBLE);
            dose2_label.setVisibility(View.INVISIBLE);
            dose1_date.setVisibility(View.INVISIBLE);
            dose2_date.setVisibility(View.INVISIBLE);
        }
    }

    public void showUserPage(View view){
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
    }
}