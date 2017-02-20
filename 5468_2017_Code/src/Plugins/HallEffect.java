package Plugins;

import org.usfirst.frc.team5468.robot.*;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HallEffect{
	//magical robot
	Robot robot;
	//circumference
	double circ = Math.PI / 2; // 2 * pi * r --> 2 * pi * 1 /4 ft
	long initialTime;
	int trials = 0;
	//call on the main robot, and thus all related objects
	public HallEffect(Robot x){
		robot = x;
		initialTime = System.currentTimeMillis();
	}
	
	//use for straight encoder applications
	public int[] findCount(){
		int h = robot.hardwareMap.driveL.get();
		int i = robot.hardwareMap.driveR.get();
		int j = robot.hardwareMap.shootL.get();
		int k = robot.hardwareMap.shootR.get();
		int[] counts = new int[]{h,i,j,k};
		return counts;
	}
	
	public double rpm(long startTime){
		int currentPos = robot.hardwareMap.shootL.get();
		int difference = currentPos - trials;
		double duration = initialTime - startTime;
		
		initialTime = startTime;
		trials = currentPos;
		return difference / (duration / 1000);
	}
	
	//find the counts needed to pass x distance
	private int countsGivenFt(double ft){
		return (int)(ft / circ);
	}
	
	//set power to motors prior to calling this function.
	//this will delay until sufficient distance has been traveled
	public void givenDistance(double time, int distance){
		int x = countsGivenFt(distance) + robot.hardwareMap.driveL.get();
		long startTime = System.currentTimeMillis();
		SmartDashboard.putNumber("Iterations remaining",  x - robot.hardwareMap.driveL.get());
		while( (startTime + time*1000 < System.currentTimeMillis()) & x > robot.hardwareMap.driveL.get()){
		}
		SmartDashboard.putNumber("Iterations remaining", 0);
	}
	
	//display iterations of rotations ie magnet detection
	public void displayValues(){
		int[] proxy = findCount();
		SmartDashboard.putNumber("Left Wheels",  proxy[0]);
		SmartDashboard.putNumber("Right Wheels", proxy[1]);
		SmartDashboard.putNumber("Left Shooter", proxy[2]);
		SmartDashboard.putNumber("Right Wheels", proxy[3]);
		SmartDashboard.putNumber("Approximate RPM", rpm(initialTime));
	}
}
