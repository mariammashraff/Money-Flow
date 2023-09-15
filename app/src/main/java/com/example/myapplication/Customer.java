package com.example.myapplication;

public class Customer
{
    private String name;

    private String image;
    private String email;
    private int balance;

    public Customer(String name, String image, String email, int balance) {
        this.name = name;
        this.image = image;
        this.email = email;
        this.balance = balance;
    }

    public Customer()
    {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
