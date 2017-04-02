package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskServer extends Remote {
	public int execute(String t, String input) throws RemoteException;

	public void interrupt(int pid) throws RemoteException;
	
	public String getOutput(int pid) throws RemoteException;
}
