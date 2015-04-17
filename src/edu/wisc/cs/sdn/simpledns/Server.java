package edu.wisc.cs.sdn.simpledns;

import java.io.*;
import java.net.*;

/**
 * Server wrapper to receive client data
 * 
 * @author schwalm
 *
 */
public class Server {
	
	private static final int DNS_PORT = 8053;
	
	public Server() {
		
	}
	
	public void processClientData() {
			
		Socket client;
		
		try {
		
			ServerSocket soc = new ServerSocket(DNS_PORT);
			
			if ((client = soc.accept()) != null) {
				
				InputStream data = client.getInputStream();
				
				
				client.close();
				soc.close();		
			}		
		
		} catch (IOException ex) {
			System.out.println("IO Error Occurred. Exiting...");
			System.exit(1);
		}
	}
}