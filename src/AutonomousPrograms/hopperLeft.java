package AutonomousPrograms;

import org.usfirst.frc.team5468.robot.Robot;
import org.usfirst.frc.team5468.robot.Shooters;

import Plugins.AutonomousUtilities;
import Plugins.Vision;
import Templates.AutonomousProgram;

public class hopperLeft extends AutonomousProgram
{
	//reference gyro and thread for auto functions
	//the thread class enables stopping of bot mid-function.
	//this makes the robot safer and obey rules	
	HL autoThread;
	AutonomousUtilities autoUtilities;
	Shooters shooters;

	//constructor
	public hopperLeft(Robot robot, String name)
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

		//Create new instance of shooters
		shooters = new Shooters(mainRobot, mainRobot.programPreferences.getDouble("Shooter P Value", 0.001),
				mainRobot.programPreferences.getDouble("Shooter I Value", 0),
				mainRobot.programPreferences.getDouble("Shooter D Value", 0));

		//Setup the vision system
		vision = new Vision("Auto", mainRobot.camera, 320, 240, 30);

		//Get the HSV mask parameters from the robot preferences and set them in the vision system
		vision.setMaskParameters(mainRobot.programPreferences.getInt("Upper Hue", 80),
				mainRobot.programPreferences.getInt("Upper Sat", 255),
				mainRobot.programPreferences.getInt("Upper Val", 170), 
				mainRobot.programPreferences.getInt("Lower Hue", 60), 
				mainRobot.programPreferences.getInt("Lower Sat", 200),
				mainRobot.programPreferences.getInt("Lower Val", 120));

		//Get camera settings from the robot preferences and set them
		vision.setCameraParameters(mainRobot.programPreferences.getInt("Exposure", 1), 
				mainRobot.programPreferences.getInt("WB", 5200),
				mainRobot.programPreferences.getInt("Brightness", 50));

		vision.startVision();


		//Reset the gyro's position
		mainRobot.hardwareMap.gyro.reset();

		//Create a new auto program thread
		autoThread = new HL(this);

		mainRobot.hardwareMap.lfDrive.enable();
		mainRobot.hardwareMap.lrDrive.enable();
		mainRobot.hardwareMap.rfDrive.enable();
		mainRobot.hardwareMap.rrDrive.enable();

		mainRobot.hardwareMap.rShooter.enable();
		mainRobot.hardwareMap.lShooter.enable();
		mainRobot.hardwareMap.rBlender.enable();
		mainRobot.hardwareMap.lBlender.enable();

		mainRobot.hardwareMap.intake.enable();

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
		//Stop vision
		if(vision != null)
		{
			vision.stopVision();
		}

		//Stop the thread
		if(autoThread != null)
		{
			autoThread.interrupt();
		}

		mainRobot.hardwareMap.lfDrive.disable();
		mainRobot.hardwareMap.lrDrive.disable();
		mainRobot.hardwareMap.rfDrive.disable();
		mainRobot.hardwareMap.rrDrive.disable();

		mainRobot.hardwareMap.rShooter.disable();
		mainRobot.hardwareMap.lShooter.disable();
		mainRobot.hardwareMap.rBlender.disable();
		mainRobot.hardwareMap.lBlender.disable();

		mainRobot.hardwareMap.intake.disable();
	}

	@Override
	public void autonomousDisabledPeriodic() 
	{
		// TODO Auto-generated method stub

	}
}

//This thread will enable functions to run in a safe format
class HL extends Thread
{
	//Reference the auto class
	hopperLeft autoProgram;

	//Constructor saves an instance of auto class for reference
	public HL (hopperLeft hopperLeft)
	{
		autoProgram = hopperLeft;
		autoProgram.currentHeading = 0;
	}

	//insert commands here
	//will follow linear form
	public void run ()
	{
		//LEFT HOPPER SHOOT
		//Drive backward to blue hopper
		autoProgram.autoUtilities.forwardWithGyro(-0.7, 1);
		autoProgram.autoUtilities.forwardWithGyro(-0.2, 0.4);
		autoProgram.mainRobot.hardwareMap.intake.set(1);

		//Turn right to hopper
		autoProgram.autoUtilities.turnWithGyro(0.7, 30);

		//autoProgram.mainRobot.hardwareMap.rShooter.set(-autoProgram.mainRobot.hardwareMap.shootPower);
		//autoProgram.mainRobot.hardwareMap.lShooter.set(autoProgram.mainRobot.hardwareMap.shootPower);

		autoProgram.autoUtilities.waitForTime(1);

		//Turn away from wall
		autoProgram.autoUtilities.turnWithGyro(0.5,-15);

		//Drive to boiler
		autoProgram.autoUtilities.forwardWithGyro(0.7, 0.5);
		autoProgram.autoUtilities.turnWithGyro(0.5,30);

		//Turn on shooter wheels
		autoProgram.shooters.shooterControl(true);

		//Vision Align
		autoProgram.autoUtilities.visionAlignX(0.5, 0);
		autoProgram.autoUtilities.visionAlignY(0.5, 30);

		//Turn on blenders
		autoProgram.mainRobot.hardwareMap.rBlender.set(1);
		autoProgram.mainRobot.hardwareMap.lBlender.set(-1);

	}
}