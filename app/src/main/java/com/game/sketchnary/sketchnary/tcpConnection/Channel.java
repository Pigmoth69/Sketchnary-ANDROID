package com.game.sketchnary.sketchnary.tcpConnection;

import com.game.sketchnary.sketchnary.utilities.*;

import java.io.IOException;


public class Channel {

	private TCPClient client_c1;
	private TCPClient client_c2;
	private TCPServer server_c1;
	private TCPServer server_c2;

	private String hostname;
	private int port_c1;
	private int port_c2;
	private Boolean server;

	public Channel(String hostname, int port_c1, int port_c2, Boolean server) {
		this.hostname = hostname;
		this.port_c1 = port_c1;
		this.port_c2 = port_c2;
		this.server = server;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPortC1() {
		return port_c1;
	}

	public int getPortC2() {
		return port_c2;
	}

	public void createChannels() {

		if (server) {
			try {
				server_c1 = new TCPServer(port_c1);
				server_c2 = new TCPServer(port_c2);
				
				server_c1.acceptSocket();
				server_c2.acceptSocket();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				client_c1 = new TCPClient(hostname, port_c1);
				client_c2 = new TCPClient(hostname, port_c2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public Boolean sendC1(String query) {

		if (server) {
			try {
				server_c1.send(query);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				client_c1.send(query);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;

	}

	public String receiveC1() {

		String received = null;

		if (server) {
			try {
				received = server_c1.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				received = client_c1.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return received;

	}

	public Boolean sendC2(String query) {

		if (server) {
			try {
				server_c2.send(query);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				client_c2.send(query);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;

	}

	public String receiveC2() {

		String received = null;

		if (server) {
			try {
				received = server_c2.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				received = client_c2.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return received;

	}

	/**
	 * Exchange on the channel 1
	 * 
	 * @param //server
	 * @param query
	 * @return received string
	 */
	public String exchangeC1(String query) {

		String received = null;
		
		if (server)
			received = receiveC1();
		else {
			if(sendC1(query))
				return Constants.OK;
			else
				return Constants.ERROR1;
		}

		return received;

	}

	/**
	 * Exchange on the channel 2
	 * Returns ERROR1 or 2 which corresponds to the error occuring in the first exchange or the second 
	 * @param //server
	 * @param query
	 * @return String
	 */
	public String exchangeC2(String query) {

		String received;

		if (server){
			if(sendC2(query)){
				received = receiveC2();
				if(received.equals(Constants.OK))
					return Constants.OK;
				else
					return Constants.ERROR2;
			}
			else
				return Constants.ERROR1;
		}
		else {
			if(receiveC2().equals(query)){
				if(sendC2(Constants.OK))
					return Constants.OK;
				else
					return Constants.ERROR2;
			}
			else
				return Constants.ERROR1;
		}

	}

}
