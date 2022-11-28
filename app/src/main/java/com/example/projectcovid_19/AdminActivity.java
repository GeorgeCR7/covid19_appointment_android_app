package com.example.projectcovid_19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.projectcovid_19.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    //Variables for Firebase access.
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    private ArrayList<Appointment> appsList;

    private String todayDate;

    TextView apps, today, no_apps;
    ListView listView;
    Button logoutBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Initialize all global variables.
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        appsList = new ArrayList<>();

        apps = findViewById(R.id.apps);
        today = findViewById(R.id.today);
        no_apps = findViewById(R.id.no_apps);
        listView = (ListView)findViewById(R.id.listview);
        logoutBtn = findViewById(R.id.logoutBtnAdmin);

        //Display the today's date in admin page.
        todayDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        today.setText(todayDate);

        //Read the appointments.
        readAppsFromDB(todayDate);
    }

    //This method reads the appointments from Firebase.
    private void readAppsFromDB(String todayDate){

        //ArrayList with all emails who have appointment today.
        ArrayList<String> emails = new ArrayList<>();

        //Take the reference from Firebase.
        DatabaseReference appsRef = database.getReference().child("Appointments");

        //Read all data from Appointments table and create objects, then add each object to the list.
        appsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment app = snapshot.getValue(Appointment.class);
                    appsList.add(app);
                }

                //Add the emails to the list who have appointment today only (dose 1 or dose 2).
                for (Appointment app : appsList){
                    if (app.getDose1().equals(todayDate) || app.getDose2().equals(todayDate)){
                        emails.add(app.getEmail());
                    }
                }

                //If no appointment today, display the proper message.
                if (emails.size() == 0){
                    no_apps.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                }else{ //If at least one appointment today, display the emails of the users in list format.
                    ArrayAdapter arrayAdapter = new ArrayAdapter(AdminActivity.this, android.R.layout.simple_list_item_1, emails);
                    no_apps.setVisibility(View.INVISIBLE);
                    listView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Log-out the admin from application.
    public void logout(View view) {
        auth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}