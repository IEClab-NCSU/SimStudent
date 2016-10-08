/*Network receiver is an extension of the receiver class intended for dealing with network
transmissions - i.e., across a socket or whadever else is necessary*/

package edu.cmu.old_pact.dormin;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkReceiver extends Receiver {
	protected Socket mySocket;
	protected DataInputStream dataStream;
	
	public NetworkReceiver(){};
	
	public NetworkReceiver(ExternalObject inObject,Socket inSocket){
		myObject =inObject;
		mySocket =inSocket;
		try {
			dataStream = new DataInputStream(inSocket.getInputStream());
		} catch (IOException i) { System.out.println(i.toString());};
//		System.out.println("Starting Receiver");
	}
	
	public synchronized void run() { 
		String line="";
		
		try {
			for (;;) {
				while (line != null && line.equals(""))
					line = dataStream.readLine();
				if (line == null) 
					break;
//				System.out.println("Receiver: Received **"+line+"**");
				MessageObject tmess = new MessageObject(line,new ObjectProxy("Application"));
				if (tmess.getParseError() == null) {
					synchronized (tmess) {	
						receive(tmess);
					}
				}
				else
					System.out.println("error parsing "+line+"::"+tmess.getParseError());
				line = "";
			}
		}catch(IOException e){
			System.out.println(e.toString());
		}
	}
}