package com.example.romsproject;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<List<Card>> hands;  // A list of hands for splitting
    private List<Integer> handValues; // A list of hand values (for multiple hands)
    private List<Boolean> hasAces;   // A list to track Aces for each hand
    private int betAmount;

    public Player() {
        hands = new ArrayList<>();
        handValues = new ArrayList<>();
        hasAces = new ArrayList<>();
        betAmount = 0;

        // Initialize with a default empty hand
        hands.add(new ArrayList<>());
        handValues.add(0);
        hasAces.add(false);
    }

    public void placeBet(int amount) {
        this.betAmount = amount;
    }

    public int getBetAmount() {
        return betAmount;
    }

    // Add a card to the first hand (default)
    public void addCard(Card card) {
        addCardToHand(0, card);  // Always add to the first hand
    }

    // Add a card to a specific hand
    public void addCardToHand(int handIndex, Card card) {
        if (handIndex >= hands.size()) {
            hands.add(new ArrayList<>());
            handValues.add(0);
            hasAces.add(false);
        }

        hands.get(handIndex).add(card);
        handValues.set(handIndex, handValues.get(handIndex) + card.getValue());

        // Check if Ace is present
        if (card.getRank().equals("Ace")) {
            hasAces.set(handIndex, true);
        }

        // Adjust Ace value if needed
        if (handValues.get(handIndex) > 21 && hasAces.get(handIndex)) {
            handValues.set(handIndex, handValues.get(handIndex) - 10);
            hasAces.set(handIndex, false);
        }
    }

    public List<Card> getHand(int handIndex) {
        if (handIndex < hands.size()) {
            return hands.get(handIndex);
        } else {
            return new ArrayList<>();
        }
    }

    // Get the value of the first hand (default) or a specified hand
    public int getHandValue() {
        return getHandValue(0); // Default to first hand
    }

    public int getHandValue(int handIndex) {
        if (handIndex < handValues.size()) {
            return handValues.get(handIndex);
        } else {
            return 0;
        }
    }

    // Check if the first hand (default) or a specified hand is busted
    public boolean isBusted() {
        return isBusted(0); // Default to first hand
    }

    public boolean isBusted(int handIndex) {
        if (handIndex < handValues.size()) {
            return handValues.get(handIndex) > 21;
        } else {
            return false;
        }
    }

    // Reset all hands and values
    public void resetHand() {
        resetHands();
    }

    public void resetHands() {
        hands.clear();
        handValues.clear();
        hasAces.clear();

        // Ensure at least one empty hand exists
        hands.add(new ArrayList<>());
        handValues.add(0);
        hasAces.add(false);
    }

    // Split the hand into two hands if the player has exactly two cards of the same rank
    public void splitHand(Deck deck) {
        if (canSplit()) {
            Card card1 = hands.get(0).remove(1); // Remove second card from the first hand

            // Create a new hand with the second card
            List<Card> newHand = new ArrayList<>();
            newHand.add(card1);
            hands.add(newHand);

            // Reset values
            handValues.add(card1.getValue());
            hasAces.add(card1.getRank().equals("Ace"));

            // Draw new cards for both hands
            hands.get(0).add(deck.drawCard());
            hands.get(1).add(deck.drawCard());

            // Recalculate values
            handValues.set(0, getHandValue(0));
            handValues.set(1, getHandValue(1));
        }
    }



    // Get the number of hands the player has (1 for single hand, more if split)
    public int getNumberOfHands() {
        return hands.size();
    }

    // Get all hands of the player (used for displaying all hands after split)
    public List<List<Card>> getHands() {
        return hands;
    }

    // Check if the player can split the hand
    public boolean canSplit() {
        // Can only split if there's one hand and two cards of the same rank
        return hands.size() == 1 && hands.get(0).size() == 2 &&
                hands.get(0).get(0).getRank().equals(hands.get(0).get(1).getRank());
    }
}
