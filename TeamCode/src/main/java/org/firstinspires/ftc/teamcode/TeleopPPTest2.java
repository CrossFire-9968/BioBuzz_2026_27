package org.firstinspires.ftc.teamcode;


import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.line;
import com.pedropathing.ivy.Scheduler;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.pedro.Constants;


import static com.pedropathing.api.Paths.line;


@TeleOp(name = "TeleopPPTest2")
public class TeleopPPTest2 extends OpMode {


    private Follower follower;


    // This tells us whether the robot is currently returning to Auto start
    private boolean returningToStart = false;


    @Override
    public void init() {


        follower = Constants.create(hardwareMap);


        // Start TeleOp localization from where Auto ended
        follower.setPose(OpModeStorage.Park);
    }


    @Override
    public void loop() {

        // Check whether Triangle was pressed
        if (gamepad1.triangleWasPressed() && !returningToStart) {

            Pose currentPose = follower.pose();
            Pose autoStartPose = OpModeStorage.LaunchingPose;

            // Calculate distance to the destination
            double dx = autoStartPose.x() - currentPose.x();
            double dy = autoStartPose.y() - currentPose.y();

            if (Math.hypot(dx, dy) > 0.5) {

                Path returnPath = line(
                        currentPose,
                        autoStartPose
                ).linear(
                        currentPose,
                        autoStartPose
                );

                // Schedule Pedro's path-following command
                schedule(follow(follower, returnPath));

                returningToStart = true;

            } else {
                telemetry.addLine("Already at Auto start!");
            }
        }

        // Update localization
        follower.update();

        // Execute scheduled Pedro commands
        Scheduler.execute();
        if (returningToStart) {
            Pose currentPose = follower.pose();
            Pose targetPose = OpModeStorage.LaunchingPose;

            double dx = targetPose.x() - currentPose.x();
            double dy = targetPose.y() - currentPose.y();

            double distance = Math.hypot(dx, dy);

            if (distance < .5) {
                returningToStart = false;
            }
        }
        // Only allow manual driving when NOT returning
        if (!returningToStart) {

            DrivePowers powers = ManualDrive.fieldCentric(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    follower.pose().heading()
            );

            follower.manual(powers);
        }

        // Telemetry
        Pose robotPose = follower.pose();

        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());

        telemetry.addData(
                "Robot Heading",
                Math.toDegrees(robotPose.heading())
        );

        telemetry.addData(
                "Auto Start X",
                OpModeStorage.LaunchingPose.x()
        );

        telemetry.addData(
                "Auto Start Y",
                OpModeStorage.LaunchingPose.y()
        );

        telemetry.addData("Returning To Start", returningToStart);

        telemetry.update();
    }
}