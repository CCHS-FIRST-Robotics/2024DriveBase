package frc.robot.subsystems.vision;

import java.util.ArrayList;

import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.numbers.*;
import frc.robot.utils.AprilTag;
import frc.robot.utils.TimestampedPose2d;
import frc.robot.utils.TimestampedPose3d;
import edu.wpi.first.units.*;
import static edu.wpi.first.units.Units.*;

public interface CameraIO {
    public static class CameraIOInputs implements LoggableInputs {
        // Values from the primary (closest) tag
        public int primaryTagId = -1;
        public Measure<Distance> primaryTagX = Meters.of(-1);
        public Measure<Distance> primaryTagY = Meters.of(-1);
        public Measure<Distance> primaryTagZ = Meters.of(-1);
        public Measure<Angle> primaryTagRoll = Radians.of(-1);
        public Measure<Angle> primaryTagPitch = Radians.of(-1);
        public Measure<Angle> primaryTagHeading = Radians.of(-1);

        // Values for all tags found by the camera
        int numTags = 0;
        public ArrayList<AprilTag> tags = new ArrayList<AprilTag>();

        // Localization data
        TimestampedPose2d tagPoseEstimate = new TimestampedPose2d(new Pose2d(-1, -1, new Rotation2d(-1)), 0);
        TimestampedPose3d tagPoseEstimate3d = new TimestampedPose3d(new Pose3d(-1, -1, -1, new Rotation3d(-1, -1, -1)), 0);

        TimestampedPose3d zedPoseEstimate3d = new TimestampedPose3d(new Pose3d(-1, -1, -1, new Rotation3d(-1, -1, -1)), 0);
        TimestampedPose2d zedPoseEstimate = new TimestampedPose2d(new Pose2d(-1, -1, new Rotation2d(-1)), 0);
        Matrix<N3, N1> zedPoseCovar = VecBuilder.fill(0, 0, 0);

        /*
         * IMPLEMENTS LOGGABLE INPUTS MANUALLY (NOT AUTOLOG) TO LOG CUSTOM AprilTag OBJECTS
         */
        @Override
        public void toLog(LogTable table) {
            table.put("primaryTag/Id", primaryTagId);
            table.put("primaryTag/X", primaryTagX);
            table.put("primaryTag/Y", primaryTagY);
            table.put("primaryTag/Z", primaryTagZ);
            table.put("primaryTag/Roll", primaryTagRoll);
            table.put("primaryTag/Pitch", primaryTagPitch);
            table.put("primaryTag/Heading", primaryTagHeading);

            table.put("numTags", tags.size());
            for (int i = 0; i < tags.size(); i++) {
                AprilTag tag = tags.get(i);
                table.put("tag" + i + "/Id", tag.getId());
                table.put("tag" + i + "/Distance", tag.getDistance());
                table.put("tag" + i + "/Pose", tag.getPose2d());
                table.put("tag" + i + "/Pose3d", tag.getPose3d());
            }

            table.put("poseEstimate2d", tagPoseEstimate.pose);
            table.put("poseEstimate3d", tagPoseEstimate3d.pose);
            table.put("poseTimestampSeconds", Seconds.of(tagPoseEstimate.timestamp));

            table.put("zedPoseEstimate2d", zedPoseEstimate.pose);
            table.put("zedPoseEstimate3d", zedPoseEstimate3d.pose);
            table.put("zedPoseTimestampSeconds", Seconds.of(zedPoseEstimate.timestamp));
        }

        @Override
        public void fromLog(LogTable table) {
            primaryTagId = table.get("primaryTag/Id", primaryTagId);
            primaryTagX = table.get("primaryTag/X", primaryTagX);
            primaryTagY = table.get("primaryTag/Y", primaryTagY);
            primaryTagZ = table.get("primaryTag/Z", primaryTagZ);
            primaryTagRoll = table.get("primaryTag/Roll", primaryTagRoll);
            primaryTagPitch = table.get("primaryTag/Pitch", primaryTagPitch);
            primaryTagHeading = table.get("primaryTag/Heading", primaryTagHeading);

            numTags = table.get("numTags", numTags);
            for (int i = 0; i < numTags; i++) {
                int id = table.get("tag" + i + "/Id", -1);
                Pose3d pose = table.get("tag" + i + "/Pose3d", new Pose3d(-1, -1, -1, new Rotation3d(-1, -1, -1)));
                tags.add(new AprilTag(id, pose));
            }

            Pose3d pose = table.get("poseEstimate3d", tagPoseEstimate3d.pose);
            double poseTimestamp = table.get("poseTimestampSeconds", tagPoseEstimate3d.timestamp);
            tagPoseEstimate3d = new TimestampedPose3d(pose, poseTimestamp);
            tagPoseEstimate = new TimestampedPose2d(pose.toPose2d(), poseTimestamp);

            pose = table.get("zedPoseEstimate3d", zedPoseEstimate3d.pose);
            poseTimestamp = table.get("zedPoseTimestampSeconds", zedPoseEstimate3d.timestamp);
            zedPoseEstimate3d = new TimestampedPose3d(pose, poseTimestamp);
            zedPoseEstimate = new TimestampedPose2d(pose.toPose2d(), poseTimestamp);
        }
    }

    /** Updates the set of loggable inputs. */
    public default void updateInputs(CameraIOInputs inputs) {}
}
