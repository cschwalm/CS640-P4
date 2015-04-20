package edu.wisc.cs.sdn.simpledns;

import edu.wisc.cs.sdn.simpledns.packet.DNS;
import edu.wisc.cs.sdn.simpledns.packet.DNSQuestion;

/**
 * Creates a simple server that processes DNS requests
 * and responds based on the CS 640 Assignment 4 Instructions
 *
 */
public class SimpleDNS {
	
	public static Server server;
	public final static String ROOT_NS = "198.41.0.4";
	
	public static void main(String[] args) {
		
		server = new Server();
		
		while (true) {
			
			DNS dns = server.processDNSRequest();
			byte opcode = dns.getOpcode();
			if (dns.getQuestions().size() <= 0) {
				continue;
			}
			
			DNSQuestion question = dns.getQuestions().get(0);
			
			/* Skip Invalid Record Types */
			if (opcode != DNS.OPCODE_STANDARD_QUERY && 
					question.getType() != DNS.TYPE_A && question.getType() != DNS.TYPE_AAAA &&
					question.getType() != DNS.TYPE_NS && question.getType() != DNS.TYPE_CNAME) { 
				continue;
			}
			
			/* Get Response From Root NS */
			DNS nsResponse = server.forwardRequest(dns, ROOT_NS);
			System.out.println(nsResponse);
		}
	}
}