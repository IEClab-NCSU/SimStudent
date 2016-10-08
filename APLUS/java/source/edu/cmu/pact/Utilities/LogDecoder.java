package edu.cmu.pact.Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * Yet another log decoder.
 */
public class LogDecoder {

	/**
	 * @param args ignored: reads stdin, writes stdout
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = rdr.readLine()) != null)
			System.out.println(URLDecoder.decode(line, "ISO-8859-1"));
	}
}
