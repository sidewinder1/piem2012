package money.Tracker.common.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import android.os.Environment;

public class IOHelper {
	private static IOHelper _instance;
	private static String sBaseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "PFMData" + File.separator;

	public static IOHelper getInstance() {
		if (_instance == null) {
			_instance = new IOHelper();
		}

		return _instance;
	}

	public void createFile(String fileName, String content) {
		File file = new File(sBaseDir + fileName);
		if (!file.exists()) {
			writeFile(fileName, content, false);
		}
	}

	public String readFile(String path) {
		String strContent = "";
		// Check whether file is existing or not.
		if (new File(sBaseDir + path).exists()) {
			InputStream fiStream;
			try {
				fiStream = new FileInputStream(sBaseDir + path);

				try {
					fiStream.read();
					strContent = new Scanner(fiStream).useDelimiter("\\A")
							.next();
					fiStream.close();
				} catch (IOException e) {
					Logger.Log(e.getMessage(), "IOHelper");
				}
			} catch (FileNotFoundException e) {
				Logger.Log(e.getMessage(), "IOHelper");
			}
		} else {
			Logger.Log(new StringBuilder(path).append(" isn't existing.")
					.toString(), "IOHelper");
		}
		return strContent;
	}

	public void writeFile(String path, String content) {
		writeFile(path, content, true);
	}

	public void writeFile(String fileName, String content, boolean append) {
		File file = new File(sBaseDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		if (file.exists()) {
			try {
				OutputStream fiStream = new FileOutputStream(sBaseDir + fileName,
						append);
				try {
					fiStream.write(content.getBytes());
					fiStream.close();
				} catch (IOException e) {
					// Logger.Log(e.getMessage(), "IOHelper");
				}
			} catch (FileNotFoundException e) {
				// Logger.Log(e.getMessage(), "IOHelper");
			}
		}
	}
}
