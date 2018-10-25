package org.usfirst.frc.team1732.robot.subsystems;

import org.usfirst.frc.team1732.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Hooks extends Subsystem {

	// private final Solenoid hooks;
	public final boolean hooksUpValue;

	public Hooks(RobotConfig config) {
		// hooks = new Solenoid(config.hookSolenoidID);
		this.hooksUpValue = config.hookOutValue;
	}

	public void setUp() {
		// hooks.set(hooksUpValue);
	}

	public void setDown() {
		// hooks.set(!hooksUpValue);
	}

	@Override
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}
}
