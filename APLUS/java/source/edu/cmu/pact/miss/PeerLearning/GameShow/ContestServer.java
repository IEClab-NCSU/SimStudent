/**
 * 
 */
package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;



public class ContestServer implements TimeoutRecovery{

	SimSt simSt;
	ServerSocket serverSocket;
	Socket socket1, socket2;
	
	private int port = 4444;
	
	protected SortedSet<Connection> connections;
	private List<ContestOrganizer> contests;
	private Connection waitingForMatch = null;
	
	private Timer challengeRequestResponse;

	public static final String JOIN = "JoinProgram";
	public static final String REQUEST_CONTEST = "RequestContest";
	public static final String SOLUTION = "SolutionMade";
	public static final String REQUEST_NEXT = "RequestNextProblem";
	public static final String REQUEST_UPDATE = "RequestUpdate";
	public static final String LEAVE = "LeaveProgram";
	public static final String LIST = "ListAvailable";
	public static final String START_CONTEST = "StartContest";
	public static final String START_PROBLEM = "StartProblem";
	public static final String ASSESSED = "SolutionAssessed";
	public static final String END_CONTEST = "EndContest";
	public static final String SUBMIT_PROBLEMS = "SubmitProblems";
	public static final String AGREE_CONTEST = "AgreeContest";
	public static final String CONTEST_REQUESTED = "ContestRequested";
	public static final String CONTEST_AGREED = "ContestAgreed";
	public static final String REQUEST_PROBLEM = "RequestProblem";
	public static final String CHAT_PRIVATE_MESSAGE = "ChatPrivateMessage";
	public static final String CHAT_GROUP_MESSAGE = "ChatGroupMessage";
	public static final String ANNOUNCE_MESSAGE = "AnnounceMessage";
	public static final String ANNOUNCE_PRIVATE_MESSAGE = "AnnouncePrivateMessage";
	public static final String REQUEST_PROBLEM_BANK = "RequestProblemBank";
	public static final String PROBLEM_BANK_LIST = "ProblemBankList";
	public static final String DUPLICATE_USERID = "DuplicateUserID";
	public static final String FORFEIT = "GameForfeited";
	public static final String CANCEL = "GameCancelled";
	public static final String LEADERBOARD = "Leaderboard";
	public static final String OUTSTANDING = "Outstanding";
	
	public static final String GAME_START_MSG = "$1 and $2 have just started a game.";
	public static final String GAME_RETURN_MSG = "$1 has returned from a game.";
	public static final String JOIN_MSG = "$1 has joined.";
	public static final String LEAVE_MSG = "$1 has left.";
	public static final String WIN_MSG = "$1 just won in a game against $2.";
	public static final String TIE_MSG = "$1 and $2's game just ended in a tie.";
	public static final String FORFEIT_MSG = "$2 forfeited a game to $1.";
	public static final String OUTSTAND_ME_MSG = "You already have an outstanding challenge.";
	public static final String OUTSTAND_MSG = "$1 already has an outstanding challenge.";
	
        public static final String PROBLEM_REQUEST_EXIT = "ProblemRequestExited";
	public static final String PROBLEM_REQUEST_TIMEOUT = "ProblemRequestTimedOut";
	public static final String CHALLENGE_REQUEST_TIMEOUT = "ChallengeRequestTimedOut";
	public static final String PROBLEM_ANSWER_TIMEOUT = "ProblemAnswerTimedOut";
	
        public static final String PROBLEM_REQUEST_EXIT_MSG = "** Request to provide problem was exited.  A random problem will be used instead. **";
	public static final String PROBLEM_REQUEST_TIMEOUT_MSG = "** Request to provide problem timed out.  A random problem will be used instead. **";
	public static final String CHALLENGE_REQUEST_TIMEOUT_MSG = "** Challenge request from $1 timed out.  It has been declined for you. **";
	public static final String CHALLENGE_NOT_CURRENT_MSG = "This challenge is no longer current.";
	

	public static final int CHALLENGE_TIMEOUT_TIME = 60000;
	public static final int PROBLEM_REQUEST_TIMEOUT_TIME = 60000;
	public static final int PROBLEM_SOLVE_TIMEOUT_TIME = 120000;
	
