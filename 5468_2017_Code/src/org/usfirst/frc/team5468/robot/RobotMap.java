package org.usfirst.frc.team5468.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.CANTalon;

public class RobotMap 
{
	public final int RF_DRIVE_ID = 20;
	public final int RR_DRIVE_ID = 21;
	public final int LF_DRIVE_ID = 22;
	public final int LR_DRIVE_ID = 23;
	
	public final int hallA_ID = 2;
	public final int hallB_ID = 3;
	
	public CANTalon rfDrive;
	public CANTalon rrDrive;
	public CANTalon lfDrive;
	public CANTalon lrDrive;
	
	public Compressor compressor;
	public DoubleSolenoid solenoid1;
	
	public ADXRS450_Gyro gyro;
	public BuiltInAccelerometer accelerometer;
	
	//insert hall effect sensors here
	public Counter hallA;
	public Counter hallB;
	
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
