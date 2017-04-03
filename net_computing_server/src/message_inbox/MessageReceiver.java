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
	
	public MessageReceiver(Connection connection, MessageQueue messages) {
		this.connection = connection;
		this.messages = messages;
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
	
	private Message<?> readMessage() {
		Message<?> message;
		Object object = null;
		
		try {
			object = this.in.readObject();
		} catch (Exception e) {
			System.out.println("Object could not be read, returning empty measurement");
		}
		if(object != null && object instanceof Measurement) {
			message = new Message<Measurement>((Measurement)object, this.connection);
		} else if(object instanceof TaskInfo){
			message = new Message<TaskInfo>((TaskInfo)object, this.connection);
		} else {
			message = new Message<Measurement>(new Measurement(), this.connection);
		}
		return message;
	}
	
	public void run() {
		try {
			this.in = new ObjectInputStream(this.connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		handleConnect();
		
		Message<?> message;
		while (true) {
			message = readMessage();
			this.messages.enqueue(message);
		}
	}
}
