package org.firstinspires.ftc.teamcode.Subsystems;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.Constants;

import java.util.function.BooleanSupplier;

public class SlideSubsystem extends SubsystemBase {
    public Motor slideMotorLeft;
    public Motor slideMotorRight;
    public BooleanSupplier isInterrupted;
    private PIDFController pidfLeftSlideMotor;
    private PIDFController pidfRightSlideMotor;
    private double[] pidfCoefficientsExtend;
    private double[] pidfCoefficientsRetract;
    public double calculateLeft;
    public double calculateRight;
    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    public boolean isAuto;
    public boolean isScoring = false;

    public SlideSubsystem(Motor slideMotorLeft, Motor slideMotorRight, Telemetry telemetry, HardwareMap hardwareMap, boolean resetEncoders) {
        this.slideMotorLeft = slideMotorLeft;
        this.slideMotorRight = slideMotorRight;
        this.hardwareMap = hardwareMap;

        this.slideMotorLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.slideMotorRight.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        this.slideMotorLeft.setRunMode(Motor.RunMode.RawPower);
        this.slideMotorRight.setRunMode(Motor.RunMode.RawPower);

        this.slideMotorRight.setInverted(true);

        if(resetEncoders) {
            this.slideMotorLeft.resetEncoder();
            this.slideMotorRight.resetEncoder();
        }

        this.telemetry = telemetry;
    }

    /**
     * Sets the extension level for the slides using a PID.
     * @param level Intended level for slide extension, in millimeters.
     */
    public void setLevel(double level) {
        Constants.SLIDE_INPUT_STATE = Constants.InputState.PRESET_POSITIONS;

        pidfCoefficientsExtend  = new double[]{Constants.SLIDE_EXTEND_PIDF_COEFF.p, Constants.SLIDE_EXTEND_PIDF_COEFF.i, Constants.SLIDE_EXTEND_PIDF_COEFF.d, Constants.SLIDE_EXTEND_PIDF_COEFF.f};
        pidfCoefficientsRetract = new double[]{Constants.SLIDE_RETRACT_PIDF_COEFF.p, Constants.SLIDE_RETRACT_PIDF_COEFF.i, Constants.SLIDE_RETRACT_PIDF_COEFF.d, Constants.SLIDE_RETRACT_PIDF_COEFF.f};

        if(ticksToMeters(slideMotorLeft.getCurrentPosition()) < level) {
            telemetry.addData("1", ticksToMeters(slideMotorLeft.getCurrentPosition()) - level);
            pidfRightSlideMotor = new PIDFController(pidfCoefficientsExtend[0], pidfCoefficientsExtend[1], pidfCoefficientsExtend[2], pidfCoefficientsExtend[3]);
            pidfLeftSlideMotor  = new PIDFController(pidfCoefficientsExtend[0], pidfCoefficientsExtend[1], pidfCoefficientsExtend[2], pidfCoefficientsExtend[3]);
        }
        else {
            telemetry.addData("2", ticksToMeters(slideMotorLeft.getCurrentPosition()) - level);
            pidfRightSlideMotor = new PIDFController(pidfCoefficientsRetract[0], pidfCoefficientsRetract[1], pidfCoefficientsRetract[2], pidfCoefficientsRetract[3]);
            pidfLeftSlideMotor  = new PIDFController(pidfCoefficientsRetract[0], pidfCoefficientsRetract[1], pidfCoefficientsRetract[2], pidfCoefficientsRetract[3]);
        }

        pidfRightSlideMotor.setSetPoint(level);
        pidfLeftSlideMotor.setSetPoint(level);

        pidfRightSlideMotor.setTolerance(Constants.SLIDE_ALLOWED_ERROR);
        pidfLeftSlideMotor.setTolerance(Constants.SLIDE_ALLOWED_ERROR);

        pidfRightSlideMotor.setIntegrationBounds(-0.1, 0.1);
        pidfLeftSlideMotor.setIntegrationBounds(-0.1, 0.1);

        while(!pidfLeftSlideMotor.atSetPoint() && !pidfRightSlideMotor.atSetPoint()) {
            calculateRight = pidfRightSlideMotor.calculate(ticksToMeters(slideMotorRight.getCurrentPosition()));
            calculateLeft = pidfLeftSlideMotor.calculate(ticksToMeters(slideMotorLeft.getCurrentPosition()));

            slideMotorRight.set(calculateRight);
            slideMotorLeft.set(calculateLeft);

            telemetry.addData("slideMotorLeft", ticksToMeters(slideMotorLeft.getCurrentPosition()));
            telemetry.addData("slideMotorRight", ticksToMeters(slideMotorRight.getCurrentPosition()));
            telemetry.update();

            try {
                sleep(10);
            } catch (InterruptedException e) {
                Constants.SLIDE_INPUT_STATE = Constants.InputState.MANUAL_CONTROL;
                slideMotorLeft.set(Constants.SLIDE_MOTOR_PASSIVE_POWER * (12 / hardwareMap.getAll(VoltageSensor.class).get(0).getVoltage()));
                slideMotorRight.set(Constants.SLIDE_MOTOR_PASSIVE_POWER * (12 / hardwareMap.getAll(VoltageSensor.class).get(0).getVoltage()));

                e.printStackTrace();
            }
        }

        if (level >= 0.05) {
            slideMotorLeft.set(Constants.SLIDE_MOTOR_PASSIVE_POWER * (12 / hardwareMap.getAll(VoltageSensor.class).get(0).getVoltage()));
            slideMotorRight.set(Constants.SLIDE_MOTOR_PASSIVE_POWER * (12 / hardwareMap.getAll(VoltageSensor.class).get(0).getVoltage()));
        } else {
            slideMotorLeft.set(0);
            slideMotorRight.set(0);
        }

        Constants.SLIDE_INPUT_STATE = Constants.InputState.MANUAL_CONTROL;
    }

    public double getPassivePower() {
        if(slideMotorLeft.getCurrentPosition() <= 0.05) {
            return 0;
        }

        return Constants.SLIDE_MOTOR_PASSIVE_POWER * (double) ticksToMeters(slideMotorLeft.getCurrentPosition())/(double)Constants.SLIDE_MAX_EXTENSION_METERS;
    }

    public void setMotorPower( double power ) {
        slideMotorLeft.set(power);
        slideMotorRight.set(power);
    }

    public void resetEnc() {
        slideMotorLeft.resetEncoder();
        slideMotorRight.resetEncoder();
    }

    public double ticksToMeters(int ticks) {
        return (double) ticks / Constants.SLIDE_MAX_EXTENSION_TICKS * Constants.SLIDE_MAX_EXTENSION_METERS;
    }

    public double metersToTicks(double meters) {
        return Math.round(meters / Constants.SLIDE_MAX_EXTENSION_METERS * Constants.SLIDE_MAX_EXTENSION_TICKS);
    }

    public int getMotorTicks() {
        return slideMotorLeft.getCurrentPosition();
    }

    public double getSlideExtensionMeters() {
        return ticksToMeters(getMotorTicks());
    }

    public void setLevelTicks(int ticks) {
        double meters = ticksToMeters(ticks);
        setLevel(meters);  // reutilizezi metoda existentă
    }


//    public void scoreHigh() {
//        setLevel(Constants.SLIDE_HIGH);
//
//    } // Sets slide level to High Junction
//    public void scoreMid() {
//        setLevel(Constants.SLIDE_MID);
//    } // Sets slide level to Mid Junction
//    public void scoreLow() {
//        setLevel(Constants.SLIDE_LOW);
//    } // Sets slide level to Low Junction
}
