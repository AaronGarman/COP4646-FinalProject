package com.example.fsugroupproject;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Transaction implements Serializable {

    private String category;  // deposit vs withdrawal
    private String type;  // type of transaction
    private String description;  // description of transaction
    private double amount;       // dollar amount for transaction

    // no argument constructor required for Firebase
    public Transaction() {
        category = "Deposit";
        type = "Rent";
        description = "Rent Payment";
        amount = 100.00;
    }

    public Transaction(String categoryInput, String typeInput, String descriptionInput, double amountInput) // constructor
    {
        category = categoryInput;
        type = typeInput;
        description = descriptionInput;
        amount = amountInput;
    }

    public String getCategory()    // getter for category
    {
        return category;
    }

    public String getType()   // getter for type
    {
        return type;
    }

    public String getDescription()  // getter for description
    {
        return description;
    }

    public double getAmount()   // getter for amount
    {
        return amount;
    }

    @NonNull
    public String toString() {      // override toString
        String formatAmount = String.format("%.2f", amount);
        formatAmount = " $" + formatAmount;

        if (category.equals("Withdraw"))
        {
            return "-" + formatAmount + "    " + type + "  -  " + description;
        }
        else
        {
            return "+" + formatAmount + "    " + type + "  -  " + description;
        }
    }
}
