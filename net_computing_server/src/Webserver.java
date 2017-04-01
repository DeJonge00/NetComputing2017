import java.io.IOException;

import message_inbox.ConnectionList;
import message_inbox.MessageInbox;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.hyperic.sigar.Sigar;

import task_distributor.TaskApi;
import task_distributor.TaskDistributor;
import task_distributor.TaskQueue;
import data_analyzer.DataAnalyzer;

public class Webserver {
	private ConnectionList workers;
	private MessageInbox message_inbox;
	private DataAnalyzer analyzer;
	private static Sigar sigar = new Sigar();
	private TaskQueue tq;
	private TaskDistributor td;
	
	public Webserver(int port) throws IOException {
		this.workers = new ConnectionList();
		this.message_inbox = new MessageInbox(this.workers, port);
		this.tq = new TaskQueue();
		td = new TaskDistributor(tq, this.workers);
		this.analyzer = new DataAnalyzer(message_inbox.getMessageQueue(), this.workers);
		
		
		/* test connection list insertion:
		Connection c1 = new Connection(new Socket("localhost", 5000));
		Measurement m1 = new Measurement();
		m1.setMemoryInfo(2048, 512, 1536);
		c1.setLastMeasurement(m1);
		
		Connection c2 = new Connection(new Socket("localhost", 5000));
		Measurement m2 = new Measurement();
		m2.setMemoryInfo(2048, 1024, 1024);
		c2.setLastMeasurement(m2);
		
		Connection c3 = new Connection(new Socket("localhost", 5000));
		Measurement m3 = new Measurement();
		m3.setMemoryInfo(2048, 700, 1348);
		c3.setLastMeasurement(m3);

		workers.addConnection(c1);
		workers.addConnection(c2);
		workers.addConnection(c3);
		*/
	}
	
	public void start() {
		Thread t = new Thread(this.message_inbox);
		t.start();
		t = new Thread(this.analyzer);
		t.start();
		
		try {
			Thread.sleep(5000);
			//Task task = new Task("./inf hello");
			//task.setConn(this.workers.getFirst());
			//this.tq.enqueue(task);
			System.out.println("tq size: " + tq.size());
			td.start();
			//System.out.println("Started ./infinte as process #" + pid);
			//System.out.println("going to sleep for 10 seconds");
			//Thread.sleep(10000);
			//td.interrupt(pid);
			//System.out.println("interrupted process #" + pid);
			/*while(true) {
				Message<?> msg = this.message_inbox.getNextMessage();
				if(msg != null && msg.getMessageContent() instanceof TaskInfo) {
					TaskInfo tf = (TaskInfo)msg.getMessageContent();
					//System.out.println("Process exit status: " + td.getTaskData(tf.getPid()));
					System.out.println("Process output: " + td.getTaskData(tf.getPid()));
				}
				Thread.sleep(50);
			}*/
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
			ObjectOutputSinallytream oos = new ObjectOutputStream(s.getOutputStream());
			
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
	public void initJetty(int port) {
		Server server = new Server(8080);
		 
        ContextHandler context = new ContextHandler();
        context.setContextPath("/task");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(context);
 
        context.setHandler(new TaskApi(tq));
        try{
        server.start();
        server.join();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void main(String [] args) {
		if(args.length <= 0) {
			System.out.println("Give a port as argument 1");
			return;
		}
		int port = Integer.parseInt(args[0]);
		Webserver s;
		try {
			s = new Webserver(port);
			s.start();
			s.initJetty(8080);
		} catch (Exception e) {
			System.out.println("Server could not make a socket, aborting");
			return;
		}
	}
}
