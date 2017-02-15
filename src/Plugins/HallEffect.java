package Plugins;

import org.usfirst.frc.team5468.robot.*;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HallEffect{
	//magical robot
	Robot robot;
	//rotations for both sides of the drive train
	//for now, only a will be used
	int a, b;
	//circumference
	double circ = Math.PI / 2; // 2 * pi * r --> 2 * pi * 1 /4 ft
	
	//call on the main robot, and thus all related objects
	public HallEffect(Robot x){
		robot = x;
	}
	
	//use for straight encoder applications
	public int findCountA(){
		return robot.hardwareMap.hallA.get();
	}
	
	//find the counts needed to pass x distance
	public int countsGivenFt(double ft){
		return (int)(ft / circ);
	}
	
	//set power to motors prior to calling this function.
	//this will delay until sufficient distance has been traveled
	public void givenDistance(double time, int counts){
		int x = counts + findCountA();
		long startTime = System.currentTimeMillis();
		SmartDashboard.putNumber("Iterations remaining",  x - findCountA());
		while( (startTime + time*1000 < System.currentTimeMillis()) & x > findCountA()){
		}
		SmartDashboard.putNumber("Iterations remaining", 0);
	}
	
	//display iterations of rotations ie magnet detection
	public void demo(){
		SmartDashboard.putNumber("magnet", findCountA());
	}
}
