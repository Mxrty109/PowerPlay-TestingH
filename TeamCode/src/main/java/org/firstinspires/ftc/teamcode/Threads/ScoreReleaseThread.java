package org.firstinspires.ftc.teamcode.Threads;

import android.transition.Slide;

import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.ScoreSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.SlideSubsystem;

public class ScoreReleaseThread extends Thread {
    SlideSubsystem slideSubsystem;
    ScoreSubsystem scoreSubsystem;

    public ScoreReleaseThread(SlideSubsystem slideSubsystem, ScoreSubsystem scoreSubsystem) {
        this.slideSubsystem = slideSubsystem;
        this.scoreSubsystem = scoreSubsystem;

        this.setPriority(MIN_PRIORITY);
        this.setDaemon(true);

    }

    public void run() {
        scoreSubsystem.useClaw(Constants.CLAW_OPEN);
        try {
            sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        scoreSubsystem.useRotate(Constants.ROTATE_INTAKE);
        scoreSubsystem.useArm(Constants.ARM_INTAKE);
        slideSubsystem.setLevel(0);
    }

    public void interrupt() {
        Constants.SLIDE_INPUT_STATE = Constants.InputState.MANUAL_CONTROL;
        super.interrupt();
    }
}
