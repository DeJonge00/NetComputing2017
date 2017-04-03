import java.io.IOException;

import message_inbox.ConnectionList;
import message_inbox.MessageInbox;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

import task_distributor.TaskApi;
import task_distributor.TaskDistributor;
import task_distributor.TaskList;
import task_distributor.TaskQueue;
import data_analyzer.DataAnalyzer;

public class Webserver {
	private ConnectionList workers;
	private MessageInbox message_inbox;
	private DataAnalyzer analyzer;
	private TaskQueue taskqueue;
	private TaskDistributor distributor;
	private TaskList tasklist;
	
	public Webserver(int port) throws IOException {
		this.workers = new ConnectionList();
		this.message_inbox = new MessageInbox(this.workers, port);
		this.taskqueue = new TaskQueue();
		this.tasklist = new TaskList();
		this.distributor = new TaskDistributor(taskqueue, this.workers, this.tasklist);
		this.analyzer = new DataAnalyzer(message_inbox.getMessageQueue(), this.workers, tasklist);
	}
	
	/* Starts a message inbox and a data analyzer. */
	public void start() {
		Thread thread = new Thread(this.message_inbox);
		thread.start();
		thread = new Thread(this.analyzer);
		thread.start();
		
		try {
			Thread.sleep(5000);
			distributor.start();
		} catch (Exception e) {
			System.err.println("Exception in Server.start");
		}
	}
	
	/* Starts a webserver on the specified port. */
	public void initJetty(int port) {
		Server server = new Server(8080);
		 
        ContextHandler context = new ContextHandler();
        context.setContextPath("/task");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(context);
 
        context.setHandler(new TaskApi(taskqueue, tasklist));
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
			System.err.println("Server could not make a socket, aborting");
		}
	}
}
