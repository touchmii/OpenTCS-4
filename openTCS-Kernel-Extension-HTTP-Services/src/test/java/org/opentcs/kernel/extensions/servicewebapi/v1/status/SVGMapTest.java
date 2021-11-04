/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.status;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentcs.access.to.model.PlantModelCreationTO;
import org.opentcs.access.to.model.PointCreationTO;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.visualization.VisualLayout;
import org.opentcs.util.persistence.ModelParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

class SVGMapTest {
    private static final Logger LOG = LoggerFactory.getLogger(SVGMapTest.class);
    private static ModelParser modelParser;
    private static PlantModelCreationTO plantModelCreationTO;
    private static File file;
    private static List<Point> pointList = new ArrayList<>();
    private static List<Path> pathList = new ArrayList<>();
    private static List<Location> locationList = new ArrayList<>();
    private static List<VisualLayout> visualLayoutList = new ArrayList<>();
    private static SVGMap svgMap;
    @BeforeAll
    public static void init() throws IOException {
        file = new File("/Users/touchmii", "aodon_g1.xml");
        modelParser = new ModelParser();
        plantModelCreationTO = modelParser.readModel(file);
        List<PointCreationTO> pointCreationTOList = plantModelCreationTO.getPoints();
        pointCreationTOList.forEach(p -> {
            pointList.add(createPoint(p));
        });
        svgMap = new SVGMap(pointList, pathList, locationList, visualLayoutList, mock(RequestStatusHandler.class));
    }

    public static Point createPoint(PointCreationTO to)
            throws ObjectExistsException {
        // Get a unique ID for the new point and create an instance.
        Point newPoint = new Point(to.getName())
                .withPosition(to.getPosition())
                .withType(to.getType())
                .withVehicleOrientationAngle(to.getVehicleOrientationAngle())
                .withProperties(to.getProperties());
//        objectPool.addObject(newPoint);
//        objectPool.emitObjectEvent(newPoint.clone(), null, TCSObjectEvent.Type.OBJECT_CREATED);
        // Return the newly created point.
        return newPoint;
    }


    @Test
    public void Test() throws IOException, TranscoderException {
        LOG.info(plantModelCreationTO.getName());
        svgMap.drawPoint();
        File file = new File("/Users/touchmii", "aodon_g1.svg");
        svgMap.writeSVG(file);
        String svg = svgMap.getSVG();
//        LOG.info("SVG: {}", svg);
        InputStream stream = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));
        TranscoderInput input_svg_image = new TranscoderInput(stream);
        OutputStream png_ostream = new FileOutputStream("aodon_g1.png");
        TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.transcode(input_svg_image, output_png_image);
        png_ostream.flush();
        png_ostream.close();

    }

}