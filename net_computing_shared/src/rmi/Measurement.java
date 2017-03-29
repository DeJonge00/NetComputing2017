package rmi;

import java.io.Serializable;

public class Measurement implements Serializable {
	static final long serialVersionUID = 1234;
	long ram;
	long memory;
	long freememory;
	
	public Measurement(long l, long m, long n) {
		this.ram = l;
		this.memory = m;
		this.freememory = n;
	}
	
	@Override
	public String toString() {
		return String.format("Ram: %d, memory: %d, free memory: %d", this.ram, this.memory, this.freememory);
	}
}