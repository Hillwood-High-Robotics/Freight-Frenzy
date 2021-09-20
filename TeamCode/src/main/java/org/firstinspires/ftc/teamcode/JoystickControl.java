package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.utils.LoggingEngine;
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
 * This program has basic driving control of the robot, it also includes a very basic instance of
 * FtcDashboard, this is used primarily for viewing the camera remotely so we can see what the
 * robot is doing remotely. This will be particularly useful in the case that we are not directly
 * in front of the robot. It also has logging metrics of all of the connected controllers and motors
 * for more debug information
 *
 * @author Owen Rummage
 * @version 1.1
 */

@Autonomous
public class JoystickControl extends LinearOpMode {

    private DcMotor right;
    private DcMotor left;
    private DcMotor flag;

    private Servo camera;

    private LoggingEngine log;

    OpenCvCamera webcam;

    int width = 640;
    int height = 480;



    /**
     * The main Op-Mode code, what runs 60 or so times per second
     *
     * @throws InterruptedException If something wrong happens, then throw an error, mainly with the webcam initialization!
     */
    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        log = new LoggingEngine(telemetry);

        right = hardwareMap.dcMotor.get("right");
        left = hardwareMap.dcMotor.get("left");
        flag = hardwareMap.dcMotor.get("flag");

        camera = hardwareMap.servo.get("camera");


        right.setDirection(DcMotorSimple.Direction.REVERSE);

        int position = 0;



        if (isStopRequested()) return;
//        msStuckDetectStop = 2500;
//
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
//                "cameraMonitorViewId",
//                "id",
//                hardwareMap.appContext.getPackageName()
//        );
//
//        webcam = OpenCvCameraFactory.getInstance().createWebcam(
//                hardwareMap.get(
//                        WebcamName.class,
//                        "Webcam 1"
//                ), cameraMonitorViewId
//        );
//
//        /*
//         * This code would enable the masking for the Skystones, with it it is very hard
//         * to see, so we want to use LiveViewPipeline for now, because it gives a better view with
//         * no included mask for a color
//         */
//        webcam.setPipeline(new LiveViewPipeline());
//
//
//        // This is what happens when the camera is opened as a device. (once its ready)
//        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//        {
//            @Override
//            public void onOpened()
//            {
//                /*
//                 * Tell the camera to start streaming images to us, use a supported resolution
//                 * or an EXCEPTION will be Thrown.
//                 */
//                webcam.startStreaming(width, height, OpenCvCameraRotation.UPRIGHT);
//            }
//        });
//
//
//        // Send the camera stream to FTCDashboard
//        dashboard.startCameraStream(webcam, 0);

        right.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait for the start of the Op-Mode
        waitForStart();

        while (opModeIsActive()) {
            // Log the Gamepads
            log.logGamepad(gamepad1, "gamepad1");
            log.logGamepad(gamepad2, "gamepad2");

            // Log the Motors
            log.logMotor(left, "left");
            log.logMotor(right, "right");


            if(gamepad2.dpad_left){
                camera.setPosition(0.1);
            }
            if(gamepad2.dpad_up){
                camera.setPosition(0.25);
            }
            if(gamepad2.dpad_right){
                camera.setPosition(0.4);
            }
            if(gamepad2.dpad_down){
                camera.setPosition(0.9);
            }

            flag.setPower(gamepad2.left_trigger);


            if(gamepad2.left_bumper){
                // Set the motor power to joystick values, so they move
                left.setPower(gamepad2.left_stick_y*1.5);
                right.setPower(gamepad2.right_stick_y);
            }else{
                // Set the motor power to joystick values, so they move
                left.setPower(gamepad2.left_stick_y/2.9);
                right.setPower(gamepad2.right_stick_y/3);
            }

            // Update the telemetry screen to FTCDashboard
            log.update();
        }
    }
}
