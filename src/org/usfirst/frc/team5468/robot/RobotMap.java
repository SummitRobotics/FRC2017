package org.usfirst.frc.team5468.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.CANTalon;

public class RobotMap 
{
	//Controller Variables
	public final int A_Button = 1;
    public final int B_Button = 2;
    public final int X_Button = 3;
    public final int Y_Button = 4;
    public final int LB_Button = 5;
    public final int RB_Button = 6;
    public final int Back_Button = 7;
    public final int Start_Button = 8;
    public final int LS_Button = 9;
    public final int RS_Button = 10;

	//TALON Ports
	public final int RF_DRIVE_ID = 26;
	public final int RR_DRIVE_ID = 25;
	public final int LF_DRIVE_ID = 21;
	public final int LR_DRIVE_ID = 20;
	
	public final int R_SHOOTER_ID = 27;
	public final int R_LOADER_ID = 28;
	public final int L_SHOOTER_ID = 22;
	public final int L_LOADER_ID = 23;
	
	public final int INTAKE_ID = 29;
	public final int WINCH_ID = 24;
	
	//Sensor Ports
	public final int hallA_ID = 7;
	public final int hallB_ID = 3;
	
	//TALON Hardware References
	public CANTalon rfDrive;
	public CANTalon rrDrive;
	public CANTalon lfDrive;
	public CANTalon lrDrive;
	
	public CANTalon rShooter;
	public CANTalon rLoader;
	public CANTalon lShooter;
	public CANTalon lLoader;
	
	public CANTalon intake;
	public CANTalon winch;
	
	//Pneumatic Hardware References
	public Compressor compressor;
	public DoubleSolenoid solenoid1;
	
	//Sensor Hardware References
	public ADXRS450_Gyro gyro;
	public BuiltInAccelerometer accelerometer;
	
	public Counter hallA;
	public Counter hallB;
	
	//Winch & Intake Settings 
    public final double winchPower = 0; //Motor not currently connected, change to 1 once attached
	public final double intakePower = 1;
	
	//Shooter Settings
	public final double shooterPower = 0.80;
	public final double loaderPower = 1; //Controls blenders as well
	public final double shooterDelay = 1.75; //Delay between shooter motor spin up and loader/blender motor activation
	
	
	//This is called when an instance of this class is created
	public RobotMap()
	{
		//This will be where the hardware instances are created
		try
		{
			rfDrive = new CANTalon(RF_DRIVE_ID);
			rrDrive = new CANTalon(RR_DRIVE_ID);
			lfDrive = new CANTalon(LF_DRIVE_ID);
			lrDrive = new CANTalon(LR_DRIVE_ID);
			
			rShooter = new CANTalon(R_SHOOTER_ID);
			rLoader = new CANTalon(R_LOADER_ID);
			lShooter = new CANTalon(L_SHOOTER_ID);
			lLoader = new CANTalon(L_LOADER_ID);
			
			intake = new CANTalon(INTAKE_ID);
			winch = new CANTalon(WINCH_ID);
			
			gyro = new ADXRS450_Gyro();
			accelerometer = new BuiltInAccelerometer();
			
			rfDrive.enable();
			rrDrive.enable();
			lfDrive.enable();
			lrDrive.enable();
			
			gyro.reset();
			
			compressor = new Compressor(0);
			solenoid1 = new DoubleSolenoid(0, 1);
			
			hallA = new Counter(hallA_ID);
			hallB = new Counter(hallB_ID);
		}
		catch(Exception e)
		{
			
		}	
	}
}
