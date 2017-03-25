package TeleopPrograms;

import org.usfirst.frc.team5468.robot.*;
import Templates.TeleopProgram;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Plugins.*;

public class TeleopTest extends TeleopProgram
{
	public final double DRIVE_EXPONENT = 2.3;
	
	public double maxOutputPower = 1;
	
	public final double VISION_H_PID_P = 0.006;
	public final double VISION_H_PID_I = 0;
	public final double VISION_H_PID_D = 0;
	
	public final double VISION_V_PID_P = 0.006;
	public final double VISION_V_PID_I = 0;
	public final double VISION_V_PID_D = 0;
	
	public final double TARGET_WANTED_X = 141;
	public final double TARGET_WANTED_Y = 0;
	
	Shooters mShooter;
	
	Vision visionProc;
	
	double leftPower = 0;
	double rightPower = 0;
	
	boolean intakeToggle = false;
	boolean gearBusy = false;
	
	boolean gearEnable = false;
	
	double pX;
	double pY;
	
	double gearStart = 0;
	
	int gearState = 1;
	
	double currentHeading;
	
	int xDeadzone = 2;
	int yDeadzone = 3;
	
	double[] pidValues;
	
	boolean targetMode = false;
	
	PID gyroPID;
	PID visionHorizontalPID;
	PID visionVerticalPID;
	
	double visionLeftPower = 0;
	double visionRightPower = 0;

	
	//This is called when an instance of this class is created
	public TeleopTest (Robot robot, String name)
	{
		super (robot, name);
		
		currentHeading = 0;
	}

	//Called once right when teleop starts
	@Override
	public void teleopInit() 
	{
		maxOutputPower = mainRobot.programPreferences.getDouble("maxDrivePower", 1.0);
		
		//Create a new instance of the vision system
		visionProc = new Vision("Vision_Test", mainRobot.camera, 320, 240, 30);
		
		//Create new instance of shooters
		mShooter = new Shooters(mainRobot, mainRobot.programPreferences.getDouble("Shooter P Value", 0.001),
				mainRobot.programPreferences.getDouble("Shooter I Value", 0),
				mainRobot.programPreferences.getDouble("Shooter D Value", 0));
		
		//Get the HSV mask parameters from the robot preferences and set them in the vision system
		visionProc.setMaskParameters(mainRobot.programPreferences.getInt("Upper Hue", 80),
				mainRobot.programPreferences.getInt("Upper Sat", 255),
				mainRobot.programPreferences.getInt("Upper Val", 170), 
				mainRobot.programPreferences.getInt("Lower Hue", 60), 
				mainRobot.programPreferences.getInt("Lower Sat", 200),
				mainRobot.programPreferences.getInt("Lower Val", 120));
		
		//Get camera settings from the robot preferences and set them
		visionProc.setCameraParameters(mainRobot.programPreferences.getInt("Exposure", 10), 
				mainRobot.programPreferences.getInt("WB", 5200),
				mainRobot.programPreferences.getInt("Brightness", 50));
		
		//Start vision processing
		visionProc.startVision();
		
		xDeadzone = mainRobot.programPreferences.getInt("X Deadzone", 2);
		yDeadzone = mainRobot.programPreferences.getInt("Y Deadzone", 3); 
		
		//visionProc.iX = mainRobot.programPreferences.getInt("Target position X cordinate", visionProc.imgWidth / 2);
		//visionProc.iY = mainRobot.programPreferences.getInt("Target position Y cordinate", visionProc.imgHeight / 2);
		
		mainRobot.hardwareMap.compressor.setClosedLoopControl(true);
		
		mainRobot.hardwareMap.lfDrive.enable();
		mainRobot.hardwareMap.lrDrive.enable();
		mainRobot.hardwareMap.rfDrive.enable();
		mainRobot.hardwareMap.rrDrive.enable();
		
		assignPower(0,0);
		
		visionHorizontalPID = new PID(mainRobot.programPreferences.getDouble("Vision PID P", 0.006), 
				mainRobot.programPreferences.getDouble("Vision PID I", 0.0), 
				mainRobot.programPreferences.getDouble("Vision PID D", 0.0));
		visionVerticalPID = new PID (VISION_V_PID_P, VISION_V_PID_I, VISION_V_PID_D);
	}

