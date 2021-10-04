package com.lvsrobot.rosbridge.library;

//import builtin_interfaces.msg.Time;
import edu.wpi.rail.jrosbridge.messages.*;
import edu.wpi.rail.jrosbridge.messages.geometry.*;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import edu.wpi.rail.jrosbridge.messages.std.Time;
//import geometry_msgs.msg.*;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
//import std_msgs.msg.Header;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Library class for parsing outgoing rcljava messages
 *
 * @author Niels Tiben
 */
public abstract class OutgoingMessageLib {

    private static int seq = 1;
    public static PoseWithCovarianceStamped generateInitialPoseMessageByPoint(@Nonnull Point point) {
        Quaternion quaternion = new Quaternion(0,0,0,1);
        Pose pose = generatePoseMessageByPoint(point, quaternion);
//        quaternion.setW(1.0); // 1.0 = default value for ROS2; Mandatory field
//        pose.setOrientation(quaternion);

        PoseWithCovariance poseWithCovariance = new PoseWithCovariance(pose);
//        poseWithCovariance.setPose(pose);

//        poseWithCovarianceStamped.setPose(poseWithCovariance);
        edu.wpi.rail.jrosbridge.primitives.Time time =
                new edu.wpi.rail.jrosbridge.primitives.Time((int) Instant.now().getEpochSecond());
        seq++;
        Header header = new Header(seq, time,"map");
//        header.setFrameId("map"); // Mandatory field

//        Time time = new Time();
//        time.setSec((int) Instant.now().getEpochSecond());
//        header.setStamp(time);
        PoseWithCovarianceStamped poseWithCovarianceStamped = new PoseWithCovarianceStamped(header, poseWithCovariance);

//        poseWithCovarianceStamped.setHeader(header);

        return poseWithCovarianceStamped;
    }

    public static PoseStamped generateScaledNavigationMessageByPoint(@Nonnull Point point) {
        Pose pose = generatePoseMessageByPoint(point, new Quaternion(0,0,0,1));
        edu.wpi.rail.jrosbridge.primitives.Time time =
                new edu.wpi.rail.jrosbridge.primitives.Time((int) Instant.now().getEpochSecond());
        seq++;
        Header header = new Header(seq, time, "map");
        PoseStamped poseStamped = new PoseStamped(header, pose);
//        poseStamped.setPose(pose);

        return poseStamped;
    }

    private static Pose generatePoseMessageByPoint(@Nonnull Point point, Quaternion quaternion) {
        Triple triple = point.getPosition();
        double[] xyzUnscaled = UnitConverterLib.convertTripleToCoordinatesInMeter(triple);
        double[] xyzScaled = ScaleCorrector.getInstance().scaleCoordinatesForVehicle(xyzUnscaled);

        edu.wpi.rail.jrosbridge.messages.geometry.Point position =
                new edu.wpi.rail.jrosbridge.messages.geometry.Point(xyzScaled[0],xyzScaled[1],xyzScaled[2]);
//        position.setX(xyzScaled[0]);
//        position.setY(xyzScaled[1]);
//        position.setZ(xyzScaled[2]);

        edu.wpi.rail.jrosbridge.messages.geometry.Pose pose =
                new edu.wpi.rail.jrosbridge.messages.geometry.Pose(position, quaternion);
//        pose.setPosition(position);

        return pose;
    }
}
