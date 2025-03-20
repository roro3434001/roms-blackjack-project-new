package com.example.romsproject.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
        if (!isLocationEnabled()) {
            Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                getCountryFromLocation(location);
                            } else {
                                requestNewLocation();
                            }
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "Permission denied. Can't fetch location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestNewLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Location", "Permission denied for location updates.");
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    getCountryFromLocation(location);
                }
            }
        }, null);
    }


    private void getCountryFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String country = addresses.get(0).getCountryName();
                Log.d("Location", "Country: " + country);
                userCountryTextView.setText(country);
            } else {
                Log.d("Location", "No address found for the location");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LocationError", "Geocoder IOException", e);
            Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Can't fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchAndDisplayCoinBalance() {
        FirebaseAuthHelper authHelper = new FirebaseAuthHelper();
        FirebaseUser currentUser = authHelper.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

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
