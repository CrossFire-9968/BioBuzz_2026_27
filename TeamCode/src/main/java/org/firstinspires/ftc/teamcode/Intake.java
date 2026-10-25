package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake
{

    public DcMotor Intake;
   // public DcMotor Intake2;

    public DcMotor IntakeServo1;

    public DcMotor IntakeServo2;


    public void init(HardwareMap hwMap)
    {
        Intake = hwMap.get(DcMotor.class, "Intake");
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        IntakeServo1 = hwMap.get(DcMotor.class, "Launcher1");
        IntakeServo1.setDirection(DcMotorSimple.Direction.FORWARD);
        IntakeServo1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        IntakeServo2 = hwMap.get(DcMotor.class, "Launcher1");
        IntakeServo2.setDirection(DcMotorSimple.Direction.FORWARD);
        IntakeServo2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //Intake2 = hwMap.get(DcMotor.class, "Intake2");
        //Intake2.setDirection(DcMotorSimple.Direction.FORWARD);
        //Intake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.stop();
    }

    public void intakeSpeed(double power) {
        Intake.setPower(power);
        //Intake2.setPower(power);
    }
    public void intake() {
        Intake.setPower(-1);
        IntakeServo1.setPower(-1);
        IntakeServo2.setPower(-1);
        //Intake2.setPower(-1);
    }
    public void outake() {
        Intake.setPower(1);
        //Intake2.setPower(1);
    }
    public void stoptake() {
        
        Intake.setPower(0);
        IntakeServo1.setPower(0);
        IntakeServo2.setPower(0);
       // Intake2.setPower(0);
    }



   /* public void noPinchSpeed() {
        double pinchPower = -0.3;
        Intake.setPower(pinchPower);
    }*/

    /*public void yeetPower(double power) {
        Intake.setPower(power);
    }*/

    public void stop() {
        double stopPower = 0.0;
        Intake.setPower(stopPower);
    }
}