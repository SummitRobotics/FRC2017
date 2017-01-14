package Templates;

import org.usfirst.frc.team5468.robot.Robot;

public abstract class TeleopProgram {
	Robot mainRobot;
	String programName;
	public TeleopProgram(Robot robot,String name)
	{
		mainRobot = robot;
		programName = name;
	}
	
	public abstract void teleopInit();
	public abstract void teleopPeriodic();
	public abstract void teleopDisabledInit();
	public abstract void teleopDisabledPeriodic();
	
}

