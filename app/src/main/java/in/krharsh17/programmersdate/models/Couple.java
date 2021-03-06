package in.krharsh17.programmersdate.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import in.krharsh17.programmersdate.Constants;

public class Couple {

    private String id;
    private String player1Name;
    private String player2Name;
    private int player1Roll;
    private int player2Roll;
    private int currentLevel;
    private ArrayList<Double> player1Location;
    private ArrayList<Double> player2Location;
    private ArrayList<Level> levels;
    private long timeLimit = Constants.timeLimit;
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public int getPlayer1Roll() {
        return player1Roll;
    }

    public void setPlayer1Roll(int player1Roll) {
        this.player1Roll = player1Roll;
    }

    public int getPlayer2Roll() {
        return player2Roll;
    }

    public void setPlayer2Roll(int player2Roll) {
        this.player2Roll = player2Roll;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public ArrayList<Double> getPlayer1Location() {
        return player1Location;
    }

    public void setPlayer1Location(ArrayList<Double> player1Location) {
        this.player1Location = player1Location;
    }

    public ArrayList<Double> getPlayer2Location() {
        return player2Location;
    }

    public void setPlayer2Location(ArrayList<Double> player2Location) {
        this.player2Location = player2Location;
    }

    public static LatLng getLatLng(ArrayList<Double> location) {
        return new LatLng(location.get(0), location.get(1));
    }
}
