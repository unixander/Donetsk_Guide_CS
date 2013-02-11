package ua.org.unixander.donetsk_guide.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkConnection {
	Socket clientSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	private String connectionError = null;
	private int port = 4444;
	private String host = "192.168.0.100";
	int timeout = 4000;

	public NetworkConnection() {

	}
	
	public NetworkConnection(String host,int port){
		this.host=host;
		this.port=port;
	}

	public String getLastError() {
		return this.connectionError;
	}

	public int getPort() {
		return this.port;
	}

	public String getHost() {
		return this.host;
	}

	public void setPort(int p) {
		this.port = p;
	}

	public void setHost(String h) {
		this.host = h;
	}

	public boolean connect() {
		this.connectionError = "";
		try {
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(host, port), timeout);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			this.connectionError = "Don't know about host:" + host;
			return false;
		} catch (IOException e) {
			this.connectionError = "Couldn't get I/O for the connection to:"
					+ host;
			return false;
		}
		return true;
	}

	public String sendMessage(String msg) {
		String fromServer, clientMessage = msg, response = "";

		try {
			out.println(clientMessage);
			fromServer = in.readLine();
			response += fromServer;
		} catch (IOException e) {
			this.connectionError = e.getLocalizedMessage();
		}
		System.out.println(response);
		return response;
	}

	public void disconnect() {
		sendMessage("CLOSE_CONNECTION");
		out.close();
		try {
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			this.connectionError = e.getLocalizedMessage();
		}
	}

}
