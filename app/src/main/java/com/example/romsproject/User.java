package com.example.romsproject;

public class User {
    private String email;
    private String uid;
    private String username;
    private int coins; // New field for coins

    //  No-argument constructor for Firebase
    public User() {
        // Firebase requires this constructor for deserialization
    }

    // Constructor with parameters
    public User(String email, String username) {
        this.email = email;
        this.username = username;
        this.coins = 100; // Set default coins value if needed
    }

    // Getters and setters for all fields
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for coins
    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
