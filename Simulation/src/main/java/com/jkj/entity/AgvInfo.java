package com.jkj.entity;

public class AgvInfo {

    private int currentPosition;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getException() {
        return exception;
    }

    public void setException(int exception) {
        this.exception = exception;
    }

    public int getElectric() {
        return electric;
    }

    public void setElectric(int electric) {
        this.electric = electric;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCargoFlag() {
        return cargoFlag;
    }

    public void setCargoFlag(int cargoFlag) {
        this.cargoFlag = cargoFlag;
    }

    private int status;

    private int exception;

    private int electric;

    private int speed;

    private int cargoFlag;
}
