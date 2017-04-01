package task_distributor;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import message_inbox.Connection;
import message_inbox.ConnectionList;
import rmi.TaskServer;

public class TaskDistributor extends Thread{
	private TaskServer stub; 
	private TaskQueue tasks;
	private ConnectionList connections;
	public TaskDistributor(TaskQueue tq, ConnectionList cl) {
		this.tasks = tq;
		this.connections = cl;
		try {
			System.out.println("Starting task distributor");
			System.setSecurityManager(new RMISecurityManager());
			//stub=(TaskServer)Naming.lookup("rmi://192.168.178.30:1099/taskManager");  
			
		} catch (Exception e){
			e.printStackTrace();
		}   
	} 
	
	
	@Override
	public void run() {
		while(true) {
			//dequeue task
			Task t = tasks.dequeue();
			//System.out.println("in taskdistributor loop");
			if(t != null) {
				// there is a task to distribute
				try {
					Connection conn = connections.getFirst();
					t.setConn(conn);
					stub=(TaskServer)Naming.lookup("rmi://" + conn.getInetAddress().getHostAddress() + ":1099/taskManager");  
					stub.execute(t.getCommand());
					// upgrade the current task to an active task
					TaskActive at = new TaskActive(t);
					at.setStartTime(System.currentTimeMillis());
					// to do: store ActiveTask
					System.out.println("executing " + t.getCommand());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// no command to execute so we sleep for 5 miliseconds
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// the task distributor was interrupted so we return and shut down
					return;
				}
			}
		}
	}
}
