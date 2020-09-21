/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vrep;

import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;

/**
 * A serializable representation of a {@link ExampleProcessModel}.
 * This TO can be sent to other applications responsible for displaying the state of the vehicle,
 * like the control center or the plant overview.
 * 序列化ExampleProcessModel
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class ExampleProcessModelTO extends VehicleProcessModelTO {
    /**
     * Whether this communication adapter is in single step mode or not (i.e. in automatic mode).
     */
    private boolean singleStepModeEnabled;
    /**
     * Indicates which operation is a loading operation.
     */
    private String loadOperation;
    /**
     * Indicates which operation is an unloading operation.
     */
    private String unloadOperation;
    /**
     * The time needed for executing operations.
     */
    private int operatingTime;
    /**
     * The maximum acceleration.
     */
    private int maxAcceleration;
    /**
     * The maximum deceleration.
     */
    private int maxDeceleration;
    /**
     * The maximum forward velocity.
     */
    private int maxFwdVelocity;
    /**
     * The maximum reverse velocity.
     */
    private int maxRevVelocity;
    /**
     * Whether the vehicle is paused or not.
     */
    private boolean vehiclePaused;

    public boolean isSingleStepModeEnabled() {
        return singleStepModeEnabled;
    }

    public ExampleProcessModelTO setSingleStepModeEnabled(boolean singleStepModeEnabled) {
        this.singleStepModeEnabled = singleStepModeEnabled;
        return this;
    }

    public String getLoadOperation() {
        return loadOperation;
    }

    public ExampleProcessModelTO setLoadOperation(String loadOperation) {
        this.loadOperation = loadOperation;
        return this;
    }

    public String getUnloadOperation() {
        return unloadOperation;
    }

    public ExampleProcessModelTO setUnloadOperation(String unloadOperation) {
        this.unloadOperation = unloadOperation;
        return this;
    }

    public int getOperatingTime() {
        return operatingTime;
    }

    public ExampleProcessModelTO setOperatingTime(int operatingTime) {
        this.operatingTime = operatingTime;
        return this;
    }

    public int getMaxAcceleration() {
        return maxAcceleration;
    }

    public ExampleProcessModelTO setMaxAcceleration(int maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
        return this;
    }

    public int getMaxDeceleration() {
        return maxDeceleration;
    }

    public ExampleProcessModelTO setMaxDeceleration(int maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
        return this;
    }

    public int getMaxFwdVelocity() {
        return maxFwdVelocity;
    }

    public ExampleProcessModelTO setMaxFwdVelocity(int maxFwdVelocity) {
        this.maxFwdVelocity = maxFwdVelocity;
        return this;
    }

    public int getMaxRevVelocity() {
        return maxRevVelocity;
    }

    public ExampleProcessModelTO setMaxRevVelocity(int maxRevVelocity) {
        this.maxRevVelocity = maxRevVelocity;
        return this;
    }

    public boolean isVehiclePaused() {
        return vehiclePaused;
    }

    public ExampleProcessModelTO setVehiclePaused(boolean vehiclePaused) {
        this.vehiclePaused = vehiclePaused;
        return this;
    }
}
