package com.example.romsproject.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.romsproject.Login;
import com.example.romsproject.MusicService;
import com.example.romsproject.R;
import com.example.romsproject.FirebaseAuthHelper;

public class SettingsPage extends Fragment {

    private FirebaseAuthHelper authHelper;
    private Button logoutButton;
    private SeekBar musicVolumeSeekBar;
    private MusicService musicService;
    private boolean isBound = false; // To check if the service is bound
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_settings_page, container, false);

        // Initialize FirebaseAuthHelper
        authHelper = new FirebaseAuthHelper();

        // Check if user is logged in
        if (authHelper.isUserLoggedIn()) {
            // User is logged in, so show logout button
            logoutButton = view.findViewById(R.id.logout_button);

            // Set up logout button to log the user out
            logoutButton.setOnClickListener(v -> logoutUser());
        } else {
            // User is not logged in, so show an appropriate message or take action (optional)
        }

        // Bind to the music service to adjust the volume
        musicVolumeSeekBar = view.findViewById(R.id.music_volume_seekbar);

        // Set the SeekBar's progress from the current volume (initial value, e.g., 70)
        musicVolumeSeekBar.setProgress(70);

        // Set the listener for SeekBar changes
        musicVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    // Set the volume based on the SeekBar progress
                    float volume = progress / 100f; // Convert to float between 0.0f and 1.0f
                    musicService.setVolume(volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    private void logoutUser() {
        // Log out the user using the correct method
        authHelper.logoutUser();  // This is the correct method name

        // Redirect to Login activity after logging out
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);

        // Optionally, finish the current activity to clear it from the back stack
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to the MusicService when the fragment starts
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the MusicService when the fragment stops
        if (isBound) {
            getActivity().unbindService(serviceConnection);
            isBound = false;
        }
    }

    // ServiceConnection to bind MusicService
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
        }
    };
}
