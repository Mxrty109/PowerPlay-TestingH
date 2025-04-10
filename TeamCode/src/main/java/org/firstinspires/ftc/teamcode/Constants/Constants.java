package org.firstinspires.ftc.teamcode.Constants;

//import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/**
 * A class containing all constants for subsystems. <br><br>
 * This allows us to share constants across OP modes, and makes it easier to change them.
 */

@Config
public class Constants {
    public static boolean ROBOT_STOPPED = false;
    public enum InputState {
        MANUAL_CONTROL,
        PRESET_POSITIONS;

    }

    public static InputState SLIDE_INPUT_STATE = InputState.MANUAL_CONTROL;



    //Slide Subsystem
    public static final double SLIDE_INIT = 0.0;
    public static final double SLIDE_LOW= 0.0;
    public static final double SLIDE_MID= 0.0;
    public static final double SLIDE_HIGH= 0.0;

    public static double SLIDE_MANUAL_CONTROL_MAX = 0.98;
    public static int SLIDE_MAX_EXTENSION_TICKS = 0;
    public static double SLIDE_MAX_EXTENSION_METERS = 0.0;
    public static double SLIDE_MOTOR_PASSIVE_POWER = 0.0;
    public static double SLIDE_ALLOWED_ERROR = 0.00;
    public static PIDFCoefficients SLIDE_RETRACT_PIDF_COEFF = new PIDFCoefficients(0, 0, 0, 0.2);
    public static PIDFCoefficients SLIDE_EXTEND_PIDF_COEFF = new PIDFCoefficients(0, 0, 0, 0);

    // Score Subsystem
    public static final double CLAW_INIT = 0.0;
    public static final double CLAW_OPEN = 0.0;
    public static final double CLAW_CLOSE = 0.0;
    public static final double ROTATE_INIT = 0.0;
    public static final double ROTATE_INTAKE = 0.0;
    public static final double ROTATE_SCORE = 0.0;

    public static final double ARM_INIT = 0.0;
    public static final double ARM_INTAKE = 0.0;
    public static final double ARM_SCORE = 0.0;




}