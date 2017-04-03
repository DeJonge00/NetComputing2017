package task_distributor;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import message_inbox.Connection;
import message_inbox.ConnectionList;
import rmi.TaskServer;

public class TaskDistributor extends Thread{
	private TaskQueue tasks;
	private ConnectionList connections;
	private TaskList tl;
	
	public TaskDistributor(TaskQueue tq, ConnectionList cl, TaskList tl) {
		this.tasks = tq;
		this.connections = cl;
		this.tl = tl;
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
					TaskServer stub=(TaskServer)Naming.lookup("rmi://" + conn.getInetAddress().getHostAddress() + ":1099/taskManager");  
					
					// upgrade the current task to an active task
					TaskActive at = new TaskActive(t);
					at.setStartTime(System.currentTimeMillis());
					
					int pid = stub.execute(t.getCommand(), t.getInput());
					at.setPid(pid);
					// to do: store ActiveTask
					tl.insertTask(at);
					System.out.println("executing " + t.getCommand());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// no command to execute so we sleep for 5 miliseconds
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// the task distributor was interrupted so we return and shut down
					return;
				}
			}
		}
	}
}
