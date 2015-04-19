package edu.wisc.cs.sdn.simpledns;

import edu.wisc.cs.sdn.simpledns.packet.DNS;

/**
 * Creates a simple server that processes DNS requests
 * and responds based on the CS 640 Assignment 4 Instructions
 *
 */
public class SimpleDNS {
	
	public static Server server;
	
	public static void main(String[] args) {
		
		server = new Server();
		DNS dns = server.processDNSRequest();
	}
}