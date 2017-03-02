package AutonomousPrograms;
import org.usfirst.frc.team5468.robot.Robot;

import Templates.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Plugins.*;

//extends our abstract class
public class hopperShootBlueCenter extends AutonomousProgram 
{
	//reference gyro and thread for auto functions
	//the thread class enables stopping of bot mid-function.
	//this makes the robot safer and obey rules
	PID gyroPID;	
	HSBC autoThread;
	HallEffect hall;
	//Reference for vision
	Vision visionProc;
	
	//constructor
	public hopperShootBlueCenter(Robot robot, String name)
	{
		super(robot, name);
		hall = new HallEffect(robot);
	}

	//overriding the abstract class
	@Override
	public void autonomousInit() 
	{
		//Setup a PID controller for using the gyro
		gyroPID = new PID(mainRobot.programPreferences.getDouble("Auto P Value", 0.01),
				mainRobot.programPreferences.getDouble("Auto I Value", 0.0),
				mainRobot.programPreferences.getDouble("Auto D Value", 0.0),
				1, 100);
		
		//Setup the vision system
		visionProc = new Vision("Auto", mainRobot.camera, 320, 240, 30);
		
		//Get the HSV mask parameters from the robot preferences and set them in the vision system
		visionProc.setMaskParameters(mainRobot.programPreferences.getInt("Upper Hue", 80),
						mainRobot.programPreferences.getInt("Upper Sat", 255),
						mainRobot.programPreferences.getInt("Upper Val", 170), 
						mainRobot.programPreferences.getInt("Lower Hue", 60), 
						mainRobot.programPreferences.getInt("Lower Sat", 200),
						mainRobot.programPreferences.getInt("Lower Val", 120));
				
				//Get camera settings from the robot preferences and set them
		visionProc.setCameraParameters(mainRobot.programPreferences.getInt("Exposure", 1), 
						mainRobot.programPreferences.getInt("WB", 5200),
						mainRobot.programPreferences.getInt("Brightness", 50));
				
		visionProc.startVision();
		
		
		//Reset the gyro's position
		mainRobot.hardwareMap.gyro.reset();
				
		//Create a new auto program thread
		autoThread = new HSBC(this);
		
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
		if(visionProc != null)
		{
			visionProc.stopVision();
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
	
	//A cleaner format for assigning power to the R and L drive trains
	public void assignPower(double powerL, double powerR)
	{
		mainRobot.hardwareMap.lfDrive.set(powerL);
		mainRobot.hardwareMap.lrDrive.set(powerL);
		mainRobot.hardwareMap.rfDrive.set(-powerR);
		mainRobot.hardwareMap.rrDrive.set(-powerR);
	}
}

//This thread will enable functions to run in a safe format
class HSBC extends Thread
{
	final int MAX_TURN_TIME = 5000; //Maximum time the robot will attempt to turn before giving up (in milliseconds)
	final int MAX_TURN_ERROR_WAIT = 50; //The time that the robot has be within the turn error before continuing (in milliseconds)
	final double TURN_ERROR = 1; //The range of acceptable error when turning (in degrees)
	
	//Reference the auto class
	hopperShootBlueCenter autoProgram;
	
	//The heading of the robot relative to its starting orientation (in degrees)
	//This ensures the robot won't drift off course in-between heading specific commands
	double currentHeading;

	//Constructor saves an instance of auto class for reference
	public HSBC (hopperShootBlueCenter program)
	{
		autoProgram = program;
		
		currentHeading = 0;
	}
	
	//insert commands here
	//will follow linear format
	public void run ()
	{
		//Forward to gear
		forwardWithGyro(0.5, 1.25);
		forwardWithGyro(0.1, .25);
		autoProgram.mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kForward);
		forwardWithGyro(0.01, 0.5);
		
		//Back away from gear
		forwardWithGyro(-0.5, 0.75);
		autoProgram.mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kReverse);
		
		//Turn right 90 degrees
		turnWithGyro(0.5, 90);
		
		//Go forward for 0.5 seconds
		forwardWithGyro(0.5, 0.65);
		
		//TODO: Implement vision tracking
	}
	
	
	//Move forward at "power" for "time" (seconds)
	public void forward(double power, double time)
	{
		//Save the time started
		long startTime = System.currentTimeMillis();
		
		//Try catch statement prevents errors if the thread is interrupted
		try
		{
			//For the duration of the travel (as given in paramater)
			//And the threat is stable
			//Give power scotty
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				autoProgram.assignPower(power,power);
				//A cullen thing <-- this ensures the roborio doesn't get mad and crash, James
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}
		
		//Disable all motors
		autoProgram.assignPower(0,0);
	}
	
