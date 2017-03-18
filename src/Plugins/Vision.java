		package Plugins;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team5468.robot.Robot;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;import edu.wpi.first.wpilibj.CameraServer;

public class Vision 
{
	//This will hold an instance of the camera being used for vision
	public UsbCamera camera;
	
	
	PID pid;
	
	public double pV = 1;
	public double iV = 3;
	public double dV = 0.2;
	
	double pidResultX = 0;
	double pidResultY = 0;
	
	//Center cordinates of ideal position
	public double iX;
	public double iY;
	
	//Cache the width and height of the image
	public int imgWidth, imgHeight;
	
	//This will hold the instance of the actual vision thread
	VisionThread visionThread;
	
	public String visionThreadName;
	
	//Framerate setting
	int updateFrequency;
	
	//Settings for the HSV mask bounds
	int upperHBound, upperSBound, upperVBound, lowerHBound,lowerSBound, lowerVBound;
	
	//Called when this class is created
	public Vision(String name, UsbCamera videoStream, int width, int height, int updateFramerate)
	{
		visionThreadName = name;
		
		//Cache an instance of the camera
		camera = videoStream;
		
		//Set the camera's resolution and FPS
		camera.setResolution(width, height);		
		camera.setFPS(updateFramerate);
		
		//Cache the image size and framerate
		imgWidth = width;
		imgHeight = height;
		updateFrequency = updateFramerate;
		
		
		pid = new PID(pV,iV,dV);
		
	}
	
	//This function starts the vision thread
	public void startVision()
	{		//If there is no instance of the vision thread...
		if(visionThread == null)
		{
			//Create a new instance of the vision thread
			visionThread = new VisionThread(visionThreadName, camera, imgWidth, imgHeight, updateFrequency);
		}
				//Set the vision's HSV mask bounds
		visionThread.setMaskParameters(upperHBound, upperSBound, upperVBound, lowerHBound, lowerSBound, lowerVBound);
		
	//Actually run the thread
		visionThread.start();
	}
	
	//This function stops the vision thread
	public void stopVision()
	{
		//If an instance of the vision thread exists...
		if(visionThread != null)
		{	
			//Interrupt the vision thread - this sets the Thread.interrupted() parameter to true
			visionThread.interrupt();
		}
	}
	
	//This sets the HSV mask parameters
	public void setMaskParameters(int upperH, int upperS, int upperV, int lowerH, int lowerS, int lowerV)
	{
		//Cache the mask values
		upperHBound = upperH;
		upperSBound = upperS;
		upperVBound = upperV;
		
		lowerHBound = lowerH;
		lowerSBound = lowerS;
		lowerVBound = lowerV;
		
		//If an instance of the vision thread exists...
		if(visionThread != null)
		{
			//Set the vision thread's mask parameters
			visionThread.setMaskParameters(upperH, upperS, upperV, lowerH, lowerS, lowerV);
		}
	}
	
	//This function sets the camera's exposure and white balance
	public void setCameraParameters(int exposure, int whiteBalance, int brightness)
	{
		camera.setExposureAuto();
		camera.setBrightness(brightness);
		camera.setWhiteBalanceManual(whiteBalance);
	}
	
	//This function returns the width of the current largest rectangle being tracked
	public double getRectangleWidth()
	{
		if(visionThread != null && visionThread.targetsFound > 0)
			return visionThread.target1.boundingRect().width;
		
		return 0;
	}
	
	//This function returns the area of the current largest rectangle being tracked
	public double getRectangleArea()
	{
		if(visionThread != null && visionThread.targetsFound > 0)
			return visionThread.target1.size.area();
			
		return 0;
	}
	
	//This function returns the aspect ratio of the current largest rectangle being tracked
	public double getRectangleAspect()
	{
		if(visionThread != null && visionThread.targetsFound > 0)
			return visionThread.target1.size.width / visionThread.target1.size.height;
			
		return 0;
	}
	
	//This function returns the distance that the currently tracked target is from the camera
	public double getTargetDistanceFromCamera()
	{
		if(visionThread != null && visionThread.targetsFound > 0)
			return visionThread.targetDistance;
			
		return 0;
	}
	
	//This function returns the x screen position of the currently tracked target
	public double getTargetScreenX()
	{
		if(visionThread != null && visionThread.targetsFound > 0)
			return visionThread.target1.boundingRect().x;
			
		return 0;
	}
	
	//This function returns the y screen position of the currently tracked target
	public double getTargetScreenY()
	{
		if(visionThread != null && visionThread.targetsFound > 0)
			return visionThread.target1.boundingRect().y;
			
		return 0;
	}
	
	public double[] getVisionShift(double power){
		return visionThread.visionPID(power);
	}
	
