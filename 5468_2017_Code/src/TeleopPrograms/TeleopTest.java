package TeleopPrograms;
import org.usfirst.frc.team5468.robot.*;
import Templates.TeleopProgram;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Plugins.*;

public class TeleopTest extends TeleopProgram
{
	public final double DRIVE_EXPONENT = 2.3;
	
	public double maxOutputPower = 1;
	
	Vision visionProc;
	
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
		maxOutputPower = mainRobot.programPreferences.getDouble("maxDrivePower", 1.0);
		visionProc = new Vision(mainRobot.camera, 320, 240, 30);
		visionProc.setMaskParameters(mainRobot.programPreferences.getInt("Upper Hue", 80),
				mainRobot.programPreferences.getInt("Upper Sat", 255),
				mainRobot.programPreferences.getInt("Upper Val", 170), 
				mainRobot.programPreferences.getInt("Lower Hue", 60), 
				mainRobot.programPreferences.getInt("Lower Sat", 200),
				mainRobot.programPreferences.getInt("Lower Val", 120));
		visionProc.setCameraParameters(mainRobot.programPreferences.getInt("Exposure", 1), 
				mainRobot.programPreferences.getInt("WB", 5200));
		visionProc.startVision();
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
			
			double leftPower = GeneralFunctions.clamp(forwardsPower+turningPower, -1, 1);
			double rightPower = -GeneralFunctions.clamp(forwardsPower-turningPower, -1, 1);
			
			mainRobot.hardwareMap.lfDrive.set(leftPower);
			mainRobot.hardwareMap.lrDrive.set(leftPower);
			
			mainRobot.hardwareMap.rfDrive.set(rightPower);
			mainRobot.hardwareMap.rrDrive.set(rightPower);
			
		}
	}

	//Called once right when the robot is disabled
	@Override
	public void teleopDisabledInit() 
	{
		// TODO Auto-generated method stub
		mainRobot.hardwareMap.lfDrive.set(0);
		mainRobot.hardwareMap.lrDrive.set(0);
		
		mainRobot.hardwareMap.rfDrive.set(0);
		mainRobot.hardwareMap.rrDrive.set(0);
		
		mainRobot.hardwareMap.lfDrive.disable();
		mainRobot.hardwareMap.lrDrive.disable();
		mainRobot.hardwareMap.rfDrive.disable();
		mainRobot.hardwareMap.rrDrive.disable();
		
		visionProc.stopVision();
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
}
