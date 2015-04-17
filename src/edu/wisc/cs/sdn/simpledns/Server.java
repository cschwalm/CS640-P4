package edu.wisc.cs.sdn.simpledns;

import java.io.*;
import java.net.*;

import edu.wisc.cs.sdn.simpledns.packet.DNS;

/**
 * Server wrapper to receive client data
 * 
 * @author schwalm
 *
 */
public class Server {
	
	private static final int DNS_PORT = 8053;
	private static final int BYTEBUFFER_SIZE = 28;
	
	
	public Server() {
		
	}
	
	public void processClientData() {
			
		int bytesRead = 0;
		Socket client;
		byte[] buffer = new byte[BYTEBUFFER_SIZE];
		DNS dns = null;
		
		try {
		
			ServerSocket soc = new ServerSocket(DNS_PORT);
			
			if ((client = soc.accept()) != null) {
				
				InputStream data = client.getInputStream();
				System.out.println("got client data");
				
				while(bytesRead != -1)
				{
					bytesRead = data.read(buffer, 0, BYTEBUFFER_SIZE);
					System.out.println("reading data");
				}
				
				dns = DNS.deserialize(buffer, bytesRead);
				System.out.println(dns);
				
				client.close();
				soc.close();		
			}		
		
		} catch (IOException ex) {
			System.out.println("IO Error Occurred. Exiting...");
			System.exit(1);
		}
	}
}