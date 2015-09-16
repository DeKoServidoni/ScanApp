package projectscan.com.br.nfscan.managers;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Class responsible to manage the camera and it's properties
 * 
 * @author DeKoServidoni
 *
 */
@SuppressWarnings("deprecation")
public class CameraManager {

	/** Log tag */
	private static final String TAG = CameraManager.class.getSimpleName();
	
	/** Device camera instance */
	private Camera mCamera = null;
	
	/**
	 * Connect to the device's camera
	 * 
	 * @return camera instance
	 */
	public Camera connectToCamera() {
		mCamera = getCameraInstance();
		
		if(mCamera != null) {
			Camera.Parameters params = mCamera.getParameters();
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			mCamera.setParameters(params);
		} else {
			Log.e(TAG,"Failed to obtain a camera!");
		}
		
		return mCamera;
	}
	
	/** 
	 * Create a File for saving an image or video 
	 * 
	 * @return file to save
	 */
	public File getOutputMediaFile(){

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "nfscan_images");
	    
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(TAG, "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = "img";//new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
	    File mediaFile;
	    mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");

	    return mediaFile;
	}
	
	/*********************
	 *  PRIVATE METHODS  *
	/*********************/
	
	/** 
	 * A safe way to get an instance of the Camera object. 
	 */
	private Camera getCameraInstance(){
	    Camera camera = null;
	    
	    try {
	    	// attempt to get a Camera instance
	    	camera = Camera.open();
	    }
	    catch (Exception e){
	        Log.e(TAG,""+e.toString());
	    }
	    
	    return camera;
	}
}
