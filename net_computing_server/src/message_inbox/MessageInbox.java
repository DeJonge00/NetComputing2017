package message_inbox;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageInbox implements Runnable {
	private ServerSocket socket;
	private MessageQueue messages;
	private ConnectionList connections;
	
	public MessageInbox(ConnectionList connections, int port) throws IOException {
		this.connections = connections;
		this.socket = new ServerSocket(port);
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
				Socket worker = this.socket.accept();
				Connection connection = new Connection(worker);
				MessageReceiver receiver = new MessageReceiver(connection, this.messages);
				this.connections.addConnection(connection);
				Thread thread = new Thread(receiver);
				thread.start();
			} catch (IOException e) {
				System.out.println("Worker failed to connect, removing worker");
				return;
			}
		}
	}
}
