package rmi;

import java.io.Serializable;
import java.net.InetAddress;

public class InitData implements Serializable {
	private static final long serialVersionUID = 1L;
	public int coreamount;
	public InetAddress address;
	public int platform;
	
	public InitData(int ca, InetAddress a, String p) {
		coreamount = ca;
		address = a;
		if(p.toLowerCase().contains("windows")) platform = 0;
		else if(p.toLowerCase().contains("linux")) platform = 1;
		else platform = -1;
	}
}
