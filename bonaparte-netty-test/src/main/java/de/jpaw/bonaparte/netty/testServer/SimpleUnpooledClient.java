package de.jpaw.bonaparte.netty.testServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;

public class SimpleUnpooledClient {

	private InetAddress addr;
	private Socket conn;

	public SimpleUnpooledClient(String hostname, int port) throws IOException {
		addr = InetAddress.getByName("localhost");
		conn = new Socket(addr, port);
	}
	
	public BonaPortable doIO(BonaPortable request) throws Exception {
		ByteArrayComposer w = new ByteArrayComposer();
		w.writeRecord(request);
		conn.getOutputStream().write(w.getBuffer(), 0, w.getLength());
		byte [] response = new byte [1000];
		int numbytes = conn.getInputStream().read(response);
		if (numbytes <= 0)
			return null;
		ByteArrayParser p = new ByteArrayParser(response, 0, numbytes);
		return p.readRecord();
	}
	
}
