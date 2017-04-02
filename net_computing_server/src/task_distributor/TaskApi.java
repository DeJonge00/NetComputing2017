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
	private TaskQueue tq;
	private TaskList tl;
	private int taskCounter;
	public TaskApi(TaskQueue tq, TaskList tl) {
		this.tq = tq;
		this.tl = tl;
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
				System.out.println("GET active_task");
			} else if(target.matches("/finished_task/?")) {
				do_GET_FinishedTask(response.getWriter());
				// INDEX finished tasks
				System.out.println("GET finished_task");
			} else if(target.matches("/create/?")) {
				// NEW task
				do_GET_CreateTask(response.getWriter());
				System.out.println("GET create task form");
			} else if (target.equals("/")) {
				do_GET_tasks(response.getWriter());
			} else if(target.matches("/\\d")) {
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
	
	void do_GET_task(PrintWriter writer, int taskId) {
		Task t = tl.findByTaskId(taskId);
		if(t != null) {
			if(t instanceof TaskFinished) {
				writer.println("<h1>standard input for task "+taskId+":</h1><br>");
				String out = t.getInput();
				String[] lines = out.split("\\r?\\n");
				for(String line : lines) {
					writer.println(line + "<br>");
				}
				
				writer.println("<h1>standard output from task "+taskId+":</h1><br>");
				out = ((TaskFinished)t).getTaskOutput();
				lines = out.split("\\r?\\n");
				for(String line : lines) {
					writer.println(line + "<br>");
				}
				
				writer.println("<h1>standard error from task "+taskId+":</h1><br>");
				out = ((TaskFinished)t).getTaskError();
				lines = out.split("\\r?\\n");
				for(String line : lines) {
					writer.println(line + "<br>");
				}
			} else {
				writer.println("<h1>Task with ID=" + taskId + " is still executing</h1>");
			}
		} else {
			writer.println("<h1>Task with ID=" + taskId + " not found</h1>");
		}
	}
	
	void do_GET_tasks(PrintWriter writer) {
		writer.println("<table class='tab'>");		
		writer.println("<tr><td class='tasktable'>");
		
		do_GET_ActiveTask(writer);
		
		writer.println("</td><td class='tasktable'>");
		
		do_GET_FinishedTask(writer);
		
		writer.println("</td></tr>");
		writer.println("</table>");
	}
	void do_GET_ActiveTask(PrintWriter writer) {
		writer.println("<h2>Active Tasks</h2><br>");
		writer.println("<table class='tab'>");
		writer.println("<tr><th>command</th><th>taskId</th><th>userId</th><th>started at</th></tr>");
		for(TaskActive ta : tl.getActiveTasks()) {
			writer.println("<tr>" + ta.toString() + 
					"<td><form method='GET' action='/task/" + ta.getTaskId() + "'><button type='submit'>view</button></form></td>" +
					"<td><form method='POST' action='/task/" + ta.getTaskId() + "'><button type='submit'>delete</button></form></td></tr>");
		}
		writer.println("</table>");
	}
	
	void do_GET_FinishedTask(PrintWriter writer) {
		writer.println("<h2>Finished Tasks</h2><br>");
		writer.println("<table class='tab'>");

		writer.println("<tr><th>command</th><th>taskId</th><th>userId</th><th>started at</th><th>finished at</th><th>exit status</th></tr>");
		for(TaskFinished tf : tl.getFinishedTasks()) {
			writer.println("<tr>" + tf.toString() + 
						"<td><form method='GET' action='/task/" + tf.getTaskId() + "'><button type='submit'>view</button></form></td>" +
						"<td><form method='POST' action='/task/" + tf.getTaskId() + "'><button type='submit'>delete</button></form></td></tr>");
		}
		writer.println("</table>");
	}

	void do_GET_CreateTask(PrintWriter writer) {  
        writer.println("<form method='POST'>");
        writer.println("<h2>Create a new Task</h2><br>");
        writer.println("Command to execute: <br>");
        writer.println("<input type='text' name='command'><br>");
        writer.println("Program input:<br>");
        writer.println("<textarea name='input' placeholder='enter program input here (optional)'></textarea><br>");
        writer.println("<input type='submit'>");
        writer.println("</form>");
	}

	void do_POST_CreateTask(String command, String input) {
		// process post data
		Task t = new Task(command, taskCounter, input);
		taskCounter++;
		t.setUserId(1);
		this.tq.enqueue(t);
	}

	void do_DELETE_DestroyTask(PrintWriter writer, int taskId) {
		this.tl.remove(taskId);
	}
}
