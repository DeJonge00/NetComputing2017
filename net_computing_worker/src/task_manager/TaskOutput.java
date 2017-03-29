package task_manager;

public class TaskOutput {
	private String output;
	public TaskOutput() {
		this.output = "not_finished";
	}
	public synchronized String getOutput() {
		return output;
	}
	public synchronized void setOutput(String output) {
		this.output = output;
		System.out.println("\n\nset output to: " + output);
	}
}
