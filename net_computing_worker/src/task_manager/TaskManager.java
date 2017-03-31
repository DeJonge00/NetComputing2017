package task_manager;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import rmi.TaskServer;


public class TaskManager extends UnicastRemoteObject implements TaskServer, Serializable {
	static final long serialVersionUID = 1234;
	
	private ArrayList<Task> tasks;
	private int pid;
	private InetAddress serveraddress;
	private int serverport;
	
	public TaskManager(InetAddress a, int p) throws RemoteException {
		super(4000);
		this.tasks = new ArrayList<Task>();
		this.pid = 0;
		this.serveraddress = a;
		this.serverport = p;
	}
	
	public synchronized int execute(String process) {
		Task t = null;
		try {
			Process p = Runtime.getRuntime().exec(process);
			TaskOutput out = new TaskOutput();
			TaskThread tt = new TaskThread(p, out, pid, serveraddress, serverport);
			Thread thread = new Thread(tt);
			t = new Task(thread, out, pid);
			this.tasks.add(t);
			this.pid++;
			thread.start();
		} catch (IOException e) {
			System.out.println("IO exception when starting executable in Taskmanager");
			e.printStackTrace();
		}
		if(t != null) {
			return t.getPid();
		} else {
			return -1;
		}
	}
	
	public synchronized void interrupt(int pid) {
		Task t;
		if((t = getTask(pid)) != null) {
			t.interrupt();
		}
	}
	
	public String getOutput(int pid) {
		Task t;
		System.out.println("XD");
		if((t = getTask(pid)) != null) {
			System.out.println("XD");
			return t.getOutput();
		}
		return null;
	}
	
	private Task getTask(int pid) {
		for(Task t:this.tasks) {
			if(t.getPid() == pid) {
				return t;
			}
		}
		return null;
	}
	
	public void initSecurityManager() {
		if (System.getSecurityManager() == null) {
		    System.setSecurityManager(new SecurityManager());
		}
	}
}
