package com.example.romsproject;

import android.util.Patterns;

public class InputValidator {

    // Validate username (at least 5 characters)
    public boolean isValidUsername(String username) {
        return username != null && username.trim().length() >= 5;
    }

    // Validate email format
    public boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate password (at least 8 characters)
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    // New method for validating bets
    public String validateBet(String betText, int userCoins) {
        if (betText == null || betText.trim().isEmpty()) {
            return "Please enter a bet amount!";
        }

        int bet;
        try {
            bet = Integer.parseInt(betText.trim());
        } catch (NumberFormatException e) {
            return "Invalid bet amount! Enter a number.";
        }

        if (bet <= 0) {
            return "Bet must be greater than zero!";
        }

        if (bet > userCoins) {
            return "Not enough coins!";
        }

        return null; // No errors
    }
}

