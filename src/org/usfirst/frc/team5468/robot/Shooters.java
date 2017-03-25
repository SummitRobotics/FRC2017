package org.usfirst.frc.team5468.robot;

import Plugins.GeneralFunctions;
import Plugins.PID;

public class Shooters
{	
	Robot mRobot;
	
	PID shooterRPID;
	PID shooterLPID;

	double shootStart = 0;
	final double SHOOTER_DELAY = 1.5;
	boolean shootersEnabled = false;
	final double targetShooterPeriod = 0.027;
	

	int state = 1;
	int nextState;
	
	double shooterPowerR;
	double shooterPowerL;
	
	double shooterPIDP;
	double shooterPIDI;
	double shooterPIDD;
	
	public Shooters(Robot robot, double PGain, double IGain, double DGain)
	{
		mRobot = robot;
		shooterPowerR = 0;
		shooterPowerL = 0;
		shooterPIDP = PGain;
		shooterPIDI = IGain;
		shooterPIDD = DGain;
		
		shooterRPID = new PID(shooterPIDP, shooterPIDI, shooterPIDD);
		shooterLPID = new PID(shooterPIDP, shooterPIDI, shooterPIDD);
	}

	public void shooterControl(boolean shooterButton)
	{
		
		//Shooter control loop
				/*if (mRobot.hardwareMap.hallSL.getPeriod() > targetShooterPeriod && shooterButton)
				{
					shooterPowerL += 0.005;
				} 
				else if(mRobot.hardwareMap.hallSL.getPeriod() < targetShooterPeriod && shooterButton)
				{
					shooterPowerL -= 0.005;
				}
				else
				{
					
				}
				
				if (mRobot.hardwareMap.hallSR.getPeriod() > targetShooterPeriod && shooterButton)
				{
					shooterPowerR += 0.005;
				} 
				else if(mRobot.hardwareMap.hallSR.getPeriod() < targetShooterPeriod && shooterButton)
				{
					shooterPowerR -= 0.005;
				}
				else
				{
					
				}*/
		
		if(shooterButton)
		{
			shooterRPID.setParameters(mRobot.hardwareMap.hallSR.getPeriod(), targetShooterPeriod);
			shooterLPID.setParameters(mRobot.hardwareMap.hallSL.getPeriod(), targetShooterPeriod);
			
			shooterPowerR = -shooterRPID.calculateOutput();
			shooterPowerL = -shooterLPID.calculateOutput();
		}
		else
		{
			shooterPowerR = 0;
			shooterPowerL = 0;
		}
				
				shooterPowerR = GeneralFunctions.clamp(shooterPowerR, 0 ,1);
				shooterPowerL = GeneralFunctions.clamp(shooterPowerL, 0 ,1);
				
				//new state machine, should do the same thing.	
				switch(state){
				case 1:
					mRobot.hardwareMap.rShooter.set(0);
					mRobot.hardwareMap.lShooter.set(0);
					mRobot.hardwareMap.rBlender.set(0);
					mRobot.hardwareMap.lBlender.set(0);
					
					if(shooterButton){nextState = 2;}
					shootStart = System.currentTimeMillis();
					break;
			
				case 2:
					
					mRobot.hardwareMap.rShooter.set(-shooterPowerR);
					mRobot.hardwareMap.lShooter.set(shooterPowerL);
					if(System.currentTimeMillis() - shootStart > SHOOTER_DELAY * 1000)
					{
						shootStart = System.currentTimeMillis();
						nextState = 3;
					}
					break;
					
				case 3:
					mRobot.hardwareMap.lShooter.set(shooterPowerL);
					mRobot.hardwareMap.rShooter.set(-shooterPowerR);
					mRobot.hardwareMap.rBlender.set(mRobot.hardwareMap.blenderPower);
					mRobot.hardwareMap.lBlender.set(-mRobot.hardwareMap.blenderPower);
					if(System.currentTimeMillis() - shootStart > 5000)
					{
						nextState = 4;
					}
					break;
					
				case 4:
					mRobot.hardwareMap.lShooter.set(shooterPowerL * 0.93);
					mRobot.hardwareMap.rShooter.set(-shooterPowerR * 0.93);
					mRobot.hardwareMap.rBlender.set(mRobot.hardwareMap.blenderPower);
					mRobot.hardwareMap.lBlender.set(-mRobot.hardwareMap.blenderPower);
					break;
				}
				
				if(!shooterButton)
				{
					nextState = 1;
				}
				
				state = nextState;
				
			}
			
			public double getShooterPower()
			{
				return shooterPowerR;
			}
			public void setShooterPower(double power)
			{
				shooterPowerR = power;
			}
			
			public double getTargetPeriod()
			{
				return targetShooterPeriod;
			}
	}

