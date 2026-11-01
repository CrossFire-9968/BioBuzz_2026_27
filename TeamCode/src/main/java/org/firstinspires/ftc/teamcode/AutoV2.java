package org.firstinspires.ftc.teamcode;


import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.ivy.pedro.PedroCommands.hold;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.State;
import org.firstinspires.ftc.teamcode.pedro.Constants;

//@Configurable
@Autonomous(name = "AutoV2")
public class AutoV2 extends OpMode {

// Establishes classes:
    //public PIDController pidcontroller = new PIDController();
    public LauncherTest launcher = new LauncherTest(1,1,2);
    public Intake intake = new Intake();


    // Establishes state machine:
    public enum CycleState {
        Cycle1,
        Cycle2,
        Cycle3
    }
    public CycleState cycleState = CycleState.Cycle1;


// Naming Conventions:
    // LZ: The side of the field containing the "loading zone".
    // NLZ: The side of the field not containing the "loading zone".
    // CP: Control point.

// Poses:
    // Initialize poses:
    public Constants constants = new Constants();
    private final PoseFactory poseFactory = PoseFactory.degrees();


    // Start poses:
    private Pose NLZstartPosePrimary = poseFactory.of(56, 8, 90);
    private Pose NLZstartPoseSecondary = poseFactory.of(33.9378, 8.7899, 90);
    private Pose LZstartPosePrimary = poseFactory.of(58.62, 133.2152, 270);
    private Pose LZstartPoseSecondary = poseFactory.of(33.4306, 133.0461, 270);


    // NLZ specific poses:
    private Pose HPintake = poseFactory.of(7.5, 15, 180);
    private Pose NLZFlower  = poseFactory.of(10.3708, 46.3227, 180);
    private Pose NLZLaunchPoseSecondary = poseFactory.of(42.5077, 16.6774, 90);
    private Pose NLZLaunchPosePrimary = poseFactory.of(58.7005, 17.6739, 90);


    // LZ specific poses:
    private Pose LZLaunchPoseSecondary = poseFactory.of(34.5359, 118.5674, 180);
    private Pose LZLaunchPosePrimary = poseFactory.of(59.4478, 118.8165, 270);
    private Pose LZFlower = poseFactory.of(46.7427, 132.0198, 90);


    // General poses:
    private Pose Park =  poseFactory.of(9, 98, -89.1655);



    // Control points:
    private Pose ParkCP = poseFactory.of(3, 7, -90);
    private Pose startPoseCP = poseFactory.of(5.7094, 73.2003, 180);
    private Pose LZLaunchPosePrimary_LZFlower_CP = poseFactory.of(45.1712, 110.954, 90);
    private Pose LZLaunchPoseSecondary_LZFlower_CP = poseFactory.of(46.9151, 111.7013, 90);
    private Pose NLZLaunchPosePrimary_NLZFlower_CP  = poseFactory.of(27.982, 22.7656, 180);
    private Pose NLZLaunchPoseSecondary_NLZFlower_CP = poseFactory.of(36.7012, 40.9513, 180);
    private Pose NLZLaunchPoseSecondary_HPintake_CP = poseFactory.of(34.9573, 5.8255, 180);
    private Pose NLZLaunchPoseSecondary_LZLaunchPoseSecondary_CP = poseFactory.of(8.0774, 70.0668, 90);
    private Pose NLZLaunchPoseSecondary_LZLaunchPosePrimary_CP = poseFactory.of(61.8373, 20.0262, 90);
    private Pose NLZLaunchPosePrimary_LZLaunchPoseSecondary_CP = poseFactory.of(29.0404, 32.3673, 90);


    // Copy and paste for new pose (convenient)
    //private Pose  = poseFactory.of();




//Paths: Defining the actual interpolations from position to positon.
  // Leaving start positions:
    // NLZ:
    Path NLZstartPosePrimary_NLZLaunchPosePrimary(){
        return line(NLZstartPosePrimary,NLZLaunchPosePrimary).linear(NLZstartPosePrimary,NLZLaunchPosePrimary);
    }
    Path NLZstartPosePrimary_NLZLaunchPoseSecondary(){
        return line(NLZstartPosePrimary,NLZLaunchPoseSecondary).linear(NLZstartPosePrimary,NLZLaunchPoseSecondary);
    }
    Path NLZstartPoseSecondary_NLZLaunchPosePrimary(){
        return line(NLZstartPoseSecondary,NLZLaunchPosePrimary).linear(NLZstartPoseSecondary,NLZLaunchPosePrimary);
    }
    Path NLZstartPoseSecondary_NLZLaunchPoseSecondary(){
        return line(NLZstartPoseSecondary,NLZLaunchPoseSecondary).linear(NLZstartPoseSecondary,NLZLaunchPoseSecondary);
    }

