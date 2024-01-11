package frc.robot.subsystems.arm;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.*;

import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.*;

public class Arm extends SubsystemBase {
    
    private final ArmIO io;
    private final ArmIOInputsAutoLogged inputs = new ArmIOInputsAutoLogged();

    // length and position of the arm in relation to the robot's center
    private final double armLength = 0.0; // TODO: set this
    private final Translation2d armOffset = new Translation2d(0.0, 0.0); // TODO: set this 

    public Arm(ArmIO io) {
        this.io = io;
    }

    /** Updates inputs and checks tunable numbers. */
    public void periodic() {
        io.updateInputs(inputs);
        Logger.processInputs("Arm", inputs);
        // io.setDriveVoltage(Volts.of(1));
        // setArmAngle(Degrees.of(90));
    }

    public void setArmAngle(Measure<Angle> angle) {
        // io.setDrivePosition(Degrees.of(90));
    }

    public Measure<Angle> getArmAngle() {
        return inputs.drivePosition;
    }

    public Measure<Velocity<Angle>> getArmVelocity() {
        return inputs.driveVelocity;
    }

    public Translation2d getArmOffset() {
        return armOffset;
    }

    public Translation2d getEndEffectorPosition() {
        return new Translation2d(armLength, new Rotation2d(getArmAngle().in(Radians))).plus(armOffset);
    }

    public Command alignWithTarget(Supplier<Translation2d> targetTranslation) {
        return run(() -> {
            Translation2d armOffset = getArmOffset();
            Rotation2d targetArmAngle = targetTranslation.get().minus(armOffset).getAngle();
            setArmAngle(Radians.of(targetArmAngle.getRadians()));
        });
    }

    public Command playMusic(String path) {
        return runOnce(
                () -> io.setMusicTrack(path)
            )
            .andThen(() -> {
                io.playMusic();
            });
    }
}