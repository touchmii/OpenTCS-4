package com.lvsrobot.vehicle;

public class AgvInfo {
    private int position;
    private int speed;
    private int electric;
    private int exception;
    private int status;

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

    public int getElectric() {
        return electric;
    }

    public void setElectric(int electric) {
        this.electric = electric;
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
}
