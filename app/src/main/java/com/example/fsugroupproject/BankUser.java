package com.example.fsugroupproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankUser implements Serializable {
    private String email;
    private double balance;
    private List<Transaction> transactionList;

    // no argument constructor required for Firebase
    public BankUser() {
        email = "default@default.com";
        balance = 0.00;
        transactionList = new ArrayList<Transaction>();
    }

    public BankUser(String e) {
        email = e;
        balance = 0.00;
        transactionList = new ArrayList<Transaction>();
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setEmail(String e) {
        email = e;
    }

    public void addTransaction(Transaction t) {
        transactionList.add(t);
    }

    public void withdrawMoney(double w) {
        balance -= w;
    }

    public void depositMoney(double d) {
        balance += d;
    }
}
