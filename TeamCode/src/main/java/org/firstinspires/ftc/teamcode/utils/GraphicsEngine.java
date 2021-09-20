package org.firstinspires.ftc.teamcode.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * The basic Graphics engine, for drawing things on a webcam stream using Imgproc
 *
 * @author Owen Rummage
 * @version 1.0
 */
public class GraphicsEngine {

    /**
     * Draws a crosshair of a specified color on the screen
     *
     * @param input The frame input
     * @param color The scalar color value
     */
    public void drawCrosshair(Mat input, Scalar color) {
        this.drawLine(
                input,
                new Point(
                        0,
                        input.rows()/2),
                new Point(
                        input.cols(),
                        input.rows()/2),
                color, 1);

        //Draw Crosshair Y line
        this.drawLine(
                input,
                new Point(
                        input.cols()/2,
                        0),
                new Point(
                        input.cols()/2,
                        input.rows()),
                color, 1);
    }

    /**
     * Draw a basic line on the screen
     *
     * @param input     The input frame
     * @param p1        Point #1
     * @param p2        Point #2
     * @param color     The color of the line
     * @param thickness The thickness of the line
     */
    public void drawLine(Mat input, Point p1, Point p2, Scalar color, int thickness){
        //Draw Crosshair Y line
        Imgproc.line(
                input,
                p1,
                p2,
                color, thickness);
    }
}
