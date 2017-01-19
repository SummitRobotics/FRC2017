package TeleopPrograms;
import org.usfirst.frc.team5468.robot.*;
import Templates.TeleopProgram;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Plugins.*;

public class TeleopTest extends TeleopProgram
{
	//This is called when an instance of this class is created
	public TeleopTest (Robot robot, String name)
	{
		super (robot, name);
	}

	//Called once right when teleop starts
	@Override
	public void teleopInit() 
	{
		// TODO Auto-generated method stub

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
		}
	}

	//Called once right when the robot is disabled
	@Override
	public void teleopDisabledInit() 
	{
		// TODO Auto-generated method stub
		
	}

	//Called periodically while the robot is disabled
	@Override
	public void teleopDisabledPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}
}
