package org.usfirst.frc.team1732.robot.commands.testing;

import org.usfirst.frc.team1732.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TestVelocityFollowing extends Command {

	private double leftVel, rightVel;

	public TestVelocityFollowing(double leftVel, double rightVel) {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.drivetrain);
		this.leftVel = leftVel;
		this.rightVel = rightVel;
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		Robot.drivetrain.velocityGains.selectGains(Robot.drivetrain.leftMaster, Robot.drivetrain.rightMaster);
		Robot.drivetrain.leftMaster.set(ControlMode.Velocity, Robot.drivetrain.convertVelocitySetpoint(leftVel));
		Robot.drivetrain.rightMaster.set(ControlMode.Velocity, Robot.drivetrain.convertVelocitySetpoint(rightVel));
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		System.out.println("left error: " + Robot.drivetrain.leftMaster.getClosedLoopError(0));
		System.out.println("right error: " + Robot.drivetrain.rightMaster.getClosedLoopError(0));
		// System.out.println("left target: " +
		// Robot.drivetrain.leftTalon1.getClosedLoopTarget(0));
		// System.out.println("right target: " +
		// Robot.drivetrain.rightTalon1.getClosedLoopTarget(0));
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		Robot.drivetrain.setStop();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
	}
}
