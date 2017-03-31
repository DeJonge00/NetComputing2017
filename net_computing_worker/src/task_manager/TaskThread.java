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
	private InetAddress serveraddress;
	private int serverport;
	
	public TaskThread(Process p, TaskOutput out, int pid, InetAddress a, int po) {
		this.process = p;
		this.out = out;
		this.pid = pid;
		this.serveraddress = a;
		this.serverport = po;
	}
	
	public void run() {
		try {
			process.waitFor();
			BufferedReader in = getInputStream();
			String output = readTaskOutput(in);
			this.out.setOutput(output);
			sendData(serveraddress, serverport, new TaskInfo(pid, "finished"));
		} catch (InterruptedException e) {
			process.destroy();
			out.setOutput("interrupted");
			sendData(serveraddress, serverport, new TaskInfo(pid, "interrupted"));
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
	
	public boolean sendData(InetAddress a, int p, TaskInfo tf) {
		try {
			Socket s = new Socket(a.getHostAddress(), p);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			oos.writeObject(tf);
			System.out.println("Sent message to inbox");
			return true;

		} catch (Exception e) {
			System.out.println(a.getHostAddress() + " -- " + p);
			System.out.println("Is there a server running on that port?");
			return false;
		}
	}
}
