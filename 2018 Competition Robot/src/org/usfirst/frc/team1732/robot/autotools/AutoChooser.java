package org.usfirst.frc.team1732.robot.autotools;

import java.util.function.Supplier;

import org.usfirst.frc.team1732.robot.Robot;
import org.usfirst.frc.team1732.robot.commands.autos.ScaleLeftDouble;
import org.usfirst.frc.team1732.robot.commands.autos.ScaleLeftSingleStraight;
import org.usfirst.frc.team1732.robot.commands.autos.ScaleRightDouble;
import org.usfirst.frc.team1732.robot.commands.autos.ScaleRightDoubleScale;
import org.usfirst.frc.team1732.robot.commands.autos.ScaleRightSingleStraight;
import org.usfirst.frc.team1732.robot.commands.autos.SwitchCenterFront;
import org.usfirst.frc.team1732.robot.commands.primitive.DriveTime;
import org.usfirst.frc.team1732.robot.commands.testing.TestCubePickup;
import org.usfirst.frc.team1732.robot.commands.testing.TestProfile;
import org.usfirst.frc.team1732.robot.commands.testing.TestVelocityFollowing;
import org.usfirst.frc.team1732.robot.input.Input;
import org.usfirst.frc.team1732.robot.util.Debugger;
import org.usfirst.frc.team1732.robot.util.Util;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.command.Command;

public final class AutoChooser {
	public static enum AutoModes {
		SwitchCenterFront(() -> new SwitchCenterFront()), //
		ScaleLeftSingle(() -> new ScaleLeftSingleStraight()), //
		ScaleRightSingle(() -> new ScaleRightSingleStraight()), //
		ScaleLeftDouble(() -> new ScaleLeftDouble()), //
		ScaleRightDouble(() -> new ScaleRightDouble()), //
		ScaleRightDoubleScale(() -> new ScaleRightDoubleScale()), //
		TestProfile(() -> new TestProfile()), //
		TestCubePickup(() -> new TestCubePickup()), //
		DriveTime(() -> new DriveTime(-0.7, -0.7, NeutralMode.Brake, 20, 100)), //
		DriveVelocity(() -> new TestVelocityFollowing(75, 75)); //

		private final Supplier<Command> commandSupplier;

		private AutoModes(Supplier<Command> commandSupplier) {
			this.commandSupplier = commandSupplier;
		}

		public Command getCommand() {
			return commandSupplier.get();
		}

	}

	private AutoChooser() {
	}

	public static void addListener(Input joysticks) {
		int first = (int) Util.limit(joysticks.autoDial.get(), 0, AutoModes.values().length - 1);
		System.err.println("ERROR " + first + " AUTO: " + AutoModes.values()[first]);
		joysticks.autoDial.addValueChangeListener(d -> {
			int i = (int) Util.limit(d, 0, AutoModes.values().length - 1);
			System.err.println("ERROR " + i + " AUTO: " + AutoModes.values()[i]);
		});
	}

	public static Command getSelectedAuto() {
		int i = (int) Util.limit(Robot.joysticks.autoDial.get(), 0, AutoModes.values().length - 1);
		Debugger.logDetailedInfo("Running auto: " + AutoModes.values()[i].name());
		return AutoModes.values()[i].getCommand();
	}
}
