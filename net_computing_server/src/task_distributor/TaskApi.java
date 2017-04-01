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
	public TaskApi(TaskQueue tq) {
		this.tq = tq;
	}
	
	public void handle(String target,Request baseRequest,HttpServletRequest request,HttpServletResponse response) 
	        throws IOException, ServletException
	    {
		if(baseRequest.getMethod().equals("GET")) {
			if(target.equals("/active_task")) {
				// INDEX active tasks
				do_GET_ActiveTask(response.getWriter());
				System.out.println("GET active_task");
			} else if(target.equals("/finished_task")) {
				do_GET_FinishedTask(response.getWriter());
				// INDEX finished tasks
				System.out.println("GET finished_task");
			} else if(target.equals("/create")) {
				// NEW task
				do_GET_CreateTask(response.getWriter());
				System.out.println("GET create task form");
			}
		} else if (baseRequest.getMethod().equals("POST")) {
			if(target.equals("/create")) {
				// CREATE task
				do_POST_CreateTask(
						baseRequest.getParameter("command"), 
						baseRequest.getParameter("name"));
				response.sendRedirect("/task/create");
				System.out.println("POST create task form");
			}
		} else if (baseRequest.getMethod().equals("DELETE")) {
			if(target.equals("/destroy")) {
				// DESTROY task
				do_DELETE_DestroyTask(response.getWriter());
				System.out.println("DELETE destroy task");
			}
		}
		
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}

	void do_GET_ActiveTask(PrintWriter writer) {
		writer.println("ActiveTask list");
	}
	
	void do_GET_FinishedTask(PrintWriter writer) {
		writer.println("FinishedTask list");
	}

	void do_GET_CreateTask(PrintWriter writer) {  
        writer.println("<form method='POST'>");
        writer.println("<h2>Create a new Task</h2><br>");
        writer.println("Command to execute: <br>");
        writer.println("<input type='text' name='command'><br>");
        writer.println("Name:<br>");
        writer.println("<input type='text' name='name'><br>");
        writer.println("<input type='submit'>");
        writer.println("</form>");
	}

	void do_POST_CreateTask(String command, String name) {
		// process post data
		System.out.println("POST createTask received command: " + command +
				"\nFrom: " + name);
		Task t = new Task(command);
		t.setUserId(1);
		this.tq.enqueue(t);
	}

	void do_DELETE_DestroyTask(PrintWriter writer) {
		
	}
}