    // Leaving start positions:
    // LZ:
    Path LZstartPosePrimary_LZLaunchPosePrimary(){
        return line(LZstartPosePrimary,LZLaunchPosePrimary).linear(LZstartPosePrimary,LZLaunchPosePrimary);
    }
    Path LZstartPosePrimary_LZLaunchPoseSecondary(){
        return line(LZstartPosePrimary,LZLaunchPoseSecondary).linear(LZstartPosePrimary,LZLaunchPoseSecondary);
    }
    Path LZstartPoseSecondary_LZLaunchPosePrimary(){
        return line(LZstartPoseSecondary,LZLaunchPosePrimary).linear(LZstartPoseSecondary,LZLaunchPosePrimary);
    }
    Path LZstartPoseSecondary_LZLaunchPoseSecondary(){
        return line(LZstartPoseSecondary,LZLaunchPoseSecondary).linear(LZstartPoseSecondary,LZLaunchPoseSecondary);
    }




//General Variables;
    // Numeric variables:
    public double timeToWaitAfterLaunch = 1000;
    public double timeToWaitBeforeLaunch = 1000;


    // Set enums so that we can select Position:
    public enum Alliance {HiveUp, HiveDown, UNKNOWN}
    public enum AutoStartLocation {Red, Blue, UNKNOWN}
    public enum StartPose {Primary,Secondary,Unknown}
    public enum LaunchPose {Primary,Secondary,Unknown}


    // Establish variables to interface with the enums:
    public Alliance alliance = Alliance.UNKNOWN;
    public AutoStartLocation location = AutoStartLocation.UNKNOWN;
    public StartPose startpose = StartPose.Unknown;
    public LaunchPose launchpose = LaunchPose.Unknown;


    // Misc:
    boolean runBuild = false;
    public static String allianceSelected = "";
    public static String locationSelected = "";
    public static String startposeSelected = "";
    public static String launchposeSelected = "";
    private Follower follower;




// Pedro commands:
//    Command setIntake = Command.build()
//            .setStart(() -> {
//                telemetry.addLine("INTAKE START");
//                intake.intake();
//                telemetry.addLine("INTAKE START2");
//                // executed on start
//            })
//            .setExecute(() -> {
//
//                // executed on execute
//            })
//            //.setDone(() -> {
//
//                // return true to end the command
//               // return false;
//            //})
//            .setEnd(endCondition -> {
//
//                // executed on end
//            });
//
//    Command intakeOff = Command.build()
//            .setStart(() -> {
//                telemetry.addLine("INTAKE START");
//                intake.stoptake();
//                telemetry.addLine("INTAKE START2");
//                // executed on start
//            })
//            .setExecute(() -> {
//
//                // executed on execute
//            })
//            //.setDone(() -> {
//
//            // return true to end the command
//            // return false;
//            //})
//            .setEnd(endCondition -> {
//                // executed on end
//            });
//            //.requiring(/* requirements */)
//            //.setInterruptedBehavior(/* interrupted behavior */)
//            //.setBlockedBehavior(/* blocked behavior */)
//            //.setConflictBehavior(/* conflict behavior */)
//            //.setPriority(/* priority */);
//
//    Command launch = Command.build()
//            .setStart(() -> {
//                telemetry.addLine("Launching");
//                launcher.LaunchPollen();
//                waitMs(timeToWaitAfterLaunch);
//                launcher.LaunchOff();
//                // executed on start
//            })
//            .setExecute(() -> {
//
//                // executed on execute
//            })
//            //.setDone(() -> {
//
//            // return true to end the command
//            // return false;
//            //})
//            .setEnd(endCondition -> {
//                // executed on end
//            });
//
//    Command spool = Command.build()
//            .setStart(() -> {
//
//               launcher.SpoolUpAndLaunch();
//
//                // executed on start
//            })
//            .setExecute(() -> {
//
//
//                // executed on execute
//            })
//            //.setDone(() -> {
//
//            // return true to end the command
//            // return false;
//            //})
//            .setEnd(endCondition -> {
//
//                // executed on end
//            });
//
//    Command spool2 = Command.build()
//            .setStart(() -> {
//                waitMs(timeToWaitBeforeLaunch);
//                launcher.SpoolUp();
//
//                // executed on start
//            })
//            .setExecute(() -> {
//
//
//                // executed on execute
//            })
//            //.setDone(() -> {
//
//            // return true to end the command
//            // return false;
//            //})
//            .setEnd(endCondition -> {
//
//                // executed on end
//            });
//
//    Command stopLaunch = Command.build()
//            .setStart(() -> {
//                telemetry.addLine("Launching");
//                launcher.stop();
//                telemetry.addLine("INTAKE START2");
//                // executed on start
//            })
//            .setExecute(() -> {
//                telemetry.addData("Intake power", intake.Intake.getPower());
//
//                // executed on execute
//            })
//            //.setDone(() -> {
//
//            // return true to end the command
//            // return false;
//            //})
//            .setEnd(endCondition -> {
//                telemetry.addLine("INTAKE END: " + endCondition);
//                // executed on end
//            });
    //.requiring(/* requirements */)
    //.setInterruptedBehavior(/* interrupted behavior */)
    //.setBlockedBehavior(/* blocked behavior */)
    //.setConflictBehavior(/* conflict behavior */)
    //.setPriority(/* priority */);




// AutoRoutine: combines path following and action commands into a cohesive auto.
    private Command autoRoutine() {
        if (alliance == Alliance.HiveUp) {
            switch (cycleState){
                case Cycle1:
                    return sequential(

                    );
            }
        }


        if (alliance == Alliance.HiveDown) {
           return sequential(
            );
        }
        telemetry.addLine("Alliance not set");
        return null;

    }




// Init: Runs the init sequence.
    @Override
    public void init() {

        Scheduler.reset();

        follower = Constants.create(hardwareMap);

        intake.init(hardwareMap);
        launcher.init(hardwareMap);

        // Log completed initialization to Panels and driver station (custom log function)
        log("Status", "Initialized");
        telemetry.update(); // Update driver station after logging

    }



