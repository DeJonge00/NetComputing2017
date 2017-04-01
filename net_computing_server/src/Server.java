import message_inbox.ConnectionList;
import message_inbox.Message;
import message_inbox.MessageInbox;

import java.io.IOException;

import org.hyperic.sigar.Sigar;

import rmi.TaskInfo;
import task_distributor.Task;
import task_distributor.TaskDistributor;
import task_distributor.TaskQueue;

public class Server {
	private ConnectionList workers;
	private MessageInbox message_inbox;
	private static Sigar sigar = new Sigar();
	
	public Server(int port) throws IOException {
		this.workers = new ConnectionList();
		this.message_inbox = new MessageInbox(this.workers, port);
	}
	
	public void start() {
		Thread t = new Thread(this.message_inbox);
		t.start();
		
		try {
			Thread.sleep(5000);
			TaskQueue tq = new TaskQueue();
			Task task = new Task("inf.exe hello");
			task.setConn(this.workers.getFirst());
			tq.enqueue(task);
			System.out.println("tq size: " + tq.size());
			TaskDistributor td = new TaskDistributor(tq, this.workers);
			td.start();
			//System.out.println("Started ./infinte as process #" + pid);
			//System.out.println("going to sleep for 10 seconds");
			//Thread.sleep(10000);
			//td.interrupt(pid);
			//System.out.println("interrupted process #" + pid);
			while(true) {
				Message<?> msg = this.message_inbox.getNextMessage();
				if(msg != null && msg.getMessageContent() instanceof TaskInfo) {
					TaskInfo tf = (TaskInfo)msg.getMessageContent();
					//System.out.println("Process exit status: " + td.getTaskData(tf.getPid()));
					System.out.println("Process output: " + td.getTaskData(tf.getPid()));
				}
				Thread.sleep(50);
			}
		} catch (Exception e) {
			System.out.println("Exception in Server.start");
			return;
		}
	}
	
	/*public void test() {
		System.out.println("Started messageInbox, sleeping for 500ms");
		try {
			Thread.sleep(500);
			this.takeMeasurement();
			Socket s = new Socket("localhost", 5000);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			
			for(int i=0; i<1000; i++) {
				Measurement msg = new Measurement();
				oos.writeObject(msg);
				System.out.println("Sent message to inbox");
				this.takeMeasurement();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.takeMeasurement();
	}*/
	
	public static void main(String [] args) {
		if(args.length <= 0) {
			System.out.println("Give a port as argument 1");
			return;
		}
		int port = Integer.parseInt(args[0]);
		Server s;
		try {
			s = new Server(port);
			s.start();
		} catch (IOException e) {
			System.out.println("Server could not make a socket, aborting");
			return;
		}
	}
}
