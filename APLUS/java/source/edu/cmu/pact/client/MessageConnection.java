/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Facility to send and receive messages through a socket. Here, a
 * message is simply a String.
  */
public class MessageConnection {

	/** Default port number to send to. */
	public static final int DEFAULT_PORT = 1502;

	/** If nonnegative, listener should expect this character as an
		end-of-message delimiter. */
	private int eom = 0;

	/** Socket for reading data from server. */
	private Socket sock = null;

	/** Stream for writing data to server. */
	private PrintWriter outStream = null;

	/** Stream for reading data from server. */	
	private BufferedReader inStream = null;

	/** Thread for reading messages from socket. */
	private Thread socketReader = null;
	
	/** Thread for delivering messages to {@link #listeners} and processing them. */
	private Thread consumer = null;

	/** Flag set by other thread to tell listener to quit. */
	private boolean stopping = false;
	
	/** Queue of messages read from {@link #sock}. */
	private LinkedList<MessageEvent> msgQueue = new LinkedList<MessageEvent>();
	
	/** Mutex lock on the msgQueue */
	private Object[] msgQueueLock = new Object[0];
	
	/** List of data receivers. */
	private Set<MessageEventListener> listeners =
		Collections.synchronizedSet(new LinkedHashSet<MessageEventListener>()); 

	/**
	 * Set the {@link #stopping} flag and interrupt this thread.
	 */
	synchronized void stopListener() {
		stopping = true;
		if (socketReader != null)
			socketReader.interrupt();
		if (consumer != null)
			consumer.interrupt();
	}

	/**
	 * Get the {@link #stopping} flag value under mutex control.
	 */
	private synchronized boolean isStopping() {
		return stopping;
	}

	/**
	 * DNS name of host to connect to.  Default value is "localhost".
	 */
	private String host = "localhost";

	/**
	 * Listener port number.  Default value is
	 * {@link SocketProxy#DEFAULT_CLIENT_PORT}.
	 */
	//socketproxy should reference below too.. to utilities util
	private int port = Utils.DEFAULT_CLIENT_PORT;//promote to utilities to util...

	/**
	 * Read from the given {@link #sock} and queue messages accepted.
	 */
	private class SocketReaderForMessConn implements Runnable {

		/**
		 * Listen on {@link #sock}. For each connection accepted, process
		 * one message terminated by a line-feed; then close the connection.
		 */
		public void run() {
			try {
				InputStreamReader isr =
					new InputStreamReader(sock.getInputStream(), "UTF-8");
				inStream = new BufferedReader(isr);
//				trace.printStack("sp", "inSock="+sock+" inStream="+inStream);
				while (!isStopping()) {
					String msg = (eom >= 0 ? SocketReader.readToEom(inStream, eom) : 
								             SocketReader.readAll(inStream));
					MessageEvent msgEvt = new MessageEvent(this, false, msg);
					trace.out("SocketReader received a msg");
					if (msg.length() < 1)
						break;
					enqueue(msgEvt);
				}
				if (inStream != null)
					inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				socketReader = null;  // null to avoid stopListener() interrupting
				stopListener();
			}
		}
	}

	/**
	 * Class to send messages to {@link MessageConnection#listeners}.
	 */
	class MessageDeliverer implements Runnable {

		/**
		 * Wait on {@link MessageConnection#msgQueue} and fire events to
		 * listeners as received.
		 */
		public void run()
		{
			while (!isStopping()) {
				MessageEvent msgEvt = dequeue();
				if (msgEvt == null)
					continue;
				synchronized(msgQueueLock)
				{
					for (MessageEventListener listener : listeners)
						listener.messageEventOccurred(msgEvt);
				}
			}
			MessageEvent stopMsg = new MessageEvent(this, false, MessageEvent.QUIT, null);
			for (MessageEventListener listener : listeners)
				listener.messageEventOccurred(stopMsg);	
		}
	}

