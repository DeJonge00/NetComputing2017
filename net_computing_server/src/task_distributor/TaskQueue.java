package task_distributor;

import java.util.LinkedList;

public class TaskQueue extends LinkedList<Task> {
	public TaskQueue() {
		super();
	}
	
	public synchronized Task dequeue() {
		//System.out.println("dequeue");
		if(size() > 0) {
			return this.removeFirst();
		} else {
			return null;
		}
	}
	
	public synchronized void enqueue(Task t) {
		this.add(t);
	}
}
