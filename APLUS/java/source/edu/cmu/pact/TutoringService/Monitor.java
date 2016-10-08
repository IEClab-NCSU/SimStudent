package edu.cmu.pact.TutoringService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.ExitableServer;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * <p>Listens for communication requests from monitors and operators. Here is a
 * sample monitor trace: 
 * <ul>
 * <li><b>cmd:</b> &lt;service cmd="all"/&gt;<br/>
 * &lt;service&gt;
 * <ul>
 * <li>&lt;memory total="16252928" max="259522560" free="10443728" /&gt;</li>
 * <li>&lt;logging diskLogEntries="16" forwardLogEntries="16" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * <li>&lt;sessions count="2"&gt;
 * <ul>
 * <li>&lt;session guid="qa-test_810" lastMessage="2012-03-02 15:16:37.790 UTC" totalTransactionMs="2671" transactionCount="6" firstTransactionTime="2012-03-02 15:16:18.550 UTC" longestTransactionMs="2622" longestTransactionStartTime="2012-03-02 15:16:18.550 UTC" diskLogEntries="7" forwardLogEntries="7" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * <li>&lt;session guid="qa-test_811" lastMessage="2012-03-02 15:19:28.712 UTC" totalTransactionMs="1074" transactionCount="8" firstTransactionTime="2012-03-02 15:18:04.231 UTC" longestTransactionMs="803" longestTransactionStartTime="2012-03-02 15:18:04.231 UTC" diskLogEntries="9" forwardLogEntries="9" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * </ul></li>
 * <li>&lt;/sessions&gt;</li>
 * </ul></li>
 * &lt;/service&gt;</li>
 * <li><b>cmd:</b> &lt;service cmd="status"/&gt;<br/>
 * &lt;service&gt;
 * <ul>
 * <li>&lt;sessions count="2" /&gt;</li>
 * <li>&lt;memory total="16252928" max="259522560" free="10064840" /&gt;</li>
 * <li>&lt;logging diskLogEntries="16" forwardLogEntries="16" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * </ul></li>
 * &lt;/service&gt;</li>
 * <li><b>cmd:</b> &lt;service keepalive="false" cmd="detail"/&gt;<br/>
 * &lt;service&gt;
 * <ul>
 * <li>&lt;sessions count="2"&gt;
 * <ul>
 * <li>&lt;session guid="qa-test_810" lastMessage="2012-03-02 15:16:37.790 UTC" totalTransactionMs="2671" transactionCount="6" firstTransactionTime="2012-03-02 15:16:18.550 UTC" longestTransactionMs="2622" longestTransactionStartTime="2012-03-02 15:16:18.550 UTC" diskLogEntries="7" forwardLogEntries="7" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * <li>&lt;session guid="qa-test_811" lastMessage="2012-03-02 15:19:28.712 UTC" totalTransactionMs="1074" transactionCount="8" firstTransactionTime="2012-03-02 15:18:04.231 UTC" longestTransactionMs="803" longestTransactionStartTime="2012-03-02 15:18:04.231 UTC" diskLogEntries="9" forwardLogEntries="9" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * </ul></li>
 * <li>&lt;/sessions&gt;</li>
 * </ul></li>
 * &lt;/service&gt;</li>
 * <li><b>cmd:</b> &lt;session guid="qa-test_814" cmd="detail"/&gt;<br/>
 * &lt;session guid="qa-test_814"&gt;
 * <ul>
 * <li>&lt;session guid="qa-test_814" lastMessage="2012-03-02 18:23:16.446 UTC" totalTransactionMs="1587" transactionCount="6" firstTransactionTime="2012-03-02 18:22:53.655 UTC" longestTransactionMs="1576" longestTransactionStartTime="2012-03-02 18:22:53.655 UTC" diskLogEntries="7" forwardLogEntries="7" diskLogErrors="0" forwardLogErrors="0" /&gt;</li>
 * </ul></li>
 * &lt;/session&gt;</li>
 * <li><b>cmd:</b> &lt;session guid="qa-test_814" cmd="remove"/&gt;<br/>
 * &lt;session guid="qa-test_814" result="removed" /&gt;</li>
 * <li><b>cmd:</b> &lt;service cmd="shutdown"/&gt;</li>
 * &lt;service shutdownTime="Fri Mar 02 13:25:55 EST 2012" /&gt;</li>
 * </ul>
 *
 * See the subclasses of {@link Monitor.RequestHandler} for the different request types supported.</p>
 * <p>If the keepalive attribute is true, the RequestHandler will continue to listen on that socket
 * for more requests of the same type (though the cmd can vary). Otherwise, the socket is closed after
 * the single request.</p>
 */

