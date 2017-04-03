package message_inbox;
import java.util.ArrayList;

public class ConnectionList extends ArrayList<Connection>{
	synchronized public void addConnection(Connection connection) {
		int i = 0;
		int size = size();
		double load = connection.getLoadInfo();
		
		// find the position at which the connection should be inserted
		if(size > 0) {
			Connection c = this.get(i);
			while (i < size && c.getLoadInfo() > load) {
				i++;
				if(i < size) {
					// make sure we're not going out of bounds
					c = this.get(i);
				}
			}
		}
		
		if(i < size) {
			// if it is in the middle of the list, insert it at index i
			this.add(i, connection);
		} else {
			// otherwise append it to the end
			this.add(connection);
		}
	}
	
	// return the first connection
	synchronized public Connection getFirst() {
		int i = 0;
		while(!this.get(i).isActive() && i < this.size());
		if(i<this.size()) return this.get(i);
		// No active connections in the connection list
		return null;
	}
	
	// update the position of the given connection if it exists,
	// otherwise it simply inserts the connection
	synchronized public void update(Connection conn) {
		this.remove(conn);
		addConnection(conn);
		System.out.println(conn.getLoadInfo());
	}
}
