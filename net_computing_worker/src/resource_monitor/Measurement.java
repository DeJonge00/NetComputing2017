package resource_monitor;
import java.io.Serializable;

public class Measurement implements Serializable {
	static final long serialVersionUID = 1234;
	private long ram;
	private long memory;
	private long freememory;
	private double loadAvg;
	
	public Measurement() {
	}
	
	@Override
	public String toString() {
		return String.format("Ram: %d, memory: %d, free memory: %d", this.ram, this.memory, this.freememory);
	}

	public void setMemoryInfo(long r, long t, long f) {
		ram = r;
		memory = t;
		freememory = f;
	}

	public void setLoadAvg(double l) {
		loadAvg = l;
	}
}