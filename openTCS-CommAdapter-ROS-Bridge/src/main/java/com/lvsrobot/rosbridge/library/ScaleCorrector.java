package com.lvsrobot.rosbridge.library;

//import lombok.Setter;
//import nl.saxion.nena.opentcs.commadapter.ros2.kernel.vehicle_adapter.Ros2CommAdapterConfiguration;
import org.opentcs.data.model.Triple;

import javax.annotation.Nonnull;

/**
 * Library Singleton class for converting measures to a given scale
 * that is provided in the {@link Ros2CommAdapterConfiguration}.
 * The purpose of this class is to give a better visual representation in the Plant Overview.
 * Without scaling, tiny or huge plants look very distorted in the Plant Overview.
 *
 * @author Niels Tiben
 */
public class ScaleCorrector {
    private static ScaleCorrector scaleCorrectorInstance;

//    @Setter
    private double scale = 1;

    /**
     * Singleton constructor
     *
     * @return an instance of the ScaleCorrector
     */
    public static ScaleCorrector getInstance() {
        if (scaleCorrectorInstance == null) {
            scaleCorrectorInstance = new ScaleCorrector();
        }
        return scaleCorrectorInstance;
    }

    /**
     * Scalar for OpenTCS to Vehicle
     *
     * @param coordinates An array holding a xyz-coordinate submitted by the fleet manager (openTCS)
     * @return An array holding a scaled xyz-coordinate meant for the vehicle.
     */
    public double[] scaleCoordinatesForVehicle(@Nonnull double[] coordinates) {
        assert coordinates.length == 3;

        double xScaled = scaleDoubleForVehicle(coordinates[0]);
        double yScaled = scaleDoubleForVehicle(coordinates[1]);
        double zScaled = scaleDoubleForVehicle(coordinates[2]);

        return new double[]{xScaled, yScaled, zScaled};
    }

    /**
     * Scalar for Vehicle to OpenTCS
     *
     * @param triple a Triple holding a xyz-coordinate submitted by the vehicle
     * @return An array holding a scaled xyz-coordinate meant for the fleet manager (openTCS)
     */
    public Triple scaleTripleForFleetManager(@Nonnull Triple triple) {
        long[] coordinates = new long[]{triple.getX(), triple.getY(), triple.getZ()};

        long xScaled = scaleLongForFleetManager(coordinates[0]);
        long yScaled = scaleLongForFleetManager(coordinates[1]);
        long zScaled = scaleLongForFleetManager(coordinates[2]);

        return new Triple(xScaled, yScaled, zScaled);
    }

    /**
     * Scalar for OpenTCS => Vehicle
     *
     * @param fromFleetManager The value submitted by the fleet manager (openTCS)
     * @return A scaled value meant for the vehicle
     */
    private double scaleDoubleForVehicle(double fromFleetManager) {
        assert scale != 0;
        return fromFleetManager * this.scale;
    }

    /**
     * Scalar for Vehicle => OpenTCS
     *
     * @param fromVehicle The value submitted by the vehicle
     * @return A scaled value meant for the fleet manager (openTCS)
     */
    private long scaleLongForFleetManager(long fromVehicle) {
        assert scale != 0;
        double fromVehicleDouble = (double) fromVehicle;

        return Math.round(fromVehicleDouble / this.scale);
    }
}
