package rmi;

import java.io.Serializable;

public class TaskInfo implements Serializable {
	public static final long serialVersionUID = 123;
	private int pid;
	private int status;
	private long finishTime;
	
	public TaskInfo(int pid, int s, long time) {
		this.pid = pid;
		this.status = s;
		this.finishTime = time;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toString() {
		return "pid: " + pid + "status: " + status;
	}
	
	public long getFinishTime() {
		return finishTime;
	}
	
	public void setFinishTime(long time) {
		this.finishTime = time;
	}
}
