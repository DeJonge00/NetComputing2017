package task_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

import rmi.TaskInfo;

public class TaskThread extends Observable implements Runnable {
	private Process process;
	private TaskOutput out;
	private int pid;
	private Thread t;
	private ObjectOutputStream oos;
	
	public TaskThread(Process p, TaskOutput out, int pid, ObjectOutputStream o) {
		this.process = p;
		this.out = out;
		this.pid = pid;
		this.oos = o;
	}
	
	public void run() {
		try {
			process.waitFor();
			BufferedReader in = getInputStream();
			String output = readTaskOutput(in);
			this.out.setOutput(output);
			sendData(new TaskInfo(pid, "finished", System.currentTimeMillis()));
		} catch (InterruptedException e) {
			process.destroy();
			out.setOutput("interrupted");
			sendData(new TaskInfo(pid, "interrupted", System.currentTimeMillis()));
			System.out.println("Process was interrupted: " + process.toString());
			return;
		}
	}
	
	private BufferedReader getInputStream() {
		return new BufferedReader(new InputStreamReader(process.getInputStream()));
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
