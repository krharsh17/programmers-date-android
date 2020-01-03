package in.krharsh17.programmersdate.models;

import java.util.ArrayList;

import in.krharsh17.programmersdate.Constants;

public class Tasks implements Constants {

    private Logo logoTask;
    private QRCode qrTask;
    private BarCode barTask;
    private Pose poseTask;

    public Logo getLogoTask() {
        return logoTask;
    }

    public void setLogoTask(Logo logoTask) {
        this.logoTask = logoTask;
    }

    public QRCode getQrTask() {
        return qrTask;
    }

    public void setQrTask(QRCode qrTask) {
        this.qrTask = qrTask;
    }

    public BarCode getBarTask() {
        return barTask;
    }

    public void setBarTask(BarCode barTask) {
        this.barTask = barTask;
    }

    public Pose getPoseTask() {
        return poseTask;
    }

    public void setPoseTask(Pose poseTask) {
        this.poseTask = poseTask;
    }

    public static class Logo {
        private String taskType = taskTypeLogo;
        private ArrayList<ArrayList<Double>> locations;
        private ArrayList<String> logoList;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public ArrayList<ArrayList<Double>> getLocations() {
            return locations;
        }

        public void setLocations(ArrayList<ArrayList<Double>> locations) {
            this.locations = locations;
        }

        public ArrayList<String> getLogoList() {
            return logoList;
        }

        public void setLogoList(ArrayList<String> logoList) {
            this.logoList = logoList;
        }
    }

    public static class QRCode {
        private String taskType = taskTypeQR;
        private ArrayList<ArrayList<Double>> locations;
        private ArrayList<String> qrList;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public ArrayList<ArrayList<Double>> getLocations() {
            return locations;
        }

        public void setLocations(ArrayList<ArrayList<Double>> locations) {
            this.locations = locations;
        }

        public ArrayList<String> getQrList() {
            return qrList;
        }

        public void setQrList(ArrayList<String> qrList) {
            this.qrList = qrList;
        }
    }

    public static class BarCode {
        private String taskType = taskTypeBar;
        private ArrayList<ArrayList<Double>> locations;
        private ArrayList<String> barcodeList;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public ArrayList<ArrayList<Double>> getLocations() {
            return locations;
        }

        public void setLocations(ArrayList<ArrayList<Double>> locations) {
            this.locations = locations;
        }

        public ArrayList<String> getBarcodeList() {
            return barcodeList;
        }

        public void setBarcodeList(ArrayList<String> barcodeList) {
            this.barcodeList = barcodeList;
        }
    }

    public static class Pose {
        private String taskType = taskTypePose;
        private ArrayList<ArrayList<Double>> locations;
        private ArrayList<String> poseList;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public ArrayList<ArrayList<Double>> getLocations() {
            return locations;
        }

        public void setLocations(ArrayList<ArrayList<Double>> locations) {
            this.locations = locations;
        }

        public ArrayList<String> getPoseList() {
            return poseList;
        }

        public void setPoseList(ArrayList<String> poseList) {
            this.poseList = poseList;
        }
    }

}
