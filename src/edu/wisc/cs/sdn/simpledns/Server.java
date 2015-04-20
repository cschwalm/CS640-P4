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

	private static final int CUSTOM_DNS_PORT = 8053;
	private static final int DNS_PORT = 53;
	private static final int BYTEBUFFER_SIZE = 28;

	public Server() {

	}

	/**
	 * This method obtains and deserialized exactly one DNS request.
	 * This method currently blocks until a request is received.
	 * 
	 * @return A DNS object.
	 */
	public DNS processDNSRequest() {

		byte[] buffer = new byte[BYTEBUFFER_SIZE];
		DNS dns = null;

		try {

			DatagramSocket serverSocket = new DatagramSocket(CUSTOM_DNS_PORT);

			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(data);
			buffer = data.getData();
			dns = DNS.deserialize(buffer, data.getLength());
			
			serverSocket.close();

		} catch (IOException ex) {
			System.out.println("IO Error Occurred. Exiting...");
			System.exit(1);
		}
		
		return dns;
	}
	
	/**
	 * Forwards the specified request to the specified name server.
	 * The response from the DNS server is returned.
	 * 
	 * @param request
	 * @return The DNS response.
	 */
	protected DNS forwardRequest(DNS request, String ip) {
		
		byte[] buffer = new byte[BYTEBUFFER_SIZE];
		DNS dns = null;
		
		try {
			
			InetAddress inet = InetAddress.getByName(ip);

			/* Send Request */
			DatagramSocket serverSocket = new DatagramSocket();
			DatagramPacket send = new DatagramPacket(request.serialize(), 
					request.getLength(), inet, DNS_PORT);
			
			serverSocket.send(send);
			
			/* Receive Response */
			DatagramPacket receive = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(receive);
			buffer = receive.getData();
			dns = DNS.deserialize(buffer, receive.getLength());
			
			serverSocket.close();
			
		} catch (IOException ex) {
			System.out.println("IO Error Occurred. Exiting...");
			System.exit(1);
		}
		
		return dns;
	}
}