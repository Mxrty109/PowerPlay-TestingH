
package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.hardware.motors.Motor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Constants.HardwareConstants;

@Config
@TeleOp
public class TeleOpSimple extends LinearOpMode {
    private DcMotor lf = null;
    private DcMotor rf = null;
    private DcMotor lb = null;
    private DcMotor rb = null;
    private Motor slideMotorLeft;
    private Motor slideMotorRight;


    private Servo armL;
    private Servo armR;
    private Servo claw;
    private Servo rotate;

    public static double clawPos = 0;
    public static double rotatePos = 0;
    public static double armLPos = 0;
    public static double armRPos = 0;



    double lfPower;
    double rfPower;
    double lbPower;
    double rbPower;

    YawPitchRollAngles orientation;

    IMU imu;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        lf = hardwareMap.get(DcMotor.class, HardwareConstants.ID_LEFT_FRONT_MOTOR);
        rf = hardwareMap.get(DcMotor.class, HardwareConstants.ID_RIGHT_FRONT_MOTOR);
        lb = hardwareMap.get(DcMotor.class, HardwareConstants.ID_LEFT_BACK_MOTOR);
        rb = hardwareMap.get(DcMotor.class, HardwareConstants.ID_RIGHT_BACK_MOTOR);

        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rf.setDirection(DcMotor.Direction.REVERSE);
        rb.setDirection(DcMotor.Direction.REVERSE);
        lf.setDirection(DcMotor.Direction.FORWARD);
        lb.setDirection(DcMotor.Direction.FORWARD);

        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slideMotorRight = new Motor(hardwareMap, HardwareConstants.ID_SLIDE_MOTOR_RIGHT);
        slideMotorLeft = new Motor(hardwareMap, HardwareConstants.ID_SLIDE_MOTOR_LEFT);

        slideMotorRight.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        slideMotorLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        slideMotorRight.setInverted(true);

        claw = hardwareMap.get(Servo.class, HardwareConstants.ID_CLAW);
        rotate = hardwareMap.get(Servo.class, HardwareConstants.ID_ROTATE);
        armL = hardwareMap.get(Servo.class, HardwareConstants.ID_ARM_LEFT);
        armR = hardwareMap.get(Servo.class, HardwareConstants.ID_ARM_RIGHT);

        claw.setPosition(0);
        rotate.setPosition(0);
        armL.setPosition(0);
        armR.setPosition(0);

        waitForStart();

        while(opModeIsActive()) {

            armL.setPosition(armLPos);
            armR.setPosition(armLPos);
            rotate.setPosition(rotatePos);
            claw.setPosition(clawPos);

            double drive = gamepad1.left_stick_y;
            double strafe = -gamepad1.left_stick_x;
            double turn = -gamepad1.right_stick_x;

            lfPower = Range.clip(drive + turn + strafe, -1.0, 1.0);
            rfPower = Range.clip(drive - turn - strafe, -1.0, 1.0);
            lbPower = Range.clip(drive + turn - strafe, -1.0, 1.0);
            rbPower = Range.clip(drive - turn + strafe, -1.0, 1.0);

            lf.setPower(lfPower);
            rf.setPower(rfPower);
            lb.setPower(lbPower);
            rb.setPower(rbPower);

            slideMotorLeft.set(gamepad1.right_trigger - gamepad1.left_trigger);
            slideMotorRight.set(gamepad1.right_trigger - gamepad1.left_trigger);

            telemetry.addData("Claw Servo", claw.getPosition());
            telemetry.addData("Rotate Servo", rotate.getPosition());
            telemetry.addData("Arm Left Servo", armL.getPosition());
            telemetry.addData("Arm Right Servo", armR.getPosition());
            telemetry.addData("Slide Motor Left", slideMotorLeft.getCurrentPosition());
            telemetry.addData("Slide Motor Right", slideMotorRight.getCurrentPosition());
            telemetry.update();


        }
    }
}
