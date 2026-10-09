package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoArm
{
    private Servo servoArm;

    public void init(HardwareMap hwMap){
        // Initialize your servo in hardware map
        servoArm = hwMap.get(Servo.class, "servo_arm");
        this.toHome();
    }

    // Start feeder servo
    public void toHome()
    {
        double homePosition = 0.5;
        servoArm.setPosition(homePosition);
    }

    // Stop feeder servo
    public void deployedPosition()
    {
        double DeployedPosition = 1.0;
        servoArm.setPosition(DeployedPosition);
    }
}