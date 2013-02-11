package ua.org.unixander.Server;

import java.net.*;
import java.io.*;

import ua.org.unixander.logger.ConsoleLog;

/**
 * 
 * Thread, instance of which creates for every connected client
 * 
 * @author unixander
 * 
 */
public class ServerThread extends Thread {
	private Socket socket = null;
	private ConsoleLog console = new ConsoleLog();

	public ServerThread(Socket socket) {
		super("ServerThread");
		this.socket = socket;
	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			console.Log("Client " + socket.getRemoteSocketAddress().toString()
					+ " is connected", ConsoleLog.SUCCESSMSG);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String inputLine, outputLine;
			ServerProtocol protocol = new ServerProtocol();

			while ((inputLine = in.readLine()) != null) {
				outputLine = protocol.processInput(inputLine);
				if (outputLine == null)
					outputLine = "null";
				out.println(outputLine);
				if (outputLine.equals("CLOSE_CONNECTION"))
					break;
			}

		} catch (IOException e) {
			console.Log(e.getMessage(), ConsoleLog.WARNINGMSG);
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {

			}
			console.Log("Client " + socket.getRemoteSocketAddress().toString()
					+ " disconnected", ConsoleLog.OTHERMSG);
		}
	}
}
