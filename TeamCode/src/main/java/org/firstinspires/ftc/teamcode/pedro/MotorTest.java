package org.firstinspires.ftc.teamcode.pedro;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "MotorTest")
public class MotorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotor Motor_LF = hardwareMap.get(DcMotor.class, "Motor_LF");
        DcMotor Motor_RF = hardwareMap.get(DcMotor.class, "Motor_RF");
        DcMotor Motor_LR = hardwareMap.get(DcMotor.class, "Motor_LR");
        DcMotor Motor_RR = hardwareMap.get(DcMotor.class, "Motor_RR");

        waitForStart();

        if (opModeIsActive()) {

            // Drive forward
            Motor_LF.setPower(1);
            Motor_RF.setPower(-1);
            Motor_LR.setPower(1);
            Motor_RR.setPower(-1);

            sleep(3000);

            // Stop
            Motor_LF.setPower(0);
            Motor_RF.setPower(0);
            Motor_LR.setPower(0);
            Motor_RR.setPower(0);
        }
    }
}