package message_inbox;
import java.util.ArrayList;

public class ConnectionList {
	private ArrayList<Connection> connections;
	
	public ConnectionList() {
		this.connections = new ArrayList<Connection>();
	}
	
	synchronized public void addConnection(Connection connection) {
		int i = 0;
		int size = connections.size();
		double load = connection.getLoadInfo();
		
		// find the position at which the connection should be inserted
		if(size > 0) {
			Connection c = this.connections.get(i);
			while (i < size && c.getLoadInfo() > load) {
				i++;
				if(i < size) {
					// make sure we're not going out of bounds
					c = this.connections.get(i);
				}
			}
		}
		
		if(i < size) {
			// if it is in the middle of the list, insert it at index i
			this.connections.add(i, connection);
		} else {
			// otherwise append it to the end
			this.connections.add(connection);
		}
	}
	
	// return the first connection
	synchronized public Connection getFirst() {
		return this.connections.get(0);
	}
	
	// update the position of the given connection if it exists,
	// otherwise it simply inserts the connection
	synchronized public void update(Connection conn) {
		this.connections.remove(conn);
		addConnection(conn);
		System.out.println(conn.getLoadInfo());
	}
}
