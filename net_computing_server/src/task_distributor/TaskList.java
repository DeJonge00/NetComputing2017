package task_distributor;

import java.util.ArrayList;

public class TaskList {
	public ArrayList<TaskActive> activeTasks;
	public ArrayList<TaskFinished> finishedTasks;
	
	public TaskList() {
		activeTasks = new ArrayList<TaskActive>();
		finishedTasks = new ArrayList<TaskFinished>();
	}
	
	/* Inserts a new task in the correct list */
	public void insertTask(Task task) {
		if (task instanceof TaskFinished) {
			int index = find(finishedTasks, task.getUserId());
			finishedTasks.add(index, (TaskFinished) task);
		} else {
			int index = find(activeTasks, task.getUserId());
			activeTasks.add(index, (TaskFinished) task);
		}
	}
	
	/* Returns all active tasks with the specified user id */
	public ArrayList<TaskActive> getActiveTasksUser(int id) {
		ArrayList<TaskActive> usertasks = new ArrayList<TaskActive>();
		int i = find(activeTasks, id);
		for (; activeTasks.get(i).getUserId() == id; i++) {
			usertasks.add(activeTasks.get(i));
			i++;
		}
		return usertasks;
	}
	
	/* Returns all finished tasks with the specified user id */
	public ArrayList<TaskActive> getFinishedTasksUser(int id) {
		ArrayList<TaskActive> usertasks = new ArrayList<TaskActive>();
		int i = find(finishedTasks, id);
		for (; finishedTasks.get(i).getUserId() == id; i++) {
			usertasks.add(finishedTasks.get(i));
			i++;
		}
		return usertasks;
	}
	
	/* Removes task from active list and adds a new finished task to finished list */
	public void finishTask(TaskActive task) {
		int index = findTask(task);
		if (index >= 0) {
			activeTasks.remove(index);
			finishedTasks.add(new TaskFinished(task));
		}
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
	
	public int findTask(TaskActive task) {
	/* Finds an active task in the task list */
		for (int i = 0; i < activeTasks.size(); i++) {
			if (activeTasks.get(i).getPid() == task.getPid()
			&& activeTasks.get(i).getConn() == task.getConn()) {
				return i;
			}
		}
		return -1;
	}
}
