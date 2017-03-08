package message_inbox;

import java.io.IOException;
import java.io.ObjectInputStream;

import resource_monitor.Measurement;


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
	
	public Message<Measurement> read_message() {
		Message<Measurement> msg;
		Object o = null;
		
		try {
			o = this.in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(o != null && o instanceof Measurement) {
			msg = new Message<Measurement>((Measurement)o);
		} else {
			msg = new Message<Measurement>(new Measurement(0, 0, 0));
		}
		
		
		return msg;
	}
	
	public void run() {
		Message<Measurement> msg;
		while (true) {
			msg = read_message();
			this.messages.enqueue(msg);
		}
	}
}