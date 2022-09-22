/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.TutoringService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import pact.CommWidgets.RemoteProxy;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.SocketProxy.ActionHandler;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.TutoringService.TSLauncherServer.Session;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * Supports collaborating tutors. An instance of this class is shared among all collaborating
 * {@link LauncherServer.Session} instances. 
 */
public class Collaborators {
	
	/**
	 * Thrown when too few collaborating sessions are present.
	 */
	public static class NotReadyException extends Exception {

		/** Avoid warning due to Serializable. */
		private static final long serialVersionUID = 20130212L;

		/** List of school-userid of collaborators not present. */
		public final List<String> absences;

		/**
		 * @param message for {@link Exception#Exception(String)}
		 * @param absences for {@link #absences}
		 */
		public NotReadyException(String message, List<String> absences) {
			super(message);
			this.absences = absences;
		}
	}

	/**
	 * To represent the operational states of an instance. 
	 */
	enum State {
		New, Awaiting, Running, Finishing
	}
	
	/**
	 * Aggregation of all {@link Collaborators} instances, indexed by "<i>school-name class-name</i>".
	 */
	public static class All extends HashMap<String, List<Collaborators>> {

		/** Avoid warning due to Serializable. */
		private static final long serialVersionUID = 20130212L;

		/**
		 * Find or create an active {@link Collaborators} instance for the given key and insert into
		 * it a new collaborator's session info.
		 * @param schoolName namespace for key
		 * @param userIDs names of collaborators
		 * @param mySession session info for new collaborator
		 * @param myPosition ordinal position of new user among collaborators
		 * @return instance created or updated
		 */
		synchronized Collaborators add(String schoolName, List<String> userIDs,
				LauncherServer.Session mySession, int myPosition) {
			String key = makeKeyWithNamespace(schoolName, userIDs.toString());
			List<Collaborators> resultList = get(key);
			if(trace.getDebugCode("collab"))
				trace.out("collab", "add("+schoolName+", "+userIDs+", "+mySession.getGuid()+
						", "+myPosition+") key "+key+", resultList "+resultList);
			if(resultList == null)
				put(key, resultList = new LinkedList<Collaborators>());
			for(Collaborators result : resultList) {
				Collaborator collab = result.getCollaborator(mySession, false);
				if(trace.getDebugCode("collab"))
					trace.out("collab", "add("+schoolName+", "+userIDs+", "+mySession.getGuid()+
							", "+myPosition+") finds result "+result);
				if(collab == null)
					continue;
				if(result.getState() != Collaborators.State.Awaiting)
					continue;
				collab.setSession(mySession);
				collab.setPosition(myPosition);
				return result;
			}
			Collaborators result = new Collaborators(schoolName, userIDs, mySession, myPosition);
			resultList.add(result);
			return result;
		}

		/**
		 * Find a single collaborator from a group. Uses {@link TSLauncherServer.Session#getTeam()},
		 * {@link TSLauncherServer.Session#getSchoolName()} to make key to find the proper
		 * {@link Collaborators} list.
		 * @param sess session object for the collaborator we want
		 * @return Collaborator object if found; else null
		 */
		synchronized Collaborator findCollaborator(TSLauncherServer.Session sess) {
			Collaborator result = null;
			if(sess != null) {
				String listKey = makeKeyWithNamespace(sess.getSchoolName(), sess.getTeam());
				List<Collaborators> collabsList = get(listKey);
				if(trace.getDebugCode("collab"))
					trace.outNT("collab", "Collab.findCollaborator() listKey "+listKey+
							";\n  list "+collabsList);
				if(collabsList != null) {
					for(Collaborators collabs : collabsList) {
						Collaborator collab = collabs.getCollaborator(sess, true);
						if(collab != null) {
							result = collab;
							break;
						}
					}
				}
			}
			if(trace.getDebugCode("collab"))
				trace.outNT("collab", "Collab.findCollaborator() for session "+sess+" => "+result);
			return result;
		}

		/**
		 * Remove a {@link Collaborators.Collaborator} instance and perform and needed
		 * housekeeping on higher layers.
		 * @param sess
		 * @return enclosing {@link Collaborators} instance
		 */
		synchronized Collaborators removeSession(TSLauncherServer.Session sess) {
			Collaborator collab = findCollaborator(sess);
			if(trace.getDebugCode("collab"))
				trace.out("collab", "Collaborators.All.removeSession("+sess+") finds Collaborator "+collab);
			if(collab == null)
				return null;
			Collaborators result = collab.remove();
			if(trace.getDebugCode("collab"))
				trace.out("collab", "Collaborators.All.removeSession("+sess+") Collaborators result "+result);
			if(!result.isEmpty())
				return result;
			String key = makeKeyWithNamespace(sess.getSchoolName(), sess.getTeam());
			List<Collaborators> collabsList = get(key);
			if(collabsList != null) {
				collabsList.remove(result);
				if(collabsList.isEmpty())
					remove(key);
			}
			return result;
		}

