package message_inbox;
import java.util.LinkedList;

public class MessageInbox extends LinkedList<Message> {
	private ReceiveMessageSocket s;
	public void enqueue(Message m) {
		this.add(m);
	}
	
	public Message dequeue() {
		Message m = getFirst();
		return m;
	}
}
