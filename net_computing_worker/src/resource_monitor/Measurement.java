package resource_monitor;
import java.io.File;
import java.io.Serializable;

public class Measurement implements Serializable {
	static final long serialVersionUID = 1234;
	int cpuamount;
	private long ram;
	private long memory;
	private long freememory;
	private double loadAvg;
	double cpuUsage;
		
	public Measurement() {
		File[] roots = File.listRoots();
		cpuamount = roots.length;
		System.out.println("xd");
	}
	
	@Override
	public String toString() {
		return "Ram: " + this.ram + ", memory: " + this.memory + ", free memory: " + this.freememory;
	}

	public void setMemoryInfo(long r, long t, long f) {
		ram = r;
		memory = t;
		freememory = f;
	}

	public void setLoadAvg(double l) {
		loadAvg = l;
	}
	
	public void setCpuUsage(double i) {
		cpuUsage = i;
	}
}