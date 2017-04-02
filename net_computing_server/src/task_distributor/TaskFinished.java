package task_distributor;

public class TaskFinished extends TaskActive {
	private long endTime;
	private String taskOutput;
	private int exitStatus;
	
	public TaskFinished(TaskActive t, long endTime, String taskOutput, int exitStatus) {
		super(t);
		this.endTime = endTime;
		this.taskOutput = taskOutput;
		this.exitStatus = exitStatus;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
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
	
	@Override
	public String toString() {
		return super.toString() + "<td>" + endTime + "</td><td>" + exitStatus + "</td>";
	}
}
