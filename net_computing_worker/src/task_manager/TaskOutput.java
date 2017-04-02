package task_manager;

public class TaskOutput {
	private String output;
	private String error;

	public TaskOutput() {
		this.output = "not_finished";
		this.error = "";
	}
	public synchronized String getOutput() {
		return output;
	}
	
	public synchronized String getError() {
		return error;
	}
	
	public synchronized void setOutput(String output) {
		this.output = output;
	}
	
	public synchronized void setError(String error) {
		this.error = error;
	}
}
