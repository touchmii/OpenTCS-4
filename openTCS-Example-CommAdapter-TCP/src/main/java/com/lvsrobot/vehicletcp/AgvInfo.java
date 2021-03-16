package com.lvsrobot.vehicletcp;

public class AgvInfo {
    private int position;
    private int direction;
    private int speed;
    private int exception;
    private int battery;
    private int status;
    private double angle;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery_) {
        this.battery = (int)(battery_*1.66);
    }

    public int getException() {
        return exception;
    }

    public void setException(int exception) {
        this.exception = exception;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAngle(int i) {
        switch (i) {
            case 1:
                angle = 180;
                break;
            case 2:
                angle = 270;
                break;
            case 3:
                angle = 0;
                break;
            case 4:
                angle = 90;
                break;
            default:
                angle = -1;
        }
    }

    public double getAngle() {
        return angle;
    }
}
