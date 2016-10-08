/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * Record the messages associated with a tool-tutor transaction.
 */
public class Transaction {

	/**
	 * A collection of {@link Tranaction}s.
	 */
	public static class TransactionSet {
		
		/** The Map: key is {@link Transaction#getKey()}; value is {@link Transaction}. */
		private Map map = new LinkedHashMap();
		private Transaction latestStartProblemTransaction;

		
		
		/**
		 * Put this transaction into the collection.
		 * @param tx
		 */
		public synchronized void add(Transaction tx) {
			map.put(tx.getKey(), tx);
			latestStartProblemTransaction = (tx.isStartProblem() ? tx : null);
			trace.out("sp", "TransactionSet.add() key " + tx.getKey());
		}

		/**
		 * Get the transaction with the given key.
		 * @param key
		 * @return transaction; null if none matchest this key.
		 */
		public Transaction get(String key) {
			return (Transaction) map.get(key);
		}

		/**
		 * Find the proper transaction for the given message, add it to the
		 * transaction's list of responses, and check whether the transaction
		 * is complete.
		 * @param msg
		 */
		public synchronized void addResponse(String msg) {
			String key = getKey(msg);
			Transaction tx;
			if (latestStartProblemTransaction != null)
				tx = latestStartProblemTransaction;
			else
				tx = get(key);
			trace.out("sp", "TransactionSet.addResponse() tx "+tx);
			if (tx != null)
				tx.addResponse(msg);
		}
		
		/**
		 * Cancel any outstanding {@link Transaction}s. Call this to
		 * notify any threads waiting when, e.g., a socket closes.
		 */
		public synchronized void cancelIncompleteTransactions() {
			for (Iterator it = map.values().iterator(); it.hasNext(); ) {
				Transaction tx = (Transaction) it.next();
				if (tx != null && !tx.isResponseComplete())
					tx.cancel();
			}
		}
		
		/**
		 * Checks each transaction to see if it is complete.  If all transactions
		 * are complete then return true.  Otherwise return false.
		 */
		public synchronized boolean allTransactionsComplete() {
			boolean passed = true;;
			for (Iterator it = map.values().iterator(); it.hasNext(); ) {
				Transaction tx = (Transaction) it.next();
				if (tx != null && !tx.isResponseComplete())
				{
					trace.out("ls", tx.toString());
					trace.out("sp", tx.toString());
					passed = false;
				}
			}
			return passed;
		}

		public int getSize() {
			// TODO Auto-generated method stub
			return map.size();
		}
	}

	/** The tool's message to the tutor engine. */
	private String requestText;
	
	/** {@link #requestText} as a MessageObject. */
	private MessageObject mo = null;

	/** The tutor's responses: List of String. */
	public List<String> responses = null;
	
	/** Whether all responses have been recorded. */
	private boolean responseComplete = false;
	
	/** Whether or not the transaction ended in correct or incorrect [false if n/a] */
	private boolean correctTransaction = false;

	/** Transaction identifier from requestText. See {@link #getKey(String)}. */
	private final String key;

	/** Whether this is a StartProblem transaction. */
	private final boolean startProblem;

	/** Whether this tranasction has been cancelled due, e.g., to a socket close. */
	private boolean cancelled = false;

	/** Timestamp when request sent. */
	private Date sendTime;
	
	/**
	 * Transaction duration in ms: from {@link #sendTime} until
	 * transaction complete. Negative value means "unset".
	 */
	private long duration = -1;

	/**
	 * Set the {@link #requestText}.
	 * @param requestText
	 * @param newTransId if true, insert a new transaction id
	 */
	public Transaction(String request, boolean newTransId) {
		this.requestText = request;
		mo = MessageObject.parse(request);
		if (newTransId) {
			if (mo.getTransactionId() != null)
				mo.setTransactionId(mo.makeTransactionId());
			this.requestText = null;
		}
		startProblem = (MsgType.SET_PREFERENCES.equalsIgnoreCase(mo.getMessageType()));
		key = getKey(mo);
	}
	
	/**
	 * Cancel this transaction. Sets {@link #cancelled}; notifies any threads
	 * waiting.
	 */
	private synchronized void cancel() {
		cancelled = true;
		notifyAll();
	}

	/**
	 * Add a response to the list {@link #responses} and call
	 * {@link #checkWhetherComplete()}.
	 * @param msg
	 */
	public synchronized void addResponse(String msg) {
		if (responses == null)
			responses = new ArrayList<String>();
		responses.add(msg);
		if (checkWhetherComplete())
			notifyAll();
	}
	
	/** Message types that, on receipt, always indicate complete transactions. */
	private static final String[] pseudoTransactions = {
		MsgType.VERSION_INFO, MsgType.START_STATE_END, MsgType.PROBLEM_SUMMARY_RESPONSE
	};
	private static Set<String> pseudoTransactionSet = 
		new HashSet<String>(Arrays.asList(pseudoTransactions));

