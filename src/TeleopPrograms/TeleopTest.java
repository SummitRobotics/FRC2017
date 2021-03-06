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
	
	//Max drive power can be adjusted through SmartDashboard
	public double maxOutputPower = 1;
	
	Shooters mShooter;
	
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
		maxOutputPower = mainRobot.programPreferences.getDouble("maxDrivePower", 1.0);
		
		//Create a new instance of the vision system
		visionProc = new Vision("Vision_Test", mainRobot.camera, 320, 240, 30);
		
		//Create new instance of shooters
		mShooter = new Shooters(mainRobot);
		
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
		
		//Start vision processing
		visionProc.startVision();
		
		mainRobot.hardwareMap.compressor.setClosedLoopControl(true);
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
			
			//Shooter control, when holding RB the shooter motor starts followed by the loader motor after a short delay
			mShooter.shooterControl(mainRobot.gamepad1.getRawButton(mainRobot.hardwareMap.RB_Button));
			
			//Hold X to enable intake, may change to a toggle later on
			if (mainRobot.gamepad1.getRawButton(mainRobot.hardwareMap.X_Button))
			{
				mainRobot.hardwareMap.intake.set(mainRobot.hardwareMap.intakePower);
			} else
			{
				mainRobot.hardwareMap.intake.set(0);
			}
			
			//Hold Y to open gear holder
			if (mainRobot.gamepad1.getRawButton(mainRobot.hardwareMap.Y_Button))
			{
				mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kForward);
			} else
			{
				mainRobot.hardwareMap.solenoid1.set(DoubleSolenoid.Value.kReverse);
			}
			
			//Hold LB to activate winch
			if (mainRobot.gamepad1.getRawButton(mainRobot.hardwareMap.LB_Button))
			{
				mainRobot.hardwareMap.winch.set(mainRobot.hardwareMap.winchPower);
			} else
			{
				mainRobot.hardwareMap.winch.set(0);
			}
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
		SmartDashboard.putBoolean("Pressure Switch Value", mainRobot.hardwareMap.compressor.getPressureSwitchValue());
		SmartDashboard.putBoolean("Compressor", mainRobot.hardwareMap.compressor.getClosedLoopControl());
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
}
