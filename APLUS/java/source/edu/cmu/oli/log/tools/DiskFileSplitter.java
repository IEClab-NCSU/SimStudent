/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.oli.log.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created from the top half of DiskImporter, splits OLI disk log by lines. Writes to stdout.
 */
public class DiskFileSplitter {

	private static boolean splitToFile = false;

	/**
	 * Print an error message and exit. Never returns.
	 * @param msg
	 */
	private static void usageExit(String msg) {
		System.out.println(msg+" Purpose: splits OLI disk log by lines. Writes to stdout or chunk files "+
				"named <inputFile>.1, <inputFile>.2, ....\n"+
				"Usage:\n"+
				"  java -cp .. "+DiskFileSplitter.class.getName()+" [-h] [-s splitLength] [inputFile]\n"+
				"where--\n"+
				"  -h          prints this message;\n"+
				"  splitLength means split the file into chunks of about this length (in messages);\n"+
				"  inputFile   OLI disk log file to read; reads stdin if omitted.");
		System.exit(1);
	}
	
	/**
	 * Splits OLI disk log by lines. Writes to stdout.
	 * @param args see {@link #usageExit(String)}
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		int i = 0;
		int splitLen = 100;
		splitToFile  = false;
		for (i = 0; i < args.length && '-' == args[i].charAt(0); ++i) {
			switch(args[i].charAt(1)) {
			case 'h': case 'H':
				usageExit("Help message."); break;
			case 's': case 'S':
				try {
					splitLen = Integer.parseInt(args[++i]); splitToFile = true; break;
				} catch (NumberFormatException nfe) {
					usageExit("Error parsing split length: "+nfe); break;
				}
			default:
				usageExit("Undefined option \"-"+args[i].charAt(1)+"\"."); break;
			}
		}

		String inFileName = null;
		Reader inFile = null;
		if (i < args.length) {
			inFileName =  args[i++];
			inFile = new FileReader(inFileName); 
		} else {
			inFileName =  "stdin";
			inFile = new InputStreamReader(System.in);
		}
		List<String> xmlDocs = new ArrayList<String>();
		StringBuffer line = new StringBuffer();
		BufferedReader in = new BufferedReader(inFile);
		try {
			int chunkLen = 512*1024;  // should be enough for any single log entry
			for (int c = -1; 0 <= (c = in.read()); c = -1) {
				line.append((char) c);
				int len = line.length();
				if (len > chunkLen && "<?xml".equals(line.substring(len-5))) {
					String chunk = line.substring(0, len-5);
					line.delete(0, len-5);
					split(xmlDocs, chunk);
					while (xmlDocs.size() >= splitLen)
						writeFile(inFileName, splitLen, xmlDocs);  // removes the records it writes
				}
			}
			split(xmlDocs, line.toString());   // add remainder
			while (xmlDocs.size() > 0)
				writeFile(inFileName, splitLen, xmlDocs);
		} catch (IOException ex) {
			System.err.println("Error reading "+(args.length > 0 ? args[0] : "stdin")+ " at line "+i);
			ex.printStackTrace();
			System.exit(2); // i/o error 
		}
		//System.out.println("FILE:\n" + toSend);
		System.out.println("\nFound "+totalRecordsWritten+" documents.");
	}

	private static int chunkNo = 0;
	private static long totalBytesWritten=0;
	private static int totalRecordsWritten = 0;

	private static void writeFile(String inFileName, int splitLen, List<String> xmlDocs) {
		String outFileName = null;
		int i = 0;
		try {
			PrintStream outStr = null; 
			if (splitToFile) {
				outFileName = inFileName+"."+Integer.toString(++chunkNo);
				outStr = new PrintStream(new FileOutputStream(outFileName));
			} else {
				outStr = System.out;				
			}
			int nToWrite = Math.min(xmlDocs.size(), splitLen);
			for(i=0; i < nToWrite; i++) {
				String record = xmlDocs.remove(0);
				byte[] bytes = record.getBytes("ISO-8859-1");
				totalRecordsWritten++;
				totalBytesWritten += bytes.length;
				if (!splitToFile)
					outStr.printf("\n%5d. %8d ", totalRecordsWritten, totalBytesWritten);
				outStr.write(bytes);
				outStr.flush();
			}
			if (splitToFile)
				outStr.close();
		} catch (IOException ex) {
			System.err.println("Error writing "+outFileName+" at record# "+i);
			ex.printStackTrace();
			System.exit(3); // i/o error 
		}
	}

	private static void split(List<String> xmlDocs, String chunk) {
		String[] chunkDocs = chunk.split("<\\?xml");
		for(int i=1; i < chunkDocs.length; i++)   // i=1: skip empty string before 1st delimiter
			xmlDocs.add("<?xml" + chunkDocs[i]);    // restore the "<?xml" delimiter on save
	}
}
