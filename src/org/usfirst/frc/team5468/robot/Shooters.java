package org.usfirst.frc.team5468.robot;

public class Shooters{
	
	Robot mRobot;
	
	public Shooters(Robot mainRobot) {
		mRobot = mainRobot;
	}
	
	double shootStart = 0;
	final double SHOOTER_DELAY = mRobot.hardwareMap.shooterDelay;
	boolean shootersEnabled = false;
	
	int state = 1;
	int nextState;

	public void shooterControl(boolean shooterButton)
	{
	//new state machine, should do the same thing.	
		switch(state){
		case 1:
			mRobot.hardwareMap.rShooter.set(0);
			mRobot.hardwareMap.lShooter.set(0);
			mRobot.hardwareMap.rLoader.set(0);
			mRobot.hardwareMap.lLoader.set(0);
			
			if(shooterButton){nextState = 2;}
			shootStart = System.currentTimeMillis();
			if(!shooterButton){nextState = 1;}
			break;
			
		case 2:
			mRobot.hardwareMap.rShooter.set(-mRobot.hardwareMap.shooterPower);
			mRobot.hardwareMap.lShooter.set(mRobot.hardwareMap.shooterPower);
			if(System.currentTimeMillis() - shootStart > SHOOTER_DELAY * 1000){nextState = 3;}
			if(!shooterButton){nextState = 1;}
			break;
			
		case 3:
			mRobot.hardwareMap.rShooter.set(-mRobot.hardwareMap.shooterPower);
			mRobot.hardwareMap.lShooter.set(mRobot.hardwareMap.shooterPower);
			mRobot.hardwareMap.rLoader.set(mRobot.hardwareMap.loaderPower);
			mRobot.hardwareMap.lLoader.set(-mRobot.hardwareMap.loaderPower);
			if(!shooterButton){nextState = 1;}
			break;
		}
		
		state = nextState;
		
	}
}
