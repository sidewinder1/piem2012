package money.Tracker.common.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

public class Logger {
	private static final String mAppFolderName = "/Pfm";
	private static final String mFileLog = "/log.pfm";
	private static String mBaseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	public static void Log(String logMessage, String module) {
		String content = new StringBuilder("- ")
				.append(Converter.toString(DateTimeHelper.now(),
						"dd/MM/yyyy HH:mm:ss \n   - [")).append(module)
				.append("]: ").append(logMessage).toString();

		IOHelper.getInstance().writeFile(
				mBaseDir + File.separator + mAppFolderName + File.separator
						+ mFileLog, content);
	}
}
