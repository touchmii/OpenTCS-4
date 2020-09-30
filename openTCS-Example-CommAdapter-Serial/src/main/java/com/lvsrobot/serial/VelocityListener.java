package com.lvsrobot.serial;

public interface VelocityListener {

    /**
     * Called when a new velocity value (in mm/s) has been computed.
     *
     * @param velocityValue The new velocity value that has been computed.
     */
    void addVelocityValue(int velocityValue);
}
