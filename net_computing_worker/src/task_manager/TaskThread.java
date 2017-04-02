package task_manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Observable;

import rmi.TaskInfo;

public class TaskThread extends Observable implements Runnable {
	private Process process;
	private TaskOutput out;
	private int pid;
	private Thread t;
	private ObjectOutputStream oos;
	private String input;
	
	public TaskThread(Process p, TaskOutput out, int pid, ObjectOutputStream o, String input) {
		this.process = p;
		this.out = out;
		this.pid = pid;
		this.oos = o;
		this.input = input;
	}
	
	public void run() {
		try {
			getOutputStream().write(input);
			process.waitFor();
			BufferedReader in = getInputStream();
			String output = readTaskOutput(in);
			this.out.setOutput(output);
			sendData(new TaskInfo(pid, process.exitValue(), System.currentTimeMillis()));
		} catch (InterruptedException e) {
			process.destroy();
			out.setOutput("interrupted");
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
	
	private BufferedWriter getOutputStream() {
		return new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}
	
	private String readTaskOutput(BufferedReader in) {
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public boolean sendData(TaskInfo tf) {
		try {
			oos.writeObject(tf);
			System.out.println("Sent message to inbox");
		} catch (Exception e) {
			System.out.println("Is there a server running on that port?");
			return false;
		}
		return true;
	}
}
