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
}
