package projectscan.com.br.nfscan.utils;

import android.os.Environment;

/**
 * Class responsible to hold all the application constants
 * 
 * @author DeKoServidoni
 *
 */
public class Constants {

	public static final int SPLASH_SCREEN_TIMEOUT = 1500;
	public static final String TESSERACT_LANGUAGE = "por";
	public static final String TESSERACT_SUBFOLDER = "tessdata";
	public static final String TESSERACT_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/tesseract/";
	public static final String TESSERACT_FILE = "/"+TESSERACT_LANGUAGE+".traineddata";
}
