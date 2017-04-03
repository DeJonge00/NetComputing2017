package message_inbox;

import java.util.LinkedList;

import rmi.Measurement;
import rmi.TaskInfo;

public class MessageQueue extends LinkedList<Message<?>>{
	private static final long serialVersionUID = 1L;
	
	public MessageQueue() {
		super();
	}

	synchronized public void enqueue(Message<?> message) {
		this.add(message);
	}
	
	synchronized public Message<?> dequeue() {
		return this.removeFirst();
	}
	
	synchronized public int getFirstType() {
		Message<?> message = this.peekFirst();
		
		if(message != null) {
			if(message.getMessageContent() instanceof Measurement) {
				return 1;
			} else if (message.getMessageContent() instanceof TaskInfo) {
				return 2;
			}
		}
		return 0;
	}
}
