
package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.teamcode.Commands.DriveCommand;
import org.firstinspires.ftc.teamcode.Commands.SlideManualCommand;
import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Constants.HardwareConstants;
import org.firstinspires.ftc.teamcode.Subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.ScoreSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.Threads.IntakeThread;
import org.firstinspires.ftc.teamcode.Threads.OuttakeThread;
import org.firstinspires.ftc.teamcode.Threads.ScoreReleaseThread;
import org.firstinspires.ftc.teamcode.Threads.ScoreThread;

@TeleOp
public class TeleOpMain extends CommandOpMode {
    private Motor lf;
    private Motor lb;
    private Motor rf;
    private Motor rb;
    private Motor slideMotorLeft;
    private Motor slideMotorRight;

    private Servo rotate;
    private Servo armL;
    private Servo armR;
    private Servo claw;

    private DriveSubsystem driveSubsystem;
    private ScoreSubsystem scoreSubsystem;
    private SlideSubsystem slideSubsystem;

    private DriveCommand driveCommand;
    private SlideManualCommand slideManualCommand;

    private IntakeThread intakeThread;
    private ScoreReleaseThread scoreReleaseThread;
    private OuttakeThread outtakeThread;
    private ScoreThread scoreThread;

    private GamepadEx driver1;

    private Consumer scoreThreadExecutor;

    private InstantCommand scoring;
    private InstantCommand scoreRelease;
    private InstantCommand clawToggle;
    private InstantCommand highJunction;
    private InstantCommand midJunction;
    private InstantCommand lowJunction;
    public int slideLevel;

    private int level = 2;
    private boolean clawClosed = false;


    @Override
    public void initialize() {

        Constants.SLIDE_INPUT_STATE = Constants.InputState.MANUAL_CONTROL;

        lf = new Motor(hardwareMap, HardwareConstants.ID_LEFT_FRONT_MOTOR);
        lb = new Motor(hardwareMap, HardwareConstants.ID_LEFT_BACK_MOTOR);
        rf = new Motor(hardwareMap, HardwareConstants.ID_RIGHT_FRONT_MOTOR);
        rb = new Motor(hardwareMap, HardwareConstants.ID_RIGHT_BACK_MOTOR);

        slideMotorLeft = new Motor(hardwareMap, HardwareConstants.ID_SLIDE_MOTOR_LEFT);
        slideMotorRight = new Motor(hardwareMap, HardwareConstants.ID_SLIDE_MOTOR_RIGHT);

        armL = hardwareMap.get(Servo.class, HardwareConstants.ID_ARM_LEFT);
        armR = hardwareMap.get(Servo.class, HardwareConstants.ID_ARM_RIGHT);
        rotate = hardwareMap.get(Servo.class, HardwareConstants.ID_ROTATE);
        claw = hardwareMap.get(Servo.class, HardwareConstants.ID_CLAW);

        driveSubsystem = new DriveSubsystem(lf, lb, rf, rb);
        scoreSubsystem = new ScoreSubsystem(armL, armR, rotate, claw, false);
        slideSubsystem = new SlideSubsystem(slideMotorLeft, slideMotorRight, FtcDashboard.getInstance().getTelemetry(), hardwareMap, true);

        driver1 = new GamepadEx(gamepad1);

        driveCommand = new DriveCommand(driveSubsystem, driver1::getLeftX, driver1::getLeftY, driver1::getRightX);
        slideManualCommand = new SlideManualCommand(slideSubsystem, () -> driver1.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER), () -> driver1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER));

        intakeThread = new IntakeThread(scoreSubsystem);
        scoreThread = new ScoreThread(slideSubsystem, scoreSubsystem, slideLevel);
        scoreReleaseThread = new ScoreReleaseThread(slideSubsystem, scoreSubsystem);

        scoreThread.setPriority(Thread.MIN_PRIORITY);
        scoreReleaseThread.setPriority(Thread.MIN_PRIORITY);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        scoreThreadExecutor = (levelForSlides) -> {
            ScoreThread thread = new ScoreThread(slideSubsystem, scoreSubsystem, slideLevel);
            thread.slideLevel = (int) levelForSlides;
            thread.start();
//            scoreThread.slideLevel = (int) levelForSlides;
//            scoreThread.interrupt();
//            scoreThread.start();
        };

        InstantCommand clawToggle = new InstantCommand(() -> {
            clawClosed = !clawClosed;
            scoreSubsystem.useClaw(clawClosed ? Constants.CLAW_CLOSE : Constants.CLAW_OPEN);
        });

        InstantCommand score = new InstantCommand(() -> scoreThread.start());

        InstantCommand scoreRelease = new InstantCommand(() -> {
            new ScoreReleaseThread(slideSubsystem, scoreSubsystem).start();
        });
        InstantCommand scoreTest = new InstantCommand(() -> {
            scoreThreadExecutor.accept(Constants.SLIDE_HIGH);
        });

//        InstantCommand scoreTest = new InstantCommand(() -> scoreThreadExecutor.accept(Constants.SLIDE_HIGH));
//        InstantCommand scoreRelease = new InstantCommand(() -> scoreReleaseThread.start());

        new GamepadButton(driver1, GamepadKeys.Button.A).whenPressed(clawToggle);
        new GamepadButton(driver1, GamepadKeys.Button.B).whenPressed(scoreTest); // Goes to High Junction
        new GamepadButton(driver1, GamepadKeys.Button.X).whenPressed(new InstantCommand(() -> scoreThreadExecutor.accept(Constants.SLIDE_MID))); // Goes to Mid Junction
        new GamepadButton(driver1, GamepadKeys.Button.Y).whenPressed(new InstantCommand(() -> slideSubsystem.setLevel(Constants.SLIDE_LOW))); // Goes to Low Junction
        new GamepadButton(driver1, GamepadKeys.Button.RIGHT_BUMPER).whenPressed(scoreRelease);
        new GamepadButton(driver1, GamepadKeys.Button.DPAD_RIGHT).whenPressed(() -> slideSubsystem.resetEnc());
        driveSubsystem.setDefaultCommand(driveCommand);
        slideSubsystem.setDefaultCommand(slideManualCommand);
    }
}
