package org.firstinspires.ftc.teamcode;


import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.ivy.pedro.PedroCommands.hold;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


import org.firstinspires.ftc.teamcode.pedro.Constants;
import static com.pedropathing.ivy.commands.Commands.instant;

//@Configurable
@Autonomous(name = "AutoV2")
public class AutoV2 extends OpMode {

    // Establishes classes:
    //public PIDController pidcontroller = new PIDController();
    public LauncherTest launcher = new LauncherTest(1, 1, 2);
    public Intake intake = new Intake();
    double advanceAmount = 1;
    double IntakeCount = 0;
    private Command activeAutoCommand;
    private boolean nextSegmentRequested = false;


    //Swaps Launching Poses
    private void swapLaunchingPoses() {

        Pose temp = LaunchingPose;

        LaunchingPose = LaunchingPose2;

        LaunchingPose2 = temp;
    }

    //Case change logic
    private void changeSegment(AutoSegment next) {
        autoSegment = next;
        nextSegmentRequested = true;
    }

    private void changeCycle(CycleState nextCycle, AutoSegment nextSegment) {
        cycleState = nextCycle;
        changeSegment(nextSegment);
    }


//    private void decideNextSegment() {
//
//        if (advanceAmount == 1) {
//            changeSegment(AutoSegment.IntakeHP);
//        }
//        else {
//            changeCycle(CycleState.Cycle2, AutoSegment.Launch);
//        }
//    }

    private void decideNextIntake() {
        //AdvanceAmount is instead of the AprilTag output
        if (cycleState == CycleState.Cycle1) {
            if (advanceAmount == 1) {
                if (humanPlayerIntake == HumanPlayerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeHP);
                } else if (closeFlowerIntake == CloseFlowerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeNLZFlower);

                } else if (farFlowerIntake == FarFlowerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeLZFlower);
                } else {
                    changeSegment(AutoSegment.Park);
                }
            } else {
                changeSegment(AutoSegment.SwitchSide);
            }
        }


        else if (cycleState == CycleState.Cycle2) {
            if (advanceAmount == 1) {
                if (farFlowerIntake == FarFlowerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeLZFlower);
                }
                else if (closeFlowerIntake == CloseFlowerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeNLZFlower);
                }
                else if (humanPlayerIntake == HumanPlayerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeHP);
                }
                else {
                    changeSegment(AutoSegment.Park);
                }
            } else {
                changeSegment(AutoSegment.SwitchSide);
            }
        }


        else if (cycleState == CycleState.Cycle3) {
            if (advanceAmount == 1) {
                if (closeFlowerIntake == CloseFlowerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeNLZFlower);
                }
                else if (farFlowerIntake == FarFlowerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeLZFlower);
                }
                else if (humanPlayerIntake == HumanPlayerIntake.UNUSED) {
                    changeSegment(AutoSegment.IntakeHP);
                }
                else {
                    changeSegment(AutoSegment.Park);
                }
            } else {
                changeSegment(AutoSegment.Park);
            }
        }
    }


    private void decideIntakeOrLaunchAfterSideSwitch(CycleState nextCycle) {

        // If we already have balls, launch immediately.
        if (intakeState == IntakeState.ElementsContained) {

            changeCycle(nextCycle, AutoSegment.Launch);
            return;
        }

        // Otherwise, we need to find an available intake.
        if (nextCycle == CycleState.Cycle2) {

            if (farFlowerIntake == FarFlowerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeLZFlower);

            } else if (closeFlowerIntake == CloseFlowerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeNLZFlower);

            } else if (humanPlayerIntake == HumanPlayerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeHP);

            } else {
                changeCycle(nextCycle, AutoSegment.Park);
            }

        } else if (nextCycle == CycleState.Cycle3) {

            if (closeFlowerIntake == CloseFlowerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeNLZFlower);

            } else if (farFlowerIntake == FarFlowerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeLZFlower);

            } else if (humanPlayerIntake == HumanPlayerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeHP);

            } else {
                changeCycle(nextCycle, AutoSegment.Park);
            }
        }
    }

    // Establishes state machine:
    public enum CycleState {
        Cycle1,
        Cycle2,
        Cycle3
    }

    public enum AutoSegment {
        LeavePark,
        Launch,
        IntakeHP,
        IntakeNLZFlower,
        IntakeLZFlower,
        SwitchSide,
        Park
    }

    public CycleState cycleState = CycleState.Cycle1;
    public AutoSegment autoSegment = AutoSegment.LeavePark;