    private void buildPaths() {
        // build paths
    }




// Start: Makes sure pedro starts correctly configured.
    @Override
    public void start() {
        follower.setPose(NLZstartPosePrimary);
        schedule(autoRoutine());
    }




// Loop: Updates stuff continuously.
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




// Init Loop: Runs the selection in init.
    @Override
    public void init_loop() {

        super.init_loop();

        // Clear the previous menu display
        telemetry.clearAll();

        // =========================
        // SELECT ALLIANCE
        // =========================

        if (alliance == Alliance.UNKNOWN) {
            telemetry.addLine("Select Position");
            telemetry.addLine("");
            telemetry.addLine("Dpad Up: HiveUp");
            telemetry.addLine("Dpad Down: HiveDown");

            if (gamepad1.dpad_up) {
                alliance = Alliance.HiveUp;
                allianceSelected = "HiveUp";
                telemetry.addLine(allianceSelected);

            } else if (gamepad1.dpad_down) {
                alliance = Alliance.HiveDown;
                allianceSelected = "HiveDown";
                telemetry.addLine(allianceSelected);
            }
        }


        // =========================
        // SELECT START LOCATION
        // =========================


        else if (location == AutoStartLocation.UNKNOWN) {
            telemetry.addLine("Alliance Selected: " + allianceSelected);
            telemetry.addLine("");
            telemetry.addLine("Circle: Start Over");
            telemetry.addLine(String.valueOf(gamepad1));

            if (gamepad1.dpad_left) {
                location = AutoStartLocation.Red;
                locationSelected = "Red";
                telemetry.addLine(locationSelected);

            } else if (gamepad1.dpad_right) {
                location = AutoStartLocation.Blue;
                locationSelected = "Blue";
                telemetry.addLine(locationSelected);

            } else if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
                allianceSelected = "";
                locationSelected = "";
            }
        }

        // =========================
        // SELECT START POSE
        // =========================


         else if (startpose == StartPose.Unknown) {
            telemetry.addLine("Start Pose Selected: " + startposeSelected);
            telemetry.addLine("");
            telemetry.addLine("Circle: Start Over");
            telemetry.addLine(String.valueOf(gamepad1));

            if (gamepad1.dpad_left) {
                startpose = StartPose.Primary;
                startposeSelected = "Primary";
                telemetry.addLine(startposeSelected);

            } else if (gamepad1.dpad_right) {
                startpose = StartPose.Secondary;
                startposeSelected = "Secondary";
                telemetry.addLine(startposeSelected);

            } else if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
                startpose = StartPose.Unknown;
                allianceSelected = "";
                locationSelected = "";
                startposeSelected = "";
            }
            // Selection is complete.
            // Tell the next init_loop() cycle to build everything.
            if (alliance != AutoV2.Alliance.UNKNOWN &&
                    location != AutoV2.AutoStartLocation.UNKNOWN) {
                telemetry.addLine("runbuild about to run");
                runBuild = true;

            }
        }


//ADD LAUNCHPOSE INITIALIZATION!!!




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
                telemetry.addLine("ran loadPreset");
                follower = Constants.create(hardwareMap);
                buildPaths();
                follower.setPose(NLZstartPosePrimary);
                runBuild = false;
            }


            // Reset selection
            if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
                allianceSelected = "";
                locationSelected = "";
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


                    // DEBUG: show the actual selected preset coordinates
                    telemetry.addLine("");
                    telemetry.addLine("Preset Check:");

//                    telemetry.addData(
//                            "Start Pose X",
//                            "%.2f",
//                            NLZstartPosePrimary.x()
//                    );
//
//                    telemetry.addData(
//                            "Start Pose Y",
//                            "%.2f",
//                            NLZstartPosePrimary.y()
//                    );
//
//                    telemetry.addData(
//                            "Launch Pose X",
//                            "%.2f",
//                            launchPose.x()
//                    );
//
//                    telemetry.addData(
//                            "Launch Pose Y",
//                            "%.2f",
//                            launchPose.y()
//                    );

                }
            }
        }


        // Sends everything above to the Driver Station
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