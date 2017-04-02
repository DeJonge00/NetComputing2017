import java.io.IOException;

import message_inbox.ConnectionList;
import message_inbox.MessageInbox;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.hyperic.sigar.Sigar;

import task_distributor.TaskApi;
import task_distributor.TaskDistributor;
import task_distributor.TaskList;
import task_distributor.TaskQueue;
import data_analyzer.DataAnalyzer;

public class Webserver {
	private ConnectionList workers;
	private MessageInbox message_inbox;
	private DataAnalyzer analyzer;
	private static Sigar sigar = new Sigar();
	private TaskQueue tq;
	private TaskDistributor td;
	private TaskList tl;
	
	public Webserver(int port) throws IOException {
		this.workers = new ConnectionList();
		this.message_inbox = new MessageInbox(this.workers, port);
		this.tq = new TaskQueue();
		this.tl = new TaskList();
		this.td = new TaskDistributor(tq, this.workers, this.tl);
		
		this.analyzer = new DataAnalyzer(message_inbox.getMessageQueue(), this.workers, tl);
	}
	
	public void start() {
		Thread t = new Thread(this.message_inbox);
		t.start();
		t = new Thread(this.analyzer);
		t.start();
		
		try {
			Thread.sleep(5000);
			System.out.println("tq size: " + tq.size());
			td.start();
		} catch (Exception e) {
			System.out.println("Exception in Server.start");
			return;
		}
	}
	
	public void initJetty(int port) {
		Server server = new Server(8080);
		 
        ContextHandler context = new ContextHandler();
        context.setContextPath("/task");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(context);
 
        context.setHandler(new TaskApi(tq, tl));
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
