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
    public Launcher launcher = new Launcher(1.2,0.5,0.5);
    private String debugStep = "NOT STARTED";
    public Intake intake = new Intake();
    double advanceAmount = 1;
    double IntakeCount = 0;
    private Command activeAutoCommand;
    private boolean nextSegmentRequested = false;
    private boolean switchAfterIntake = false;


    //tries to fix path inconsistency
    private Command followFully(Path path) {
        return Command.build()
                .setStart(() -> follower.follow(path))
                .setDone(() -> !follower.following());
    }


    //Swaps Launching Poses
    private void swapLaunchingPoses() {

        Pose temp = LaunchingPose;

        LaunchingPose = LaunchingPose2;

        LaunchingPose2 = temp;

        Pose temp2 = SCCP1;

        SCCP1 = SCCP2;

        SCCP2 = temp2;
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

    private AutoSegment availableIntake() {

        if (cycleState == CycleState.Cycle1) {

            if (humanPlayerIntake == HumanPlayerIntake.UNUSED)
                return AutoSegment.IntakeHP;

            if (closeFlowerIntake == CloseFlowerIntake.UNUSED)
                return AutoSegment.IntakeNLZFlower;

            if (farFlowerIntake == FarFlowerIntake.UNUSED)
                return AutoSegment.IntakeLZFlower;

        } else if (cycleState == CycleState.Cycle2) {

            if (humanPlayerIntake == HumanPlayerIntake.UNUSED)
                return AutoSegment.IntakeHP;

            if (farFlowerIntake == FarFlowerIntake.UNUSED)
                return AutoSegment.IntakeLZFlower;

            if (closeFlowerIntake == CloseFlowerIntake.UNUSED)
                return AutoSegment.IntakeNLZFlower;

        } else if (cycleState == CycleState.Cycle3) {

            if (closeFlowerIntake == CloseFlowerIntake.UNUSED)
                return AutoSegment.IntakeNLZFlower;

            if (farFlowerIntake == FarFlowerIntake.UNUSED)
                return AutoSegment.IntakeLZFlower;

            if (humanPlayerIntake == HumanPlayerIntake.UNUSED)
                return AutoSegment.IntakeHP;
        }

        return null;
    }

    private void decideNextIntake() {
        // Cycle 3 has no fourth cycle to switch into.
        if (cycleState == CycleState.Cycle3 && advanceAmount != 1) {
            changeSegment(AutoSegment.Park);
            return;
        }

        AutoSegment nextIntake = availableIntake();

        if (advanceAmount != 1) {
            // Collect before switching; take the elements across the field.
            if (nextIntake != null) {
                switchAfterIntake = true;
                changeSegment(nextIntake);
            } else {
                switchAfterIntake = false;
                changeSegment(AutoSegment.SwitchSide);
            }
        } else {
            switchAfterIntake = false;
            changeSegment(nextIntake != null ? nextIntake : AutoSegment.Park);
        }
    }

    private void onIntakeFinished() {
        if (switchAfterIntake && cycleState != CycleState.Cycle3) {
            switchAfterIntake = false;
            changeSegment(AutoSegment.SwitchSide);
        } else {
            switchAfterIntake = false;
            changeSegment(AutoSegment.Launch);
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

            if (humanPlayerIntake == HumanPlayerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeHP);

            } else if (farFlowerIntake == FarFlowerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeLZFlower);

            } else if (closeFlowerIntake == CloseFlowerIntake.UNUSED) {
                changeCycle(nextCycle, AutoSegment.IntakeNLZFlower);

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
    private Pose NLZstartPosePrimary = poseFactory.of(9.5908, 34.0771,  0);
    private Pose NLZstartPoseSecondary = poseFactory.of(33.9378, 8.7899, 90);
    private Pose LZstartPosePrimary = poseFactory.of(72.6809, 27.9969, 180);
    private Pose LZstartPoseSecondary = poseFactory.of(33.4306, 133.0461, 270);


    // NLZ specific poses:
    private Pose HPintake = poseFactory.of(9, 9, 180);
    private Pose NLZFlower = poseFactory.of(10.3708, 46.3227, 180);
    private Pose NLZLaunchPoseSecondary = poseFactory.of(42.5077, 16.6774, 90);
    private Pose NLZLaunchPosePrimary = poseFactory.of(34, 8.5, 43.2);


    // LZ specific poses:
    private Pose LZLaunchPoseSecondary = poseFactory.of(120, 120, 315);
    private Pose LZLaunchPosePrimary = poseFactory.of(119.6363, 116.8849, 270);
    private Pose LZFlower = poseFactory.of(46.7427, 120, 90);


    // General poses:
    private Pose Park = poseFactory.of(16, 92.926, 0);


    // Control points:
    private Pose ParkCP = poseFactory.of(32.9054, 68.2875, 0);
    private Pose startPoseCP = poseFactory.of(5.7094, 73.2003, 180);
    private Pose LZLaunchPosePrimary_LZFlower_CP = poseFactory.of(45.1712, 110.954, 90);
    private Pose LZLaunchPosePrimary_HPIntake_CP = poseFactory.of(52.6482, 4.0845, 180);
    private Pose LZLaunchPosePrimary_HPIntake_CP2 = poseFactory.of(0, 141.5, 180);
    private Pose LZLaunchPosePrimary_NLZFlower_CP1 = poseFactory.of(7.8399, 125.5386, 270);
    private Pose LZLaunchPosePrimary_NLZFlower_CP2 = poseFactory.of(47.9496, 38.7858, 180);
    private Pose LZLaunchPoseSecondary_LZFlower_CP = poseFactory.of(46.9151, 111.7013, 90);
    private Pose NLZLaunchPosePrimary_NLZFlower_CP = poseFactory.of(27.982, 22.7656, 180);
    private Pose NLZLaunchPosePrimary_LZFlower_CP = poseFactory.of(62.98, 101.3146, 90);
    private Pose NLZLaunchPoseSecondary_NLZFlower_CP = poseFactory.of(36.7012, 40.9513, 180);
    private Pose NLZLaunchPosePrimary_HPintake_CP = poseFactory.of(57.2321, 10, 180);
    private Pose LZLaunchPoseSecondary_HPIntake_CP = poseFactory.of(40.6105, 3.2279, 180);
    private Pose LZLaunchPoseSecondary_NLZFlower_CP1 = poseFactory.of(38.2093, 45.4206, 90);
    private Pose NLZLaunchPoseSecondary_HPintake_CP = poseFactory.of(34.9573, 5.8255, 180);
    private Pose NLZLaunchPoseSecondary_LZLaunchPoseSecondary_CP = poseFactory.of(8.0774, 70.0668, 90);
    private Pose NLZLaunchPoseSecondary_LZLaunchPosePrimary_CP = poseFactory.of(61.8373, 20.0262, 90);
    private Pose NLZLaunchPosePrimary_LZLaunchPoseSecondary_CP = poseFactory.of(29.0404, 32.3673, 90);
    private Pose SwitchSidesCp1 = poseFactory.of(131.6524, 24.9164, 90);
    private Pose SwitchSidesCp2 = poseFactory.of(4.0257, 114.158, 90);
    private Pose SCCP1 = SwitchSidesCp1;
    private Pose SCCP2 = SwitchSidesCp2;


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
        return line(LaunchingPose, HPintake).linear(LaunchingPose, HPintake);
    }
    Path NLZLaunchingPose_NLZFlower() {
        return curve(LaunchingPose, NLZLaunchPosePrimary_NLZFlower_CP, NLZFlower).heading(Interpolator.piecewise().until(0.5,Interpolator.linear(LaunchingPose, NLZLaunchPosePrimary_NLZFlower_CP)).until(1,Interpolator.linear(NLZLaunchPosePrimary_NLZFlower_CP, NLZFlower)));
    }
    Path NLZLaunchingPose_LZFlower() {
        return curve(LaunchingPose, NLZLaunchPosePrimary_LZFlower_CP, LZFlower).linear(LaunchingPose, LZFlower);
    }


    //Returns:
    Path HPIntake_NLZLaunchingPose() {
        return line(HPintake, LaunchingPose).linear(HPintake,LaunchingPose);
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
        return curve(LaunchingPose, LZLaunchPosePrimary_HPIntake_CP,LZLaunchPosePrimary_HPIntake_CP2, HPintake).linear(LaunchingPose, HPintake);
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
        return curve(LaunchingPose, SCCP1,SCCP2, LaunchingPose2).linear(LaunchingPose, LaunchingPose2);
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
    public enum HumanPlayerIntake {UNUSED,USED,UNKNOWN}
    public enum CloseFlowerIntake {UNUSED,USED,UNKNOWN}
    public enum FarFlowerIntake {UNUSED,USED,UNKNOWN}
    public enum IntakeState {ElementsContained, NoElements}





    // Establish variables to interface with the enums:
    public Alliance alliance = Alliance.UNKNOWN;
    public AutoStartLocation location = AutoStartLocation.UNKNOWN;
    public StartPose startpose = StartPose.Unknown;
    public LaunchPose launchpose = LaunchPose.Unknown;
    public HumanPlayerIntake humanPlayerIntake = HumanPlayerIntake.UNKNOWN;
    public CloseFlowerIntake closeFlowerIntake = CloseFlowerIntake.UNKNOWN;
    public FarFlowerIntake farFlowerIntake = FarFlowerIntake.UNKNOWN;
    public IntakeState intakeState = IntakeState.ElementsContained;


    // Misc:
    boolean runBuild = false;
    public static String allianceSelected = "";
    public static String locationSelected = "";
    public static String startposeSelected = "";
    public static String launchposeSelected = "";
    public static String humanplayerintakeselected = "";
    public static String closeflowerintakeselected = "";
    public static String farflowerintakeselected = "";

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

    Command spoolUp = instant(() -> launcher.SpoolUpAndLaunch(160.8));
    Command spoolOff = instant(() -> launcher.LaunchOff());


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
                                    instant(() -> debugStep = "SPOOLING"),
                                    spoolUp,

                                    instant(() -> debugStep = "FOLLOWING FIRST PATH"),
                                    follow(follower, StartingPose_LaunchingPose()),

                                    instant(() -> {
                                        debugStep = "FIRST PATH COMPLETE";
                                        changeSegment(AutoSegment.Launch);
                                    })
                            );
                        case Launch:
                            return sequential(
                                    instant(() -> debugStep = "LAUNCH ENTERED"),
                                    //setIntake,
                                    launcher.LaunchPollenAuto(6000),
                                    launcher.LaunchPollenAuto(0),
                                    launcher.LaunchPollenAuto(0),
                                    launcher.LaunchPollenAuto(0),
                                    //intakeOff,
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
                                    instant(this::onIntakeFinished)
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
                                    instant(this::onIntakeFinished)
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
                                    instant(this::onIntakeFinished)
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
                                    follow(follower, NLZLaunchingPose_Park()),
                                    spoolOff
                            );

                    }
                    break;


                case Cycle2:
                    switch (autoSegment) {
                        case Launch:
                            return sequential(
                                    setIntake,
                                    launcher.LaunchPollenAuto(2000),
                                    intakeOff,
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
                                    instant(this::onIntakeFinished)
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
                                    instant(this::onIntakeFinished)
                            );
                        case IntakeHP:
                            return sequential(
                                    setIntake,
                                    follow(follower, LZLaunchingPose_HPIntake()),
                                    follow(follower, HPIntake_LZLaunchingPose()),
                                    intakeOff,
                                    MarkOffHPIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(this::onIntakeFinished)
                            );
                        case Park:
                            return sequential(
                                    follow(follower, LZLaunchingPose_Park()),
                                    spoolOff
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
                                    setIntake,
                                    launcher.LaunchPollenAuto(2000),
                                    intakeOff,
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
                                    instant(this::onIntakeFinished)
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
                                    instant(this::onIntakeFinished)
                            );
                        case IntakeHP:
                            return sequential(
                                    setIntake,
                                    follow(follower, NLZLaunchingPose_HPIntake()),
                                    follow(follower, HPIntake_NLZLaunchingPose()),
                                    intakeOff,
                                    MarkOffHPIntake,
                                    instant(() -> intakeState = IntakeState.ElementsContained),
                                    instant(this::onIntakeFinished)
                            );
                        case Park:
                            return sequential(
                                    follow(follower, NLZLaunchingPose_Park()),
                                    spoolOff
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

        if (!runBuild ||
                StartingPose == null ||
                LaunchingPose == null ||
                LaunchingPose2 == null) {

            telemetry.addLine("ERROR: Auto setup incomplete!");
            telemetry.update();
            return;
        }

        follower.setPose(StartingPose);
        // Primary launch: selected during INIT
        OpModeStorage.LaunchingPose = LaunchingPose;

// Secondary TeleOp launch: fixed coordinates
        OpModeStorage.LaunchingPoseSecondary =
                poseFactory.of(37.5, 124,313);

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
        telemetry.addData("Pinpoint Heading", Math.toDegrees(follower.pose().heading()));

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
        telemetry.addData("Debug Step", debugStep);
        telemetry.addData("Segment", autoSegment);
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.addData("Following", follower.following());

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData(
                "Heading",
                Math.toDegrees(follower.pose().heading())
        );

        telemetry.addData(
                "Distance to Launch",
                Math.hypot(
                        follower.pose().x() - LaunchingPose.x(),
                        follower.pose().y() - LaunchingPose.y()
                )
        );

        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }


    // Init Loop: Runs the selection in init.

    @Override
    public void init_loop() {
        super.init_loop();
        telemetry.clearAll();

        // RESET ALL SELECTIONS
        if (gamepad1.circle) {
            alliance = Alliance.UNKNOWN;
            location = AutoStartLocation.UNKNOWN;
            startpose = StartPose.Unknown;
            launchpose = LaunchPose.Unknown;

            humanPlayerIntake = HumanPlayerIntake.UNKNOWN;
            closeFlowerIntake = CloseFlowerIntake.UNKNOWN;
            farFlowerIntake = FarFlowerIntake.UNKNOWN;

            allianceSelected = "";
            locationSelected = "";
            startposeSelected = "";
            launchposeSelected = "";

            humanplayerintakeselected = "";
            closeflowerintakeselected = "";
            farflowerintakeselected = "";

            StartingPose = null;
            LaunchingPose = null;
            LaunchingPose2 = null;

            runBuild = false;

            telemetry.addLine("AUTO SETUP RESET");
            telemetry.update();
            return;
        }

        telemetry.addLine("===== AUTO SETUP =====");

        // 1. ALLIANCE / HIVE
        if (alliance == Alliance.UNKNOWN) {
            telemetry.addLine("STEP 1/7: SELECT HIVE");
            telemetry.addLine("DPAD UP: HiveUp");
            telemetry.addLine("DPAD DOWN: HiveDown");

            if (gamepad1.dpad_up) {
                alliance = Alliance.HiveUp;
                allianceSelected = "HiveUp";
            } else if (gamepad1.dpad_down) {
                alliance = Alliance.HiveDown;
                allianceSelected = "HiveDown";
            }
        }

        // 2. START LOCATION
        else if (location == AutoStartLocation.UNKNOWN) {
            telemetry.addLine("STEP 2/7: SELECT START LOCATION");
            telemetry.addLine("DPAD LEFT: Red");
            telemetry.addLine("DPAD RIGHT: Blue");

            if (gamepad1.dpad_left) {
                location = AutoStartLocation.Red;
                locationSelected = "Red";
            } else if (gamepad1.dpad_right) {
                location = AutoStartLocation.Blue;
                locationSelected = "Blue";
            }
        }

        // 3. STARTING POSE
        else if (startpose == StartPose.Unknown) {
            telemetry.addLine("STEP 3/7: SELECT STARTING POSE");
            telemetry.addLine("DPAD UP: Primary");
            telemetry.addLine("DPAD DOWN: Secondary");

            if (gamepad1.dpad_up) {
                startpose = StartPose.Primary;
                startposeSelected = "Primary";

                StartingPose = (alliance == Alliance.HiveUp)
                        ? NLZstartPosePrimary
                        : LZstartPosePrimary;

            } else if (gamepad1.dpad_down) {
                startpose = StartPose.Secondary;
                startposeSelected = "Secondary";

                StartingPose = (alliance == Alliance.HiveUp)
                        ? NLZstartPoseSecondary
                        : LZstartPoseSecondary;
            }
        }

        // 4. LAUNCHING POSE
        else if (launchpose == LaunchPose.Unknown) {
            telemetry.addLine("STEP 4/7: SELECT LAUNCHING POSE");
            telemetry.addLine("DPAD LEFT: Primary");
            telemetry.addLine("DPAD RIGHT: Secondary");

            if (gamepad1.dpad_left) {
                launchpose = LaunchPose.Primary;
                launchposeSelected = "Primary";

                if (alliance == Alliance.HiveUp) {
                    LaunchingPose = NLZLaunchPosePrimary;
                    LaunchingPose2 = LZLaunchPosePrimary;
                } else {
                    LaunchingPose = LZLaunchPosePrimary;
                    LaunchingPose2 = NLZLaunchPosePrimary;
                }

            } else if (gamepad1.dpad_right) {
                launchpose = LaunchPose.Secondary;
                launchposeSelected = "Secondary";

                if (alliance == Alliance.HiveUp) {
                    LaunchingPose = NLZLaunchPoseSecondary;
                    LaunchingPose2 = LZLaunchPoseSecondary;
                } else {
                    LaunchingPose = LZLaunchPoseSecondary;
                    LaunchingPose2 = NLZLaunchPoseSecondary;
                }
            }
        }

        // 5. HUMAN PLAYER INTAKE
        else if (humanPlayerIntake == HumanPlayerIntake.UNKNOWN) {
            telemetry.addLine("STEP 5/7: HUMAN PLAYER INTAKE");
            telemetry.addLine("DPAD UP: Use / Open");
            telemetry.addLine("DPAD DOWN: Skip / Closed");

            if (gamepad1.dpad_up) {
                humanPlayerIntake = HumanPlayerIntake.UNUSED;
                humanplayerintakeselected = "Open";
            } else if (gamepad1.dpad_down) {
                humanPlayerIntake = HumanPlayerIntake.USED;
                humanplayerintakeselected = "Closed";
            }
        }

        // 6. CLOSE FLOWER INTAKE
        else if (closeFlowerIntake == CloseFlowerIntake.UNKNOWN) {
            telemetry.addLine("STEP 6/7: CLOSE FLOWER INTAKE");
            telemetry.addLine("DPAD LEFT: Use / Open");
            telemetry.addLine("DPAD RIGHT: Skip / Closed");

            if (gamepad1.dpad_left) {
                closeFlowerIntake = CloseFlowerIntake.UNUSED;
                closeflowerintakeselected = "Open";
            } else if (gamepad1.dpad_right) {
                closeFlowerIntake = CloseFlowerIntake.USED;
                closeflowerintakeselected = "Closed";
            }
        }

        // 7. FAR FLOWER INTAKE
        else if (farFlowerIntake == FarFlowerIntake.UNKNOWN) {
            telemetry.addLine("STEP 7/7: FAR FLOWER INTAKE");
            telemetry.addLine("DPAD UP: Use / Open");
            telemetry.addLine("DPAD DOWN: Skip / Closed");

            if (gamepad1.dpad_up) {
                farFlowerIntake = FarFlowerIntake.UNUSED;
                farflowerintakeselected = "Open";
            } else if (gamepad1.dpad_down) {
                farFlowerIntake = FarFlowerIntake.USED;
                farflowerintakeselected = "Closed";
            }
        }

        // ALL SELECTIONS COMPLETE
        else {
            if (!runBuild) {
                buildPaths();
                follower.setPose(StartingPose);
                runBuild = true;
            }

            telemetry.addLine("");
            telemetry.addLine("===== READY FOR PLAY =====");

            telemetry.addData("Start X", StartingPose.x());
            telemetry.addData("Start Y", StartingPose.y());
            telemetry.addData(
                    "Start Heading",
                    Math.toDegrees(StartingPose.heading())
            );

            telemetry.addData("Launch X", LaunchingPose.x());
            telemetry.addData("Launch Y", LaunchingPose.y());
            telemetry.addData(
                    "Launch Heading",
                    Math.toDegrees(LaunchingPose.heading())
            );
        }

        // ALWAYS DISPLAY CURRENT SELECTIONS
        telemetry.addLine("");
        telemetry.addLine("===== CURRENT SELECTIONS =====");

        telemetry.addData("Hive", allianceSelected.isEmpty()
                ? "Not selected" : allianceSelected);

        telemetry.addData("Start Location", locationSelected.isEmpty()
                ? "Not selected" : locationSelected);

        telemetry.addData("Starting Pose", startposeSelected.isEmpty()
                ? "Not selected" : startposeSelected);

        telemetry.addData("Launching Pose", launchposeSelected.isEmpty()
                ? "Not selected" : launchposeSelected);

        telemetry.addData("HP Intake", humanplayerintakeselected.isEmpty()
                ? "Not selected" : humanplayerintakeselected);

        telemetry.addData("Close Flower", closeflowerintakeselected.isEmpty()
                ? "Not selected" : closeflowerintakeselected);

        telemetry.addData("Far Flower", farflowerintakeselected.isEmpty()
                ? "Not selected" : farflowerintakeselected);

        telemetry.addLine("");
        telemetry.addLine("CIRCLE: Reset all selections");

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
        // Save only where the robot actually ended
        OpModeStorage.Park = follower.pose();
    }
}