/* Copyright (c) 2017 FIRST. All rights reserved.
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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;


import org.firstinspires.ftc.robotcore.external.Func;

import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * This OpMode illustrates various ways in which telemetry can be
 * transmitted from the robot controller to the driver station. The sample illustrates
 * numeric and text data, formatted output, and optimized evaluation of expensive-to-acquire
 * information. The telemetry log is illustrated by scrolling a poem
 * to the driver station.
 *
 * Also see the Telemetry javadocs.
 */
@TeleOp(name = "Concept: Diagnostics", group = "Concept")
//@Disabled
public class Diagnostics extends LinearOpMode  {

//    GoBildaPinpointDriver pinpoint;

    // declare the encoder interface
    private DcMotor m0;
    private DcMotor m1;
    private DcMotor m2;
    private DcMotor m3;
//
//    @Override
//    public void init() {
//        // Get a reference to the sensor
//        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
//
//        // Configure the sensor
//        configurePinpoint();
//
//        // Set the location of the robot - this should be the place you are starting the robot from
//        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
//    }

    @Override public void runOpMode() {


        /*
         * As an illustration, the first line on our telemetry display will display the battery voltage.
         * The idea here is that it's expensive to compute the voltage (at least for purposes of illustration)
         * so you don't want to do it unless the data is _actually_ going to make it to the
         * driver station (recall that telemetry transmission is throttled to reduce bandwidth use.
         * Note that getBatteryVoltage() below returns 'Infinity' if there's no voltage sensor attached.
         *
         * @see Telemetry#getMsTransmissionInterval()
         */
        telemetry.addData("voltage", "%.1f volts", new Func<Double>() {
            @Override public Double value() {
                return getBatteryVoltage();
            }
            });


        // Initialize encoder using exact name from HW configuration
        m0 = hardwareMap.get(DcMotor.class, "m0");
        m1 = hardwareMap.get(DcMotor.class, "m1");
        m2 = hardwareMap.get(DcMotor.class, "m2");
        m3 = hardwareMap.get(DcMotor.class, "m3");

        // Reset the encoder tick count to zero at startup
        m0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized & ready");
        telemetry.update();


        int loopCount = 1;

        waitForStart();

        // Go go gadget robot!
        while (opModeIsActive()) {


            // As an illustration, show some loop timing information
            telemetry.addData("loop count", loopCount);
            //telemetry.addData("ms/loop", "%.3f ms", opmodeRunTime.milliseconds() / loopCount);

            // Show joystick information as some other illustrative data
            telemetry.addLine("left joystick | ")
                    .addData("x", gamepad1.left_stick_x)
                    .addData("y", gamepad1.left_stick_y);
            telemetry.addLine("right joystick | ")
                    .addData("x", gamepad1.right_stick_x)
                    .addData("y", gamepad1.right_stick_y);

            /*
             * Transmit the telemetry to the driver station, subject to throttling.
             * See the documentation for Telemetry.getMsTransmissionInterval() for more information.
             */
            //telemetry.update();

            // Read encoder ticks
            int currentTicks_m0 = m0.getCurrentPosition();
            int currentTicks_m1 = m1.getCurrentPosition();
            int currentTicks_m2 = m2.getCurrentPosition();
            int currentTicks_m3 = m3.getCurrentPosition();

            // send tick metrics to Driver Station
            telemetry.addData("m0 Encoder Ticks", "%d", currentTicks_m0);
            telemetry.addData("m1 Encoder Ticks", "%d", currentTicks_m1);
            telemetry.addData("m2 Encoder Ticks", "%d", currentTicks_m2);
            telemetry.addData("m3 Encoder Ticks", "%d", currentTicks_m3);
            telemetry.update();

            // Update loop info
            loopCount++;
        }

    }

    // Computes the current battery voltage
    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }

