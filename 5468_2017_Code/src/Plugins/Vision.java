package Plugins;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.CameraServer;


public class Vision 
{
	public UsbCamera camera;
	public int imgWidth, imgHeight;
	
	VisionThread visionThread;
	
	int updateFrequency;
	
	int upperHBound, upperSBound, upperVBound, lowerHBound,lowerSBound, lowerVBound;
	
	public Vision(UsbCamera videoStream, int width, int height, int updateFramerate)
	{
		camera = videoStream;
		camera.setResolution(width, height);
		camera.setFPS(updateFramerate);
		imgWidth = width;
		imgHeight = height;
		updateFrequency = updateFramerate;
	}
	
	public void startVision(){
		if(visionThread == null){
			visionThread = new VisionThread(camera, imgWidth, imgHeight, updateFrequency);
		}
		visionThread.setMaskParameters(upperHBound, upperSBound, upperVBound, lowerHBound, lowerSBound, lowerVBound);
		visionThread.start();
	}
	
	public void stopVision(){
		if(visionThread != null){
			visionThread.interrupt();
		}
	}
	
	public void setMaskParameters(int upperH, int upperS, int upperV, int lowerH, int lowerS, int lowerV){
		upperHBound = upperH;
		upperSBound = upperS;
		upperVBound = upperV;
		
		lowerHBound = lowerH;
		lowerSBound = lowerS;
		lowerVBound = lowerV;
		
		if(visionThread != null){
			visionThread.setMaskParameters(upperH, upperS, upperV, lowerH, lowerS, lowerV);
		}
	}
	
	public void setCameraParameters(int exposure, int whiteBalance){
		camera.setExposureManual(exposure);
		camera.setWhiteBalanceManual(whiteBalance);
	}
}

class VisionThread extends Thread
{
	CvSink videoIn;
	CvSource videoOut;
	
	Mat inputImage;
	Mat outputImage;
	
	Mat hsvImage;
	
	Scalar lowerBounds;
	Scalar upperBounds;
	
	int threadWait;
	
	public VisionThread (VideoSource videoSource, int width, int height, double updateFrequency)
	{
		videoIn = CameraServer.getInstance().getVideo(videoSource);
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