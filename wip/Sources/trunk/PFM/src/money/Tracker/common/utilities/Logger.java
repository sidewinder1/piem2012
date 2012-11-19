package money.Tracker.common.utilities;

public class Logger {
	private static final String mFileLog = "/log.pfm";
	
	public static void Log(String logMessage, String module) {
		String content = new StringBuilder("\r\n- ")
				.append(Converter.toString(DateTimeHelper.now(),
						"dd/MM/yyyy hh:mm:ss \r\n   - [")).append(module)
				.append("]: ").append(logMessage).toString();

		IOHelper.getInstance().writeFile(mFileLog, content);
	}
}
