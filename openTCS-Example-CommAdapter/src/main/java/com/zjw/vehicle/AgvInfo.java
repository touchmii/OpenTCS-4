package com.zjw.vehicle;

public class AgvInfo {
    private int position;
    private int angle;
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

    public void setAngle(int i) {
        switch (i) {
            case 1:
                angle = 270;
                break;
            case 2:
                angle = 0;
                break;
            case 3:
                angle = 90;
                break;
            case 4:
                angle = 180;
                break;
            default:
                angle = -1;
        }
    }
    public int getAngle() { return angle; }

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
