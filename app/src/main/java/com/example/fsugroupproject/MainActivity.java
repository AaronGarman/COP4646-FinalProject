package com.example.fsugroupproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE = "message"; // message to display transaction type
    private FirebaseDatabase userDatabase;
    private DatabaseReference userReference, currentUserReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // linking UI elements to activity
        TextView balanceTV = findViewById(R.id.balanceTV);  // balance value display
        Button transactionsBTN = findViewById(R.id.transactionsBTN);   // goes to transactions list
        Button depositBTN = findViewById(R.id.depositBTN);   // goes to deposit screen
        Button withdrawBTN = findViewById(R.id.withdrawBTN);    // goes to withdraw screen

        // sets up everything that uses Firebase functionality in this activity
        FirebaseApp.initializeApp(this);
        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // event listener used to check if the user is already in the database
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userExists = false; // tracks if user has been found

                // iterates though "users" node and checks all user Uids
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String currentUserUid = childSnapshot.getKey();

                    // compares Uid of current user logged in to the current "users" node
                    if (currentUserUid.equals(currentUser.getUid())) {
                        userExists = true;
                    }
                }

                // if the user does not exist this makes a new BankUser object for that user
                if (!userExists) {
                    BankUser newUser = new BankUser(currentUser.getEmail());
                    userReference.child(currentUser.getUid()).setValue(newUser);
                    balanceTV.setText("$0.00");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        // separate database reference for the current user's individual data
        currentUserReference = userReference.child(currentUser.getUid());

        // set up sms broadcast receiver - this, sms file, manifest
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            String[] perms = new String[]{Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, perms, 101);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED){
            String[] perms = new String[]{Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, perms, 101);
        }

        // event listener used to check for updates in user balance and display it
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // checks if the user exists in the database
                if (dataSnapshot.exists()) {
                    // grabs BankUser object from database
                    BankUser currentBankUser = dataSnapshot.getValue(BankUser.class);

                    // sets balance value to be displayed
                    String newBalance = String.format("%.2f", currentBankUser.getBalance());
                    newBalance = "$" + newBalance;
                    balanceTV.setText(newBalance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // transaction button pressed
        transactionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionsListActivity.class);
                startActivity(intent);
            }
        });

        // deposit button pressed
        depositBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putExtra(MESSAGE, "Deposit");
                startActivity(intent);
            }
        });

        // withdraw button pressed
        withdrawBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putExtra(MESSAGE, "Withdraw");
                startActivity(intent);
            }
        });
    }
}


