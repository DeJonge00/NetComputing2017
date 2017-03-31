package resource_monitor;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;

import rmi.InitData;
import rmi.Measurement;

public class ResourceMonitor extends Thread {

	private boolean running;
	private int serverPort;
	private InetAddress serverAddress;
	private static Sigar sigar;
	Socket socket;
	
	public ResourceMonitor(InetAddress a, int p) throws UnknownHostException, IOException {
		running = false;
		this.serverAddress = a;
		this.serverPort = p;
		sigar = new Sigar();
		socket = new Socket(a.getHostAddress(), p);
	}

	public void run() {
		if(sendInitData(serverAddress, serverPort)) {
			running = true;
			while (running) {
				if (!sendMeasurement(serverAddress, serverPort, takeMeasurement())) {
					// Sending message failed
					running = false;
					System.out.println("Failed sending to server address: " + serverAddress.getHostAddress() + ", server port: " + serverPort);
				}
	
				// End of cycle
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Connections closed, stopping worker");
		} else {
			System.out.println("Server not found, aborting");
		}
	}

	private boolean sendInitData(InetAddress a, int p) {
		InitData data;
		// Create init data
		try {
			data = new InitData(File.listRoots().length, InetAddress.getLocalHost(), System.getProperty("os.name"));
		} catch (UnknownHostException e1) {
			System.out.println("UnknownHostException in initdata");
			return false;
		}
		// Send data to server
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(data);
		} catch (Exception e) {
			System.out.println("Unable to make a socket connection");
			return false;
		}
		return true;
	}

	public Measurement takeMeasurement() {
		Mem mem;
		CpuPerc[] cpu;
		Measurement measurement = new Measurement();
		try {
			mem = sigar.getMem();
			measurement.setMemoryInfo(mem.getRam(), mem.getTotal(), mem.getTotal() - mem.getUsed());
		} catch (SigarException se) {
			System.out.println("Aquiring of memory info failed");
		}
		
		try {
			cpu = sigar.getCpuPercList();
			int total = 0;
			for(int j = 0; j<cpu.length; j++) {
				total += cpu[j].getCombined();
			}
			measurement.setCpuUsage(total);
		} catch (SigarException e3) {
			System.out.println("Aquiring cpu info failed");
		}
		
		try {
			double[] loadAvg = sigar.getLoadAverage();
			double[] avg = sigar.getLoadAverage();
			loadAvg[0] = new Double(avg[0]);
			loadAvg[1] = new Double(avg[1]);
			loadAvg[2] = new Double(avg[2]);
			measurement.setLoadAvg(loadAvg[1]);
		} catch (SigarNotImplementedException e) {
			System.out.println("Load avg unimplemented");
		} catch (SigarException e) {
			System.out.println("load average sigar exception");
		}
		return measurement;
	}

	public boolean sendMeasurement(InetAddress a, int p, Measurement m) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(m);
		} catch (Exception e) {
			System.out.println("Unable to make a socket connection");
			return false;
		}
		return true;
	}
}
