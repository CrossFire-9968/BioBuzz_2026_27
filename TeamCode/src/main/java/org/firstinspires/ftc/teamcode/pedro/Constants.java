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

    public static PinpointConfig localizerConfig = new PinpointConfig(
            c -> {
                c.name.set("pinpoint");
                c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
                c.xPodOffset.set(0.8113278366449311);
                c.yPodOffset.set(-5.723549549973856);
                c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.globalDistanceUnit.set(DistanceUnit.INCH);
                c.offsetUnits.set(DistanceUnit.INCH);
            }
    );

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.15813841241045526);
                Controller secondaryTranslationalForward = Controller.proportional(0.05842791784946079);
                Controller primaryTranslationalLateral = Controller.proportional(0.22123476247498888);
                Controller secondaryTranslationalLateral = Controller.proportional(0.08174033323278138);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01921006332653327));
                c.brake.set(Controller.proportionalFeedforward(0.01632855382755328));

                c.headingFeedback.set(Controller.proportional(2.8706753437844377));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.039804060034137924, 0.006550084829090279));

                c.linearBrakeCoefficients.set(Matrix.diag(0.02837783196593669, 0.015635781162112224));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.002613338360318456, 0.0026232437336171214));

                c.maxAchievableForwardVelocity.set(56.13187715938316);
                c.maxAchievableStrafeVelocity.set(47.9146832014075);
                c.naturalForwardDeceleration.set(45.54854541740019);
                c.naturalStrafeDeceleration.set(73.85625377084412);
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