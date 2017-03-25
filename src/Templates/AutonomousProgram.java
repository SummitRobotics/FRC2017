package Templates;

import org.usfirst.frc.team5468.robot.Robot;
import Plugins.PID;
import Plugins.Vision;

public abstract class AutonomousProgram 
{
	public Robot mainRobot;
	public String programName;
	public PID gyroPID;
	public Vision vision;
	
	public double forwardPIDP;
	public double forwardPIDI;
	public double forwardPIDD;
	
	public double turnPIDP;
	public double turnPIDI;
	public double turnPIDD;
	
	public double currentHeading;
	public final double MAX_TURN_TIME = 5000;
	public final double MAX_TURN_ERROR_WAIT = 50;
	public final double TURN_ERROR = 1;
	public final double HEIGHT_ERROR = 5;
	public final double MAX_FORWARD_ALIGN_PERIOD = 0.1;
	
	//assign the paramaters of constructor to the class
	public AutonomousProgram(Robot robot, String program){
		mainRobot = robot;
		programName = program;
	}
	
	public abstract void autonomousInit();
	public abstract void autonomousPeriodic();
	public abstract void autonomousDisabledInit();
	public abstract void autonomousDisabledPeriodic();
	
	//A cleaner format for assigning power to the R and L drive trains
	public void assignPower(double powerL, double powerR)
	{
		mainRobot.hardwareMap.lfDrive.set(powerL);
		mainRobot.hardwareMap.lrDrive.set(powerL);
		mainRobot.hardwareMap.rfDrive.set(-powerR);
		mainRobot.hardwareMap.rrDrive.set(-powerR);
	}	
}
