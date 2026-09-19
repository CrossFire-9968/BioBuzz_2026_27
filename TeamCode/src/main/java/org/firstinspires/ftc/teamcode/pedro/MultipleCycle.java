package org.firstinspires.ftc.teamcode.pedro;


import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

//@Configurable
@Autonomous(name = "MultipleCycle")
public class MultipleCycle extends OpMode {
    // Initialize poses
    public Constants constants = new Constants();
    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final Pose startPose = null;
    private final Pose launchPose = null;
    private final Pose HPIntakeApproach = null;
    private final Pose HPintake = null;

    private final Pose Flower1 = null;

    private final Pose Park = null;

    private enum Alliance {BLUE, RED, UNKNOWN}

    private enum AutoStartLocation {GOAL, POINT, UNKNOWN}

    Alliance alliance = Alliance.UNKNOWN;
    AutoStartLocation location = AutoStartLocation.UNKNOWN;
    boolean runBuild = false;
    String allianceSelected = "";
    String locationSelected = "";

    // Other Variables
    //public Yeeter yeeter = new Yeeter();

    // Initialize variables for paths
    //private PathChain scorePreload;


    private Follower follower;


    //private TelemetryManager panelsTelemetry; // Panels telemetry

// Add robot Master states
    private enum masterStateEnum {}

    private enum masterSideState {BLUE, RED}

    private masterStateEnum masterState;
    private masterStateEnum MotifPose;

// Establish paths: these lines create the interpolations from coordinate-to-coordinate,
// also interpolating heading at the same time.
Path Cycle() {
    return line(startPose, launchPose).linear(startPose, launchPose);
}
Path approachIntake() {
    return line(launchPose, HPIntakeApproach).linear(launchPose, HPIntakeApproach);
}
    Path intakeFromHP() {
    return line(HPIntakeApproach, HPintake).linear(HPIntakeApproach, HPintake);
    }

    Path Cycle2() {
        return line(HPintake, launchPose).linear(HPintake, launchPose);
    }

    Path Flower1Intake() {
        return line(launchPose, Flower1).linear(launchPose, Flower1);
    }

    Path Cycle3() {
        return line(Flower1, launchPose).linear(Flower1, launchPose);
    }

    Path parkInSpot() {
    return line(HPintake, Park).linear(HPintake, Park);
    }

    private Command autoRoutine() {

        return sequential(

                follow(follower, Cycle()),

                follow(follower, approachIntake()),

                follow(follower, intakeFromHP()),

                follow(follower, Cycle2()),

                follow(follower, Flower1Intake()),

                follow(follower, Cycle3())
        );
    }





    private void loadPreset(Alliance alliance, AutoStartLocation location) {
        if (alliance == Alliance.BLUE && location == AutoStartLocation.GOAL) {

        }
        if (alliance == Alliance.RED && location == AutoStartLocation.GOAL) {
             final Pose startPose = poseFactory.of(56, 8, 90);
             final Pose launchPose = poseFactory.of(56.318, 29.9584, 90);
             final Pose HPIntakeApproach = poseFactory.of(6.6719, 13.6764, 18.1575);
             final Pose HPintake = poseFactory.of(6.4966, 3.1921, 89.0422);
             final Pose Flower1 = poseFactory.of(5.2112, 46.7966, 0);
             final Pose Park = poseFactory.of(4.9708, 107.9449, -89.1655);



        }

        if (alliance == Alliance.RED && location == AutoStartLocation.POINT) {
           //add coordinates here
        }


        if (alliance == Alliance.BLUE && location == AutoStartLocation.POINT) {
           //add coordinates here
        }
    }
    @Override
    public void init() {

            Scheduler.reset();

            follower = Constants.create(hardwareMap);


        // Log completed initialization to Panels and driver station (custom log function)
        log("Status", "Initialized");
        telemetry.update(); // Update driver station after logging

        }