	public void setPidValues(double p, double i, double d)
	{
		pV = p;
		iV = i;
		dV = d;
		pid = new PID(pV,iV,dV);
	}
	
	public double[] pidVisionAim()
	{
		double tX = getTargetScreenX();
		double tY = getTargetScreenX();
		
		pid.setGains(pV, iV, dV);
		
		//Calculate X Error
		pid.setParameters(tX, iX);
		pidResultX = pid.calculateOutput();
		
		//Calculate Y Error
		pid.setParameters(tY, iY);
		pidResultY = pid.calculateOutput();
		
		return new double[] {pidResultX, pidResultY};
	}
	
	
}

//This is the class that will be executed on a separate thread
class VisionThread extends Thread
{
	String threadName;
	
	//Target filtering variables
	final double minTargetSize = 5; //The minimum area a rectangle has to take up on-screen to be considered as a target
	
	//Variables for calculating target distance
	//To use distance calculation correctly, you must measure the width of a target (in pixels) from a known distance away from the camera
	final double cameraFOV = 60; //The horizontal FOV of the camera (in degrees)
	final double fovPlaneDistance = 5; //The distance from the camera that the target's size was measured (in feet)
	final double targetWidth = 10.0 / 12.0; //The width of the target (this should be the width of the area actually seen by the camera)
	final double targetWidthPixels = 61; //The width of the target on-screen measured at "fovPlaneDistance" away from the camera (in pixels)
	final double targetWidthConversion = targetWidth / targetWidthPixels; //This creates a conversion to convert target width from pixels to feet
	final double gearTargetWidth = 0;
	
	//References to get and put video frames
	CvSink videoIn;
	CvSource videoOut;
	
	//References to hold images
	Mat inputImage;
	Mat outputImage;
	Mat hsvImage;
	Mat blurredImage;
	Mat hierarchy;
	Mat maskImage;
	
	//HSV mask bounds
	Scalar lowerBounds;
	Scalar upperBounds;
	
	List<MatOfPoint> contours;
	
	//The time (in milliseconds) that the vision thread should wait
	int threadWait;
	
	//These are variables returned by the vision system
	//Since this class is being executed on a separate thread, they have to be volatile to make sure no conflicts arise when another script tries to access them
	public volatile RotatedRect target1;
	public volatile RotatedRect target2;
	public volatile int targetsFound;
	public volatile double targetDistance;
	
	//Called when an instance of this class is created
	public VisionThread (String name, VideoSource videoSource, int width, int height, double framerate)
	{
		threadName = name;
		
		//Setup videoIn to serve video frames
		videoIn = CameraServer.getInstance().getVideo(videoSource);
		
		//Setup a video server that the smartdashboard can view
		videoOut = CameraServer.getInstance().putVideo(threadName, width, height);
		
		//Initialize the image references
		inputImage = new Mat();
		outputImage = new Mat();
		hsvImage = new Mat();
		hierarchy = new Mat();
		blurredImage = new Mat();
		maskImage = new Mat();
		
		//Initialize our target variables
		target1 = new RotatedRect();
		target2 = new RotatedRect();
		targetsFound = 0;
		
		//Calculate how long the thread should wait given a frequency (and ensure it waits at least 1 ms)
		threadWait = Math.max((int)(Math.round(1.0 / framerate)) * 1000, 1);
		
		//Intialize the HSV mask bounds to default values
		lowerBounds = new Scalar(0,0,0);
		upperBounds = new Scalar(0,0,0);
		
		contours = new ArrayList<MatOfPoint>();
	}
	
	//This function sets the HSV mask parameters
	public void setMaskParameters(int upperH, int upperS, int upperV, int lowerH, int lowerS, int lowerV)
	{
		lowerBounds = new Scalar(lowerH, lowerS, lowerV);
		upperBounds = new Scalar(upperH, upperS, upperV);
	}
	
	//This function is run on a separate thread when the visionThread.start() method is called
	public void run()
	{
		//Catch an InterruptedExceptions
		try
		{	
			//Loop until this thread is interrupted
			while(!Thread.interrupted())
			{				
				//Get the most recent image from the camera and store it
				videoIn.grabFrame(inputImage);
				
				//Process the camera's image
				processImage();
				
				//Process the target
				processTarget();
				
				//Draws ellipses over the two tracked targets for debugging
				visionDebug();
				
				//Have this thread wait for some time
				Thread.sleep(Math.max(threadWait / 2, 1));
			}
			
		}
		catch(InterruptedException e) {}
	}
	
