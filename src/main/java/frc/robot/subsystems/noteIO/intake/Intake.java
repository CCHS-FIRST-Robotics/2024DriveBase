package frc.robot.subsystems.noteIO.intake;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.littletonrobotics.junction.Logger;

// be able to intake (turns off automatically), shoot (moves towards shooter), and outtake

public class Intake extends SubsystemBase {
    IntakeIO io;
    double volts = 0;
    IntakeIOInputsAutoLogged inputs = new IntakeIOInputsAutoLogged();

    public Intake(IntakeIO io) {
        this.io = io;
    }

    public void start(double v) {
        volts = v;
    }

    public void stop() {
        volts = 0;
    }

    // probably should be private
    public boolean checkNoteThere() {
        if (inputs.motorCurrent > 15 && inputs.motorVelocity > 5000 * (volts / 12)) {
            return true;
        }
        return false;
        // there's this crazy thing called a boolean idk if youve heard of it
        return inputs.motorCurrent > 15 && inputs.motorVelocity > 5000 * (volts / 12);
    }

    @Override
    public void periodic() {
        io.updateInputs(inputs);
        Logger.processInputs("intake", inputs);

        io.setVoltage(volts);
    }

    public Command getIntakeCommand(double v) {
        // turns motor on until note detected
        return new FunctionalCommand(
                () -> start(v),
                () -> {
                },
                (interrupted) -> stop(),
                () -> checkNoteThere(),
                this);
    }
    
    public Command getShootCommand(double v) {
        // turns motor on until note not detected
        // ^ This might not work in reality because the current might drop under even though we still have the note in the intake
        return new FunctionalCommand(
                () -> start(v),
                () -> {
                },
                (interrupted) -> stop(),
                () -> !checkNoteThere(),
                this);
    }
}