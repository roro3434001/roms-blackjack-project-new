package com.example.romsproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private FirebaseAuthHelper authHelper;
    private InputValidator inputValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuthHelper and InputValidator
        authHelper = new FirebaseAuthHelper();
        inputValidator = new InputValidator();

        // Initialize views
        emailEditText = findViewById(R.id.email_write);
        passwordEditText = findViewById(R.id.password_write);
        loginButton = findViewById(R.id.btn_Login);
        registerButton = findViewById(R.id.btn_goRegister);

        // Set up login button click listener
        loginButton.setOnClickListener(v -> loginUser());

        // Set up register button click listener to go to the Register Activity
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);  // Navigate to Register Activity
        });
    }

    // Method to handle login
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Input validation
        if (!inputValidator.isValidEmail(email)) {
            emailEditText.setError("Invalid email format");
            emailEditText.requestFocus();
            return;
        }
        if (!inputValidator.isValidPassword(password)) {
            passwordEditText.setError("Password must be at least 8 characters");
            passwordEditText.requestFocus();
            return;
        }
        authHelper.loginUser(email,password,Login.this);
    }
}
