package money.Tracker.common.utilities;

import java.io.File;

import android.os.Environment;

public class Logger {
	private static final String mAppFolderName = "/Pfm";
	private static final String mFileLog = "/log.pfm";
	private static String mBaseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	public static void Log(String logMessage, String module) {
		String content = new StringBuilder("- ")
				.append(Converter.toString(DateTimeHelper.now(),
						"dd/MM/yyyy hh:mm:ss \n   - [")).append(module)
				.append("]: ").append(logMessage).toString();

		IOHelper.getInstance().writeFile(
				mBaseDir + File.separator + mAppFolderName + File.separator
						+ mFileLog, content);
	}
}
