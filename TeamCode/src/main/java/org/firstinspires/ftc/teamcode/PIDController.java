/* Copyright (c) 2025 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.RobotHiveDistanceCalculation.RobotToHiveDistance;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/*
 * This OpMode illustrates how to program your robot to drive field relative.  This means
 * that the robot drives the direction you push the joystick regardless of the current orientation
 * of the robot.
 *
 * This OpMode assumes that you have four mecanum wheels each on its own motor named:
 *   front_left_motor, front_right_motor, back_left_motor, back_right_motor
 *
 *   and that the left motors are flipped such that when they turn clockwise the wheel moves backwards
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 *
 */
@TeleOp(name = "PIDController", group = "Robot")
//@Disabled
public class PIDController extends OpMode {

//    RobotHiveDistanceCalculation robotHiveDistanceCalculation = new RobotHiveDistanceCalculation();

    DcMotorEx Launcher1;
    DcMotorEx Launcher2;

    public static final double NEW_P = 1;
    public static final double NEW_I = 1;
    public static final double NEW_D = 0;
    public static final double NEW_F = 0;

    //Nectar target speed. Adjust for pollen.
    double TargetSpeed_DegPerSec = 165.0;

    private static Follower follower;
    static Pose robotPose = follower.pose();

    public void init(HardwareMap hwMap) {
        Launcher1 = hardwareMap.get(DcMotorEx.class, "Launcher1");
        Launcher2 = hardwareMap.get(DcMotorEx.class, "Launcher2");

        Launcher1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        Launcher2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        //Get coefficients for the RUN_USING_ENCODER RunMode.
        //PIDFCoefficients pidfOrig = Launcher1.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);

        //Change coefficients using methods included with DcMotorEx class.
        PIDFCoefficients pidfNew = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        Launcher1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfNew);

        //telemetry.setAutoClear(false);



    }
    public void PIDOuttake() {



        //Change target speed during test runs
//        if (gamepad1.dpadLeftWasPressed()) {
//            TargetSpeed_DegPerSec -= 5;
//        } else if (gamepad1.dpadRightWasPressed()) {
//            TargetSpeed_DegPerSec += 5;
//        } else if (gamepad1.dpadDownWasPressed()) {
//            TargetSpeed_DegPerSec = 0;
//        }


        //Driving speed control motor
        Launcher2.setVelocity(TargetSpeed_DegPerSec, AngleUnit.DEGREES);
        Launcher2.setDirection(DcMotorEx.Direction.REVERSE);
        //Launcher2.setPower(-0.6);


        //Get coefficients for the RUN_USING_ENCODER RunMode.
        PIDFCoefficients pidfOrig = Launcher1.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);

        //Display information to user.
        //telemetry.addData("Runtime (sec)", "%.01f", getRuntime());
        //telemetry.addData("P,I,D,F (orig)", "%.04f, %.04f, %.04f, %.04f",
               // pidfOrig.p, pidfOrig.i, pidfOrig.d, pidfOrig.f);
        //telemetry.update();

        //telemetry.addData("Motor Speed Setpoint (DegPerSec):", "%.01f", TargetSpeed_DegPerSec);
        //telemetry.addData("Shooter1 velocity (DegPerSec):","%.01f", Launcher1.getVelocity(AngleUnit.DEGREES));
        telemetry.addData("Shooter2 velocity (DegPerSec):","%.01f", Launcher2.getVelocity(AngleUnit.DEGREES));
        telemetry.addData("Distance To Hive (Inches):", "%.01f", RobotToHiveDistance(robotPose.x(), robotPose.y()));

        telemetry.update();
    }
  }