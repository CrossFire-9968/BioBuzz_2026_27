
package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "TeleoPedro", group = "Robot")
public class TeleoPedro extends OpMode {

    private Follower follower;

    // Return path states
    private boolean returningToLaunch = false;
    private boolean returningToLaunch2 = false;

    boolean beebot = false;

    public ServoArm servoArm = new ServoArm();
    public Intake intake = new Intake();
    public Launcher launcher = new Launcher(0.7, 0.4, 0.4);

    // Drivetrain motors
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    // Internal IMU
    IMU imu;

    @Override
    public void init() {

        follower = Constants.create(hardwareMap);

        // Start TeleOp from the final autonomous position
        follower.setPose(OpModeStorage.Park);

        intake.init(hardwareMap);
        launcher.init(hardwareMap);

        frontLeftDrive = hardwareMap.get(
                DcMotor.class, "Motor_LF"
        );

        frontRightDrive = hardwareMap.get(
                DcMotor.class, "Motor_RF"
        );

        backLeftDrive = hardwareMap.get(
                DcMotor.class, "Motor_LR"
        );

        backRightDrive = hardwareMap.get(
                DcMotor.class, "Motor_RR"
        );

        // Original drivetrain direction settings
        backLeftDrive.setDirection(
                DcMotor.Direction.REVERSE
        );

        frontLeftDrive.setDirection(
                DcMotor.Direction.REVERSE
        );

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection;

        if (!beebot) {

            // Competition Robot
            logoDirection =
                    RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;

            usbDirection =
                    RevHubOrientationOnRobot.UsbFacingDirection.UP;

        } else {

            // Outreach Robot
            logoDirection =
                    RevHubOrientationOnRobot.LogoFacingDirection.UP;

            usbDirection =
                    RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        }

        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(
                        logoDirection,
                        usbDirection
                );

        imu.initialize(
                new IMU.Parameters(orientationOnRobot)
        );

        telemetry.addLine("TeleOp Initialized");
        telemetry.addData(
                "Saved Primary Launch",
                "(%.2f, %.2f)",
                OpModeStorage.LaunchingPose.x(),
                OpModeStorage.LaunchingPose.y()
        );

        telemetry.addData(
                "Saved Secondary Launch",
                "(%.2f, %.2f)",
                OpModeStorage.LaunchingPoseSecondary.x(),
                OpModeStorage.LaunchingPoseSecondary.y()
        );

        telemetry.update();
    }

    @Override
    public void loop() {

        // =====================================
        // INTAKE CONTROLS
        // =====================================

        if (gamepad2.triangle) {

            intake.intake();

        } else if (gamepad2.square) {

            intake.outake();

        } else {

            intake.stoptake();
        }

        // Extra controls for Beebot
        if (beebot) {

            if (gamepad1.right_bumper) {
                intake.intake();
            }

            if (gamepad1.left_bumper) {
                intake.outake();
            }
        }

        // =====================================
        // LAUNCHER CONTROLS
        // =====================================

        if (gamepad2.left_bumper) {

            launcher.SpoolUpAndLaunch(160.8);

            if (gamepad2.right_bumper) {
                launcher.LaunchPollen();
            }

        } else {

            launcher.LaunchOff();
        }

        // =====================================
        // IMU CONTROL
        // =====================================

        if (gamepad1.dpad_up) {
            imu.resetYaw();
        }

        // =====================================
        // AUTOMATIC RETURN CONTROLS
        // =====================================

        // SQUARE: Cancel automatic return
        if (gamepad1.squareWasPressed()) {

            follower.stop();

            returningToLaunch = false;
            returningToLaunch2 = false;

        }

        // TRIANGLE: Primary launch position
        else if (gamepad1.triangleWasPressed()) {

            startReturn(
                    OpModeStorage.LaunchingPose,
                    false
            );

        }

        // CROSS (X): Secondary launch position
        else if (gamepad1.crossWasPressed()) {

            startReturn(
                    OpModeStorage.LaunchingPoseSecondary,
                    true
            );
        }

        // =====================================
        // MANUAL DRIVE
        // =====================================

        // Never override an active automatic return.
        if (!returningToLaunch && !returningToLaunch2) {

            DrivePowers powers = ManualDrive.fieldCentric(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    follower.pose().heading()
            );

            follower.manual(powers);
        }

        // =====================================
        // FOLLOWER UPDATE
        // =====================================

        // Execute Pedro once per loop
        follower.update();

        // Determine if automatic return finished
        if (returningToLaunch || returningToLaunch2) {

            if (!follower.following()) {

                returningToLaunch = false;
                returningToLaunch2 = false;
            }
        }

        // =====================================
        // TELEMETRY
        // =====================================

        Pose robotPose = follower.pose();

        telemetry.addLine("===== TELEOP =====");

        telemetry.addLine("Triangle: Primary Launch");
        telemetry.addLine("Cross: Secondary Launch");
        telemetry.addLine("Square: Cancel Return");

        telemetry.addLine("");

        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());

