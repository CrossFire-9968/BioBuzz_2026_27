package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Launcher {

    public Intake intake = new Intake();

    public DcMotor Launcher1;
    public DcMotor Launcher2;
    double TargetSpeedPollen = 5;

    double FireSpeed = -0.05;
    double TargetSpeedNecter = 165.0;

    public CRServo CarouselServo;

    public CRServo IntakeServo1;

    public CRServo IntakeServo2;

    //
    public void init(HardwareMap hwMap)
    {
        Launcher1 = hwMap.get(DcMotor.class, "Launcher1");
        Launcher1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Launcher1.setDirection(DcMotor.Direction.REVERSE);

        CarouselServo = hwMap.get(CRServo.class, "CarouselServo");
        CarouselServo.setDirection(DcMotorSimple.Direction.FORWARD);

        IntakeServo1 = hwMap.get(CRServo.class, "IntakeServo1");
        IntakeServo1.setDirection(CRServo.Direction.FORWARD);

        IntakeServo2 = hwMap.get(CRServo.class, "IntakeServo2");
        IntakeServo2.setDirection(CRServo.Direction.FORWARD);








    //    PIDCoefficients pidNew = new PIDCoefficients(NEW_P, NEW_I, NEW_D, NEW_F)
        this.stop();
    }

    //Manually turn on the launcher
    public void LauncherSpeed(double speed) {
//        Launcher1.setVelocity(speed, AngleUnit.DEGREES);
//        Launcher1.setPower(FireSpeed);

        //Launcher2.setPower(power);
    }
    //This is the function to launch the Pollen
    public void LaunchPollen() {
        CarouselServo.setPower(1);
        IntakeServo1.setPower(-1);
        IntakeServo2.setPower(1);
        Launcher1.setPower(FireSpeed);



        //Launcher2.setPower(-1);
    }

    public void SpoolUpAndLaunch() {


        Launcher1.setPower(FireSpeed);

        //if (Launcher1.getVelocity(AngleUnit.DEGREES) > TargetSpeedPollen){

        //}
        //return Launcher1.getVelocity(AngleUnit.DEGREES);
    }

    public void SpoolUp() {
        Launcher1.setPower(FireSpeed);

    }

    //This is the function to launch the Nectar
    public void LaunchNectar() {
        Launcher1.setPower(FireSpeed);

        //Launcher2.setPower(1);
    }
    //This is the function to stop the launcher
    public void LaunchOff() {
        Launcher1.setPower(0);
        CarouselServo.setPower(0);
        //IntakeServo1.setPower(0);
        //IntakeServo2.setPower(0);
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
        //Launcher1.setPower(stopPower);
       // Launcher2.setPower(stopPower);
    }
}
