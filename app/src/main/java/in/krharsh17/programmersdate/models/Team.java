package in.krharsh17.programmersdate.models;

import java.util.ArrayList;

public class Team  {

    private String id;
    private String player1Name;
    private String player2Name;
    private int player1Roll;
    private int player2Roll;
    private int teamLevel;
    private ArrayList<Double> player1Location;
    private ArrayList<Double> player2Location;

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

    public int getTeamLevel() {
        return teamLevel;
    }

    public void setTeamLevel(int teamLevel) {
        this.teamLevel = teamLevel;
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
}
