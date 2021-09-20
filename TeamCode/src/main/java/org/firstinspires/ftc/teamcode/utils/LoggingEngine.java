package org.firstinspires.ftc.teamcode.utils;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.acmerobotics.dashboard.telemetry.*;
/**
 * The basic Logging Engine, for logging things to a MultiTelemetry object
 *
 * @author Owen Rummage
 * @version 1.0
 */
public class LoggingEngine {
    public Telemetry telemetryHandler;

    /**
     * This is the main instantiation of the class,
     * its the main instance that gets everything started.
     *
     * @param telemetry The telemetry instance, pulled from your favorite telemetry provider {@link MultipleTelemetry}
     */
    public LoggingEngine(Telemetry telemetry) {this.telemetryHandler = telemetry;}


    /**
     * Logs a given gamepads values to the telemetry object that is passed,
     * this allows for us to view live data about the controller from the
     * graphing engine on the FTCDashboard application.
     *
     * @param gamepad The gamepad to log
     * @param prefix  The prefix of the gamepad in the telemetry view
     */

    public void logGamepad(Gamepad gamepad, String prefix){
        telemetryHandler.addData(prefix + "Synthetic",
                gamepad.getGamepadId() == Gamepad.ID_UNASSOCIATED);
        for (Field field : gamepad.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            try {
                telemetryHandler.addData(prefix + field.getName(), field.get(gamepad));
            } catch (IllegalAccessException e) {
                // ignore for now
            }
        }
    }

    /**
     * This will log out the Power, Position, and Mode of any given motor.
     * THis is extremely useful in cases where you need to know the exact
     * position of the motor for debug reasons
     *
     * @param motor  The motor to be tracked
     * @param prefix The prefix of the motor in the telemetry view
     */
    public void logMotor(DcMotor motor, String prefix){
        telemetryHandler.addData(prefix + "_POWER", motor.getPower());
        telemetryHandler.addData(prefix + "_POSITION", motor.getCurrentPosition());
        telemetryHandler.addData(prefix + "_MODE", motor.getMode());
    }

    /**
     * Update the Telemetry
     */
    public void update(){
        // Update Telemetry Handler
        telemetryHandler.update();
    }
}
