package org.usfirst.frc.team5468.robot;
import java.util.ArrayList;
import java.util.List;

import AutonomousPrograms.prototype;
import Templates.*;
import TeleopPrograms.*;

public class ProgramManager 
{
	//These hold the number of autonomous and teleop programs
	public int autonomousProgramCount;
	public int teleopProgramCount;
	
	//These hold lists of the autonomous and teleop program classes
	public List<AutonomousProgram> autonomousPrograms;
	public List<TeleopProgram> teleopPrograms;	
	
	//Called when an instance of this class is created
	public ProgramManager(Robot robot)
	{
		//Setup the autonomous and teleop program lists (makes the variables no longer null)
		autonomousPrograms = new ArrayList<>();
		teleopPrograms = new ArrayList<>();
		
		//Add the robot's autonomous programs here...
		autonomousPrograms.add(new prototype(robot, "CTRLib"));
		autonomousPrograms.add(new path1(robot, "Basic Gear"));
		autonomousPrograms.add(new path2(robot, "Advanced Gear"));
		autonomousPrograms.add(new path3(robot, "Advanced Gear / Shoot"));
		//Add the robot's teleop programs here...
		teleopPrograms.add(new TeleopTest(robot, "Teleop Test"));
		
		//Cache the number of autonomous and teleop classes
		autonomousProgramCount = autonomousPrograms.size();
		teleopProgramCount = teleopPrograms.size();
		
	}
	
	//Find an autonomous program via an index
	public AutonomousProgram getAutonomousProgram(int index)
	{
		//If the index of the program we're looking for is within the range of indices of our autonomous programs...
		if(index >= 0 && index < autonomousProgramCount)
		{
			//Return the autonomous program that corresponds to the current index
			return autonomousPrograms.get(index);
		}
		
		//The index is incorrect, so return nothing
		return null;
	}
	
	//Find an autonomous program via its name
	public AutonomousProgram getAutonomousProgram(String name)
	{
		//Setup a return variable and initialize it to null as an error state
		AutonomousProgram program = null;
		
		//For each autonomous program...
		for(int i = 0; i < autonomousProgramCount; i++)
		{
			//Check whether the name of the current autonomous program we're checking is the same as the given name
			if(getAutonomousProgram(i).programName == name)
			{
				//Set the return variable to be this autonomous program (since we found a match)
				program = getAutonomousProgram(i);
				
				//Break out of the for loop, since we found a match
				break;
			}
		}
		
		//Return an autonomous program (if one was found)
		return program;
	}
	
	//Find a teleop program via an index
	public TeleopProgram getTeleopProgram(int index)
	{
		//If the index of the program we're looking for is within the range of indices of our teleop programs...
		if(index >= 0 && index < teleopProgramCount)
		{
			//Return the teleop program that corresponds to the current index
			return teleopPrograms.get(index);
		}
		
		//The index is incorrect, so return nothing
		return null;
	}

	//Find a teleop program via its name
	public TeleopProgram getTeleopProgram(String name)
	{
		//Setup a return variable and initialize it to null as an error state
		TeleopProgram program = null;
		
		//For each teleop program...
		for(int i = 0; i < teleopProgramCount; i++)
		{
			//Check whether the name of the current autonomous program we're checking is the same as the given name
			if(getTeleopProgram(i).programName == name)
			{
				//Set the return variable to be this teleop program (since we found a match)
				program = getTeleopProgram(i);
				
				//Break out of the for loop, since we found a match
				break;
			}
		}
		
		//Return a teleop program (if one was found)
		return program;
	}
		
}
