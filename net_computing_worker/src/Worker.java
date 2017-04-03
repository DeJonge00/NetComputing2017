import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
			System.out.println("Give a port to send results too!");
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
			System.out.println("That is not a valid address");
			return;
		}
		
		// Set local RMI to port if it is in the arguments
		int rmiport;
		if(args.length>=3) {
			rmiport = Integer.parseInt(args[1]);
		} else {
			rmiport = 1099;
		}
		
		// Create resourcemonitor
		Socket socket;
		ObjectOutputStream oos;
		try {
			socket = new Socket(serverAddress.getHostAddress(), serverPort);
			oos = new ObjectOutputStream(socket.getOutputStream());
			monitor = new ResourceMonitor(oos);
			monitor.start();
		} catch (IOException e1) {
			System.out.println("Starting resourcemonitor failed");
			return;
		}
		
		// initialize taskManager
		try {
			TaskManager tm = new TaskManager(oos);
			tm.initSecurityManager();
			System.out.println("\n\nStarting registry");
			//TaskManager stub = (TaskManager) UnicastRemoteObject.exportObject(tm, 0);
			Registry registry = LocateRegistry.createRegistry(rmiport);
			//System.out.println(registry);
			Naming.rebind("rmi://localhost:" + rmiport + "/taskManager", tm);
			//System.out.println(registry);
			//System.out.println("\n\n" + registry.lookup("taskManager"));
			//System.out.println("\n" + Naming.lookup("rmi://localhost:" + rmiport + "/taskManager"));
			
            System.out.println("TaskManager bound\n\n");
		} catch (Exception e) {
			System.out.println("remote exception in taskManager\n\n");
			monitor.quit();
			e.printStackTrace();
		}
	}
}
