package projectscan.com.br.nfscan;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import projectscan.com.br.nfscan.customs.CameraPreview;
import projectscan.com.br.nfscan.entities.Picture;
import projectscan.com.br.nfscan.managers.CameraManager;
import projectscan.com.br.nfscan.tasks.ProcessPhotoTask;

/**
 * Main class of the application
 * 
 * @author DeKoServidoni
 *
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements ProcessPhotoTask.ProcessPhotoCallback, PictureCallback {

	/** Log tag */
	private static final String TAG = MainActivity.class.getSimpleName();
	
	/** UI Components */
	private Button mButton = null;
	private CameraPreview mCameraPreview = null;
	private FrameLayout mPreviewHolder = null;
	
	/** Device camera instance */
	private Camera mCamera = null;
	
	/** Camera manager instance */
	private CameraManager mCameraManager = null;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
        
		mCameraManager = new CameraManager();
		
		mapUI();	
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		mCamera = mCameraManager.connectToCamera();
        
		if(mCamera != null) {
			
			mCameraPreview = new CameraPreview(this, mCamera);
			mCameraPreview.setCamera(mCamera);
			
			mPreviewHolder.addView(mCameraPreview);
			
		} else {
			Log.e(TAG, "Failed to connect to device's camera!");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		// cleaning the preview holder
		mPreviewHolder.removeAllViews();
		
		// stop and release the resources of the camera
		mCamera.stopPreview();
		mCamera.release();
		
		// forces garbage collector
		mCamera = null;
	}
	
	/**********************
	 *  CALLBACK METHODS  *
	/**********************/
	
	/*
	 * (non-Javadoc)
	 * @see com.deko.projectscan.tasks.ProcessPhotoTask.ProcessPhotoCallback#onFinish(java.lang.String)
	 */
	@Override
	public void onFinish(String text) {
		Log.e(TAG,"RESULT: "+text);

		Picture picture = new Picture();
		picture.setCode(text);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.hardware.Camera.PictureCallback#onPictureTaken(byte[], android.hardware.Camera)
	 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		
		boolean isOk = false;
		File pictureFile = mCameraManager.getOutputMediaFile();
		
        if (pictureFile == null){
            Log.e(TAG, "Error creating media file, check storage permissions.");
        } else {

	        FileOutputStream output = null;
	        
	        try {
	        	output = new FileOutputStream(pictureFile);
	        	output.write(data);
	        	isOk = true;
	            
	        } catch (FileNotFoundException e) {
	            Log.e(TAG, "File not found: " + e.getMessage());
	            
	        } catch (IOException e) {
	            Log.e(TAG, "Error accessing file: " + e.getMessage());
	            
	        } finally {
	        	
	        	try {
	        		
	        		if(output != null) {
	        			output.close();
	        		}
	        		
				} catch (IOException e) {
					Log.e(TAG,"Error to close the output: "+e.getLocalizedMessage());
				}
	        }
	        
	        if(isOk) {
	        	ProcessPhotoTask task = new ProcessPhotoTask(MainActivity.this, MainActivity.this);
	    		task.execute(pictureFile.getAbsolutePath());
	        }
        }
        
        camera.startPreview();
	}
	
	/*********************
	 *  PRIVATE METHODS  *
	/*********************/
	
	/**
	 * Map UI components from XML
	 */
	private void mapUI() {
		mPreviewHolder = (FrameLayout) findViewById(R.id.main_camerapreview);
		
		mButton = (Button) findViewById(R.id.main_button_takeshot);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(mCamera != null) {
					mCamera.takePicture(null, null, MainActivity.this);
				}
			}
		});
	}
}
