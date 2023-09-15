package com.example.myapplication;

import java.util.Date;

public class Transaction
{
    private int amount;
    private String date;
    private String from;
    private String toId;

    public Transaction() {
    }

    public Transaction(int amount, String date, String from, String toId) {
        this.amount = amount;
        this.date = date;
        this.from = from;
        this.toId = toId;

    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
