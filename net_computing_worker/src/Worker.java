import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import resource_monitor.ResourceMonitor;
import task_manager.TaskManager;


public class Worker {
	/* The following argumentlists work:
	 * <serverport>
	 * <serveraddress> <serverport>
	 * <serveraddress> <serverport> <rmi port>
	 */
	public static void main(String [] args) {
		if(args.length<=0) {
			System.err.println("Give a port to send results too!");
			return;
		}
		
		// Set serverport and serveraddress from the arguments
		int serverPort;
		InetAddress serverAddress;
		ResourceMonitor monitor;
		try {
			if(args.length>=2) {
				serverAddress = InetAddress.getByName(args[0]);
				serverPort = Integer.parseInt(args[1]);
			} else {
				serverAddress = InetAddress.getByName("localhost");
				serverPort = Integer.parseInt(args[0]);
			}
		} catch (UnknownHostException e) {
			System.err.println("That is not a valid address");
			return;
		}
		
		// Set local RMI to port if it is in the arguments
		int rmiport;
		if(args.length>=3) {
			rmiport = Integer.parseInt(args[1]);
		} else {
			rmiport = 1099;
		}
		System.out.println(rmiport);
		
		try {
			// Get security.policy filepath
			Path path = Paths.get(System.getProperty("user.dir"));
			System.getProperties().setProperty("java.library.path", path.toString());
			Path dir = Paths.get(path.toString(), "security.policy");
			
			System.getProperties().setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
			
			System.getProperties().setProperty("java.security.policy", dir.toString());
			System.out.println(dir.toString());
		} catch (UnknownHostException e) {
			System.err.println("Could not initialize localhost");
			return;
		}
		
		// Create resourcemonitor
		Socket socket;
		ObjectOutputStream out;
		try {
			socket = new Socket(serverAddress.getHostAddress(), serverPort);
			out = new ObjectOutputStream(socket.getOutputStream());
			monitor = new ResourceMonitor(out, rmiport);
			monitor.start();
		} catch (IOException e1) {
			System.err.println("Starting resourcemonitor failed");
			return;
		}
		

		
		// initialize taskManager
		try {
			TaskManager manager = new TaskManager(out);
			manager.initSecurityManager();
			LocateRegistry.createRegistry(rmiport);
			Naming.rebind("rmi://localhost:" + rmiport + "/taskManager", manager);
		} catch (Exception e) {
			System.err.println("remote exception in taskManager\n\n");
			monitor.quit();
		}
	}
}
