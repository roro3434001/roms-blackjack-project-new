package com.example.romsproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import android.os.CountDownTimer;

import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

public class Gameplay extends AppCompatActivity {

    private TextView currentCoinsTextView, errorMessageTextView, playerHandTextView, dealerHandTextView, resultText;
    private EditText betAmountInput;
    private Button placeBetButton, btnHit, btnStand, btnSplit, btnDouble;
    private int currentBet = 0;
    private int userCoins = 0;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private Game game;
    private Player player;
    private List<Card> dealerHand;

    private CountDownTimer turnTimer;
    private TextView timerTextView; // UI element to display the countdown
    private static final int TURN_TIME_LIMIT = 30000; // 30 seconds

    private LinearLayout dealerHandContainer, playerHandContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        // Initialize views
        timerTextView = findViewById(R.id.timerTextView); // Ensure you add this TextView in XML

        currentCoinsTextView = findViewById(R.id.currentCoinsTextView);
        betAmountInput = findViewById(R.id.betAmountInput);
        placeBetButton = findViewById(R.id.placeBetButton);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        playerHandTextView = findViewById(R.id.playerHandTextView);
        dealerHandTextView = findViewById(R.id.dealerHandTextView);

        // Initialize the hand containers
        dealerHandContainer = findViewById(R.id.dealerHandContainer);
        playerHandContainer = findViewById(R.id.playerHandContainer);

        // Initialize buttons
        btnHit = findViewById(R.id.btn_hit);
        btnStand = findViewById(R.id.btn_stand);
        btnSplit = findViewById(R.id.btn_split);
        btnDouble = findViewById(R.id.btn_double);

        fetchUserCoins();

        // Set click listeners for the buttons
        placeBetButton.setOnClickListener(v -> placeBet());
        btnHit.setOnClickListener(v -> hit());
        btnStand.setOnClickListener(v -> stand());
        btnSplit.setOnClickListener(v -> split());
        btnDouble.setOnClickListener(v -> doubleDown());

