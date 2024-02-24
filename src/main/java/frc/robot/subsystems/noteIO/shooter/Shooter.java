package frc.robot.subsystems.noteIO.shooter;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

public class Shooter extends SubsystemBase {
    ShooterIO io;
    ShooterIOInputsAutoLogged inputs = new ShooterIOInputsAutoLogged();

    public Shooter(ShooterIO io) {
        this.io = io;
    }

    public void start(double velocity) {
        io.setVelocity(velocity);
    }

    public void stop() {
        io.setVelocity(0);
    }

    @Override
    public void periodic() {
        io.updateInputs(inputs);
        Logger.processInputs("shooter", inputs);
    }

    public boolean checkCompleteShot() {
        // kinda placeholder thing idk if shooter should always run for 5s or something
        // to detect if its gone
        return inputs.motorCurrent > 15 && inputs.motorVelocity > (4000 / 60) * (inputs.motorVoltage / 12);
    }
}