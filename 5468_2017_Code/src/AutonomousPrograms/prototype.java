package AutonomousPrograms;
import org.usfirst.frc.team5468.robot.Robot;

import Templates.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Plugins.*;

//extends our abstract class
public class prototype extends AutonomousProgram {
	
	//reference gyro and thread for auto functions
	//the thread class enables stopping of bot mid-function.
	//this makes the robot safer and obey rules
	PID gyroPID;	
	Gossamer autoThread;
	
	Vision visionProc;
	
	//constructor
	public prototype(Robot robot, String name){
		super(robot, name);
	}

	//overriding the abstract class
	@Override
	public void autonomousInit() 
	{
		//pass values to the gyro that affect it's output
		//Proportional
		//Integral
		//Derivative
		gyroPID = new PID(mainRobot.programPreferences.getDouble("Auto P Value", 0.01),
				mainRobot.programPreferences.getDouble("Auto I Value", 0.0),
				mainRobot.programPreferences.getDouble("Auto D Value", 0.0),
				1, 100);
		
		visionProc = new Vision("Auto", mainRobot.camera, 320, 240, 30);
		
		mainRobot.hardwareMap.gyro.reset();
		
		//make an instance of Gossamer
		autoThread = new Gossamer(this);
		
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
		// reset the gyro to be based at a 0 pt start
		mainRobot.hardwareMap.gyro.reset();
		
		if(visionProc != null)
		{
			visionProc.stopVision();
		}
		
		//cut thread
		if(autoThread != null)
		{
			autoThread.interrupt();
		}
	}

	@Override
	public void autonomousDisabledPeriodic() {
		// TODO Auto-generated method stub
		
	}
	
	//a cleaner format for assigning power to the R and L drive trains
	//note that the negative wheels may be improperly set
	public void assignPower(double powerL, double powerR){
		mainRobot.hardwareMap.lfDrive.set(powerL);
		mainRobot.hardwareMap.lrDrive.set(powerL);
		mainRobot.hardwareMap.rfDrive.set(-powerR);
		mainRobot.hardwareMap.rrDrive.set(-powerR);
	}
}

//this thread will enable functions to run in a safe format
class Gossamer extends Thread
{
	//reference the auto class
	prototype autoProgram;
	
	double wantedHeading;
	
	//constructor pulls instance of auto class for reference
	public Gossamer (prototype program)
	{
		autoProgram = program;
	}
	
	//insert commands here
	//will follow linear format
	public void run ()
	{
		forwardWithGyro(0.5, 1);
		turnWithGyro(.5, 90);
		forwardWithGyro(0.5, 0.5);
		waitForTime(2);
		forwardWithGyro(-0.5, 0.25);
		turnWithGyro(0.5, 90);
	}
	
	
	//move forward
	public void forward(double power, double time)
	{
		//find the time started
		long startTime = System.currentTimeMillis();
		//try catch statement prevents errors if the thread is interupted
		try
		{
			//for the duration of the travel (as given in paramater)
			//and the threat is stable
			//give power scotty
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				autoProgram.assignPower(power,power);
				//A cullen thing
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}
		//disable all motors
		autoProgram.assignPower(0,0);
	}
	
	//if the bot is pushed, realign
	public void forwardWithGyro(double power, double time)
	{
		long startTime = System.currentTimeMillis();
		double startHeading = autoProgram.mainRobot.hardwareMap.gyro.getAngle();
		autoProgram.gyroPID.setMaxOutput(1.0);
		//double startingAngle = mainRobot.hardwareMap.gyro.getAngle();
		try
		{
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				//give the gyro the initial degree and target position
				autoProgram.gyroPID.setParameters(autoProgram.mainRobot.hardwareMap.gyro.getAngle(), startHeading);
				//PID magic occurs here
				double pidOutput = autoProgram.gyroPID.calculateOutput();
				
				//for debugging purposes
				SmartDashboard.putNumber("Right Power", power - pidOutput);
				SmartDashboard.putNumber("Left Power", power + pidOutput);
				SmartDashboard.putNumber("Gyro Angle", autoProgram.mainRobot.hardwareMap.gyro.getAngle());
				
				//adjust the power accordingly
				autoProgram.assignPower(power + pidOutput, power - pidOutput);
				
				//cullen thing
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}
		//stop motors
		autoProgram.assignPower(0,0);
	}
	
	//turn with gyros
	public void turnWithGyro(double power, double theta){
		double gyroInitial = autoProgram.mainRobot.hardwareMap.gyro.getAngle();
		//time based stop
		long start = System.currentTimeMillis();
		int maxTime = 5000;
		int maxErrorWait = 10;
		//angle based stop
		double error = 1.0;
		boolean finished = false;
		int errorCounts = 0;
		autoProgram.gyroPID.setMaxOutput(power);
		try
		{
			while(!Thread.interrupted() & !finished & (System.currentTimeMillis() - start < maxTime)){
				autoProgram.gyroPID.setParameters(autoProgram.mainRobot.hardwareMap.gyro.getAngle(), gyroInitial + theta);
				double pidOutput = autoProgram.gyroPID.calculateOutput();
				//if the angle is good enough quit the loop
				if( Math.abs((gyroInitial + theta) - autoProgram.mainRobot.hardwareMap.gyro.getAngle()) < error)
				{
					errorCounts++;
				}
				else
				{
					errorCounts = 0;
				}
				if(errorCounts >= maxErrorWait)
				{
					finished = true;
				}
				autoProgram.assignPower( pidOutput, - pidOutput);
				Thread.sleep(1);
			}
		} catch(InterruptedException e) {}
		autoProgram.assignPower(0,0);
	}
	
	public void waitForTime(double time)
	{
		long startTime = System.currentTimeMillis();
		autoProgram.assignPower(0,0);
		
		try
		{
			while (System.currentTimeMillis() - startTime < time * 1000)
			{
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {}
	}
}