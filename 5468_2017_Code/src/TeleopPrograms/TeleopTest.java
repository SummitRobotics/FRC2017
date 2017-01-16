package TeleopPrograms;
import org.usfirst.frc.team5468.robot.*;
import Templates.TeleopProgram;
import Plugins.*;

public class TeleopTest extends TeleopProgram
{
	public TeleopTest (Robot robot, String name)
	{
		super (robot, name);
	}

	@Override
	public void teleopInit() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void teleopPeriodic() 
	{
		// TODO Auto-generated method stub
		if(mainRobot.gamepad1 != null)
		{
			
		}
	}

	@Override
	public void teleopDisabledInit() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleopDisabledPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}
}
