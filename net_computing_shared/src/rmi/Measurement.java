package rmi;

import java.io.File;
import java.io.Serializable;

public class Measurement implements Serializable {
	static final long serialVersionUID = 1234;
	private int cpuamount;
	private float ram;
	private float memory;
	private float freememory;
	private double loadAvg;
	private double cpuUsage;
		
	public Measurement() {
		cpuamount = 0;
		ram = 0;
		memory = 0;
		freememory = 0;
		loadAvg = 0;
		cpuUsage = 0;
	}
	
	@Override
	public String toString() {
		return "Cpu amount: " + cpuamount 
				+ ", ram: " + this.ram 
				+ ", memory: " + this.memory 
				+ ", free memory: " + this.freememory 
				+ ", loadAvg: " + this.loadAvg
				+ ", cpu usage: " + this.cpuUsage;
	}

	public void setMemoryInfo(float r, float t, float f) {
		ram = r;
		memory = t;
		freememory = f;
	}

	public int getCpuamount() {
		return cpuamount;
	}

	public void setCpuamount(int cpuamount) {
		this.cpuamount = cpuamount;
	}

	public float getRam() {
		return ram;
	}

	public void setRam(float ram) {
		this.ram = ram;
	}

	public float getMemory() {
		return memory;
	}

	public void setMemory(float memory) {
		this.memory = memory;
	}

	public float getFreememory() {
		return freememory;
	}

	public void setFreememory(float freememory) {
		this.freememory = freememory;
	}

	public double getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public double getLoadAvg() {
		return loadAvg;
	}
	
	public void setLoadAvg(double loadAvg) {
		this.loadAvg = loadAvg;
	}
}