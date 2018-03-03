package org.usfirst.frc.team1732.robot.subsystems;

import org.usfirst.frc.team1732.robot.Robot;
import org.usfirst.frc.team1732.robot.config.MotorUtils;
import org.usfirst.frc.team1732.robot.config.RobotConfig;
import org.usfirst.frc.team1732.robot.controlutils.ClosedLoopProfile;
import org.usfirst.frc.team1732.robot.sensors.encoders.TalonEncoder;
import org.usfirst.frc.team1732.robot.util.Util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem to control the arm
 * 
 * Manages 1 motor
 */
public class Arm extends Subsystem {

	public final TalonSRX motor;
	public final TalonEncoder encoder;

	public final ClosedLoopProfile upGains;
	public final ClosedLoopProfile downGains;

	private int desiredPosition;
	private boolean desiredIsSet;
	private boolean autoControl = false;

	private final int allowedError;

	public Arm(RobotConfig config) {
		motor = MotorUtils.makeTalon(config.arm, config.armConfig);
		upGains = config.armDownPID;
		downGains = config.armDownPID;
		upGains.applyToTalon(motor);
		downGains.applyToTalon(motor);
		// ClosedLoopProfile.applyZeroGainToTalon(upGains.feedback, upGains.slotIdx, 1,
		// motor);
		// ClosedLoopProfile.applyZeroGainToTalon(downGains.feedback, downGains.slotIdx,
		// 1, motor);
		encoder = new TalonEncoder(motor, FeedbackDevice.CTRE_MagEncoder_Absolute);
		encoder.setPhase(config.reverseArmSensor);

		allowedError = config.armAllowedErrorCount;

		Robot.dash.add("Arm Encoder Position", encoder::getPosition);
		Robot.dash.add("Arm Encoder Pulses", encoder::getPulses);
	}

	public static enum Positions {

		// set these in pulses
		MIN(-10207), INTAKE(-9990), SWITCH(-9990), TUCK(-6297), MAX_LOW(-3625), START(-3988), SCALE(-2083), MAX(-2083);

		public final int value;

		private Positions(int value) {
			this.value = value;
		}
	}
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	@Override
	public void periodic() {
		// System.out.println("Arm Encoder: " +
		// motor.getSensorCollection().getPulseWidthRiseToRiseUs());
		if (autoControl) {
			if (desiredPosition > Positions.MAX_LOW.value && !Robot.elevator.isArmSafeToGoUp() && desiredIsSet) {
				motor.set(ControlMode.Position, Positions.TUCK.value);
				desiredIsSet = false;
			}
			if (Robot.elevator.isArmSafeToGoUp() && !desiredIsSet) {
				motor.set(ControlMode.Position, desiredPosition);
				desiredIsSet = true;
			}
		}
	}

	@Override
	public void initDefaultCommand() {
	}

	public void set(int position) {
		if (position < Positions.MIN.value) {
			position = Positions.MIN.value;
		}
		if (position > Positions.MAX.value) {
			position = Positions.MAX.value;
		}
		desiredPosition = position;
		desiredIsSet = true;
		motor.set(ControlMode.Position, desiredPosition);
		autoControl = true;
	}

	public void set(Positions position) {
		desiredPosition = position.value;
		desiredIsSet = true;
		motor.set(ControlMode.Position, desiredPosition);
		autoControl = true;
	}

	public void setManual(double percentVolt) {
		motor.set(ControlMode.PercentOutput, percentVolt);
		autoControl = false;
	}

	public void holdPosition() {
		desiredPosition = encoder.getPulses();
		desiredIsSet = true;
		motor.set(ControlMode.Position, desiredPosition);
		autoControl = true;
	}

	public void setStop() {
		motor.neutralOutput();
		autoControl = false;
	}

	public boolean atSetpoint() {
		return Util.epsilonEquals(encoder.getPulses(), desiredPosition, allowedError);
	}

	public boolean isElevatorSafeToGoDown() {
		return encoder.getPulses() + allowedError < Positions.TUCK.value;
	}

}
