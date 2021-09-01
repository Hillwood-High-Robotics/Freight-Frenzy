package org.firstinspires.ftc.teamcode.pipelines;

import org.firstinspires.ftc.teamcode.utils.GraphicsEngine;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

/**
 * A pipeline that creates a basic crosshair on the screen, this is used to see what
 * the robot sees. This is useful in a situation where we are driving the robot
 * remotely and do not have direct line of sight. Such as when sending it from Engineering
 * to Culinary and back to retrieve food.
 *
 * @author Owen Rummage
 * @version 1.1
 */
public class LiveViewPipeline extends OpenCvPipeline
{

    GraphicsEngine engine = new GraphicsEngine();
    /**
     *
     * @param  input The frame to be processed
     * @return The returned, modified, camera frame to be displayed
     */
    @Override
    public Mat processFrame(Mat input)
    {
        //Draw Crosshair X line
        engine.drawCrosshair(
                input,
                new Scalar(255, 0, 0)
        );


        return input;
    }
}