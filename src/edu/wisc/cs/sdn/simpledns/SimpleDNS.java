package edu.wisc.cs.sdn.simpledns;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
	public static String csvPath;
	public static ArrayList<EC2Entry> entries = new ArrayList<EC2Entry>();

	public static void main(String[] args) {

		server = new Server();
		if(args.length != 4)
		{
			System.out.println("usage: -r <root sever ip> -e <ec2 csv>");
			System.exit(0);
		}

		//		ROOT_NS = args[1];
		csvPath = args[3];

		// parse the CSV file
		BufferedReader br = null;
		String sep1 = "/";
		String sep2 = ",";
		String line;



		try {
			br = new BufferedReader(new FileReader(csvPath));

			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] split = line.split(sep1);
				String[] split2 = split[1].split(sep2);
				
				entries.add(new EC2Entry(split[0], split2[0], split2[1]));
			}
			br.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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