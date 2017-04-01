package task_distributor;

/* 
 * TaskApi listens to API calls from the webserver.
 * When  it receives an API call, it enqueues the task 
 * in the TaskQueue where it can be retrieved by the
 * TaskDistributor so it can be executed on a worker
 */
public class TaskApi {
	public TaskApi() {}
	
	public Task createTask(String command) {
		Task t = new Task(command);
		return t;
	}
}
