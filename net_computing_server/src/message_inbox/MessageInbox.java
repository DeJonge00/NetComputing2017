package message_inbox;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageInbox implements Runnable {
	private ServerSocket server_socket;
	private MessageQueue messages;
	private ConnectionList workers;
	
	public MessageInbox(ConnectionList w, int port) throws IOException {
		this.workers = w;
		this.server_socket = new ServerSocket(port);
		this.messages = new MessageQueue();
	}
	
	public MessageQueue getMessageQueue() {
		return messages;
	}

	public Message<?> getNextMessage() {
		if(this.messages.isEmpty()) {
			return null;
		}
		return this.messages.dequeue();
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
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
