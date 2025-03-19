package com.example.romsproject;

public class LeaderBoardModel {
    String playerName;
    String playerCoinBalance;
    int image;

    public LeaderBoardModel(String playerName, String playerCoinCount, int image) {
        this.playerName = playerName;
        this.playerCoinBalance = playerCoinCount;
        this.image = image;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerCoinBalance() {
        return playerCoinBalance;
    }

    public int getImage() {
        return image;
    }
}
