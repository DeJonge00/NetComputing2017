package rmi;

import java.io.Serializable;
import java.net.InetAddress;

public class InitData implements Serializable {
	private static final long serialVersionUID = 1L;
	public int coreAmount;
	public InetAddress address;
	public int platform;
	
	public InitData(int coreAmount, InetAddress adress, String platform) {
		this.coreAmount = coreAmount;
		address = adress;
		
		if(platform.toLowerCase().contains("windows")) {
			this.platform = 0;
		} else if(platform.toLowerCase().contains("linux")) {
			this.platform = 1;
		} else {
			this.platform = -1;
		}
	}
}
