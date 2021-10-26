package com.lvsrobot.vehicletcp;

import org.opentcs.data.model.Point;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.opentcs.drivers.vehicle.MovementCommand;

import java.util.List;

public class VehicleStatus {

    private MovementCommand curCommand;

    private Point pathStartPosition;
    private String pathStartID;


    double current_angle;

    double previous_angle;

    String currentPoint;
    int currentPoint_int;
    int currentStatus;
    int currentCharge;
    double currentAngle;
    long chargeTime = 0;

    String action = "";

    String ppp = null;

    private String wait_point = "";
    private String wait_oprate = "";

    private List<DriveOrder> driveOrderList = null;
    private List<Route.Step> stepList = null;
    private int driver_index = 0;

    private List<Point> openDoorList = null;
    private List<Point> closeDoorList = null;
    private String waitDoorID = null;
    private String openDoorID = null;
    private String closeDoorID = null;
    private int openDoorIndex = 0;
    private int closeDoorIndex = 0;
    private String doorName = null;
    private Route.Step doorStep = null;
    private boolean singleAction = false;
    private boolean updateDoorFlag = false;
}
