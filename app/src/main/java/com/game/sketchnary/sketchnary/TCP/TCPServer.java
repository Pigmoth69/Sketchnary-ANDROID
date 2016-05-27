package tcpConnection;

import java.io.*;
import java.net.*;

public class TCPServer{
	
	ServerSocket svSocket;
	Socket connectionSocket;
	int port;
	
	public TCPServer(int port) throws IOException{
		svSocket = new ServerSocket(port);
		this.port = port;
	}
	
	public void acceptSocket(){
		try {
			connectionSocket = svSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ServerSocket getSvSocket() {
		return svSocket;
	}

	public void setSvSocket(ServerSocket svSocket) {
		this.svSocket = svSocket;
	}

	public Socket getConnectionSocket() {
		return connectionSocket;
	}

	public void setConnectionSocket(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void keepReceiving() throws Exception {
		String clientSentence;
		String capitalizedSentence;
		svSocket = new ServerSocket(6789);

		while (true) {
			connectionSocket = svSocket.accept();
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));

			DataOutputStream outToClient = new DataOutputStream(
					connectionSocket.getOutputStream());

			clientSentence = inFromClient.readLine();
			System.out.println("Received: " + clientSentence);
			capitalizedSentence = clientSentence.toUpperCase() + '\n';

			outToClient.writeBytes(capitalizedSentence);
		}
	}
   
	public String receive() throws IOException {

		String clientSentence;
		
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

		clientSentence = inFromClient.readLine();

		return clientSentence;
	}
	
	public void send(String sentence) throws IOException {
		
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		outToClient.writeBytes(sentence + '\n');
		
	}
}