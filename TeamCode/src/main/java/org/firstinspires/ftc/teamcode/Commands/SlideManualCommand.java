
package org.firstinspires.ftc.teamcode.Commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Constants.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.SlideSubsystem;

import java.util.function.DoubleSupplier;

public class SlideManualCommand extends CommandBase {

    private SlideSubsystem slideSubsystem;
    private DoubleSupplier rightTrigger;
    private DoubleSupplier leftTrigger;

    public SlideManualCommand(SlideSubsystem slideSubsystem, DoubleSupplier rightTrigger, DoubleSupplier leftTrigger) {
        this.slideSubsystem = slideSubsystem;
        this.rightTrigger = rightTrigger;
        this.leftTrigger = leftTrigger;

        addRequirements(slideSubsystem);
    }

    @Override
    public void execute() {
        if (Constants.SLIDE_INPUT_STATE == Constants.InputState.MANUAL_CONTROL) {
            double rightTriggerOutput = (slideSubsystem.getSlideExtensionMeters() > Constants.SLIDE_MANUAL_CONTROL_MAX) ? slideSubsystem.getPassivePower() : Math.max(slideSubsystem.getPassivePower(), rightTrigger.getAsDouble());
            double leftTriggerOutput = leftTrigger.getAsDouble();
            double slidePower = rightTriggerOutput - leftTriggerOutput;

            slideSubsystem.setMotorPower(slidePower);
        }
    }
}
