package com.example.fsugroupproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth fireAuth;
    public static final String EMAIL = "com.example.fsugroupproject.EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // sets up everything that uses Firebase functionality in this activity
        FirebaseApp.initializeApp(this);
        fireAuth = FirebaseAuth.getInstance();

        Button loginBTN = findViewById(R.id.loginBTN);   // goes to login screen
        Button signupBTN = findViewById(R.id.createAccountBTN);   // goes to signup screen

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailLoginField = findViewById(R.id.emailLoginET);
                final String userLoginEmail = emailLoginField.getText().toString();

                EditText passwordLoginField = findViewById(R.id.passwordLoginET);
                final String userLoginPassword = passwordLoginField.getText().toString();

                if (!userLoginEmail.equals("") && !userLoginPassword.equals("")) {
                    fireAuth.signInWithEmailAndPassword(userLoginEmail, userLoginPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser fireUser = fireAuth.getCurrentUser();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra(EMAIL, fireUser.getEmail());
                                        startActivity(intent);
                                    } else {
                                        emailLoginField.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                        passwordLoginField.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                        Toast.makeText(LoginActivity.this, "Log in failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Please enter an email and password", Toast.LENGTH_LONG).show();
                }
            }
        });

        signupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}