	/**
	 * Check the last message in the {@link #responses} list to see whether it
	 * marks the end of the transaction. Sets {@link #responseComplete}.
	 * @return new value of {@link #responseComplete}
	 */
	private boolean checkWhetherComplete() {
		String msg = (String) responses.get(responses.size()-1);
		MessageObject mo = MessageObject.parse(msg);
		String msgType = mo.getMessageType();
		
		if (pseudoTransactionSet.contains(msgType))
			responseComplete = true;
		else {
			if ("InCorrectAction".equalsIgnoreCase(msgType))
				correctTransaction = false;
			else if ("CorrectAction".equalsIgnoreCase(msgType))
				correctTransaction = true;
			Object endOfTransaction = mo.getProperty("end_of_transaction");
			if (endOfTransaction instanceof Boolean)
				responseComplete = ((Boolean) endOfTransaction).booleanValue();
			else if (endOfTransaction != null)
				responseComplete = Boolean.parseBoolean(endOfTransaction.toString());
			else
				responseComplete = false;
		}
		trace.out("sp", "checkWhetherComplete() msgType "+msgType+" rtns "+
				responseComplete+":\n"+msg);
		return responseComplete;
	}

	/**
	 * For a SetPreferences requestText, the key is the ProblemName. 
	 * For a tool requestText, the key is the transaction_id.
	 * @param msg XMLConverter message
	 * @return key as defined above; null if not defined above
	 */
	public static String getKey(String msg) {
		MessageObject mo = MessageObject.parse(msg);
		return getKey(mo);
	}
	
	/**
	 * For a SetPreferences requestText, the key is the ProblemName. 
	 * For a tool requestText, the key is the transaction_id.
	 * @param mo message
	 * @return key as defined above; null if not defined above
	 */
	public static String getKey(MessageObject mo) {
		String msgType = mo.getMessageType();
		if ("SetPreferences".equalsIgnoreCase(msgType))
			return (String) mo.getProperty("ProblemName");
		else if (msgType.startsWith("ProblemSummary"))
			return "ProblemSummary";
		else
			return mo.getTransactionId();
	}

	/**
	 * Return this transaction's key.
	 * @return value of {@link #key}
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * @return the number of responses for the transaction
	 */
	public int getNumResponses() {
		return (responses == null ? 0 : responses.size());
	}

	/**
	 * @return {@link #responseComplete} || {@link #cancelled}
	 */
	public boolean isResponseComplete() {
		return responseComplete || cancelled;
	}

	/**
	 * @return (@link {@link #correctTransaction})
	 */
	public boolean isResponseCorrect() {
		return correctTransaction;
	}
	/**
	 * @return the {@link #startProblem}
	 */
	public boolean isStartProblem() {
		return startProblem;
	}
	
	/**
	 * For debugging.
	 * @return string with key, responseComplete, startProblem
	 */
	public String toString() {
		return "[Transaction key "+getKey()+", startProblem "+isStartProblem()+
				", nResponses "+(responses == null ? 0 : responses.size())+
				", responseComplete "+isResponseComplete()+
				", responseCorrect "+isResponseCorrect()+"]";
	}

	/**
	 * Regenerate {@link #requestText}, if necessary, and return it. 
	 * @return {@link #requestText}; if null, resets from {@link MessageObject#toString()}
	 */
	public String getRequestText() {
		if (requestText == null)
			requestText = mo.toString();
		return requestText;
	}

	/**
	 * @return {@link #mo}.{@link MessageObject#getMessageType() getMessageType()}
	 */
	public String getMessageType() {
		return mo.getMessageType();
	}

	/**
	 * Labels messages that have no response from the tutor, e.g.
	 * {@value MsgType#INTERFACE_IDENTIFICATION}.  We send them and
	 * don't wait for a response.
	 * @return true if this {@link #requestText} needs no response
	 */
	public boolean hasNoResponse() {
		return MsgType.INTERFACE_IDENTIFICATION.equalsIgnoreCase(getMessageType());
	}

	/**
	 * Change the session_id parameter in the message, if present, to this value.
	 * This affects {@value MsgType#SET_PREFERENCES} messages and, as a special case,
	 * the Guid field in {@value MsgType#INTERFACE_IDENTIFICATION} messages.
	 * @param sessionId new value for session id
	 */
	public void setSessionId(String sessionId) {
		if (MsgType.INTERFACE_IDENTIFICATION.equalsIgnoreCase(getMessageType()))
			mo.setProperty("Guid", sessionId);
		else if (mo.getProperty(Logger.SESSION_ID_PROPERTY) != null)
			mo.setProperty(Logger.SESSION_ID_PROPERTY, sessionId);
		else
			return;
		requestText = null;
		
	}

	/**
	 * @param date new value for {@link #sendTime}
	 */
	public void setSendTime(Date date) {
		sendTime = date;
		if (hasNoResponse())
			duration = -1;
	}

	/**
	 * @return the {@link #sendTime}
	 */
	public Date getSendTime() {
		return sendTime;
	}

	public void setReceiveTime(long currentTimeMillis) {
		if (sendTime != null)
			duration = currentTimeMillis - sendTime.getTime();
	}

	/**
	 * @return the {@link #duration}
	 */
	public long getDuration() {
		return duration;
	}
}
