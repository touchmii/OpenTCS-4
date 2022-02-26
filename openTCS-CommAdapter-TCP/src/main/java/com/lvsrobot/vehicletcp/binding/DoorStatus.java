package com.lvsrobot.vehicletcp.binding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DoorStatus {
    public void setError(int error) {
        this.error = error;
    }

    //{"error":-4, "door":1, "controlPin":2, "statusPin":1, "action":"close", "message":"Door is already closed", "hold time":"forever", "device id":"10733225", "UTC+8 Time":"2021/10/18 11:43:41"}
    //{"error":0, "door":1, "controlPin":2, "statusPin":1, "action":"status", "status":"closed", "relay":"0", "hold time":"forever", "decive id":"10733225", "UTC+8 Time":"2021/10/18 11:43:23"}
    //{"error":0, "door":1, "controlPin":2, "statusPin":1, "action":"open", "message":"Door opened", "hold time":"forever", "device id":"10733225", "UTC+8 Time":"2021/10/18 11:55:12"}
    //{"error":-3, "door":1, "controlPin":2, "statusPin":1, "action":"open", "message":"Door is already open", "hold time":"forever", "device id":"10733225", "UTC+8 Time":"2021/10/18 11:55:25"}
    //{"error":0, "door":1, "controlPin":2, "statusPin":1, "action":"close", "message":"Door closed", "hold time":"forever", "device id":"10733225", "UTC+8 Time":"2021/10/18 11:55:40"}
    //离线状态
    private int error = -99;
    @JsonProperty(value = "door")
    private int doorID;
    private int controlPin;
    private int statusPin;

    public void setAction(String action) {
        this.action = action;
    }

    private String action;
//    @JsonProperty(value = "message")
//    @JsonIgnore(value = true)
//    @JsonIgnoreProperties()
//    @JsonIgnoreProperties
    private String message;

    public void setStatus(String status) {
        this.status = status;
    }

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

    @Override
    public String toString() {
        return "DoorStatus{" +
                "error=" + error +
                ", doorID=" + doorID +
                ", controlPin=" + controlPin +
                ", statusPin=" + statusPin +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", relayID='" + relayID + '\'' +
                ", hold_time='" + hold_time + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", utcTime='" + utcTime + '\'' +
                '}';
    }
}
