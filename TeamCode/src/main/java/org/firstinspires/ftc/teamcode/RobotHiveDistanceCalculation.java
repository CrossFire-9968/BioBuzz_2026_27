package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.pedro.OneCycle1Intake;


public class RobotHiveDistanceCalculation {

        //OneCycle1Intake onecycle1intake = new OneCycle1Intake();

    static double RedX = 58;
    static double BlueX = 84;
    static double AudienceY = 53;
    static double ScoringY = 89;
    static double HiveHeight = 18;
    static String allianceColor = OneCycle1Intake.locationSelected;

        public static double RobotToHiveDistance(double RobotX, double RobotY) {

            // these are temp variables to enable the distance calculations
            double XVariable = 0;
            double YVariable = 0;

            // this is deciding between which Hive parameters (Blue or Red)
            if (RobotY > 70.75) {
                YVariable = ScoringY;
            } else if (RobotY < 70.75) {
                YVariable = AudienceY;
            }
            if (allianceColor == "Red") {
                XVariable = RedX;
            } else if (allianceColor == "Blue") {
                XVariable = BlueX;
            }
        /* Using the Pythagorean theorem and the variables above,
        these next several lines will calculate the distance between
        the robot and the hive.

        Will find a way to make interchangeable variables depending on where we shoot.
        */

            double legA = Math.abs(XVariable - RobotX);
            double legB = Math.abs(YVariable - RobotY);
            double sideC = Math.sqrt(legA * legA + legB * legB);
            return Math.sqrt(sideC * sideC + HiveHeight * HiveHeight);
        }
    }