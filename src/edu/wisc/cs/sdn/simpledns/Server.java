package edu.wisc.cs.sdn.simpledns;

import java.io.*;
import java.net.*;

import edu.wisc.cs.sdn.simpledns.packet.DNS;
import edu.wisc.cs.sdn.simpledns.packet.DNSQuestion;
import edu.wisc.cs.sdn.simpledns.packet.DNSResourceRecord;

/**
 * Server wrapper to receive client data
 * 
 * @author schwalm
 *
 */
public class Server {

	protected static final int CUSTOM_DNS_PORT = 8053;
	private static final int DNS_PORT = 53;
	private static final int BYTEBUFFER_SIZE = 1472;

	public Server() {

	}

	/**
	 * This method obtains and deserialized exactly one DNS request.
	 * This method currently blocks until a request is received.
	 * 
	 * @return A DNS object.
	 */
	public DNS processDNSRequest(DatagramSocket serverSocket) {

		byte[] buffer = new byte[BYTEBUFFER_SIZE];
		DNS dns = null;

		try {

			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(data);

			SimpleDNS.clientIp = data.getAddress();
			SimpleDNS.clientPort = data.getPort();
			
			buffer = data.getData();
			dns = DNS.deserialize(buffer, data.getLength());
			
		} catch (IOException ex) {
			System.out.println("IO Error 2 Occurred. Exiting...");
			System.exit(1);
		}
		
		return dns;
	}
	
	/**
	 * This method obtains and deserialized exactly one DNS request.
	 * This method currently blocks until a request is received.
	 * 
	 * @return A DNS object.
	 */
	public void returnDNSRequest(DatagramSocket serverSocket, DNS response) {

		try {

			DatagramPacket send = new DatagramPacket(response.serialize(), 
					response.getLength(), SimpleDNS.clientIp, SimpleDNS.clientPort);
			
			serverSocket.send(send);

		} catch (IOException ex) {
			System.out.println("IO Error 3 Occurred. Exiting...");
			System.exit(1);
		}
	}
	
	/**
	 * Forwards the specified request to the specified name server.
	 * The response from the DNS server is returned.
	 * 
	 * @param request
	 * @return The DNS response.
	 */
	protected DNS resolveDNS(DNS request, String ip, String initialDomain) {
		
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
			System.out.println("IO Error 4 Occurred. Exiting... - " + ex.getMessage());
			System.exit(1);
		}
		
		DNSResourceRecord answer = null;
		
		if (dns.getAnswers().size() > 0) {
			
			for (DNSResourceRecord potMatch : dns.getAnswers()) {
				
				 if (potMatch.getType() == DNS.TYPE_A || potMatch.getType() == DNS.TYPE_CNAME) {
					 answer = potMatch;
				 }
			}
		}
		
		if (answer == null && dns.getAdditional().size() > 0) {
			
			for (DNSResourceRecord potMatch : dns.getAdditional()) {

				if (potMatch.getType() == DNS.TYPE_A || potMatch.getType() == DNS.TYPE_CNAME) {
					answer = potMatch;
				}
			}		
		}
		
		if (answer == null) {
			return null;
		}
		
		if ( (answer.getName().equals(initialDomain) && answer.getType() != DNS.TYPE_CNAME) || 
				request.isRecursionDesired() == false) {
			
			return dns;
		}		
						
		if (answer.getType() == DNS.TYPE_CNAME) {
			
			DNS newRequest = new DNS();
			DNSQuestion newQuestion = new DNSQuestion();
			
			newRequest.setOpcode(DNS.OPCODE_STANDARD_QUERY);
			newRequest.setRecursionDesired(true);
			
			newQuestion.setClass(DNS.CLASS_IN);
			newQuestion.setName(answer.getData().toString());
			newQuestion.setType(DNS.TYPE_A);
			
			newRequest.getQuestions().add(newQuestion);
			
			dns = this.resolveDNS(newRequest, SimpleDNS.rootNs, answer.getData().toString());
		
		} else {
			
			dns = this.resolveDNS(request, answer.getData().toString(), initialDomain);
		}
		
		return dns;
	}
}