	public static final String COMPETITOR_FILE = "competitors.csv";
	public static final String PROBLEM_FILE = "problemStats.csv";
	
    static Hashtable<String, Competitor> competitors = new Hashtable<String, Competitor>();
    static Hashtable<String, ProblemType> problemStatistics = new Hashtable<String, ProblemType>();
	
    //Basic server start-up constructors
	public ContestServer(SimSt ss)
	{
		simSt = ss;
		runServer();
		
	}
	
	public ContestServer()
	{
		runServer();
		
	}

	public ContestServer(int portNum)
	{
		port = portNum;
		runServer();
		
	}
	
	//Start up a server on the given port
	public void runServer()
	{
	
    	
		try {
		    serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			//If can't listen to the port, throw an error msg & end program 
		    System.out.println("Could not listen on port: "+port);
		    System.exit(-1);
		}

		//
		Comparator<Connection> comp = new Comparator<Connection>()
		{
			@Override
			public int compare(Connection arg0, Connection arg1) {
				if(arg0 == null || arg1 == null)
					return -1;
				int one = competitors.get(arg0.userid).rating;
				int two = competitors.get(arg1.userid).rating;
				if(one < two) return -1;
				if( one == two)
				{
					int comps = arg0.name.compareTo(arg1.name);
					if(comps != 0)
						return comps;
					return arg0.userid.compareTo(arg1.userid);
				}
				return 1;
			}
		};
		connections = Collections.synchronizedSortedSet(new TreeSet<Connection>(comp));
		contests = new LinkedList<ContestOrganizer>();
		
		
		//reload competitor stats from file
		readCompetitorFile();
		
		//reload problem stats from file
		readProblemStatisticFile();
		
		System.out.println("Starting a new Thread AcceptThread: " + Thread.currentThread());
		new Thread(new AcceptThread()).start();
		System.out.println("Started a new Thread AcceptThread: " + Thread.currentThread());
	}
	

