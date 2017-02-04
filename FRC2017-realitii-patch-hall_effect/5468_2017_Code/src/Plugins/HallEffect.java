package Plugins;

import org.usfirst.frc.team5468.robot.*;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HallEffect{
	Robot robot;
	int a, b;
	double circ = 1;
	
	
	public HallEffect(Robot x){
		robot = x;
	}
	
	
	public void initialize(){
		robot.hardwareMap.hallA.reset();
		robot.hardwareMap.hallB.reset();
	}
	
	//returns counts(s)
	public int[] findCount(){
		a = robot.hardwareMap.hallA.get();
		b = robot.hardwareMap.hallB.get();
		int[] output = new int[2];
		output[0] = a;
		output[1] = b;
		
		return output;
	}
	
	//use for straight encoder applicatiosn
	public int findCountA(){
		return robot.hardwareMap.hallA.get();
	}
	
	public int countsGivenFt(double ft){
		return (int)(ft / circ);
	}
	
	//set power to motors prior to calling this function.
	//this will delay until sufficient distance has been traveled
	public void givenDistance(double time, double distance){
		int x = countsGivenFt(distance) + findCountA();
		long startTime = System.currentTimeMillis();
		while(!Thread.interrupted() & (startTime + time*1000 < System.currentTimeMillis()) & x > findCountA()){
			SmartDashboard.putNumber("Iterations remaining",  x - findCountA());
		}
	}
	
	public void demo(){
		SmartDashboard.putNumber("magnet", findCountA());
	}
}
