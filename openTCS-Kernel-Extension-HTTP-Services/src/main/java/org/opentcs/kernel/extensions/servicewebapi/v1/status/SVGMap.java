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
import org.opentcs.data.model.visualization.ModelLayoutElement;
import org.opentcs.data.model.visualization.VisualLayout;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SVGMap {
    private SVGGraphics2D graphics2D;
    private List<Point> pointList;
    private List<Path> pathList;
    private List<Location> locationList;
    private List<VisualLayout> visualLayoutList;
    private final RequestStatusHandler requestStatusHandler;

    public SVGMap(List<Point> pointList, List<Path> pathList, List<Location> locationList, List<VisualLayout> visualLayoutList, RequestStatusHandler requestStatusHandler) {
        this.pointList = pointList;
        this.pathList = pathList;
        this.locationList = locationList;
        this.visualLayoutList = visualLayoutList;
        this.requestStatusHandler = requestStatusHandler;
        graphics2D = new SVGGraphics2D(400 , 400);
    }

    public int getSize() {
        return 0;
    }

    public List<ModelLayoutElement> getLocationLayout() {
//        return
        List<ModelLayoutElement> elements = visualLayoutList.get(0).getLayoutElements().stream().map(v -> (ModelLayoutElement)v).collect(Collectors.toList());
        return elements.stream().filter(v -> v.getVisualizedObject().getReferentClass().equals(Location.class)).collect(Collectors.toList());
    }

    public int[] convertCoord(long x, long y) {
        return new int[]{(int)x/250, (int)-y/250 + 400};
    }

    public void drawPoint() {
        graphics2D.setStroke(new BasicStroke(0.5F));
        pointList.forEach(p -> {
//            graphics2D.setPaint();
            int x = (int)p.getPosition().getX()/250;
            int y = -(int)p.getPosition().getY()/250 + 400;
//            graphics2D.setPaint(new Paint);
            graphics2D.drawArc(x, y, 1, 1, 0, 360);
        });
        graphics2D.setStroke(new BasicStroke(0.1F));
        graphics2D.setFont(new Font("sans-serif", 0, 4));
//        graphics2D.setFontSizeUnits(SVGUnits);
        getLocationLayout().forEach(l -> {
            int x = Integer.parseInt(l.getProperties().get("POSITION_X")) + Integer.parseInt(l.getProperties().get("LABEL_OFFSET_X"));
            int y = Integer.parseInt(l.getProperties().get("POSITION_Y")) + Integer.parseInt(l.getProperties().get("LABEL_OFFSET_Y"));
            graphics2D.drawString(l.getVisualizedObject().getName(), x/250, -y/250+400);
        });
        requestStatusHandler.getAllVehicles().forEach(v -> {
            try {
                String name = v.getCurrentPosition().getName();
                Optional<Point> point = pointList.stream().filter(p -> p.getName().equals(name)).findFirst();
                if (point.isPresent()) {
                    int[] p = convertCoord(point.get().getPosition().getX(), point.get().getPosition().getY());
                    graphics2D.fillRect(p[0]-3, p[1]-3, 6, 6);
                }

            } catch (NullPointerException e) {}
        });
        graphics2D.setStroke(new BasicStroke(1F));
        graphics2D.setPaint(Color.ORANGE);
        requestStatusHandler.getVehiclesExecOrder().forEach(o -> {
            o.getRoute().getSteps().forEach(s -> {
                long sx = s.getSourcePoint().getPosition().getX();
                long sy = s.getSourcePoint().getPosition().getY();
                long dx = s.getDestinationPoint().getPosition().getX();
                long dy = s.getDestinationPoint().getPosition().getY();
                int[] sp = convertCoord(sx, sy);
                int[] dp = convertCoord(dx, dy);
                graphics2D.drawLine(sp[0], sp[1], dp[0], dp[1]);
            });
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
        graphics2D = new SVGGraphics2D(400 , 400);
        drawPoint();
        return graphics2D.getSVGDocument();
    }
}
