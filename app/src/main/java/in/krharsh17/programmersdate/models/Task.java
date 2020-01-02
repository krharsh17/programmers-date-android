package in.krharsh17.programmersdate.models;

import java.util.ArrayList;

import in.krharsh17.programmersdate.Constants;

public class Task implements Constants {

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
        private ArrayList<String> QRList;

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

        public ArrayList<String> getQRList() {
            return QRList;
        }

        public void setQRList(ArrayList<String> QRList) {
            this.QRList = QRList;
        }
    }

    public static class BarCode {
        private String taskType = taskTypeBar;
        private ArrayList<ArrayList<Double>> locations;
        private ArrayList<String> BarcodeList;

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
            return BarcodeList;
        }

        public void setBarcodeList(ArrayList<String> barcodeList) {
            BarcodeList = barcodeList;
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
