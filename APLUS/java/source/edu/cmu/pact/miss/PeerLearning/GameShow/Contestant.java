package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.GameShow.Connection;
import edu.cmu.pact.miss.PeerLearning.GameShow.ContestExecution.Solution;
import edu.cmu.pact.miss.console.controller.MissController;

public class Contestant implements ActionListener, ListSelectionListener, ChangeListener {

        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        GameShowPlatform gameShowPlatform;
        ContestExecution execution;
        ListenerThread listener;
        String nameImg;
        String name;
        String opponentName;
        String opponentID;
        Hashtable<String, Competitor> competitors;
        SimStLogger logger;
        int rating = 0;
        private long startMatchupTime;
        boolean outstandingChallenge = false;
        List<String> recentChallenges = new LinkedList<String>();
        
        //The location at which to contact the server running the gameshow
        private static String contestServer =  "128.2.176.81";
        //The port at that location the server is listening to
        private static int contestPort = 4444;
        
        

        long reviewStart;
        long lastTabViewStart;
        String lastTabViewed;
        long gameShowStart;
    	long requestContestStartTime = (new Date()).getTime();
        
        /*
         * setContestServer - setter method for contest server address
         */
        public static void setContestServer(String server)
        {
        	contestServer = server;
        }
        /*
         * getContestServer - getter method for contest server address
         */
        public static String getContestServer()
        {
        	return contestServer;
        }
        /*
         * setContestPort - setter method for contest server port
         */
        public static void setContestPort(int port)
        {
        	contestPort = port;
        }
        /*
         * getContestPort - getter method for contest server port
         */
        public static int getContestPort()
        {
        	return contestPort;
        }
        
        /*
         * Action Commands for user interface objects to determine action to take
         */
        public static final String CONTEST_REQUEST_BUTTON = "ContestRequestButton";
        public static final String CONTEST_CHALLENGE_BUTTON = "ContestChallengeButton";
        public static final String REVIEW_CONTINUE_BUTTON = "ReviewContinueButton";
        public static final String INPUT_FIELD = "InputField";
        public static final String GROUP_INPUT_FIELD = "GroupInputField";
        public static final String PROBLEM_INPUT_SUBMIT = "ProblemInputSubmit";
        public static final String PROBLEM_BANK_BUTTON = "ProblemBankButton";
        public static final String LEADERBOARD_BUTTON = "LeaderboardButton";
        public static final String WINDOW_EXIT = "WindowClosed";
        
        public static final String NUM_CORRECT = "Correct Solutions: ";
		public static final String AVG_STEPS = "Average Steps to a Correct Solution: ";
		public static final String PCT_CORRECT = "Percentage of Steps Correct: ";
		public static final String TIE_MSG = "And it looks like we have<br>a tie!  No winner this round.";
        public static final String WINNER_MSG = "It looks like our winner is<br>$1.  Congratulations!";
        public static final String FORFEIT_MSG = "It looks like our winner is<br>$1, since $2 forfeited.";
        public static final String CANCEL_MSG = "$2 had to forfeit due<br>to a technical issue, which leaves<br>$1 as our default winner,<br>but there will be no penalties.";
        public static final String WIN_MORE_CORRECT = "<br>$1 completed more<br>problems correctly.";
        public static final String WIN_SHORTER = "<br>You had the same number<br>of problems correct, but<br>$1's answers were more<br>concise.";
        public static final String WIN_MORE_STEPS_CORRECT = "<br>You had the same number<br>of problems correct, but<br>$1 made less mistakes<br>overall.";
        public static final String BOTH_GOT = "That's right! You both<br>got it.";
		public static final String ONE_GOT = "$1 gets it!";
		public static final String NONE_GOT = "Oh, too bad!  No one<br>got it right this time.";
		public static final String BEGIN_MSG = "Get ready!  We'll begin<br>shortly.";
		public static final String PROBLEM_START_MSG = "<p>Alright, the next problem</p><p>is $1.  Go!";
		public static final String BLANK_ERROR = "Don't leave either side of the equation blank.";
		public static final int ALLOWABLE_RATING_DIFF = 100;
		public static final String DUPLICATE_USERID_MSG = "Your User ID has been logged on with another session.  Closing this sesion.";
		public static final String PROJECTED_SCORE_MSG = "<html>If you win your new score would be $1 and<br>if you lose your new score would be $2.";
		private final String USER_ID_REQUEST_TITLE = "User ID"; 
		private final String USER_ID_REQUEST_MSG = "Please enter your User ID:";
		private static final String TOO_RECENT_MSG = "You have already challenged that person recently.";

