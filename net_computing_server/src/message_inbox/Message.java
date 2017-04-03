package message_inbox;

import java.io.Serializable;
import java.net.InetAddress;

public class Message<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private T t;
	
	public Message(T t, Connection c) {
		this.t = t;
		this.conn = c;
	}
	
	public T getMessageContent() {
		return this.t;
	}
	
	public int getSenderPort() {
		return this.conn.getPort();
	}
	
	public InetAddress getSenderAddress() {
		return this.conn.getInetAddress();
	}
	
	public Connection getConn() {
		return this.conn;
	}
}
