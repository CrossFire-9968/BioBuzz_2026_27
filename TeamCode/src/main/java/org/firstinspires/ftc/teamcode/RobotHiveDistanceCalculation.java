package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;




public class RobotHiveDistanceCalculation {



    private Follower follower;
    Pose robotPose = follower.pose();

    double RobotX = robotPose.x();
    double RobotY = robotPose.y();

    double RedAudienceX = 58;
    double RedAudienceY = 53;
    double RedScoringX = 58;
    double RedScoringY = 53;
}

//class HiveCoordinates {
//    public double x;
//    public double y;
//
//    HiveCoordinates(double x, double y) {
//        this.x = x;
//        this.y = y;
//    }
//}