		/**
		 * If there are collaborators for the given session, then queue this message to be processed
		 * by all of them.
		 * @param sess
		 * @param mo message to send
		 * @return 0 or result of {@link Collaborators#enqueue(MessageObject)}
		 */
		synchronized int enqueueToCollaborators(Session sess, MessageObject mo) {
			if(trace.getDebugCode("collab"))
				trace.out("collab", "All.enqueue() keySet "+keySet());
			Collaborator collab = findCollaborator(sess);  // requires session guid match
			if(collab == null)
				return 0;
			if(trace.getDebugCode("collab"))
				trace.outNT("collab", "enqueueToCollaborators("+mo.getMessageType()+"): "+collab);
			return collab.enqueueToCollaborators(mo);
		}
	}

	/**
	 * Information on a single collaborator.
	 */
	public class Collaborator {
		
		/** User name. */
		private final String userid;
		
		/** Ordinal position (1-based index) among collaborating users. Used for the ACTOR. */
		private int position;
		
		/** Session for the tutor. */
		LauncherServer.Session session = null;
		/**
		 * Set the named field(s). 
		 * @param userid
		 * @param position
		 */
		private Collaborator(String userid, int position) {
			this.userid = userid;
			this.position = position;
		}

		/**
		 * @return "(<i>userid</i>, <i>position</i>, <i>session</i>)";
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "("+userid+", "+position+", "+session+")";
		}

		/**
		 * Connect this instance to a session to begin participating.
		 * Side effects: <ul>
		 *     <li>increments {@link Collaborators#nReady};</li>
		 *     <li>calls {@link Collaborators#notifyAll()}.</li>
		 * </ul>
		 * @param session
		 */
		private void setSession(Session session) {
			synchronized(Collaborators.this) {
				Session oldSession = this.session;
				if(session != null) {
					if(this.session == null)
						nReady++;          // increment only on change from null to non-null
				}
				this.session = session;
				Collaborators.this.notifyAll();
				if(trace.getDebugCode("collab"))
					trace.printStack("collab", String.format("Collaborators[%s].collab[%s].setSession(%s)"+
							".notify() nReady %d, oldSession %s", Collaborators.this, this, session, nReady,
							oldSession));
			}
		}
		
		/**
		 * Remove this collaborator from participation by unlinking from the {@link #session}.
		 * Side effects for integrity: <ul>
		 *     <li>increments {@link Collaborators#nFinished};</li>
		 *     <li>removes from {@link Collaborators#collabs};</li>
		 * </ul>
		 * @return enclosing {@link Collaborators} instance
		 */
		private Collaborators remove() {
			synchronized(Collaborators.this) {
				if(session != null)
					nFinished++;              // increment only 1st time we null it
				session = null;
				Collaborators.this.collabs.remove(userid.trim().toLowerCase());
				return Collaborators.this;
			}
		}

		/**
		 * Call {@link Collaborators#enqueue(MessageObject, String)}.
		 * @param mo message to send
		 * @return result from parent method
		 */
		private int enqueueToCollaborators(MessageObject mo) {
			return enqueue(mo, session.getGuid());
		}

		/**
		 * @return enclosing object {@link Collaborators}.this
		 */
		Collaborators getCollaborators() {
			return Collaborators.this;
		}

		/**
		 * Asynchronously send a {@value MsgType#TUTORING_SERVICE_ERROR} message to this
		 * collaborator, to indicate that the student interface should abort the problem
		 * session.  <b>N.B.:</b> this method creates and starts a separate thread.
		 * @param message value for Details property in message
		 * @return the new Thread instance (already running)
		 */
		private Thread abort(final String message) {
			if(session == null)
				return null;
			class Abort implements Runnable {
				public void run() {
					if(session.getController() != null) {
						MessageObject newMessage = makeAbortMessage(message);
						if (trace.getDebugCode("collab")) trace.out("collab", "Collaborator.abort("+message+") "+this);
						session.getController().handleMessageUTP(newMessage);
					}			
				}
			}
			Thread result = new Thread(new Abort(), "Abort-"+session.getGuid());
			result.start();
			return result;
		}

		/**
		 * @return the {@link #userid}
		 */
		String getUserid() {
			return userid;
		}

		/**
		 * @return the {@link #position}
		 */
		int getPosition() {
			return position;
		}

