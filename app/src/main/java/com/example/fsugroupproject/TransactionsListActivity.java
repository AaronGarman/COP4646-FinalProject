package com.example.fsugroupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TransactionsListActivity extends AppCompatActivity {
    public static String format = "";
    private FirebaseDatabase userDatabase;
    private DatabaseReference userReference, currentUserReference;
    private FirebaseUser currentUser;
    private List<Transaction> userTransactionList;
    private ArrayAdapter<Transaction> adapter;
    private ListView transactionsListView;

    // broadcast receiver that listens for sms message
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMSReceiver.SMS_RECEIVED_ACTION)) {
                // if a message is received, call sortList method to update list using input message
                sortList(format);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);

        // sets up everything that uses Firebase functionality in this activity
        FirebaseApp.initializeApp(this);
        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserReference = userReference.child(currentUser.getUid());

        // linking UI elements to activity

        transactionsListView = findViewById(R.id.transactionsLV);
        Button backBTN = findViewById(R.id.backBTN);

        // TEST

        userTransactionList = new ArrayList<>();
        // my test
        Transaction tran = new Transaction("deposit", "rent", "desc", 100);
        Transaction tran1 = new Transaction("deposit", "rent", "desc", 100);
        Transaction tran2 = new Transaction("deposit", "rent", "desc", 100);
        Transaction tran3 = new Transaction("deposit", "rent", "desc", 100);
        Transaction tran4 = new Transaction("deposit", "rent", "desc", 100);

        userTransactionList.add(tran);

        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, userTransactionList);
        transactionsListView.setAdapter(adapter);

        // TEST

        // event listener used to check for updates in user balance and display it
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // checks if the user exists in the database
                if (dataSnapshot.exists()) {
                    // grabs BankUser object from database
                    BankUser currentBankUser = dataSnapshot.getValue(BankUser.class);

                    // sets balance value to be displayed
                    userTransactionList = currentBankUser.getTransactionList();

                    // setting up Listview adapter
                    adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, userTransactionList);
                    transactionsListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // back button pressed

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionsListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() { // onResume() for broadcast receiver
        super.onResume();
        IntentFilter filter = new IntentFilter(SMSReceiver.SMS_RECEIVED_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() { // onPause() for broadcast receiver
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void sortList(String sortCriteria) {
        if (sortCriteria.matches("category")) { // checks for valid sort criteria
            // sorts userTransactionList based on results from getCategory() using Collections
            Collections.sort(userTransactionList, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t1.getCategory().compareTo(t2.getCategory());
                }
            });
        } else if (sortCriteria.matches("type")) { // checks for valid sort criteria
            Collections.sort(userTransactionList, new Comparator<Transaction>() {
                // sorts userTransactionList based on results from getType() using Collections
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t1.getType().compareTo(t2.getType());
                }
            });
        } else if (sortCriteria.matches("description")) { // checks for valid sort criteria
            Collections.sort(userTransactionList, new Comparator<Transaction>() {
                // sorts userTransactionList based on results from getDescription() using Collections
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return t1.getDescription().compareTo(t2.getDescription());
                }
            });
        } else if (sortCriteria.matches("amount")) { // checks for valid sort criteria
            Collections.sort(userTransactionList, new Comparator<Transaction>() {
                // sorts userTransactionList based on results from getAmount() using Collections
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return Double.compare(t1.getAmount(), t2.getAmount());
                }
            });
        } else {
            // if no valid sort criteria is received user gets a Toast notifying them
            Toast.makeText(getApplicationContext(), "Invalid sort criteria.", Toast.LENGTH_LONG).show();
        }

        // updates the adapter using the sorted userTransactionList (if it was sorted)
        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, userTransactionList);

        // sets the adapter of the transactionsListView to the updated adapter
        transactionsListView.setAdapter(adapter);
    }
}