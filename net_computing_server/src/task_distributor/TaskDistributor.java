package task_distributor;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import message_inbox.Connection;
import message_inbox.ConnectionList;
import rmi.TaskServer;

public class TaskDistributor extends Thread{
	private TaskQueue tasks;
	private ConnectionList connections;
	private TaskList taskList;
	
	public TaskDistributor(TaskQueue taskQueue, ConnectionList connections, TaskList taskList) {
		this.tasks = taskQueue;
		this.connections = connections;
		this.taskList = taskList;
		
		try {
			System.out.println("Starting task distributor");
			System.setSecurityManager(new RMISecurityManager());			
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}
	
	
	@Override
	public void run() {
		while(true) {
			//dequeue task
			Task task = tasks.dequeue();
			//System.out.println("in taskdistributor loop");
			if(task != null) {
				// there is a task to distribute
				try {
					Connection conn = connections.getFirst();

					task.setConn(conn);
					TaskServer stub=(TaskServer)Naming.lookup("rmi://" + conn.getInetAddress().getHostAddress() + ":1099/taskManager");  

					
					// upgrade the current task to an active task
					TaskActive at = new TaskActive(task);
					at.setStartTime(System.currentTimeMillis());
					
					int pid = stub.execute(task.getCommand(), task.getInput());
					at.setPid(pid);
					// to do: store ActiveTask
					taskList.insertTask(at);
					System.out.println("executing " + task.getCommand());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// no command to execute so we sleep for 5 miliseconds
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.err.println("The task distributer was interrupted.");
					return;
				}
			}
		}
	}
}
