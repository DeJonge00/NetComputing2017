package task_distributor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/* 
 * TaskApi listens to API calls from the webserver.
 * When  it receives an API call, it enqueues the task 
 * in the TaskQueue where it can be retrieved by the
 * TaskDistributor so it can be executed on a worker
 */
//@WebServlet(name = "task", urlPatterns = {"/tasks"})
public class TaskApi extends AbstractHandler {
	private TaskQueue taskQueue;
	private TaskList taskList;
	private int taskCounter;
	
	public TaskApi(TaskQueue taskQueue, TaskList taskList) {
		this.taskQueue = taskQueue;
		this.taskList = taskList;
		this.taskCounter = 0;
	}
	
	public void handle(String target,Request baseRequest,HttpServletRequest request,HttpServletResponse response) 
	        throws IOException, ServletException
	    {
		boolean err404 = false;
		HTMLTemplate.renderTemplate(response.getWriter(), "./HTML/Header.html");
		if(baseRequest.getMethod().equals("GET")) {
			if(target.matches("/active_task/?")) {
				// INDEX active tasks
				do_GET_ActiveTask(response.getWriter());
			} else if(target.matches("/finished_task/?")) {
				do_GET_FinishedTask(response.getWriter());
				// INDEX finished tasks
			} else if(target.matches("/create/?")) {
				// NEW task
				do_GET_CreateTask(response.getWriter());
			} else if (target.equals("/")) {
				do_GET_tasks(response.getWriter());
			} else if(target.matches("/(\\d)+")) {
				do_GET_task(response.getWriter(), Integer.parseInt(target.substring(1)));
			} else {
				err404 = true;
			}
		} else if (baseRequest.getMethod().equals("POST")) {
			if(target.matches("/create/?")) {
				// CREATE task
				do_POST_CreateTask(
						baseRequest.getParameter("command"),
						baseRequest.getParameter("input"));
				response.sendRedirect("/task/create/");
			} else if (target.matches("/\\d")) {
				// Standard HTML5 forms do not support sending DELETE requests, so we post to the resource instead
				do_DELETE_DestroyTask(response.getWriter(), Integer.parseInt(target.substring(1)));
				response.sendRedirect("/task/");
			} else {
				err404 = true;
			}
		} else {
			err404 = true;
		}
		
		if(err404 == true) {
			HTMLTemplate.renderTemplate(response.getWriter(), "./HTML/404body.html");
		}
		HTMLTemplate.renderTemplate(response.getWriter(), "./HTML/Footer.html");
		
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}
	
	/* Prints the output of the task with the specified taskId. */
	void do_GET_task(PrintWriter writer, int taskId) {
		Task task = taskList.findByTaskId(taskId);
		if(task != null) {
			if(task instanceof TaskFinished) {
				writer.println("<h2>standard input for task "+taskId+":</h2><br><div class='outputdiv'>");
				String out = task.getInput();
				String[] lines = out.split("\\r?\\n");
				for(String line : lines) {
					writer.println(line + "<br>");
				}
				
				writer.println("</div><br><h2>standard output from task "+taskId+":</h2><br><div class='outputdiv'>");
				out = ((TaskFinished) task).getTaskOutput();
				lines = out.split("\\r?\\n");
				for(String line : lines) {
					writer.println(line + "<br>");
				}
				
				writer.println("</div><br><h2>standard error from task "+taskId+":</h2><br><div class='outputdiv'>");
				out = ((TaskFinished) task).getTaskError();
				lines = out.split("\\r?\\n");
				for(String line : lines) {
					writer.println(line + "<br>");
				}
				writer.println("</div>");
			} else {
				writer.println("<h1>Task with ID=" + taskId + " is still executing</h1>");
			}
		} else {
			writer.println("<h1>Task with ID=" + taskId + " not found</h1>");
		}
	}
	
	/* Prints two HTML tables: one with all active tasks and one with all finished tasks. */
	void do_GET_tasks(PrintWriter writer) {
		writer.println("<table class='tab'>");		
		writer.println("<tr><td class='tasktable'>");
		
		do_GET_ActiveTask(writer);
		
		writer.println("</td><td class='tasktable'>");
		
		do_GET_FinishedTask(writer);
		
		writer.println("</td></tr>");
		writer.println("</table>");
	}
	
	/* Writes a table with all active tasks to writer. */
	void do_GET_ActiveTask(PrintWriter writer) {
		writer.println("<h2>Active Tasks</h2><br>");
		writer.println("<table class='tab'>");
		writer.println("<tr><th>command</th><th>taskId</th><th>userId</th><th>started at</th></tr>");
		for(TaskActive active : taskList.getActiveTasks()) {
			writer.println("<tr>" + active.toString() + 
					"<td><form method='GET' action='/task/" + active.getTaskId() + "'><button type='submit'>view</button></form></td>" +
					"<td><form method='POST' action='/task/" + active.getTaskId() + "'><button type='submit'>delete</button></form></td></tr>");
		}
		writer.println("</table>");
	}
	
	/* Writes a table with all active tasks to writer. */
	void do_GET_FinishedTask(PrintWriter writer) {
		writer.println("<h2>Finished Tasks</h2><br>");
		writer.println("<table class='tab'>");

		writer.println("<tr><th>command</th><th>taskId</th><th>userId</th><th>started at</th><th>finished at</th><th>exit status</th></tr>");
		for(TaskFinished finished : taskList.getFinishedTasks()) {
			writer.println("<tr>" + finished.toString() + 
						"<td><form method='GET' action='/task/" + finished.getTaskId() + "'><button type='submit'>view</button></form></td>" +
						"<td><form method='POST' action='/task/" + finished.getTaskId() + "'><button type='submit'>delete</button></form></td></tr>");
		}
		writer.println("</table>");
	}

	/* Writes a HTML form for creating a new task to writer. */
	void do_GET_CreateTask(PrintWriter writer) {
        writer.println("<div'><form method='POST' class='createform'>");
        writer.println("<h2>Create a new Task</h2><br>");
        writer.println("<table class='tab'><tr><td>Command to execute: </td></tr>");
        writer.println("<tr><td><input style='width:100%;' type='text' name='command'></td></tr>");
        writer.println("<tr><td>Program input:</td></tr>");
        writer.println("<tr><td><textarea style='width:100%;' name='input' placeholder='enter program input here (optional)'></textarea></td></tr>");
        writer.println("<tr><td><input style='width:100%;' type='submit'></td></tr>");
        writer.println("</table></form></div>");
	}

	/* Create a new task when the data from the create form is POSTed. */
	void do_POST_CreateTask(String command, String input) {
		System.out.println("\n\n\nmatched command "+command+"\n\n\n");
			Task task = new Task(command, taskCounter, input);
			taskCounter++;
			task.setUserId(1);
			this.taskQueue.enqueue(task);
	}

	/* Remove a task from the tasklist. */
	void do_DELETE_DestroyTask(PrintWriter writer, int taskId) {
		this.taskList.remove(taskId);
	}
}
