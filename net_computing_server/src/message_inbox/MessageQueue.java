package message_inbox;

import java.util.LinkedList;

import rmi.Measurement;
import rmi.TaskInfo;

public class MessageQueue extends LinkedList<Message<?>>{
	public MessageQueue() {
		
	}
	
	synchronized public void enqueue(Message<?> m) {
		this.add(m);
	}
	
	synchronized public Message<?> dequeue() {
		return this.removeFirst();
	}
	
	synchronized public int getFirstType() {
	
		Message<?> m = this.peekFirst();
		if(m != null) {
			if(m.getMessageContent() instanceof Measurement) {
				return 1;
			} else if (m.getMessageContent() instanceof TaskInfo) {
				return 2;
			}
		}
		
		return 0;
	}
}
