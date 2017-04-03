package task_distributor;

import java.util.LinkedList;

public class TaskQueue extends LinkedList<Task> {
	private static final long serialVersionUID = 1L;
	
	public TaskQueue() {
		super();
	}
	
	public synchronized Task dequeue() {
		if(size() > 0) {
			return this.removeFirst();
		} else {
			return null;
		}
	}
	public synchronized void enqueue(Task task) {
		this.add(task);
	}
}
