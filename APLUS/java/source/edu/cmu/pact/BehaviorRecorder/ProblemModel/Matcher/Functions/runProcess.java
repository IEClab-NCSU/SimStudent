package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import edu.cmu.pact.Utilities.trace;

/**
 * Experimental class for running an external process.
 */
public class runProcess {
	
	private String processToRun;
	private StringWriter stderr = new StringWriter();
	private StringWriter stdout = new StringWriter();
	private InputStream stdin;
	private Process proc;
	private int result = -1;

	/**
	 * <p>Run {@link #processToRun} with this argument as stdin.</p>
	 * @param processName filename of process to run
	 * @param procInput stdin for the process.
	 * @return stdout from the process
	 */
	public String runProcess(String processName, String procInput) {
		ByteArrayInputStream bais = new ByteArrayInputStream(procInput.getBytes());
		return runProcess(processName, bais);
	}


	/**
	 * <p>Run {@link #processToRun} with this stream as stdin.</p>
	 * @param processName filename of process to run
	 * @param procInput stdin for the process.
	 * @return stdout from the process
	 */
	public String runProcess(String processName, InputStream procInput) {

		class grabOutput extends Thread {
			InputStream is;
			String streamName;
			Writer result;
			grabOutput(InputStream is, String streamName, Writer result) {
				super();
				this.is = is; this.streamName = streamName; this.result = result;
			}
			public void run() {
				BufferedReader procOutput = new BufferedReader(new InputStreamReader(is));
				try {
					for (int c = -1; (c = procOutput.read()) != -1; )
						result.append((char) c);
				} catch (IOException ioe) {
					trace.err("Error reading "+processToRun+" "+streamName+": "+ioe+
							(ioe.getCause() == null ? "." : ";\n cause: "+ioe.getCause()));
				}
				synchronized(this) {
					streamName = "done";
					notifyAll();
				}
			}
		};
		processToRun = processName;
		stdin = procInput;
		stdout = new StringWriter();
		stderr = new StringWriter();

		try {
			proc = Runtime.getRuntime().exec(processToRun);
		} catch (IOException ioe) {
			trace.err("Error executing "+processToRun+": "+ioe+
					(ioe.getCause() == null ? "." : ";\n cause: "+ioe.getCause()));
			return "";
		}
		
		grabOutput outThread = new grabOutput(proc.getInputStream(), "stdout", stdout);
		grabOutput errThread = new grabOutput(proc.getErrorStream(), "stderr", stderr);
		outThread.start();
		errThread.start();
		BufferedWriter ow = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
		try {
			for (int c = -1; (c = stdin.read()) != -1; )
				ow.write(c);
		} catch (IOException ioe) {
			trace.err("Error writing "+processToRun+" stdin: "+ioe+
					(ioe.getCause() == null ? "." : ";\n cause: "+ioe.getCause()));
		} finally {
			try { ow.close(); }
			catch (IOException ioe) { trace.err("Error closing "+processToRun+" stdin: "+ioe+
					(ioe.getCause() == null ? "." : ";\n cause: "+ioe.getCause())); }
		}
			
		while (true) {
			try {
				result = proc.waitFor();
				break;
			} catch (InterruptedException ie) {
				trace.err("Exception while waiting on "+processToRun+": "+ie);
			}
		}
		synchronized(outThread) {
			while (!"done".equalsIgnoreCase(outThread.streamName)) {
				try { outThread.wait(); } catch (InterruptedException ie) { trace.err("outThread interrupted: "+ie); }
			}
		}
		synchronized(errThread) {
			while (!"done".equalsIgnoreCase(errThread.streamName)) {
				try { errThread.wait(); } catch (InterruptedException ie) { trace.err("errThread interrupted: "+ie); }
			}
		}
		return stdout.toString();
	}
	
	/**
	 * Test harness. Must specify value for {@link #processToRun}.
	 * @param args first arg is {@link #processToRun}; if no 2nd arg, read stdin; else pass 2nd arg as stdin
	 */
	public static void main(String[] args) {
		String processToRun = args[0];
		runProcess rp = new runProcess();
		if (args.length > 1) {
			String procOutput = rp.runProcess(processToRun, args[1]);
			trace.out("Process "+processToRun+"("+args[1]+"):\n output: "+procOutput
					+"\n error: "+rp.stderr);
		} else {
			String procOutput = rp.runProcess(processToRun, System.in);
			trace.out("Process "+processToRun+"( - ):\n output: "+procOutput
					+"\n error: "+rp.stderr);
		}
	}
}