		/**
		 * Set the position, which should have been set at construction.
		 * Includes error check if the new and old values don't agree.
		 * @param position new value for {@link #position}
		 */
		void setPosition(int position) {
			if(this.position != position) {  // error check
				try {
					throw new IllegalArgumentException("old position "+this.position+" != new "+position);
				} catch(IllegalArgumentException iae) {
					trace.errStack("Failed integrity check: "+this, iae);
				}
			}
			this.position = position;
		}

		/**
		 * @return the {@link #session}
		 */
		LauncherServer.Session getSession() {
			return session;
		}
	}

	/** Maximum wait time for collaborators. See {@link #waitForOtherCollaborators()}. */
	public static final long WAIT_TIME = 120000;
;	
	/** THE list of collaborators. */
	private Map<String, Collaborator> collabs = null;

	/** The number of {@link #collabs} entries with session information. */
	private int nReady = 0;

	/** The number of {@link #collabs} who have finished the problem. */
	private int nFinished = 0;

	/** School (effectively a namespace) containing users and class. */
	private final String schoolName;

	/**
	 * Create {@link #collabs} from the list of userids, each prefixed by the schoolName.
	 * Set {@link Collaborator#session} and increment {@link #nReady} on the instance
	 * whose key matches mySchUserid.  
	 * @param schoolName namespace for className
	 * @param className
	 * @param userids collaborators' identifiers
	 * @param mySession caller's session to set in my {@link Collaborators.Collaborator}
	 * @param myPosition ordinal position of new user among collaborators
	 */
	private Collaborators(String schoolName, List<String> userids,
			LauncherServer.Session mySession, int myPosition) {
		this.schoolName = schoolName;
		collabs = new LinkedHashMap<String, Collaborator>();
		for(int i = 0; i < userids.size(); ++i) {
			String userid = userids.get(i);
			collabs.put(userid.trim().toLowerCase(), new Collaborator(userid, i+1));
		}
		if(trace.getDebugCode("collab"))
			trace.out("collab", "Collaborators.<init> map of collabs "+collabs);
		Collaborator myCollab = getCollaborator(mySession.getUserGuid());
		if(myCollab == null)
			throw new IllegalArgumentException("No collaborator found for session["+mySession+
					"].userGuid "+mySession.getUserGuid());
		myCollab.setSession(mySession);
		myCollab.setPosition(myPosition);
	}
	
	/**
	 * @return {@link #collabs}.{@link Map#isEmpty()} 
	 */
	public boolean isEmpty() {
		return collabs.isEmpty();
	}

	/**
	 * Find a {@link Collaborators.Collaborator} instance from a {@link LauncherServer.Session}.
	 * @param sess lookup this {@link LauncherServer.Session#getUserGuid()} in {@link #collabs}
	 * @param requireSessionIdMatch if true, return instance only if Session guids also match
	 * @return found instance
	 */
	public Collaborator getCollaborator(Session sess, boolean requireSessionIdMatch) {
		if(trace.getDebugCode("collab"))
			trace.outNT("collab", "Collab.getCollaborator() sess.userGuid "+sess.getUserGuid()+
					", sess.guid "+sess.getGuid()+";\n  this "+this);
		Collaborator collab = getCollaborator(sess.getUserGuid());
		if(collab == null)
			return null;
		if(!requireSessionIdMatch)
			return collab;
		if(collab.session != null && sess.getGuid().equals(collab.session.getGuid()))
			return collab;
		return null;
	}
	
	/**
	 * @return state calculated from {@link #nReady} and {@link #nFinished}
	 */
	State getState() {
		State result;
		if(nReady < 1)
			result = State.New;
		else if(nFinished < 1) {
			if(nReady < collabs.size())
				result = State.Awaiting;
			else
				result = State.Running;
		} else
			result = State.Finishing;

		if(trace.getDebugCode("collab"))
			trace.out("collab", "getState() result "+result+", nReady "+nReady+", nFinished "+nFinished+
					", size() "+collabs.size());
		return result;
	}
	
	/**
	 * @return the {@link #schoolName}
	 */
	String getSchoolName() {
		return schoolName;
	}

