package org.usfirst.frc.team1732.robot.config.robots;

import org.usfirst.frc.team1732.robot.config.RobotConfig;

public class PracticeConfig extends RobotConfig {

	{
		// drivetrain
		effectiveRobotWidth = robotWidth; // calculate
		drivetrainInchesPerPulse = 0; // calculate
		maxUnitsPer100Ms = 0; // measure
		// drivetrainConfig. Change stuff like this:
		drivetrainConfig.enableVoltageCompensation = true;
		// change PID values like this:
		drivetrainVelocityPID.kP = 0;
		drivetrainVelocityPID.kI = 0;
		drivetrainVelocityPID.kD = 0;
		drivetrainVelocityPID.integralZone = 0;
		drivetrainVelocityPID.allowableError = 0;
		drivetrainVelocityPID.maxIntegralAccumulated = 0;
		drivetrainVelocityPID.secondsFromNeutralToFull = 0;

		// arm
		armConfig.enableVoltageCompensation = true;
		armUpPID.kP = 0.0001;
		armUpPID.kI = 0;
		armUpPID.kD = 0;
		armUpPID.integralZone = 0;
		armUpPID.allowableError = 0;
		armUpPID.maxIntegralAccumulated = 0;
		armUpPID.secondsFromNeutralToFull = 0;
		armDownPID.kP = 0.0001;
		armDownPID.kI = 0;
		armDownPID.kD = 0;
		armDownPID.integralZone = 0;
		armDownPID.maxIntegralAccumulated = 0;
		armDownPID.secondsFromNeutralToFull = 0;

		// elevator
		elevatorConfig.enableVoltageCompensation = true;
		elevatorUpPID.kP = 0;
		elevatorUpPID.kI = 0;
		elevatorUpPID.kD = 0;
		elevatorUpPID.integralZone = 0;
		elevatorUpPID.allowableError = 0;
		elevatorUpPID.maxIntegralAccumulated = 0;
		elevatorUpPID.secondsFromNeutralToFull = 0;
		elevatorDownPID.kP = 0;
		elevatorDownPID.kI = 0;
		elevatorDownPID.kD = 0;
		elevatorDownPID.integralZone = 0;
		elevatorDownPID.maxIntegralAccumulated = 0;
		elevatorDownPID.secondsFromNeutralToFull = 0;

		// climber
		climberConfig.enableVoltageCompensation = true;

		// manip
		manipConfig.enableVoltageCompensation = true;
		manipConfig.continousCurrentLimit = 0; // figure out how talon current limiting works
		manipStopCurrent = 0;
	}

}
