package task_distributor;

public class ActiveTask extends Task {
	private long startTime;
	private int pid;
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public ActiveTask(Task t) {
		super(t);
	}
	
	protected ActiveTask(ActiveTask t) {
		super(t);
		this.startTime = t.getStartTime();
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
