/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class CollaboratorsTest extends TestCase {

	public static Test suite() { 
		TestSuite suite= new TestSuite(CollaboratorsTest.class); 
		return suite;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		trace.addDebugCodes("collab");
	}
	
	protected void tearDown() throws Exception {
		trace.removeDebugCode("collab");
		super.tearDown();
	}

	public void testGetUserids() {
		String className, team;     // classNames, team members
		String[] userids;  // test userids
		int[] position = new int[1];
		Integer[] resultLengths;

		System.out.printf("ClassName: %s\n", className = "Collaborators(fred,mary,hank, Luke Skywalker)");
		userids = new String[] {"fred", "mary", " Hank", "Luke Skywalker ", "not there", "luke "};
		resultLengths = new Integer[] { 4, 4, 4, 4, null, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("ClassName: %s\n", className = "Collaborators at Westinghouse(fred,mary,hank, Luke Skywalker)");
		userids = new String[] {"fred", "mary", " Hank", "Luke Skywalker ", "not there", "luke "};
		resultLengths = new Integer[] { 4, 4, 4, 4, null, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("ClassName: %s\n", className = "Collaborators( fred, mary,hank, Luke Skywalker )");
		userids = new String[] {"fred", "mary", " Hank", "Luke Skywalker ", "not there", "luke "};
		resultLengths = new Integer[] { 4, 4, 4, 4, null, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("ClassName: %s\n", className = "Collaborators(fred,mary,hank, Luke Skywalker"); // missing )
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { null, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("ClassName: %s\n", className = "Collaborators:fred,mary,hank, Luke Skywalker"); // missing (
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { null, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("ClassName: %s\n", className = "Collaborators (fred)"); // single name
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { 1, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("ClassName: %s\n", className = "Collaborators ( )"); // empty list
		userids = new String[] {" ", "not there"};
		resultLengths = new Integer[] { null, null };
		runTestGetUserids(null, className, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = "fred , mary,hank, Luke Skywalker");
		userids = new String[] {"fred", "mary", " Hank", "Luke Skywalker ", "not there", "luke "};
		resultLengths = new Integer[] { 4, 4, 4, 4, null, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = "fred, mary, hank , Luke Skywalker");
		userids = new String[] {"fred", "mary", " Hank", "Luke Skywalker ", "not there", "luke "};
		resultLengths = new Integer[] { 4, 4, 4, 4, null, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = "fred, mary,hank, Luke Skywalker");
		userids = new String[] {"fred", "mary", " Hank", "Luke Skywalker ", "not there", "luke "};
		resultLengths = new Integer[] { 4, 4, 4, 4, null, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = " , fred,mary,hank, Luke Skywalker"); // leading ,
		userids = new String[] {"fred", "MARY ", " hank", "not there"};
		resultLengths = new Integer[] { 4, 4, 4, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = "fred,mary,hank, Luke Skywalker ,"); // trailing ,
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { 4, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = "fred"); // single name
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { 1, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = ",fred"); // single name w/ leading ,
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { 1, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = "fred ,"); // single name w/ trailing ,
		userids = new String[] {"fred", "not there"};
		resultLengths = new Integer[] { 1, null };
		runTestGetUserids(team, null, userids, position, resultLengths);

		System.out.printf("Team: %s\n", team = ""); // empty list
		userids = new String[] {" ", "not there"};
		resultLengths = new Integer[] { null, null };
		runTestGetUserids(team, null, userids, position, resultLengths);
	}

	/**
	 * @param team TODO
	 * @param userids
	 * @param position
	 * @param resultLengths
	 * @param setPrefsMsgs
	 */
	private void runTestGetUserids(String team,
			String className, String[] userids, int[] position, Integer[] resultLengths) {
		for(int i = 0; i < userids.length; ++i) {
			List<String> result = null;
			MessageObject setPrefs = MessageObject.parse(setPrefsMsgs[0]);
			setPrefs.setProperty(Logger.STUDENT_NAME_PROPERTY, userids[i]);
			String label = null;
			if(team != null) {
				setPrefs.setProperty(Logger.STUDY_CONDITION_TYPE+i, "Collaborators");
				setPrefs.setProperty(Logger.STUDY_CONDITION_NAME+i, team);
				label = "\""+team+"\"";
			}
			if(className != null) {
				setPrefs.setProperty(Logger.CLASS_NAME_PROPERTY, className);
				if(label == null)
					label = "\""+className+"\"";
			}
			result = Collaborators.getUserids(setPrefs, position);
			System.out.printf("  %-10s: %s %2d;\n", userids[i], result, position[0]);
			if(resultLengths[i] == null)
				assertTrue(label+", user \""+userids[i]+"\" position < 0", position[0] < 0);
			else {
				assertEquals(label+", user \""+userids[i]+"\" bad length",
						resultLengths[i].intValue(), result.size());
				assertEquals(label+", user \""+userids[i]+"\" bad position",
						i+1, position[0]);
			}
		}
	}

	private static final String schoolName = "My School Name";
	private static final String[] setPrefsMsgs = {
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<message>\n"+
		"  <verb>NotePropertySet</verb>\n"+
		"  <properties>\n"+
		"    <MessageType>"+MsgType.SET_PREFERENCES+"</MessageType>\n"+
		"    <"+Logger.CLASS_NAME_PROPERTY+">CollaboratorsTest0(fred, mary, hank)</"+Logger.CLASS_NAME_PROPERTY+">\n"+
		"    <"+Logger.SCHOOL_NAME_PROPERTY+">"+schoolName+"</"+Logger.SCHOOL_NAME_PROPERTY+">\n"+
		"    <"+Logger.STUDENT_NAME_PROPERTY+">replace with user</"+Logger.STUDENT_NAME_PROPERTY+">\n"+
		"  </properties>\n"+
		"</message>",

		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<message>\n"+
			"  <verb>NotePropertySet</verb>\n"+
			"  <properties>\n"+
			"    <MessageType>"+MsgType.SET_PREFERENCES+"</MessageType>\n"+
			"    <"+Logger.CLASS_NAME_PROPERTY+">CollaboratorsTest1(FRED , Mary, jane)</"+Logger.CLASS_NAME_PROPERTY+">\n"+
			"    <"+Logger.SCHOOL_NAME_PROPERTY+">"+schoolName+"</"+Logger.SCHOOL_NAME_PROPERTY+">\n"+
			"    <"+Logger.STUDENT_NAME_PROPERTY+">replace with user</"+Logger.STUDENT_NAME_PROPERTY+">\n"+
			"  </properties>\n"+
			"</message>"
	};
	private static int guids = 0;
	public void testCreate() {
		final LauncherServer ls = new LauncherServer();
		final String[][] userids = { { "fred", "mary", "hank" }, { "Mary", "FRED ", "jane" } };
		class create implements Runnable {
			private final MessageObject setPrefs;
			private final String userid;
			private final int problemCount;  // number of problems to run in the session
			create(String setPrefs, String userid, int nProblems) {
				this.setPrefs = MessageObject.parse(setPrefs);
				this.setPrefs.setProperty(Logger.STUDENT_NAME_PROPERTY, userid);
				this.userid = userid;
				this.problemCount = nProblems;
			}
			public synchronized void run() {
				LauncherServer.Session session = ls.new Session(Integer.toString(++guids), userid);
				session.setTeam(Collaborators.getUserids(setPrefs, null).toString());
				session.setSchoolName(schoolName);
				setPrefs.setProperty(Logger.STUDENT_NAME_PROPERTY, userid);
				for(int p = 0; p < problemCount; ++p) {
					try {
						Collaborators.create(session, setPrefs);
					} catch(Exception e) {
						e.printStackTrace();
						fail("exception from create-"+userid+": "+e);
					} finally {
						notify();
					}

					System.out.printf("... Now running problem %d in Thread %s: session %s\n",
							p, Thread.currentThread().toString(), session.toString());
					// wait until monitor has checked us
					synchronized(CollaboratorsTest.this) {
						try {
							CollaboratorsTest.this.wait();
						} catch(InterruptedException ie) {
							trace.err("exception during wait() for main thread");
						}
					}

					if(trace.getDebugCode("collab"))
						trace.out("collab", "About to call Collaborators.remove("+session+")");
					Collaborators.remove(session);
				}
			}
		}
		List<Thread> threads = new LinkedList<Thread>();
		for(int i = 0; i < setPrefsMsgs.length; ++i) {
			for(String userid : userids[i]) {
				// className index + 1 is number of problems to run in the session
				Thread t = new Thread(new create(setPrefsMsgs[i], userid, i+1), "create-"+i+"-"+userid);
				threads.add(t);
				t.start();
//				System.out.print("\nEnter to continue...\n");   uncomment to prompt at start of each thread
//				try { System.in.read(); } catch (IOException e) {}
			}				
		}
		
		Collaborators.All allCollabs = ls.getAllCollaborators();
		for(int p = 0; p < setPrefsMsgs.length; ++p) {

			for(Thread t : threads) {
				if(!(t.isAlive()))
					continue;
				synchronized(t) {
					try {
						t.wait(2000);
					} catch(InterruptedException ie) {
						trace.err("exception during wait() for thread "+t);
					}
				}
			}

			if(trace.getDebugCode("collab"))
				trace.out("collab", "allCollabs:\n  "+allCollabs+"\n");

			assertEquals("wrong number of collaboration lists", setPrefsMsgs.length-p, allCollabs.size());
			for(int i = p; i < setPrefsMsgs.length; ++i) {
				List<String> userNames = Collaborators.getUserids(MessageObject.parse(setPrefsMsgs[i]), null);
				String listKey = Collaborators.makeKeyWithNamespace(schoolName, userNames.toString());
				List<Collaborators> collabsList;
				assertNotNull("could not find list for "+listKey, collabsList = allCollabs.get(listKey));
				assertEquals("wrong size for listKey "+listKey, 1, collabsList.size());
				Collaborators collabs = collabsList.get(0);
				if(trace.getDebugCode("collab"))
					trace.out("collab", "Instance for "+listKey+"[0]:\n  "+collabs+"\n");
				assertEquals("wrong state for "+listKey+"[0]", Collaborators.State.Running, collabs.getState());
				for(int j = 0; j < userids[i].length; ++j) {
					String userid = userids[i][j].trim().toLowerCase();
					Collaborators.Collaborator collab = collabs.getCollaborator(userid);
					assertNotNull("no Collaborator for team ("+userNames+"), userid "+userid, collab);
					assertEquals("bad userid in Collaborator for team "+userNames+", userid "+userid,
							userid, collab.getUserid());

				}
			}
			synchronized(this) {
				notifyAll();
			}
		}
		
		for(Thread t : threads) {   // cleanup: wait for all sessions to exit
			try {
				t.join(2000);
			} catch(InterruptedException ie) {
				trace.err("exception during join() for thread "+t);
			}
		}
		assertEquals("allCollabs not empty", 0, allCollabs.size());
	}
}