	//Called periodically during teleop
	@Override
	public void teleopPeriodic() 
	{
		//Ensure a gamepad instance exists that we can use...
		if(mainRobot.gamepad1 != null)
		{
			//This just puts the X and Y values of the left joystick of the gamepad onto the smart dashboard
			SmartDashboard.putString("Teleop Info", "Left: X: " + Double.toString(mainRobot.gamepad1.getX(Hand.kLeft)) + " Y: " + Double.toString(mainRobot.gamepad1.getY(Hand.kLeft)));
			
			double forwardsPower = GeneralFunctions.toExponential(GeneralFunctions.deadzone(mainRobot.gamepad1.getThrottle() - mainRobot.gamepad1.getZ(), 0.2), DRIVE_EXPONENT);
			double turningPower = GeneralFunctions.toExponential(GeneralFunctions.deadzone(mainRobot.gamepad1.getX(Hand.kLeft), 0.2), DRIVE_EXPONENT);
			
			leftPower = forwardsPower + turningPower;
			rightPower = forwardsPower - turningPower;
			
			visionAlign();
			
			
			leftPower = GeneralFunctions.clamp(leftPower + visionLeftPower, -1, 1);
			rightPower = GeneralFunctions.clamp(rightPower + visionRightPower, -1, 1);
			
			assignPower(leftPower, rightPower);
			
			//Shooter control, when holding RB the shooter motor starts followed by the loader motor after a short delay
			mShooter.shooterControl(mainRobot.gamepad1.getRawButton(6));
			
			
			//Hold X to use intake
			if (mainRobot.gamepad1.getRawButton(3))
			{
				mainRobot.hardwareMap.intake.set(1);
			}
			else
			{
				mainRobot.hardwareMap.intake.set(0);
			}
			
			
			
			//Hold Y to deposit gear
			if (mainRobot.gamepad1.getRawButton(4))
			{
				mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kForward);
			} else
			{
				mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kReverse);
			}
			
			
			/*
			//Press Y to auto deposit gear
			if(mainRobot.gamepad1.getRawButton(4)){
				gearEnable = true;
			}
			pushGear();
			*/
			
			//Hold B for winch
			if(mainRobot.gamepad1.getRawButton(2))
			{
				mainRobot.hardwareMap.winch.set(-1);
			}
			else
			{
				mainRobot.hardwareMap.winch.set(0);
			}
			
			//Hold left bumper to camera aim
			if (mainRobot.gamepad1.getRawButton(5))
			{
				targetMode = true;
			} else
			{
				targetMode = false;
			}
			
			//Hold A to open gear flap TODO: Might need to be reversed, just change around the 180 and 0 in the gearflap.setAngle()
			if(mainRobot.gamepad1.getRawButton(1))
			{
				mainRobot.hardwareMap.gearFlap.setAngle(180);
			}
			else
			{
				mainRobot.hardwareMap.gearFlap.setAngle(0);
			}
			
