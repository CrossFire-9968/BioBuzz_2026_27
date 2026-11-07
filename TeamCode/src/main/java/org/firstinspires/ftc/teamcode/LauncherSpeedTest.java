package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "LauncherSpeedTest")
public class LauncherSpeedTest extends OpMode {

    public LauncherTest launcher = new LauncherTest(1, 1, 2);

    public void init() {
        launcher.init(hardwareMap);
    }

    public void loop() {

        if (gamepad1.left_bumper) {
            launcher.SpoolUpAndLaunch();
        } else if (gamepad1.left_trigger_pressed) {
            launcher.LaunchOff();
        }

        if (gamepad1.right_bumper) {
            launcher.LaunchPollen();
        }

        if (gamepad1.dpadLeftWasPressed()) {
            launcher.FireSpeed -= 1;
        } else if (gamepad1.dpadRightWasPressed()) {
            launcher.FireSpeed += 1;
        }

        if (gamepad1.triangle) {
            if (gamepad1.dpadUpWasPressed()) {
                launcher.kp += 0.1;
            }
            if (gamepad1.dpadDownWasPressed()) {
                launcher.kp -= 0.1;
            }
        }

        if (gamepad1.square) {
            if (gamepad1.dpadUpWasPressed()) {
                launcher.ki += 0.1;
            }
            if (gamepad1.dpadDownWasPressed()) {
                launcher.ki -= 0.1;
            }
        }

        if (gamepad1.cross) {
            if (gamepad1.dpadUpWasPressed()) {
                launcher.kd += 0.1;
            }
            if (gamepad1.dpadDownWasPressed()) {
                launcher.kd -= 0.1;
            }
        }

        if (gamepad1.rightTriggerWasPressed()) {
            launcher.setPID();
        }


        telemetry.addData("Motor Speed Setpoint (DegPerSec):", "%.01f", launcher.FireSpeed);
        telemetry.addData("Shooter1 velocity (DegPerSec):","%.01f", launcher.Launcher1.getVelocity(AngleUnit.DEGREES));
        telemetry.addData("Shooter1 power (DegPerSec):","%.04f", launcher.Launcher1.getPower());
        telemetry.addData("kp","%.01f", launcher.kp);
        telemetry.addData("ki","%.01f", launcher.ki);
        telemetry.addData("kd","%.01f", launcher.kd);
    }

}
