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

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView createBtn;
    ProgressBar progressBar;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        loginBtn = findViewById(R.id.loginBtn);
        createBtn = findViewById(R.id.createText);

        auth = FirebaseAuth.getInstance();

    }

    //This method login a user in this application.
    public void loginUser(View view) {

        String myEmail = email.getText().toString().trim();
        String myPass = password.getText().toString().trim();

        //Check if email form field is empty or not.
        if (TextUtils.isEmpty(myEmail)) {
            email.setError("Email is required.");
            return;
        }

        //Check if password form field is empty or not.
        if (TextUtils.isEmpty(myPass)) {
            password.setError("Password is required.");
            return;
        }

        //Check the length of the password, must be more than 6 chars.
        if (password.length() < 6) {
            password.setError("Password must be >= 6 characters.");
            return;
        }

        //Set the progress bar visible to user, cause register is success.
        progressBar.setVisibility(View.VISIBLE);

        //Authenticate the user.
        auth.signInWithEmailAndPassword(myEmail, myPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (myEmail.equals("admin@covid.gr")) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    //This method shows the register page, if the user is new to application.
    public void showRegisterPage(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}