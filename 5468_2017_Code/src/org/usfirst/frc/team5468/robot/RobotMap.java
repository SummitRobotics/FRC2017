package org.usfirst.frc.team5468.robot;

import edu.wpi.first.wpilibj.TalonSRX;
import com.ctre.CANTalon;

public class RobotMap 
{
	public final int RF_DRIVE_ID = 20;
	public final int RR_DRIVE_ID = 21;
	public final int LF_DRIVE_ID = 22;
	public final int LR_DRIVE_ID = 23;
	
	public CANTalon rfDrive;
	public CANTalon rrDrive;
	public CANTalon lfDrive;
	public CANTalon lrDrive;
	
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
			
			rfDrive.enable();
			rrDrive.enable();
			lfDrive.enable();
			lrDrive.enable();
		}
		catch(Exception e)
		{
			
		}	
	}
}
