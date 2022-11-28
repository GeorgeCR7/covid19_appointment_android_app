package com.example.projectcovid_19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.projectcovid_19.models.Appointment;
import com.example.projectcovid_19.models.VaccineCenter;
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

public class UserActivity extends AppCompatActivity implements LocationListener {

    //Variables for Firebase access.
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    //ArrayLists for storing data from Firebase, vaccine centers & appointments.
    private ArrayList<VaccineCenter> centers;
    private ArrayList<Appointment> apps;

    //The email of the current user in the app.
    private String myEmail;

    //The values of latitude & longitude of current location.
    double lat, longi;

    TextView center_title, center_location;
    Button newAppBtn;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //Initialize all global variables.
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        myEmail = auth.getCurrentUser().getEmail();

        centers = new ArrayList<>();
        apps = new ArrayList<>();

        center_title = findViewById(R.id.center_title);
        center_location = findViewById(R.id.center_location);
        newAppBtn = findViewById(R.id.new_appointment);

        //Runtime permissions for get access to Location.
        if (ContextCompat.checkSelfPermission(UserActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(UserActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        }

        //Get my current location via GPS.
        getLocation();
    }

    //Show all the vaccine centers in map.
    public void showCenters(View view) {

        //Take the reference from Firebase.
        DatabaseReference centersRef = database.getReference().child("Vaccine Centers");

        //Read all data from Vaccine Centers table and create objects, then add each object to the list.
        centersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VaccineCenter center = snapshot.getValue(VaccineCenter.class);
                    centers.add(center);
                }
                //Open the Map Activity and send the list with all Vaccine Centers.
                Intent intent = new Intent(UserActivity.this, MapActivity.class);
                intent.putParcelableArrayListExtra("CENTERS_LIST", centers);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Show information about my vaccine appointment.
    public void showMyAppointment(View view) {

        //Take the reference from Firebase.
        DatabaseReference appsRef = database.getReference().child("Appointments");

        //Read all data from Appointments table and create objects, then add each object to the list.
        appsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment app = snapshot.getValue(Appointment.class);
                    apps.add(app);
                }

                int appFound = 0;
                String date1 = "", date2 = "";

                //Check if current user in app, has an active appointment.
                for (Appointment app : apps) {

                    if (appFound == 1){
                        break;
                    }

                    if (app.getEmail().equals(myEmail)) {
                        date1 = app.getDose1();
                        date2 = app.getDose2();
                        appFound = 1;
                    }
                }

                Bundle b = new Bundle();
                if (appFound == 1){ //If yes, open My Appointment Activity and send the dates of 2 doses.
                    b.putStringArray("DOSES_DATES", new String[]{date1,date2});
                    Intent intent = new Intent(UserActivity.this, MyAppointmentActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }else{ //If no, open My Appointment Activity and send a proper message.
                    b.putStringArray("DOSES_DATES", new String[]{});
                    Intent intent = new Intent(UserActivity.this, MyAppointmentActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //User creates a new appointment.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewAppointment(View view) {

        //Take the reference from Firebase.
        DatabaseReference appsRef = database.getReference().child("Appointments");

        //Create the date for dose 1, in 7 days from today.
        LocalDate date = LocalDate.now().plusDays(7);
        String dose1 = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

        //Create the date for dose 2, depends from dose 1 date + 28 days.
        date = date.plusDays(28);
        String dose2 = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

        //Create the new Appointment object, and add it to the Firebase.
        Appointment thisApp = new Appointment(myEmail, dose1, dose2);
        appsRef.push().setValue(thisApp);

        //Display a message to the user.
        Toast.makeText(UserActivity.this, "Your appointment created successfully.", Toast.LENGTH_SHORT).show();
    }

    //Log-out the user from application.
    public void logout(View view) {
        auth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, UserActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        // Get the latitude & longitude of my current location, store the values in global variables.
        lat = location.getLatitude();
        longi = location.getLongitude();

        //Read the table of Vaccine Centers from Firebase.
        readCentersFromDB();
    }

    //Read all vaccine centers data from Firebase.
    private void readCentersFromDB() {

        //Take the reference from Firebase.
        DatabaseReference centersRef = database.getReference().child("Vaccine Centers");

        //Read all data from Vaccine Centers table and create objects, then add each object to the list.
        centersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VaccineCenter center = snapshot.getValue(VaccineCenter.class);
                    centers.add(center);
                }
                //Call method to find nearest center, depends to ur location.
                findNearestCenter(centers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //This method finds the nearest vaccine center based to my location.
    private void findNearestCenter(ArrayList<VaccineCenter> centers){

        //List with all the distances from my location to all vaccine centers.
        ArrayList<Float> distances = new ArrayList<>();

        //Create my location object.
        Location myLocation = new Location("My Location");
        myLocation.setLatitude(lat);
        myLocation.setLongitude(longi);

        //Compute & save to the proper list all the distances from my location to all vaccine centers.
        for (VaccineCenter center : centers){

            Location otherLocation = new Location(center.getTitle());
            otherLocation.setLatitude(Double.parseDouble(center.getLatitude()));
            otherLocation.setLongitude(Double.parseDouble(center.getLongitude()));
            float distance = myLocation.distanceTo(otherLocation);
            distances.add(distance);
        }

        //Find & save the position of the min distance from the list.
        double minDistance = distances.get(0);
        int minPosCenter = 0;

        for (int i = 1 ; i < distances.size(); i++){
            if (distances.get(i) < minDistance){
                minDistance = distances.get(i);
                minPosCenter = i;
            }
        }

        //Display the data to user, in User Activity page.
        center_title.setText(centers.get(minPosCenter).getTitle());
        center_location.setText(centers.get(minPosCenter).getLocation());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}