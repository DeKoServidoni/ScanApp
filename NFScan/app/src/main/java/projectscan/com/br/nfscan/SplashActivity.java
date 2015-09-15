package projectscan.com.br.nfscan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import projectscan.com.br.nfscan.utils.Constants;

/**
 * Splash screen
 * 
 * @author DeKoServidoni
 *
 */
public class SplashActivity extends Activity {

	/** Log tag */
	private static final String TAG = SplashActivity.class.getSimpleName();
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_splash);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		new Handler().postDelayed(new Runnable() {
			
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				// copy the file from assets to the user sdcard
				manageFile();
				
				// launch main activity
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
			}
			
		}, Constants.SPLASH_SCREEN_TIMEOUT);
	}
	
	/**
	 * Verify if the language file exists or not on the SDCard.
	 * If don't we copy to it.
	 */
	private void manageFile() {
		
		File dir = new File(Constants.TESSERACT_FOLDER + Constants.TESSERACT_SUBFOLDER);
		if(!dir.exists()) {
			Log.i(TAG, "Folder not found, let's create it!");
			boolean result = dir.mkdirs();
			Log.i(TAG, "Folder created? "+result);
		}
		
		File file = new File(dir.getAbsolutePath() + Constants.TESSERACT_FILE);
		if(!file.exists()) {
			Log.i(TAG, "File not found, let's copy it!");
			InputStream input = null;
			OutputStream output = null;
			
			try {
				AssetManager assetManager = getAssets();
			    
		        String[] files = assetManager.list(Constants.TESSERACT_SUBFOLDER);
		        
		        if(files.length >= 1) {
		        	Log.i(TAG, "Copying file " + files[0] + "to the SDCard folder...");
		        	
		        	input = assetManager.open(Constants.TESSERACT_SUBFOLDER + "/" + files[0]);
		        	
		        	File language = new File(dir.getAbsolutePath(), files[0]);
		        	output = new FileOutputStream(language);
		        	
		        	byte[] buffer = new byte[1024];
		            int read;
		            
		            while((read = input.read(buffer)) != -1){
		              output.write(buffer, 0, read);
		            }
		        }
		        
		    } catch (IOException e) {
		        Log.e(TAG, "Failed to get asset file list.\n" + e.getLocalizedMessage());
		        
		    } finally {
		    	
		    	if(input != null) {
		    		try {
		    			input.close();
		    		} catch (IOException e) {
		    			Log.e(TAG,"Failed to close input stream. \n" + e.getLocalizedMessage());
		    		}
		    	}
		    	
		    	if(output != null) {
		    		try {
		    			output.close();
		    		} catch (IOException e) {
		    			Log.e(TAG,"Failed to close output stream. \n" + e.getLocalizedMessage());
		    		}
		    	}
		    }
		}
		
		Log.i(TAG,"File verification complete!");
	}
}
