package org.usfirst.frc.team5468.robot;

public class Shooters{
	
	Robot mRobot;
	
	public Shooters(Robot mainRobot) {
		mRobot = mainRobot;
	}
	
	double shootStart = 0;
	final double SHOOTER_DELAY = 1;
	boolean shootersEnabled = false;
	
	public void shooterControl(boolean shooterButton)
	{
		if (shooterButton)
		{
			if(!shootersEnabled)
			{
				shootersEnabled = true;
				
				shootStart = System.currentTimeMillis();
			}
		} else
		{
			shootersEnabled = false;
		}
		
		if(shootersEnabled)
		{
			mRobot.hardwareMap.rShooter.set(1);
			mRobot.hardwareMap.lShooter.set(-1);
			
			if(System.currentTimeMillis() - shootStart > SHOOTER_DELAY * 1000)
			{
				mRobot.hardwareMap.rLoader.set(1);
				mRobot.hardwareMap.lLoader.set(-1);
			} else
			{
				mRobot.hardwareMap.rLoader.set(0);
				mRobot.hardwareMap.lLoader.set(0);
			}
		} else
		{
			mRobot.hardwareMap.rShooter.set(0);
			mRobot.hardwareMap.lShooter.set(0);
			mRobot.hardwareMap.rLoader.set(0);
			mRobot.hardwareMap.lLoader.set(0);
		}
	}
}
