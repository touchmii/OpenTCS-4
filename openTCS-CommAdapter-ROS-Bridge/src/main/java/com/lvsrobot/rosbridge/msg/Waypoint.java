package com.lvsrobot.rosbridge.msg;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.std.Header;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;
import java.util.Arrays;

/*WayPoint msg
Header header
string name
geometry_msgs/Pose pose*/

public class Waypoint extends Message {
    public static final String FIELD_HEADER = "header";

    public static final String FILED_NAME = "name";

    /**
     * The name of the poses field for the message.
     */
    public static final String FIELD_POSE = "pose";

    /**
     * The message type.
     */
    public static final String TYPE = "yocs_msgs/WayPoint";

    private final Header header;
    private final Pose pose;
    private String name = null;

    /**
     * Create a new WayPoint with no poses.
     */
    public Waypoint() {
        this(new Header(), "0", new Pose());
    }

    /**
     * Create a new WayPoint with the given set of poses and header. The array
     * of poses will be copied into this object.
     *
     * @param header The message header.
     * @param pose  The poses of the pose array.
     */
    public Waypoint(Header header, String name, Pose pose) {
        // build the JSON object
        super(Json
                .createObjectBuilder()
                .add(Waypoint.FIELD_HEADER, header.toJsonObject())
//                .add(Waypoint.FILED_NAME, Json.createObjectBuilder().add("name", name).build())
                .add(Waypoint.FILED_NAME, name)
                .add(Waypoint.FIELD_POSE, pose.toJsonObject()).build(), Waypoint.TYPE);

        this.header = header;
        this.name = name;
        // copy the poses
        this.pose = pose;
    }

    /**
     * Get the poses of this pose array. Note that this array should never be
     * modified directly.
     *
     * @return The poses of this pose array.
     */
    public Pose getPose() {
        return this.pose;
    }

    public String getName() { return this.name; }

    /**
     * Get the header value of this pose array.
     *
     * @return The poses header value of this pose array.
     */
    public Header getHeader() {
        return this.header;
    }

    /**
     * Create a clone of this WayPoint.
     */
    @Override
    public Waypoint clone() {
        return new Waypoint(this.header, this.name, this.pose);
    }

    /**
     * Create a new WayPoint based on the given JSON string. Any missing values
     * will be set to their defaults.
     *
     * @param jsonString The JSON string to parse.
     * @return A WayPoint message based on the given JSON string.
     */
    public static Waypoint fromJsonString(String jsonString) {
        // convert to a message
        return Waypoint.fromMessage(new Message(jsonString));
    }

    /**
     * Create a new WayPoint based on the given Message. Any missing values
     * will be set to their defaults.
     *
     * @param m The Message to parse.
     * @return A WayPoint message based on the given Message.
     */
    public static Waypoint fromMessage(Message m) {
        // get it from the JSON object
        return Waypoint.fromJsonObject(m.toJsonObject());
    }

    /**
     * Create a new WayPoint based on the given JSON object. Any missing values
     * will be set to their defaults.
     *
     * @param jsonObject The JSON object to parse.
     * @return A WayPoint message based on the given JSON object.
     */
    public static Waypoint fromJsonObject(JsonObject jsonObject) {
        // grab the header if there is one
        Header header = jsonObject.containsKey(Waypoint.FIELD_HEADER) ? Header
                .fromJsonObject(jsonObject
                        .getJsonObject(Waypoint.FIELD_HEADER)) : new Header();

        // check the array
        String name = jsonObject.containsKey(Waypoint.FILED_NAME) ? jsonObject.getJsonObject(Waypoint.FILED_NAME).toString() : "0";
        Pose pose = jsonObject.containsKey(Waypoint.FIELD_HEADER) ?
                Pose.fromJsonObject(jsonObject.getJsonObject(Waypoint.FIELD_POSE)) : new Pose();

        return new Waypoint(header, name, pose);



    }
}