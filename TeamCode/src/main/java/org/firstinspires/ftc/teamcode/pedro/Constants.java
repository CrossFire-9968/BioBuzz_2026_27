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
                c.xPodOffset.set(2.9167304452010026);
                c.yPodOffset.set(-3.4083821454386074);
                c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.globalDistanceUnit.set(DistanceUnit.INCH);
                c.offsetUnits.set(DistanceUnit.INCH);
            }
    );

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.21203511908848013);
                Controller secondaryTranslationalForward = Controller.proportional(0.07834131082046498);
                Controller primaryTranslationalLateral = Controller.proportional(0.409844374564053);
                Controller secondaryTranslationalLateral = Controller.proportional(0.1514265451580373);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.014030324406942934));
                c.brake.set(Controller.proportionalFeedforward(0.011925775745901495));

                c.headingFeedback.set(Controller.proportional(30.58598999933561));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.041987945076859844, 0.06930626798515016));

                c.linearBrakeCoefficients.set(Matrix.diag(-0.018349361107202894, 2.3731563799091786));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0024362678817250174, -0.039729232062704324));

                c.maxAchievableForwardVelocity.set(51.33061911470598);
                c.maxAchievableStrafeVelocity.set(37.336544695530364);
                c.naturalForwardDeceleration.set(36.79007387704796);
                c.naturalStrafeDeceleration.set(60.446493525481685);
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