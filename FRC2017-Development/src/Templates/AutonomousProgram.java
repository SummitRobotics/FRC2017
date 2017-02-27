package Templates;

import org.usfirst.frc.team5468.robot.Robot;

public abstract class AutonomousProgram {
	public Robot mainRobot;
	public String programName;
	
	//assign the paramaters of constructor to the class
	public AutonomousProgram(Robot robot, String program){
		mainRobot = robot;
		programName = program;
	}
	
	public abstract void autonomousInit();
	public abstract void autonomousPeriodic();
	public abstract void autonomousDisabledInit();
	public abstract void autonomousDisabledPeriodic();
	
}
