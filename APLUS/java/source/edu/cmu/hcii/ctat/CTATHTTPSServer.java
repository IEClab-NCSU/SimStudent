/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.regex.Matcher;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import edu.cmu.pact.Utilities.trace;

/**
 * 
 */
public class CTATHTTPSServer extends CTATHTTPServer {

	/** Default keystore for SSL. */
	public static final String defaultKeystore = "/etc/apache2/ssl.key/server.jks"; // "lib"+(File.separator)+"PACTkeyStore";
	
	/** Keystore file name. Default value is {@value #defaultKeystore}. */
	private String keystore = defaultKeystore;

	/**
	 * @return the {@link #keystore}
	 */
	public String getKeystore() {
		return keystore;
	}

	/**
	 * @param keystore new value for {@link #keystore}
	 */
	public void setKeystore(String keystore) {
		if(keystore != null && keystore.trim().length() > 0)
			this.keystore = keystore;
	}
	
	/** The permanent socket, where we listen for connections. */
	private SSLServerSocket serverSocket = null;

	public CTATHTTPSServer(int port, String pathToRoot, String logFileName,
			CTATHTTPHandlerInterface aHandler) {
		super(port, pathToRoot, logFileName, aHandler);
		if(trace.getDebugCode("http"))
			trace.out("http", String.format("CTATHTTPSServer.<init>(%d, %s, %s, %s)",
					port, pathToRoot, logFileName, trace.nh(aHandler)));
	}

	/**
	 * If instantiated with a socket, call {@link CTATHTTPServer#run() super.run()}.
	 * Else create SSLServerSocket {@link #serverSocket} and block to await connections on
	 * {@link SSLServerSocket#accept()}.
	 * @see edu.cmu.hcii.ctat.CTATHTTPServer#run()
	 */
	public void run()
	{
		if(trace.getDebugCode("http"))
			trace.out("http", "CTATHTTPSServer.run() socket "+socket);
		
		if(socket == null) // listen if this thread is not handling a specific connection
		{
			char ksPass[] = "pact123".toCharArray();
			char ctPass[] = "pact123".toCharArray();  // "My1stKey" didn't take

			Thread.currentThread().setName("HTTPServer");
			try 
			{
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream(keystore), ksPass);
				KeyManagerFactory kmf = 
						KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, ctPass);
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(kmf.getKeyManagers(), null, null);
				SSLServerSocketFactory ssf = sc.getServerSocketFactory();
				serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
				if(trace.getDebugCode("http")) {
					trace.out("http", "CTATHTTPSServer.run() serverSocket "+serverSocket);
					printServerSocketInfo(serverSocket);
				}
				// accept incoming connections and serve them with new threads
				while(!nowExiting)
				{
					if(trace.getDebugCode("http"))
						trace.out("http", "CTATHTTPSServer.run() top of loop: to block on accept();"+
								" serverSocket "+serverSocket);

					socket = serverSocket.accept();
					if(socket != null)
					{
						(new Thread(new CTATHTTPServer(socket, getHandler()))).start();
					}
				}
			}
			catch (Exception uhe) 
			{
				trace.err("Failed to open HTTPSserver on localhost port "+port+", error "+uhe+
						"; cause "+uhe.getCause());
				uhe.printStackTrace(System.out);
			}
			debug("run(): nowExiting "+nowExiting+", shutdownTime "+shutdownTime);
		}
		else // there is a specific connection that this thread is to serve
			super.run();
	}
	private static void printServerSocketInfo(SSLServerSocket s) {
		trace.out("Server socket class: "+s.getClass());
		trace.out("   Socker address = "
				+s.getInetAddress().toString());
		trace.out("   Socker port = "
				+s.getLocalPort());
		trace.out("   Need client authentication = "
				+s.getNeedClientAuth());
		trace.out("   Want client authentication = "
				+s.getWantClientAuth());
		trace.out("   Use client mode = "
				+s.getUseClientMode());
	} 

}
