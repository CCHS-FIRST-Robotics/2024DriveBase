package frc.robot.commands;

import frc.robot.Constants;

import frc.robot.subsystems.swerveDrive.*;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import java.util.function.Supplier;

public class DriveWithJoysticks extends CommandBase {

    Drive drive;
    Supplier<Double> linearXSpeedSupplier;
    Supplier<Double> linearYSpeedSupplier;
    Supplier<Double> angularSpeedSupplier;
    Supplier<Double> linearSpeedMultiplierSupplier;

    ChassisSpeeds prevSpeeds;
    
    public DriveWithJoysticks(
        Drive drive, 
        Supplier<Double> leftXSupplier, 
        Supplier<Double> leftYSupplier, 
        Supplier<Double> rightXSupplier,
        Supplier<Double> linearSpeedMultiplierSupplier
    ) {
        addRequirements(drive);
        this.drive = drive;
        linearXSpeedSupplier = leftXSupplier;
        linearYSpeedSupplier = leftYSupplier;
        angularSpeedSupplier = rightXSupplier;
        this.linearSpeedMultiplierSupplier = linearSpeedMultiplierSupplier;
    }

    @Override
    public void initialize() {
        prevSpeeds = new ChassisSpeeds();
    }

    @Override
    public void execute() {
        double linearXSpeed = linearXSpeedSupplier.get();
        double linearYSpeed = linearYSpeedSupplier.get();
        double linearSpeed = Math.hypot(linearXSpeed, linearYSpeed);
        Rotation2d linearDirection = new Rotation2d(linearXSpeed, linearYSpeed);
        
        double angularSpeed = angularSpeedSupplier.get();

        // TODO: switch constants to tunable numbers
        linearSpeed = applyPreferences(linearSpeed, Constants.LIENAR_SPEED_EXPONENT, Constants.ANALOG_DEADZONE);
        angularSpeed = applyPreferences(angularSpeed, Constants.ANGULAR_SPEED_EXPONENT, Constants.ANALOG_DEADZONE);

        linearSpeed *= linearSpeedMultiplierSupplier.get();

        // Calcaulate new linear components
        Translation2d linearVelocity = new Translation2d(linearSpeed, linearDirection);

        // Convert to meters per second
        ChassisSpeeds speeds =
            new ChassisSpeeds(
                linearVelocity.getX() * drive.getMaxLinearSpeedMetersPerSec(),
                linearVelocity.getY() * drive.getMaxLinearSpeedMetersPerSec(),
                angularSpeed * drive.getMaxAngularSpeedRadPerSec());

        // Desaturate speeds to max acceleration
        speeds =
        new ChassisSpeeds(
            MathUtil.clamp(
                speeds.vxMetersPerSecond,
                prevSpeeds.vxMetersPerSecond - drive.getMaxLinearAccelerationMetersPerSecPerSec() * Constants.PERIOD,
                prevSpeeds.vxMetersPerSecond + drive.getMaxLinearAccelerationMetersPerSecPerSec() * Constants.PERIOD),
            MathUtil.clamp(
                speeds.vyMetersPerSecond,
                prevSpeeds.vyMetersPerSecond - drive.getMaxLinearAccelerationMetersPerSecPerSec() * Constants.PERIOD,
                prevSpeeds.vyMetersPerSecond + drive.getMaxLinearAccelerationMetersPerSecPerSec() * Constants.PERIOD),
            speeds.omegaRadiansPerSecond
        );
        prevSpeeds = speeds;
        
        drive.runVelocity(speeds);
    }

    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }

    private double applyPreferences(double input, double exponent, double deadzone) {
        if (Math.abs(input) < deadzone) {
            return 0;
        }
        return Math.pow(Math.abs(input), exponent) * Math.signum(input);
    }
}