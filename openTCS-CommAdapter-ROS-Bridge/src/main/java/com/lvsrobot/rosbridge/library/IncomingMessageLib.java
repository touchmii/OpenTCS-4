package com.lvsrobot.rosbridge.library;

import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseWithCovarianceStamped;
//import geometry_msgs.msg.Point;
//import geometry_msgs.msg.PoseWithCovarianceStamped;
import org.opentcs.data.model.Triple;

import javax.annotation.Nonnull;

/**
 * Library class for parsing incoming rcljava messages
 *
 * @author Niels Tiben
 */
public class IncomingMessageLib {
    public static Triple generateTripleByAmclPose(@Nonnull PoseWithCovarianceStamped amclPose){
        Point amclPosePoint = amclPose.getPose().getPose().getPosition();

        Triple estimatePositionUnscaled = UnitConverterLib.convertCoordinatesInMeterToTriple(
                amclPosePoint.getX(),
                amclPosePoint.getY(),
                amclPosePoint.getZ()
        );

        // Scale triple
        return ScaleCorrector.getInstance().scaleTripleForFleetManager(estimatePositionUnscaled);
    }
}
