package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com. qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Launcher {

    public Intake intake = new Intake();

    public DcMotorEx Launcher1;
    public DcMotorEx Launcher2;
    double TargetSpeedPollen = 165.0;
    double TargetSpeedNecter = 165.0;

    public DcMotor CarouselServo;

    //
    public void init(HardwareMap hwMap)
    {
        Launcher1 = hwMap.get(DcMotorEx.class, "Launcher1");
        Launcher1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        Launcher1.setDirection(DcMotor.Direction.FORWARD);

        Launcher2 = hwMap.get(DcMotorEx.class, "Launcher2");
        Launcher2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Launcher2.setDirection(DcMotor.Direction.FORWARD);

        CarouselServo = hwMap.get(DcMotor.class, "Launcher1");
        CarouselServo.setDirection(DcMotorSimple.Direction.FORWARD);
        CarouselServo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



    //    PIDCoefficients pidNew = new PIDCoefficients(NEW_P, NEW_I, NEW_D, NEW_F)
        this.stop();
    }

    //Manually turn on the launcher
    public void LauncherSpeed(double speed) {
        Launcher1.setVelocity(speed, AngleUnit.DEGREES);
        Launcher1.setDirection(DcMotorSimple.Direction.FORWARD);
        //Launcher2.setPower(power);
    }
    //This is the function to launch the Pollen
    public void LaunchPollen() {
        Launcher1.setVelocity(TargetSpeedPollen, AngleUnit.DEGREES);
        Launcher1.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.intake();
        CarouselServo.setPower(-1);
        //Launcher2.setPower(-1);
    }
    //This is the function to launch the Nectar
    public void LaunchNectar() {
        Launcher1.setVelocity(TargetSpeedNecter, AngleUnit.DEGREES);
        Launcher1.setDirection(DcMotorSimple.Direction.FORWARD);
        //Launcher2.setPower(1);
    }
    //This is the function to stop the launcher
    public void LaunchOff() {
        Launcher1.setVelocity(0);
        //Launcher2.setPower(0);
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
        Launcher1.setPower(stopPower);
        Launcher2.setPower(stopPower);
    }
}
