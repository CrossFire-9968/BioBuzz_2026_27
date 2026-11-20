
package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class OpModeStorage {

    private static final PoseFactory poseFactory =
            PoseFactory.degrees();

    // Primary launching position
    public static Pose LaunchingPose =
            poseFactory.of(34, 8.5, 43.2);

    // Secondary launching position
    public static Pose LaunchingPoseSecondary =
            poseFactory.of(37.5, 124,313);

    // Where autonomous ended
    public static Pose Park =
            poseFactory.of(16, 92.926, 0);
}
