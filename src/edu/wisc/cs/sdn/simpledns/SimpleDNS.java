package edu.wisc.cs.sdn.simpledns;

public class SimpleDNS 
{
	static Server server;
	public static void main(String[] args)
	{
		System.out.println("started server");
		server = new Server();
		server.processClientData();
	}
}
