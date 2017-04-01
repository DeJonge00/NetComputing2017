package task_distributor;

import java.util.ArrayList;

public class TaskList {
	public ArrayList<ActiveTask> activeTasks;
	public ArrayList<FinishedTask> finishedTasks;
	
	public TaskList() {
		activeTasks = new ArrayList<ActiveTask>();
		finishedTasks = new ArrayList<FinishedTask>();
	}
	
	public void insertTask(Task task) {
		if (task instanceof FinishedTask) {
			int index = find(finishedTasks, task.getUserId());
			finishedTasks.add(index, (FinishedTask) task);
		} else {
			int index = find(activeTasks, task.getUserId());
			activeTasks.add(index, (FinishedTask) task);
		}
	}
	
	public ArrayList<ActiveTask> getActiveTasksUser(int id) {
		ArrayList<ActiveTask> usertasks = new ArrayList<ActiveTask>();
		int i = find(activeTasks, id);
		for (; activeTasks.get(i).getUserId() == id; i++) {
			usertasks.add(activeTasks.get(i));
			i++;
		}
		return usertasks;
	}
	
	public ArrayList<FinishedTask> getFinishedTasksUser(int id) {
		ArrayList<FinishedTask> usertasks = new ArrayList<FinishedTask>();
		int i = find(finishedTasks, id);
		for (; finishedTasks.get(i).getUserId() == id; i++) {
			usertasks.add(finishedTasks.get(i));
			i++;
		}
		return usertasks;
	}
	
	public void finishTask(ActiveTask task) {
		int index = activeTasks.indexOf(task);
		activeTasks.remove(index);
		finishedTasks.add(new FinishedTask(task));
	}
	
	/* Implements binary search to find the first occurence of a task with userid id */
	private int find(ArrayList<? extends Task> list, int id) {
		int l = 0, r = list.size() - 1, m = 0, currId;
		while(l <= r) {
			m = (l + r) / 2;
			currId = list.get(m).getUserId();
			if (currId < id) {
				r = m - 1;
			} else if (currId > id) {
				l = m + 1;
			} else if (l != m) {
				r = m;
			} else {
				break;
			}
		}
		return m;
	}
}
