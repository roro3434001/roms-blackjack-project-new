package com.example.romsproject;

public class Game {
    private Deck deck;
    private Player player;
    private Player dealer;
    private boolean isGameOver;
    private int currentHandIndex = 0; // Track which hand is active

    public Game() {
        deck = new Deck();
        player = new Player();
        dealer = new Player();
        isGameOver = false;
    }

    public void startNewRound(int bet) {
        if (isGameOver) {
            resetGame();
        }
        player.placeBet(bet);
        player.resetHand();
        dealer.resetHand();
        deck.shuffleDeck();
        currentHandIndex = 0; // Reset hand index for new round

        // Deal two cards to player
        player.addCard(deck.drawCard());
        player.addCard(deck.drawCard());

        // Deal two cards to dealer: one hidden ("card_back") and one visible
        dealer.addCard(new Card()); // Hidden card for dealer (card_back)
        dealer.addCard(deck.drawCard()); // Visible card for dealer

        // If player gets 21 from initial deal, their turn ends immediately
        if (player.getHandValue() == 21) {
            playerStands();
        }
    }


    public void playerHits() {
        if (!isGameOver) {
            player.addCardToHand(currentHandIndex, deck.drawCard());

            if (player.getHandValue(currentHandIndex) == 21 || player.isBusted(currentHandIndex)) {
                switchToNextHandOrDealer();
            }
        }
    }


    public void playerStands() {
        if (!isGameOver) {
            switchToNextHandOrDealer();
        }
    }


    private void switchToNextHandOrDealer() {
        if (currentHandIndex < player.getNumberOfHands() - 1) {
            currentHandIndex++; // Move to the next hand
        } else {
            while (dealer.getHandValue(0) < 17) { // Dealer plays
                dealer.addCard(deck.drawCard());
            }
            isGameOver = true;
        }
    }



    public String getWinner() {
        if (player.isBusted()) {
            return "Dealer Wins!";
        } else if (dealer.isBusted()) {
            return "Player Wins!";
        } else if (player.getHandValue(0) > dealer.getHandValue(0)) {
            return "Player Wins!";
        } else if (player.getHandValue(0) < dealer.getHandValue(0)) {
            return "Dealer Wins!";
        } else {
            return "It's a Tie!";
        }
    }

    public void resetGame() {
        deck = new Deck();
        player.resetHand();
        dealer.resetHand();
        isGameOver = false;
        currentHandIndex = 0; // Reset for new round
    }

    public int getCurrentHandIndex() {
        return currentHandIndex;
    }

    public void switchToNextHand() {
        if (currentHandIndex < player.getNumberOfHands() - 1) {
            currentHandIndex++;
        }
    }

    public void resetHandIndex() {
        currentHandIndex = 0; // Reset for a new round
    }

    public Player getPlayer() {
        return player;
    }

    public Player getDealer() {
        return dealer;
    }

    public Deck getDeck() {
        return deck;
    }

}
