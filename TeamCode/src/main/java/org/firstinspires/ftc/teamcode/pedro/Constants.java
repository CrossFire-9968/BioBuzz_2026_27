package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(
            c -> {
                c.frontLeftName.set("Motor_LF");
                c.frontRightName.set("Motor_RF");
                c.backLeftName.set("Motor_LR");
                c.backRightName.set("Motor_RR");
                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);

            }
    );

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-3.151744331900529);
        c.yPodOffset.set(6.956327543483944);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.2855105680793361);
                Controller secondaryTranslationalForward = Controller.proportional(0.10548852592243065);
                Controller primaryTranslationalLateral = Controller.proportional(0.35515344593852166);
                Controller secondaryTranslationalLateral = Controller.proportional(0.13121970839943073);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.013839286978962804));
                c.brake.set(Controller.proportionalFeedforward(0.011763393932118383));

                c.headingFeedback.set(Controller.proportional(5.311206826965353));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05082676139334226, 0.006122291995677586));

                c.linearBrakeCoefficients.set(Matrix.diag(0.07669275079281551, 0.04577340773613116));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0017414352022802491, 0.0020474384498538225));

                c.maxAchievableForwardVelocity.set(54.83547651700087);
                c.maxAchievableStrafeVelocity.set(48.33284579944602);
                c.naturalForwardDeceleration.set(40.2485190237706);
                c.naturalStrafeDeceleration.set(64.30078612036516);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}