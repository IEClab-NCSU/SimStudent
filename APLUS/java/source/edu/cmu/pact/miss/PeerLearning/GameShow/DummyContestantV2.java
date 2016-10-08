package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.GameShow.ContestExecution.Solution;
import edu.cmu.pact.miss.PeerLearning.GameShow.Contestant.ListenerThread;

public class DummyContestantV2 extends Contestant {

	//static String prodRules = "productionRules-Dummy.pr";
	//static String name = "Dummy";
	//static String img = "SimStudentLuckyImage.png";
	
	public DummyContestantV2(BR_Controller brController,
			GameShowPlatform gameShowPlatform) {

    	//super(prodRules, "Dummy", img);
		super(brController,gameShowPlatform);
    	/*
    	name = "Dummy";
		this.gameShowPlatform = gameShowPlatform;
    	
    	gameShowPlatform.setName(name);
    	gameShowPlatform.setImage(img);
    	
    	setBrController(brController);
        getBrController().activateMissController(false);
        
        setMissController(getBrController().getMissController());
                    
        SimSt simSt = getMissController().getSimSt();
        setSimSt(simSt);
        
        // ssDontShowAllRaWhenTutored
        getSimSt().setDontShowAllRA(true);

    	execution = new ContestExecution(getSimSt(), prodRules);*/
    	
	}
	
	@Override
	public void init(String prodRules)
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
        listener = new DummyListenerThread(connection);
		new Thread(listener).start();
		
		out.println(ContestServer.JOIN+","+nameImg+","+getSimSt().getUserID()+","+getBrController().getLogger().getClassName()+",static");
			
		
	}
	class DummyListenerThread extends Contestant.ListenerThread
    {
		//boolean activeListener = true;
		//Connection connection;
		DummyListenerThread(Connection connect)
		{
			super(connect);
		}
    	
    	
    	protected void contestAgreed(String incomingMsg) {}
		protected void contestRequested(String incomingMsg) {
			String[] args = incomingMsg.split(",");
			connection.writer.println(ContestServer.AGREE_CONTEST+","+args[1]+",true");
			
		}

		/*protected void listParticipants(String list){
			if(list.indexOf(',') >= 0)
			{
				list = list.substring(list.indexOf(',')+1);
	    		String[] entries = list.split(",");

	    		for(int i=0;i<entries.length;i++)
	    		{
	    			String[] details = entries[i].split(";");
	    			Competitor competitor;
	    			if(competitors.containsKey(details[0]))
	    				competitor = competitors.get(details[0]);
	    			else
	    				competitor = new Competitor(details[0],details[1],"");
	    			competitor.rating = Integer.parseInt(details[2]);
	    			competitor.wins = Integer.parseInt(details[3]);
	    			competitor.losses = Integer.parseInt(details[4]);
	    			competitor.ties = Integer.parseInt(details[5]);
	    			competitors.put(competitor.name, competitor);

	    		}

			}
		}*/
		  

		protected void endContest(String incomingMsg) {
			messageSent = false;
			gameShowPlatform.viewMatchup();
			connection.writer.println(ContestServer.JOIN+","+nameImg+","+getBrController().getLogger().getClassName()+","+getSimSt().getUserID()+",static");
			
		}

		protected void forfeitContest(String incomingMsg) {
			messageSent = false;
			gameShowPlatform.viewMatchup();
			connection.writer.println(ContestServer.JOIN+","+nameImg+","+getSimSt().getUserID()+","+getBrController().getLogger().getClassName()+",static");
			
		}
		
    	
    }

	boolean messageSent = false;
	
	@Override
	public void addPrivateChatMessage(String incomingMsg) {
		String chat = incomingMsg.substring(incomingMsg.indexOf(',')+1);
		gameShowPlatform.addPrivateChatText(chat);
		if(!chat.startsWith(name) && !messageSent)
		{
			messageSent = true;
			out.println(ContestServer.CHAT_PRIVATE_MESSAGE+","+"I am not a real person.");
		}
	}
	
	
	@Override
    public void requestProblem(String incomingMsg) {
    	/*String problem = ""+((int)(Math.random()*20)-10)+"x";
    	if(Math.random()<.5)
    		problem += "+"+((int)(Math.random()*20)-10);
    	problem += "="+((int)(Math.random()*20)-10);
    	problem = problem.replaceAll("0","1");
    	problem = problem.replaceAll("\\+-","-");*/
		String problem = GameShowUtilities.generate();
		out.println(ContestServer.SUBMIT_PROBLEMS+","+problem);
		
	}

}
