import java.io.ObjectOutputStream;
import java.net.Socket;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import message_inbox.ConnectionList;
import message_inbox.Message;
import message_inbox.MessageInbox;

public class Server {
	private ConnectionList workers;
	private MessageInbox message_inbox;
	private static Sigar sigar = new Sigar();
	
	public Server() {
		this.workers = new ConnectionList();
		this.message_inbox = new MessageInbox(this.workers);
	}
	
	public void start() {
		Thread t = new Thread(this.message_inbox);
		t.start();
	}
	
	public void takeMeasurement() {
		Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException se) {
            se.printStackTrace();
        }

        System.out.println("Actual total free system memory: "
                + mem.getActualFree() / 1024 / 1024+ " MB");
        System.out.println("Actual total used system memory: "
                + mem.getActualUsed() / 1024 / 1024 + " MB");
        System.out.println("Total free system memory ......: " + mem.getFree()
                / 1024 / 1024+ " MB");
        System.out.println("System Random Access Memory....: " + mem.getRam()
                + " MB");
        System.out.println("Total system memory............: " + mem.getTotal()
                / 1024 / 1024+ " MB");
        System.out.println("Total used system memory.......: " + mem.getUsed()
                / 1024 / 1024+ " MB");

        System.out.println("\n**************************************\n");
	}
	
	public void test() {
		System.out.println("Started messageInbox, sleeping for 500ms");
		try {
			Thread.sleep(500);
			this.takeMeasurement();
			Socket s = new Socket("localhost", 5000);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			
			for(int i=0; i<1000; i++) {
				Message msg = new Message(String.format("test%d",i));
				oos.writeObject(msg);
				System.out.println("Sent message to inbox");
				//Thread.sleep(100);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.takeMeasurement();
	}
	
	public static void main(String [] args) {
		Server s = new Server();
		s.start();
		
		s.test();
	}
}
