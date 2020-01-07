package in.krharsh17.programmersdate.models;

import java.util.ArrayList;

public class Level {
    private int levelNumber;
    private String taskType;
    private ArrayList<ArrayList<Double>> locations;
    private String qrValue;
    private String barValue;
    private String poseValue;
    private String logoValue;
    private String audioValue;

    public Level() {

    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Level(int levelNumber, String taskType) {
        this.levelNumber = levelNumber;
        this.taskType = taskType;
    }

    public ArrayList<ArrayList<Double>> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<ArrayList<Double>> locations) {
        this.locations = locations;
    }

    public String getQrValue() {
        return qrValue;
    }

    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }

    public String getBarValue() {
        return barValue;
    }

    public void setBarValue(String barValue) {
        this.barValue = barValue;
    }

    public String getPoseValue() {
        return poseValue;
    }

    public void setPoseValue(String poseValue) {
        this.poseValue = poseValue;
    }

    public String getLogoValue() {
        return logoValue;
    }

    public void setLogoValue(String logoValue) {
        this.logoValue = logoValue;
    }

    public String getAudioValue() {
        return audioValue;
    }

    public void setAudioValue(String audioValue) {
        this.audioValue = audioValue;
    }
}