//    /*
//     * This OpMode illustrates various ways in which telemetry can be
//     * transmitted from the robot controller to the driver station. The sample illustrates
//     * numeric and text data, formatted output, and optimized evaluation of expensive-to-acquire
//     * information. The telemetry log is illustrated by scrolling a poem
//     * to the driver station.
//     *
//     * Also see the Telemetry javadocs.
//     */
//    @TeleOp(name = "Concept: Diagnostics", group = "Concept")
//    @Disabled
//    public static class Diagnostics extends LinearOpMode  {
//        /** Keeps track of the line of the poem which is to be emitted next */
//        int poemLine = 0;
//
//        /** Keeps track of how long it's been since we last emitted a line of poetry */
//        ElapsedTime poemElapsed = new ElapsedTime();
//
//        static final String[] poem = new String[] {
//
//
//        };
//
//        @Override public void runOpMode() {
//
//            /* we keep track of how long it's been since the OpMode was started, just
//             * to have some interesting data to show */
//            ElapsedTime opmodeRunTime = new ElapsedTime();
//
//            // We show the log in oldest-to-newest order, as that's better for poetry
//            telemetry.log().setDisplayOrder(Telemetry.Log.DisplayOrder.OLDEST_FIRST);
//            // We can control the number of lines shown in the log
//            telemetry.log().setCapacity(6);
//            // The interval between lines of poetry, in seconds
//            double sPoemInterval = 0.6;
//
//            /*
//             * Wait until we've been given the ok to go. For something to do, we emit the
//             * elapsed time as we sit here and wait. If we didn't want to do anything while
//             * we waited, we would just call waitForStart().
//             */
//            while (!isStarted()) {
//                telemetry.addData("time", "%.1f seconds", opmodeRunTime.seconds());
//                telemetry.update();
//                idle();
//            }
//
//            // Ok, we've been given the ok to go
//
//            /*
//             * As an illustration, the first line on our telemetry display will display the battery voltage.
//             * The idea here is that it's expensive to compute the voltage (at least for purposes of illustration)
//             * so you don't want to do it unless the data is _actually_ going to make it to the
//             * driver station (recall that telemetry transmission is throttled to reduce bandwidth use.
//             * Note that getBatteryVoltage() below returns 'Infinity' if there's no voltage sensor attached.
//             *
//             * @see Telemetry#getMsTransmissionInterval()
//             */
//            telemetry.addData("voltage", "%.1f volts", new Func<Double>() {
//                @Override public Double value() {
//                    return getBatteryVoltage();
//                }
//            });
//
//            // Reset to keep some timing stats for the post-'start' part of the OpMode
//            opmodeRunTime.reset();
//            int loopCount = 1;
//
//            // Go go gadget robot!
//            while (opModeIsActive()) {
//
//                // Emit poetry if it's been a while
//                if (poemElapsed.seconds() > sPoemInterval) {
//                    emitPoemLine();
//                }
//
//                // As an illustration, show some loop timing information
//                telemetry.addData("loop count", loopCount);
//                telemetry.addData("ms/loop", "%.3f ms", opmodeRunTime.milliseconds() / loopCount);
//
//                // Show joystick information as some other illustrative data
//                telemetry.addLine("left joystick | ")
//                        .addData("x", gamepad1.left_stick_x)
//                        .addData("y", gamepad1.left_stick_y);
//                telemetry.addLine("right joystick | ")
//                        .addData("x", gamepad1.right_stick_x)
//                        .addData("y", gamepad1.right_stick_y);
//
//                /*
//                 * Transmit the telemetry to the driver station, subject to throttling.
//                 * See the documentation for Telemetry.getMsTransmissionInterval() for more information.
//                 */
//                telemetry.update();
//
//                // Update loop info
//                loopCount++;
//            }
//        }
//
//        // emits a line of poetry to the telemetry log
//        void emitPoemLine() {
//            telemetry.log().add(poem[poemLine]);
//            poemLine = (poemLine+1) % poem.length;
//            poemElapsed.reset();
//        }
//
//        // Computes the current battery voltage
//        double getBatteryVoltage() {
//            double result = Double.POSITIVE_INFINITY;
//            for (VoltageSensor sensor : hardwareMap.voltageSensor) {
//                double voltage = sensor.getVoltage();
//                if (voltage > 0) {
//                    result = Math.min(result, voltage);
//                }
//            }
//            return result;
//        }
//    }
}
