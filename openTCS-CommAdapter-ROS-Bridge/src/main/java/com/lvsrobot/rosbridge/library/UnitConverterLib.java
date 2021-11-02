package com.lvsrobot.rosbridge.library;

import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;
//import geometry_msgs.msg.Quaternion;
import org.opentcs.data.model.Triple;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Library class for converting distance and rotation units.
 *
 * @author Niels Tiben
 */
public abstract class UnitConverterLib {
    public static double convertMillimetersToMeters(long millimeters) {
        double millimetersDouble = (double) millimeters;

        return millimetersDouble / 1000.0;
    }

    public static double[] convertTripleToCoordinatesInMeter(@Nonnull Triple triple) {
        double x = convertMillimetersToMeters(triple.getX());
        double y = convertMillimetersToMeters(triple.getY());
        double z = convertMillimetersToMeters(triple.getZ());

        return new double[]{x, y, z};
    }

    public static Triple convertCoordinatesInMeterToTriple(double x, double y, double z) {
        long xInMillimeter = convertMetersToMillimeters(x);
        long yInMillimeter = convertMetersToMillimeters(y);
        long zInMillimeter = convertMetersToMillimeters(z);

        return new Triple(xInMillimeter, yInMillimeter, zInMillimeter);
    }

    private static long convertMetersToMillimeters(double meters) {
        double millimeters = meters * 1000.0;

        return Math.round(millimeters);
    }

    public static double convertAngleToRadian(double angle) {
        //先对角度取余数,让绝对值大于360的数值转换到360度内.因ros接受的角度为正负180度,所以还需要在再做转换;
        double x = angle%360;
        return Math.abs(x) > 180 ? (x-360)/180*Math.PI : x/180*Math.PI;
    }

    /**
     * 偏航角转四元数
     * @param yaw, 单位弧度
     * @return
     */
    public static Quaternion yawToQuaternion(double yaw) {
//        qx = np.sin(roll/2) * np.cos(pitch/2) * np.cos(yaw/2) - np.cos(roll/2) * np.sin(pitch/2) * np.sin(yaw/2)
//        qy = np.cos(roll/2) * np.sin(pitch/2) * np.cos(yaw/2) + np.sin(roll/2) * np.cos(pitch/2) * np.sin(yaw/2)
//        qz = np.cos(roll/2) * np.cos(pitch/2) * np.sin(yaw/2) - np.sin(roll/2) * np.sin(pitch/2) * np.cos(yaw/2)
//        qw = np.cos(roll/2) * np.cos(pitch/2) * np.cos(yaw/2) + np.sin(roll/2) * np.sin(pitch/2) * np.sin(yaw/2)
        //弧度为单位
        double z = Math.sin(yaw/2);
        double w = Math.cos(yaw/2);
        return new Quaternion(0,0,z,w);
    }

    public static double quaternionToAngleDegree(@Nonnull Quaternion quaternion) {
        // Convert quaternion to angle radians
        double angleRad = 2.0 * Math.acos(quaternion.getZ());

        // Inverse angle radian
        angleRad = angleRad * -1;

        // Convert angle radians to angle degrees
        double angleDegree = Math.toDegrees(angleRad);

        // Add 180° to comply with OpenTCS way of storing orientations (n=90°; e=0°; s=270°; w=180°)
        angleDegree = angleDegree + 180;

        // Normalize degree between 0° and 360°
        angleDegree = angleDegree % 360;
        if (angleDegree < 0) {
            angleDegree += 360;
        }

        return angleDegree;
    }
}
