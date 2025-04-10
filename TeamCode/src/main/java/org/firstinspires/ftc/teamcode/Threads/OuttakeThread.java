package org.firstinspires.ftc.teamcode.Threads;

import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.ScoreSubsystem;

public class OuttakeThread extends Thread {
    ScoreSubsystem scoreSubsystem;

    public OuttakeThread(ScoreSubsystem scoreSubsystem) {
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

    }
}