	/**
	 * Queue this message to the work queue of each collaborator's tutor engine.
	 * For each collaborator position <i>p</i> [1..<i>N</i>], set the message's
	 * {@value Matcher#ACTOR} property to {@value Matcher#DEFAULT_STUDENT_ACTOR}<i>p</i>.
	 * @param mo message to queue.
	 * @param mySessionId caller's session identifier, to avoid redundant tool message
	 * @return number of collaborators getting the message
	 */
	private int enqueue(MessageObject mo, String mySessionId) {
		int count = 0;
		Object oldActor = mo.getProperty(Matcher.ACTOR);
		if(trace.getDebugCode("collab"))
			trace.out("collab", "enqueue("+mo.getMessageType()+") oldActor "+oldActor+" "+this);
		if(!isMsgToBeShared(mo))
			return 0;             // means "don't share"

		for(Collaborator collab : collabs.values()) {
			BR_Controller controller = collab.session.getController();
			if(controller == null)
				continue;
			RemoteProxy proxy = controller.getRemoteProxy();
			if(proxy == null)
				continue;
			ActionHandler handler = proxy.getActionHandler();
			if(handler == null)
				continue;
			
			String actor = Matcher.DEFAULT_STUDENT_ACTOR+Integer.toString(collab.position);
			if (!mySessionId.equalsIgnoreCase(collab.session.getGuid())) {
				MessageObject moToShare = null;
				if(mo.isMessageType(MsgType.UNTUTORED_ACTION)) {
					moToShare = mo.copy();
					moToShare.setTransactionId(MessageObject.makeTransactionId());
				} else
					moToShare = PseudoTutorMessageBuilder.buildToolInterfaceAction(mo.getSelection(),
							mo.getAction(), mo.getInput(),
							PseudoTutorMessageBuilder.TRIGGER_USER, actor);
				controller.getUniversalToolProxy().handleMessageByPlatform(moToShare, false);
			}
			mo.setProperty(Matcher.ACTOR, actor);
			if(trace.getDebugCode("collab"))
				trace.outNT("collab", "enqueue to session "+collab.session.getGuid());
			handler.enqueue(mo);
			++count;
		}
		mo.setProperty(Matcher.ACTOR, oldActor);		// restore message
		return count;
	}

	/**
	 * Tell whether a tool message should be forwarded to collaborators' tutor engines.
	 * Currently returns true only for {@value MsgType#INTERFACE_ACTION} and
	 * {@value MsgType#UNTUTORED_ACTION} messages.
	 * @param mo tool message to test
	 * @return true if should be shared; else false
	 */
	public static boolean isMsgToBeShared(MessageObject mo) {
		if(MsgType.INTERFACE_ACTION.equalsIgnoreCase(mo.getMessageType()))
			return true;
		if(MsgType.UNTUTORED_ACTION.equalsIgnoreCase(mo.getMessageType()))
			return true;
		return false;
	}

	/**
	 * Factory for instances of this class. <b>Side effect:</b> calls
	 * {@link LauncherServer.Session#setCollaborator(Collaborators) with new instance;
	 * cf. {@link #removeSession(LauncherServer.Session)}.
	 * @param session
	 * @param setPrefs {@value MsgType#SET_PREFERENCES} message with all parameters
	 * @return new instance, after {@link #waitForOtherCollaborators()}
	 */
	public static Collaborators create(TSLauncherServer.Session session,
			MessageObject setPrefs) throws NotReadyException {

		int[] rtnPosition = new int[1];
		rtnPosition[0] = -1;              // initially "unset"
		List<String> userids = getUserids(setPrefs, rtnPosition);
		if(userids.size() < 2)
			return null;
		int position = rtnPosition[0];
		
		String schoolName = (String) setPrefs.getProperty(Logger.SCHOOL_NAME_PROPERTY);
		Collaborators result = 
				session.getLauncherServer().getAllCollaborators().add(schoolName,
						userids, session, position);
		if(trace.getDebugCode("collab"))
			trace.out("collab", "Collaborators.create("+schoolName+","+userids+") session "+session+
					", result\n  "+result);
		
		result.waitForOtherCollaborators();
		return result;
	}
	
	/** printf format for usernames created for collaboration. */
	private static final String UsernameFmt = "Collaborator_%d_of_%d_%s";

