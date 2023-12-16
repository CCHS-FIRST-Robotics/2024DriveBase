package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drive extends SubsystemBase{
    private Module[] modules;
    private ChassisSpeeds robotSpeeds;
    private Rotation2d robotAngle;
    private SwerveModuleState moduleState;

    public Drive(){
        robotSpeeds = new ChassisSpeeds();
        modules = new Module[4];
        for(int i = 0; i < modules.length; i++){
            modules[i] = new Module();
        }
    }

    public void swerveThatShi(SwerveModuleState whatWeWant){
        moduleState = whatWeWant;
    }

    @Override
    public void periodic(){
        // uhh maybe later?
        // robotSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(goalpos, robotAngle);
        // setState();
        for(int i = 0; i < modules.length; i++){
            modules[i].periodic();
            modules[i].driveMotors(moduleState);
        }
    }

    // Foobar (Jk kinda):
    public void setState(ChassisSpeeds relativeFieldPosition, double robotAngle){
        robotSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(relativeFieldPosition, new Rotation2d(robotAngle));
        moduleState.speedMetersPerSecond = Math.sqrt(
            robotSpeeds.vxMetersPerSecond * robotSpeeds.vxMetersPerSecond + 
            robotSpeeds.vyMetersPerSecond * robotSpeeds.vyMetersPerSecond);
        Rotation2d turning = new Rotation2d(
            Math.asin(robotSpeeds.vxMetersPerSecond / moduleState.speedMetersPerSecond), 
            Math.acos(robotSpeeds.vyMetersPerSecond / moduleState.speedMetersPerSecond));
        moduleState.angle = moduleState.angle.rotateBy(turning);

    }

}