	//This function takes whatever image is stored in "inputImage" and processes it to find contours
	void processImage()
	{
		//Blur the image slightly to reduce artifacts
		Imgproc.blur(inputImage, blurredImage, new Size(2, 2));
		
		//Convert the RGB image into an HSV image
		Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
		
		//Apply a color mask to the HSV image - and colors within the range are turned white, and the rest, black
		Core.inRange(hsvImage, lowerBounds, upperBounds, maskImage);
		
		//Store the mask in a separate variable for debugging
		outputImage = maskImage.clone();
		
		//Clear the list of contours
		contours.clear();
		
		//Find all image contours and store them in the contours list
		Imgproc.findContours(maskImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
	}
	
	//This function processes the HSV mask and works out target variables
	void processTarget()
	{
		//Reset the number of found targets
		targetsFound = 0;
		
		//If we have contours...
		if (contours.size() > 0)
		{
			//Variables to cache values during the sorting process
			int biggestAreaIndex = -1;
			int secondBiggestAreaIndex = -1;
			double largestArea = 0;
			double secondLargestArea = 0;
			RotatedRect currentRect = new RotatedRect();
					
			//Find the two largest rectangles and store them into their respective indices
			for(int i = 0; i < contours.size(); i++)
			{
				//Make a rectangle that fits the current contours
				currentRect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
				
				//Only process this rectangle if it is possibly a target
				if(isPossibleTarget(currentRect))
				{
					//Store the current rectangle's area
					double area = currentRect.size.area();
					
					//If this rectangle's area is greater than the current largest area...
					if(area > largestArea)
					{
						//Save the previous largest rectangle as the second largest
						secondBiggestAreaIndex = biggestAreaIndex;
						secondLargestArea = largestArea;
						
						//Save the current rectangle as the largest
						biggestAreaIndex = i;
						largestArea = area;
					}//If this area isn't the largest, but is largest than the current second largest...
					else if(area > secondLargestArea)
					{
						//Save this rectangle as the second largest
						secondLargestArea = area;
						secondBiggestAreaIndex = i;
					}
				}
			}
			
			//Store the largest rectangle
			if(biggestAreaIndex >= 0)
			{
				target1 = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(biggestAreaIndex).toArray()));
				targetsFound++;
				
				targetDistance = calculateDistance(target1.boundingRect().width);
			}
			
			//Store the second largest rectangle
			if(secondBiggestAreaIndex >= 0)
			{
				target2 = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(secondBiggestAreaIndex).toArray()));
				targetsFound++;
			}
		}
	}
	
	//This function draws ellipses over the tracked targets
	void visionDebug()
	{
		if(target1 != null)
		{
			Imgproc.ellipse(inputImage, target1, new Scalar(255, 0, 255), 3);
		}
		
		if(target2 != null)
		{
			Imgproc.ellipse(inputImage, target2, new Scalar(0, 0, 255), 3);
		}
		
		//Put the processed image into the server so that the smartdashboard can view it
		videoOut.putFrame(outputImage);
	}
	
	//This function returns whether a rectangle could potentially be a target
	boolean isPossibleTarget(RotatedRect rect)
	{
		return rect.size.area() > minTargetSize;
	}
	
	
	//This function calculates how far a target is from the camera
	double calculateDistance(double targetSizeX)
	{
		//Convert the target's width from pixels to feet and halve that, since we're only going to be using half of the image
		double targetWidthFt = targetSizeX * targetWidthConversion * 0.5;
		
		//Calculate the angle that the right-most edge of the target is from the center of a line extending straight from the camera
		double targetAngle = Math.atan((targetWidthFt * Math.tan(cameraFOV * 0.5 * (Math.PI / 180.0))) / fovPlaneDistance);
		
		//Scale a triangle that fits the calculated target angle and has an end length of half the width of the target
		return (targetWidth * 0.5) / Math.tan(targetAngle);
	}
	
	//Don't delete these methods = even if you think they will spawn satan
	//because... the ritual hasn't been completed yet.
	//find the average distance the middle of the polygons is from the center of camera
	
	//how much error will be allowed
	double tolerance = 0;
	int findDeviation(){
		double center;
		if(target1.center.x > target2.center.x){
			center = target1.center.x - target2.center.x;		
		}
		else{
			center = target2.center.x - target1.center.x;
		}
		if(Math.abs(center) < tolerance){
			return 0;
		}
		return (int) (center - (inputImage.width() / 2)) / inputImage.width();
	}
	
	
	//adjust direction of bot relative to reflective tape
	public double[] visionPID(double a){
		double r = 1 - a;
		int x = 0, y = 0;
		if(findDeviation() < 0){
			x += findDeviation() * r;
		}
		else{
			y += findDeviation() * r;
		}
		double[] z = new double[]{x,y};
		return z;
	}
	
	
}
