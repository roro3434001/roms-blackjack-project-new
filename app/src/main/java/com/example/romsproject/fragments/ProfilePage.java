package com.example.romsproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.romsproject.FirebaseAuthHelper;
import com.example.romsproject.R;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePage extends Fragment {

    private View view; // Existing field for the fragment's view
    private TextView coinBalanceTextView; // New field to reference the coin balance TextView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout
        view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        // Initialize the coin balance TextView
        coinBalanceTextView = view.findViewById(R.id.coin_balance_value);

        // Fetch and display the user's coin balance
        fetchAndDisplayCoinBalance();

        return view;
    }

    // New method to fetch and display the user's coin balance
    private void fetchAndDisplayCoinBalance() {
        FirebaseAuthHelper authHelper = new FirebaseAuthHelper();
        FirebaseUser currentUser = authHelper.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid(); // Get the logged-in user's UID

            // Use the fetchUserCoins method from FirebaseAuthHelper
            authHelper.fetchCoinBalance(uid, coins -> {
                if (coins != 0) {
                    // Update the TextView with the fetched coin balance
                    coinBalanceTextView.setText(coins + " Coins");
                } else {
                    // Set a default value if coins are null or fetching failed
                    coinBalanceTextView.setText("0 Coins");
                }
            });

        }
    }
}
