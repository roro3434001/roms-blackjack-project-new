package com.example.romsproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private EditText emailEditText, usernameEditText, passwordEditText;
    private Button registerButton, loginButton;
    private FirebaseAuthHelper authHelper;
    private InputValidator inputValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize FirebaseAuthHelper and InputValidator
        authHelper = new FirebaseAuthHelper();
        inputValidator = new InputValidator();

        // Initialize views
        emailEditText = findViewById(R.id.email_write);
        usernameEditText = findViewById(R.id.username_write);
        passwordEditText = findViewById(R.id.password_write);
        registerButton = findViewById(R.id.Register);
        loginButton = findViewById(R.id.button_goLogin);  // Ensure the ID is correct

        // Check if the user is already logged in
        if (authHelper.isUserLoggedIn()) {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set up login button click listener to go to the Login Activity
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            });
        }

        // Set up register button click listener
        registerButton.setOnClickListener(v -> registerUser());
    }

    // Method to handle user registration
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email
        if (!inputValidator.isValidEmail(email)) {
            emailEditText.setError("Invalid email format");
            emailEditText.requestFocus();
            return;
        }

        // Validate username
        if (!inputValidator.isValidUsername(username)) {
            usernameEditText.setError("Username must be at least 5 characters");
            usernameEditText.requestFocus();
            return;
        }

        // Validate password
        if (!inputValidator.isValidPassword(password)) {
            passwordEditText.setError("Password must be at least 8 characters");
            passwordEditText.requestFocus();
            return;
        }

        // New Code: Create a User object and initialize it with email and username
        User user = new User(email, username); // The User object now includes a "coins" field

        // Pass the user object to FirebaseAuthHelper to register
        authHelper.registerUser(email, password, user, Register.this); // Call the updated registerUser method
    }
}
