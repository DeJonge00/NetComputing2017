package data_analyzer;

import java.io.IOException;
import java.net.Socket;

import message_inbox.Message;
import message_inbox.MessageQueue;
import resource_monitor.Measurement;

public class DataAnalyzer implements Runnable {
	
	private MessageQueue messages;
	
	public DataAnalyzer(MessageQueue messages) {
		this.messages = messages;
	}
	
	public void run() {
		while (true) {
			Message<?> m = messages.peek();
			if (m.getMessageContent() instanceof Measurement) {
				
			}
		}
	}
}
