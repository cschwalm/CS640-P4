package edu.wisc.cs.sdn.simpledns;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import edu.wisc.cs.sdn.simpledns.packet.DNS;
import edu.wisc.cs.sdn.simpledns.packet.DNSQuestion;
import edu.wisc.cs.sdn.simpledns.packet.DNSRdataText;
import edu.wisc.cs.sdn.simpledns.packet.DNSResourceRecord;

/**
 * Creates a simple server that processes DNS requests
 * and responds based on the CS 640 Assignment 4 Instructions
 *
 */
public class SimpleDNS {

	public static Server server;
	public static String rootNs; //198.41.0.4
	public static String csvPath;
	public static ArrayList<EC2Entry> entries;

	protected static InetAddress clientIp = null;
	protected static int clientPort = 0;
	
	private static EC2Entry matchingEntry;

	public static void main(String[] args) {

		server = new Server();
		entries = new ArrayList<EC2Entry>();

		if(args.length != 4) {
			System.out.println("usage: -r <root sever ip> -e <ec2 csv>");
			System.exit(0);
		}

		rootNs = args[1];
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
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {

			DatagramSocket serverSocket = null;

			try {

				serverSocket = new DatagramSocket(Server.CUSTOM_DNS_PORT);

			} catch (SocketException e) {
				System.out.println("IO Error 1 Occurred. Exiting...");
				System.exit(1);
			}

			DNS dns = server.processDNSRequest(serverSocket);

			byte opcode = dns.getOpcode();
			if (dns.getQuestions().size() <= 0) {
				continue;
			}

			DNSQuestion question = dns.getQuestions().get(0);
			String initialDomain = question.getName();

			/* Skip Invalid Record Types */
			if (opcode != DNS.OPCODE_STANDARD_QUERY && 
					question.getType() != DNS.TYPE_A && question.getType() != DNS.TYPE_AAAA &&
					question.getType() != DNS.TYPE_NS && question.getType() != DNS.TYPE_CNAME) { 
				continue;
			}

			/* Get Response From Root NS */
			DNS nsResponse = server.resolveDNS(dns, rootNs, initialDomain);

			for (int i = 0; i < nsResponse.getAnswers().size(); i++) {
				
				DNSResourceRecord answer = nsResponse.getAnswers().get(i);

				if (answer.getType() == DNS.TYPE_A && inRange(answer.getData().toString())) {
					
					EC2Entry ec2 = matchingEntry;
					DNSRdataText txt = new DNSRdataText(ec2.location + "-" + answer.getData().toString());

					DNSResourceRecord newRecord = new DNSResourceRecord();
					newRecord.setType(DNS.TYPE_TXT);
					newRecord.setTtl(3600);
					newRecord.setName(answer.getName());
					newRecord.setData(txt);

					nsResponse.addAnswer(newRecord);
				}
			}

			server.returnDNSRequest(serverSocket, nsResponse);

			serverSocket.close();
		}
	}

	private static boolean inRange(String ip)
	{
		int testIp = pack(ip);
		for(int i = 0; i < entries.size(); i++)
		{
			int base = pack(entries.get(i).IP);
			int pow = 32 - Integer.parseInt(entries.get(i).size);
			int power = (int) Math.pow(2.0, pow);
			if(testIp >= base && testIp <= (base + power))
			{
				matchingEntry = entries.get(i);
				return true;
			}

		}

		return false;
	}

	private static int pack(String addr) {
		
		String[] parts = addr.split("\\.");
		int retAddr = (int) (Integer.parseInt(parts[0]) * Math.pow(2,24) + Integer.parseInt(parts[1]) * Math.pow(2,16)
				+ Integer.parseInt(parts[2]) * Math.pow(2,8) + Integer.parseInt(parts[0]));

		return retAddr;
	}
}