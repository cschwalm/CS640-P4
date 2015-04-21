package edu.wisc.cs.sdn.simpledns;

public class EC2Entry 
{
	public String IP;
	public String size;
	public String location;
	
	public EC2Entry(String ip, String size, String loc)
	{
		this.IP = ip;
		this.size = size;
		this.location = loc;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