	/**
	 * Edit a {@value MsgType#SET_PREFERENCES} message that would trigger collaboration.
	 * @param setPrefs message to edit
	 * @param collabID caller should ensure that this value is unique; e.g. a timestamp
	 * @param teamSize number of collaborators
	 * @param sessionID a session id processed by {@link #editSessionIDForCollaboration(String)}
	 * @return true if edited the message, false otherwise
	 */
	public static boolean editSetPreferences(MessageObject setPrefs, String collabID,
			int teamSize, String sessionID) {
		if(Utils.isRuntime())
			return false;          // no-op in tutoring service
		int position = -1;
		int i = 0;
		try {
			java.util.regex.Matcher m = SessionIDPattern.matcher(sessionID);
			boolean isCollabSession = m.matches();
			if(trace.getDebugCode("collab"))
				trace.out("collab",
						String.format("Collab.editSetPreferences(): sessionID %s %s match",
								sessionID, (isCollabSession ? "is" : "not")));
			if(!isCollabSession)
				return false;
			position = Integer.parseInt(m.group(++i));
			int total = Integer.parseInt(m.group(++i));
			if(total != teamSize)
				throw new IllegalArgumentException("Collaborators session "+sessionID+
						" not match teamSize "+teamSize);
			String cid = m.group(++i);
			if(!cid.equals(collabID))
				throw new IllegalArgumentException("Collaborators session "+sessionID+
						" not match collabID "+collabID);
			String origID = m.group(++i);
			if(trace.getDebugCode("collab"))
				trace.out("collab",
						String.format("Collab.editSetPreferences(%s): position %d, total %d, cid %s, origID %s",
								(setPrefs == null ? null : setPrefs.summary()), position, total, cid, origID));
		} catch(Exception e) {
			trace.errStack("Error parsing collaborator session id \""+sessionID+"\" at group "+i+": "+e, e);
			return false;
		}
		String studentName = String.format(UsernameFmt, position, teamSize, collabID);
		setPrefs.setProperty(Logger.STUDENT_NAME_PROPERTY, studentName);

		String schoolName = "school_"+collabID;
		setPrefs.setProperty(Logger.SCHOOL_NAME_PROPERTY, schoolName);
		
		i = 1;
		StringBuilder sb = new StringBuilder(String.format(UsernameFmt, i, teamSize, collabID));
		while(++i <= teamSize)
			sb.append(UseridListDelimiter).append(String.format(UsernameFmt, i, teamSize, collabID));
		setPrefs.setProperty(COLLABORATORS_SET_PREFS_PROP, sb.toString());
		
		if(trace.getDebugCode("collab"))
			trace.out("collab", "Collab.editSetPreferences() to rtn true: student name "+studentName+
					", school name "+schoolName+", collaborators "+sb);
		return true;
	}
	
	/** printf format of session ids (guids) used for collaboration; keep consistent with {@link #SessionIDPattern}. */
	private static final String SessionIDFmt = "CollabSession_%d_of_%d_%s_%s";
	
	/** To parse session ids (guids) used for collaboration; keep consistent with {@link #SessionIDFmt}. */
	private static final Pattern SessionIDPattern =
			Pattern.compile("CollabSession_([0-9]+)_of_([0-9]+)_([^_]+)_(.*)");

	/**
	 * Prefix a session identifier with collaboration info to have the form<br />
	 *   <tt>CollabSession_<i>position</i>_of_<i>teamSize</i>_<i>schoolName</i>_<i>originalID</i></tt>
	 * No-op if teamSize < 2.
	 * @param originalID the original session identifier
	 * @param collabID unique name for this collaboration
	 * @param position ordinal (1-based) position of this user in team
	 * @param teamSize number of members in team
	 * @return string formatted as above
	 */
	public static String editSessionIDForCollaboration(String originalID,
			String collabID, int position, int teamSize) {
		String result = originalID;
		if(teamSize > 1)
			result = String.format(SessionIDFmt, position, teamSize, collabID, originalID);
		if(trace.getDebugCode("collab"))
			trace.out("collab", "editSessionIDForCollaboration() returns "+result);
		return result;
	}

	/**
	 * Get the userids of the participants in this collaboration. First try the proper
	 * {@value Logger#STUDY_CONDITION_NAME} property in the given {@value MsgType#SET_PREFERENCES}
	 * message. For backward compatibility, try if needed the {@value Logger#CLASS_NAME_PROPERTY}
	 * property. Also find in the list the position of the current userid, given by the value
	 * of the {@value Logger#STUDENT_NAME_PROPERTY} property.
	 * @param setPrefs message to scan for values
	 * @param rtnPosition if not null, return index of current userid
	 * @return list of userids; null if none; single-entry list if only one
	 */
	public static List<String> getUserids(MessageObject setPrefs, int[] rtnPosition) {
		
		String myUserid = (String) setPrefs.getProperty(Logger.STUDENT_NAME_PROPERTY);
		List<String> userids = getUseridsFromCollaboratorsProperty(setPrefs, myUserid, rtnPosition);
		if(userids == null)
			userids = getUseridsFromCondition(setPrefs, myUserid, rtnPosition);
		if(userids == null) {
			String className = (String) setPrefs.getProperty(Logger.CLASS_NAME_PROPERTY);
			userids = getUseridsFromClassName(className, myUserid, rtnPosition);
		}
		if(userids == null)
			userids = new ArrayList<String>(1);
		if(trace.getDebugCode("collab"))
			trace.out("collab", "getUserids() for "+myUserid+" returns "+userids+
					", position "+(rtnPosition == null ? null : String.valueOf(rtnPosition[0])));
		return userids;
	}
	
