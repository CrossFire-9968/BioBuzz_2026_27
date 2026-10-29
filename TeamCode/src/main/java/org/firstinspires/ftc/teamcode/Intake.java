package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class Intake
{

    public DcMotor Intake;
   // public DcMotor Intake2;

    public CRServo IntakeServo1;

    public CRServo IntakeServo2;


    public void init(HardwareMap hwMap)
    {
        Intake = hwMap.get(DcMotor.class, "Intake");
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        IntakeServo1 = hwMap.get(CRServo.class, "IntakeServo1");
        IntakeServo1.setDirection(CRServo.Direction.FORWARD);

        IntakeServo2 = hwMap.get(CRServo.class, "IntakeServo2");
        IntakeServo2.setDirection(CRServo.Direction.FORWARD);




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
        Intake.setPower(1);
        IntakeServo1.setPower(-1);
        IntakeServo2.setPower(1);

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