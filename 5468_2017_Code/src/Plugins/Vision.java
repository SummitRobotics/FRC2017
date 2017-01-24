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
		//Framerate setting
	int updateFrequency;
	
	//Settings for the HSV mask bounds
	int upperHBound, upperSBound, upperVBound, lowerHBound,lowerSBound, lowerVBound;
	
	//Called when this class is created
	public Vision(UsbCamera videoStream, int width, int height, int updateFramerate)
	{
		//Cache an instance of the camera
		camera = videoStream;
		
		//Set the camera's resolution and FPS
		camera.setResolution(width, height);		camera.setFPS(updateFramerate);
		
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
			visionThread = new VisionThread(camera, imgWidth, imgHeight, updateFrequency);
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
	public VisionThread (VideoSource videoSource, int width, int height, double updateFrequency)
	{
		//Setup videoIn to serve video frames
		videoIn = CameraServer.getInstance().getVideo(videoSource);
		
		//Setup a video server that the smartdashboard can view
		videoOut = CameraServer.getInstance().putVideo("vision", width, height);
		inputImage = new Mat();
		outputImage = new Mat();
		hsvImage = new Mat();
		threadWait = (int)Math.round(1.0/updateFrequency);
		lowerBounds = new Scalar(0,0,0);
		upperBounds = new Scalar(0,0,0);
	}
	
	public void setMaskParameters(int upperH, int upperS, int upperV, int lowerH, int lowerS, int lowerV){
		lowerBounds = new Scalar(lowerH, lowerS, lowerV);
		upperBounds = new Scalar(upperH, upperS, upperV);
	}
	
	public void run(){
		try{
			
			while(!Thread.interrupted()){
				
				videoIn.grabFrame(inputImage);
				
				Imgproc.cvtColor(inputImage, hsvImage, Imgproc.COLOR_BGR2HSV);
				Core.inRange(hsvImage, lowerBounds, upperBounds, outputImage);
				
				videoOut.putFrame(outputImage);
				
				
				Thread.sleep(threadWait);
				
			}
			
		}
		catch(InterruptedException e){
			
		}
	}
}
