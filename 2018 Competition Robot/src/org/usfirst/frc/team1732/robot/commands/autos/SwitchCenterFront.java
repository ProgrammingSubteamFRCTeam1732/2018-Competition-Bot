package org.usfirst.frc.team1732.robot.commands.autos;

import org.usfirst.frc.team1732.robot.Robot;
import org.usfirst.frc.team1732.robot.autotools.DriverStationData;
import org.usfirst.frc.team1732.robot.commands.primitive.FollowVelocityPath;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SwitchCenterFront extends CommandGroup {

	public SwitchCenterFront() {
		// addParallel(new PreAuto(Arm.Positions.TUCK));

		if (DriverStationData.closeSwitchIsLeft) {
			addSequential(new FollowVelocityPath(Robot.paths.switchCenterFrontLeftProfile));
		} else {
			addSequential(new FollowVelocityPath(Robot.paths.switchCenterFrontStraightProfile));
		}
		// addParallel(new CommandGroup() {
		// {
		// addSequential(new Wait(2));
		// addSequential(new ManipAutoEject(0.7));
		// }
		// });
		// addSequential(new DriveTime(0.3, 0.3, NeutralMode.Coast, 4));
	}
}