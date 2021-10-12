package com.lvsrobot.vehicletcp.binding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DoorStatus {
    //离线状态
    private int error = -99;
    @JsonProperty(value = "door")
    private int doorID;
    private int controlPin;
    private int statusPin;
    private String action;
//    @JsonProperty(value = "message")
//    @JsonIgnore(value = true)
//    @JsonIgnoreProperties()
    private String message;
    private String status;
    @JsonProperty(value = "relay")
    private String relayID;
    @JsonProperty(value = "hold time")
    private String hold_time;
    @JsonProperty(value = "device id")
    private String deviceID;
    @JsonProperty(value = "firmware version")
    private String firmwareVersion;
    @JsonProperty(value = "UTC Time")
    private String utcTime;

//    private boolean offline;

    public int getError() {
        return error;
    }

    public int getDoorID() {
        return doorID;
    }

    public int getControlPin() {
        return controlPin;
    }

    public int getStatusPin() {
        return statusPin;
    }

    public String getAction() {
        return action;
    }

    public String getStatus() {
        return status;
    }

    public String getRelayID() {
        return relayID;
    }

    public String getHold_time() {
        return hold_time;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getUtcTime() {
        return utcTime;
    }
}
