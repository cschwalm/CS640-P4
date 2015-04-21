package edu.wisc.cs.sdn.simpledns.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DNSRdataText implements DNSRdata {
	
	private ArrayList<String> strings;
	
	public DNSRdataText() {
		
		this.strings = new ArrayList<String>();
	}
	
	public DNSRdataText(String s) {
		this.strings = new ArrayList<String>();
		strings.add(s);
	}
	
	public void add(String string) {
		strings.add(string);
	}

	@Override
	public byte[] serialize() {
		
		String newString = "";
		
		for (String s : strings) {
			newString += s;
		}
		
		byte[] data = new byte[newString.length() + 1 + (newString.length() > 0 ? 1 : 0)];
		ByteBuffer bb = ByteBuffer.wrap(data);
		
		bb.put((byte)(newString.length()));
		bb.put(newString.getBytes(StandardCharsets.US_ASCII));
		bb.put((byte)0);
		
		return data;
	}

	@Override
	public int getLength() {
		return this.serialize().length;
	}
}