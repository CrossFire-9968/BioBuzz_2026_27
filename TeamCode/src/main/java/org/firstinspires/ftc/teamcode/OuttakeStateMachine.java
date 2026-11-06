package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "OuttakeStateMachine", group = "Robot")
//@Disabled
public class OuttakeStateMachine extends OpMode {

    public DcMotor Launcher1;
    public DcMotor Launcher2;
    double TargetSpeedPollen = 5;

    double FireSpeed = -0.05;
    double TargetSpeedNecter = 165.0;

    public Servo CarouselServo;

    public CRServo IntakeServo1;

    public CRServo IntakeServo2;

    public enum State {
        ServoWheels,
        FlipUp,
        FlipDown
    }

    private final double t1;
    private final double t2;
    private final double t3;

    private double stateTime = 0.0;
    private State currentState = State.ServoWheels;

    long now = System.nanoTime();


    long lastLoopTime = System.nanoTime();
    double dt = (now - lastLoopTime);

    public OuttakeStateMachine(double t1, double t2, double t3){
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    double CarouselPos1 = 0.25;
    double CarouselPos2 = 0.75;

    @Override
    public void init() {

        Launcher1 = hardwareMap.get(DcMotor.class, "Launcher1");
        Launcher1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Launcher1.setDirection(DcMotor.Direction.REVERSE);

        CarouselServo = hardwareMap.get(Servo.class, "CarouselServo");

        IntakeServo1 = hardwareMap.get(CRServo.class, "IntakeServo1");
        IntakeServo1.setDirection(CRServo.Direction.FORWARD);

        IntakeServo2 = hardwareMap.get(CRServo.class, "IntakeServo2");
        IntakeServo2.setDirection(CRServo.Direction.FORWARD);

    }

    @Override
    public void loop() {
        if (gamepad2.left_bumper) {
            UpdateState();
        }
    }

    public void UpdateState() {
        now = System.nanoTime();
        dt = (now - lastLoopTime) / 1e9;
        lastLoopTime = now;

        stateTime += dt;

        switch(currentState) {
            case ServoWheels:
                IntakeServo1.setPower(-1);
                IntakeServo2.setPower(1);
                CarouselServo.setPosition(CarouselPos1);
                if(stateTime >= t1) {
                    currentState = State.FlipUp;
                    stateTime = 0;
                }
                break;

            case FlipUp:
                IntakeServo1.setPower(0);
                IntakeServo2.setPower(0);
                CarouselServo.setPosition(CarouselPos2);
                if(stateTime >= t2) {
                    currentState = State.FlipDown;
                    stateTime = 0;
                }
                break;

            case FlipDown:
                IntakeServo1.setPower(0);
                IntakeServo2.setPower(0);
                CarouselServo.setPosition(CarouselPos1);
                if(stateTime >= t3) {
                    currentState = State.ServoWheels;
                    stateTime = 0;
                }
                break;
        }
    }
}

