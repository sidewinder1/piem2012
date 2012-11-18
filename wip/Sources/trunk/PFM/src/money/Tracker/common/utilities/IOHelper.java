package money.Tracker.common.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class IOHelper {
	private static IOHelper _instance;
	
	public static IOHelper getInstance()
	{
		if (_instance == null){
			_instance = new IOHelper();
		}
		
		return _instance;
	}
	
	public void createFile(String filePath, String content){
		File file = new File(filePath);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writeFile(filePath, content, false);
		}
	}
	
	public String readFile(String path) {
		String strContent = "";
		// Check whether file is existing or not.
		if (new File(path).exists()) {
			InputStream fiStream;
			try {
				fiStream = new FileInputStream(path);

				try {
					fiStream.read();
					strContent = new Scanner(fiStream).useDelimiter("\\A").next();
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

	public void writeFile(String path, String content){
		writeFile(path, content, true);
	}
	
	public void writeFile(String path, String content, boolean append) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		
		if (file.exists()) {
			try {
				OutputStream fiStream = new FileOutputStream(path, append);
				try {
					fiStream.write(content.getBytes());

					fiStream.close();
				} catch (IOException e) {
					Logger.Log(e.getMessage(), "IOHelper");
				}
			} catch (FileNotFoundException e) {
				Logger.Log(e.getMessage(), "IOHelper");
			}
		}
	}
}
