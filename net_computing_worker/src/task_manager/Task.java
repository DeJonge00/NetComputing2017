package task_manager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import rmi.TaskInfo;

public class Task extends Thread {
	private Process process;
	private int pid;
	private ObjectOutputStream out;
	private String input;
	private String output;
	private String error;
	
	public Task(Process process, int pid, ObjectOutputStream out, String input) {
		this.process = process;
		this.pid = pid;
		this.out = out;
		this.input = input;
		this.output = "not_finished";
		this.error = "";
	}
	
	public void run() {
		try {
			String[] inLines = input.split("\\r?\\n");
			PrintWriter writer = getOutputStream();
			for(String line : inLines) {
				writer.println(line);
			}
			process.waitFor();
			BufferedReader in = getInputStream();
			String output = readTaskOutput(in);
			BufferedReader err = getErrorStream();
			String error = readTaskOutput(err);
			this.setOutput(output);
			this.setError(error);
			sendData(new TaskInfo(pid, process.exitValue(), System.currentTimeMillis()));
		} catch (InterruptedException e) {
			process.destroy();
			setOutput("interrupted");
			sendData(new TaskInfo(pid, -1, System.currentTimeMillis()));
			System.out.println("Process was interrupted: " + process.toString());
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedReader getInputStream() {
		return new BufferedReader(new InputStreamReader(process.getInputStream()));
	}
	
	private BufferedReader getErrorStream() {
		return new BufferedReader(new InputStreamReader(process.getErrorStream()));
	}
	
	private PrintWriter getOutputStream() {
		return new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(process.getOutputStream())), true);

	}
	
	private String readTaskOutput(BufferedReader in) {
		StringBuffer buffer = new StringBuffer();
		String line;
		try {
			while((line = in.readLine()) != null) {
				buffer.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	public boolean sendData(TaskInfo tf) {
		try {
			out.writeObject(tf);
			System.out.println("Sent message to inbox");
		} catch (Exception e) {
			System.out.println("Is there a server running on that port?");
			return false;
		}
		return true;
	}

	public synchronized String getOutput() {
		return output;
	}
	
	public synchronized String getError() {
		return error;
	}
	
	public synchronized void setOutput(String output) {
		this.output = output;
	}
	
	public synchronized void setError(String error) {
		this.error = error;
	}

	public int getPid() {
		return this.pid;
	}
}
