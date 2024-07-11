package com.example.fexifit4;

public class Progress {

    private String weight;
    private String runningSpeed;
    private String feedback;

    public Progress() {
        // Default constructor required for calls to DataSnapshot.getValue(Progress.class)
    }

    public Progress(String weight, String runningSpeed, String feedback) {
        this.weight = weight;
        this.runningSpeed = runningSpeed;
        this.feedback = feedback;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRunningSpeed() {
        return runningSpeed;
    }

    public void setRunningSpeed(String runningSpeed) {
        this.runningSpeed = runningSpeed;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