// Naming Conventions:
    // LZ: The side of the field containing the "loading zone".
    // NLZ: The side of the field not containing the "loading zone".
    // CP: Control point.

    // Poses:
    // Initialize poses:
    public Constants constants = new Constants();
    private final PoseFactory poseFactory = PoseFactory.degrees();


    private Pose StartingPose = null;
    private Pose LaunchingPose = null;
    private Pose LaunchingPose2 = null;

    // Start poses:
    private Pose NLZstartPosePrimary = poseFactory.of(56, 8, 90);
    private Pose NLZstartPoseSecondary = poseFactory.of(33.9378, 8.7899, 90);
    private Pose LZstartPosePrimary = poseFactory.of(58.62, 133.2152, 270);
    private Pose LZstartPoseSecondary = poseFactory.of(33.4306, 133.0461, 270);


    // NLZ specific poses:
    private Pose HPintake = poseFactory.of(7.5, 15, 180);
    private Pose NLZFlower = poseFactory.of(10.3708, 46.3227, 180);
    private Pose NLZLaunchPoseSecondary = poseFactory.of(42.5077, 16.6774, 90);
    private Pose NLZLaunchPosePrimary = poseFactory.of(58.7005, 17.6739, 90);


    // LZ specific poses:
    private Pose LZLaunchPoseSecondary = poseFactory.of(34.5359, 118.5674, 270);
    private Pose LZLaunchPosePrimary = poseFactory.of(59.4478, 118.8165, 270);
    private Pose LZFlower = poseFactory.of(46.7427, 132.0198, 90);


    // General poses:
    private Pose Park = poseFactory.of(9, 98, 270);


    // Control points:
    private Pose ParkCP = poseFactory.of(3, 7, 270);
    private Pose startPoseCP = poseFactory.of(5.7094, 73.2003, 180);
    private Pose LZLaunchPosePrimary_LZFlower_CP = poseFactory.of(45.1712, 110.954, 90);
    private Pose LZLaunchPosePrimary_HPIntake_CP = poseFactory.of(68.7279, 0, 180);
    private Pose LZLaunchPosePrimary_NLZFlower_CP1 = poseFactory.of(10.2343, 118.8719, 270);
    private Pose LZLaunchPosePrimary_NLZFlower_CP2 = poseFactory.of(47.9496, 38.7858, 180);
    private Pose LZLaunchPoseSecondary_LZFlower_CP = poseFactory.of(46.9151, 111.7013, 90);
    private Pose NLZLaunchPosePrimary_NLZFlower_CP = poseFactory.of(27.982, 22.7656, 180);
    private Pose NLZLaunchPosePrimary_LZFlower_CP = poseFactory.of(62.98, 101.3146, 90);
    private Pose NLZLaunchPoseSecondary_NLZFlower_CP = poseFactory.of(36.7012, 40.9513, 180);
    private Pose NLZLaunchPosePrimary_HPintake_CP = poseFactory.of(57.2321, 3.7692, 180);
    private Pose LZLaunchPoseSecondary_HPIntake_CP = poseFactory.of(40.6105, 3.2279, 180);
    private Pose LZLaunchPoseSecondary_NLZFlower_CP1 = poseFactory.of(38.2093, 45.4206, 90);
    private Pose NLZLaunchPoseSecondary_HPintake_CP = poseFactory.of(34.9573, 5.8255, 180);
    private Pose NLZLaunchPoseSecondary_LZLaunchPoseSecondary_CP = poseFactory.of(8.0774, 70.0668, 90);
    private Pose NLZLaunchPoseSecondary_LZLaunchPosePrimary_CP = poseFactory.of(61.8373, 20.0262, 90);
    private Pose NLZLaunchPosePrimary_LZLaunchPoseSecondary_CP = poseFactory.of(29.0404, 32.3673, 90);


    // Copy and paste for new pose (convenient)
    //private Pose  = poseFactory.of();


    //Paths: Defining the actual interpolations from position to positon.
         // Leaving start positions:
            // Leaving start positions
    Path StartingPose_LaunchingPose() {
        return line(StartingPose, LaunchingPose).linear(StartingPose, LaunchingPose);
    }


    //NLZ Cycles:
        //Primary:
            //Intakes:
    Path NLZLaunchingPose_HPIntake() {
        return curve(LaunchingPose, NLZLaunchPosePrimary_HPintake_CP, HPintake).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LaunchingPose, NLZLaunchPosePrimary_HPintake_CP)).until(1,Interpolator.linear(NLZLaunchPosePrimary_HPintake_CP ,HPintake)));
    }
    Path NLZLaunchingPose_NLZFlower() {
        return curve(LaunchingPose, NLZLaunchPosePrimary_NLZFlower_CP, NLZFlower).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LaunchingPose, NLZLaunchPosePrimary_NLZFlower_CP)).until(1,Interpolator.linear(NLZLaunchPosePrimary_NLZFlower_CP, NLZFlower)));
    }
    Path NLZLaunchingPose_LZFlower() {
        return curve(LaunchingPose, NLZLaunchPosePrimary_LZFlower_CP, LZFlower).linear(LaunchingPose, LZFlower);
    }


            //Returns:
    Path HPIntake_NLZLaunchingPose() {
        return curve(HPintake, NLZLaunchPosePrimary_HPintake_CP, LaunchingPose).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(HPintake, NLZLaunchPosePrimary_HPintake_CP)).until(1,Interpolator.linear(NLZLaunchPosePrimary_HPintake_CP, LaunchingPose)));
    }
    Path NLZFlower_NLZLaunchingPose() {
        return curve(NLZFlower, NLZLaunchPosePrimary_NLZFlower_CP,LaunchingPose).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(NLZFlower, NLZLaunchPosePrimary_NLZFlower_CP)).until(1,Interpolator.linear(NLZLaunchPosePrimary_NLZFlower_CP,LaunchingPose)));
    }
    Path LZFlower_NLZLaunchingPose() {
        return curve(LZFlower, NLZLaunchPosePrimary_LZFlower_CP,LaunchingPose).linear(LZFlower,LaunchingPose);
    }


    //LZ Cycles:
        //Primary:
            //Intakes:
    Path LZLaunchingPose_HPIntake() {
        return curve(LaunchingPose, LZLaunchPosePrimary_HPIntake_CP, HPintake).heading(Interpolator.piecewise().until(0.75,Interpolator.linear(LaunchingPose, LZLaunchPosePrimary_HPIntake_CP)).until(1,Interpolator.linear(LZLaunchPosePrimary_HPIntake_CP, HPintake)));
    }
    Path LZLaunchingPose_NLZFlower() {
        return curve(LaunchingPose,LZLaunchPosePrimary_NLZFlower_CP1,LZLaunchPosePrimary_NLZFlower_CP2 , NLZFlower).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LaunchingPose,LZLaunchPosePrimary_NLZFlower_CP1)).until(1,Interpolator.linear(LZLaunchPosePrimary_NLZFlower_CP1, NLZFlower)));
    }
    Path LZLaunchingPose_LZFlower() {
        return curve(LaunchingPose, LZLaunchPosePrimary_LZFlower_CP, LZFlower).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LaunchingPose, LZLaunchPosePrimary_LZFlower_CP)).until(1,Interpolator.linear(LZLaunchPosePrimary_LZFlower_CP, LZFlower)));
    }


    //Returns:
    Path HPIntake_LZLaunchingPose() {
        return curve(HPintake, LZLaunchPosePrimary_HPIntake_CP, LaunchingPose).heading(Interpolator.piecewise().until(0.25,Interpolator.linear(HPintake, LZLaunchPosePrimary_HPIntake_CP)).until(1,Interpolator.linear(LZLaunchPosePrimary_HPIntake_CP, LaunchingPose)));
    }
    Path NLZFlower_LZLaunchingPose() {
        return curve(NLZFlower, LZLaunchPosePrimary_NLZFlower_CP2, LZLaunchPosePrimary_NLZFlower_CP1, LaunchingPose).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LZLaunchPosePrimary_NLZFlower_CP2, LZLaunchPosePrimary_NLZFlower_CP1)).until(1,Interpolator.linear(LZLaunchPosePrimary_NLZFlower_CP1, LaunchingPose)));
    }
    Path LZFlower_LZLaunchingPose() {
        return curve(LZFlower, LZLaunchPosePrimary_LZFlower_CP, LaunchingPose).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LZFlower, LZLaunchPosePrimary_LZFlower_CP)).until(1,Interpolator.linear(LZLaunchPosePrimary_LZFlower_CP, LaunchingPose)));
    }


    //Parks:
    Path NLZLaunchingPose_Park() {
        return curve(LaunchingPose,ParkCP,Park).heading(Interpolator.piecewise().until(0.75,Interpolator.linear(LaunchingPose,ParkCP)).until(1,Interpolator.linear(ParkCP, Park)));
    }
    Path LZLaunchingPose_Park() {
        return line(LaunchingPose,Park).linear(LaunchingPose,Park);
    }


    //Switch Sides:
    Path SwitchSides() {
        return line(LaunchingPose, LaunchingPose2).linear(LaunchingPose, LaunchingPose2);
    }








    //General Variables;
    // Numeric variables:
    public double timeToWaitAfterLaunch = 1000;
    public double timeToWaitBeforeLaunch = 1000;


    // Set enums so that we can select Position:
    public enum Alliance {HiveUp, HiveDown, UNKNOWN}
    public enum AutoStartLocation {Red, Blue, UNKNOWN}
    public enum StartPose {Primary, Secondary, Unknown}
    public enum LaunchPose {Primary, Secondary, Unknown}
    public enum HumanPlayerIntake {UNUSED,USED}
    public enum CloseFlowerIntake {UNUSED,USED}
    public enum FarFlowerIntake {UNUSED,USED}
    public enum IntakeState {ElementsContained, NoElements}





    // Establish variables to interface with the enums:
    public Alliance alliance = Alliance.UNKNOWN;
    public AutoStartLocation location = AutoStartLocation.UNKNOWN;
    public StartPose startpose = StartPose.Unknown;
    public LaunchPose launchpose = LaunchPose.Unknown;
    public HumanPlayerIntake humanPlayerIntake = HumanPlayerIntake.UNUSED;
    public CloseFlowerIntake closeFlowerIntake = CloseFlowerIntake.UNUSED;
    public FarFlowerIntake farFlowerIntake = FarFlowerIntake.UNUSED;
    public IntakeState intakeState = IntakeState.ElementsContained;


    // Misc:
    boolean runBuild = false;
    public static String allianceSelected = "";
    public static String locationSelected = "";
    public static String startposeSelected = "";
    public static String launchposeSelected = "";
    private Follower follower;

    //Robot's Current pose:
    Pose currentPose;


