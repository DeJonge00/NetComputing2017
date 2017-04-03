package resource_monitor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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
	private static Sigar sigar;
	ObjectOutputStream oos;
	
	public ResourceMonitor(ObjectOutputStream o) {
		running = false;
		sigar = new Sigar();
		oos = o;
	}

	public void run() {
		if(sendInitData(oos)) {
			running = true;
			while (running) {
				if (!sendMeasurement(oos, takeMeasurement())) {
					// Sending message failed
					running = false;
					System.out.println("Failed sending measurement");
				}
	
				// End of cycle
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				oos.close();
			} catch (IOException e) {
				System.out.println("Failed to close outputstream or socket");
			}
			System.out.println("Connections closed, stopping worker");
		} else {
			System.out.println("Server not found, aborting");
		}
	}

	private boolean sendInitData(ObjectOutputStream oos) {
		InitData data;
		// Create init data
		try {
			data = new InitData(sigar.getCpuPercList().length, InetAddress.getLocalHost(), System.getProperty("os.name"));
		} catch (UnknownHostException e1) {
			System.out.println("UnknownHostException in initdata");
			return false;
		} catch (SigarException e2) {
			e2.printStackTrace();
			return false;
		}
		// Send data to server
		try {
			oos.writeObject(data);
			oos.flush();
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
		// Get ram info
		try {
			mem = sigar.getMem();
			measurement.setMemoryInfo(mem.getRam(), mem.getTotal(), mem.getTotal() - mem.getActualUsed());
		} catch (SigarException se) {
			System.out.println("Aquiring of memory info failed");
		}
		
		// Get cpu info (amount of cores and usage from 0-1*cores)
		try {
			cpu = sigar.getCpuPercList();
			int total = 0;
			for(int j = 0; j<cpu.length; j++) {
				total += cpu[j].getCombined();
			}
			measurement.setCpuUsage(total);
			measurement.setCpuamount(cpu.length);
		} catch (SigarException e3) {
			System.out.println("Aquiring cpu info failed");
		}
		
		// Get load average (windows not implemented)
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
		System.out.println(measurement.toString());
		return measurement;
	}

	public boolean sendMeasurement(ObjectOutputStream oos, Measurement m) {
		try {
			oos.writeObject(m);
			oos.flush();
		} catch (Exception e) {
			System.out.println("Unable to make a socket connection");
			return false;
		}
		return true;
	}
	
	public void quit() {
		this.running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
}
