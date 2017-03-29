package task_manager;

import java.util.Observable;
import java.util.Observer;

public class Task {
	private Thread taskThread;
	private int pid;
	private TaskOutput output;

	public Task(Thread t, TaskOutput outObj, int pid) {
		this.taskThread = t;
		this.pid = pid;
		this.output = outObj;
	}
	
	public int getPid() {
		return this.pid;
	}
	
	public void interrupt() {
		this.taskThread.interrupt();
	}
	
	public String getOutput() {
		return this.output.getOutput();
	}
}
