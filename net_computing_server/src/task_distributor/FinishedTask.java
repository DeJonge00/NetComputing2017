package task_distributor;

public class FinishedTask extends ActiveTask {
	private int endTime;
	private String taskOutput;
	private int exitStatus;
	
	public FinishedTask(ActiveTask t) {
		super(t);
	}
}
