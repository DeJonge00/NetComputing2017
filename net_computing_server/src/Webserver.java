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
	private MessageInbox messageInbox;
	private DataAnalyzer analyzer;
	private TaskQueue taskQueue;
	private TaskDistributor distributor;
	private TaskList taskList;
	
	public Webserver(int port) throws IOException {
		this.workers = new ConnectionList();
		this.messageInbox = new MessageInbox(this.workers, port);
		this.taskQueue = new TaskQueue();
		this.taskList = new TaskList();
		this.distributor = new TaskDistributor(taskQueue, this.workers, this.taskList);
		this.analyzer = new DataAnalyzer(messageInbox.getMessageQueue(), this.workers, taskList);
	}
	
	/* Starts a message inbox and a data analyzer. */
	public void start() {
		Thread thread = new Thread(this.messageInbox);
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
 
        context.setHandler(new TaskApi(taskQueue, taskList));
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
		Webserver server;
		try {
			server = new Webserver(port);
			server.start();
			server.initJetty(8080);
		} catch (Exception e) {
			System.err.println("Server could not make a socket, aborting");
		}
	}
}
