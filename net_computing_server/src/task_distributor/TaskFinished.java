package task_distributor;

public class TaskFinished extends TaskActive {
	private int endTime;
	private String taskOutput;
	private int exitStatus;
	
	public TaskFinished(TaskActive t, int endTime, String taskOutput, int exitStatus) {
		super(t);
		this.endTime = endTime;
		this.taskOutput = taskOutput;
		this.exitStatus = exitStatus;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public String getTaskOutput() {
		return taskOutput;
	}

	public void setTaskOutput(String taskOutput) {
		this.taskOutput = taskOutput;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(int exitStatus) {
		this.exitStatus = exitStatus;
	}
}