public class Monitor extends Thread implements ExitableServer 
{	
	/** By default, listen on this port for monitoring and control. */
	public static final int MONITOR_PORT = 1503;
	
	/** Maximum time {@link #request(String)} will wait for a response, in milliseconds. */
	public static final long TIMEOUT = 60000;

	/** For formatting timestamps in responses. */
	static final DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
	
	/** For tracking the various monitors */
	public static ArrayList<Socket> monitorList = new ArrayList<Socket>();
	
	static 
	{
		dateFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	/** Message formatter. */
	static XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat()
			.setOmitDeclaration(true).setLineSeparator("\r\n").setIndent("  "));

	/** The remote host to connect to. See {@link #main(String[])}. */
	private static final String REMOTE_HOST = "127.0.0.1";

	/** Table of request handlers. */
	private HashMap<String, RequestHandler> requestHandlers = new HashMap<String, RequestHandler>();
	
	/** TCP port to listen on. */
	private int commPort = MONITOR_PORT;

	/** Top-level socket, to listen on {@link #commPort}. */
	ServerSocket commSocket = null;

	/**
	 * Create a monitor, but don't start it, yet.
	 * @param commPort TCP port to listen on
	 * @param ls server to be monitore
	 */
	public Monitor (int commPort) 
	{
		debug ("Monitor (int,LauncherServer)");
		
		this.commPort = commPort;
	}	
	/**
	 * debug method since this class doesn't extend CTATDebug
	 */
	private void debug (String aMessage)
	{
		CTATBase.debug ("Monitor",aMessage);
	}
	/**
	 * Run Monitor
	 */
	public void run()
	{
		debug ("run ()");
		
		try 
		{
			commSocket = new ServerSocket();
			commSocket.setReuseAddress(true);  // allow connections while in TIME_WAIT
			InetSocketAddress sa = new InetSocketAddress(InetAddress.getByName("0.0.0.0"),commPort);
			
			commSocket.bind(sa);
			
			while (!nowExiting)
			{
				try 
				{
					Socket cs = commSocket.accept();
					MonitorRequestConsumer ctc = new MonitorRequestConsumer(cs);
					ctc.start();
				} 
				catch (SocketException se) 
				{
					if (trace.getDebugCode("ls"))
						trace.out("ls", "run(): nowExiting "+nowExiting+"; accept() threw "+se+", cause "+se.getCause());
					se.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		} 
		catch (SocketException se) 
		{
			se.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Register a request handler to {@link #requestHandlers}. Replaces any handler
	 * with the same name.
	 * @param name element tag name of messages for this handler
	 * @param handler the handler itself
	 */
	public void addRequestHandler(String name, RequestHandler handler) {
		requestHandlers.put(name, handler);
	}

	/**
	 * A single connection for the Monitor sockets.
	 */
	private class MonitorRequestConsumer extends Thread	
	{
		/** Socket for this connection. */
		private Socket cSock;
		
		/**
		 * Constructor
		 * @param sock	=	newly accepted socket connection
		 */
		public MonitorRequestConsumer (Socket sock) 
		{
			debug ("MonitorRequestConsumer (Socket)");
			this.cSock = sock;
		}
		/**
		 * debug method since this class doesn't extend CTATDebug
		 */
		private void debug (String aMessage)
		{
			CTATBase.debug ("MonitorRequestConsumer",aMessage);
		}		
		/**
		 * Run MonitorRequestConsumer
		 */
		public void run() 
		{
			debug ("run ()");
			
			try 
			{
				boolean keepalive = handleRequest(cSock);
				if (!keepalive)
					cSock.close();
			} catch (IOException ioe) 
			{
				System.out.println(ioe.getStackTrace());
				try 
				{
					if (cSock != null) cSock.close(); 
				} catch (Exception e) {}
			} 
		}
		/**
		 * 
		 */
		private boolean handleRequest(Socket cSock) 
		{
			debug ("handleRequest ("+ cSock +")");
			
			boolean keepalive = false;       // return value: whether to close the socket
			BufferedReader in = null;
			PrintWriter out = null;
			Element root = null;
			Element resp = null;
			RequestHandler handler = null;
			
			try 
			{
				in = new BufferedReader(new InputStreamReader(cSock.getInputStream(), "UTF-8"));
				
				String request = SocketProxy.readToEom(in, 0x00);
				
				if (trace.getDebugCode("monitor"))
					trace.out("monitor", "MonitorRequestConsumer.handleRequest()>"+request+"<");

				if (SocketProxy.handlePolicyFileRequest(request, cSock))
					return false;

				out = new PrintWriter(new OutputStreamWriter(cSock.getOutputStream()));
		        SAXBuilder builder = new SAXBuilder();	
				Document doc = builder.build(new StringReader(request));
				root = doc.getRootElement();
				
				keepalive = Boolean.parseBoolean(root.getAttributeValue("keepalive"));
				handler = requestHandlers.get(root.getName());
				if (keepalive) {
					handler = handler.clone();
					monitorList.add(cSock);
				}
				if (handler == null)
					throw new IllegalArgumentException("no handler for \""+root.getName()+"\"");
				else
					resp = handler.handleRequest(root);
				
				if (trace.getDebugCode("monitor"))
					trace.out("monitor", "MonitorRequestConsumer resp |"+
							outputter.outputString(resp)+"|");
			} 
			catch (Throwable t) 
			{
				trace.errStack("Monitor: error handling request "+ (root == null ? "(null)" : root.getName()), t);
				resp = new Element("error");
				resp.setAttribute("exception", t.getClass().getName());
				resp.addContent(t.getMessage());
			}
			
			try 
			{
				debug("outputting information");
				if (keepalive) {
					for(int i=0; i<Monitor.monitorList.size();i++){
						Socket sock= Monitor.monitorList.get(i); 

						debug(sock + " is one the monitorList");

						if(sock.isConnected()){
							debug(sock +" is connected");
							PrintWriter mlOut = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
							outputter.output(resp, mlOut);
							mlOut.write('\0');
							mlOut.flush();
						}
					}
				} else {
					outputter.output(resp, out);
					out.write('\0');
					out.flush();
				}
			}
				
			catch (Throwable t) 
			{
				trace.errStack("Monitor: error writing socket to reply to request "+
						(root == null ? "(null)" : root.getName()), t);
			} 
			finally 
			{
				if (!keepalive) 
				{
					if (in != null)
						try { in.close(); } catch (IOException e) {}
						if (out != null)
							out.close();
				} 
				else 
				{
					handler.setSocketIO(cSock, in, out);
					(new Thread(handler)).start();     // let it continue to respond
				}
			}
			
			return keepalive;
		}
	}

	/**
	 * A receiver for {@link Monitor#request(String, int, long)}. 
	 * This reader is synchronized with the sender in that method.
	 */
	static class SocketReader extends Thread {
		Socket sock;
		BufferedInputStream inputStream = null;
		String response;

		/**
		 * @param sock socket to read
		 */
		SocketReader(Socket sock) {
			this.sock = sock;
		}
		
		/**
		 * Open an input stream on {@link #sock}, read until a '\0', close {@link #sock}.
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			StringBuilder sb = new StringBuilder();
			try {
				synchronized(this) {
					inputStream = new BufferedInputStream(sock.getInputStream());
					notify();
				}
				int c;
				while ((c = inputStream.read()) > 0)  // -1 is EOF; '\0' is delimiter
					sb.append((char) c);
			} catch (IOException ioe) {
				trace.errStack("Error receiving shutdown response \""+sb+"\" from socket "+
						sock, ioe);
			} finally {
				response = sb.toString();
				try { sock.close(); } catch (Exception e) {};  
			}
		}

		/**
		 * @return {@link #response} string read from socket
		 */
		String getResponse() {
			return response;
		}
		
		/**
		 * Sender can synchronize on this method to learn when the receiver is ready.
		 * @return {@link #inputStream}
		 */
		synchronized InputStream getInputStream() {
			return inputStream;
		}
	}

	/**
	 * Send the given message to port {@value Monitor#MONITOR_PORT} with no delay afterwards.
	 * @param msg message to send; this method will append an end-of-message character
	 * @return response from monitor
	 */
	public static String request(String msg)
			throws Exception  {
		return request(msg, Monitor.MONITOR_PORT, 0);
	}

	/**
	 * Send the given message to the given port and sleep for a delay afterwards.
	 * The optional sleep (in milliseconds) is to let the Monitor exit on a kill, e.g.
	 * @param msg message to send; this method will append an end-of-message character
	 * @param port monitor port
	 * @param delayAfterResponse milliseconds to sleep after response
	 * @return response from monitor
	 */
	public static String request(String msg, int port, long delayAfterResponse)
			throws Exception  {
		return request(msg, REMOTE_HOST, port, delayAfterResponse);
	}

	/**
	 * Send the given message to the given port and sleep for a delay afterwards.
	 * The optional sleep (in milliseconds) is to let the Monitor exit on a kill, e.g.
	 * @param msg message to send; this method will append an end-of-message character
	 * @param remoteHost the hostname of the target server
	 * @param port monitor port of the target server
	 * @param delayAfterResponse milliseconds to sleep after response
	 * @return response from monitor
	 * @throws Exception upon error on send
	 */
	public static String request(String msg, String remoteHost, int port, long delayAfterResponse)
			throws Exception {
		String response = null;

		try {
			Socket sock = new Socket();
			InetSocketAddress sa = new InetSocketAddress(InetAddress.getByName(remoteHost ), port);
			sock.connect(sa, 2000);  // wait max 2 sec for connection

			SocketReader socketReader = new SocketReader(sock);
			synchronized(socketReader) {
				socketReader.start();
				while (socketReader.getInputStream() == null) {
					try {socketReader.wait(1000); } catch (InterruptedException ie) { }
				}
			}

			PrintStream ps = new PrintStream(sock.getOutputStream());
			ps.print(msg);
			ps.print('\0');       // end-of-message
			ps.flush();
			if (trace.getDebugCode("monitor"))
				trace.out("monitor", "request() sent to port "+port+": "+msg);

			for (long now=System.currentTimeMillis(), then=now+TIMEOUT; now < then;) { // wait max 1 min
				try {
					socketReader.join(then-now);
					if (then <= (now = System.currentTimeMillis()))
						trace.err("Monitor.request(\""+msg+"\") timeout awaiting response "+
								TIMEOUT+" ms");
					break;
				} catch (InterruptedException ie) {
					if (trace.getDebugCode("monitor"))
						trace.out("monitor", "request() exception awaiting reader quit: "+
								ie+"; cause "+ie.getCause());
					now = System.currentTimeMillis();
				}
			}
			response = socketReader.getResponse();
			if (trace.getDebugCode("monitor"))
				trace.out("monitor", "request() delay "+delayAfterResponse+", received: "+response);
			if (delayAfterResponse > 0)
				Utils.sleep(delayAfterResponse);

		} catch (IOException ioe) {
			throw new Exception("Error trying to send shutdown message to monitor port "+port, ioe);
		}
		return response;
	}
	
	/** Set this flag to tell the server to quit. */
	private volatile boolean nowExiting = false;

	/**
	 * @return {@link #nowExiting}
	 * @see edu.cmu.hcii.ctat.ExitableServer#isExiting()
	 */
	@Override
	public synchronized boolean isExiting() {
		return nowExiting;
	}

	/**
	 * Set {@link #nowExiting} true. This instance should be running in a thread blocked
	 * on {@link ServerSocket#accept()} awaiting client connections: interrupt the
	 * thread to tell it to exit.
	 * @return previous value of {@link #nowExiting} 
	 * @see edu.cmu.hcii.ctat.ExitableServer#startExiting()
	 */
	@Override
	public synchronized boolean startExiting() {
		boolean result = nowExiting;
		nowExiting = true;
		if (trace.getDebugCode("ls"))
			trace.out("ls", "Monitor.startExiting() previous nowExiting "+result+
				", server socket to close "+commSocket);
		try {
			commSocket.close();
			commSocket = null;
		} catch (Exception e) {
			trace.errStack("LauncherServer.startExiting() error closing server socket "+e+
					";\n  cause "+e.getCause(), e);
		}
		return result;
	}

	/**
	 * Standalone program to call {@link #request(String, int, long)} with a request
	 * from the command line arguments.
	 * @param args see {@link #usageExit(String)}
	 */
	public static void main(String[] args) {
		String remoteHost = REMOTE_HOST;
		int port = MONITOR_PORT;
		long timeToWait = 0;      
		int i = 0;
		
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			char option = args[i].charAt(1);
			switch(option) {
			case 'h': case 'H':
				try {
					if (++i >= args.length)
						throw new IllegalArgumentException("missing host name");
					InetAddress.getByName(args[i]);  // try to resolve
					remoteHost = args[i];
				} catch (Exception e) {
					usageExit("Error on -h option: "+(e.getMessage() == null ? "bad hostname" : e.getMessage())+".");
				}
				break;
			case 'p': case 'P':
				try {
					if (++i >= args.length)
						throw new IllegalArgumentException("missing port number");
					port = Integer.parseInt(args[i]);
				} catch (Exception e) {
					usageExit("Error on -p option: "+(e.getMessage() == null ? "bad number" : e.getMessage())+".");
				}
				break;
			case 't': case 'T':
				try {
					if (++i >= args.length)
						throw new IllegalArgumentException("missing time");
					timeToWait = Long.parseLong(args[i]);
				} catch (Exception e) {
					usageExit("Error on -t option: "+(e.getMessage() == null ? "bad number" : e.getMessage())+".");
				}
				break;
			default:
				usageExit("Unknown option -"+option+".");
			}
		}
		if (i >= args.length)
			usageExit("Missing request (e.g. \"<service cmd=\"shutdown\"/>\").");
		String req = args[i];
		try {
			String resp = request(args[i], remoteHost, port, timeToWait);
			System.out.printf("%8s: %s\n%8s: %s\n%8s: %s\n", "sent", req, "received", resp, "time", new Date());
			System.exit(0);   // good return code
		} catch (Exception e) {
			trace.errStack(e.getMessage()+"; cause: "+e.getCause(), e);
			System.exit(1);   // bad return code
		}
	}
	
	/**
	 * Print an error message and a usage guide and call {@link System#exit(int)}.
	 * @param errMsg error message
	 * @return never returns; calls {@link System#exit(int) System.exit(2)}
	 */
	private static int usageExit(String errMsg) {
		String clsName = new Monitor(-1).getClass().getName();
		if (errMsg != null)
			System.out.printf("%s ", errMsg);
		System.out.println("Usage:\n"+
				"  java -cp ... "+clsName+" [-h host] [-p port] [-t timeToWait] request\n"+
				"where--\n"+
				"  host       is the Tutoring Service host to connect to (default "+REMOTE_HOST+");\n"+
				"  port       is the port to send to (default "+MONITOR_PORT+");\n"+
				"  timeToWait is the delay to wait after the response, in ms (default "+0+");\n"+
				"  request    is the XML request to send (e.g. \"<service cmd=\"shutdown\"/>\").\n");
		System.exit(2);
		return 2;
	}
} // end Monitor
