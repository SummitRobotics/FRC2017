package Plugins;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;import edu.wpi.first.wpilibj.CameraServer;

public class Vision 
{
	//This will hold an instance of the camera being used for vision
	public UsbCamera camera;
	
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
	public void setCameraParameters(int exposure, int whiteBalance)
	{
		camera.setExposureManual(exposure);
		camera.setWhiteBalanceManual(whiteBalance);
	}
}

//This is the class that will be executed on a separate thread
class VisionThread extends Thread
{
	String threadName;
	
	//References to get and put video frames
	CvSink videoIn;
	CvSource videoOut;
	
	//References to hold images
	Mat inputImage;
	Mat outputImage;
	Mat hsvImage;
	
	//HSV mask bounds
	Scalar lowerBounds;
	Scalar upperBounds;
	
	//The time (in milliseconds) that the vision thread should wait
	int threadWait;
	
	//Called when an instance of this class is created
	public VisionThread (String name, VideoSource videoSource, int width, int height, double updateFrequency)
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
		
		//Calculate how long the thread should wait given a frequency
		threadWait = (int)Math.round(1.0/updateFrequency);
		
		//Intialize the HSV mask bounds to default values
		lowerBounds = new Scalar(0,0,0);
		upperBounds = new Scalar(0,0,0);
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
				long startTime = System.nanoTime();
				
				//Get the most recent image from the camera and store it
				videoIn.grabFrame(inputImage);
				
				//Process the camera's image
				processImage();
				
				//Process the target
				processTarget();
				
				//Put the processed image into the server so that the smartdashboard can view it
				videoOut.putFrame(outputImage);
				
				//Calculate how many milliseconds it took to run the vision processing code
				long millisecondsTaken = (System.nanoTime() - startTime) % 1000;
				long waitTime = threadWait - millisecondsTaken;
				
				//Have this thread wait for some time to achieve a certain framerate
				Thread.sleep(waitTime);
			}
			
		}
		catch(InterruptedException e)
		{
			
		}
	}
	
	//This function takes whatever image is stored in "inputImage", processes it, and saves the output in "outputImage"
	void processImage()
	{
		//Vision procession code goes here
		//Convert the RGB image into an HSV image
		Imgproc.cvtColor(inputImage, hsvImage, Imgproc.COLOR_BGR2HSV);
		
		//Apply a color mask to the HSV image - and colors within the range are turned white, and the rest, black
		Core.inRange(hsvImage, lowerBounds, upperBounds, outputImage);
	}
	
	//This function processes the HSV mask and works out target variables
	void processTarget()
	{
		//TODO: Write target processing code
	}
}
