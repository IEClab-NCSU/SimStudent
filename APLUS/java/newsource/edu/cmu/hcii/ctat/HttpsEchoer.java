package edu.cmu.hcii.ctat;

/**
 * HttpsEchoer.java
 * Copyright (c) 2005 by Dr. Herong Yang
 * Source: http://www.herongyang.com/JDK/HTTPS-HttpsEchoer-Better-HTTPS-Server.html
 * Requires keystore file herong.jks, created with
 * <tt>
 * $ keytool -genkey -alias my_home -keystore herong.jks
 *   Enter keystore password: HerongJKS
 *   Re-enter new password: HerongJKS
 *   What is your first and last name? [Unknown]: Herong Yang
 *   What is the name of your organizational unit? [Unknown]: My Unit
 *   What is the name of your organization? [Unknown]: My Home
 *   What is the name of your City or Locality? [Unknown]: My City
 *   What is the name of your State or Province? [Unknown]: My State
 *   What is the two-letter country code for this unit? [Unknown]: US
 *   Is CN=Herong Yang, OU=My Unit, O=My Home, L=My City, ST=My State, C=US correct? [no]: yes
 *   Enter key password for <my_home> (RETURN if same as keystore password): My1stKey
 * </tt>
 */
import edu.cmu.pact.Utilities.trace;

import java.io.*;
import java.security.*;
import java.util.Date;
import java.util.regex.Matcher;
import javax.net.ssl.*;

public class HttpsEchoer {
	public static void main(String[] args) {
		String pkg = HttpsEchoer.class.getPackage().getName();
		trace.out(pkg);
		String ksName = pkg.replaceAll("[.]", Matcher.quoteReplacement(File.separator))+
				File.separator+"herong.jks";
		char ksPass[] = "HerongJKS".toCharArray();
		char ctPass[] = "HerongJKS".toCharArray();  // "My1stKey" didn't take
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(ksName), ksPass);
			KeyManagerFactory kmf = 
					KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ctPass);
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			SSLServerSocket s 
			= (SSLServerSocket) ssf.createServerSocket(8888);
			trace.out("Server started:");
			printServerSocketInfo(s);
			// Listening to the port
			int count = 0;
			while (true) {
				try {
					SSLSocket c = (SSLSocket) s.accept();
					// Someone is calling this server
					count++;
					trace.out("Connection #: "+count);
					printSocketInfo(c);
					BufferedWriter w = new BufferedWriter(
							new OutputStreamWriter(c.getOutputStream()));
					BufferedReader r = new BufferedReader(
							new InputStreamReader(c.getInputStream()));
					String m = r.readLine();
					if (m!=null) {
						// We have a real data connection
						w.write("HTTP/1.0 200 OK");
						w.newLine();
						w.write("Content-Type: text/html");
						w.newLine();
						w.newLine();
						w.write("<html><body><pre>");
						w.newLine();
						w.write("Connection #: "+count);
						w.newLine();
						w.newLine();
						w.write(m);
						w.newLine();
						while ((m=r.readLine())!= null) {
							if (m.length()==0) break; // End of a GET call
							w.write(m);
							w.newLine();
						}
						w.write("</pre></body></html>");
						w.newLine();
						w.flush();
					}     
					w.close();
					r.close();
					c.close();
					trace.out("Finished send at "+(new Date())+".\n\n");
				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void printSocketInfo(SSLSocket s) {
		trace.out("Socket class: "+s.getClass()+" at "+(new Date()));
		trace.out("   Remote address = "
				+s.getInetAddress().toString());
		trace.out("   Remote port = "
				+s.getPort());
		trace.out("   Local socket address = "
				+s.getLocalSocketAddress().toString());
		trace.out("   Local address = "
				+s.getLocalAddress().toString());
		trace.out("   Local port = "
				+s.getLocalPort());
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