package message_inbox;


import java.io.IOException;
import java.io.ObjectInputStream;

import rmi.InitData;
import rmi.Measurement;
import rmi.TaskInfo;


public class MessageReceiver implements Runnable {
	private Connection connection;
	private ObjectInputStream in;
	private MessageQueue messages;
	
	public MessageReceiver(Connection c, MessageQueue mq) {
		this.connection = c;
		this.messages = mq;
	}
	
	public void handleConnect() {
		try {
			Object o = this.in.readObject();
			if(o instanceof InitData) {
				this.connection.setData((InitData)o);
			}
		} catch (Exception e) {
			System.out.println("Object could not be read (handleconnect)");
			e.printStackTrace();
			return;
		}
	}
	
	public Message<?> read_message() {
		Message<?> msg;
		Object o = null;
		
		try {
			o = this.in.readObject();
		} catch (Exception e) {
			System.out.println("Object could not be read, returning empty measurement");
		}
		if(o != null && o instanceof Measurement) {
			msg = new Message<Measurement>((Measurement)o, this.connection);
		} else if(o instanceof TaskInfo){
			msg = new Message<TaskInfo>((TaskInfo)o, this.connection);
		} else {
			msg = new Message<Measurement>(new Measurement(), this.connection);
		}
		return msg;
	}
	
	public void run() {
		try {
			this.in = new ObjectInputStream(this.connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		handleConnect();
		
		Message<?> msg;
		while (true) {
			msg = read_message();
			this.messages.enqueue(msg);
		}
	}
}
