package money.Tracker.common.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Environment;

public class Logger {
	private static final String mFileName = "/PfmLogger.pfm";
	private static String mBaseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	private static File mFile = new File(mBaseDir + File.separator + mFileName);

	public static void Log(String logMessage, String module) {
		if (mFile.exists()) {
			try {
				FileOutputStream fiStream = new FileOutputStream(mFile);
				try {
					fiStream.write(new StringBuilder("- ")
							.append(Converter.toString(DateTimeHelper.now(),
									"dd/MM/yyyy HH:mm:ss \n   - ["))
							.append(module).append("]: ").append(logMessage)
							.toString().getBytes());
					
					fiStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
	}
}
