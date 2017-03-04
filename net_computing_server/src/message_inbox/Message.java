package message_inbox;
import java.io.Serializable;

public class Message implements Serializable {
	private String message_body;
	
	public Message() {
		this.message_body = "";
	}
	
	public Message(String message_body) {
		this.message_body = message_body;
	}
	
	public String get_message_body() {
		return this.message_body;
	}
}
