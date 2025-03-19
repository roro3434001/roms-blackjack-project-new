package com.example.romsproject;

public class Card {
    private String suit;
    private String rank;
    private int value;

    // Constructor for normal cards
    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    // Constructor for the "card_back"
    public Card() {
        this.suit = "card_back"; // Special identifier for a hidden card
        this.rank = "card_back"; // Same here
        this.value = 0; // No value for the hidden card
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {
        return value;
    }

    // A method to check if the card is a "card_back" card
    public boolean isCardBack() {
        return "card_back".equals(this.suit) && "card_back".equals(this.rank);
    }

    @Override
    public String toString() {
        if (isCardBack()) {
            return "Card Back"; // Representing the card as hidden
        }
        return rank + " of " + suit; // Normal card representation
    }
}
