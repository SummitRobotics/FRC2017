package AutonomousPrograms;

import org.usfirst.frc.team5468.robot.Robot;

import Plugins.AutonomousUtilities;
import Plugins.Vision;
import Templates.AutonomousProgram;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class VISgearRightSide extends AutonomousProgram{

	//reference gyro and thread for auto functions
		//the thread class enables stopping of bot mid-function.
		//this makes the robot safer and obey rules	
		VGRS autoThread;
		AutonomousUtilities autoUtilities;
		
		//constructor
		public VISgearRightSide(Robot robot, String name)
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
			autoThread = new VGRS(this);
				
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
		}

		@Override
		public void autonomousDisabledPeriodic() 
		{
			// TODO Auto-generated method stub
			
		}
	}

	//This thread will enable functions to run in a safe format
	class VGRS extends Thread
	{
		//Reference the auto class
		VISgearRightSide autoProgram;

		//Constructor saves an instance of auto class for reference
		public VGRS (VISgearRightSide VISgearRightSide)
		{
			autoProgram = VISgearRightSide;
			autoProgram.currentHeading = 0;
		}
		
		//insert commands here
		//will follow linear form
		public void run ()
		{
			//Vision gear right peg, right side
			//Forward for 1.2s
			autoProgram.autoUtilities.forwardWithGyro(0.5, 1.2);
			
			//Turn left to face peg
			autoProgram.autoUtilities.turnWithGyro(0.5, -60);
			
			//Align with vision
			autoProgram.autoUtilities.visionAlignX(0.5, 0);
			
			//Once aligned with vision, go forward the remaining distance to the peg
			//forwardWithGyro(0.5, 0.3);
			autoProgram.autoUtilities.forwardWithGyro(0.1, 1.2);
			autoProgram.mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kForward);
			autoProgram.autoUtilities.waitForTime(0.6);
			
			//Pushing gear further on
			autoProgram.autoUtilities.forwardWithGyro(-0.2, 0.65);
			autoProgram.mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kReverse);
			autoProgram.autoUtilities.forwardWithGyro(0.2, 0.65);
			
			//Back away from gear
			autoProgram.autoUtilities.forwardWithGyro(-0.13, 0.4);
			autoProgram.autoUtilities.forwardWithGyro(-0.5, 0.3);
			
			//Turn right 60 degrees
			autoProgram.autoUtilities.turnWithGyro(0.5, 60);
			
			//Go forward for 1.5 seconds
			autoProgram.autoUtilities.forwardWithGyro(0.7, 1.5);
		}
}
