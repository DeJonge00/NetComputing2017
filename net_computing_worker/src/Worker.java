import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import resource_monitor.ResourceMonitor;
import task_manager.TaskManager;



public class Worker {	
	public static void main(String [] args) {
		if(args.length<=0) {
			System.out.println("Give a port to send results too!");
			return;
		}
		
		
		int serverPort;
		InetAddress serverAddress;
		ResourceMonitor monitor;
		

		//System.setProperty("java.security.policy","../lib/security.policy‌​");
		
		try {
			if(args.length==2) {
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
		
		monitor = new ResourceMonitor(serverAddress, serverPort);
		monitor.start();
		
		
		// initialize taskManager
		
		
		try {
			TaskManager tm = new TaskManager();
			tm.initSecurityManager();
			System.out.println("\n\nStarting registry");
			//TaskManager stub = (TaskManager) UnicastRemoteObject.exportObject(tm, 0);
			Registry registry = LocateRegistry.createRegistry(1099);
			//System.out.println(registry);
			Naming.rebind("rmi://145.97.164.22:1099/taskManager", tm);
			//System.out.println(registry);
			//System.out.println("\n\n" + registry.lookup("taskManager"));
			System.out.println("\n\n" + Naming.lookup("rmi://localhost:1099/taskManager"));
			
            System.out.println("TaskManager bound\n\n");
		} catch (Exception e) {

			System.out.println("remote exception in taskManager\n\n");
			e.printStackTrace();
		}
	}
}
