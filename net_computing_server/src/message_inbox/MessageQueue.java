package message_inbox;

import java.util.LinkedList;

import rmi.Measurement;

public class MessageQueue extends LinkedList<Message<?>>{
	public MessageQueue() {
		
	}
	
	synchronized public void enqueue(Message<?> m) {
		Object o = m.getMessageContent();
		if(o instanceof Measurement) {
			System.out.println(((Measurement)o).toString());
		}
		this.add(m);
	}
	
	synchronized public Message<?> dequeue() {
		return this.removeFirst();
	}
}
