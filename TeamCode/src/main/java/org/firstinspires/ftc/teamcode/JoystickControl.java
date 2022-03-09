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


    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;
    private DcMotor arm_lift;
    private DcMotor spinner_motor;
    private Servo claw_close;


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
        //Get FTCDashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();

        //setup telemetry
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        log = new LoggingEngine(telemetry);

        //Initialize all the motors from hardwaremap
        front_left    = hardwareMap.get(DcMotor.class, "fl");
        front_right   = hardwareMap.get(DcMotor.class, "fr");
        back_left     = hardwareMap.get(DcMotor.class, "bl");
        back_right    = hardwareMap.get(DcMotor.class, "br");
        claw_close    = hardwareMap.get(Servo.class, "claw");
        arm_lift    = hardwareMap.get(DcMotor.class, "arm");


        //Set Directions for the Motors
        front_left.setDirection(DcMotorSimple.Direction.FORWARD);
        front_right.setDirection(DcMotorSimple.Direction.REVERSE);
        back_left.setDirection(DcMotorSimple.Direction.FORWARD);
        back_right.setDirection(DcMotorSimple.Direction.REVERSE);



        //Camera shit
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
        webcam.setPipeline(new LiveViewPipeline());


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
                webcam.startStreaming(width, height, OpenCvCameraRotation.SIDEWAYS_RIGHT);
            }
        });


        // Send the camera stream to FTCDashboard
        dashboard.startCameraStream(webcam, 0);


        // Wait for the start of the Op-Mode
        waitForStart();

        while (opModeIsActive()) {
            // Log the Gamepads
            log.logGamepad(gamepad1, "gamepad1");
            log.logGamepad(gamepad2, "gamepad2");


            //Joystick Controls
            front_left.setPower(gamepad1.left_stick_y);
            front_right.setPower(gamepad1.right_stick_y);
            back_left.setPower(gamepad1.left_stick_y);
            back_right.setPower(gamepad1.right_stick_y);

            //Drive Forward
            if(gamepad1.dpad_up){
                front_left.setPower(-10);
                front_right.setPower(-10);
                back_left.setPower(-10);
                back_right.setPower(-10);
            }

            //Drive Backwards
            if(gamepad1.dpad_down){
                front_left.setPower(10);
                front_right.setPower(10);
                back_left.setPower(10);
                back_right.setPower(10);
            }

            //Drive Left
            if(gamepad1.dpad_left){
                front_left.setPower(-10);
                front_right.setPower(10);
                back_left.setPower(10);
                back_right.setPower(-10);
            }

            //Drive Right
            if(gamepad1.dpad_right){
                front_left.setPower(10);
                front_right.setPower(-10);
                back_left.setPower(-10);
                back_right.setPower(10);
            }


            //Reset motors
            front_left.setPower(0);
            front_right.setPower(0);
            back_left.setPower(0);
            back_right.setPower(0);

            //Open Claw
            if(gamepad1.x){
                claw_close.setPosition(0.1);
            }

            //Close Claw
            if(gamepad1.y){
                claw_close.setPosition(0.8);
            }

            //Set triggers to arm lift
            arm_lift.setPower(gamepad1.left_trigger*5);
            arm_lift.setPower(-gamepad1.right_trigger*5);

            //Reset arm so it doesnt run forever
            arm_lift.setPower(0);
            // Update the telemetry screen to FTCDashboard
            log.update();
        }
    }
}