		SmartDashboard.putNumber("Rectangle Area", visionProc.getRectangleArea());
		SmartDashboard.putNumber("Rectangle Width", visionProc.getRectangleWidth());
		SmartDashboard.putNumber("Rectangle Aspect", visionProc.getRectangleAspect());
		SmartDashboard.putNumber("Rectangle Distance", visionProc.getTargetDistanceFromCamera());
		SmartDashboard.putNumber("Rectangle X", visionProc.getTargetScreenX());
		SmartDashboard.putNumber("Rectangle Y", visionProc.getTargetScreenY());
		SmartDashboard.putNumber("Gyro Angle", mainRobot.hardwareMap.gyro.getAngle());
		SmartDashboard.putNumber("Accelerometer x", mainRobot.hardwareMap.accelerometer.getX());
		SmartDashboard.putNumber("Accelerometer y", mainRobot.hardwareMap.accelerometer.getY());
		SmartDashboard.putNumber("Accelerometer z", mainRobot.hardwareMap.accelerometer.getZ());
		SmartDashboard.putNumber("PID X Error", pX);
		SmartDashboard.putNumber("PID Y Error", pY);
		SmartDashboard.putBoolean("Pressure Switch Value", mainRobot.hardwareMap.compressor.getPressureSwitchValue());
		SmartDashboard.putBoolean("Compressor", mainRobot.hardwareMap.compressor.getClosedLoopControl());
		SmartDashboard.putBoolean("Target Mode Value", targetMode);
		SmartDashboard.putNumber("Left Shooter Period Length", mainRobot.hardwareMap.hallSR.getPeriod());
		SmartDashboard.putNumber("Right Shooter Period Length", mainRobot.hardwareMap.hallSR.getPeriod());
		
		}
	}

	//Called once right when the robot is disabled
	@Override
	public void teleopDisabledInit() 
	{
		// TODO Auto-generated method stub
		assignPower(0, 0);
		
		mainRobot.hardwareMap.lfDrive.disable();
		mainRobot.hardwareMap.lrDrive.disable();
		mainRobot.hardwareMap.rfDrive.disable();
		mainRobot.hardwareMap.rrDrive.disable();
		mainRobot.hardwareMap.rShooter.disable();
		mainRobot.hardwareMap.lShooter.disable();
		mainRobot.hardwareMap.rShooter.disable();
		mainRobot.hardwareMap.rShooter.disable();
		mainRobot.hardwareMap.rShooter.disable();
		mainRobot.hardwareMap.rShooter.disable();
		
		//Stop the vision system
		visionProc.stopVision();
		
		mainRobot.hardwareMap.compressor.setClosedLoopControl(false);
	}

	//Called periodically while the robot is disabled
	@Override
	public void teleopDisabledPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}
	
	public void getProgramPreferences()
	{
		
	}
	
	public void visionAlign()
	{
		//visionProc.setPidValues(0.006, 0.0, 0);
		//visionProc.pidVisionAim();
		//pidValues = visionProc.pidVisionAim();
		visionHorizontalPID.setParameters(visionProc.getTargetScreenX(), TARGET_WANTED_X);
		pX = visionHorizontalPID.calculateOutput();
		if(targetMode)
		{
			visionLeftPower = -pX;
			visionRightPower = pX;
		}
		else
		{
			visionLeftPower = 0;
			visionRightPower = 0;
		}
		
	}
	
	public void forwardWithGyro(double power, double time)
	{
		//Save the time we started this command
		long startTime = System.currentTimeMillis();
		
		//Set the PID system so the output will be between -1 and 1
		gyroPID.setMaxOutput(1.0);
		try
		{
			//Loop until enough time has passed or this thread has been interrupted
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				//Set the target and sensor values for the PID controller
				gyroPID.setParameters(mainRobot.hardwareMap.gyro.getAngle(), currentHeading);
				
				//PID magic occurs here (OOOOH, AHHHH)
				double pidOutput = gyroPID.calculateOutput();
				
				//for debugging purposes
				/*SmartDashboard.putNumber("Right Power", power - pidOutput);
				SmartDashboard.putNumber("Left Power", power + pidOutput);
				SmartDashboard.putNumber("Gyro Angle", autoProgram.mainRobot.hardwareMap.gyro.getAngle());*/
				
				//Adjust the power accordingly
				assignPower(power + pidOutput, power - pidOutput);
				
				//cullen thing <- Prevent crashing (the program, not the robot, unfortunately)
				//TODO If its crashing its probably this line below. I'm sorry
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}
		
		//stop motors
		assignPower(0,0);
	}
	
	public void assignPower(double powerL, double powerR)
	{
		mainRobot.hardwareMap.lfDrive.set(powerL);
		mainRobot.hardwareMap.lrDrive.set(powerL);
		mainRobot.hardwareMap.rfDrive.set(-powerR);
		mainRobot.hardwareMap.rrDrive.set(-powerR);
	}
	
	public void pushGear()
	{
		
		
		if(!gearBusy && gearEnable){
			gearBusy = true;
			
			switch(gearState){
			case 1:
				mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kForward);
				gearState = 2;
				gearStart = System.currentTimeMillis();
			
			case 2:
				if(System.currentTimeMillis() - gearStart > 600){gearState = 3;}
				
			case 3:
				//Pushing gear further on
				forwardWithGyro(-0.2, 0.65);
				mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kReverse);
				forwardWithGyro(0.13, 1.25);
				
				//Back away from gear
				forwardWithGyro(-0.13, 0.4);
				forwardWithGyro(-0.5, 0.6);
				
				gearBusy = false;
				gearState = 1;
				gearEnable = false;
			}
		
		}
		
		
	}
	
	//Make the robot wait for some time
	/*public void waitForTime(double time)
	{
		//Save the current time
		long startTime = System.currentTimeMillis();
		
		try
		{
			//Loop until enough time has passed
			while (System.currentTimeMillis() - startTime < time * 1000)
			{
				//Don't crash pls
				//TODO omg again im sory
				//Thread.sleep(10);
			}
		} 
	}*/
	
}
