package com.example.romsproject;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.romsproject.fragments.HomePage;
import com.example.romsproject.fragments.LeaderBoardPage;
import com.example.romsproject.fragments.PlayPage;
import com.example.romsproject.fragments.ProfilePage;
import com.example.romsproject.fragments.SettingsPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the MusicService to play music
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);  // Starts the service and begins music playback

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(navListener);

        //
        Fragment selectedFragment = new HomePage();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        Fragment selectedFragment = null;

        if (itemId == R.id.nav_leaderboard) {
            selectedFragment = new LeaderBoardPage();
        } else if (itemId == R.id.nav_play) {
            selectedFragment = new PlayPage();
        } else if (itemId == R.id.nav_home) {
            selectedFragment = new HomePage();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfilePage();
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsPage();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };
}
