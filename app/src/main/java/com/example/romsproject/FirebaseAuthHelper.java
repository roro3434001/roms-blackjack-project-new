package com.example.romsproject;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAuthHelper {
    private FirebaseAuth mAuth;

    // Constructor to initialize FirebaseAuth
    public FirebaseAuthHelper() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Check if a user is already logged in
    public boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null; // Returns true if the user is logged in, false otherwise
    }

    // Register a new user with email and password
    public void registerUser(String email, String password, User user, Context context) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser firebaseUser = mAuth.getCurrentUser(); // Get the currently logged-in user
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            // New Code: Set initial coins to 100 for the new user
                            user.setCoins(100); // Initialize the user's coins to 100

                            // Save the User object to Firebase Realtime Database
                            DatabaseReference userRef = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(uid);
                            userRef.setValue(user) // Save the updated User object
                                    .addOnSuccessListener(aVoid -> {
                                        // Data saved successfully
                                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();

                                        // Show the starting gift message after successful registration
                                        Toast.makeText(context, "As a starting gift, here are 100 coins!", Toast.LENGTH_LONG).show();

                                        // Navigate to MainActivity after successful registration
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure to save user data
                                        Toast.makeText(context, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Registration failed
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Login the user using email and password
    public void loginUser(String email, String password, Context context) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Login failed
                        Toast.makeText(context, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Get the currently logged-in user
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Sign out the current user
    public void logoutUser() {
        mAuth.signOut();
    }

    // Fetch username for the current user
    public void fetchUsername(String uid, UsernameCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(com.google.android.gms.tasks.Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult(); // Correct usage of DataSnapshot
                    String username = snapshot.child("username").getValue(String.class); // Fetch the username from the snapshot
                    callback.onUsernameFetched(username); // Return the username to the callback
                } else {
                    callback.onUsernameFetched(null); // Return null if no username found
                }
            }
        });
    }

    // Fetch coin balance for the current user
    public void fetchCoinBalance(String uid, CoinBalanceCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("coins");
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Integer coins = task.getResult().getValue(Integer.class); // Fetch the coin balance
                callback.onCoinBalanceFetched(coins != null ? coins : 0); // Return the coin balance, default to 0 if null
            } else {
                callback.onCoinBalanceFetched(0); // Return 0 if fetching fails
            }
        });
    }

    // Callback interface for coin balance fetch
    public interface CoinBalanceCallback {
        void onCoinBalanceFetched(int coins);
    }


    // Callback interface for username fetch
    public interface UsernameCallback {
        void onUsernameFetched(String username);
    }
}
