
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LauncherTest {

    public DcMotor Launcher1;
    public DcMotor Launcher2;

    public Servo CarouselServo;

    public CRServo IntakeServo1;
    public CRServo IntakeServo2;

    private final double t1;
    private final double t2;
    private final double t3;

    private double FireSpeed = 0.7;

    private final double CarouselPos1 = 0.25;
    private final double CarouselPos2 = 0.4;

    public enum State {
        ServoWheels,
        FlipUp,
        FlipDown
    }

    private State currentState = State.ServoWheels;

    private double stateTime = 0.0;

    private long lastLoopTime;

    public LauncherTest(double t1, double t2, double t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public void init(HardwareMap hwMap) {

        Launcher1 = hwMap.get(DcMotor.class, "Launcher1");
        Launcher1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Launcher1.setDirection(DcMotor.Direction.REVERSE);

        CarouselServo = hwMap.get(Servo.class, "CarouselServo");

        IntakeServo1 = hwMap.get(CRServo.class, "IntakeServo1");
        IntakeServo1.setDirection(CRServo.Direction.FORWARD);

        IntakeServo2 = hwMap.get(CRServo.class, "IntakeServo2");
        IntakeServo2.setDirection(CRServo.Direction.FORWARD);

        lastLoopTime = System.nanoTime();
    }

    public void LaunchPollen() {

        long now = System.nanoTime();

        double dt = (now - lastLoopTime) / 1e9;

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

    public void SpoolUp() {
        Launcher1.setPower(FireSpeed);
    }

    public void SpoolUpAndLaunch() {
        Launcher1.setPower(FireSpeed);
    }

    public void LaunchNectar() {
        Launcher1.setPower(FireSpeed);
    }

    public void LaunchOff() {

        Launcher1.setPower(0);

        IntakeServo1.setPower(0);
        IntakeServo2.setPower(0);

        CarouselServo.setPosition(CarouselPos1);

        currentState = State.ServoWheels;
        stateTime = 0;

        lastLoopTime = System.nanoTime();
    }

    public void stop() {
        LaunchOff();
    }
}
