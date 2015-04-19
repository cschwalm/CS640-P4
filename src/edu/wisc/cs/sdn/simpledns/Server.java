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

		byte[] buffer = new byte[BYTEBUFFER_SIZE];
		DNS dns = null;

		try {

			DatagramSocket serverSocket = new DatagramSocket(DNS_PORT);

			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(data);
			System.out.println("got client data");

			buffer = data.getData();
			System.out.println("reading data");

			dns = DNS.deserialize(buffer, data.getLength());
			System.out.println(dns);
			
			serverSocket.close();

		} catch (IOException ex) {
			System.out.println("IO Error Occurred. Exiting...");
			System.exit(1);
		}
	}
}