package AutonomousPrograms;
import org.usfirst.frc.team5468.robot.Robot;

import Templates.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import Plugins.*;

//extends our abstract class
public class gearLeftCenter extends AutonomousProgram 
{
	//the thread class enables stopping of bot mid-function.
	//this makes the robot safer and obey rules
	AutonomousUtilities autoUtilities;
	GLC autoThread;
	
	//constructor
	public gearLeftCenter(Robot robot, String name)
	{
		super(robot, name);
	}

	//overriding the abstract class
	@Override
	public void autonomousInit() 
	{
		autoUtilities = new AutonomousUtilities(this);
		
		forwardPIDP = mainRobot.programPreferences.getDouble("Auto Forward P", 0.01);
		forwardPIDI = mainRobot.programPreferences.getDouble("Auto Forward I", 0);
		forwardPIDD = mainRobot.programPreferences.getDouble("Auto Forward D", 0);
		turnPIDP = mainRobot.programPreferences.getDouble("Auto Turn P", 0.01);
		turnPIDI = mainRobot.programPreferences.getDouble("Auto Turn I", 0);
		turnPIDD = mainRobot.programPreferences.getDouble("Auto Turn D", 0);

		//Reset the gyro's position
		mainRobot.hardwareMap.gyro.reset();
				
		//Create a new auto program thread
		autoThread = new GLC(this);
		
		mainRobot.hardwareMap.lfDrive.enable();
		mainRobot.hardwareMap.lrDrive.enable();
		mainRobot.hardwareMap.rfDrive.enable();
		mainRobot.hardwareMap.rrDrive.enable();
			
		//Start executing the auto thread
		autoThread.start();
		
	}

	@Override
	public void autonomousPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}

	//do this when the bot is disabled
	//this should kill the Gossamer thread
	@Override
	public void autonomousDisabledInit() 
	{
		//Stop the thread
		if(autoThread != null)
		{
			autoThread.interrupt();
		}
		
		mainRobot.hardwareMap.rfDrive.disable();
		mainRobot.hardwareMap.rrDrive.disable();
		mainRobot.hardwareMap.lfDrive.disable();
		mainRobot.hardwareMap.lrDrive.disable();
	}

	@Override
	public void autonomousDisabledPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}
}

//This thread will enable functions to run in a safe format
class GLC extends Thread
{
	//Reference the auto class
	gearLeftCenter autoProgram;

	//Constructor saves an instance of auto class for reference
	public GLC (gearLeftCenter program)
	{
		autoProgram = program;
		
		autoProgram.currentHeading = 0;
	}
	
	//insert commands here
	//will follow linear format
	public void run ()
	{
		//LEFT
		//Forward to gear
		autoProgram.autoUtilities.forwardWithGyro(0.5, 1.2);
		autoProgram.autoUtilities.forwardWithGyro(0.1, 1.2);
		
		//Release gear
		autoProgram.mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kForward);
		autoProgram.autoUtilities.waitForTime(0.6);
		
		//Push gear further on
		autoProgram.autoUtilities.forwardWithGyro(-0.2, 0.65);
		autoProgram.mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kReverse);
		autoProgram.autoUtilities.forwardWithGyro(0.13, 1.25);
		
		//Back away from gear
		autoProgram.autoUtilities.forwardWithGyro(-0.13, 0.4);
		autoProgram.autoUtilities.forwardWithGyro(-0.5, 0.6);
		autoProgram.mainRobot.hardwareMap.intake.set(1);
		
		//Turn left 75 degrees
		autoProgram.autoUtilities.turnWithGyro(1, -75);
		
		//Go forward for 0.5 seconds
		autoProgram.autoUtilities.forwardWithGyro(0.5, 1.9);
		
		//Turn right 75 degrees
		autoProgram.autoUtilities.turnWithGyro(1, 75);
		
		//Go forward past line
		autoProgram.autoUtilities.forwardWithGyro(0.25, 0.1);
		autoProgram.autoUtilities.forwardWithGyro(0.9, 2.5);
	}
}