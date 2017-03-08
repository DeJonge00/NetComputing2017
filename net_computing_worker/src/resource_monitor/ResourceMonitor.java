package resource_monitor;

import java.io.File;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;

import resource_monitor.Measurement;

public class ResourceMonitor extends Thread {

	private boolean running;
	private int serverPort;
	private InetAddress serverAddress;
	private static Sigar sigar;
	
	public ResourceMonitor(InetAddress a, int p) {
		running = false;
		this.serverAddress = a;
		this.serverPort = p;
		sigar = new Sigar();
	}

	public void run() {
		running = true;
		sendInitData(serverAddress, serverPort);
		while (running) {
			if (!sendMeasurement(serverAddress, serverPort, takeMeasurement())) {
				// Sending message failed
				running = false;
				System.out.println("Server address: " + serverAddress.getHostAddress() + ", server port: " + serverPort);
			}

			// End of cycle
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Connections closed, stopping worker");
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
			for(int j = 0; j<cpu.length; j++) {
				System.out.println(j + " user: " + cpu[j].getUser());
				System.out.println(j + " All: " + cpu[j].getCombined() + "\n");
			}
		} catch (SigarException e3) {
			System.out.println("Aquiring cpu info failed");
		}

		double uptime;
		try {
			uptime = sigar.getUptime().getUptime();
			System.out.println("Uptime: " + uptime);
		} catch (SigarException e2) {
			System.out.println("Sigar.getUptime() failed");
		}
		
		try {
			double[] loadAvg = sigar.getLoadAverage();
			double[] avg = sigar.getLoadAverage();
			loadAvg[0] = new Double(avg[0]);
			loadAvg[1] = new Double(avg[1]);
			loadAvg[2] = new Double(avg[2]);
			System.out.println("load average(1 min):" + loadAvg[0]);
			System.out.println("load average(5 min): " + loadAvg[1]);
			System.out.println("load average:(15 min): " + loadAvg[2]);
			measurement.setLoadAvg(loadAvg[1]);
		} catch (SigarNotImplementedException e) {
			System.out.println("Load avg unimplemented");
		} catch (SigarException e) {
			System.out.println("load average sigar exception");
		}
		
		try {
			System.out.println(sigar.getNetStat().getAllOutboundTotal());
		} catch (SigarException e1) {
			System.out.println("Sigar.getNetStat() failed");
		}

		return measurement;
	}

	public boolean sendMeasurement(InetAddress a, int p, Measurement m) {
		try {
			Socket s = new Socket(a.getHostAddress(), p);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			oos.writeObject(m);
			System.out.println("Sent message to server inbox");
			return true;
		} catch (Exception e) {
			System.out.println("Unable to make a socket connection");
			return false;
		}
	}
	
	public boolean sendInitData(InetAddress a, int p) {
		// CpuInfoList contains information about the clock speed, cache
		// size, model, number of cores of the CPU
		int cpuamount = 0;
		try {
			CpuInfo[] cpus;
			Cpu[] CpuInfo = sigar.getCpuList();
			File[] roots = File.listRoots();
			cpus = sigar.getCpuInfoList();
			for (File root : roots) {
				System.out.println(root.getAbsolutePath());
				System.out.println(root.getTotalSpace());
			}
			cpuamount = roots.length;
			DiskUsage disk = sigar.getDiskUsage(roots[0].getAbsolutePath());
			System.out.println(disk + "\n");
			for (int i = 0; i < cpus.length; i++) {
				System.out.println(cpus[i].toString());
				System.out.println(CpuInfo[i].toString()+ "\n");
			}
		} catch (SigarException e2) {
			System.out.println("Aquiring cpu info failed");
			return false;
		}
		
		try {
			Socket s = new Socket(a.getHostAddress(), p);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			oos.writeObject(cpuamount);
			System.out.println("Sent message to server inbox");
			return true;
		} catch (Exception e) {
			System.out.println("Unable to make a socket connection");
			return false;
		}
	}
}
