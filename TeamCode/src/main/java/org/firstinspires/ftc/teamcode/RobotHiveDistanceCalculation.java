package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;




public class RobotHiveDistanceCalculation {



    private static Follower follower;
    static Pose robotPose = follower.pose();

    static double RobotX = Math.round(robotPose.x());
    static double RobotY = Math.round(robotPose.y());

    static double RedX = 58;
    static double BlueX = 84;
    static double AudienceY = 53;
    static double ScoringY = 89;
    static double HiveHeight = 18;



    /* Using the Pythagorean theorem and the variables above,
    these next several lines will calculate the distance between
    the robot and the hive.

    Will find a way to make interchangeable variables depending on where we shoot.
     */

    static double legA = Math.abs(RedX - RobotX);
    static double legB = Math.abs(AudienceY - RobotY);
    static double sideC = Math.sqrt(legA * legA + legB * legB);
    static double RobotToHiveDistance = Math.sqrt(sideC * sideC + HiveHeight * HiveHeight);


}