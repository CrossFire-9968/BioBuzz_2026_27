package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake
{

    public DcMotor Intake;

    public void init(HardwareMap hwMap)
    {
        Intake = hwMap.get(DcMotor.class, "Intake");
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.stop();
    }

    public void intakeSpeed(double power) {
        Intake.setPower(power);
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