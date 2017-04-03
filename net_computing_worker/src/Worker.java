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
		
		// Check if worker has GCC installed
		boolean hasGcc = false;
		String[] paths = System.getenv("PATH").split(":");
		for(int i = 0; i < paths.length; i++) {
			if (new File(paths[i] + "/gcc").isFile()) {
				hasGcc = true;
			}
		}
		if (!hasGcc) {
			System.err.println("Worker most have gcc installed!");
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
		
		// Create resourcemonitor
		Socket socket;
		ObjectOutputStream out;
		try {
			socket = new Socket(serverAddress.getHostAddress(), serverPort);
			out = new ObjectOutputStream(socket.getOutputStream());
			monitor = new ResourceMonitor(out);
			monitor.start();
		} catch (IOException e1) {
			System.err.println("Starting resourcemonitor failed");
			return;
		}
		
		try {
			// Get security.policy filepath
			Path path = Paths.get(System.getProperty("user.dir")).getParent();
			Path dir = Paths.get(path.toString(), "net_computing_shared", "src", "rmi", "security.policy");
			
			System.getProperties().setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
			System.getProperties().setProperty("java.security.policy", dir.toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			monitor.quit();
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
			e.printStackTrace();
		}
	}
}
