package message_inbox;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import task_distributor.TaskDistributor;

public class MessageInbox implements Runnable {
	private ServerSocket server_socket;
	private MessageQueue messages;
	private ConnectionList workers;
	
	public MessageInbox(ConnectionList w, int port) {
		this.workers = w;
		try {
			this.server_socket = new ServerSocket(port);
			this.messages = new MessageQueue();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error creating socket");
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket worker = this.server_socket.accept();
				Connection conn = new Connection(worker);
				MessageReceiver mr = new MessageReceiver(conn, this.messages);
				this.workers.addConnection(conn);
				Thread t = new Thread(mr);
				t.start();
				TaskDistributor td = new TaskDistributor();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
