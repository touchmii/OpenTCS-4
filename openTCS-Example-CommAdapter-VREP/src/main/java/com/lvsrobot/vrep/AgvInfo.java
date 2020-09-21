package com.lvsrobot.vrep;

import org.opentcs.data.model.Triple;

public class AgvInfo {
    private int precisePosition[];
    private int currentPosition[];
    private int previousPosition[];
    private double vehicle_orientation;
//    private int current_position_y;
    private int currentPositionID;
    private int targetPosition[];
//    private int target_position_y;
    private int targetPositionID;
    private int previousPositonID;
    private int currentSpeed;
    private int currentAngle;
    private int orderID;
    private int orderStatus;
    private int navigateStatus;
    private int navigateType;
    private int battery;
    private int up_time;
    private int cureent_use_map;
    private int avoid_status;
    private int cureent_velocity[];
    private int angular_velocity;
    private int forkAltitude;
    private int forkAngle;
    private int forkLength;
    private int forkStatus;
    private int loadStatus;
    private int unload_status;
    private int carries_status;
    private int charge_status;
    private int electric;
    private int exception;
    private int status;
    private int avoidance;
    private int chargeStatus;


    /**
     * 设置车辆叉臂状态
     * @param int fork_status
     * 0: UnLoad 1: load
     */
    public void setVehicleForkStatus(int fork_status) {
        this.forkStatus = fork_status;
    }

    /**
     * 获取车辆叉臂状态
     * @return int for_status
     * 0: Unload 1: Load
     */
    public int getVehicleForkStatus() { return this.forkStatus;}
    /**
     * 设置车辆避障状态
     * @param int avoidance_status
     */
    public void setVehicleAvoidance(int avoidance_status) { this.avoidance = avoidance_status;}

    /**
     * 获取车辆避障状态
     * @return int avoidance_tatus
     */
    public int getVehicleAvoidance() { return this.avoidance;}
    /**
     * 设置车辆角度，单位度
     * @param double orientation
     */
    public void setVehicleOrientation(double orientation) { this.vehicle_orientation = orientation; }
    public double getVehicleOrientation() { return this.vehicle_orientation > 270.0 ? this.vehicle_orientation-270 : this.vehicle_orientation+90; }
    public int getPosition() {
        return currentPositionID;
    }
    public void setPostion() {}
    /*
    设置车辆当前坐标
     */
    public void setCurrentPosition(int position[]) {
        this.currentPosition = position;
    }
    /*
    获取车辆当前坐标
     */
    public Triple getCurrentPosition() {return new Triple((long)currentPosition[0], (long)currentPosition[1], 0);}
    public void setCurrentPositionID(int position_id) { this.currentPositionID = position_id; }
//    public int getCurrentPositionId() { return this.currentPositionID; }
    public void setPreviousPositon(int previous_position[]) {
        this.previousPosition = previous_position;
    }
    public Triple getPreviousPosition() { return new Triple((long)previousPosition[0], (long)previousPosition[1], 0); }
    public void setPrecisePosition( int precise_position[]) { this.precisePosition = precise_position; }
    public Triple getPrecisePosition() { return new Triple((long)precisePosition[0], (long)precisePosition[1], 0);}
    public void setTargetPosition( int target_position[]) { this.targetPosition = target_position; }
    public Triple getTargetPosition() { return new Triple((long)targetPosition[0], (long)targetPosition[1], 0);}

    public void setOrderID( int order_id) { this.orderID = order_id; }
    public int getOrderID() { return this.orderID; }

    public int getSpeed() {
        return this.currentSpeed;
    }

    public void setSpeed(int speed) {
        this.currentSpeed = speed;
    }

    public int getBattery() {return this.battery;}

    public void setBattery(int battery) { this.battery = battery; }

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

    public void setLoadStatus(int load_status) { this.loadStatus = load_status; }
    public int getLoadStatus() { return this.loadStatus; }

    public void setCharge_status(int charge_status1) {this.chargeStatus = charge_status;}
    public int getChargeStatus() {return this.chargeStatus;}
}
