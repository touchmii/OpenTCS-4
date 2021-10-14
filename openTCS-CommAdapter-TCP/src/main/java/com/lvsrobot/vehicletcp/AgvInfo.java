package com.lvsrobot.vehicletcp;

public class AgvInfo {
    private int position;
    private int direction;
    private int speed;
    private int exception;
    private int battery;
    private int status;
    private double angle;
    /*
    避障距离cm
     */
    private int obstacle;
    private int charge;

    public int getTuopan() {
        return tuopan;
    }

    public void setTuopan(int tuopan) {
        this.tuopan = tuopan;
    }

    private int tuopan;

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

    public void setObstacle(int _bizhang) { this.obstacle = _bizhang;}

    public int getObstacle() {return this.obstacle;}

    public void setCharge(int _charge) { this.charge = _charge;}

    public int getCharge() {return this.charge;}

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

    public double getAngle() {
        return angle;
    }
}
