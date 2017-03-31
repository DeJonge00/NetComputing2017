package rmi;

import java.io.File;
import java.io.Serializable;

public class Measurement implements Serializable {
	static final long serialVersionUID = 1234;
	int cpuamount;
	private float ram;
	private float memory;
	private float freememory;
	private double loadAvg;
	double cpuUsage;
		
	public Measurement() {
		File[] roots = File.listRoots();
		cpuamount = roots.length;
	}
	
	@Override
	public String toString() {
		return "Cpu amount: " + cpuamount + ", ram: " + this.ram + ", memory: " + this.memory + ", free memory: " + this.freememory;
	}

	public void setMemoryInfo(float r, float t, float f) {
		ram = r;
		memory = t;
		freememory = f;
	}

	public void setLoadAvg(double l) {
		loadAvg = l;
	}
	
	public void setCpuUsage(float i) {
		cpuUsage = i;
	}
}