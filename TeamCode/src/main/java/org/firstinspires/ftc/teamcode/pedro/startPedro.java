/*
package org.firstinspires.ftc.teamcode.pedro;




import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.pedropathing.follower.Follower;
import com.sun.tools.javac.util.Constants;

//@Configurable
//@Autonomous(name = "startPedro")
//public static class startPedro {
    // Initialize poses

    // private Pose startPose = null;

    //Pose grabGPPControlPoint = null;


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
    private int pathState = 0;

    private TelemetryManager panelsTelemetry; // Panels telemetry

// Add robot Master states
    private enum masterStateEnum {}

    private enum masterSideState {BLUE, RED}

    private masterStateEnum masterState;
    private masterStateEnum MotifPose;

//    private masterSideState sideState;


    private void loadPreset(Alliance alliance, AutoStartLocation location) {
        if (alliance == Alliance.BLUE && location == AutoStartLocation.GOAL) {
            //grabGPPControlPoint = new Pose(48, 85, Math.toRadians(180));
            //add coordinates here
        }
        if (alliance == Alliance.RED && location == AutoStartLocation.GOAL) {
            //add coordinates here
        }

        if (alliance == Alliance.RED && location == AutoStartLocation.POINT) {
           //add coordinates here
        }


        if (alliance == Alliance.BLUE && location == AutoStartLocation.POINT) {
           //add coordinates here
        }
    }


    private void buildPaths() {
        // build paths

    }


/*
    @Override
    public void init() {
        //follower = Constants.createFollower(hardwareMap);
//        follower.setStartingPose(startPoseFrontLeft);



        // Reset master state machine
        masterState = masterStateEnum.;

        // Manually set mosaic until apriltag works
        MotifPose = masterStateEnum.;

        // Hardware inits needed during autonomous

        // Reset state machine
        setPathState(0);

        // Initialize Panels telemetry
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Log completed initialization to Panels and driver station (custom log function)
        log("Status", "Initialized");
        telemetry.update(); // Update driver station after logging
    }


    @Override
    public void init_loop() {
        super.init_loop();


        if (alliance == Alliance.UNKNOWN) {
            telemetry.addLine("Select Alliance");
            telemetry.addLine("   Dpad Up: Blue");
            telemetry.addLine("   Dpad Down: Red");

            if (gamepad1.dpad_up) {
                alliance = Alliance.BLUE;
                allianceSelected = "BLUE";
            } else if (gamepad1.dpad_down) {
                alliance = Alliance.RED;
                allianceSelected = "RED";
            }
        } else if (alliance != Alliance.UNKNOWN && location == AutoStartLocation.UNKNOWN) {
            telemetry.addLine("Alliance Selected: " + allianceSelected);
            telemetry.addLine("Select Start Location");
            telemetry.addLine("   Dpad Left: Goal");
            telemetry.addLine("   Dpad Right: Point");
            telemetry.addLine("   Circle: Start Over");

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

            // Force a rebuild when the selection has changed and all settings are known
            if (alliance != Alliance.UNKNOWN && location != AutoStartLocation.UNKNOWN) {
                runBuild = true;
            }
        }
        else if (alliance != Alliance.UNKNOWN && location != AutoStartLocation.UNKNOWN) {
            telemetry.addLine("Alliance Selected: " + allianceSelected);
            telemetry.addLine("Location Selected: " + locationSelected);
            telemetry.addLine("   Circle: Start Over");

            if (runBuild) {
                loadPreset(alliance, location);
                follower = Constants.createFollower(hardwareMap);
                buildPaths();
                follower.setStartingPose(startPose);
                runBuild = false;
            }

            if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
            }

            if (!runBuild) {
                telemetry.addLine("");
                telemetry.addLine("...ready for play");
            }
        }
    }


/*    @Override
    public void loop() {
        follower.update();

        // The master state machine. This allows for creating smaller reusable state
        // machines then stringing them together in the desired order during runtime.
        // For example, if you use AprilTags to determine the mosaic, the after the
        // preload yeet, the robot could be routed to the correct set of elements.
        switch (masterState) {
            case PRELOAD:
                updateStateMachinePreload();
                if (pathState == -1) {
                    masterState = masterStateEnum.YEET;
                    setPathState(0);
                }
                break;

            case YEET:
                updateStateMachineYeet();
                if (pathState == -1) {

                    // After first yeet, go to the Artifacts
                    if (yeetCount == 1) {
                        masterState = MotifPose;
                    }
                    // After yeeting Motif do ???
                    else if (yeetCount == 2) {
                        masterState = masterStateEnum.PGP;
                    }
                    else if (yeetCount == 3) {
                        masterState = masterStateEnum.PPG;
                    }
                    else if (yeetCount == 4) {
                        masterState = masterStateEnum.PGP;
                    }

                    setPathState(0);
                }
                break;

            case GPP:
                updateStateMachineGPP();
                if (pathState == -1) {
                    masterState = masterStateEnum.YEET;
                    setPathState(0);
                }
                break;

            case PGP:
                updateStateMachinePGP();
                if (pathState == -1) {
                    masterState = masterStateEnum.YEET;
                    setPathState(0);
                }
                break;

            case PPG:
                updateStateMachinePPG();
                if (pathState == -1) {
                    masterState = masterStateEnum.YEET;
                    setPathState(0);
                }
                break;

            case COMPLETE:
                log("State", "Autonomous Complete");
                break;
       }

        // Log to Panels and driver station (custom log function)
//        log("path state", pathState);
//        log("x", follower.getPose().getX());
//        log("y", follower.getPose().getY());
//        log("heading", follower.getPose().getHeading());
        log("Yeet count:", yeetCount);
        telemetry.update();
        panelsTelemetry.update();
*/     //}


    //public void buildPathsPreload() {
        // Move to yeet position from start pose
    //    scorePreload = follower.pathBuilder()
    //            .addPath(new BezierLine(startPose, yeetPose))
    //            .setLinearHeadingInterpolation(startPose.getHeading(), yeetPose.getHeading())
    //            .build();
    //}


    // Move to GPP pose, then back to scoring pose



    // State machine for yeeting preload upon start of autonomous
   /* public void updateStateMachinePreload() {
        switch (pathState) {
            case 0:
                // Move to the yeet pose from the start pose
                log("State", "Auto pathing started");
                follower.followPath(scorePreload, true);
                setPathState(1);
                break;

            case 1:
                log("State", "Moving to yeet pose");

                // Wait until yeet position reached
                if (!follower.isBusy()) {
                    log("State", "Arrived at yeet pose");
                    setPathState(-1); // finished
                }
                break;
        }
    }


    public void updateStateMachineGPP() {
        switch (pathState) {
            case 0:
                log("State", "Moving to GPP pose");
                yeeter.intakeOn();
                follower.followPath(grabGPP, true);
                setPathState(1);
                break;

            case 1:
                log("State", "Arrived at GPP pose");
                // Wait until yeet position reached
                if (!follower.isBusy()) {
                    log("State", "Moving to yeet");
                    yeeter.intakeOff();
                    follower.followPath(scoreGPP, true);
                    setPathState(2);
                }
                break;

            case 2:
                log("Moving to yeet position");

                if (!follower.isBusy()) {
                    log("State", "Arrived at yeet pose");
                    setPathState(-1);
                }
                break;
        }
    }


    public void updateStateMachinePGP() {
        switch (pathState) {
            case 0:
                log("State", "Moving to PGP pose");
                yeeter.intakeOn();
                follower.followPath(grabPGP, true);
                setPathState(1);
                break;

            case 1:
                log("State", "Arrived at PGP pose");

                // Wait until yeet position reached
                if (!follower.isBusy()) {
                    log("State", "Moving to yeet");
                    yeeter.intakeOff();
                    follower.followPath(scorePGP, true);
                    setPathState(-1);
                }
                break;

            case 2:
                log("Moving to yeet position");

                if (!follower.isBusy()) {
                    log("State", "Arrived at yeet pose");
                    setPathState(-1);
                }
                break;
        }
    }


    public void updateStateMachinePPG() {
        switch (pathState) {
            case 0:
                log("State", "Moving to PPG pose");
                yeeter.intakeOn();
                follower.followPath(grabPPG, true);
                setPathState(1);
                break;

            case 1:
                log("Arrived at PPG pose");

                // Wait until yeet position reached
                if (!follower.isBusy()) {
                    log("State", "Moving to yeet");
                    yeeter.intakeOff();
                    follower.followPath(scorePPG, true);
                    setPathState(-1);
                }
                break;

            case 2:
                log("Moving to yeet position");

                if (!follower.isBusy()) {
                    log("State", "Arrived at yeet pose");
                    setPathState(-1);
                }
                break;
        }
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
            panelsTelemetry.debug(caption + ": " + text[0]);
        } else if (text.length >= 2) {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < text.length; i++) {
                message.append(text[i]);
                if (i < text.length - 1) {
                    message.append(" ");
                }
            }
            telemetry.addData(caption, message.toString());
            panelsTelemetry.debug(caption + ": " + message);
        }
    }
}
*/