package org.usfirst.frc.team1732.robot.commands.primitive;

import static org.usfirst.frc.team1732.robot.Robot.drivetrain;

import org.usfirst.frc.team1732.robot.Robot;
import org.usfirst.frc.team1732.robot.controlutils.ClosedLoopProfile;
import org.usfirst.frc.team1732.robot.sensors.navx.GyroReader;
import org.usfirst.frc.team1732.robot.util.NotifierCommand;
import org.usfirst.frc.team1732.robot.util.Util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 */
public class TurnAngle extends NotifierCommand {

	private static final double DEADBAND_TIME = 0.5;
	private static final double ANGLE_DEADBAND = 1;
	private static final double HEADING_P = 2;
	private static final double startMinVel = 20;
	private static final double endMinVel = 8;
	private static final double endMaxVel = 30;

	private final Timer deadbandTimer;
	private boolean inDeadband = false;
	private final double goalAngle;
	private final double maxVel;
	private final double k;
	private GyroReader navx;
	private double endZone = 20;

	public TurnAngle(double angle) {
		super(5);
		requires(Robot.drivetrain);
		deadbandTimer = new Timer();
		deadbandTimer.reset();
		deadbandTimer.stop();
		this.goalAngle = angle;
		drivetrain.setNeutralMode(NeutralMode.Brake);
		navx = Robot.sensors.navx.makeReader();
		this.maxVel = Math.abs(angle) * 2.5 / 3.0;
		this.k = Math.PI * 2 / (Math.abs(angle));
		endZone = (25 * 90) / (60) * (maxVel / angle);
		System.out.println("Endzone : " + endZone);
	}

	private double getVelocity(double angle) {
		return maxVel * (-0.5 * Math.cos(angle * k) + 0.5);
	}

	@Override
	protected void init() {
		navx.zero();
		System.out.println("Turn to angle started");
		// pid.setSetpoint(goalAngle);
		Robot.drivetrain.setNeutralMode(NeutralMode.Brake);
		ClosedLoopProfile gains = Robot.drivetrain.velocityGains.clone();
		gains.kP = 0.3;
		gains.selectGains(Robot.drivetrain.leftMaster, Robot.drivetrain.rightMaster);
	}

	@Override
	protected void exec() {
		double currentHeading = navx.getTotalAngle();
		double headingError = goalAngle - currentHeading;
		double headingAdjustment = headingError * HEADING_P;

		double vel = getVelocity(currentHeading) * Math.signum(headingError);

		// bump start vel
		if (Math.abs(currentHeading) < Math.abs(goalAngle / 2) && Math.abs(vel) < startMinVel) {
			System.out.println("bumping start velocity");
			vel = startMinVel * Math.signum(headingError);
		}

		if (Math.abs(currentHeading) > Math.abs(goalAngle / 2) && Math.abs(vel) < endMinVel) {
			vel = endMinVel * Math.signum(headingError);
			System.out.println("bumping end velocity");
		}

		if (Math.abs(currentHeading) > Math.abs(goalAngle - endZone * Math.signum(goalAngle))
				&& Math.abs(vel) > endMaxVel) {
			System.out.println("capping end velocity");
			vel = endMaxVel * Math.signum(headingError);
		}

		// stuff during second half of turn (after maxVel has been reached
		// theoeretically)
		// if (Math.abs(currentHeading) > Math.abs(goalAngle / 2)) {
		// // if we're towards the end, cap velocity so we start slowing down
		// if (Math.abs(currentHeading) > Math.abs(goalAngle - endZone *
		// Math.signum(goalAngle))
		// && Math.abs(vel) > endMaxVel) {
		// System.out.println("capping end velocity");
		// vel = endMaxVel * Math.signum(headingError);
		// }
		// // // if we've past the setpoint, agressivly go back with PID
		// // if (Math.abs(currentHeading) > Math.abs(goalAngle)) {
		// // System.out.println("Using heading P");
		// // vel = headingAdjustment;
		// // }
		// // make sure we don't go too slow at the end
		// if (Math.abs(vel) < endMinVel) {
		// vel = endMinVel * Math.signum(headingError);
		// System.out.println("bumping end velocity");
		// }
		// }

		if (Util.epsilonEquals(goalAngle, currentHeading, ANGLE_DEADBAND + 0.5)) {
			System.out.println("trying to stop robot");
			drivetrain.leftMaster.set(ControlMode.Velocity, Robot.drivetrain.convertVelocitySetpoint(0));
			drivetrain.rightMaster.set(ControlMode.Velocity, Robot.drivetrain.convertVelocitySetpoint(0));
		} else {
			drivetrain.leftMaster.set(ControlMode.Velocity, Robot.drivetrain.convertVelocitySetpoint(vel));
			drivetrain.rightMaster.set(ControlMode.Velocity, Robot.drivetrain.convertVelocitySetpoint(-vel));
		}
		// drivetrain.leftTalon1.set(ControlMode.Velocity,
		// Robot.drivetrain.convertVelocitySetpoint(headingAdjustment));
		// drivetrain.rightTalon1.set(ControlMode.Velocity,
		// Robot.drivetrain.convertVelocitySetpoint(-headingAdjustment));

		if (!inDeadband && Math.abs(goalAngle - currentHeading) < ANGLE_DEADBAND) {
			deadbandTimer.start();
			inDeadband = true;
		} else if (inDeadband && !(Math.abs(goalAngle - currentHeading) < ANGLE_DEADBAND)) {
			inDeadband = false;
			deadbandTimer.reset();
			deadbandTimer.stop();
		}
		System.out.println("angle: " + currentHeading + " " + headingError + " "
				+ (Math.abs(headingError) < ANGLE_DEADBAND) + " " + headingAdjustment);
		System.out.println("left: " + Robot.drivetrain.leftMaster.getClosedLoopError(0) + " "
				+ Robot.drivetrain.leftMaster.getClosedLoopTarget(0) + " " + vel);
		System.out.println("right: " + Robot.drivetrain.rightMaster.getClosedLoopError(0) + " "
				+ Robot.drivetrain.rightMaster.getClosedLoopTarget(0) + " " + -vel);
		System.out.println();
	}

	@Override
	protected boolean isDone() {
		return Math.abs(goalAngle - navx.getTotalAngle()) < ANGLE_DEADBAND && deadbandTimer.get() > DEADBAND_TIME;
	}

	@Override
	protected void whenEnded() {
		System.out.println("Turn to angle finished");
		drivetrain.setStop();
	}
}