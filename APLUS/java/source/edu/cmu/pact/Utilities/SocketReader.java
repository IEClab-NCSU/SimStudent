package edu.cmu.pact.Utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;


public class SocketReader {

	/** If a message is longer than this, it will be truncated or discarded. */
	private static final int MAX_MSG_LEN = 3*1024*1024;
	
	/**Read the characters from a Reader into a String. Reads until receives the
	 * given end-of-message character, which it consumes without returning.
	 * 
	 * @param rdr
	 *            Reader to read; should be BufferedReader or equivalent for
	 *            efficiency
	 * @param eom
	 *            end-of-message character; not returned with String result
	 * @return String with all characters from Reader
	 * @exception IOException
	 */
	public static String readToEom(Reader rdr, int eom) throws IOException {
		if (trace.getDebugCode("sp")) trace.out("sp", "entering readToEom...");
		StringWriter result = new StringWriter(4096);
		int c;
		int count = 0;
		while (count < MAX_MSG_LEN && (0 <= (c = rdr.read()) && c != eom)) {
			count++;
			if (c == '\r')
				if (trace.getDebugCode("sp")) trace.out("sp", "CR return is found at offset " + count);
			result.write(c);
			// trace.out("sp", result + "...");
		}
		if (trace.getDebugCode("sp"))
			trace.out("sp", "readToEom(" + eom + ") len "+count+": " + result);
		if (count >= MAX_MSG_LEN)
			trace.err("SocketReader.readToEom("+eom+") count "+count+": max buffer length reached");
	
		return result.toString();
	}
	 /*Read all the characters from a Reader into a String. Reads until receives
	 * end-of-file mark.
	 * 
	 * @param rdr
	 *            Reader to read; should be BufferedReader or equivalent for
	 *            efficiency
	 * @return String with all characters from Reader
	 * @exception IOException
	 */
	public static String readAll(Reader rdr) throws IOException {
		StringWriter result = new StringWriter(4096);
		char[] cbuf = new char[4096];
		int len, totalLen = 0;
		while (totalLen < MAX_MSG_LEN && (0 <= (len = rdr.read(cbuf, 0, cbuf.length)))) {
			result.write(cbuf, 0, len);
			totalLen += len;
		}
		return result.toString();
	}
	/**
	 * Write the given string to the output stream and flush.
	 * @param str string to send
	 * @param outstream; no-op if null
	 * @param eom if nonnegative, write this as a trailing byte
	 */
	public static void sendString(String str, PrintWriter out, int eom) {
		if (out == null)
			return;
		synchronized(out) {
			out.print(str);
			if (eom >= 0) {
				out.write(eom);
			}
			out.flush();
		}
		if (trace.getDebugCode("sp")) trace.out("sp", "SocketTP.sendString() success: sent to "+out);
	}
}
