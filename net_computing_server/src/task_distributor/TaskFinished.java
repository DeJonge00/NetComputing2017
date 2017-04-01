package task_distributor;

public class TaskFinished extends TaskActive {
	private int endTime;
	private String taskOutput;
	private int exitStatus;
	
	public TaskFinished(TaskActive t) {
		super(t);
	}
}
