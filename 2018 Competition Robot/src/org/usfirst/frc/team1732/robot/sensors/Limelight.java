package org.usfirst.frc.team1732.robot.sensors;

import org.usfirst.frc.team1732.robot.Robot;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
	private static final String LED_MODE = "ledMode", CAM_MODE = "camMode", STREAM_MODE = "stream";
	private static final int BUFFER_SIZE = 10;

	private final NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

	private double hOffAve;

	private static final double alpha = 0.7;
	private double filteredOut;
	private double previousFiltered = 0;

	public Limelight() {
		// add listener to calculate rolling average
		table.getEntry("tx").addListener(t -> hOffAve += (-hOffAve + t.value.getDouble()) / BUFFER_SIZE,
				EntryListenerFlags.kUpdate);
		table.getEntry("tx").addListener(t -> {
			filteredOut = getRawHorizontalOffset() * alpha + previousFiltered * (1 - alpha);
			previousFiltered = filteredOut;
		}, EntryListenerFlags.kUpdate);
		Robot.dash.add("Limelight offset", this::getFilteredHorizontalOffset);
	}

	// ----- SETTERS -----
	public void setLEDMode(LEDMode mode) {
		table.getEntry(LED_MODE).setNumber(mode.val);
	}

	public void setCamMode(CamMode mode) {
		table.getEntry(CAM_MODE).setNumber(mode.val);
	}

	public void setPipeline(int pipeline) {
		table.getEntry("pipeline").setNumber(pipeline);
	}

	public void setStreamMode(StreamMode mode) {
		table.getEntry(STREAM_MODE).setNumber(mode.val);
	}

	// ----- GETTERS -----
	// returns the horizonatal offset of the target (between -27 and 27 degrees)
	public double getRawHorizontalOffset() {
		return table.getEntry("tx").getNumber(0).doubleValue();
	}

	public double getFilteredHorizontalOffset() {
		return filteredOut;
	}

	public double getRawHorizontalOffset(double defaultValue) {
		return hasValidTargets() ? getRawHorizontalOffset() : defaultValue;
	}

	// returns horizontal offset between -1 and 1
	public double getNormalizedHorizontalOffset() {
		return getRawHorizontalOffset() / 27;
	}

	public double getNormalizedHorizontalOffset(double defaultValue) {
		return hasValidTargets() ? getNormalizedHorizontalOffset() : defaultValue;
	}

	// returns rolling-averaged horizontal offset
	public double getHorizontalOffset() {
		return hOffAve;
	}

	// returns the vertical offset of the target (between -20.5 and 20.5 degrees)
	public double getVerticalOffset() {
		return table.getEntry("ty").getNumber(0).doubleValue();
	}

	public double getVerticalOffset(double defaultValue) {
		return hasValidTargets() ? getVerticalOffset() : defaultValue;
	}

	// returns true if it is tracking a target
	public boolean hasValidTargets() {
		return table.getEntry("tv").getNumber(0).intValue() == 1;
	}

	// returns percent of screen area target takes up (between 0 and 100)
	public double getTargetArea() {
		return table.getEntry("ta").getNumber(0).doubleValue();
	}

	public double getTargetArea(double defaultValue) {
		return hasValidTargets() ? getTargetArea() : defaultValue;
	}

	// returns target area between 0 and 1
	public double getNormalizedTargetArea() {
		return getTargetArea() / 100;
	}

	// Estimates distance in inches using target area
	public double getDistanceToTarget(double defaultValue) {
		return hasValidTargets() ? 123.31 * Math.pow(getTargetArea(), -0.529) : defaultValue;
	}

	// GATHERED DATA
	/*
	 * AREA____DIST 1.0______118 1.2______108 1.5_______96 2.3_______84 3.0_______72
	 * 4.3_______60 5.8_______48 10.2______36 23.2______24 88.1______12 100.0_____10
	 */
	// rotation of target (between -90 and 0 degrees)
	public double getTargetSkew() {
		return table.getEntry("ts").getNumber(0).doubleValue();
	}

	// returns pipeline latency in milliseconds
	// doesnt take image capture latency into account (website says add 11 ms)
	public double getLatency() {
		return table.getEntry("tl").getNumber(0).doubleValue();
	}

	public void toggleLED() {
		NetworkTableEntry lights = table.getEntry(LED_MODE);
		lights.setNumber(lights.getNumber(1).intValue() == 0 ? 1 : 0);
	}

	public void toggleCamMode() {
		NetworkTableEntry cam = table.getEntry(CAM_MODE);
		cam.setNumber(cam.getNumber(1).intValue() == 0 ? 1 : 0);
	}

	public static enum LEDMode {
		ON(0), OFF(1), BLINK(2);
		public int val;

		private LEDMode(int n) {
			val = n;
		}
	}

	public static enum CamMode {
		VISION_PROCESSOR(0), DRIVER_FEEDBACK(1);
		public int val;

		private CamMode(int n) {
			val = n;
		}
	}

	public static enum StreamMode {
		STANDARD(0), PIP_MAIN(1), PIP_SECONDARY(2);
		public int val;

		private StreamMode(int n) {
			val = n;
		}
	}
}