	/**
	 * If the given {@value MsgType#SET_PREFERENCES} message has a property named
	 * {@value #COLLABORATORS_SET_PREFS_PROP}, then create a list of userids from the value.
	 * Return also the 1-based position of the given myUserid in this list.
	 * @param setPrefs message to scan
	 * @param myUserid
	 * @param rtnPosition if not null, return myUserid index in element[0]
	 * @return list of userids from property {@value #COLLABORATORS_SET_PREFS_PROP}
	 */
	private static List<String> getUseridsFromCollaboratorsProperty(MessageObject setPrefs,
			String myUserid, int[] rtnPosition) {

		String userIDstring = (String) setPrefs.getProperty(COLLABORATORS_SET_PREFS_PROP);
		if(userIDstring == null || userIDstring.length() < 1)
			return null;
		return parseUserIDsFromTeamList(userIDstring, myUserid, rtnPosition);
	}

	/**
	 * If the given {@value MsgType#SET_PREFERENCES} message has a property named
	 * {@value Logger#STUDY_CONDITION_TYPE}<i>N</i> whose value is "collaborators," then
	 * create a list of userids from the value of the corresponding property
	 * {@value Logger#STUDY_CONDITION_NAME}<i>N</i>. Return also the 1-based position
	 * of the given myUserid in this list.
	 * @param setPrefs message to scan
	 * @param myUserid
	 * @param rtnPosition if not null, return myUserid index in element[0]
	 * @return list of userids from property {@value Logger#STUDY_CONDITION_NAME}<i>N</i>;
	 *         null if no {@value Logger#STUDY_CONDITION_TYPE}<i>N</i> "collaborators"
	 */
	private static List<String> getUseridsFromCondition(MessageObject setPrefs,
			String myUserid, int[] rtnPosition) {

		for(int i = 0; i < 10; ++i) {         // search only at most 10 conditions
			String type, name;
			if((type = (String) setPrefs.getProperty(Logger.STUDY_CONDITION_TYPE+i)) == null)
				continue;
			if(!"collaborators".equalsIgnoreCase(type))
				continue;
			if((name = (String) setPrefs.getProperty(Logger.STUDY_CONDITION_NAME+i)) == null) {
				trace.err("Message has property "+Logger.STUDY_CONDITION_TYPE+i+" with value "+
						type+" but null "+Logger.STUDY_CONDITION_NAME+i+" property");
				continue;
			}
			return parseUserIDsFromTeamList(name, myUserid, rtnPosition);
		}
		return null;
	}

	/**
	 * Parse a delimited string of usernames into a list. Return also the 1-based position
	 * of the given myUserid in this list. 
	 * @param userIDstring
	 * @param myUserid
	 * @param rtnPosition if not null, return myUserid index in element[0]
	 * @return list of userids, all in lower case
	 */
	private static List<String> parseUserIDsFromTeamList(String userIDstring,
			String myUserid, int[] rtnPosition) {
		if(myUserid != null)
			myUserid = myUserid.trim().toLowerCase();
		if(rtnPosition != null)
			rtnPosition[0] = -1;              // initialize to "not found"
		
		String[] userIDs = UseridListDelimiterPattern.split(userIDstring.toLowerCase());
		List<String> result = new ArrayList<String>(userIDs.length);

		for(int j = 0; j < userIDs.length; ++j) {
			String trimmed = userIDs[j].trim();
			if(trimmed == null || trimmed.length() < 1)
				continue;
			result.add(trimmed);               // already in lower case
			if(trimmed.equals(myUserid) && rtnPosition != null)
				rtnPosition[0] = result.size();
		}
		return result;
	}

	/**
	 * Wait until all collaborators have entered their {@link LauncherServer.Session} information.
	 * Loops, with {@link #wait(long) wait(WAIT_TIME)}
	 */
	private synchronized void waitForOtherCollaborators() throws NotReadyException {
		long now = System.currentTimeMillis();
		long end = now+WAIT_TIME;
		while(nReady < collabs.size() && now < end) {
			Throwable ex = null;
			if(trace.getDebugCode("collab"))
				trace.printStack("collab", String.format("Collaborators[%s].waitForOtherCs() in loop:"+
						" nReady %d, collabs[%s].size %d, to wait %d ms", trace.nh(this), nReady,
						trace.nh(collabs), collabs.size(), end-now));
			try {
				wait(end-now);
			} catch (Exception e) {
				ex = e;
			}
			now = System.currentTimeMillis();
			if(trace.getDebugCode("collab"))
				trace.outNT("collab", "Collaborators.waitForOtherCs() wait ended with "+(end-now)+
						" ms left; exception "+ex+", cause "+(ex == null ? null : ex.getCause()));
		}
		if(trace.getDebugCode("collab"))
			trace.out("collab", String.format("Collaborators[%s].waitForOtherCs() exited loop: nReady %d,"+
					" collabs[%s].size %d, ms left %d", trace.nh(this), nReady,
					trace.nh(collabs), collabs.size(), end-(now=System.currentTimeMillis())));
		if(nReady < collabs.size()) {
			List<String> absences = new LinkedList<String>();
			for(Collaborator collab : collabs.values())
				if(collab.session == null) absences.add(collab.userid);
			throw new NotReadyException("nReady "+nReady+", collabs.size() "+collabs.size(), absences);
		}
		notifyAll();  // awake 3rd, 4th, ... collaborators
	}
	
