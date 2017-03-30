package data_analyzer;

import message_inbox.Message;
import message_inbox.MessageQueue;
import rmi.Measurement;

public class DataAnalyzer implements Runnable {
	
	private MessageQueue messages;
	
	public DataAnalyzer(MessageQueue messages) {
		this.messages = messages;
	}
	
	@SuppressWarnings("unused")
	public void run() {
		while (true) {
			Message<?> message = messages.peek();
			if (message != null && message.getMessageContent() instanceof Measurement) {
				Measurement measure = (Measurement) messages.dequeue().getMessageContent();
				System.out.println("-" + measure.toString());
				if (false) {
					//
				}
			}
		}
	}
}
