package org.firstinspires.ftc.teamcode.Threads;

import android.transition.Slide;

import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.ScoreSubsystem;
import org.firstinspires.ftc.teamcode.Subsystems.SlideSubsystem;

public class ScoreThread extends Thread {
    SlideSubsystem slideSubsystem;
    ScoreSubsystem scoreSubsystem;


    public int slideLevel;

    public ScoreThread(SlideSubsystem slideSubsystem, ScoreSubsystem scoreSubsystem, int slideLevel) {
        this.slideSubsystem = slideSubsystem;
        this.scoreSubsystem = scoreSubsystem;
        this.slideLevel = slideLevel;

        this.setPriority(MIN_PRIORITY);
        this.setDaemon(true);

    }

    public void run() {
        slideSubsystem.setLevelTicks(slideLevel);
        try {
            sleep(300);
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
