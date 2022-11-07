package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.io.*;
import java.net.*;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.GameShow.Connection;
import edu.cmu.pact.miss.console.controller.MissController;

public class DummyContestant  {

        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
      
        ListenerThread listener;
        String nameImg;
        
        public DummyContestant() {
        
        	nameImg = "Dummy,SimStudentLuckyImage.png";
        	init();
        }   
		
		public void init()
		{
			

	        try {
	            //echoSocket = new Socket("127.0.0.1", 4444);
	        	echoSocket = new Socket("mocha.pslc.cs.cmu.edu", 4444);
	            out = new PrintWriter(echoSocket.getOutputStream(), true);
	            in = new BufferedReader(new InputStreamReader(
	                                        echoSocket.getInputStream()));
	        } catch (UnknownHostException e) {
	            System.err.println("Don't know about host.");
	            System.exit(1);
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for "
	                               + "the connection.");
	            System.exit(1);
	        }

	        Connection connection = new Connection(echoSocket,out,in);
	        listener = new ListenerThread(connection);
			new Thread(listener).start();

			out.println(ContestServer.JOIN+","+nameImg);
				

			
		}
        
        private SimSt simSt = null;
		public SimSt getSimSt() {
			return simSt;
		}

		public void setSimSt(SimSt simSt) {
			this.simSt = simSt;
		}

		private MissController missController = null;
		public MissController getMissController() {
			return missController;
		}

		public void setMissController(MissController missController) {
			this.missController = missController;
		}

		private BR_Controller brController = null;
		public BR_Controller getBrController() {
			return brController;
		}

		public void setBrController(BR_Controller brController) {
			this.brController = brController;
		}


		class ListenerThread extends Thread
	    {
			boolean activeListener = true;
			Connection connection;
			ListenerThread(Connection connect)
			{
				connection = connect;
			}
	    	public void run() {
	    		String incomingMsg;
				try {
					while(activeListener && (incomingMsg = connection.reader.readLine()) != null)
					{
					    trace.out("Server: " + incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.LIST))
					    	listParticipants(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.START_CONTEST))
					    	startContest(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.START_PROBLEM))
					    	startProblem(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.ASSESSED))
					    	solutionAssessed(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.END_CONTEST))
					    	endContest(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.CONTEST_REQUESTED))
					    	contestRequested(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.CONTEST_AGREED))
					    	contestAgreed(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.REQUEST_PROBLEM))
					    	requestProblem(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.LEAVE))
					    {
					    	activeListener = false;
					    	break;
					    }
					   
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
	    	}
	    	
	    	private void contestAgreed(String incomingMsg) {
				
				
			}
			private void contestRequested(String incomingMsg) {
				String[] args = incomingMsg.split(",");
				connection.writer.println(ContestServer.AGREE_CONTEST+","+args[1]+",true");
				
				
			}
			private void endContest(String incomingMsg) {
				leave();
				init();
				
			}
			private void solutionAssessed(String incomingMsg) {

			}
			
			void listParticipants(String list)
	    	{

	    	}
	    	

	    	void startContest(String msg)
	    	{
	    	}
	    	
	    	void startProblem(String msg)
	    	{
	    		try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		String[] msgParts = msg.split(",");
	    		String problem = msgParts[1];
	    		String answer = "x="+((int)(Math.random()*20)-10);
	    		boolean isCorrect = false;
	    		int nSteps = (int)(Math.random()*10);
	    		int nIncorrect = nSteps; 
	    		String path = problem+" SolutionPath goes here";
	    		connection.writer.println(ContestServer.SOLUTION+","+answer+","+isCorrect+","+nSteps+","
	    				+nIncorrect+","+path);
	    	}
	    	
	    }

		public void leave()
		{
			listener.activeListener = false;
			out.println(ContestServer.LEAVE);
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			try {
				out.close();
				echoSocket.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	    public void requestProblem(String incomingMsg) {
	    	String problem = ""+((int)(Math.random()*20)-10)+"x";
	    	if(Math.random()<.5)
	    		problem += "+"+((int)(Math.random()*20)-10);
	    	problem += "="+((int)(Math.random()*20)-10);
	    	problem = problem.replaceAll("\\+-","-");
			out.println(ContestServer.SUBMIT_PROBLEMS+","+problem);
			
		}

		public static void main(String[] args) throws IOException 
	    {

	    	new DummyContestant();
	    	

	    }

	
}