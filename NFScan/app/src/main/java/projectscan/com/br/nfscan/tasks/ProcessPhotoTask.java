package projectscan.com.br.nfscan.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;

import projectscan.com.br.nfscan.R;
import projectscan.com.br.nfscan.utils.Constants;


/**
 * Class responsible to process the picture in the background
 * 
 * @author DeKoServidoni
 *
 */
public class ProcessPhotoTask extends AsyncTask<String, Void, String> {
	
	/** Log tag */
	private static final String TAG = ProcessPhotoTask.class.getSimpleName();
	
	/**
	 * Interface responsible to indicates when the task finish
	 * 
	 * @author DeKoServidoni
	 */
	public interface ProcessPhotoCallback {
		void onFinish(String text);
	}
	
	/** Callback reference */
	private ProcessPhotoCallback mCallback = null;
	
	/** Application context */
	private Context mContext = null;
	
	/** Progress dialog object */
	private ProgressDialog mDialog = null;
	
	/**
	 * Constructor
	 * 
	 * @param context of the application
	 * @param callback to return the result
	 */
	public ProcessPhotoTask(Context context, ProcessPhotoCallback callback) {
		mCallback = callback;
		mContext = context;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.app_name),
				mContext.getString(R.string.img_process_text), true, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected String doInBackground(String... arg) {
		String result;
		
		try {
			result = processPhoto(arg[0]);
			
		} catch(IOException ex) {
			Log.e(TAG, ex.getLocalizedMessage());
			result = null;
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(mDialog != null) {
			mDialog.dismiss();
		}
		
		if(mCallback != null) {
			mCallback.onFinish(result);
		}
	}
	
	/**
	 * Process the photo to extract the text
	 * 
	 * @param path
	 * 			Picture path
	 * 
	 * @return the extracted text
	 * 
	 * @throws IOException
	 */
	private String processPhoto(String path) throws IOException {
		Bitmap bitmap = correctRotation(path);
		
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.init(Constants.TESSERACT_FOLDER, Constants.TESSERACT_LANGUAGE);
		baseApi.setImage(bitmap);
		
		//TODO: convert image to GRAY SCALE USING OCV Library
		
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		
		return recognizedText;
	}
	
	/**
	 * Correct the picture orientation after the user take it
	 * 
	 * @param path
	 * 			Picture path
	 * 
	 * @return corrected bitmap object
	 * 	
	 * @throws IOException
	 */
	private Bitmap correctRotation(String path) throws IOException {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 4;
	    	
	    Bitmap bitmap = BitmapFactory.decodeFile( path, options );

		ExifInterface exif = new ExifInterface(path);
		int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

		int rotate = 0;

		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
		    rotate = 90;
		    break;
		case ExifInterface.ORIENTATION_ROTATE_180:
		    rotate = 180;
		    break;
		case ExifInterface.ORIENTATION_ROTATE_270:
		    rotate = 270;
		    break;
		}

		if (rotate != 0) {
		    int w = bitmap.getWidth();
		    int h = bitmap.getHeight();

		    // Setting pre rotate
		    Matrix mtx = new Matrix();
		    mtx.preRotate(rotate);

		    // Rotating Bitmap & convert to ARGB_8888, required by tess
		    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
		}
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		return bitmap;
	}
}
