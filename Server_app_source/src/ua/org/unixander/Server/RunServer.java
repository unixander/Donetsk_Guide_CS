package ua.org.unixander.Server;

import java.io.IOException;
import java.net.ServerSocket;

import ua.org.unixander.logger.ConsoleLog;

import com.almworks.sqlite4java.SQLiteException;

/**
 * 
 * Main server class
 * @author unixander
 *
 */
public class RunServer {
	private ServerSocket serverSocket = null;
	private ConsoleLog console = new ConsoleLog();
	DBAdapter dbHelper = new DBAdapter();

	public RunServer(String address, int port) {
		dbHelper = new DBAdapter();
		try {
			dbHelper.open();
		} catch (SQLiteException ex) {
			console.Log(ex.getMessage(), ConsoleLog.ERRORMSG);
			return;
		} finally {
			dbHelper.close();
		}
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			console.Log("Could not listen on port: " + Integer.toString(port),
					ConsoleLog.WARNINGMSG);
			System.exit(-1);
		}
		console.Log("Server is running at " + Integer.toString(port) + " port",
				ConsoleLog.OTHERMSG);
		while (listening) {
			try {
				new ServerThread(serverSocket.accept()).start();
			} catch (Exception ex) {
				console.Log(ex.getMessage(), ConsoleLog.WARNINGMSG);
			}
			console.Log("New Connection", ConsoleLog.SUCCESSMSG);
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			console.Log(e.getMessage(),ConsoleLog.ERRORMSG);
		} finally {
			dbHelper.close();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String address="localhost";
		int port=4444;
		if(args.length==2){
			address=args[0];
			port=Integer.parseInt(args[1]);
		}
		new RunServer(address, port);
	}

}
