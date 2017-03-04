package message_inbox;
import java.io.Serializable;

public class Message<T> implements Serializable {
	private T t;
	
	public Message(T t) {
		this.t = t;
	}
	
	
	public T getMessageContent() {
		return this.t;
	}
}
