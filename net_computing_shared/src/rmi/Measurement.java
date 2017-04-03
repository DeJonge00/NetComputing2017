package rmi;

import java.io.Serializable;

public class Measurement implements Serializable {
	static final long serialVersionUID = 1234;
	private int cpuAmount;
	private float ram;
	private float memory;
	private float freeMemory;
	private double loadAvg;
	private double cpuUsage;
		
	public Measurement() {
		cpuAmount = 0;
		ram = 0;
		memory = 0;
		freeMemory = 0;
		loadAvg = 0;
		cpuUsage = 0;
	}
	
	@Override
	public String toString() {
		return "Cpu amount: " + cpuAmount 
				+ ", ram: " + this.ram 
				+ ", memory: " + this.memory 
				+ ", free memory: " + this.freeMemory 
				+ ", loadAvg: " + this.loadAvg
				+ ", cpu usage: " + this.cpuUsage;
	}

	public void setMemoryInfo(float ram, float memory, float freeMemory) {
		this.ram = ram;
		this.memory = memory;
		this.freeMemory = freeMemory;
	}

	public int getCpuamount() {
		return cpuAmount;
	}

	public void setCpuamount(int cpuAmount) {
		this.cpuAmount = cpuAmount;
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

	public float getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(float freeMemory) {
		this.freeMemory = freeMemory;
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