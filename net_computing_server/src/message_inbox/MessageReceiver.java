package message_inbox;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.StringBuilder;
import java.lang.Runnable;
import java.net.Socket;

public class MessageReceiver implements Runnable {
	private Connection connection;
	private ObjectInputStream in;
	private MessageQueue messages;
	
	public MessageReceiver(Connection c, MessageQueue mq) {
			this.connection = c;
			this.messages = mq;
			try {
				this.in = new ObjectInputStream(this.connection.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public Message read_message() {
		Message msg;
		Object o = null;
		
		try {
			o = this.in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(o != null && o instanceof Message) {
			msg = (Message)o;
		} else {
			msg = new Message("error");
		}
		
		
		return msg;
	}
	
	public void run() {
		Message msg;
		while (true) {
			msg = read_message();
			this.messages.enqueue(msg);
		}
	}
}
