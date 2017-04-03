package data_analyzer;

import java.rmi.Naming;

import message_inbox.Connection;
import message_inbox.ConnectionList;
import message_inbox.Message;
import message_inbox.MessageQueue;
import rmi.Measurement;
import rmi.TaskInfo;
import rmi.TaskServer;
import task_distributor.TaskList;

public class DataAnalyzer implements Runnable {
	
	private MessageQueue messages;
	private ConnectionList workers;
	private TaskList tl;
	
	public DataAnalyzer(MessageQueue messages, ConnectionList c, TaskList tl) {
		this.messages = messages;
		this.workers = c;
		this.tl = tl;
	}
	
	@SuppressWarnings("unused")
	public void run() {
		while (true) {
			int messageType = messages.getFirstType();
			if (messageType == 1) {
				// it's a measurement
				Message<Measurement> m = (Message<Measurement>)messages.dequeue();
				Measurement measure = m.getMessageContent();
				// update the connection that sent the message
				Connection conn = m.getConn();
				conn.setLastMeasurement(measure);
				this.workers.update(conn);
			} else if (messageType == 2) {
				Message<TaskInfo> m = (Message<TaskInfo>)messages.dequeue();
				TaskInfo tf = m.getMessageContent();
				int pid = tf.getPid();
				System.out.println("dataanalyzer pid:" + pid);
				
				Connection conn = m.getConn();
				// retrieve active task based on pid and conn
				//Task t = ActiveTasks.getTask(pid, conn);
				
				try {
					TaskServer stub=(TaskServer)Naming.lookup("rmi://" + conn.getInetAddress().getHostAddress() + ":1099/taskManager");  
					String[] result = stub.getOutput(pid);
					tl.finishTask(pid, conn, tf.getFinishTime(), result[0], result[1], tf.getStatus());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// upgrade the current active task to finished task
				
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
