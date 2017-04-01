package task_distributor;

import message_inbox.Connection;

public class Task { 
	private Connection conn;
	private String command;
	private int userId;
	
	public Task(String c) {
		this.command = c;
	}
	
	protected Task(Task other) {
		this.conn = other.getConn();
		this.command = other.getCommand();
		this.userId = other.getUserId();
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
