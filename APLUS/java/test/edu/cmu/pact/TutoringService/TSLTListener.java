/*Listens for a connection from Flash (see tsltloadtest.as in authoringtools/FlashComponents...
 *Extracts filename out of whatever flash sends it and writes the rest to that file.
 *Takes 0 or 1 arguments for port. If 0, port defaults to 1515.
 */

package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cmu.pact.Utilities.SocketReader;
import edu.cmu.pact.Utilities.trace;

class TSLTListener {
	private static String usage = "TSLTListener accepts 0 or 1 arguments"+
							"0 arguments-> Listener opens on port 1515"+
							"1 arguments-> Listener opens on port specified in first argument";
	class CommunicationThread extends Thread
	{
		private final int commPort;
		private class CommThreadConsumer extends Thread
		{
			Socket cSock;
			
			private String mutex = "";
			
			CommThreadConsumer (Socket sock)
			{
				this.cSock = sock;
			}
			
			public void run()
			{
				try {
					trace.out("tslt", "Entering conthreadconsumer run");
					BufferedReader br =
						new BufferedReader(new InputStreamReader(cSock.getInputStream(), "UTF-8"));
					synchronized(mutex)
					{
						FileWriter fw = null;
						String result = SocketReader.readToEom(br, '\0');
						String msgs[] = result.split("\n");
						trace.out("tslt", "Read " + result.length() + " characters ");
						for (int i = 0; i < msgs.length; i++)
						{
							String msg = msgs[i];
							if (fw == null)
							{
								String fName = msg.split("#")[0];
								trace.out("tslt", "msgs.length = " + msgs.length);
								fw = new FileWriter(fName + ".test");
								trace.out("tslt", "Filename : " + fName + ".test");
								if (msg.split("#").length > 1)
									fw.write(msg.split("#")[1] +"\n");							
							}
							else
							{
								fw.write(msg+"\n");
							}
						}
						if (fw != null)	fw.close();
						trace.out("tslt", "File Written");
					}											
					br.close();
					cSock.close();
				} catch (IOException ioe) {
					System.out.println("Exception in reading/writing (TSLT listener)");
					ioe.printStackTrace();} 
				
			}
		}
		
		CommunicationThread(int commPort)
		{
			this.commPort = commPort;
		}
		
		public void run()
		{
			try {
				ServerSocket commSocket = new ServerSocket(commPort);
				while (true)
				{
					trace.out("tslt", "TSLT Listener : Listening");
					Socket cs = commSocket.accept();
					trace.out("tslt", "TSLT listener Got Connection");
					CommThreadConsumer ctc = new CommThreadConsumer(cs);
					ctc.start();
				}
			} catch (IOException e) {e.printStackTrace();}
		}
	}
 	
 	public TSLTListener(int port)
 	{	
 		CommunicationThread cthread = new CommunicationThread(port);
 		cthread.start(); 		
 	}
 	
 	public static void main (String args[])
 	{
 		int port;
 		if(args.length > 1){
 			System.out.println(usage);
 		}
 		if(args.length == 1){
 			port = Integer.parseInt(args[0]);
 		}else{
 			port = 1515;
 		}
 		TSLTListener tslt = new TSLTListener(port);
 	}
}
