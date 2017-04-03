package message_inbox;

import java.io.Serializable;
import java.net.InetAddress;

public class Message<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private T type;
	
	public Message(T type, Connection c) {
		this.type = type;
		this.connection = c;
	}
	
	public T getMessageContent() {
		return this.type;
	}
	
	public int getSenderPort() {
		return this.connection.getPort();
	}
	
	public InetAddress getSenderAddress() {
		return this.connection.getInetAddress();
	}
	
	public Connection getConn() {
		return this.connection;
	}
}
