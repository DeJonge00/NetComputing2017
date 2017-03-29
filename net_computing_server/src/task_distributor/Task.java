package task_distributor;

import message_inbox.Connection;

public class Task { 
	private Connection conn;
	private String command;
	
	public Task(String c) {
		this.command = c;
	}
	
	public String getAddress() {
		return conn.getInetAddress().toString();
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}