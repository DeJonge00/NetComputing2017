package task_manager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import rmi.TaskServer;

public class TaskManager extends UnicastRemoteObject implements TaskServer, Serializable {
	static final long serialVersionUID = 1234;
	
	private ArrayList<Task> tasks;
	private int pid;
	private ObjectOutputStream out;
	
	public TaskManager(ObjectOutputStream out) throws RemoteException {
		super(4000);
		this.tasks = new ArrayList<Task>();
		this.pid = 0;
		this.out = out;
	}
	
	public synchronized int execute(String processString, String input) {
		Task task = null;
		try {
			System.out.println(input);
			Process process = Runtime.getRuntime().exec(processString);
			task = new Task(process, pid, out, input);
			this.tasks.add(task);
			this.pid++;
			task.start();
		} catch (IOException e) {
			System.out.println("IO exception when starting executable in Taskmanager");
		}
		if(task == null) return -1;
		return task.getPid();
	}
	
	public synchronized void interrupt(int pid) {
		Task task;
		if((task = getTask(pid)) != null) {
			task.interrupt();
		}
	}
	
	public String[] getOutput(int pid) {
		Task task;
		String[] out = new String[2];
		if((task = getTask(pid)) != null) {
			 out[0] = task.getOutput();
			 out[1] = task.getError();
			 return out;
		}
		return null;
	}
	
	private Task getTask(int pid) {
		for(Task task:this.tasks) {
			if(task.getPid() == pid) {
				return task;
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
