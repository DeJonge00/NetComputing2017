package task_distributor;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.TaskServer;

public class TaskDistributor {
	  
	public TaskDistributor() {
		try {
			System.out.println("Starting task distributor");
			System.setSecurityManager(new RMISecurityManager());
			Registry registry = LocateRegistry.getRegistry(1099);
			//System.out.println(registry.toString());
			TaskServer stub=(TaskServer)Naming.lookup("rmi://192.168.178.30:1099/taskManager");  
			System.out.println("asdfasdf");
			System.out.println("stub: " + stub.toString());
			int pid = stub.execute("gcc infinite.c -o infinity");
			int pid2 = stub.execute("ls");
			//int pid3 = stub.execute("cat ~/jeroen/remco/net_computing_server/src/resource_monitor/TaskDistributor.java");
			Thread.sleep(200);
			//stub.interrupt(pid);
			//System.out.println(stub.getOutput(pid));
			System.out.println(stub.getOutput(pid2));
			//Thread.sleep(50000);
		}catch(Exception e){
			e.printStackTrace();
		}   
	}  
}
