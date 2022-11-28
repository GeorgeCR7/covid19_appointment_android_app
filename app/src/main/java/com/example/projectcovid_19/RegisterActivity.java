package com.example.projectcovid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, email, password, phone;
    Button registerBtn;
    TextView loginBtn;
    ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.createText);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        //If the user is already authenticated and logged in, send to Main Activity.
        if (auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            finish();
        }
    }

    //This method register a new user to application and write the data in firebase.
    public void registerUser(View view){

        //Take the data from input fields in Register page.
        String myEmail = email.getText().toString().trim();
        String myPass = password.getText().toString().trim();
        String myPhone = phone.getText().toString().trim();

        //Check if email form field is empty or not.
        if (TextUtils.isEmpty(myEmail)){
            email.setError("Email is required.");
            return;
        }

        //Check if password form field is empty or not.
        if (TextUtils.isEmpty(myPass)){
            password.setError("Password is required.");
            return;
        }

        //Check the length of the password, must be more than 6 chars.
        if (password.length() < 6){
            password.setError("Password must be >= 6 characters.");
            return;
        }

        //Check if the phone input starts with 69....
        if (!myPhone.startsWith("69")){
            phone.setError("Phone number must starts with numbers 69...");
            return;
        }

        //Check if the phone input contains only numbers.
        int len = myPhone.length();
        if ( onlyDigits(myPhone) ){
            phone.setError("Phone number must contains only numbers.");
            return;
        }

        //Check if the phone input is exactly 10 numbers long.
        if (len != 10){
            phone.setError("Phone number must be exactly 10 numbers long.");
            return;
        }

        //Set the progress bar visible to user, cause register is success.
        progressBar.setVisibility(View.VISIBLE);

        //Register the new user in firebase.
        auth.createUserWithEmailAndPassword(myEmail, myPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    //This method shows the login page, if the user has already an account.
    public void showLoginPage(View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    //This method helps to check if phone number input contains only numbers.
    private static boolean onlyDigits(String str){

        for (int i = 0 ; i < str.length() ; i++){

            int digit = (int) str.charAt(i);

            if (!(digit >= 49 && digit <= 57)) {
                return false;
            }
        }

        return true;
    }
}