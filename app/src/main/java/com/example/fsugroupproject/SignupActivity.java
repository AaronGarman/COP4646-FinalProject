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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseDatabase fireDatabase;
    private FirebaseAuth fireAuth;
    private DatabaseReference fireReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // sets up everything that uses Firebase functionality in this activity
        FirebaseApp.initializeApp(this);
        fireAuth = FirebaseAuth.getInstance();

        Button createAccountBTN = findViewById(R.id.createAccountBTN);   // creates account and if successful returns to login
        Button cancelSignupBTN = findViewById(R.id.cancelSignupBTN);   // goes to signup screen

        createAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // extracts email as a string from the text field
                EditText emailSignupField = findViewById(R.id.emailSignupET);
                final String userSignupEmail = emailSignupField.getText().toString();

                // extracts password as a string from the text field
                EditText passwordSignupField = findViewById(R.id.passwordSignupET);
                String userSignupPassword = passwordSignupField.getText().toString();

                // creates a user given the email and password (modeled after example code)
                fireAuth.createUserWithEmailAndPassword(userSignupEmail, userSignupPassword)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    BankUser bankUser = new BankUser();
                                    bankUser.setEmail(userSignupEmail);
                                    Toast.makeText(SignupActivity.this, "Sign up successful.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    emailSignupField.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    passwordSignupField.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    Toast.makeText(SignupActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        cancelSignupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


}

