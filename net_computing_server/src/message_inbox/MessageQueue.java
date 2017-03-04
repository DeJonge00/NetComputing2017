package message_inbox;

import java.util.LinkedList;

public class MessageQueue extends LinkedList<Message>{
	public MessageQueue() {
		
	}
	
	synchronized public void enqueue(Message m) {
		System.out.println("enqueued message: " + m.get_message_body());
		this.add(m);
	}
	
	synchronized public Message dequeue() {
		return this.removeFirst();
	}
}
