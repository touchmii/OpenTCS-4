/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.status;

import org.jfree.svg.SVGGraphics2D;
import org.jfree.svg.SVGUtils;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SVGMap {
    private SVGGraphics2D graphics2D;
    private List<Point> pointList;
    private List<Path> pathList;
    private List<Location> locationList;

    public SVGMap(List<Point> pointList, List<Path> pathList, List<Location> locationList) {
        this.pointList = pointList;
        this.pathList = pathList;
        this.locationList = locationList;
        graphics2D = new SVGGraphics2D(200 , 200);
    }

    public int getSize() {
        return 0;
    }
    public void drawPoint() {
        pointList.forEach(p -> {
//            graphics2D.setPaint();
            int x = (int)p.getPosition().getX()/500;
            int y = (int)p.getPosition().getY()/500;
//            graphics2D.setPaint(new Paint);
            graphics2D.setStroke(new BasicStroke(0.5F));
            graphics2D.drawArc(x, y, 1, 1, 0, 360);
        });
    }

    public void writeSVG(File file) {
        try {
            SVGUtils.writeToSVG(file, graphics2D.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSVG() {
        return graphics2D.getSVGDocument();
    }
}