        telemetry.addData(
                "Robot Heading",
                Math.toDegrees(robotPose.heading())
        );

        telemetry.addLine("");

        telemetry.addLine("===== SAVED POSES =====");

        telemetry.addData(
                "Primary Launch X",
                OpModeStorage.LaunchingPose.x()
        );

        telemetry.addData(
                "Primary Launch Y",
                OpModeStorage.LaunchingPose.y()
        );

        telemetry.addData(
                "Secondary Launch X",
                OpModeStorage.LaunchingPoseSecondary.x()
        );

        telemetry.addData(
                "Secondary Launch Y",
                OpModeStorage.LaunchingPoseSecondary.y()
        );

        telemetry.addLine("");

        telemetry.addLine("===== RETURN STATUS =====");

        telemetry.addData(
                "Returning Primary",
                returningToLaunch
        );

        telemetry.addData(
                "Returning Secondary",
                returningToLaunch2
        );

        telemetry.addData(
                "Follower Mode",
                follower.mode()
        );

        telemetry.addData(
                "Following Path",
                follower.following()
        );

        telemetry.update();
    }

    // =====================================
    // AUTOMATIC RETURN HELPER
    // =====================================

    private void startReturn(Pose target, boolean secondary) {

        if (target == null) {

            telemetry.addLine(
                    "ERROR: Launch position not saved!"
            );

            return;
        }

        Pose currentPose = follower.pose();

        double dx = target.x() - currentPose.x();
        double dy = target.y() - currentPose.y();

        double distance = Math.hypot(dx, dy);

        // If already at the requested position,
        // cancel any previous return.
        if (distance <= 0.5) {

            follower.stop();

            returningToLaunch = false;
            returningToLaunch2 = false;

            telemetry.addLine(
                    "Already at target position!"
            );

            return;
        }

        // Build a path from the CURRENT position
        // to the selected saved launch position.
        Path returnPath = line(
                currentPose,
                target
        ).linear(
                currentPose,
                target
        );

        // Start path directly through Pedro.
        // This replaces any previous follower path.
        follower.follow(returnPath);

        // Only one return mode can be active.
        returningToLaunch = !secondary;
        returningToLaunch2 = secondary;
    }

    // =====================================
    // LEGACY FIELD RELATIVE DRIVE HELPER
    // =====================================

    private void driveFieldRelative(
            double forward,
            double right,
            double rotate
    ) {

        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        theta = AngleUnit.normalizeRadians(
                theta -
                        imu.getRobotYawPitchRollAngles()
                                .getYaw(AngleUnit.RADIANS)
        );

        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        drive(
                newForward,
                newRight,
                rotate
        );
    }

    // =====================================
    // LEGACY DIRECT MOTOR DRIVE
    // =====================================

    public void drive(
            double forward,
            double right,
            double rotate
    ) {

        double frontLeftPower =
                forward + right + rotate;

        double frontRightPower =
                forward - right - rotate;

        double backRightPower =
                forward + right - rotate;

        double backLeftPower =
                forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;

        maxPower = Math.max(
                maxPower,
                Math.abs(frontLeftPower)
        );

        maxPower = Math.max(
                maxPower,
                Math.abs(frontRightPower)
        );

        maxPower = Math.max(
                maxPower,
                Math.abs(backRightPower)
        );

        maxPower = Math.max(
                maxPower,
                Math.abs(backLeftPower)
        );

        frontLeftDrive.setPower(
                maxSpeed * frontLeftPower / maxPower
        );

        frontRightDrive.setPower(
                maxSpeed * frontRightPower / maxPower
        );

        backLeftDrive.setPower(
                maxSpeed * backLeftPower / maxPower
        );

        backRightDrive.setPower(
                maxSpeed * backRightPower / maxPower
        );
    }

    @Override
    public void stop() {

        launcher.LaunchOff();
        intake.stoptake();

        follower.stop();
        follower.update();
    }
}