	/** To generate unique identifiers from timestamps. Output has no white space or underscores. */
	public static DateFormat dateFmt = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");

	/** Name of the property in {@value MsgType#SET_PREFERENCES} messages having team members. */
	public static final String COLLABORATORS_SET_PREFS_PROP = "collaborators";

	/** Regex for 1st name in collaborator class name; includes "collaborator" keyword and opening "(". */
	private static final Pattern CollaboratorClassName1 =
			Pattern.compile("collaborators[^(]*\\( *([^,()]+)", Pattern.CASE_INSENSITIVE);

	/** Regex for 2nd and subsequent names in collaborator class name; includes closing ")". */
	private static final Pattern CollaboratorClassName2 =
			Pattern.compile("(?:,([^,()]+))?(?:,([^,()]+))*\\)");

	/** Delimiter for list of userids. */
	private static final String UseridListDelimiter = ",";
	
	/** Regex for splitting list of userids into individual elements. */
	private static final Pattern UseridListDelimiterPattern = Pattern.compile(UseridListDelimiter);
	
	/**
	 * Scan the given class name for a list of collaborating student userids. If the list
	 * is there and myUserid is among them, then return the list.
	 * @param className className to parse
	 * @param myUserid 
	 * @param position return in the first element the 1-based position of myUserid in the list
	 * @return list of userids parsed from the className
	 */
	private static List<String> getUseridsFromClassName(String className,
			String myUserid, int[] position) {
		if(position != null && position.length > 0)
			position[0] = -1;
		if(className == null)
			return null;
		className = className.trim();
		myUserid = myUserid.trim();

		java.util.regex.Matcher m = CollaboratorClassName1.matcher(className);
		int p = 1;
		int end = -1;
		String group = ""; 
		boolean matched = m.find();
		if(matched) {
			end = m.end(1);
			group = m.group(1).trim();
		}
		if(!matched || m.group(p) == null)
			return null;

		List<String> result = new ArrayList<String>();
		result.add(group.trim().toLowerCase());
		if(trace.getDebugCode("collabclass"))
			trace.out("collabclass", "Collab.getUseridsFromClassName("+className+", "+myUserid+
					") found1 "+matched+", end "+end);

		boolean foundMyUserid = false;
		if(myUserid.length() > 0 && myUserid.equalsIgnoreCase(group)) {
			foundMyUserid = true;
			if(position != null && position.length > 0)
				position[0] = p;
		}

		m = CollaboratorClassName2.matcher(className);
		matched = m.find(end);
		if(trace.getDebugCode("collabclass"))
			trace.out("collabclass", "Collab.getUseridsFromClassName("+className+", "+myUserid+
					") found2 "+matched);
		if(!matched) {        // failed to match end of pattern: no closing ")"?
			if(position != null && position.length > 0)
				position[0] = -1;               // undo since illegal pattern
			return null;
		}
		
		for(++p; matched; p++) {
			try {
				end = m.end(1);
				group = m.group(1);
				if(trace.getDebugCode("collabclass"))
					trace.out("collabclass", "Collab.getUseridsFromClassName() end "+end+", group(1) "+group);
				if(group == null)
					break;
				group = group.trim().toLowerCase();
				result.add(group.trim().toLowerCase());
				if(myUserid.length() > 0 && myUserid.equalsIgnoreCase(group)) {
					foundMyUserid = true;
					if(position != null && position.length > 0)
						position[0] = p;
				}
				matched = m.find(end);
				if(trace.getDebugCode("collabclass"))
					trace.out("collabclass", "Collab.getUseridsFromClassName("+className+", "+myUserid+
							") found3 "+matched);
			} catch (Exception e) {
				if(trace.getDebugCode("collabclass"))
					trace.out("collabclass", "Collab.getUseridsFromClassName() error at group("+p+"): "+e);
				break;
			}
		}

		if(trace.getDebugCode("collabclass"))
			trace.out("collabclass", "Collab.getUseridsFromClassName(..., "+myUserid+") result "+result+
					", foundMyUserid "+foundMyUserid+
					", position "+(position == null ? "null" : Integer.toString(position[0])));
		 return result;
	}
	
