package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

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
@TeleOp(name = "TeleoPedro", group = "Robot")
//@Disabled

//public class Manual extends OpMode {
//    public Mecanum mecanum = new Mecanum();
//    public Yeeter yeeter = new Yeeter();
//    public AprilTag_9968 aTag = new AprilTag_9968();
//    public PrisimColor led = new PrisimColor();
//    public ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
//}



        // This tells us whether the robot is currently returning to Auto start


public class TeleoPedro extends OpMode {
    private Follower follower;
    private boolean returningToLaunch = false;
    private boolean returningToLaunch2 = false;
    boolean beebot = false;
    public ServoArm servoArm = new ServoArm();
    public Intake intake = new Intake();

    //public PIDController pidcontroller = new PIDController();
    public Launcher launcher = new Launcher(1,0.5,0.5);

    double Myspeed = 0;

    // This declares the five motors needed
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;


    // This declares the IMU needed to get the current direction the robot is facing
    IMU imu;

    @Override
    public void init() {
        //servoArm.init(hardwareMap);
        follower = Constants.create(hardwareMap);


        // Start TeleOp localization from where Auto ended
        follower.setPose(OpModeStorage.Park);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "Motor_LF");
        frontRightDrive = hardwareMap.get(DcMotor.class, "Motor_RF");
        backLeftDrive = hardwareMap.get(DcMotor.class, "Motor_LR");
        backRightDrive = hardwareMap.get(DcMotor.class, "Motor_RR");

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        // Compitition and Beebot
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection;

        if (beebot == false) {
            // Production Robot
            logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
            usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        }
        else {
            // Outreach Robot
            logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
            usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        }

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");


        // If you press the up dpad, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.dpad_up) {
            imu.resetYaw();
        }
        // If you press x, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
       // if (gamepad1.square) {
        //    drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        //} else {
          //  driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        //}
        // If you press y, servo extends and then retracts on release
        //if (gamepad1.y) {
        //    servoArm.deployedPosition();
        //} else {
        //    servoArm.toHome();
        //}
        // If you press right bumper the intake turns on


        if (gamepad2.triangle) {
            intake.intake();
        }
        else if (gamepad2.square) {
            intake.outake();
        }
        else {
//            intake.intakeSpeed(0);
            intake.stoptake();

        }
        if (beebot == true) {
            if (gamepad1.right_bumper) {
                intake.intake();
            }
            if (gamepad1.left_bumper) {
                intake.outake();
            }
        }

        if (gamepad2.left_bumper) {
            launcher.SpoolUpAndLaunch(150);
            //telemetry.addData("Shooter2 velocity (DegPerSec):","%.01f", Myspeed);
            if (gamepad2.right_bumper) {
                launcher.LaunchPollen();
            }
            launcher.SpoolUpAndLaunch(160.8);
            //telemetry.addData("Shooter2 velocity (DegPerSec):","%.01f", Myspeed);

        }
        else {
            launcher.LaunchOff();
        }
        if (gamepad1.triangleWasPressed() && !returningToLaunch) {

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

                returningToLaunch = true;

            } else {
                telemetry.addLine("Already at Auto Launch!");
            }
        }
        if (gamepad1.xWasPressed() && !returningToLaunch2) {

            Pose currentPose = follower.pose();
            Pose autoStartPose = OpModeStorage.LaunchingPoseSecondary;

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

                returningToLaunch2 = true;

            } else {
                telemetry.addLine("Already at Auto Launch!");
            }
        }
        // Update localization
        follower.update();

        // Execute scheduled Pedro commands
        Scheduler.execute();
        if (returningToLaunch) {
            Pose currentPose = follower.pose();
            Pose targetPose = OpModeStorage.LaunchingPose;

            double dx = targetPose.x() - currentPose.x();
            double dy = targetPose.y() - currentPose.y();

            double distance = Math.hypot(dx, dy);

            if (distance < 1.5) {
                returningToLaunch = false;
            }
            if (gamepad1.squareWasPressed() && returningToLaunch){
                returningToLaunch=false;
            }
            if (gamepad1.squareWasPressed() && returningToLaunch2){
                returningToLaunch2=false;
            }
        }
        // Only allow manual driving when NOT returning
        if (!returningToLaunch) {

            DrivePowers powers = ManualDrive.fieldCentric(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    follower.pose().heading()
            );

            follower.manual(powers);
        }
        if (!returningToLaunch2) {

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
                "Auto Launch X",
                OpModeStorage.LaunchingPose.x()
        );

        telemetry.addData(
                "Auto Launch Y",
                OpModeStorage.LaunchingPose.y()
        );
        telemetry.addData(
                "Auto Launchs X",
                OpModeStorage.LaunchingPoseSecondary.x()
        );

        telemetry.addData(
                "Auto Launch2 Y",
                OpModeStorage.LaunchingPoseSecondary.y()
        );

        telemetry.addData("Returning To Launch", returningToLaunch);
        telemetry.addData("Returning To Launch2", returningToLaunch2);

        telemetry.update();


    }



    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // Thanks to FTC16072 for sharing this code!!
    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }
}
