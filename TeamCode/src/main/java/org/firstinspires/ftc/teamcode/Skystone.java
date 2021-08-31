package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.firstinspires.ftc.teamcode.pipelines.*;

/**
 * This program is used as an example of the Skystone detection algorythm.
 * It will turn the robot twoards the skystone at any given time
 *
 * @author Owen Rummage
 * @version 1.1
 */

@Autonomous
public class Skystone extends LinearOpMode {

    private DcMotor right;
    private DcMotor left;

    OpenCvCamera webcam;
    int width = 640;
    int height = 480;

    SkystonePipeline pipeline = new SkystonePipeline(width);


    /**
     * Logs a given gamepads values to the telemetry object that is passed,
     * this allows for us to view live data about the controller from the
     * graphing engine on the FTCDashboard application.
     *
     * @param telemetry The telemetry instance for logging, called from {@link FtcDashboard}
     * @param gamepad   The gamepad that is being logged
     * @param prefix    The Prefix of the log message, for example "gamepad1"
     */
    private static void logGamepad(Telemetry telemetry, Gamepad gamepad, String prefix) {
        telemetry.addData(prefix + "Synthetic",
                gamepad.getGamepadId() == Gamepad.ID_UNASSOCIATED);
        for (Field field : gamepad.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            try {
                telemetry.addData(prefix + field.getName(), field.get(gamepad));
            } catch (IllegalAccessException e) {
                // ignore for now
            }
        }
    }

    /**
     * The main Op-Mode code, what runs 60 or so times per second
     *
     * @throws InterruptedException If something wrong happens, then throw an error, mainly with the webcam initialization!
     */
    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        right = hardwareMap.dcMotor.get("right");
        left = hardwareMap.dcMotor.get("left");


        right.setDirection(DcMotorSimple.Direction.REVERSE);




        if (isStopRequested()) return;
        msStuckDetectStop = 2500;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId",
                "id",
                hardwareMap.appContext.getPackageName()
        );

        webcam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(
                        WebcamName.class,
                        "Webcam 1"
                ), cameraMonitorViewId
        );

        /*
         * This code would enable the masking for the Skystones, with it it is very hard
         * to see, so we want to use LiveViewPipeline for now, because it gives a better view with
         * no included mask for a color
         */
        webcam.setPipeline(pipeline);


        // This is what happens when the camera is opened as a device. (once its ready)
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                /*
                 * Tell the camera to start streaming images to us, use a supported resolution
                 * or an EXCEPTION will be Thrown.
                 */
                webcam.startStreaming(width, height, OpenCvCameraRotation.UPRIGHT);
            }
        });


        // Send the camera stream to FTCDashboard
        dashboard.startCameraStream(webcam, 0);

        // Wait for the start of the Op-Mode
        waitForStart();

        while (opModeIsActive()) {
            // Log the Gamepads
            logGamepad(telemetry, gamepad1, "gamepad1");
            logGamepad(telemetry, gamepad2, "gamepad2");

            //Log the Motors
            telemetry.addData("motor_left_speed", left.getPower());
            telemetry.addData("motor_right_speed", right.getPower());

            if(pipeline.getLocation() == SkystonePipeline.SkystoneLocation.RIGHT){
                right.setPower(50);
                left.setPower(-50);
            }

            if(pipeline.getLocation() == SkystonePipeline.SkystoneLocation.LEFT){
                right.setPower(-50);
                left.setPower(50);
            }

            if(pipeline.getLocation() == SkystonePipeline.SkystoneLocation.NONE){
                right.setPower(0);
                left.setPower(0);
            }

            // Update the telemetry screen to FTCDashboard
            telemetry.update();
        }
    }
}