        public Contestant(BR_Controller brController, GameShowPlatform gameShowPlatform) {

        	this.gameShowPlatform = gameShowPlatform;
        	
        	setBrController(brController);
            getBrController().activateMissController(false);
            
            setMissController((MissController) getBrController().getMissController());
                        
            SimSt simSt = getMissController().getSimSt();
            setSimSt(simSt);
            logger = new SimStLogger(brController);
            
            String prodRules = null;
            // Ask for the userID. It is no longer passed as command line argument.
   	        String user = simSt.getUserID();
   	        if(user == null)
   	        {
   	        	user = (String)JOptionPane.showInputDialog( brController.getActiveWindow(),
   	            		USER_ID_REQUEST_MSG, USER_ID_REQUEST_TITLE, JOptionPane.QUESTION_MESSAGE);
   	        	simSt.setUserID(user);
    	        logger.enableLogging(getSimSt().getLoggingEnabled(), getSimSt().getLocalLoggingEnabled(), user);   	        	
   	        }  else
            {
    	        user = simSt.getUserID();
            	if(user == null)
            		user = "";
    	        logger.enableLogging(getSimSt().getLoggingEnabled(),getSimSt().getLocalLoggingEnabled(),  user);
            }

            trace.out("miss", "UserID: " + getSimSt().getUserID());
            // Check if it is running locally or run using WebStart
            if(!getSimSt().isWebStartMode()) {
            	trace.out("miss", "Contestant: Running locally and getting account file");
	        	String str = new WebStartFileDownloader().findFile(getSimSt().getUserID()+".account");
	        	File accountFile = new File(str);
	        	if(accountFile != null && accountFile.exists())
	        	{
	        		trace.out("miss", "Contestant: Running locally and account file exists");
		        	try {
						BufferedReader read = new BufferedReader(new FileReader(accountFile));
						String charName = read.readLine();
						SimSt.setSimStName(charName);
						String imgName = read.readLine();
						getSimSt().setSimStImage(imgName);
						read.close();
			        	//productionRules file is personalized by name
			        	prodRules = "productionRules-"+user+".pr";
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
            } else {
            	boolean success = false;
            	String account = getSimSt().getUserID()+".account";
            	trace.out("miss", "account: " + account);
            	File accountFile = new File(WebStartFileDownloader.SimStWebStartDir+account);
            	trace.out("miss", "Contestant: Running Webstart and getting account file");
            	if(!accountFile.exists()) {
					try {
		            	trace.out("miss", "Contestant: Running Webstart and getting file from server");
						success = getMissController().getStorageClient().retrieveFile(account, account, WebStartFileDownloader.SimStWebStartDir);
						if(success) {
							accountFile = new File(WebStartFileDownloader.SimStWebStartDir+account);
							trace.out("miss", accountFile.getCanonicalPath());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            	trace.out("miss", "accountFile.exists(): " + accountFile.exists());
            	if(accountFile != null && accountFile.exists()) {
            		try {
    	        		trace.out("miss", "Contestant: Running Webstart and found account file");            			
						BufferedReader read = new BufferedReader(new FileReader(accountFile));
						String charName = read.readLine();
						SimSt.setSimStName(charName);
						trace.out("miss", "setSimStName: " + charName);
						String imgName = read.readLine();
						getSimSt().setSimStImage(imgName);
						trace.out("miss", "setSimStImage: " + imgName);
						read.close();
			        	//productionRules file is personalized by name
			        	prodRules = WebStartFileDownloader.SimStWebStartDir+"productionRules-"+user+".pr";
			        	trace.out("miss", "prodRules: " + prodRules);
			        	File prFile = new File(prodRules);
			        	if(!prFile.exists()) {
			        		trace.out("miss", "Download production file from the server");
			        		getMissController().getStorageClient().retrieveFile("productionRules-"+user+".pr","productionRules-"+user+".pr" , WebStartFileDownloader.SimStWebStartDir);
			        	} else {
			        		trace.out("miss", "Production rule file exists");
			        	}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
            
        	//String id = "default";
        	//if(getSimSt().getUserID() != null && getSimSt().getUserID().length() > 0)
        	//	id = getSimSt().getUserID();
            //Name & image are set in SimSt - arguments change them there
        	//trace.out("miss", "id: " + id);
        	name = SimSt.getSimStName();
        	String img = getSimSt().getSimStImage();
  	
        	trace.out("miss", "name: " + name + " img: " + img);
        	logger.simStShortLog(SimStLogger.SSGAME, SimStLogger.GAMESHOW_STARTUP_ACTION, name, img);
        	
        	
        	//productionRules file is personalized by name
        	//String prodRules = "productionRules-"+id+".pr";

        	gameShowPlatform.setName(name);
        	gameShowPlatform.setImage(img);
        	
        	competitors = new Hashtable<String, Competitor>();
        	
            getSimSt().setDontShowAllRA(true);

            trace.out("miss", "prodRules: " + prodRules);
        	execution = new ContestExecution(getSimSt(), prodRules,this);
        	trace.out("miss", "After init ContestExecution");
        	
        	nameImg = name+","+img;
        	init(prodRules);
        }   

		Contestant(String prodRules, String name, String img)
		{
			nameImg = name+","+img;
        	competitors = new Hashtable<String, Competitor>();
			init(prodRules);
		}
		
		/*
		 * init
		 * Connect to sever on port & get set up with it & production rules
		 */
		public void init(String prodRules)
		{
			trace.out("miss", "init Contestant");
			
	        try {
	            //echoSocket = new Socket("127.0.0.1", 4444);
	        	echoSocket = new Socket(contestServer, contestPort);
	            out = new PrintWriter(echoSocket.getOutputStream(), true);
	            in = new BufferedReader(new InputStreamReader(
	                                        echoSocket.getInputStream()));
	        }
	        //exit contestant if unable to connect
	        catch (UnknownHostException e) {
	            System.err.println("Don't know about host.");
				logger.ssGameShowException(e, "Host not found");
	            System.exit(1);
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for "
	                               + "the connection. "+e.getMessage()+" "+e.getCause());
				logger.ssGameShowException(e, "Could not connect to server");
	            System.exit(1);
	        }

	        Connection connection = new Connection(echoSocket,out,in);
	        listener = new ListenerThread(connection);
			new Thread(listener).start();
			
			//Connection Message w/ name & info to server
			out.println(ContestServer.JOIN+","+nameImg+","+getSimSt().getUserID()+","+getBrController().getLogger().getClassName());
			logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.JOIN_MATCHUP_ACTION, "", rating);
			requestLeaderboard();

			startMatchupTime = Calendar.getInstance().getTimeInMillis();
							
			gameShowStart = Calendar.getInstance().getTimeInMillis();
			
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

    	Solution lastSolution = null;

    	//A thread to compute the solution to a problem without locking up the contestant's
    	//ability to respond to other messages
		class SolverThread extends Thread
		{
			Connection connection; //to communicate answer back to server
			String problem; //the problem to solve
			SolverThread(Connection connect, String prob)
			{
				connection = connect;
				problem = prob;
			}
			public void run() {
				gameShowPlatform.setGameShowExpression(SimStPLE.THINK_EXPRESSION);
				gameShowPlatform.setOppGameShowExpression(SimStPLE.THINK_EXPRESSION);
				//solve problem and save details for display
	    		lastSolution = execution.contestOnWholeProblem(problem);
				gameShowPlatform.setGameShowExpression(SimStPLE.NORMAL_EXPRESSION);
	    		String answer = lastSolution.answer;
	    		boolean isCorrect = lastSolution.correctness;
	    		int nSteps = lastSolution.steps;
	    		int nIncorrect = lastSolution.incorrectSteps; 
	    		String path = lastSolution.solutionPath;

	    		//if no answer, display question mark
	    		if(answer.length() == 0)
	    			answer = "?";
	    		gameShowPlatform.setAnswer(answer);
	    		//report solution to server
	    		connection.writer.println(ContestServer.SOLUTION+","+answer+","+isCorrect+","+nSteps+","
	    				+nIncorrect+","+path);
	    		
			}
		}
		
		class AcceptPromptThread extends Thread
		{
			Connection connection; //to communicate answer back to server
			String name;
			AcceptPromptThread(Connection connect,String n)
			{
				name = n;
				connection = connect;
			}
			public void run()
			{
				challengeCurrent = true;
				Competitor challenger = competitors.get(name);
				logger.simStShortLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGED_ACTION, ""+challenger.rating, "", name, rating);
	            long startTime = (new Date()).getTime();

				int winScore = ContestServer.projectWin(rating, challenger.rating);
				int loseScore = ContestServer.projectLoss(rating, challenger.rating);
	            String msg = "<html>"+challenger.name+" has challenged you to a game show.  Will you accept?<br>"+
	            	GameShowUtilities.replaceTwoPieces(PROJECTED_SCORE_MSG,""+winScore,""+loseScore);
				int response = JOptionPane.showConfirmDialog(gameShowPlatform, msg);
				long endTime = (new Date()).getTime();
	            long duration = endTime - startTime;
				if(challengeCurrent)
				{
					if(response == JOptionPane.YES_OPTION)
					{
						logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_ACCEPT_ACTION, "", 
								""+challenger.rating, "", (int) duration, "", name, rating);
						connection.writer.println(ContestServer.AGREE_CONTEST+","+name+",true");
					}
					else
					{
						logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_REFUSE_ACTION, "", 
								""+challenger.rating, "", (int) duration, "", name, rating);
						connection.writer.println(ContestServer.AGREE_CONTEST+","+name+",false");
					}
				}
				else if(response == JOptionPane.YES_OPTION)
				{
					JOptionPane.showMessageDialog(gameShowPlatform, ContestServer.CHALLENGE_NOT_CURRENT_MSG);
				}
				challengeCurrent = false;
			}
		}
		
		/*
		 * A threaded class to listen for message responses from the server
		 */
		class ListenerThread extends Thread
	    {
			//whether or not to keep listening for more messages
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
						//As each message is received, determine message type and distribute to the
						//correct method
					    System.out.println("Server: " + incomingMsg);
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
					    	//This is a reply-back message after the contestant has sent a message
					    	//to the server that it is leaving, acknowledging that that message is
					    	//received, don't need to process, just stop listening
					    	activeListener = false;
					    	break;
					    }
					    if(incomingMsg.startsWith(ContestServer.CHAT_PRIVATE_MESSAGE))
					    	addPrivateChatMessage(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.CHAT_GROUP_MESSAGE))
					    	addGroupChatMessage(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.ANNOUNCE_PRIVATE_MESSAGE))
					    	addPrivateAnnouncement(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.ANNOUNCE_MESSAGE))
					    	addGroupAnnouncement(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.PROBLEM_REQUEST_TIMEOUT))
					    	problemRequestTimeout(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.CHALLENGE_REQUEST_TIMEOUT))
					    	challengeRequestTimeout(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.PROBLEM_ANSWER_TIMEOUT))
					    	problemAnswerTimeout(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.PROBLEM_BANK_LIST))
					    	problemBankList(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.DUPLICATE_USERID))
					    	duplicateID(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.FORFEIT))
					    	forfeitContest(incomingMsg,false);
					    if(incomingMsg.startsWith(ContestServer.CANCEL))
					    	forfeitContest(incomingMsg,true);
					    if(incomingMsg.startsWith(ContestServer.LEADERBOARD))
					    	displayLeaderboard(incomingMsg);
					    if(incomingMsg.startsWith(ContestServer.OUTSTANDING))
					    	outstandingChallenge(incomingMsg);
					   
					}
				} catch (IOException e) {
					e.printStackTrace();
					logger.ssGameShowException(e, "Error receiving message");
				}
				
	    	}
	    	
	    	
	    	/*
	    	 * Handle a contest agreed message: give a message if the challenge was not
	    	 * accepted.
	    	 * Received message format is: CONTEST_AGREED,challengee,true/false
	    	 */
	    	protected void contestAgreed(String incomingMsg) {
				String[] args = incomingMsg.split(",");
	            long endTime = (new Date()).getTime();
	            long duration = endTime - requestContestStartTime;
	            Competitor comp = competitors.get(args[1]);
				if(args.length < 3 || !args[2].equals("true"))
				{
					JOptionPane.showMessageDialog(gameShowPlatform, comp.name+" has declined your challenge or did not respond.");

					logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_REFUSED_ACTION, "", 
							""+comp.rating, "", (int) duration, "", args[1], rating);
				}
				else
				{
					logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_ACCEPTED_ACTION, "", 
							""+comp.rating, "", (int) duration, "", args[1], rating);

					recentChallenges.remove(comp.userid);
				}

				gameShowPlatform.enableChallengeButton(true);
				outstandingChallenge = false;
			}
	    	
	    	/*
	    	 * Handle a contest requested message: give a choice to accept or decline a challenge
	    	 * and send the response back to the server.
	    	 * Received message format is: CONTEST_REQUESTED,challenger
	    	 */
			protected void contestRequested(String incomingMsg) {
				String[] args = incomingMsg.split(",");
				/*challengeCurrent = true;
				int response = JOptionPane.showConfirmDialog(null, args[1]+" has challenged you to a game show.  Will you accept?");
				if(challengeCurrent)
				{
					if(response == JOptionPane.YES_OPTION)
					{
						connection.writer.println(ContestServer.AGREE_CONTEST+","+args[1]+",true");
					}
					else
					{
						connection.writer.println(ContestServer.AGREE_CONTEST+","+args[1]+",false");
					}
				}
				else if(response == JOptionPane.YES_OPTION)
				{
					JOptionPane.showMessageDialog(null, ContestServer.CHALLENGE_NOT_CURRENT_MSG);
				}
				challengeCurrent = false;
				*/
				new Thread(new AcceptPromptThread(connection,args[1])).start();
			}
			
			/*
			 * Handle an end contest message: switch to the review screen and show details of
			 * the winner and solutions used.
			 * Received message format is: END_CONTEST,winner,contestant1_name:contestant1_new_rating,
			 * contestant2_name:contestant2_new_rating, contestant1_name:contestant1_problems_correct;
			 * contestant1_avg_solution_length;contestant1_%correct,contestant2_name:contestant2_problems_correct;
			 * contestant2_avg_solution_length;contestant2_%correct,contestant1_name:contestant1_solution_path1;
			 * contestant1_solution_path2;contestant1_solution_path3;contestant1_solution_path4;
			 * contestant1_solution_path5;,contestant2_name:contestant2_solution_path1;
			 * contestant2_solution_path2;contestant2_solution_path3;contestant2_solution_path4;
			 * contestant2_solution_path5;,contestant1_name:contestant1_correctness1;contestant1_correctness2;
			 * contestant1_correctness3;contestant1_correctness4;contestant1_correctness5;,
			 * contestant2_name:contestant2_correctness1;contestant2_correctness2;
			 * contestant2_correctness3;contestant2_correctness4;contestant2_correctness5;
			 */
			protected void endContest(String incomingMsg) {
				//JOptionPane.showMessageDialog(null, "Contest Over");
				brController.startNewProblem();
				gameShowPlatform.setProblem("");
				gameShowPlatform.clearPrivateChat();
				String[] args = incomingMsg.split(",");
				String winner = args[1];
				
				//parse the statistics into those for contestant1 & contestant2
				String[] ratings1 = args[4].split(":");
				String[] ratings2 = args[5].split(":");
				String[] solutions1 = args[6].split(":");
				String[] solutions2 = args[7].split(":");
				String[] corrects1 = args[8].split(":");
				String[] corrects2 = args[9].split(":");
				String[] solutionCont, solutionOpp, correctCont, correctOpp;
				String[] ratingsCont, ratingsOpp;
				
				//assign contestant1 & contestant2's data to this contestant or their opponent
				//based on the name used
				if(solutions1[0].equals(name))
				{
					//contestant1 is this contestant
					solutionCont = solutions1[1].split(";");
					solutionOpp = solutions2[1].split(";");
				}
				else
				{
					//contestant2 is this contestant
					solutionOpp = solutions1[1].split(";");
					solutionCont = solutions2[1].split(";");
				}

				if(corrects1[0].equals(name))
				{
					//contestant1 is this contestant
					correctCont = corrects1[1].split(";");
					correctOpp = corrects2[1].split(";");
				}
				else
				{
					//contestant2 is this contestant
					correctOpp = corrects1[1].split(";");
					correctCont = corrects2[1].split(";");
				}
				

				if(ratings1[0].equals(name))
				{
					//contestant1 is this contestant
					ratingsCont = ratings1[1].split(";");
					ratingsOpp = ratings2[1].split(";");
				}
				else
				{
					//contestant2 is this contestant
					ratingsOpp = ratings1[1].split(";");
					ratingsCont = ratings2[1].split(";");
				}

				String winMessage;
				String whoWin = "";

				//Determine whether contestant1 or contestant2 is the winner and create
				//an appropriate win message for the host to say
				if(ratings1[0].equals(winner))
				{
					//contestant1 is the winner
					String[] ratingsWin = ratings1[1].split(";");
					String[] ratingsLose = ratings2[1].split(";");
					winMessage = getWinMessage(winner, ratingsWin, ratingsLose);
				}
				else
				{
					//contestant2 is the winner
					String[] ratingsLose = ratings1[1].split(";");
					String[] ratingsWin = ratings2[1].split(";");
					winMessage = getWinMessage(winner, ratingsWin, ratingsLose);
				}
				
				gameShowPlatform.setHostSpeech(winMessage);
				if(winner.length() == 0)
				{
					gameShowPlatform.setReviewExpression(SimStPLE.NORMAL_EXPRESSION);
					gameShowPlatform.setOppReviewExpression(SimStPLE.NORMAL_EXPRESSION);
					whoWin = "tie";
				}
				else if(winner.equals(name))
				{
					gameShowPlatform.setReviewExpression(SimStPLE.SUCCESS_EXPRESSION);
					gameShowPlatform.setOppReviewExpression(SimStPLE.SAD_EXPRESSION);
					whoWin = "self";
				}
				else
				{
					gameShowPlatform.setOppReviewExpression(SimStPLE.SUCCESS_EXPRESSION);
					gameShowPlatform.setReviewExpression(SimStPLE.SAD_EXPRESSION);
					whoWin = "opponent";
				}
				
				//create the formatted strings for the match statistics
				String contRating = getRatingString(ratingsCont);
				String oppRating = getRatingString(ratingsOpp);
				
				gameShowPlatform.setRatings(contRating, oppRating);
				
				//set labels with the solution paths
				gameShowPlatform.setSolutions(solutionCont, solutionOpp, correctCont, correctOpp);
				
				long endTime = (new Date()).getTime();
	            long duration = endTime - contestStartTime;
				if(winner.length() == 0)
					winner = "tie";
				logger.simStLog(SimStLogger.SSGAME_REVIEW, SimStLogger.WIN_DECIDED_ACTION, "", whoWin,
						"review"+opponentName, null, (int) duration, winMessage, opponentID, rating);
				
				logger.simStShortLog(SimStLogger.SSGAME_REVIEW, SimStLogger.PROBLEMS_CORRECT_STATISTIC, 
						ratingsCont[0], ratingsOpp[0], opponentID,rating);
				logger.simStShortLog(SimStLogger.SSGAME_REVIEW, SimStLogger.PROBLEM_LENGTH_STATISTIC, 
						ratingsCont[1], ratingsOpp[1], opponentID,rating);
				logger.simStShortLog(SimStLogger.SSGAME_REVIEW, SimStLogger.PERCENT_CORRECT_STATISTIC, 
						ratingsCont[2], ratingsOpp[2], opponentID,rating);
				
				String myRating,opponentRating;
				if(args[2].startsWith(name))
				{
					myRating = args[2];
					opponentRating = args[3];
				}
				else
				{
					myRating = args[3];
					opponentRating = args[2];
				}
				String rate = myRating.substring(myRating.indexOf(':')+1);
				String oppRate = opponentRating.substring(opponentRating.indexOf(':')+1);
				rating = Integer.parseInt(rate);
				logger.simStShortLog(SimStLogger.SSGAME_REVIEW, SimStLogger.RATING_CHANGED_ACTION, 
						rate, oppRate, rating);
				
				//switch to the review screen
				gameShowPlatform.viewReview();

		        lastTabViewStart = Calendar.getInstance().getTimeInMillis(); 
		        lastTabViewed = "";
		        reviewStart = Calendar.getInstance().getTimeInMillis();
			}
			
			/*
			 * Handle an end contest message: switch to the review screen and show details of
			 * the winner and solutions used.
			 * Received message format is: END_CONTEST,winner,contestant1_name:contestant1_new_rating,
			 * contestant2_name:contestant2_new_rating, contestant1_name:contestant1_problems_correct;
			 * contestant1_avg_solution_length;contestant1_%correct,contestant2_name:contestant2_problems_correct;
			 * contestant2_avg_solution_length;contestant2_%correct,contestant1_name:contestant1_solution_path1;
			 * contestant1_solution_path2;contestant1_solution_path3;contestant1_solution_path4;
			 * contestant1_solution_path5;,contestant2_name:contestant2_solution_path1;
			 * contestant2_solution_path2;contestant2_solution_path3;contestant2_solution_path4;
			 * contestant2_solution_path5;,contestant1_name:contestant1_correctness1;contestant1_correctness2;
			 * contestant1_correctness3;contestant1_correctness4;contestant1_correctness5;,
			 * contestant2_name:contestant2_correctness1;contestant2_correctness2;
			 * contestant2_correctness3;contestant2_correctness4;contestant2_correctness5;
			 */
			protected void forfeitContest(String incomingMsg,boolean cancel) {
				//JOptionPane.showMessageDialog(null, "Contest Over");
				brController.startNewProblem();
				gameShowPlatform.closeProblemInputDialog();
				gameShowPlatform.setProblem("");
				gameShowPlatform.clearPrivateChat();
				String[] args = incomingMsg.split(",");
				String forfeitee = args[1];
				String forfeiter = opponentName;
				if(forfeitee.equals(opponentName))
					forfeiter = name;
				
				//parse the statistics into those for contestant1 & contestant2
				String[] ratings1 = args[4].split(":");
				String[] ratings2 = args[5].split(":");
				String[] solutions1 = args[6].split(":");
				String[] solutions2 = args[7].split(":");
				String[] corrects1 = args[8].split(":");
				String[] corrects2 = args[9].split(":");
				String[] solutionCont = new String[0];
				String[] solutionOpp= new String[0];
				String[] correctCont= new String[0];
				String[] correctOpp= new String[0];
				String[] ratingsCont= new String[0];
				String[] ratingsOpp= new String[0];
				
				if(solutions1.length > 1 && solutions2.length > 1)
				{
					//assign contestant1 & contestant2's data to this contestant or their opponent
					//based on the name used
					if(solutions1[0].equals(name))
					{
						//contestant1 is this contestant
						solutionCont = solutions1[1].split(";");
						solutionOpp = solutions2[1].split(";");
					}
					else
					{
						//contestant2 is this contestant
						solutionOpp = solutions1[1].split(";");
						solutionCont = solutions2[1].split(";");
					}
				}

				if(corrects1.length > 1 && corrects2.length > 1)
				{
					if(corrects1[0].equals(name))
					{
						//contestant1 is this contestant
						correctCont = corrects1[1].split(";");
						correctOpp = corrects2[1].split(";");
					}
					else
					{
						//contestant2 is this contestant
						correctOpp = corrects1[1].split(";");
						correctCont = corrects2[1].split(";");
					}
				}
				

				if(ratings1.length > 1 && ratings2.length > 1)
				{
					if(ratings1[0].equals(name))
					{
						//contestant1 is this contestant
						ratingsCont = ratings1[1].split(";");
						ratingsOpp = ratings2[1].split(";");
					}
					else
					{
						//contestant2 is this contestant
						ratingsOpp = ratings1[1].split(";");
						ratingsCont = ratings2[1].split(";");
					}
				}

				String winMessage = getForfeitMessage(forfeitee, forfeiter, cancel);
				
				
				gameShowPlatform.setHostSpeech(winMessage);
				if(forfeitee.equals(name))
				{
					gameShowPlatform.setReviewExpression(SimStPLE.NORMAL_EXPRESSION);
					gameShowPlatform.setOppReviewExpression(SimStPLE.SAD_EXPRESSION);
				}
				else
				{
					gameShowPlatform.setOppReviewExpression(SimStPLE.NORMAL_EXPRESSION);
					gameShowPlatform.setReviewExpression(SimStPLE.SAD_EXPRESSION);
				}
				
				//create the formatted strings for the match statistics
				String contRating = getRatingString(ratingsCont);
				String oppRating = getRatingString(ratingsOpp);
				
				gameShowPlatform.setRatings(contRating, oppRating);
				
				//set labels with the solution paths
				gameShowPlatform.setSolutions(solutionCont, solutionOpp, correctCont, correctOpp);
				
				long endTime = (new Date()).getTime();
	            long duration = endTime - contestStartTime;
				//TODO Correct logs
				/*logger.simStLog(SimStLogger.SSGAME_REVIEW, SimStLogger.WIN_DECIDED_ACTION, "", winner,
						"review"+opponentName, null, (int) duration, winMessage, opponentID);
				logger.simStLog(SimStLogger.SSGAME_REVIEW, SimStLogger.WIN_STATISTICS_ACTION, "", 
						ratingsCont[0]+"/"+ratingsCont[1]+"/"+ratingsCont[2]+"vs"+ratingsOpp[0]+"/"+ratingsOpp[1]+"/"+ratingsOpp[2],
						"", 0, "", opponentID);*/
				
				String myRating;
				if(args[2].startsWith(name))
					myRating = args[2];
				else
					myRating = args[3];
				int rate = Integer.parseInt(myRating.substring(myRating.indexOf(':')+1));
				
				rating = rate;
				logger.simStShortLog(SimStLogger.SSGAME_REVIEW, SimStLogger.RATING_CHANGED_ACTION, 
						myRating, oppRating, rating);
				
				//switch to the review screen
				gameShowPlatform.viewReview();

		        lastTabViewStart = Calendar.getInstance().getTimeInMillis(); 
		        lastTabViewed = "";
		        reviewStart = Calendar.getInstance().getTimeInMillis();
			}
			
			
			
			
			
			/*
			 * Build a formatted html string to display the match statistics
			 * Format of values is {number_correct_solutions, avg_steps_in_correct_solution, %_steps_correct}
			 */
			protected String getRatingString(String[] values)
			{
				String rating = "<html>";
				
				if(values.length >= 3)
				{
					//Add headings for each statistic and display on a separate line
					rating += NUM_CORRECT+values[0]+"<br>";
					rating += AVG_STEPS+values[1]+"<br>";
					rating += PCT_CORRECT+values[2]+"%<br>";
				}
				rating += "</html>";
				
				return rating;
			}
			
			/*
			 * Determine why the winner won and create an appropriate message for the host to
			 * say to explain it.
			 * Format of values is {number_correct_solutions, avg_steps_in_correct_solution, %_steps_correct}
			 * 
			 */
			protected String getWinMessage(String winner, String[] valuesWinner, String[] valuesLoser)
			{
				String win = "<html>";
				if(winner.length()== 0)
				{
					//If winner is empty string, result is a tie
					win += TIE_MSG;
					win += "</html>";
					return win;
				}
					win += GameShowUtilities.replacePiece(WINNER_MSG, winner);
				if(valuesWinner.length >= 3 && valuesLoser.length >= 3)
				{
					int correct = Integer.parseInt(valuesWinner[0])-Integer.parseInt(valuesLoser[0]);
					double steps = Double.parseDouble(valuesWinner[1])-Double.parseDouble(valuesLoser[1]);
					int percent = Integer.parseInt(valuesWinner[2])-Integer.parseInt(valuesLoser[2]);
					//determine which is the first statistic the winner did better on
					if(correct > 0)
					{
						//The winner had more problems correct
						win += GameShowUtilities.replacePiece(WIN_MORE_CORRECT, winner);
					}
					else if(steps < 0)
					{
						//Number of problems correct was equal, but the winner had shorter solution paths
						win += GameShowUtilities.replacePiece(WIN_SHORTER, winner);
					}
					else if(percent > 0)
					{
						//Number of problems correct was equal, but the winner made less mistakes overall
						win += GameShowUtilities.replacePiece(WIN_MORE_STEPS_CORRECT, winner);
					}
				}
				win += "</html>";
				
				return win;
			}
			
			/*
			 * Determine why the winner won and create an appropriate message for the host to
			 * say to explain it.
			 * Format of values is {number_correct_solutions, avg_steps_in_correct_solution, %_steps_correct}
			 * 
			 */
			protected String getForfeitMessage(String forfeitee, String forfeiter, boolean cancel)
			{
				String win = "<html>";
				
				if(cancel)
					win += GameShowUtilities.replaceTwoPieces(CANCEL_MSG, forfeitee, forfeiter);
				else
					win += GameShowUtilities.replaceTwoPieces(FORFEIT_MSG, forfeitee, forfeiter);
				
				win += "</html>";
				
				return win;
			}
			
			//Keep track of how many solutions this contestant and their opponent has gottent correct
			int contScore = 0;
			int oppScore = 0;
			
			/*
			 * Handle a solution assessed message: display appropriate messaging noting who was correct
			 * and track scores
			 * Message received format is: SOLUTION_ASSESSED,Opponent_Answer,Opponent_Correctness
			 * 
			 * Only opponent information is sent, this contestant's information is compared against 
			 * the lastSolution variable, which holds their work from the most recent problem
			 */
			protected void solutionAssessed(String incomingMsg) {
				String[] solution = incomingMsg.split(",");
				gameShowPlatform.setOppAnswer(solution[1]);
				boolean oppCorrectness = Boolean.parseBoolean(solution[2]);
				
				String correct = "";
		    	if(oppCorrectness){
		    		correct = SimStLogger.TRUE;
		    	}
		    	else {
		    		correct = SimStLogger.FALSE;
		    	}
				logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.OPPONENT_ANSWER_ACTION, "oppAnswer",
						solution[1], "", null, null, correct, "", "", "", 0, "", opponentID, rating);
				
				gameShowPlatform.setCorrectness(lastSolution.correctness, oppCorrectness);
				if(lastSolution.correctness)
					gameShowPlatform.setGameShowExpression(SimStPLE.SUCCESS_EXPRESSION);
				else
					gameShowPlatform.setGameShowExpression(SimStPLE.SAD_EXPRESSION);
				
				if(oppCorrectness)
					gameShowPlatform.setOppGameShowExpression(SimStPLE.SUCCESS_EXPRESSION);
				else
					gameShowPlatform.setOppGameShowExpression(SimStPLE.SAD_EXPRESSION);
					
				String message = "<html>";
				String result = "";
				if(lastSolution.correctness&&oppCorrectness)
				{
					//Both are correct
					message += BOTH_GOT;
					contScore++;
					oppScore++;
					result = "both";
				}
				else if(lastSolution.correctness)
				{
					//Only this contestant is correct
					message += GameShowUtilities.replacePiece(ONE_GOT, name);
					contScore++;
					result = "self";
				}
				else if(oppCorrectness)
				{
					//Only the opponent is correct
					message += GameShowUtilities.replacePiece(ONE_GOT, opponentName);
					oppScore++;
					result = "opponent";
				}
				else
				{
					//Neither are correct
					message += NONE_GOT;
					result = "none";
				}
				message += "</html>";
				gameShowPlatform.setHostSpeech(message);
				gameShowPlatform.setScores(contScore, oppScore);
								
				logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.CONTEST_RESULT_ACTION, "", result,
						"", null, null, (""+lastSolution.correctness).toUpperCase(), "", "", "", 0, "", opponentID);
			}
			
			/*
			 * Handle a list participants message: store information about current participants and
			 * display list of participants.  Maintain same selection if still present, if none selected
			 * select one.
			 * Format of received message is: LIST,Participant1_name;Participant1_image;Participant1_rating;
			 * Participant1_#wins;Participant1_#losses;Participant1_#ties,Participant2_name;Participant2_image;
			 * Participant2_rating;Participant2_#wins;Participant2_#losses;Participant2_#ties (and so on for all participants)
			 */
			protected void listParticipants(String list)
	    	{
				//See who is already selected
				Competitor selected = gameShowPlatform.getSelectedParticipant();
				if(list.indexOf(',') >= 0)
				{
					list = list.substring(list.indexOf(',')+1);
		    		String[] entries = list.split(",");
		    		Competitor[] names = new Competitor[entries.length-1];
		    		int count = 0;
		    		//Look individually at the entry for each different participant
		    		for(int i=0;i<entries.length;i++)
		    		{
		    			String[] details = entries[i].split(";");
		    			Competitor competitor;
		    			//If already in the hashtable, retrieve prior data
		    			if(competitors.containsKey(details[6]))
		    				competitor = competitors.get(details[6]);
		    			else //If not already in hashtable, create new w/ default info
		    				competitor = new Competitor(details[0],details[1],details[6]);
		    			//parse data into the object
		    			competitor.name = details[0];
		    			competitor.img = details[1];
		    			competitor.rating = Integer.parseInt(details[2]);
		    			competitor.wins = Integer.parseInt(details[3]);
		    			competitor.losses = Integer.parseInt(details[4]);
		    			competitor.ties = Integer.parseInt(details[5]);
		    			competitor.userid = details[6];
		    			competitors.put(competitor.userid, competitor);
		    			if(competitor.userid.equals(getSimSt().getUserID()))
		    			{
		    				//if the entry is for this contestant, update their statistics, but don't go on
		    				//to add to list of names
		    				gameShowPlatform.setRating(competitor);
		    				rating = competitor.rating;
		    				continue;
		    			}
		    			//add name onto the list of available competitors
		    			names[count] = competitor;
		    			count++;
		    		}
		    		//display list of all available competitors in selectable list
		    		gameShowPlatform.setParticipantList(names);

					Competitor closest = null;
					int closeness = 100;
		    		for(int i=0;i<names.length;i++)
		    		{
		    			if(closest == null || closeness > Math.abs(names[i].rating-rating))
		    			{
		    				closest = names[i];
		    				closeness = Math.abs(names[i].rating-rating);
		    			}
		    		}
		    		
		    		if(selected == null && closest != null)
		    		{
		    			//select the participant with the closest rating
		    			gameShowPlatform.participantList.setSelectedValue(closest, true);
		    			//If nothing is still selected, just select the first 
		    			if(gameShowPlatform.participantList.getSelectedIndex() == -1)
		    				gameShowPlatform.participantList.setSelectedIndex(0);
		    		}
		    		else if(selected == null && count > 0)
		    		{
		    			//If none were previously selected, select the first
		    			gameShowPlatform.participantList.setSelectedIndex(0);
		    		}
		    		else if(count > 0)
		    		{
		    			//If one was previously selected, try to select it again
		    			gameShowPlatform.participantList.setSelectedValue(selected, true);
		    			//If nothing is still selected, just select the first 
		    			if(gameShowPlatform.participantList.getSelectedIndex() == -1)
		    				gameShowPlatform.participantList.setSelectedIndex(0);
		    			
		    		}
				}
	    	}
	    	
			long contestStartTime = 0;

			/*
			 * Handle a start contest message: switch to the gameshow view and display the correct
			 * opponent.
			 * Format of received message is: START_CONTEST,opponent
			 */
	    	protected void startContest(String msg)
	    	{
	    		execution.reset();
	    		//switch to the gameshow screen
	    		gameShowPlatform.viewGameshow();
	    		//reset scores
	    		contScore = 0;
	    		oppScore = 0;
	    		String[] msgParts = msg.split(",");
	    		opponentID = msgParts[1];
	    		//opponentName = msgParts[1];
	    		Competitor comp = null;
	    		//Look up opponent in hashtable of competitors
	    		if(competitors.get(opponentID) != null)
	    		{
	    			comp = competitors.get(opponentID);
	    			opponentName = comp.name;
	    		}
	    		else
	    		{
	    			comp = new Competitor(opponentName, msgParts[2], opponentID);
	    			competitors.put(comp.userid, comp);
	    		}
	    		//Display correct opponent
	    		gameShowPlatform.setOpponent(comp);
	    		//Clear previous answers and scores
	    		gameShowPlatform.clearAnswers();
				gameShowPlatform.setScores(contScore, oppScore);
				gameShowPlatform.setHostSpeech("<html>"+BEGIN_MSG+"</html>");
				gameShowPlatform.setGameShowExpression(SimStPLE.NORMAL_EXPRESSION);
				gameShowPlatform.setOppGameShowExpression(SimStPLE.NORMAL_EXPRESSION);

				int duration = (int) (Calendar.getInstance().getTimeInMillis()-startMatchupTime);
				logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.SUCCESSFUL_MATCHUP_ACTION, "", "",
						"", duration, "", opponentID, rating);
				logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.CONTEST_START_ACTION, "", "",
						"challenge"+opponentName, 0, "", opponentID, rating);
				contestStartTime = (new Date()).getTime();
	    	}
	    	
	    	/*
	    	 * Handle a start problem message: Display problem and start a thread to solve it
	    	 * Format of received message is:  START_PROBLEM,problem
	    	 */
	    	protected void startProblem(String msg)
	    	{
	    		String[] msgParts = msg.split(",");
	    		String problem = msgParts[1];
	    		//clear away old answers
	    		gameShowPlatform.clearAnswers();
	    		//display new problem
	    		gameShowPlatform.setHostSpeech("<html>"+GameShowUtilities.replacePiece(PROBLEM_START_MSG, problem));
	    		gameShowPlatform.setProblem(problem);
	    		
	    		//start separate thread to solve problem
	    		new SolverThread(connection, problem).start();

	    	}
	    	
	    }
		

	    public static void main(String[] args) throws IOException 
	    {

	    	new Contestant(args[0],args[1],args[2]);
	    	

	    }

	    public void outstandingChallenge(String incomingMsg) {
			String[] parts = incomingMsg.split(",");
			
			gameShowPlatform.addGroupNotification(parts[1]);
			outstandingChallenge = false;
			gameShowPlatform.enableChallengeButton(true);
			logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.OUTSTANDING_ACTION, "", "", "",
					null, 0, parts[1], parts[2], rating);
			recentChallenges.remove(parts[2]);
			
		}
		public void displayLeaderboard(String incomingMsg) {
			
	    	String leaderboard = "<html>"+incomingMsg.replaceAll(",", "<br>")+"</html>";
			//JOptionPane.showMessageDialog(null, leaderboard);
	    	 gameShowPlatform.setLeaderboard(leaderboard);
		}
	    
		public void duplicateID(String incomingMsg) {
			JOptionPane.showMessageDialog(null, DUPLICATE_USERID_MSG);
			leave();
			System.exit(0);
		}
		
		public void problemBankList(String incomingMsg) {
			//String problems = incomingMsg.replaceAll(ContestServer.PROBLEM_BANK_LIST+",", "<html>");
			String problems = incomingMsg.substring(16);
			//problems = problems.replaceAll(",", "<br>");
			//problems += "</html>";
			//JLabel label = new JLabel(problems);
			//frame.getContentPane().add(label);
			//frame.setPreferredSize(label.getPreferredSize());
			//String[] columns = {"Problem","Attempts","Attempts Correct","Percentage","Difficulty"};
			String[] columns = {"Problem","Attempts","Difficulty"};
			
			String[] rows = problems.split(",");
			
			/*Object[][] data = {
					{"2x=3","5","4","80%","2-star"},	
					{"4x=5x","3","1","33%","4-star"}
			};*/
			
			Object[][] data = new Object[rows.length][];
			for(int i=0;i<data.length;i++)
			{
				String[] tmp = rows[i].split(";");
				/*data[i] = new Object[5];
				data[i][0] = tmp[0];
				data[i][1] = new Integer(tmp[1]);
				data[i][2] = new Integer(tmp[2]);
				data[i][3] = tmp[3];
				data[i][4] = gameShowPlatform.createImageIcon("img/"+tmp[4]);*/
				data[i] = new Object[3];
				data[i][0] = tmp[0];
				data[i][1] = new Integer(tmp[1]);
				data[i][2] = gameShowPlatform.createImageIcon("img/"+tmp[2]);
			}
						
			
			gameShowPlatform.displayProblemBank(columns, data);
			//frame.getContentPane().add(table);
		}
	    
		public void problemAnswerTimeout(String incomingMsg) {
	    	lastSolution = execution.new Solution();
	    	lastSolution.correctness = false;
	    	lastSolution.answer = "?";
	    	gameShowPlatform.setAnswer("?");
	    	
			logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_SUBMIT_TIMEOUT_ACTION, "", "",
					"", ContestServer.PROBLEM_SOLVE_TIMEOUT_TIME, "", opponentID,rating);
			
		}

		boolean challengeCurrent = false;
	    public void challengeRequestTimeout(String incomingMsg) {
	    	String[] parts = incomingMsg.split(",");
	    	
	    	String notif = GameShowUtilities.replacePiece(ContestServer.CHALLENGE_REQUEST_TIMEOUT_MSG, parts[2]);
			gameShowPlatform.addGroupNotification(notif);
			challengeCurrent = false;
			
			logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_TIMEOUT_ACTION, "", "",
					"", ContestServer.CHALLENGE_TIMEOUT_TIME, notif, parts[1], rating);
		}
	    
		public void problemRequestTimeout(String incomingMsg) {
			gameShowPlatform.closeProblemInputDialog();
			gameShowPlatform.addPrivateNotification(ContestServer.PROBLEM_REQUEST_TIMEOUT_MSG);

			logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_REQUEST_TIMEOUT_ACTION, "", "",
					"", ContestServer.PROBLEM_REQUEST_TIMEOUT_TIME, ContestServer.PROBLEM_REQUEST_TIMEOUT_MSG,
					opponentID, rating);
		}
                
                public void problemRequestExit(String incomingMsg, Connection connection) {
                        connection.writer.println(ContestServer.PROBLEM_REQUEST_EXIT);
			gameShowPlatform.closeProblemInputDialog();
			gameShowPlatform.addPrivateNotification(ContestServer.PROBLEM_REQUEST_EXIT_MSG);

			logger.simStLog(SimStLogger.SSGAME_CONTEST, incomingMsg, "", "",
		  			     "", 0, ContestServer.PROBLEM_REQUEST_EXIT_MSG, opponentID, rating);
		}
		/*
	     * Handle a private chat message: add the message onto the gameshow screen's chat
	     * Format of received message is: CHAT_PRIVATE_MESSAGE,message
	     */
		public void addPrivateChatMessage(String incomingMsg) {
			String chat = incomingMsg.substring(incomingMsg.indexOf(',')+1);
			gameShowPlatform.addPrivateChatText(chat);
			logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.CHAT_MESSAGE_PRIVATE_ACTION, chat, rating);
			
		}

	    /*
	     * Handle a private chat message: add the message onto the matchup screen's chat
	     * Format of received message is: CHAT_GROUP_MESSAGE,message
	     */
		public void addGroupChatMessage(String incomingMsg) {
			String chat = incomingMsg.substring(incomingMsg.indexOf(',')+1);
			gameShowPlatform.addGroupChatText(chat);
			logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHAT_MESSAGE_GROUP_ACTION, chat, rating);
			
		}

	    /*
	     * Handle a private announcement chat message: add the message onto the gameshow screen's chat
	     * Format of received message is: ANNOUNCE_PRIVATE_MESSAGE,message
	     */
		public void addPrivateAnnouncement(String incomingMsg) {
			String chat = incomingMsg.substring(incomingMsg.indexOf(',')+1);
			gameShowPlatform.addPrivateAnnounceText(chat);
			logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.ANNOUNCE_MESSAGE_PRIVATE_ACTION, chat, rating);
			
		}

	    /*
	     * Handle an announcement chat message: add the message onto the matchup screen's chat
	     * Format of received message is: ANNOUNCE_MESSAGE,message
	     */
		public void addGroupAnnouncement(String incomingMsg) {
			String chat = incomingMsg.substring(incomingMsg.indexOf(',')+1);
			gameShowPlatform.addGroupAnnounceText(chat);
			logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.ANNOUNCE_MESSAGE_GROUP_ACTION, chat, rating);
			
		}

		/*
		 * Handle a problem request message: display a dialog for the problem to be typed into
		 * Format of received message is: REQUEST_PROBLEM
		 */
		public void requestProblem(String incomingMsg) {
			gameShowPlatform.showProblemInputDialog();
			logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_REQUEST_ACTION, 
					"problem"+(execution.numProblems+1), "", "challenge"+opponentName,0,"",rating);
			submitProblemRequestStartTime = (new Date()).getTime();
			//listener.connection.writer.println(ContestServer.REQUEST_PROBLEM_BANK);
		}

		
		public void requestLeaderboard()
		{
			out.println(ContestServer.LEADERBOARD+","+brController.getLogger().getClassName());
			logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.LEADERBOARD_REQUEST_ACTION, "", rating);
		}
		
		
		long submitProblemRequestStartTime = -1;
		/*
		 * Process a user interface action
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {

			if(arg0.getActionCommand().equals(CONTEST_REQUEST_BUTTON))
			{
				//send a message to the server requesting a random challenge
				out.println(ContestServer.REQUEST_CONTEST);
				logger.simStShortLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_CONTESTANT_ACTION, "Any", "", "Any",rating);
				requestContestStartTime = (new Date()).getTime();
			}
			if(arg0.getActionCommand().equals(CONTEST_CHALLENGE_BUTTON))
			{
				final String userid = gameShowPlatform.getSelectedParticipant().userid;
				
				if(recentChallenges.contains(userid))
				{
					JOptionPane.showMessageDialog(null, TOO_RECENT_MSG);
					return;
				}
				
				//send a message to the server requesting a challenge with the participant selected on the list
				out.println(ContestServer.REQUEST_CONTEST+","+userid);
				
				logger.simStShortLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.CHALLENGE_CONTESTANT_ACTION, 
					""+gameShowPlatform.getSelectedParticipant().rating, "", gameShowPlatform.getSelectedParticipant().userid);
				
				gameShowPlatform.enableChallengeButton(false);
				outstandingChallenge = true;
				boolean worked = recentChallenges.add(userid);
				Timer tooRecent = new Timer(ContestServer.CHALLENGE_TIMEOUT_TIME, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						recentChallenges.remove(userid);
					}
				});
				tooRecent.setRepeats(false);
				tooRecent.start();
				requestContestStartTime = (new Date()).getTime();
			}
			if(arg0.getActionCommand().equals(REVIEW_CONTINUE_BUTTON))
			{
				//rejoin the game and view the matchup screen again
				gameShowPlatform.viewMatchup();
				
				int duration = (int) (Calendar.getInstance().getTimeInMillis() - lastTabViewStart);
		        logger.simStLog(SimStLogger.SSGAME_REVIEW, SimStLogger.CONTINUE_BUTTON_ACTION, "", "", "", duration, rating);
				
				out.println(ContestServer.JOIN+","+nameImg+","+getSimSt().getUserID()+","+getBrController().getLogger().getClassName());

				logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.JOIN_MATCHUP_ACTION, "");
				startMatchupTime = Calendar.getInstance().getTimeInMillis();

				requestLeaderboard();
			}
			if(arg0.getActionCommand().equals(INPUT_FIELD))
			{
				//send a private chat message to the server
				String input = gameShowPlatform.takeInput(); //also clears the input line
				if(input.length() == 0)
					return;  //don't send if no message
				out.println(ContestServer.CHAT_PRIVATE_MESSAGE+","+input);
			}
			if(arg0.getActionCommand().equals(GROUP_INPUT_FIELD))
			{
				//send a group chat message to the server
				String input = gameShowPlatform.takeGroupInput(); //also clears the input line
				if(input.length() == 0)
					return; //don't send if no message
				out.println(ContestServer.CHAT_GROUP_MESSAGE+","+input);
			}

			if(arg0.getActionCommand().equals(PROBLEM_INPUT_SUBMIT))
			{
				String aLhs = gameShowPlatform.getLhsInput();
				String aRhs = gameShowPlatform.getRhsInput();
				
				//determine if the problem in the problem input dialog is valid
				String lhs = simSt.getInputChecker().interpret("commTable1_C1R1", gameShowPlatform.getLhsInput());
				String rhs = simSt.getInputChecker().interpret("commTable2_C1R1", gameShowPlatform.getRhsInput());
				//Display the first error if there is one
				if(lhs == null)
				{
					//Not valid because the left hand side isn't valid
					String error = simSt.getInputChecker().invalidInputMessage("commTable1_C1R1", gameShowPlatform.getLhsInput(), null);

					String problem = aLhs+"="+aRhs;
					long endTime = (new Date()).getTime();
		            long duration = endTime - submitProblemRequestStartTime;
					logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_INVALID_ACTION,
							"problem"+execution.numProblems, problem, "", (int) duration, error);
					submitProblemRequestStartTime = (new Date()).getTime();
					
					JOptionPane.showMessageDialog(gameShowPlatform.problemInputOk, error);
				}else if(rhs == null)
				{
					//Not valid because the right hand side isn't valid
					String error = simSt.getInputChecker().invalidInputMessage("commTable2_C1R1", gameShowPlatform.getRhsInput(), null);

					String problem = aLhs+"="+aRhs;
					long endTime = (new Date()).getTime();
		            long duration = endTime - submitProblemRequestStartTime;
					logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_INVALID_ACTION,
							"problem"+execution.numProblems, problem, "", (int) duration, error);
					submitProblemRequestStartTime = (new Date()).getTime();
					
					JOptionPane.showMessageDialog(gameShowPlatform.problemInputOk, error);
				}
				else if(!simSt.getInputChecker().checkVariables(lhs, rhs))
				{
					//Not valid because the variables aren't the same between the sides
					String error = simSt.getInputChecker().invalidVariablesMessage(lhs, rhs);

					String problem = aLhs+"="+aRhs;
					long endTime = (new Date()).getTime();
		            long duration = endTime - submitProblemRequestStartTime;
					logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_INVALID_ACTION,
							"problem"+execution.numProblems, problem, "", (int) duration, error);
					submitProblemRequestStartTime = (new Date()).getTime();
					
					JOptionPane.showMessageDialog(gameShowPlatform.problemInputOk, error);
				}
				else if(lhs.length() == 0 || rhs.length() == 0)
				{
					//Not valid because one side is blank
					String error = BLANK_ERROR;

					String problem = aLhs+"="+aRhs;
					long endTime = (new Date()).getTime();
		            long duration = endTime - submitProblemRequestStartTime;
					logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_INVALID_ACTION,
							"problem"+execution.numProblems, problem, "", (int) duration, BLANK_ERROR);
					submitProblemRequestStartTime = (new Date()).getTime();
					
					JOptionPane.showMessageDialog(gameShowPlatform.problemInputOk, error);
				}
				else
				{
					//close the dialog and send a message to the server with the problem
					gameShowPlatform.closeProblemInputDialog();
					String problem = lhs+"="+rhs;
					long endTime = (new Date()).getTime();
		            
		            long duration = endTime - submitProblemRequestStartTime;
					out.println(ContestServer.SUBMIT_PROBLEMS+","+problem);
					logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.PROBLEM_SUBMIT_ACTION,
							"problem"+execution.numProblems, problem, "", (int) duration);
				}
			}
			
			if(arg0.getActionCommand().equals(PROBLEM_BANK_BUTTON))
			{
				listener.connection.writer.println(ContestServer.REQUEST_PROBLEM_BANK);
				logger.simStLog(SimStLogger.SSGAME_CONTEST, SimStLogger.GENERATE_PROBLEMS_BUTTON_ACTION,
						"problem"+execution.numProblems, "", "");
			}
			
			if(arg0.getActionCommand().equals(LEADERBOARD_BUTTON))
			{
				requestLeaderboard();
			}

                        if(arg0.getActionCommand().equals(WINDOW_EXIT))
                        {
                            problemRequestExit(ContestServer.PROBLEM_REQUEST_EXIT, listener.connection);
                        }
		}
		
		/*
		 * Automatically called on closing the program: tell the server that this contestant is
		 * leaving.
		 */
		public void leave()
		{
			int duration = (int) (Calendar.getInstance().getTimeInMillis() - gameShowStart);
			logger.simStLog(SimStLogger.SSGAME, SimStLogger.GAMESHOW_CLOSED_ACTION, "", "", "", duration, rating);
			
			//stop listening for more messages
			listener.activeListener = false;
			//send leave message
			out.println(ContestServer.LEAVE);
			//wait a short time for the server to acknowledge leaving
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				logger.ssGameShowException(e1, "Error in leaving");
			}

			//close down open sockets and streams
			try {
				out.close();
				echoSocket.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.ssGameShowException(e, "Error in shutdown");
			}
		}
		

		Competitor lastSelected = null;
		
		/*
		 * Process a user interface action of an item in the list of available participants
		 * being selected or unselected
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			JList list = ((JList) arg0.getSource());
			if(list.getSelectedIndex() != -1)
			{
				//Someone is selected: display their info
				//String selected = list.getSelectedValue().toString();
				//Competitor comp = competitors.get(selected);
				Competitor comp = (Competitor) list.getSelectedValue();
				gameShowPlatform.setOpponent(comp);
				/*if(rating - comp.rating > ALLOWABLE_RATING_DIFF)
				{
					//too easy
					gameShowPlatform.enableChallengeButton(false);
					gameShowPlatform.colorOpponentRating(Color.gray);
				}
				else if(comp.rating - rating > ALLOWABLE_RATING_DIFF)
				{
					//too hard
					gameShowPlatform.enableChallengeButton(false);
					gameShowPlatform.colorOpponentRating(Color.red);
				}*/
				if(!outstandingChallenge)
				{
					//good
					gameShowPlatform.enableChallengeButton(true);
					gameShowPlatform.colorOpponentRating(Color.black);
				}
				if(lastSelected != comp)
				{
					logger.simStShortLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.VIEW_CONTESTANT_ACTION, ""+comp.rating, comp.name+"-"+comp.stats(), comp.userid, rating);
					lastSelected = comp;
				}
				
				int winScore = ContestServer.projectWin(rating, comp.rating);
				int loseScore = ContestServer.projectLoss(rating, comp.rating);
				gameShowPlatform.setMatchupComment(GameShowUtilities.replaceTwoPieces(PROJECTED_SCORE_MSG, ""+winScore, ""+loseScore));
				//JOptionPane.showMessageDialog(null, "If you win your new score will be "+winScore+" and if you lose your new score will be "+loseScore+".");
				
			}
			else
			{
				//No one is selected, clear any displayed info
				gameShowPlatform.setOpponent(null);
				gameShowPlatform.enableChallengeButton(false);
				gameShowPlatform.setMatchupComment("");
				if(lastSelected != null)
				{
					logger.simStLog(SimStLogger.SSGAME_MATCHUP, SimStLogger.VIEW_CONTESTANT_ACTION, "", "None Selected",
						"", null, null, "", "", "", "", 0, "", "None");
					lastSelected = null;
				}
			}
						
		}
		
		@Override
		public void stateChanged(ChangeEvent event) {
			if(event.getSource() instanceof JTabbedPane)
			{
				JTabbedPane tabPane = (JTabbedPane) event.getSource();
			
				if(lastTabViewed.length() == 0)
				{
					lastTabViewed = tabPane.getTitleAt(0);
				}
				int duration = (int) (Calendar.getInstance().getTimeInMillis() - lastTabViewStart);
		        lastTabViewStart = Calendar.getInstance().getTimeInMillis(); 
		        
		        logger.simStLog(SimStLogger.SSGAME_REVIEW, SimStLogger.TAB_LEFT_ACTION, "",lastTabViewed,"",duration, rating);
	            
				logger.simStLog(SimStLogger.SSGAME_REVIEW, SimStLogger.TAB_SWITCH_ACTION, tabPane.getTitleAt(tabPane.getSelectedIndex()), rating);
				
	            //getMissController().getSimStPLE().setUpTab(tabPane.getTitleAt(tabPane.getSelectedIndex()));
	            
	            lastTabViewed = tabPane.getTitleAt(tabPane.getSelectedIndex());
	            
			}
		}
		
		 class ParticipantListCellRenderer extends JLabel implements ListCellRenderer {
		     
		     /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private static final String spaces = "                                 ";

			// This is the only method defined by ListCellRenderer.
		     // We just reconfigure the JLabel each time we're called.

		     public Component getListCellRendererComponent(
		       JList list,
		       Object value,            // value to display
		       int index,               // cell index
		       boolean isSelected,      // is the cell selected
		       boolean cellHasFocus)    // the list and the cell have the focus
		     {
		         String s = "";
		         
		         if(value instanceof Competitor)
		         {
		        	 Competitor comp = (Competitor) value;
		        	 s = comp.name+comp.rating;
		        	 if(s.length() < 30)
		        		 s = comp.name+spaces.substring(0,30-s.length())+comp.rating;
		        	 else
		        		 s = comp.name+spaces.substring(0,1)+comp.rating;
		         }
		         else if(value != null)
		         {
		        	 s = value.toString();
		         }
		         
		         setText(s);
		         
		   	   if (isSelected) {
		             setBackground(list.getSelectionBackground());
			       setForeground(list.getSelectionForeground());
			   }
		         else {
			       setBackground(list.getBackground());
			       setForeground(list.getForeground());
			   }
			   setEnabled(list.isEnabled());
			   setFont(list.getFont());
		         setOpaque(true);
		         return this;
		     }
		 }


		 public Action getReconnectAction()
		 {
			 return new AbstractAction() {
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					int duration = (int) (Calendar.getInstance().getTimeInMillis() - gameShowStart);
					
					//stop listening for more messages
					listener.activeListener = false;
					//send leave message
					out.println(ContestServer.LEAVE+",technical");
					//wait a short time for the server to acknowledge leaving
		    		try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
						logger.ssGameShowException(e1, "Error in leaving");
					}

					//close down open sockets and streams
					try {
						out.close();
						echoSocket.close();
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
						logger.ssGameShowException(e, "Error in shutdown");
					}
										
					trace.out("miss", "reconnect Contestant");
					
			        try {
			        	echoSocket = new Socket(contestServer, contestPort);
			            out = new PrintWriter(echoSocket.getOutputStream(), true);
			            in = new BufferedReader(new InputStreamReader(
			                                        echoSocket.getInputStream()));
			        }
			        catch (UnknownHostException e) {
			            System.err.println("Don't know about host.");
						logger.ssGameShowException(e, "Host not found");
			        } catch (IOException e) {
			            System.err.println("Couldn't get I/O for "
			                               + "the connection.");
						logger.ssGameShowException(e, "Could not reconnect to server");
			        }

			        Connection connection = new Connection(echoSocket,out,in);
			        listener = new ListenerThread(connection);
					new Thread(listener).start();
					
					//Connection Message w/ name & info to server
					out.println(ContestServer.JOIN+","+nameImg+","+getSimSt().getUserID()+","+getBrController().getLogger().getClassName());
					logger.simStLog(SimStLogger.SSGAME, SimStLogger.RECONNECT_ACTION, "", "", "", duration, rating);
					requestLeaderboard();

					gameShowPlatform.viewMatchup();
					gameShowPlatform.closeProblemInputDialog();
					startMatchupTime = Calendar.getInstance().getTimeInMillis();
					
				} 
			 };
		 }

	
}
