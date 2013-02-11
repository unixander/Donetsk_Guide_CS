package ua.org.unixander.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Class for logging 
 * @author unixander
 *
 */
public class ConsoleLog {
	public static int ERRORMSG = 0, WARNINGMSG = 1, SUCCESSMSG = 2,OTHERMSG=3;
	private static String logFilePath = "\\log\\server.log";
	private static String logDirPath = "\\log\\";
	private static File file;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Date date = new Date();

	public ConsoleLog() {
		super();
		String path = "";
		try {
			path = new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new File(path+logDirPath).mkdir();
		file = new File(path+logFilePath);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("[" + dateFormat.format(date) + "] Can't create log file");
			}
	};

	public ConsoleLog(String path) {
		super();
		if (path != null && !path.isEmpty()) {
			file = new File(path);
			if(!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.out.println("[" + dateFormat.format(date) + "] Can't create log file");
				}
			if (!file.canWrite()) {
				System.out.println("[" + dateFormat.format(date) + "] No access for writing log file.");
			}
		}
	}

	public void Log(String message, int type) {
		String msg = "";
		BufferedWriter out = null;

		if (file != null) {
			try {
				out = new BufferedWriter(new FileWriter(file, true));
			} catch (IOException e) {
				System.out.println("[" + dateFormat.format(date) + "] "
						+ e.getMessage());
			}
		}
		switch (type) {
		case 0:
			msg = "(Error)[" + dateFormat.format(date) + "] " + message;
			break;
		case 1:
			msg = "(Warning)[" + dateFormat.format(date) + "] " + message;
			break;
		case 2:
			msg = "(Success)[" + dateFormat.format(date) + "] " + message;
			break;
		default:
			msg = "[" + dateFormat.format(date) + "] " + message;
		}
		System.out.println(msg);
		if (out != null && !msg.isEmpty()) {
			try {
				out.write(msg);
				out.newLine();
				out.close();
			} catch (IOException e) {
				System.out.println("[" + dateFormat.format(date) + "] "
						+ e.getMessage());
			}
		}
	}
}
