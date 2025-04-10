package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants.Constants;

public class ScoreSubsystem extends SubsystemBase {
    private Servo armL;
    private Servo armR;
    private Servo rotate;
    private Servo claw;

    public ScoreSubsystem(Servo armL, Servo armR, Servo rotate, Servo claw, boolean isAuto) {
        this.armL = armL;
        this.armR = armR;
        this.rotate = rotate;
        this.claw = claw;

        this.armL.setPosition(Constants.ARM_INIT);
        this.armR.setPosition(Constants.ARM_INIT);
        this.rotate.setPosition(Constants.ROTATE_INIT);

        if (isAuto) {
            this.claw.setPosition(Constants.CLAW_CLOSE);
        } else {
            this.claw.setPosition(Constants.CLAW_OPEN);
        }
    }

    public void useArm(double pos) {
        armL.setPosition(pos);
        armR.setPosition(pos);
    }

    public void useRotate(double pos) {
        rotate.setPosition(pos);
    }

    public void useClaw(double pos) {
        claw.setPosition(pos);
    }
}
