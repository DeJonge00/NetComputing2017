package task_distributor;

public class TaskActive extends Task {
	private long startTime;
	private int pid;
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public TaskActive(Task t) {
		super(t);
	}
	
	protected TaskActive(TaskActive t) {
		super(t);
		this.startTime = t.getStartTime();
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	@Override
	public String toString() {
		return super.toString() + "<td>"+startTime+"</td>";
	}
}
