package com.example.romsproject;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LeaderBoard_RecyclerViewAdapter adapter;
    private ArrayList<LeaderBoardModel> leaderBoardModels = new ArrayList<>();

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        recyclerView = findViewById(R.id.MyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Load users from Firebase
        fetchUsersFromFirebase();
    }

    private void fetchUsersFromFirebase() {
        usersRef.orderByChild("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderBoardModels.clear(); // Clear previous data

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        // Assign default image (change this if needed)
                        int defaultImage = R.drawable.baseline_face_24;

                        // Create a LeaderBoardModel object with user data
                        LeaderBoardModel model = new LeaderBoardModel(
                                user.getUsername(),
                                String.valueOf(user.getCoins()), // Convert int to String
                                defaultImage
                        );

                        leaderBoardModels.add(model);
                    }
                }

                // Reverse list to show highest coins first (leaderboard style)
                leaderBoardModels.sort((u1, u2) -> Integer.compare(Integer.parseInt(u2.getPlayerCoinBalance()), Integer.parseInt(u1.getPlayerCoinBalance())));

                // Set up the adapter
                adapter = new LeaderBoard_RecyclerViewAdapter(LeaderBoardActivity.this, leaderBoardModels);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch users: " + error.getMessage());
            }
        });
    }
}