// Pedro commands:
    Command setIntake = instant(() ->intake.intake());

    Command intakeOff = instant(() ->intake.stoptake());

    Command setIntakeFlower = sequential(
            instant(() -> intake.intake()),
            waitMs(1500)
            );


    Command MarkOffHPIntake = instant(() -> humanPlayerIntake = HumanPlayerIntake.USED);

    Command MarkOffNLZFlowerIntake = instant(() -> closeFlowerIntake = CloseFlowerIntake.USED);

    Command MarkOffLZFlowerIntake = instant(() -> farFlowerIntake = FarFlowerIntake.USED);




    // AutoRoutine: combines path following and action commands into a cohesive auto.
    private Command autoRoutine() {
        if (alliance == Alliance.HiveUp) {
            switch (cycleState) {
                case Cycle1:
                    switch (autoSegment) {
                            case LeavePark:
                            return sequential(
                                    follow(follower, StartingPose_LaunchingPose()),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                            case Launch:
                            return sequential(
                                    //check atag
                                    //launch
                                    //check atag
                                    instant(() -> intakeState = IntakeState.NoElements),
                                    instant(() -> decideNextIntake())
                            );

                            case IntakeHP:
                            return sequential(
                                    setIntake,
                                    follow(follower, NLZLaunchingPose_HPIntake()),
                                    follow(follower, HPIntake_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffHPIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );

                            case IntakeNLZFlower:
                            return sequential(
                                    //FlowerIntake Down
                                    follow(follower, NLZLaunchingPose_NLZFlower()),
                                    setIntakeFlower,
                                    follow(follower, NLZFlower_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffNLZFlowerIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );

                            case IntakeLZFlower:
                            return sequential(
                                    //FlowerIntake Down
                                    follow(follower, NLZLaunchingPose_LZFlower()),
                                    setIntakeFlower,
                                    follow(follower, LZFlower_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffLZFlowerIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );

                            case SwitchSide:
                            return sequential(
                                    follow(follower, SwitchSides()),
                                    instant(() -> swapLaunchingPoses()),
                                    instant(() -> decideIntakeOrLaunchAfterSideSwitch(
                                            CycleState.Cycle2
                                    ))
                            );

                            case Park:
                            return sequential(
                                    follow(follower, NLZLaunchingPose_Park())
                            );

                    }
                    break;


                case Cycle2:
                    switch (autoSegment) {
                        case Launch:
                            return sequential(
                                    //check atag
                                    //launch
                                    //check atag
                                    instant(() -> intakeState = IntakeState.NoElements),
                                    instant(() -> decideNextIntake())
                            );
                            case IntakeLZFlower:
                            return sequential(
                                    //FlowerIntake Down
                                    follow(follower, LZLaunchingPose_LZFlower()),
                                    setIntakeFlower,
                                    follow(follower, LZFlower_LZLaunchingPose()),
                                    intakeOff,
                                    MarkOffLZFlowerIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                            case IntakeNLZFlower:
                            return sequential(
                                    //FlowerIntake Down
                                    follow(follower, LZLaunchingPose_NLZFlower()),
                                    setIntakeFlower,
                                    follow(follower, NLZFlower_LZLaunchingPose()),
                                    intakeOff,
                                    MarkOffNLZFlowerIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                            case IntakeHP:
                            return sequential(
                                    setIntake,
                                    follow(follower, LZLaunchingPose_HPIntake()),
                                    follow(follower, HPIntake_LZLaunchingPose()),
                                    intakeOff,
                                    MarkOffHPIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                            case Park:
                            return sequential(
                                    follow(follower, LZLaunchingPose_Park())
                            );
                            case SwitchSide:
                            return sequential(
                                    follow(follower, SwitchSides()),
                                    instant(() -> swapLaunchingPoses()),
                                    instant(() -> decideIntakeOrLaunchAfterSideSwitch(
                                            CycleState.Cycle3
                                    ))
                            );
                    }
                    break;


                case Cycle3:
                    switch (autoSegment) {
                        case Launch:
                            return sequential(
                                    //check atag
                                    //launch
                                    //check atag
                                    instant(() -> intakeState = IntakeState.NoElements),
                                    instant(() -> decideNextIntake())
                            );
                        case IntakeLZFlower:
                            return sequential(
                                    //FlowerIntake Down
                                    follow(follower, NLZLaunchingPose_LZFlower()),
                                    setIntakeFlower,
                                    follow(follower, LZFlower_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffLZFlowerIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                        case IntakeNLZFlower:
                            return sequential(
                                    //FlowerIntake Down
                                    follow(follower, NLZLaunchingPose_NLZFlower()),
                                    setIntakeFlower,
                                    follow(follower, NLZFlower_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffNLZFlowerIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                        case IntakeHP:
                            return sequential(
                                    setIntake,
                                    follow(follower, NLZLaunchingPose_HPIntake()),
                                    follow(follower, HPIntake_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffHPIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(() -> changeSegment(AutoSegment.Launch))
                            );
                        case Park:
                            return sequential(
                                    follow(follower, NLZLaunchingPose_Park())
                            );
                    }
                break;
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
        //launcher.init(hardwareMap);

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

        if (!runBuild ||
                StartingPose == null ||
                LaunchingPose == null ||
                LaunchingPose2 == null) {

            telemetry.addLine("ERROR: Auto setup incomplete!");
            telemetry.update();
            return;
        }

        follower.setPose(StartingPose);

        activeAutoCommand = autoRoutine();

        if (activeAutoCommand != null) {
            schedule(activeAutoCommand);
        }
    }

    // Loop: Updates stuff continuously.
    @Override
    public void loop() {

        follower.update();
        currentPose = follower.pose();
        Scheduler.execute();

            if (activeAutoCommand != null &&
                    !Scheduler.isScheduled(activeAutoCommand)) {

                activeAutoCommand = null;

                if (nextSegmentRequested) {
                    nextSegmentRequested = false;

                    activeAutoCommand = autoRoutine();

                    if (activeAutoCommand != null) {
                        schedule(activeAutoCommand);
                    }
                }
            }

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
            } else if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                allianceSelected = "";
                runBuild = false;
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
                runBuild = false;
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
                if(alliance == Alliance.HiveUp)
                    StartingPose = NLZstartPosePrimary;
                if(alliance == Alliance.HiveDown)
                    StartingPose = LZstartPosePrimary;

            } else if (gamepad1.dpad_right) {
                startpose = StartPose.Secondary;
                startposeSelected = "Secondary";
                telemetry.addLine(startposeSelected);
                if(alliance == Alliance.HiveUp)
                    StartingPose = NLZstartPoseSecondary;
                if(alliance == Alliance.HiveDown)
                    StartingPose = LZstartPoseSecondary;

            } else if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
                startpose = StartPose.Unknown;
                allianceSelected = "";
                locationSelected = "";
                startposeSelected = "";
                runBuild = false;
            }


            // Selection is complete.
            // Tell the next init_loop() cycle to build everything.


        } else if (launchpose == LaunchPose.Unknown) {
            telemetry.addLine("Launch Pose Selected: " + launchposeSelected);
            telemetry.addLine("");
            telemetry.addLine("Circle: Start Over");
            telemetry.addLine(String.valueOf(gamepad1));

            if (gamepad1.dpad_left) {
                launchpose = LaunchPose.Primary;
                launchposeSelected = "Primary";
                telemetry.addLine(launchposeSelected);
                if(alliance == Alliance.HiveUp) {
                    LaunchingPose = NLZLaunchPosePrimary;
                    LaunchingPose2 = LZLaunchPosePrimary;
                }
                if(alliance == Alliance.HiveDown) {
                    LaunchingPose = LZLaunchPosePrimary;
                    LaunchingPose2 = NLZLaunchPosePrimary;
                }
            }
            else if (gamepad1.dpad_right) {
                launchpose = LaunchPose.Secondary;
                launchposeSelected = "Secondary";
                telemetry.addLine(launchposeSelected);
                if(alliance == Alliance.HiveUp) {
                    LaunchingPose = NLZLaunchPoseSecondary;
                    LaunchingPose2 = LZLaunchPoseSecondary;
                }
                if(alliance == Alliance.HiveDown) {
                    LaunchingPose = LZLaunchPoseSecondary;
                    LaunchingPose2 = NLZLaunchPoseSecondary;
                }
            } else if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
                startpose = StartPose.Unknown;
                launchpose = LaunchPose.Unknown;
                allianceSelected = "";
                locationSelected = "";
                startposeSelected = "";
                launchposeSelected = "";
                runBuild = false;
            }
        } else {

            if (alliance != Alliance.UNKNOWN &&
                    location != AutoStartLocation.UNKNOWN &&
                    launchpose != LaunchPose.Unknown &&
                    startpose != StartPose.Unknown) {

                if (!runBuild) {
                    telemetry.addLine("Building autonomous paths");

                    buildPaths();
                    follower.setPose(StartingPose);

                    runBuild = true;
                }
            }


            // Reset selection
            if (gamepad1.circle) {
                alliance = Alliance.UNKNOWN;
                location = AutoStartLocation.UNKNOWN;
                startpose = StartPose.Unknown;
                launchpose = LaunchPose.Unknown;

                allianceSelected = "";
                locationSelected = "";
                startposeSelected = "";
                launchposeSelected = "";

                runBuild = false;
            }


            if (runBuild) {
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
    @Override
    public void stop() {
        OpModeStorage.LaunchingPose = follower.pose(); //saves your position in that file
        OpModeStorage.Park = follower.pose();
    }
}