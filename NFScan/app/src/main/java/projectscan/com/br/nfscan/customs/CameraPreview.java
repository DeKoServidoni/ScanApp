package projectscan.com.br.nfscan.customs;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Class responsible to show a preview to the user 
 * of the device application
 * 
 * @author DeKoServidoni
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	/** Log tag */
	private static final String TAG = CameraPreview.class.getSimpleName();
	
	/** Holder responsible to show the camera preview */
	private SurfaceHolder mHolder = null;
	
	/** Device camera of the device */
	private Camera mCamera = null;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * 			Application context
	 * @param camera
	 * 			Camera instance
	 */
	public CameraPreview(Context context, Camera camera) {
		super(context);
		
		setCamera(camera);
		mCamera.setDisplayOrientation(90);
		
		mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	/**
	 * Set camera object
	 * 
	 * @param camera instance
	 */
	public void setCamera(Camera camera) {
		mCamera = camera;
	}
	
	/**
	 * Method responsible to tell the camera where to draw the preview picture
	 * 
	 * @param holder
	 * 			Holder object to draw
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
	}
	
	/**
	 * Release the camera when the preview is destroyed
	 * 
	 * @param holder
	 * 			Holder object of the preview
	 */
    public void surfaceDestroyed(SurfaceHolder holder) {

    	mHolder.removeCallback(this);
    	mCamera.stopPreview();
    }
    
    /**
     * Method responsible to handle changes of the preview holder
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

}
