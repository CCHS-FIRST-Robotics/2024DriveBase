package frc.robot.subsystems.swerveDrive;

import org.littletonrobotics.junction.AutoLog;

import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;

public interface ModuleIO {
    
    @AutoLog
    public static class ModuleIOInputs {
        public Measure<Angle> drivePositionRad = Radians.of(0.0);
        public Measure<Velocity<Angle>> driveVelocityRadPerSec = RadiansPerSecond.of(0.0);
        public Measure<Voltage> driveAppliedVolts = Volts.of(0.0);
        public Measure<Current> driveCurrentAmps = Amps.of(0);
        public Measure<Temperature> driveTempCelcius = Celsius.of(0);

        public Measure<Angle> turnAbsolutePositionRad = Radians.of(0.0);
        public Measure<Angle> turnPositionRad = Radians.of(0.0);
        public Measure<Velocity<Angle>> turnVelocityRadPerSec = RadiansPerSecond.of(0.0);
        public Measure<Voltage> turnAppliedVolts =Volts.of(0.0);
        public Measure<Current> turnCurrentAmps = Amps.of(0.0);
        public Measure<Temperature> turnTempCelcius = Celsius.of(0.0);
    }

    /** Updates the set of loggable inputs. */
    public default void updateInputs(ModuleIOInputs inputs) {}

    /** Run the drive motor at the specified voltage. */
    public default void setDriveVoltage(Measure<Voltage> volts) {}

    /** Run the turn motor at the specified voltage. */
    public default void setTurnVoltage(Measure<Voltage> volts) {}

    /** Run the drive motor at the specified velocity. */
    public default void setDriveVelocity(double velocityRadPerSec) {}

    /** Run the turn motor to the specified position. */
    public default void setTurnPosition(Measure<Angle> positionRad) {}

    /** Enable or disable brake mode on the drive motor. */
    public default void setDriveBrakeMode(boolean enable) {}

    /** Enable or disable brake mode on the turn motor. */
    public default void setTurnBrakeMode(boolean enable) {}
}
