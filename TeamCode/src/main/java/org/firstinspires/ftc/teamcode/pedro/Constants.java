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
                c.xPodOffset.set(4.1424416369340555);
                c.yPodOffset.set(-4.0589982881320745);
                c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.globalDistanceUnit.set(DistanceUnit.INCH);
                c.offsetUnits.set(DistanceUnit.INCH);
            }
    );

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.18467611708985632);
                Controller secondaryTranslationalForward = Controller.proportional(0.06823289062797076);
                Controller primaryTranslationalLateral = Controller.proportional(0.307463474545951);
                Controller secondaryTranslationalLateral = Controller.proportional(0.11359953826938061);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.016347139333801476));
                c.brake.set(Controller.proportionalFeedforward(0.013895068433731254));

                c.headingFeedback.set(Controller.proportional(3.585678773709204));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.039199284791560325, 0.007653430516528906));

                c.linearBrakeCoefficients.set(Matrix.diag(0.033724102108156524, 0.019168559336013045));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0026443019732566454, 0.002330926467410744));

                c.maxAchievableForwardVelocity.set(50.24675472057429);
                c.maxAchievableStrafeVelocity.set(40.82379420812944);
                c.naturalForwardDeceleration.set(54.388140225169);
                c.naturalStrafeDeceleration.set(76.21061933168828);
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