        setGameplayButtonsEnabled(false);
    }


    private void fetchUserCoins() {
        mUserRef.child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userCoins = dataSnapshot.getValue(Integer.class);
                    updateCoinsDisplay();
                } else {
                    Toast.makeText(Gameplay.this, "Error: Coin balance not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Gameplay.this, "Error fetching coin balance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeBet() {
        String betText = betAmountInput.getText().toString().trim();

        if (betText.isEmpty()) {
            errorMessageTextView.setText("Please enter a bet amount.");
            return;
        }

        int betAmount = Integer.parseInt(betText);

        if (betAmount <= 0) {
            errorMessageTextView.setText("Bet amount must be greater than zero.");
        } else if (betAmount > userCoins) {
            errorMessageTextView.setText("You do not have enough coins.");
        } else {
            currentBet = betAmount;
            userCoins -= betAmount;
            updateCoinsDisplay();
            errorMessageTextView.setText("");

            setGameplayButtonsEnabled(true);
            placeBetButton.setEnabled(false);
            placeBetButton.setVisibility(View.INVISIBLE);
            betAmountInput.setEnabled(false);
            betAmountInput.setVisibility(View.INVISIBLE);

            startGame();
        }
    }

    private void startGame() {
        game = new Game();
        game.startNewRound(currentBet);
        player = game.getPlayer();

        if (player != null) {
            dealerHand = game.getDealer().getHand(0);
            updateHandDisplay(false);
            startTurnTimer(); // Start the countdown when the round begins

            if (player.getHandValue() == 21 && player.getNumberOfHands() == 1 && player.getHand(0).size() == 2) {
                endRound(true);
                return;
            }

            setGameplayButtonsEnabled(true);
        } else {
            Toast.makeText(this, "Error: Player initialization failed", Toast.LENGTH_SHORT).show();
        }
    }




    private void hit() {
        if (player != null) {
            game.playerHits();
            updateHandDisplay(false);

            // ✅ Reset the turn timer immediately after player action
            startTurnTimer();

            if (player.getHandValue(game.getCurrentHandIndex()) == 21) {
                Toast.makeText(Gameplay.this, "You hit 21 on Hand " + (game.getCurrentHandIndex() + 1) + "!", Toast.LENGTH_SHORT).show();
                stand();  // Auto-stand if 21
            } else if (player.isBusted(game.getCurrentHandIndex())) {
                endRound(false);
            }
        }
    }



    private void stand() {
        if (player != null) {
            Toast.makeText(Gameplay.this, "You chose to stand for Hand " + (game.getCurrentHandIndex() + 1), Toast.LENGTH_SHORT).show();

            game.playerStands();
            updateHandDisplay(true);

            // ✅ Reset the turn timer after the player action
            startTurnTimer();

            if (game.getCurrentHandIndex() < player.getNumberOfHands() - 1) {
                game.switchToNextHand();
                updateHandDisplay(false);
            } else {
                checkWinner();
            }
        }
    }




    private void split() {
        if (player != null && player.canSplit()) {
            if (userCoins >= currentBet) {
                userCoins -= currentBet;
                updateCoinsDisplay();
                player.splitHand(game.getDeck());
                game.switchToNextHand();
                updateHandDisplay(false);

                // ✅ Reset the turn timer after player splits
                startTurnTimer();

                Toast.makeText(this, "You chose to split!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not enough coins to split!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You cannot split at this time.", Toast.LENGTH_SHORT).show();
        }
    }



    private void doubleDown() {
        if (player != null) {
            if (userCoins >= currentBet) {
                userCoins -= currentBet;
                currentBet *= 2;
                updateCoinsDisplay();

                game.playerHits();
                updateHandDisplay(false);

                // ✅ Reset the timer right after the action
                stand();
                startTurnTimer();

            } else {
                Toast.makeText(this, "Not enough coins for double down!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateHandDisplay(boolean revealDealer) {
        if (player != null) {
            // Clear the player hand container before adding new cards
            playerHandContainer.removeAllViews();

            // Display each hand for the player
            for (int i = 0; i < player.getNumberOfHands(); i++) {
                // Display the cards in the player's hand (reveal all cards)
                displayCards(player.getHand(i), playerHandContainer, false);

                // Optionally log when a hand has 21 (if you want this feature)
                if (player.getHandValue(i) == 21) {
                    Log.d("Blackjack", "Hand " + (i + 1) + " has 21! Stopping...");
                }
            }
        }

        if (dealerHand != null) {
            // Clear the dealer hand container before adding new cards
            dealerHandContainer.removeAllViews();

            if (revealDealer) {
                // Show full dealer hand and reveal all cards when the dealer's hand is revealed
                displayCards(dealerHand, dealerHandContainer, true);
            } else {
                // Show only the first card (face-up), second card is hidden
                List<Card> hiddenHand = new ArrayList<>();
                hiddenHand.add(dealerHand.get(0));  // First card visible
                hiddenHand.add(null);  // Second card hidden (represent it with null or a placeholder)

                // Display the dealer's hidden card using the placeholder logic
                displayCards(hiddenHand, dealerHandContainer, false);
            }
        }
    }



    private String formatHand(List<Card> hand) {
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card.getRank()).append(" of ").append(card.getSuit()).append(", ");
        }
        return sb.toString();
    }

    private void checkWinner() {
        String winner = game.getWinner();
        Toast.makeText(this, winner, Toast.LENGTH_SHORT).show();
        endRound(winner.equals("Player Wins!"));
    }

    private void updateCoinsDisplay() {
        currentCoinsTextView.setText("Coins: " + userCoins);
    }

    private boolean isBlackjack(List<Card> hand) {
        // Blackjack is an Ace (value 11) and a 10-point card (10, Jack, Queen, King)
        if (hand.size() == 2) {
            int totalValue = 0;
            boolean hasAce = false;
            boolean hasTenCard = false;

            for (Card card : hand) {
                totalValue += card.getValue();
                if (card.getValue() == 11) { // Ace
                    hasAce = true;
                } else if (card.getValue() == 10) { // 10, Jack, Queen, King
                    hasTenCard = true;
                }
            }

            return hasAce && hasTenCard && totalValue == 21;
        }
        return false;
    }

    private void endRound(boolean playerWon) {
        if (turnTimer != null) {
            turnTimer.cancel(); // Stop timer at end of round
        }

        updateHandDisplay(true);

        boolean isBlackjack = (player.getHandValue() == 21 && player.getHand(0).size() == 2);
        boolean isTie = (player.getHandValue() == game.getDealer().getHandValue());

        if (playerWon) {
            if (isBlackjack) {
                userCoins += (int) (currentBet * 2.5);
                Toast.makeText(Gameplay.this, "Blackjack! You win " + (int) (currentBet * 1.5), Toast.LENGTH_SHORT).show();
            } else {
                userCoins += currentBet * 2;
                Toast.makeText(Gameplay.this, "You win " + currentBet, Toast.LENGTH_SHORT).show();
            }
        } else if (isTie) {
            userCoins += currentBet;
            Toast.makeText(Gameplay.this, "It's a tie! Your bet is returned.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Gameplay.this, "You lose. Better luck next time!", Toast.LENGTH_SHORT).show();
        }

        setGameplayButtonsEnabled(false);
        placeBetButton.setEnabled(true);
        placeBetButton.setVisibility(View.VISIBLE);
        betAmountInput.setEnabled(true);
        betAmountInput.setVisibility(View.VISIBLE);
    }







    private void updateCoinsInFirebase(boolean playerWon, boolean isBlackjack) {
        // Firebase only updates userCoins (no extra additions here)
        mUserRef.child("coins").setValue(userCoins)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Gameplay.this, "Error updating coins!", Toast.LENGTH_SHORT).show();
                    }
                });

        updateCoinsDisplay(); // Ensure UI reflects the updated coin count
    }



    private void setGameplayButtonsEnabled(boolean isEnabled) {
        if (player != null) {
            btnHit.setEnabled(isEnabled);
            btnStand.setEnabled(isEnabled);
            btnSplit.setEnabled(isEnabled && player.canSplit()); // Enable split only if player can split
            btnDouble.setEnabled(isEnabled);
        }
    }

    private void startTurnTimer() {
        if (turnTimer != null) {
            turnTimer.cancel(); // Cancel any existing timer
        }

        turnTimer = new CountDownTimer(TURN_TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time Left: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                Toast.makeText(Gameplay.this, "Time's up! You automatically stand.", Toast.LENGTH_SHORT).show();
                stand(); // Automatically stand if time runs out
            }
        }.start();
    }

    private int getCardImageResource(Card card) {
        if (card == null) {
            return R.drawable.card_back; // Placeholder for hidden card
        }

        // Create the card name as "rank_of_suit"
        String cardName = card.getRank().toString().toLowerCase() + "_of_" + card.getSuit().toString().toLowerCase();

        // Return the corresponding drawable resource
        int resId = getResources().getIdentifier(cardName, "drawable", getPackageName());
        return resId;
    }

    // Helper method to display cards as images in a container
    private void displayCards(List<Card> hand, LinearLayout container, boolean revealCards) {
        container.removeAllViews(); // Clear previous cards from the container

        for (Card card : hand) {
            ImageView cardImageView = new ImageView(this);

            // If the card is null (for hidden cards), show a placeholder
            if (card == null && !revealCards) {
                cardImageView.setImageResource(R.drawable.card_back);  // A placeholder image for the hidden card
            } else {
                // Otherwise, display the actual card image
                int cardImageResId = getCardImageResource(card);  // Get the image resource for the card
                cardImageView.setImageResource(cardImageResId);
            }

            // Set layout parameters for the image (optional, you can customize this)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(8, 0, 8, 0); // Set margins for spacing between the cards
            cardImageView.setLayoutParams(params);

            // Add the image to the container
            container.addView(cardImageView);
        }
    }



}
