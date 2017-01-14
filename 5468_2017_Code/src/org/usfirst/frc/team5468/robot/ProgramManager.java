package org.usfirst.frc.team5468.robot;
import java.util.List;
import Templates.*;

//find and assign classes given index or string
public class ProgramManager {
	//quantity of classes that exist per group
	public int autonomousProgramCount;
	public int teleopProgramCount;
	
	//where lists of classes are compiled
	public List<AutonomousProgram> autonomousPrograms;
	public List<TeleopProgram> teleopPrograms;	
	
	public ProgramManager(Robot robot){
		// this is where we manually input programs that we will need
		// autonomousPrograms.add(new AutoProgram1(robot, "test"));
		
		//find the numbers of classes
		autonomousProgramCount = autonomousPrograms.size();
		teleopProgramCount = teleopPrograms.size();
		
	}
	
	//find autonomous program via integer
	public AutonomousProgram getAutonomousProgram(int index){
		if(index >= 0 && index < autonomousProgramCount){
			return autonomousPrograms.get(index);
		}
		return null;
	}
	
	//search for autonomous program via string
	public AutonomousProgram getAutonomousProgram(String name){
		//return error if string is not matched
		AutonomousProgram program = null;
		for(int a = 0; a < autonomousProgramCount; ++a){
			if(getAutonomousProgram(a).programName == name){
				//assign the matching class
				program = getAutonomousProgram(a);
				return program;
			}
		}
		return program;
	}
	
	//find teleop program via integer
	public TeleopProgram getTeleopProgram(int index){
		if(index >= 0 && index < teleopProgramCount){
			return teleopPrograms.get(index);
		}
		return null;
	}

	//find teleop program via string
	public TeleopProgram getTeleopProgram(String name){
		//return error if string is not matched
		TeleopProgram program = null;
		for(int a = 0; a < teleopProgramCount; ++a){
			if(getTeleopProgram(a).programName == name){
				//since a matching name has been found
				//assign the program
				program = getTeleopProgram(a);
				return program;
			}
		}
		return program;
	}
		
}