	/**
	 * Constructor opens socket, starts threads.
	 */
	public MessageConnection(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Open the socket and establish a TCP connection.
	 */
	public void openConnection() {
        create2wayConnection(host, port);
	}

	/**
	 * Add a listener for messages to {@link #listeners}
	 * @param listener
	 */
	public void addMessageEventListener(MessageEventListener listener) {
		if (listener != null)
			synchronized(msgQueueLock)
			{
				listeners.add(listener);
			}
	}

	/**
	 * Remove a listener from {@link #listeners}.
	 * @param listener
	 */
	public void removeMessageEventListener(MessageEventListener listener) {
		if (listener != null)
			listeners.remove(listener);
	}
	
	/**
	 * Create a socket connection. Side effects:<ul>
	 *   <li>sets {@link #outSock}</li>
	 *   <li>sets {@link #sock}</li>
	 *   <li>sets {@link #outStream}</li>
	 *   <li>sets {@link #listener} and starts new listener thread</li>
	 * </ul>
	 * @param host
	 * @param clientPort
	 * @return socket created
	 */
	private Socket create2wayConnection(String host, int clientPort) {
		try {
	        InetAddress addr = InetAddress.getLocalHost();
	        if (host != null && host.length() > 0)
	            addr = InetAddress.getByName(host);
	        outStream = null;
	        sock = new Socket(addr, clientPort);
	        outStream = new PrintWriter(sock.getOutputStream(), false);
	        consumer = new Thread(new MessageDeliverer());
	        consumer.start();
	        socketReader = new Thread(new SocketReaderForMessConn());
	        socketReader.start();
	        trace.out("Successfully connected");
	        return sock;
		} catch (Exception e) {
			trace.err("Error connecting to host "+host+", port "+clientPort+": "+e+
					(e.getCause() == null ? "" : "; cause: "+e.getCause()));
			return null;
		}
	}

	/**
	 * Remove and return the message at the head of the queue. Calls wait()
	 * until queue is nonempty.
	 * 
	 * @return message from head of queue
	 */
	private MessageEvent dequeue() {
		synchronized(msgQueue) {
			while (msgQueue.isEmpty()) {
				try {
					msgQueue.wait();
				} catch (InterruptedException ie) {
					trace.printStack("sp", "MessageConnection.dequeue isStopping() "+isStopping()+
							": "+ie);
					break;
				}
			}
			trace.out("msg dequeued");
			
			try {
				return msgQueue.removeFirst();
			} catch (NoSuchElementException nsee) {
				if (!isStopping())
					trace.err("Error dequeuing MessageEvent when not stopping: "+nsee+
							(nsee.getCause() == null ? "" : "; cause: "+nsee.getCause()));
				return null;
			}
		}
	}

	/**
	 * Enqueue a message for later processing. Calls notifyAll() after
	 * adding to wake up waiting dequeue().
	 * 
	 * @param msg message to queue
	 * @return length of the {@link #msgQueue} after adding msgEvt
	 */
	private int enqueue(MessageEvent msgEvt) {
		synchronized(msgQueue) {
			trace.out("msg enqueued");
			msgQueue.addLast(msgEvt);
			int result = msgQueue.size();
			msgQueue.notifyAll();
			return result;
		}
	}
	void unfilteredSendString(String str){
		try{
			trace.out("sp", "sendString() out="+outStream+" str=\n "+str);
		    if (outStream == null)
		        return;
		    outStream.println(str);
			if (eom >= 0)
				outStream.write(eom);
			outStream.flush();
		} catch(Exception e){
			trace.err("Error sending \""+(str.length() < 48 ? str : str.substring(0,48))+"\":\n "+
					e+(e.getCause() == null ? "" : ";\n cause: "+e.getCause()));
			e.printStackTrace();
		}
	}
	/**
	 * Send a single message the host.
	 * @param  str message to send
	 */
	public void sendString(String str) {
		String tempstr = str.toLowerCase();
		if(tempstr.contains("<selection>done</selection>")||tempstr.contains("<selection>help</selection>")||tempstr.contains("<selection>hint</selection>"))
			return;
		unfilteredSendString(str);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String host = null;
		int port = DEFAULT_PORT;
		int i = 0;
		for (; i < args.length; ++i) {
			switch(i) {
			case 0:
				host = args[i]; break;
			case 1:
				port = Integer.parseInt(args[i]); break;
			}
		}
		MessageConnection mc = new MessageConnection(host, port);
		mc.addMessageEventListener(new MessageEventListener() {
			public void messageEventOccurred(MessageEvent msgEvt) {
				trace.out(msgEvt.toString());
			}
		});
		mc.openConnection();
	}
}