
package org.usfirst.frc.team5468.robot;
import Templates.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class Robot extends IterativeRobot 
{
	public RobotMap robotMap;
	public ProgramManager programManager;
	
	SendableChooser<String> autoChooser;
	SendableChooser<String> teleopChooser;
	
	AutonomousProgram auto = null;
	TeleopProgram teleop = null;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() 
	{
		//initialize your life
		robotMap = new RobotMap(this);
		programManager = new ProgramManager(this);
		
		autoChooser = new SendableChooser<String>();
		teleopChooser = new SendableChooser<String>();
		
		//arrange all given classes for the USER and GUI
		setupProgramChooser();
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the only purpose in life is the search thereof
	 */
	@Override
	public void disabledInit() 
	{
		if(auto != null){
			auto.autonomousDisabledInit();
		}
		if(teleop != null){
			teleop.teleopInit();
		}
	}
	
	//called periodic
	@Override
	public void disabledPeriodic() 
	{
		if(auto != null){
			auto.autonomousDisabledPeriodic();
		}
		if(teleop != null){
			teleop.teleopDisabledPeriodic();
		}
	}
	
	//when autononmous is selected
	//find the selected class and run it
	@Override
	public void autonomousInit() 
	{
		auto = programManager.getAutonomousProgram(autoChooser.getSelected());
		if(auto != null){
			auto.autonomousInit();
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() 
	{
		if(auto != null){
			auto.autonomousPeriodic();
		}
	}

	//when teleop is selected
	//find the selected class and run it
	@Override
	public void teleopInit() 
	{
		teleop = programManager.getTeleopProgram(autoChooser.getSelected());
		if(teleop != null){
			teleop.teleopInit();
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() 
	{
		if(teleop != null){
			teleop.teleopPeriodic();
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() 
	{

	}
	
	//setup the names of the classes for the user
	public void setupProgramChooser(){
		//set the initial class
		autoChooser.addDefault("none", null);
		for(int a = 0; a < programManager.autonomousProgramCount; ++a){
			autoChooser.addObject(programManager.getAutonomousProgram(a).programName, programManager.getAutonomousProgram(a).programName);
		}
		
		teleopChooser.addDefault("none", null);
		for(int a = 0; a < programManager.teleopProgramCount; ++a){
			teleopChooser.addObject(programManager.getTeleopProgram(a).programName, programManager.getTeleopProgram(a).programName);
		}
		
		SmartDashboard.putData("Autonomous Chooser", autoChooser);
		SmartDashboard.putData("Teleop Chooser", teleopChooser);
	}
}
