import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	private MessageInbox messageInbox;
	private DataAnalyzer analyzer;
	private TaskDistributor distributor;
	private TaskApi taskApi;
	
	public Webserver(int port) throws IOException {
		ConnectionList workers = new ConnectionList();
		TaskQueue taskQueue = new TaskQueue();
		TaskList taskList = new TaskList();
		
		this.messageInbox = new MessageInbox(workers, port);
		this.distributor = new TaskDistributor(taskQueue, workers, taskList);
		this.analyzer = new DataAnalyzer(messageInbox.getMessageQueue(), workers, taskList);
		this.taskApi = new TaskApi(taskQueue, taskList);
	}
	
	/* Starts a message inbox and a data analyzer. */
	public void start() {
		Thread messageInboxThread = new Thread(this.messageInbox);
		messageInboxThread.start();
		Thread analyzerThread = new Thread(this.analyzer);
		analyzerThread.start();
		
		distributor.start();
	}
	
	/* Starts a webserver on the specified port. */
	public void initJetty(int port) throws Exception {
		Server server = new Server(8080);
		 
        ContextHandler context = new ContextHandler();
        context.setContextPath("/task");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(context);
 
        context.setHandler(this.taskApi);

        server.start();
        server.join();
	}
	
	public static void main(String [] args) {
		if(args.length <= 0) {
			System.err.println("Give a port as argument 1");
			return;
		}
		int port = Integer.parseInt(args[0]);
		
		// Setup policy file
		Path p = Paths.get(System.getProperty("user.dir")).getParent();
		Path dir = Paths.get(p.toString(), "net_computing_shared", "src", "rmi", "security.policy");
		System.out.println(dir.toString());
		System.getProperties().setProperty("java.security.policy", dir.toString());
		
		Webserver server;
		try {
			server = new Webserver(port);
			server.start();
			server.initJetty(8080);
		} catch (Exception e) {
			System.err.println("Server could not make a socket or Jetty, aborting");
		}
	}
}
