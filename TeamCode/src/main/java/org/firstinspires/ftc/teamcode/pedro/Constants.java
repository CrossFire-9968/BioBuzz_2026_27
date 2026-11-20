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
        c.xPodOffset.set(-0.040165035743413006);
        c.yPodOffset.set(6.260849359467274);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.2486738066697346);
                Controller secondaryTranslationalForward = Controller.proportional(0.09187832687797577);
                Controller primaryTranslationalLateral = Controller.proportional(0.4304180579644362);
                Controller secondaryTranslationalLateral = Controller.proportional(0.15902797143553377);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.011869115223923098));
                c.brake.set(Controller.proportionalFeedforward(0.010088747940334633));

                c.headingFeedback.set(Controller.proportional(4.537205501110056));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.051278600184235155, 0.006617788637694987));

                c.linearBrakeCoefficients.set(Matrix.diag(0.08329062427326046, 0.0652786076169374));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0014216668513295921, 0.0018425777622876));

                c.maxAchievableForwardVelocity.set(71.67746156019243);
                c.maxAchievableStrafeVelocity.set(60.86847699109269);
                c.naturalForwardDeceleration.set(31.831998831942677);
                c.naturalStrafeDeceleration.set(60.110754436180976);
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