    class AcceptThread extends Thread
    {
    	public void run() {

    		//continuing receiving new connections while running
    		while(true)
    		{
    			System.out.println("Calling acceptConnection AcceptThread running: " + Thread.currentThread());
    			acceptConnection();
   				System.out.println("Called acceptConnection AcceptThread running: " + Thread.currentThread());
    		}
    	}
    }
	
	    
	/*
	 * Class to handle threaded receiving of messages separate from one another
	 */
	class ListenerThread extends Thread
    {
		//while true, keep listening for more messages, if false, no longer active
		boolean activeListener = true;
		Connection connection;
		ListenerThread(Connection connect)
		{
			connection = connect;
		}
    	public void run() {
    		String incomingMsg = "";
			try {
				//Loop through receiving messages & perform activities as dictated
				while(activeListener && (incomingMsg = connection.reader.readLine()) != null)
				{
				    System.out.println("Client (server): " + incomingMsg);
				    if(incomingMsg.startsWith(JOIN))
				    	joinProgram(incomingMsg);
				    else if(incomingMsg.startsWith(LEAVE))
				    	leaveProgram(incomingMsg);
				    else if(incomingMsg.startsWith(REQUEST_CONTEST))
				    	requestContest(incomingMsg);
				    else if(incomingMsg.startsWith(AGREE_CONTEST))
				    	agreeContest(incomingMsg);
				    else if(incomingMsg.startsWith(ContestServer.CHAT_GROUP_MESSAGE))
				    	sendChatMessage(incomingMsg);
				    else if(incomingMsg.startsWith(LEADERBOARD))
				    	sendLeaderboard(incomingMsg);
				    else  //if invalid message, process connection as leaving
				    	leaveProgram(incomingMsg);
				    trace.out("ss-gameshow", "Listening to "+connection.name);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
    	}
    	
    	private void sendLeaderboard(String incomingMsg) {
			String[] args = incomingMsg.split(",");
			String classname = "";
			if(args.length > 1)
				classname = args[1];
			
			Comparator<Competitor> comp = new Comparator<Competitor>()
			{
				@Override
				public int compare(Competitor arg0, Competitor arg1) {
					int one = arg0.rating;
					int two = arg1.rating;
					if(one < two) return -1;
					if( one == two)
					{
						int comps = arg0.name.compareTo(arg1.name);
						if(comps != 0)
							return comps;
						return arg0.userid.compareTo(arg1.userid);
					}
					return 1;
				}
			};
			SortedSet<Competitor> leaderList = Collections.synchronizedSortedSet(new TreeSet<Competitor>(comp));
			
			for(Competitor c:competitors.values())
			{
				if(c.classroom.equals(classname)){
					leaderList.add(c);
				}
			}
				
			String list = LEADERBOARD;
			String spaces = "........................................";
			int listCount = 0;
			while(leaderList.size() > 0 && listCount < 10)
			{
	        	listCount++;
				Competitor c = leaderList.last();
	        	String s = c.name+c.rating;
	        	if(s.length() < 20)
	        		s = c.name+spaces.substring(0,20-s.length())+c.rating;
	        	else
	        		s = c.name+spaces.substring(0,1)+c.rating;
	        	list += ","+listCount+". "+s;
	        	leaderList.remove(c);
			}
			
			
			Calendar cal = Calendar.getInstance();
	    	String now;
	    	if(cal.get(Calendar.MINUTE) < 10)
	        	now = cal.get(Calendar.HOUR)+":0"+cal.get(Calendar.MINUTE);
	    	else
	    		now = cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE);
	    	if(now.startsWith("0:"))
	    		now = now.replace("0:", "12:");
			list += ",(Updated: "+now+")";
			connection.writer.println(list);
			System.out.println(connection.userid+": "+list);
		}
    	
    	
		/*
    	 * Process a chat message: take the message, add the name onto it, and
    	 * send to all active connections not currently in games
    	 * Message received format is: CHAT_GROUP_MESSAGE,message
    	 */
    	public void sendChatMessage(String incomingMsg) {
			String chat = incomingMsg.substring(incomingMsg.indexOf(',')+1);
			chat = connection.name+"'s Tutor: "+chat;
			
			for(Connection connect:connections)
			{
				connect.writer.println(ContestServer.CHAT_GROUP_MESSAGE+","+chat);
				System.out.println(connect.userid+":"+ContestServer.CHAT_GROUP_MESSAGE+","+chat);
			}
		}

    	/*
    	 * Process a join message:  ensure the data from the person joining is stored
    	 * in the list of competitors or create a new object to add in if not there
    	 * Message received format is: JOIN,name,image,userID
    	 */
		protected void joinProgram(String args)
    	{
    		String[] argList = args.split(",");
    		String id = "";
    		String classname = "";
    		//If userID or image arguments are not present, don't fill in
    		boolean stat = false;
    		if(argList.length > 5 && argList[5].equals("static"))
    			stat = true;
    		if(argList.length > 3)
    			id = argList[3];
    		if(argList.length > 4)
    			classname = argList[4];
    		if(argList.length > 2)
    			connection.setImage(argList[2]);
    		if(argList.length > 1)
    		{
    			connection.setName(argList[1]);
    			connection.setUserID(id);
    			//If no record of this competitor, create new and add to list
    			if(!competitors.containsKey(connection.userid))
    			{
    				Competitor compete = new Competitor(connection.name,connection.img, id);
    				compete.stat = stat;
    				compete.classroom = classname;
    				competitors.put(compete.userid, compete);
    			}
    			else
    			{
    				//If record of this competitor, update it
    				Competitor compete = competitors.get(connection.userid);
    				if(connection.name != null)
    					compete.name = connection.name;
    				if(connection.img != null)
    					compete.img = connection.img;
    				//Name can only be used by one userID
    				if(id.length() > 0 && compete.userid.length() > 0 && !id.equals(compete.userid))
    				{
    					trace.err("Same student name between different students!");
    				} else if(!id.equals("") && compete.userid.equals(""))
    					compete.userid = id;

    				compete.stat = stat;
    			}
    			//announce joining
    			sendAnnounceMessage(GameShowUtilities.replacePiece(JOIN_MSG, connection.name));
    		}
    		Connection remove = null;
    		for(Connection c:connections)
    		{
    			if(c.userid.equals(connection.userid))
    			{
    				remove = c;
    			}
    		}
    		if(remove != null)
    		{
				remove.writer.println(DUPLICATE_USERID);
				System.out.println(remove.userid+":"+DUPLICATE_USERID);
				connections.remove(remove);
    		}
    		connection.valid = true;
    		connections.add(connection);
    		//send updated list to all
    		reportConnections();
    	}
		
		/*
		 * Process leave message: send a response message which informs the contestant
		 * that they have been removed and stop listening at the socket
		 * Message received format is: LEAVE
		 * Arguments are ignored
		 */
    	protected void leaveProgram(String args)
    	{
    		connection.writer.println(LEAVE);
    		System.out.println(connection.userid+":"+LEAVE);
    		//give the contestant time to close their own connection before dropping them
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			//remove from the active connection list and close the socket
    	    connections.remove(connection);
    	    connection.writer.close();
    	    try {
    	    	connection.reader.close();
				connection.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//stop listening at socket
			activeListener = false;
			
			if(connection.valid)
			{
				//announce leaving and send updated list to all others
				sendAnnounceMessage(GameShowUtilities.replacePiece(LEAVE_MSG, connection.name));
	    		reportConnections();
			}
    	}
    	
    	/*
    	 * Process contest request message: if a specific challengee, send the challenge,
    	 * if not, arrange a random challenge
    	 * Message received format is: REQUEST_CONTEST,challengee
    	 * Challengee is optional
    	 */
    	protected void requestContest(String args)
    	{
    	    String[] challengee = args.split(",");
    	    boolean result = false;
    	    if(challengee.length > 1)
    	    {
    	    	//challengee argument is present
    	    	result = arrangeContest(connection, challengee[1]);
    	    }
    	    else
    	    {
    	    	//random contest
    	    	arrangeContest(connection);
    	    }
    	    
    	    if(result)
    	    	activeListener = false;
    	}
    	

    	/*
    	 * Process agree contest message: send to be processed as accepted or refused
    	 * contest based on argument given
    	 * Message received format is: AGREE_CONTEST,challengee_name,true/false
    	 */
    	private void agreeContest(String args)
    	{
    	    String[] challengee = args.split(",");
    	    if(challengeRequestResponse != null)
    	    	challengeRequestResponse.stop();
    	    if(challengee.length > 2 && challengee[2].equals("false"))
    	    {
    	    	refuseContest(connection, challengee[1]);
    	    }
    	    else
    	    {
    	    	acceptContest(connection, challengee[1]);
    	    	activeListener = false;
    	    }
    	    //activeListener = false;
    	}
    	
    }
	
	
	/*
	 * Arrange a random contest.  If none has already been requested, put this connection
	 * on a list to wait, and if one has, match them up and start the game
	 */
	private void arrangeContest(Connection connection)
	{
		//No one is waiting for a game
		if(waitingForMatch == null)
			waitingForMatch = connection;
		else
		{
			//someone is waiting for a game.
			Connection contestant1 = waitingForMatch;
			//with them matched with this request, none are waiting again
			waitingForMatch = null;
			Connection contestant2 = connection;
			//create object to handle contest
			ContestOrganizer organizer = new ContestOrganizer(this, contestant1, contestant2);
			contests.add(organizer);
			//contestants that are starting game are no longer active for matchups
			connections.remove(contestant1);
			connections.remove(contestant2);
			//announce game started & update list of active
			sendAnnounceMessage(
					GameShowUtilities.replaceTwoPieces(GAME_START_MSG, contestant1.name, contestant2.name));
			reportConnections();
		}
	}
	
	/*
	 * Arrange a contest with a specific desired contestant: inform desired contestant
	 * that a contest has been requested
	 */
	private boolean arrangeContest(Connection connection, String challengee)
	{
		if(connection.hasOutstandingChallenge())
		{
			//TODO :  You can't make a challenge when you have one outstanding! 
			System.out.println(connection.name+" was outstanding - abort!");
			connection.writer.println(OUTSTANDING+","+OUTSTAND_ME_MSG+",self");
			return false;
		}
		//go through all active connections
		//for(int i=0;i<connections.size();i++)
		for(Connection challengeConnection:connections)
		{
			//Connection challengeConnection = connections.get(i);
			//when name matches the name of desired contestant, send request message to
			//that contestant
			if(challengeConnection.userid.equals(challengee))
			{
				if(challengeConnection.hasOutstandingChallenge())
				{
					System.out.println(challengeConnection.name+" was outstanding - abort!");
					connection.writer.println(OUTSTANDING+","+GameShowUtilities.replacePiece(OUTSTAND_MSG, challengeConnection.name)+","+challengeConnection.userid);
					return false;
				}
				connection.setOutstandingChallenge(true);
				challengeConnection.setOutstandingChallenge(true);
				
				challengeConnection.writer.println(CONTEST_REQUESTED+","+connection.userid);
				System.out.println(challengeConnection.userid+":"+CONTEST_REQUESTED+","+connection.userid);
				challengeRequestResponse = new Timer(CHALLENGE_TIMEOUT_TIME, new TimeoutDelay(this, ContestServer.CHALLENGE_REQUEST_TIMEOUT,challengeConnection,connection));
				challengeRequestResponse.setRepeats(false);
				challengeRequestResponse.start();
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Inform a challenger that their contest challenge has been refused
	 */
	public void refuseContest(Connection connection, String challenger)
	{
		//go through all active connections
		//for(int i=0;i<connections.size();i++)
		for(Connection challengeConnection:connections)
		{
			//Connection challengeConnection = connections.get(i);
			//when name matches the name of challenger who made request, send message to
			//that contestant
			if(challengeConnection.userid.equals(challenger))
			{
				connection.setOutstandingChallenge(false);
				challengeConnection.setOutstandingChallenge(false);
				
				challengeConnection.writer.println(CONTEST_AGREED+","+connection.userid+",false");
				System.out.println(challengeConnection.userid+":"+CONTEST_AGREED+","+connection.userid+",false");

				new Thread(new ListenerThread(challengeConnection)).start();
				return;
			}
		}
	}
	
	/*
	 * Inform a challenger that their contest challenge has been accepted & 
	 * set up game
	 */
	public void acceptContest(Connection connection, String challenger) {
		//go through all active connections
		//for(int i=0;i<connections.size();i++)
		for(Connection challengeConnection:connections)
		{
			//Connection challengeConnection = connections.get(i);
			//when name matches the name of challenger who made request, send message to
			//that contestant & start up the game between them
			if(challengeConnection.userid.equals(challenger))
			{
				connection.setOutstandingChallenge(false);
				challengeConnection.setOutstandingChallenge(false);
				
				challengeConnection.writer.println(CONTEST_AGREED+","+connection.userid+",true");
				System.out.println(challengeConnection.userid+":"+CONTEST_AGREED+","+connection.userid+",true");
				Connection contestant1 = challengeConnection;
				Connection contestant2 = connection;
				//create object to organize game
				ContestOrganizer organizer = new ContestOrganizer(this, contestant1, contestant2);
				contests.add(organizer);
				//contestants are no longer active in matchup window
				connections.remove(contestant1);
				connections.remove(contestant2);
				//send message to challenger
				//announce to all active that game has started & update list of active connections
				sendAnnounceMessage(
						GameShowUtilities.replaceTwoPieces(GAME_START_MSG, contestant1.name, contestant2.name));
				reportConnections();
				return;
			}
		}
		
	}
	
	public static String requestProblemBank()
	{
		String bank = "";
		String[] examples = GameShowUtilities.generateExamples();
		for(int i=0;i<examples.length;i++)
		{
			String pattern = GameShowUtilities.determinePattern(examples[i]);
			ProblemType type = problemStatistics.get(pattern);
			if(type != null)
			{
				int pct = (int)((double)type.successCount*100/type.attemptCount);
				String diff = (5-((pct-1)/20))+"-star.png"; 
				//bank += examples[i]+";"+type.attemptCount+";"+type.successCount+";"+pct+"%;"+diff+",";
				bank += examples[i]+";"+type.attemptCount+";"+diff+",";
			}
			//else
			//	bank += examples[i]+";No Data;No Data;No Data,";
		}
		return bank;
	}

	/*
	 * Accept a new connection to the server
	 */
	private void acceptConnection()
	{
		try {
			System.out.println("Enter in acceptConnection: " + Thread.currentThread());
			//create a connection object for the new connection
		    Socket socket = serverSocket.accept();
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
			Connection connection = new Connection(socket,out,in);
		    //connections.add(connection);

			//begin listening to it, though not added as an active connection until it
			//sends a join message
			new Thread(new ListenerThread(connection)).start();
			System.out.println("Exit from acceptConnection: " + Thread.currentThread());
			
		} catch (IOException e) {
		    System.out.println("Accept failed: 4444");
		    System.exit(-1);
		}
	}
	
	/*
	 * A connection is returned back to the contest server after it was listened to by,
	 * for example, a contest organizer
	 */
	public synchronized void returnConnection(Connection connect)
	{
		
		Connection remove = null;
		for(Connection c:connections)
		{
			if(c.userid.equals(connect.userid))
			{
				remove = connect;
			}
		}
		if(remove != null)
		{
			remove.writer.println(DUPLICATE_USERID);
			System.out.println(remove.userid+":"+DUPLICATE_USERID);
			connections.remove(remove);
			return;
		}
		
		//announce return to all active connections
		sendAnnounceMessage(GameShowUtilities.replacePiece(GAME_RETURN_MSG, connect.name));
		//put the connection back on the list of active connections
		connections.add(connect);
		//startup a thread to listen to it again
		new Thread(new ListenerThread(connect)).start();
		//send updated list of active connections
		reportConnections();
	}
	
	/*
	 * Send updated list of active connections
	 */
	private void reportConnections()
	{
		String report = LIST+",";
		//Put together a connection summary for each connection including name, image, rating, and counts
		for(Connection connect:connections)
		{
			report += connect.name+";"+connect.img+";"+competitors.get(connect.userid).rating+";"
			+competitors.get(connect.userid).wins+";"+competitors.get(connect.userid).losses+";"
			+competitors.get(connect.userid).ties+";"+connect.userid+",";
		}
		//Send all of the connection summaries to each active connection
		for(Connection connect:connections)
		{
			connect.writer.println(report);
			System.out.println(connect.userid+":"+report);
		}
		//Update the details in the connection file for later retrieval
		writeCompetitorFile();
	}
	

	public static void updateProblemStatistics(String problem, int attempts, int successes)
	{
		String problemType = GameShowUtilities.determinePattern(problem);
		
		if(problemStatistics.containsKey(problemType))
		{
			ProblemType stats = problemStatistics.get(problemType);
			stats.addAttempts(attempts, successes);
		}
		else
		{
			problemStatistics.put(problemType, new ProblemType(problemType, attempts,successes));
		}
		writeProblemStatisticFile();
	}
	
	/*
	 * Write file to track name, image, rating, counts and userID for all competitors which have 
	 * used the system 
	 */
	private synchronized static void writeProblemStatisticFile()
	{
		try
		{
			
			FileWriter f = new FileWriter(new File(PROBLEM_FILE)); 

			for(String c:problemStatistics.keySet())
			{
				f.write(problemStatistics.get(c)+"\n");
			}
			
			f.flush();
			f.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Read the file tracking results of each different type of problem into
	 * the hashtable for reference
	 */
	private synchronized static void readProblemStatisticFile()
	{
		try
		{
			File file = new File(PROBLEM_FILE);
			if(!file.exists())
				return;
			BufferedReader f = new BufferedReader(new FileReader(file)); 

			String line = f.readLine();
			
			while(line != null && !line.equals("-1"))
			{
				String[] problemDetails = line.split(",");
				int attempts = Integer.parseInt(problemDetails[1]);
				int successes = Integer.parseInt(problemDetails[2]);
				ProblemType problem = new ProblemType(problemDetails[0],attempts,successes);
				
				problemStatistics.put(problemDetails[0], problem);
				line = f.readLine();
			}
			
			f.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Write file to track name, image, rating, counts and userID for all competitors which have 
	 * used the system 
	 */
	private synchronized static void writeCompetitorFile()
	{
		try
		{
			
			FileWriter f = new FileWriter(new File(COMPETITOR_FILE)); 

			for(String c:competitors.keySet())
			{
				f.write(competitors.get(c)+"\n");
			}
			
			f.flush();
			f.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Read the file tracking all competitors with their name, image, rating, counts and userID into
	 * the hashtable for reference
	 */
	private synchronized static void readCompetitorFile()
	{
		try
		{
			File file = new File(COMPETITOR_FILE);
			if(!file.exists())
				return;
			BufferedReader f = new BufferedReader(new FileReader(file)); 

			String line = f.readLine();
			
			while(line != null && !line.equals("-1"))
			{
				String[] competitorDetails = line.split(",");
				Competitor compete = new Competitor(competitorDetails[0],competitorDetails[1],competitorDetails[6]);
				compete.rating = Integer.parseInt(competitorDetails[2]);
				compete.wins = Integer.parseInt(competitorDetails[3]);
				compete.losses = Integer.parseInt(competitorDetails[4]);
				compete.ties = Integer.parseInt(competitorDetails[5]);
				compete.classroom = competitorDetails[7];
				competitors.put(compete.userid, compete);
				line = f.readLine();
			}
			
			f.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Send a message from the server to all active connections. Server announcement messages
	 * are surrounded by **'s to set them off.
	 */
	public void sendAnnounceMessage(String message) {
		
		message = "** "+message+" **";
		for(Connection connect:connections)
		{
			connect.writer.println(ContestServer.ANNOUNCE_MESSAGE+","+message);
			System.out.println(connect.userid+":"+ContestServer.ANNOUNCE_MESSAGE+","+message);
		}
	}
	
	/*
	 * Send an announcement to all active for a win of winner against loser
	 */
	public void announceWin(String winner, String loser)
	{
		sendAnnounceMessage(GameShowUtilities.replaceTwoPieces(WIN_MSG, winner, loser));
	}
	

	/*
	 * Send an announcement to all active for forfeiter forfeiting to forfeitee
	 */
	public void announceForfeit(String forfeitee, String forfeiter)
	{
		sendAnnounceMessage(GameShowUtilities.replaceTwoPieces(FORFEIT_MSG, forfeitee, forfeiter));
	}
	
	/*
	 * Send an announcement to all active of a tie between contestant1 and contestant2
	 */
	public void announceTie(String contestant1, String contestant2)
	{
		sendAnnounceMessage(GameShowUtilities.replaceTwoPieces(TIE_MSG, contestant1, contestant2));
	}
	
	/*
	 * Update the competitor ratings in the hashtable for a tie.  This does not change
	 * the ratings, only increments count of ties for each
	 */
	public static void updateRatingTie(String one, String two)
	{
		//Competitor compOne = competitors.get(one);
		//Competitor compTwo = competitors.get(two);
		Competitor compOne = findCompetitor(one);
		Competitor compTwo = findCompetitor(two);
		
		//If either competitor is somehow not in the hashtable, create a default entry for them
		if(compOne == null)
		{
			compOne = new Competitor(one,"",one);
		}

		if(compTwo == null)
		{
			compTwo = new Competitor(two,"",two);
		}
		
		Competitor lower = compOne;
		Competitor higher = compTwo;
		if(compOne.rating > compTwo.rating)
		{
			lower = compTwo;
			higher = compOne;
		}
		
		//Calculate expected chance of each winning
		int diff = lower.rating - higher.rating;
		
		double lowChanceWin = 1.0 / (1.0+Math.pow(10,(-diff/50.0)));
		
		//Compute new score based on expectations of actual outcome (half of increase that a true win would give)
		double newScoreLower = lower.rating + 8*(1-lowChanceWin);
		
		//If ratings fall outside of [1,100] range, place them back in
		if(newScoreLower > 100) newScoreLower = 100;
		
		//update rating only if the increase would not exceed the higher scorers' 
		if(!lower.stat && newScoreLower < higher.rating)
			lower.rating = (int) newScoreLower;
		else
			lower.rating = higher.rating;
		
		
		//increment ties
		compOne.ties++;
		compTwo.ties++;
		
		//write the updated file out
		writeCompetitorFile();
	}
	
	private static Competitor findCompetitor(String userid)
	{
		for(Competitor check:competitors.values())
		{
			if(check.userid.equals(userid))
				return check;
		}
		return null;
	}
	
	/*
	 * Update the competitor ratings in the hashtable for a win.  The rating increases
	 * for the winner and decreases for the loser.  The amount of increase/decrease depends
	 * on the expected outcome based on previous ratings.
	 * 
	 * If winner was previously higher than loser, increase/decrease is slight.
	 * If loser was previously higher than winner, increase/decrease is great.
	 * If winner & loser were previously similar, increase/decrease is moderate.
	 * 
	 * Based on ELO chess rating system.  Implementation is based on 
	 * http://www.wowwiki.com/Arena_PvP_system [Arena Rating System, 12/22/2010]
	 */
	public static void updateRating(String winner, String loser)
	{
		//Competitor win = competitors.get(winner);
		//Competitor lose = competitors.get(loser);
		Competitor win = findCompetitor(winner);
		Competitor lose = findCompetitor(loser);

		//If either competitor is somehow not in the hashtable, create a default entry for them
		if(win == null)
		{
			win = new Competitor(winner,"",winner);
		}

		if(lose == null)
		{
			lose = new Competitor(loser,"",loser);
		}
		
		//Calculate expected chance of each winning
		int diff = win.rating - lose.rating;
		
		double winChanceWin = 1.0 / (1.0+Math.pow(10,(-diff/50.0)));
		double loseChanceWin = 1.0 / (1.0+Math.pow(10,(diff/50.0)));
		
		//Compute new score based on expectations of actual outcome
		double newScoreWinner = win.rating + 16*(1-winChanceWin);
		double newScoreLoser = lose.rating + 16*(0-loseChanceWin);
		
		//If ratings fall outside of [1,100] range, place them back in
		if(newScoreWinner > 100) newScoreWinner = 100;
		if(newScoreLoser < 1) newScoreLoser = 1;
		
		//update rating
		if(!win.stat)
			win.rating = (int) newScoreWinner;
		if(!lose.stat)
		lose.rating = (int) newScoreLoser;
		
		//increment win/loss counter
		win.wins++;
		lose.losses++;
		
		//write the updated file out
		writeCompetitorFile();
	}
	
	public static int projectWin(int winnerRating, int loserRating)
	{
				
		//Calculate expected chance of each winning
		int diff = winnerRating - loserRating;
		
		double winChanceWin = 1.0 / (1.0+Math.pow(10,(-diff/50.0)));
		
		//Compute new score based on expectations of actual outcome
		double newScoreWinner = winnerRating + 16*(1-winChanceWin);
		
		//If ratings fall outside of [1,100] range, place them back in
		if(newScoreWinner > 100) newScoreWinner = 100;
		
		return (int) newScoreWinner;
		
	}
	
	public static int projectLoss(int loserRating, int winnerRating)
	{
				
		//Calculate expected chance of each winning
		int diff = winnerRating - loserRating;
		
		double loseChanceWin = 1.0 / (1.0+Math.pow(10,(diff/50.0)));
		
		//Compute new score based on expectations of actual outcome
		double newScoreLoser = loserRating + 16*(0-loseChanceWin);
		
		//If ratings fall outside of [1,100] range, place them back in
		if(newScoreLoser < 1) newScoreLoser = 1;
		
		return (int) newScoreLoser;
		
	}

	
	public static void main(String[] args)
	{
		/*String[] examples = GameShowUtilities.generateExamples();
		for(int i=0;i<examples.length;i++)
		{
			System.out.println(examples[i]+" "+GameShowUtilities.determinePattern(examples[i]));
		}*/
		if(args.length > 0)
		{
			System.out.println("Port "+args[0]);
			new ContestServer(Integer.parseInt(args[0]));
		}
		else
			new ContestServer();
	}

	@Override
	public void timeoutRecovery(String timeoutType, Connection connection1,
			Connection connection2) {
		if(timeoutType.equals(CHALLENGE_REQUEST_TIMEOUT))
		{
			connection1.setOutstandingChallenge(false);
			
			connection1.writer.println(CHALLENGE_REQUEST_TIMEOUT+","+connection2.userid+","+connection2.name);
			System.out.println(connection1.userid+":"+CHALLENGE_REQUEST_TIMEOUT+","+connection2.userid);
			if(connection2 != null)
			{
				connection2.setOutstandingChallenge(false);
				
				connection2.writer.println(CONTEST_AGREED+","+connection1.userid+",false");
				System.out.println(connection2.userid+":"+CONTEST_AGREED+","+connection1.userid+",false");
				new Thread(new ListenerThread(connection2)).start();
			}
		}
		
	}
	
}
