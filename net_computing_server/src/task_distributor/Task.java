package task_distributor;

import message_inbox.Connection;

public class Task { 
	private Connection conn;
	private String command;
	private String input;
	private int userId;
	private int taskId;
	
	public Task(String c, int taskId, String taskInput) {
		this.command = c;
		this.taskId = taskId;
		this.input = taskInput;
	}
	
	protected Task(Task other) {
		this.conn = other.getConn();
		this.command = other.getCommand();
		this.userId = other.getUserId();
		this.taskId = other.getTaskId();
		this.input = other.getInput();
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
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

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public String toString() {
		return "<td>"+command+"</td>"+taskId+"</td><td>"+userId+"</td>";
	}
}