	//Drive forward while monitoring and maintaining a heading
	public void forwardWithGyro(double power, double time)
	{
		//Save the time we started this command
		long startTime = System.currentTimeMillis();
		
		//Set the PID system so the output will be between -1 and 1
		autoProgram.gyroPID.setMaxOutput(1.0);
		try
		{
			//Loop until enough time has passed or this thread has been interrupted
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				//Set the target and sensor values for the PID controller
				autoProgram.gyroPID.setParameters(autoProgram.mainRobot.hardwareMap.gyro.getAngle(), currentHeading);
				
				//PID magic occurs here (OOOOH, AHHHH)
				double pidOutput = autoProgram.gyroPID.calculateOutput();
				
				//for debugging purposes
				/*SmartDashboard.putNumber("Right Power", power - pidOutput);
				SmartDashboard.putNumber("Left Power", power + pidOutput);
				SmartDashboard.putNumber("Gyro Angle", autoProgram.mainRobot.hardwareMap.gyro.getAngle());*/
				
				//Adjust the power accordingly
				autoProgram.assignPower(power + pidOutput, power - pidOutput);
				
				//cullen thing <- Prevent crashing (the program, not the robot, unfortunately)
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}
		
		//stop motors
		autoProgram.assignPower(0,0);
	}
	
	//Turn and angle using the gyro, positive theta is for right turns
	public void turnWithGyro(double power, double theta)
	{
		//Adjust our heading
		currentHeading += theta;
		
		//Save the current time
		long start = System.currentTimeMillis();
		
		//Flag for stopping turning when we've reached our wanted angle
		boolean finished = false;
		
		//Counter for the number of ms the robot is within the turning error
		int errorCounts = 0;
		
		//Constrain the PID output to be the maximum power we want while turning
		autoProgram.gyroPID.setMaxOutput(power);
		
		try
		{
			//Loop while the robot hasn't reached our goal and while we haven't given up
			while(!Thread.interrupted() & !finished & (System.currentTimeMillis() - start < MAX_TURN_TIME))
			{
				//Set the PID parameters so our target is our new heading
				autoProgram.gyroPID.setParameters(autoProgram.mainRobot.hardwareMap.gyro.getAngle(), currentHeading);
				
				//PID magic
				double pidOutput = autoProgram.gyroPID.calculateOutput();
				
				//if the angle is good enough...
				if( Math.abs(currentHeading - autoProgram.mainRobot.hardwareMap.gyro.getAngle()) < TURN_ERROR)
				{
					//Increase the number of ms we're in our error
					errorCounts++;
				}
				else
				{
					//We're not there yet, so reset the counter
					errorCounts = 0;
				}
				
				//If we've been within the error for long enough...
				if(errorCounts >= MAX_TURN_ERROR_WAIT)
				{
					//We're done!
					finished = true;
				}
				
				//Set motor powers
				autoProgram.assignPower(pidOutput, -pidOutput);
				
				//Prevent the program from exploding (you're WELCOME, James)
				Thread.sleep(1);
			}
		} catch(InterruptedException e) {}
		
		//Stop!!!
		autoProgram.assignPower(0,0);
	}
	
	//Make the robot wait for some time
	public void waitForTime(double time)
	{
		//Save the current time
		long startTime = System.currentTimeMillis();
		
		//Ensure we aren't moving
		autoProgram.assignPower(0,0);
		
		try
		{
			//Loop until enough time has passed
			while (System.currentTimeMillis() - startTime < time * 1000)
			{
				//Don't crash pls
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {}
	}
}