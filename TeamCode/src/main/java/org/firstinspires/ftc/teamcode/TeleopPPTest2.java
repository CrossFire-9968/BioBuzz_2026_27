package org.firstinspires.ftc.teamcode;


import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;


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
        follower.setPose(OpModeStorage.autonomousEndPose);
    }


    @Override
    public void loop() {


        /*
         * ==========================================
         * RETURN TO AUTO START BUTTON
         * ==========================================
         */


        if (gamepad1.triangleWasPressed() && !returningToStart) {


            // Get where the robot is RIGHT NOW
            Pose currentPose = follower.pose();


            // Get where Auto originally started
            Pose autoStartPose = OpModeStorage.autonomousStartPose;


            // Create a path from current position to Auto start
            Path returnPath = line(
                    currentPose,
                    autoStartPose
            ).linear(
                    currentPose,
                    autoStartPose
            );


            // Tell Pedro to follow that path
            follower.follow(returnPath);


            // Tell our code that the robot is returning
            returningToStart = true;
        }




        /*
         * ==========================================
         * NORMAL TELEOP DRIVING
         * ==========================================
         */


        if (!returningToStart) {


            DrivePowers powers = ManualDrive.fieldCentric(
                    -gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    gamepad1.right_stick_x,
                    follower.pose().heading()
            );


            follower.manual(powers);
        }




        /*
         * ==========================================
         * CHECK IF RETURN PATH IS FINISHED
         * ==========================================
         */


        if (returningToStart && !follower.isBusy()) {


            returningToStart = false;
        }




        /*
         * ==========================================
         * UPDATE PEDRO
         * ==========================================
         */


        follower.update();




        /*
         * ==========================================
         * TELEMETRY
         * ==========================================
         */


        Pose robotPose = follower.pose();


        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());
        telemetry.addData(
                "Robot Heading",
                Math.toDegrees(robotPose.heading())
        );


        telemetry.addData(
                "Auto Start X",
                OpModeStorage.autonomousStartPose.x()
        );


        telemetry.addData(
                "Auto Start Y",
                OpModeStorage.autonomousStartPose.y()
        );


        telemetry.addData(
                "Returning To Start",
                returningToStart
        );


        telemetry.update();
    }
}

