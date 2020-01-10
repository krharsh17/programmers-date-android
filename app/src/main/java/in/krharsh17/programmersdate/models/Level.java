package in.krharsh17.programmersdate.models;

import java.util.ArrayList;

public class Level {
    private int levelNumber;
    private String taskType;
    private ArrayList<String> locations;
    private String qrValue;
    private String barValue;
    private String poseValue;
    private String logoValue;
    private String audioValue;
    private boolean skipped;

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
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

    public Level(int levelNumber, String taskType, boolean isSkipped ) {
        this.levelNumber = levelNumber;
        this.taskType = taskType;
        this.skipped = isSkipped;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> locations) {
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

    public Level(){

    }
}
