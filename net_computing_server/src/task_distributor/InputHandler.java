package task_distributor;

public class InputHandler {
	public InputHandler() {}
	
	public Task createTask(String command) {
		Task t = new Task(command);
		return t;
	}
}
