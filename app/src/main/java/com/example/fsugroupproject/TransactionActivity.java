package com.example.fsugroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransactionActivity extends AppCompatActivity {
    private FirebaseDatabase userDatabase;
    private DatabaseReference userReference, currentUserReference;
    private FirebaseUser currentUser;
    private Intent transactionServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // sets up the service that notifies users of new transactions
        transactionServiceIntent = new Intent(this, TransactionService.class);

        // sets up everything that uses Firebase functionality in this activity
        FirebaseApp.initializeApp(this);
        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserReference = userReference.child(currentUser.getUid());

        // linking UI elements to activity

        TextView typeTV = findViewById(R.id.typeTV);    // for error highlighting
        TextView descriptionTV = findViewById(R.id.descriptionTV);  // for error highlighting
        TextView amountTV = findViewById(R.id.amountTV);    // for error highlighting
        Spinner typeSPNR = findViewById(R.id.typesSPNR);    // spinner of transaction types
        TextView transactionCategoryTV = findViewById(R.id.transactionCategoryTV);  // shows transaction type
        EditText descriptionET = findViewById(R.id.descriptionET);  // description user input
        EditText amountET = findViewById(R.id.amountET);    // amount user input
        Button cancelBTN = findViewById(R.id.cancelBTN);    // goes to main activity without adding transaction
        Button finishBTN = findViewById(R.id.finishBTN);    // adds transaction to list then goes to main activity

        // get transaction type from intent

        Intent intent = getIntent();
        final String transactionCategory = intent.getStringExtra(MainActivity.MESSAGE);
        transactionCategoryTV.setText(transactionCategory);

        // cancel button clicked

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // finish button clicked

        finishBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check for errors

                String errorMsg = "";

                if (typeSPNR.getSelectedItem().toString().equals("Select:"))
                {
                    errorMsg += "-Select a type";
                    typeTV.setTextColor(getColor(R.color.red));
                }
                else
                {
                    typeTV.setTextColor(getColor(R.color.gray));
                }

                if (descriptionET.getText().toString().equals(""))
                {
                    errorMsg += "\n-Enter a description";
                    descriptionTV.setTextColor(getColor(R.color.red));
                }
                else
                {
                    descriptionTV.setTextColor(getColor(R.color.gray));
                }

                if (amountET.getText().toString().equals(""))
                {
                    errorMsg += "\n-Enter an amount";
                    amountTV.setTextColor(getColor(R.color.red));
                }
                else
                {
                    amountTV.setTextColor(getColor(R.color.gray));
                }

                if (errorMsg.equals(""))    // if no errors
                {
                    // get values from inputs

                    String categoryInput = transactionCategory;
                    String typeInput = typeSPNR.getSelectedItem().toString();
                    String descriptionInput = descriptionET.getText().toString();
                    double amountInput = Double.parseDouble(amountET.getText().toString());

                    // event listener used to access and update the BankUser object in the database
                    currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // creates new Transaction object given user inputs
                                Transaction transaction = new Transaction(transactionCategory, typeInput, descriptionInput, amountInput);

                                // grabs BankUser object from database
                                BankUser currentBankUser = dataSnapshot.getValue(BankUser.class);

                                // adds the transaction to the user's transaction list
                                currentBankUser.addTransaction(transaction);

                                // checks if the transaction was a withdrawal or deposit
                                if (transactionCategory.equals("Withdraw")) {
                                    currentBankUser.withdrawMoney(amountInput); // subtracts from user balance
                                } else if (transactionCategory.equals("Deposit")) {
                                    currentBankUser.depositMoney(amountInput); // adds to user balance
                                }

                                // relays information to the transaction service about a new transaction
                                Intent intent = new Intent(getApplicationContext(), TransactionService.class);
                                String notificationMessage = transaction.getCategory() + " for " + transaction.getType()
                                        + " (" + transaction.getDescription() + ")"
                                        + " | Amount: $" + String.format("%.2f", transaction.getAmount());
                                intent.putExtra("transactionOccurred", notificationMessage);
                                startService(intent);

                                // updates the old BankUser object in the database with the new one
                                userReference.child(currentUser.getUid()).setValue(currentBankUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) { }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                    // complete transaction then go back to main activity

                    Toast.makeText(TransactionActivity.this, "Transaction complete", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TransactionActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else    // if errors present - shows errors in toast
                {
                    Toast.makeText(TransactionActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() { // used to stop the service whenever the transaction screen is left
        super.onPause();
        stopService(transactionServiceIntent);
    }
}
