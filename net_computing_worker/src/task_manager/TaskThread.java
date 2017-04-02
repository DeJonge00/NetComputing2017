package task_manager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
			this.out.setOutput(output);
			this.out.setError(error);
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
	
	private BufferedReader getErrorStream() {
		return new BufferedReader(new InputStreamReader(process.getErrorStream()));
	}
	
	private PrintWriter getOutputStream() {
		return new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(process.getOutputStream())), true);

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
