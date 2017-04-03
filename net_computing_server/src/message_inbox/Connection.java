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
	private Measurement lastMeasurement;
	private boolean active;

	public Connection(Socket socket) {
		this.socket = socket;
		this.lastMeasurement = new Measurement();
		active = true;
	}
	
	public synchronized void setLastMeasurement(Measurement measurement) {
		this.lastMeasurement = measurement;
	}
	
	public synchronized double getLoadInfo() {
		double memoryLoad = (lastMeasurement.getMemory() - lastMeasurement.getFreeMemory()) / lastMeasurement.getMemory();
		double cpuLoad = lastMeasurement.getCpuUsage() / (double)lastMeasurement.getCpuamount();
		
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
		Connection connection;
		
		if(other instanceof Connection) {
			connection = (Connection)other;
			
			if(connection.getInetAddress() == this.getInetAddress()
			&& connection.getPort() == this.getPort()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void quit() {
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Unable to close connection socket");
		}
	}
}
