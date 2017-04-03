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
	
	private void checkConnections() {
		for(Connection c : this.connections) {
			if(!c.isActive()) {
				for(Task t : this.taskList.getActiveTasks()) {
					if(t.getConn().equals(c)) {
						this.taskList.remove(t.getTaskId());
						tasks.enqueue(t);
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		while(true) {
			//dequeue task
			checkConnections();
			Task task = tasks.dequeue();
			//System.out.println("in taskdistributor loop");
			if(task != null) {
				// there is a task to distribute

				Connection conn = connections.getFirst();
				if(conn==null) tasks.enqueue(task);
				else {
					String command = task.getCommand();
					if(!command.startsWith("gcc ")) {
						StringBuilder builder = new StringBuilder();
						builder.append(command);
						if(conn.getData().platform == 0) {
							builder.insert(command.indexOf(" "), ".exe");
						} else if(conn.getData().platform == 1) {
							builder.insert(0, "./");
						} else {
							continue;
						}
						task.setCommand(builder.toString());
					}
					try {
						System.out.println(task.getCommand());
						task.setConn(conn);
						TaskServer stub=(TaskServer)Naming.lookup("rmi://" + conn.getInetAddress().getHostAddress() + ":1099/taskManager");  
	
						// upgrade the current task to an active task
						TaskActive activeTask = new TaskActive(task);
						activeTask.setStartTime(System.currentTimeMillis());
						
						int pid = stub.execute(task.getCommand(), task.getInput());
						activeTask.setPid(pid);
						taskList.insertTask(activeTask);
						System.out.println("executing " + task.getCommand());
					} catch (Exception e) {
						System.out.println("Task execution failed, re-enqueueing task");
						tasks.enqueue(task);
					}
				}
			} else {
				// no command to execute so we sleep for 5 miliseconds
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					System.err.println("The task distributer was interrupted.");
				}
			}
		}
	}
}
