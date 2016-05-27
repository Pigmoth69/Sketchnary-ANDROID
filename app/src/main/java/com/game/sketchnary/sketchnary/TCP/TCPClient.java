package tcpConnection;

import java.io.*;
import java.net.*;

public class TCPClient {
	
	Socket clientSocket;
	String host;
	int port;
	
	public TCPClient(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		
		clientSocket = new Socket(host ,port);
	}
	
	public String receive() throws UnknownHostException, IOException{
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String sentence = inFromServer.readLine();
		
		return sentence;
	}
	
	public void send(String sentence) throws UnknownHostException, IOException{
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeBytes(sentence + '\n');

	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void closeSocket(){
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
