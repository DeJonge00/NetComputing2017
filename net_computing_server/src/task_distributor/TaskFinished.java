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
}
