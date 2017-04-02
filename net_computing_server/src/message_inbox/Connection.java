package message_inbox;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import rmi.InitData;
import rmi.Measurement;

public class Connection {
	private Socket socket;
	private InitData data;
	private Measurement last_measurement;

	public Connection(Socket s) {
		this.socket = s;
		this.last_measurement = new Measurement();
	}
	
	public synchronized void setLastMeasurement(Measurement m) {
		this.last_measurement = m;
	}
	
	public synchronized double getLoadInfo() {
		double memoryLoad, cpuLoad, load;
		memoryLoad = (last_measurement.getMemory()-last_measurement.getFreememory())/last_measurement.getMemory();
		cpuLoad = last_measurement.getCpuUsage() / (double)last_measurement.getCpuamount();
		if(memoryLoad < 0.9 && cpuLoad < 0.9) {
			return ((memoryLoad + cpuLoad)/2);
		} else {
			return memoryLoad > cpuLoad ? (1.0-memoryLoad) : (1.0-cpuLoad);
		}
	}
	
	public InitData getData() {
		return data;
	}

	public void setData(InitData data) {
		this.data = data;
		System.out.println(data.platform);
	}
	
	public int getPort() {
		return this.socket.getPort();
	}
	
	public InetAddress getInetAddress() {
		return this.socket.getInetAddress();
	}
	
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}
	
	@Override
	public boolean equals(Object other) {
		Connection c;
		if(other instanceof Connection) {
			c = (Connection)other;
			if(c.getInetAddress() == this.getInetAddress() &&
					c.getPort() == this.getPort()) {
				return true;
			}
		}
		return false;
	}
}
