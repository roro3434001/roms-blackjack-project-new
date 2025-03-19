package com.example.romsproject.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.romsproject.FirebaseAuthHelper;
import com.example.romsproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfilePage extends Fragment {

    private View view;
    private TextView coinBalanceTextView;
    private TextView userCountryTextView; // TextView for displaying country
    private FusedLocationProviderClient fusedLocationClient;

    // This will hold the country information
    private String country = "";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout
        view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        // Initialize UI elements
        coinBalanceTextView = view.findViewById(R.id.coin_balance_value);
        userCountryTextView = view.findViewById(R.id.user_country); // Ensure this ID exists in your XML

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Fetch and display the user's coin balance
        fetchAndDisplayCoinBalance();

        // Get country of origin (location)
        getCountryOfOrigin();

        return view;
    }

    private void getCountryOfOrigin() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, fetch location
            fetchLocation();
        }
    }

    private void fetchLocation() {
        // Check again in case permission was revoked after request
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fetch the last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Use Geocoder to get country name
                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses != null && !addresses.isEmpty()) {
                                        Address address = addresses.get(0);
                                        country = address.getCountryName();
                                        Log.d("Location", "Country: " + country);

                                        // Update TextView with country name
                                        userCountryTextView.setText("Location: " + country);
                                    } else {
                                        Log.d("Location", "No address found for the location");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e("LocationError", "Geocoder IOException", e);
                                    Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            // Handle case where permission is not granted (this should be covered by the requestPermissions logic)
            Toast.makeText(requireContext(), "Permission denied. Can't fetch location.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now fetch location
                fetchLocation();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(requireContext(), "Permission denied. Can't fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Fetch and display user's coin balance
    private void fetchAndDisplayCoinBalance() {
        FirebaseAuthHelper authHelper = new FirebaseAuthHelper();
        FirebaseUser currentUser = authHelper.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid(); // Get the logged-in user's UID

            // Use the fetchUserCoins method from FirebaseAuthHelper
            authHelper.fetchCoinBalance(uid, coins -> {
                if (coins != 0) {
                    coinBalanceTextView.setText(coins + " Coins");
                } else {
                    coinBalanceTextView.setText("0 Coins");
                }
            });
        }
    }
}
