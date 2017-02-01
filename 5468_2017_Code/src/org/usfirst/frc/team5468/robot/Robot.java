
package org.usfirst.frc.team5468.robot;
import Plugins.HallEffect;
import Templates.*;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class Robot extends IterativeRobot 
{
	//Define constant variables
	public final int GAMEPAD_1_INDEX = 0;
	public final int GAMEPAD_2_INDEX = 1;
	
	public RobotMap hardwareMap; //This holds the instance of the hardware manager class
	public ProgramManager programManager; //This holds the instance of the program manager class
	
	//These are the instances of the gamepads that the users will be using
	public Joystick gamepad1;
	public Joystick gamepad2;
	
	public Preferences programPreferences;
	
	//These are the instances of the program choosers for the autonomous and teleop programs
	SendableChooser<String> autoChooser;
	SendableChooser<String> teleopChooser;
	
	//These hold the actual instances for the autonomous and teleop programs that the robot will be using
	AutonomousProgram auto = null;
	TeleopProgram teleop = null;
	
	public UsbCamera camera;
	HallEffect sensor;
	
	/*
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() 
	{
		//Initialize the hardware and program managers
		hardwareMap = new RobotMap();
		programManager = new ProgramManager(this);
		sensor = new HallEffect(this);
		
		//Initialize the smart dashboard choosers for the autonomous and teleop programs
		autoChooser = new SendableChooser<String>();
		teleopChooser = new SendableChooser<String>();
		
		programPreferences = Preferences.getInstance();
		
		//Setup the program choosers
		setupProgramChooser();
		
		//Setup the joysticks
		setupJoysticks();
		
		camera = CameraServer.getInstance().startAutomaticCapture();
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the only purpose in life is the search thereof
	 */
	@Override
	public void disabledInit() 
	{
		//Call the proper methods in the autonomous and teleop programs (if they exist)
		if(auto != null)
		{
			auto.autonomousDisabledInit();
		}
		
		if(teleop != null)
		{
			teleop.teleopDisabledInit();
		}
	}
	
	//called periodic
	@Override
	public void disabledPeriodic() 
	{
		hardwareMap.compressor.setClosedLoopControl(false);
		
		//Call the proper methods in the autonomous and teleop programs (if they exist)
		if(auto != null)
		{
			auto.autonomousDisabledPeriodic();
		}
		
		if(teleop != null)
		{
			teleop.teleopDisabledPeriodic();
		}
	}
	
	//when autononmous is selected
	//find the selected class and run it
	@Override
	public void autonomousInit() 
	{
		//Get the currently selected autonomous program and attempt to find a matching program instance
		//If a program can't be found, auto will be null
		auto = programManager.getAutonomousProgram(autoChooser.getSelected());
		
		//Call the proper method in the autonomous program (if it exists)
		if(auto != null)
		{
			auto.autonomousInit();
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() 
	{
		//Call the proper method in the autonomous program (if it exists)
		if(auto != null)
		{
			auto.autonomousPeriodic();
		}
	}

	//when teleop is selected
	//find the selected class and run it
	@Override
	public void teleopInit() 
	{
		//Get the currently selected teleop program and attempt to find a matching program instance
		//If a program can't be found, teleop will be null
		teleop = programManager.getTeleopProgram(teleopChooser.getSelected());
		
		hardwareMap.compressor.setClosedLoopControl(true);
		
		//Call the proper method in the teleop program (if it exists)
		if(teleop != null)
		{
			teleop.teleopInit();
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() 
	{
		//Call the proper method in the teleop program (if it exists)
		if(teleop != null)
		{
			teleop.teleopPeriodic();
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() 
	{
		sensor.demo();
	}
	
	//Setup the program choosers for the autonomous and teleop programs
	public void setupProgramChooser()
	{
		//Set a default autonomous option for no autonomous program
		autoChooser.addDefault("none", null);
		
		//For each available autonomous program...
		for(int i = 0; i < programManager.autonomousProgramCount; i++)
		{
			//Add the autonomous program to the auto chooser's program list
			autoChooser.addObject(programManager.getAutonomousProgram(i).programName, programManager.getAutonomousProgram(i).programName);
		}
		
		//Set a default teleop option for no teleop program
		teleopChooser.addDefault("none", null);
		
		//For each available teleop program...
		for(int i = 0; i < programManager.teleopProgramCount; i++)
		{
			//Add the teleop program to the teleop chooser's program list
			teleopChooser.addObject(programManager.getTeleopProgram(i).programName, programManager.getTeleopProgram(i).programName);
		}
		
		//Put the setup program choosers onto the smart dashboard
		SmartDashboard.putData("Autonomous Chooser", autoChooser);
		SmartDashboard.putData("Teleop Chooser", teleopChooser);
	}
	
	//This function sets up the instances of the joysticks
	public void setupJoysticks()
	{
		try
		{
			//Make new instances of the joysticks using the set joystick indices
			gamepad1 = new Joystick(GAMEPAD_1_INDEX);
			gamepad2 = new Joystick(GAMEPAD_2_INDEX);
		} catch (Exception e)
		{
			
		}
	}
}
