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
	private ObjectOutputStream oos;
	
	public TaskManager(ObjectOutputStream o) throws RemoteException {
		super(4000);
		this.tasks = new ArrayList<Task>();
		this.pid = 0;
		this.oos = o;
	}
	
	public synchronized int execute(String process, String input) {
		Task t = null;
		try {
			System.out.println(input);
			Process p = Runtime.getRuntime().exec(process);
			TaskOutput out = new TaskOutput();
			TaskThread tt = new TaskThread(p, out, pid, oos, input);
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
	
	public String[] getOutput(int pid) {
		Task t;
		String[] out = new String[2];
		if((t = getTask(pid)) != null) {
			 out[0] = t.getOutput();
			 out[1] = t.getError();
			 return out;
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