	public String toString() {
		return super.toString()+"{school "+schoolName+", state "+getState()+
				", nReady "+nReady+", nFinished "+nFinished+"\n  "+collabs+"}";
	}

	/**
	 * Create a key by prefixing a namespace to a name.
	 * @param namespace
	 * @param name
	 * @return arguments concatenated, space-delimited, lower case
	 */
	public static String makeKeyWithNamespace(String namespace, String name) {
		namespace = (namespace == null ? "" : namespace);
		name = (name == null ? "" : name);
		return namespace.toLowerCase().trim() + ' ' + name.toLowerCase().trim();
	}

	/**
	 * Abort a collaboration by terminating the calling session and notifying other collaborators
	 * via {@link Collaborator#abort(String)}.
	 * @param controller originating session's controller
	 * @param callingSessionID session which triggered the abort
	 * @param message error description to send to collaborators' user interfaces
	 * @return true if there's a collaboration in progress
	 */
	public static boolean abort(BR_Controller controller, String callingSessionID, String message) {
		TSLauncherServer ls = null;
		if(controller == null || controller.getLauncher() == null ||
				(ls = controller.getLauncher().getLauncherServer()) == null)
			return false;
		LauncherServer.Session callingSession = ls.getSession(callingSessionID);
		Collaborator collab = ls.getAllCollaborators().findCollaborator(callingSession);
		if(collab == null)
			return false;

		MessageObject mo = makeAbortMessage(message);
		String guid = null;
		if(ls !=null) {
			guid = SocketProxy.getGuid(controller);
			ls.updateTimeStamp(guid);
			mo.setTransactionInfo(ls.createTransactionInfo(guid));
		}
		return(ls != null && ls.enqueueToCollaborators(guid, mo) > 0);
	}

	/**
	 * Create a message to send to clients to abort a collaboration.
	 * @param message details to include in message
	 * @return message of type {@value MsgType#TUTORING_SERVICE_ERROR}
	 */
	private static MessageObject makeAbortMessage(String message) {
		MessageObject newMessage =
				MessageObject.create(MsgType.TUTORING_SERVICE_ERROR, "SendNoteProperty");
		newMessage.setProperty("ErrorType", "Collaboration Failure");
		newMessage.setProperty("Details", message);
		return newMessage;
	}

	/**
	 * Find a {@link Collaborators.Collaborator} record by username. This method will
	 * call {@link String#trim()} and {@link String#toLowerCase()} for the caller.
	 * @param userid: don't need to trim() or make lower case
	 * @return instance from {@link #collabs} with this key;
	 *         null if null userid or not found
	 */
	public Collaborator getCollaborator(String userid) {
		Collaborator result = null;
		if(userid != null)
			result = collabs.get(userid.trim().toLowerCase());
		if(trace.getDebugCode("collab"))
			trace.out("collab", "Collaborators.getCollaborator("+userid+") => "+result);
		return result; 
	}

	/**
	 * Top-level call to remove a {@link LauncherServer.Session} from a collaboration.
	 * @param sess session to remove
	 */
	public static void remove(Session sess) {
		TSLauncherServer ls;
		if(sess == null || (ls = sess.getLauncherServer()) == null)
			return;
		ls.getAllCollaborators().removeSession(sess);
	}

	/**
	 * Handle a message generated when a collaborator quits. No-op if the collaborative
	 * problem has been finished.
	 * @param mo messsage generated to describe the quit, to forward to collaborators' clients
	 * @param controller my session's controller
	 * @return true if had to abort; false if collaboration already ended
	 */
	public static boolean handleTutoringServiceErrorMessage(MessageObject mo, BR_Controller controller) {
		SingleSessionLauncher launcher;
		if(controller == null || (launcher = controller.getLauncher()) == null)
			return false;
		Collaborators.Collaborator collab = launcher.findCollaborator();
		if (trace.getDebugCode("collab")) trace.out("collab", "handleTutoringServiceErrorMessage()"+
				" findCollaborator() returns "+collab);
		if(collab == null)
			return false;  // no-op: collaboration already ended

		controller.handleMessageUTP(mo);  // tell the user to quit trying
		
		String sessionID = null;
		if(collab.session instanceof LauncherServer.Session)
			sessionID = collab.session.getGuid();
		if(sessionID == null)
			return false;           // not sure this should ever happen
		if(launcher.getLauncherServer() != null)
			launcher.getLauncherServer().removeSession(sessionID);
		return true;
	}
}
