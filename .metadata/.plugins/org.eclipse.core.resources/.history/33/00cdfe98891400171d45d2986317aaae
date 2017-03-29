package message_inbox;

import java.util.LinkedList;

public class MessageQueue extends LinkedList<Message<?>>{
	public MessageQueue() {
		
	}
	
	synchronized public void enqueue(Message<?> m) {
		this.add(m);
	}
	
	synchronized public Message<?> dequeue() {
		return this.removeFirst();
	}
}
