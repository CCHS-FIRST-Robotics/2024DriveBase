package frc.robot.subsystems.noteIO.arm;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants;
import frc.robot.Constants.AutoPathConstants;
import frc.robot.utils.DriveTrajectory;

import static edu.wpi.first.units.Units.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;

import com.choreo.lib.Choreo;
import com.choreo.lib.ChoreoTrajectory;
import com.choreo.lib.ChoreoTrajectoryState;
import com.ctre.phoenix6.SignalLogger;

import edu.wpi.first.units.*;

// rev sucks
public class Arm extends SubsystemBase {


    private final ArmIO io;
    private final ArmIOInputsAutoLogged inputs = new ArmIOInputsAutoLogged();
    private SysIdRoutine sysIdRoutine;
    private SignalLogger signalLogger;
    // length and position of the arm in relation to the robot's center
    private final double armLength = 0.0; // TODO: set this
    private final Translation2d armOffset = new Translation2d(0.0, .425); // TODO: set this 

    // added for auto stuff maybe
    private Supplier<Translation2d> translationToTargetGround;
    private Supplier<Pose3d> targetPose;

    public Arm(ArmIO io) {
        this.io = io;
        sysIdRoutine = new SysIdRoutine(
            new SysIdRoutine.Config(), 
            new SysIdRoutine.Mechanism(
            (Measure<Voltage> volts) -> {
                io.setDriveVoltage(volts);
            },
            null, this
        ));
        SignalLogger.start();
    }

    /** Updates inputs and checks tunable numbers. */
    public void periodic() {
        io.updateInputs(inputs);
        Logger.processInputs("Arm", inputs);
        SignalLogger.writeDouble("Position", this.getArmAngle().in(Radians));
        SignalLogger.writeDouble("Velocity", this.getArmVelocity().in(RadiansPerSecond));
        // trust!
        // io.setDriveVoltage(Volts.of(1));
        // setArmAngle(Degrees.of(90));

        Logger.recordOutput("Arm Angle", getArmAngle());
    }

    public void setArmAngle(Measure<Angle> angle) {
        io.setDrivePosition(angle);
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

    // // used to set stuff for suto events
    // public void setCurrentArmPos(Supplier<Translation2d> translationToTargetGround, Supplier<Pose3d> targetPose) {
    //     this.translationToTargetGround = translationToTargetGround;
    //     this.targetPose = targetPose;
    // }

    public Command alignWithTarget(Supplier<Translation2d> translationToTargetGround, Supplier<Pose3d> targetPose) {
        return run(() -> {
            Translation2d armOffset = getArmOffset();
            Translation2d tranlationToTargetHigh = new Translation2d(translationToTargetGround.get().getNorm(), targetPose.get().getZ());
            Rotation2d targetArmAngle = tranlationToTargetHigh.minus(armOffset).getAngle();
            setArmAngle(Radians.of(Math.PI/2.0 - targetArmAngle.getRadians())); // add 90 degrees since 0 is vertical
        });
    }

    public Command getPosFromPath(String path, double eventTime) {
        ChoreoTrajectory choreoTrajectory = Choreo.getTrajectory(path);

        double timeToEnd = choreoTrajectory.getTotalTime();

        for (int i = 0; i < (int) (timeToEnd / Constants.PERIOD) + 2; i++) {
            double time = i * Constants.PERIOD;
            ChoreoTrajectoryState state = choreoTrajectory.sample(time);

            if (time >= eventTime) {
                // so this is not the ideal way to do it with time but i feel like you could do something simialr w/out time
                // especially if the em are not using time anymore (which still needs to be switched whoops)
                Translation2d translationToTargetGround = new Translation2d(state.x, state.y);
                Pose3d targetPose = new Pose3d(new Pose2d(state.x, state.y, new Rotation2d(state.heading)));
                return run(() -> {
                    Translation2d armOffset = getArmOffset();
                    Translation2d tranlationToTargetHigh = new Translation2d(translationToTargetGround.getNorm(), targetPose.getZ());
                    Rotation2d targetArmAngle = tranlationToTargetHigh.minus(armOffset).getAngle();
                    setArmAngle(Radians.of(Math.PI/2.0 - targetArmAngle.getRadians())); // add 90 degrees since 0 is vertical
                });
            }
        }

        return null;
    }

    public Command getMoveAngleCommand(Measure<Angle> angle) {
        return run(() -> {
            setArmAngle(angle);
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