package com.lvsrobot.rosbridge.msg;

import java.io.StringReader;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseArray;
import edu.wpi.rail.jrosbridge.messages.std.Header;

/*# Way points

Waypoint[] waypoints*/

public class WaypointList extends Message {
    /**
     * The name of the poses field for the message.
     */
    public static final String FIELD_WAYPOINTS = "waypoints";

    /**
     * The message type.
     */
    public static final String TYPE = "yocs_msgs/WaypointList";

    private final Waypoint[] waypoints;

    /**
     * Create a new PoseArray with no poses.
     */
    public WaypointList() {
        this(new Waypoint[]{});
    }

    /**
     * Create a new PoseArray with the given set of poses and header. The array
     * of poses will be copied into this object.
     *
     * @param waypoints  The poses of the pose array.
     */
    public WaypointList(Waypoint[] waypoints) {
        // build the JSON object
        super(Json
                .createObjectBuilder()
                .add(WaypointList.FIELD_WAYPOINTS,
                        Json.createReader(
                                new StringReader(Arrays.deepToString(waypoints)))
                                .readArray()).build(), WaypointList.TYPE);

        // copy the poses
        this.waypoints = new Waypoint[waypoints.length];
        System.arraycopy(waypoints, 0, this.waypoints, 0, waypoints.length);
    }

    /**
     * Get the number of poses in this pose array.
     *
     * @return The number of poses in this pose array.
     */
    public int size() {
        return this.waypoints.length;
    }

    /**
     * Get the pose in the pose array at the given index.
     *
     * @param index The index to get the pose of.
     * @return The pose at the given index.
     */
    public Waypoint get(int index) {
        return this.waypoints[index];
    }

    /**
     * Get the poses of this pose array. Note that this array should never be
     * modified directly.
     *
     * @return The poses of this pose array.
     */
    public Waypoint[] getWaypoints() {
        return this.waypoints;
    }


    /**
     * Create a clone of this PoseArray.
     */
    @Override
    public WaypointList clone() {
        return new WaypointList(this.waypoints);
    }

    /**
     * Create a new PoseArray based on the given JSON string. Any missing values
     * will be set to their defaults.
     *
     * @param jsonString The JSON string to parse.
     * @return A PoseArray message based on the given JSON string.
     */
    public static WaypointList fromJsonString(String jsonString) {
        // convert to a message
        return WaypointList.fromMessage(new Message(jsonString));
    }

    /**
     * Create a new PoseArray based on the given Message. Any missing values
     * will be set to their defaults.
     *
     * @param m The Message to parse.
     * @return A PoseArray message based on the given Message.
     */
    public static WaypointList fromMessage(Message m) {
        // get it from the JSON object
        return WaypointList.fromJsonObject(m.toJsonObject());
    }

    /**
     * Create a new PoseArray based on the given JSON object. Any missing values
     * will be set to their defaults.
     *
     * @param jsonObject The JSON object to parse.
     * @return A PoseArray message based on the given JSON object.
     */
    public static WaypointList fromJsonObject(JsonObject jsonObject) {
        // grab the header if there is one

        // check the array
        JsonArray jsonWaypoints = jsonObject.getJsonArray(WaypointList.FIELD_WAYPOINTS);
            if (jsonWaypoints != null) {
            // convert each pose
            Waypoint[] waypoints = new Waypoint[jsonWaypoints.size()];
            for (int i = 0; i < waypoints.length; i++) {
                waypoints[i] = Waypoint.fromJsonObject(jsonWaypoints.getJsonObject(i));
            }
            return new WaypointList(waypoints);
        } else {
            return new WaypointList(new Waypoint[]{});
        }


    }
}