    private void buildPaths() {
        // build paths

    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here
        //telemetryData.addData("X", follower.pose().x());
        //telemetryData.addData("Y", follower.pose().y());
        //telemetryData.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }





    @Override
    public void init_loop() {
        super.init_loop();

        // Clear the previous menu display
        telemetry.clearAll();


        // =========================
        // SELECT ALLIANCE
        // =========================
        if (alliance == Alliance.UNKNOWN) {

            telemetry.addLine("Select Alliance");
            telemetry.addLine("");
            telemetry.addLine("Dpad Up: Blue");
            telemetry.addLine("Dpad Down: Red");

            if (gamepad1.dpad_up) {
                alliance = Alliance.BLUE;
                allianceSelected = "BLUE";

            } else if (gamepad1.dpad_down) {
                alliance = Alliance.RED;
                allianceSelected = "RED";
            }
        }


        // =========================
        // SELECT START LOCATION
        // =========================
        else if (location == AutoStartLocation.UNKNOWN) {

            telemetry.addLine("Alliance Selected: " + allianceSelected);
            telemetry.addLine("");
            telemetry.addLine("Select Start Location");
            telemetry.addLine("Dpad Left: Goal");
            telemetry.addLine("Dpad Right: Point");
            telemetry.addLine("");
            telemetry.addLine("Circle: Start Over");


            if (gamepad1.dpad_left) {

                location = AutoStartLocation.GOAL;
                locationSelected = "GOAL";

            } else if (gamepad1.dpad_right) {

                location = AutoStartLocation.POINT;
                locationSelected = "POINT";

            } else if (gamepad1.circle) {

                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
            }


            // Selection is complete.
            // Tell the next init_loop() cycle to build everything.
            if (alliance != Alliance.UNKNOWN &&
                    location != AutoStartLocation.UNKNOWN) {

                runBuild = true;
            }
        }


        // =========================
        // SELECTION COMPLETE
        // =========================
        else {

            telemetry.addLine("Alliance Selected: " + allianceSelected);
            telemetry.addLine("Location Selected: " + locationSelected);
            telemetry.addLine("");
            telemetry.addLine("Circle: Start Over");


            // Only rebuild once
            if (runBuild) {

                loadPreset(alliance, location);

                follower = Constants.create(hardwareMap);

                buildPaths();

               // follower.setStartingPose(startPose);

                runBuild = false;
            }


            // Reset selection
            if (gamepad1.circle) {

                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;

                // Make sure the next completed selection rebuilds everything
                runBuild = true;
            }


            if (!runBuild) {

                telemetry.addLine("");
                telemetry.addLine("====================");
                telemetry.addLine("READY FOR PLAY");
                telemetry.addLine("====================");


                // Pedro telemetry
                if (follower != null) {

                    telemetry.addLine("");

                    telemetry.addData(
                            "X",
                            "%.2f",
                            follower.pose().x()
                    );

                    telemetry.addData(
                            "Y",
                            "%.2f",
                            follower.pose().y()
                    );

                    telemetry.addData(
                            "Heading",
                            "%.1f deg",
                            Math.toDegrees(
                                    follower.pose().heading()
                            )
                    );
                }
            }
        }


        // Send everything above to the Driver Station
        telemetry.update();
    }



    // State machine handling the yeeting of elements in autonomous
// Assumes yeet position is always the same throughout autonomous

        //public void setPathState(int pState) {
        //pathState = pState;
    //}


    // Custom logging function to support telemetry and Panels
    private void log(String caption, Object... text) {
        if (text.length == 1) {
            telemetry.addData(caption, text[0]);
            //panelsTelemetry.debug(caption + ": " + text[0]);
        } else if (text.length >= 2) {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < text.length; i++) {
                message.append(text[i]);
                if (i < text.length - 1) {
                    message.append(" ");
                }
            }
            telemetry.addData(caption, message.toString());
            //panelsTelemetry.debug(caption + ": " + message);
        }
    }
}
