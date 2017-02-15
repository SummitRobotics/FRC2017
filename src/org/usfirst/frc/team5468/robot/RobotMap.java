package org.usfirst.frc.team5468.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.CANTalon;

public class RobotMap 
{
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
	
	public Compressor compressor;
	public DoubleSolenoid solenoid1;
	
	public ADXRS450_Gyro gyro;
	public BuiltInAccelerometer accelerometer;
	
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
		}
		catch(Exception e)
		{
			
		}	
	}
}
