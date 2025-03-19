package com.example.romsproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.romsproject.R;

public class PlayPage extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_play_page, container, false);

        // Set up the button click listener
        view.findViewById(R.id.btn_move_to_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to move to Gameplay activity
                Intent intent = new Intent(getActivity(), com.example.romsproject.Gameplay.class); // Using Gameplay instead of GameplayActivity
                startActivity(intent); // Start the new activity
            }
        });

        return view;
    }
}
