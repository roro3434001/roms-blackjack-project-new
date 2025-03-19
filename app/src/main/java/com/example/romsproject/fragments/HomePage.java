package com.example.romsproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.romsproject.FirebaseAuthHelper;
import com.example.romsproject.MusicService;
import com.example.romsproject.R;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends Fragment {

    View view;
    private FirebaseAuthHelper authHelper;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize FirebaseAuthHelper and views
        authHelper = new FirebaseAuthHelper();
        usernameTextView = view.findViewById(R.id.username_text_view);

        // Get the current user
        FirebaseUser currentUser = authHelper.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Fetch the username and set it to the TextView
            authHelper.fetchUsername(uid, username -> {
                if (username != null) {
                    usernameTextView.setText("Welcome, " + username + "!");
                } else {
                    usernameTextView.setText("Welcome!");
                }
            });
        } else {
            usernameTextView.setText("Welcome!");
        }


        // Start the Music Service
        // Start the Music Service
        Intent musicServiceIntent = new Intent(getContext(), MusicService.class);
        if (getContext() != null) {  // Ensure the fragment is attached to an activity
            getContext().startService(musicServiceIntent);  // Start the service using the context of the fragment
        }


        return view;
    }
}
