package org.firstinspires.ftc.teamcode.Threads;

import android.transition.Slide;

import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.ScoreSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.SlideSubsystem;

public class ScoreThread extends Thread {
    SlideSubsystem slideSubsystem;
    ScoreSubsystem scoreSubsystem;
    private int level;


    public double slideLevel = 0.0;

    public ScoreThread(SlideSubsystem slideSubsystem, ScoreSubsystem scoreSubsystem) {
        this.slideSubsystem = slideSubsystem;
        this.scoreSubsystem = scoreSubsystem;

        this.setPriority(MIN_PRIORITY);
        this.setDaemon(true);

    }

    public void run() {
        slideSubsystem.setLevel(slideLevel);
        scoreSubsystem.useRotate(Constants.ROTATE_SCORE);
        try {
            sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        scoreSubsystem.useArm(Constants.ARM_SCORE);

    }

    public void interrupt() {
        Constants.SLIDE_INPUT_STATE = Constants.InputState.MANUAL_CONTROL;
        super.interrupt();
    }
}
