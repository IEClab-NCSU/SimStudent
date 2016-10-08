/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.model;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import pact.CommWidgets.UniversalToolProxy;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * Code to reconcile start state between a graph and a student interface.
 */
public class StartStateModel implements Cloneable {
	
	/**
	 * For callbacks.
	 */
	public static interface Listener {
		
		/**
		 * Called when start state info has been received by the StartState.
		 */
		public void startStateReceived(EventObject evt);
	}
	
	/**
	 * For detecting errors in {@link CompareInterfaceDescriptions#setStudentInterfaceFields(MessageObject)}.
	 */
	public static class WidgetTypeException extends RuntimeException {

		private static final long serialVersionUID = 201402201355L;

		public WidgetTypeException(String message) { super(message); }
	}
	
	/** Value of {@link #match} if settings are the same. */
	public static final String SAME = "same";
	
	/** Value of {@link #match} if settings differ. */
	public static final String DIFFER = "differ";

	/** Property name for component type in {@value MsgType#INTERFACE_DESCRIPTION} messages. */
	public static final String WIDGET_TYPE = "WidgetType";
	
	/** Property name for component instance name in {@value MsgType#INTERFACE_DESCRIPTION} messages. */
	private static final String COMM_NAME = "CommName";
	
	/** Parameter name and type name for Group Name parameter in {@value MsgType#INTERFACE_DESCRIPTION} messages. */
	private static final String GROUP = "group";

	/** Type name for cells in {@value MsgType#INTERFACE_DESCRIPTION} messages. */
	private static final String CELL = "TableCell";

	/** Type name for system components in {@value MsgType#INTERFACE_DESCRIPTION} messages. */
	private static final String SYSTEM = "system";
	
	/** Property name for detail element in {@value MsgType#INTERFACE_DESCRIPTION} messages. */
	private static final String SERIALIZED = "serialized";
	
	/**
	 * Available actions for {@link StartStateModel.CompareInterfaceDescriptions}.
	 */
	public static enum Choice {
		
		keepPM("Keep Graph Settings", 'K'),
		saveSI("Save Interface Settings to Graph", 'S'),
		omit("Omit Settings from Graph", 'O');
		
		/** For {@link #toString()}. */ private final String display;
		
		/** For {@link #toChar()}. */   private final char chr;
		
		private Choice(String display, char chr) {
			this.display = display;
			this.chr = chr;
		}
		
		public String toString() { return display; }
		
		public char toChar() { return chr; }

		public static Choice fromChar(char chr) throws IllegalArgumentException {
			for(Choice choice : values()) {
				if(choice.chr == Character.toUpperCase(chr))
					return choice;
			}
			throw new IllegalArgumentException("Invalid character '"+chr+"'");
		}
	}
	
	/**
	 * Results of comparing a pair of {@value MsgType#INTERFACE_DESCRIPTION} messages.
	 */
	public static class CompareInterfaceDescriptions implements Comparable<CompareInterfaceDescriptions> {

		/** Component type. */
		String widgetType = null;
		
		/** Instance name found in {@link ProblemModel}. */
		String pmCommName = null;

		/** Instance name in {@value MsgType#INTERFACE_DESCRIPTION} message from student interface. */
		String siCommName = null;
		
		/** {@value MsgType#INTERFACE_DESCRIPTION} message from {@link ProblemModel}. */
		MessageObject pmMsg = null;

		/** {@value MsgType#INTERFACE_DESCRIPTION} message from student interface. */
		MessageObject siMsg = null;
		
		/** Disposition: which {@value MsgType#INTERFACE_DESCRIPTION} message to keep. */
		Choice choice = null;

		/**
		 * If not null, UI should display with a warning. Current use: same instance name is
		 * found in messages with different widget types. 
		 */
		private String alert = null;

		/**
		 * Result of comparing {@link #pmMsg} and {@link #siMsg}: {@value StartStateModel#SAME}
		 * or {@value StartStateModel#DIFFER}; "" if not compared.
		 */
		String match = "";
	
		/**
		 * Set {@link #pmMsg}, {@link #widgetType}, {@link #pmCommName}.
		 * @param pmMsg
		 */
		CompareInterfaceDescriptions(MessageObject pmMsg) {
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("CompareInterfaceDescriptions(%s) type %s, name %s",
						(pmMsg == null ? null : pmMsg.getMessageType()),
						(pmMsg == null ? null : pmMsg.getProperty("WidgetType")),
						(pmMsg == null ? null : pmMsg.getProperty(COMM_NAME))));

			choice = Choice.omit;  // may change below
			this.pmMsg = pmMsg;
			if(pmMsg == null)
				return;
			
			String widgetType = (String) pmMsg.getProperty(WIDGET_TYPE);
			if(widgetType != null && widgetType.length() > 0)
				this.widgetType = widgetType;
			
			String pmCommName = (String) pmMsg.getProperty(COMM_NAME);
			if(pmCommName != null && pmCommName.length() > 0)
				this.pmCommName = pmCommName;
			
			if(this.widgetType != null && this.pmCommName != null)
				choice = Choice.keepPM;
		}

		/**
		 * @return the {@link #widgetType}
		 */
		public String getWidgetType() { return widgetType; }

		/**
		 * @return the {@link #pmCommName}
		 */
		public String getPmCommName() { return pmCommName; }

		/**
		 * @return the {@link #siCommName}
		 */
		public String getSiCommName() { return siCommName; }

		/**
		 * @return the {@link #pmMsg}
		 */
		public MessageObject getPmMsg() {
			return pmMsg;
		}

		/**
		 * @return the {@link #siMsg}
		 */
		public MessageObject getSiMsg() {
			return siMsg;
		}

		/**
		 * @return the {@link #match}
		 */
		public String getMatch() { return match; }

		/**
		 * @return {@link #choice}
		 */
		public Choice getChoice() { return choice; }

		/**
		 * Will set {@link Choice#keepPM} or {@link Choice#saveSI} only if corresponding
		 * {@link #getPmCommName()} or {@link #getSiCommName()} is not null and not "".
		 * @param choice new value for {@link #choice}
		 * @return prior value
		 */
		public Choice setChoice(Choice choice) {
			Choice result = this.choice;
			switch(choice) {
			case keepPM:
				if(getPmCommName() != null && getPmCommName().length() > 0)
					this.choice = choice;
				break;
			case saveSI:
				if(getSiCommName() != null && getSiCommName().length() > 0)
					this.choice = choice;
				break;
			default:
				this.choice = choice;
				break;
			}
			return result;
		}

		/**
		 * @param choice new value for {@link #choice}
		 * @return prior value
		 */
		public char setChoice(char chr) {
			Choice result = this.choice;
			this.choice = Choice.fromChar(chr);
			return result.toChar();
		}

		/**
		 * Dump fields for debugging.
		 * @return fields delimited by commas enclosed in square brackets
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			sb.append(widgetType).append(',');
			sb.append(match).append(',');
			sb.append(pmCommName).append(',');
			sb.append(siCommName).append(',');
			sb.append(choice.toChar()).append(']');
			return sb.toString();
		}
		
		/**
		 * Generate markup for an HTML table row.
		 * @return string with markup
		 */
		public String toHtmlRow() {
			StringBuilder sb = new StringBuilder("<tr>");
			String[] fields = { widgetType, match, pmCommName, "__", siCommName, "__" };
			for(String field : fields) {
				if("__".equals(field))
					sb.append("<td align=\"center\">");
				else
					sb.append("<td>");
				sb.append(field == null ? "" : field).append("</td>");
			}
			sb.append("</tr>");
			return sb.toString();
		}
		
		/**
		 * Generate markup for the HTML table rows created by {@link #toHtmlRow()}.
		 * @return string with markup
		 */
		public String toHtmlHeader() {
			StringBuilder sb = new StringBuilder("<tr>");
			String[] fields = { "Type", "Match", "Name in Graph", "Delete", "Name in Interface", "Retain" };
			for(String field : fields)
				sb.append("<th align=\"left\">").append(field).append("</th>");
			sb.append("</tr>");
			return sb.toString();
		}

		/**
		 * Compare 2 instances of CompareInterfaceDescriptions for sorting.
		 * @param o other instance to compare
		 * @return result of comparing instances by {@link CompareInterfaceDescriptions#pmCommName} or
		 * {@link CompareInterfaceDescriptions#siCommName} .
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(CompareInterfaceDescriptions o) {
			if(!(match.equals(o.match))) {
				if(SAME.equals(match))
					return 1;                // sort same entries last
				if(DIFFER.equals(match))
					return -1;               // sort different entries first
				if(SAME.equals(o.match))
					return -1;               // sort same entries last
				if(DIFFER.equals(o.match))
					return 1;                // sort different entries first
			}
			
			String myCommName = (pmCommName != null ? pmCommName : siCommName);
			String otherCommName = (o.pmCommName != null ? o.pmCommName : o.siCommName);
			if(myCommName == null) {
				if(otherCommName != null)
					return 1;          // sort null entries last
			} else if(otherCommName == null)
				return -1;             // sort null entries last
			else {
				int result = myCommName.compareTo(otherCommName);
				if(result != 0)
					return result;
			}

			if(widgetType == null) {
				return (o.widgetType != null ? 1 : 0);
			} else if(o.widgetType == null)
				return -1;
			else
				return widgetType.compareTo(o.widgetType);
		}

		/** For removing CDATA brackets. */
		private static final Pattern removeCDATADelimiter = Pattern.compile("<!\\[CDATA\\[([^]]*)\\]\\]>");
				
		/**
		 * Set {@link #siMsg} and {@link #siCommName} from the given message.
		 * Also set {@link #match} by comparing {@link #siMsg} and {@link #pmMsg}.
		 * @param siDesc {@value MsgType#INTERFACE_DESCRIPTION} message from the student interface
		 * @throws WidgetTypeException if WidgetType in msg fails to match {@link #widgetType}
		 */
		private void setStudentInterfaceFields(MessageObject siDesc) {
			if(siDesc == null)
				return;

			String siWidgetType = (String) siDesc.getProperty("WidgetType");
			if(widgetType == null) {                   // check widget type before any other edits 
				if(siWidgetType != null && siWidgetType.length() > 0)
					this.widgetType = siWidgetType;
			} else if(!(widgetType.equals(siWidgetType)))
				throw new WidgetTypeException("CID "+this+" type mismatch: interface type "+siWidgetType);

			String siCommName = (String) siDesc.getProperty(COMM_NAME);
			if(siCommName != null && siCommName.length() > 0) {
				this.siCommName = siCommName;
				this.siMsg = siDesc;
			}
			
			if(pmMsg != null) {
				String pmMsgStr = removeCDATADelimiter.matcher(pmMsg.toMinimalXML()).replaceAll("$1");
				String siMsgStr = removeCDATADelimiter.matcher(siMsg.toMinimalXML()).replaceAll("$1");
				match = (pmMsgStr.equals(siMsgStr) ? SAME : DIFFER);
				if(trace.getDebugCode("startstatell") && DIFFER.equals(match))
					trace.out("startstatell", "CID.setStudentInterfaceFields() pm:\n    "+pmMsgStr+
							"\n  siMsg:\n    "+siMsgStr);
			}
		}

		/**
		 * Provide a warning message for the UI to display. Current use:<ol>
		 * <li>same instance name is found in messages with different widget types.</li>
		 * </ol> 
		 * @return the {@link #alert}
		 */
		public String getAlert() {
			return alert;
		}

		/**
		 * See {@link #hasAlert()} for semantics.
		 * @param alert new value for {@link #alert} 
		 */
		private void setAlert(String alert) {
			this.alert = alert;
		}
	}

	/**
	 * The {@value MsgType#INTERFACE_DESCRIPTION} messages from the student interface.
	 * Key is commName in lower case.
	 */
	private LinkedHashMap<String, MessageObject> interfaceDescriptionsFromInterface =
			new LinkedHashMap<String, MessageObject>();
	
	/**
	 * Start-state messages from the interface whose type is {@value MsgType#INTERFACE_ACTION}.
	 * These are substitutes for parameters that should be set by  {@value MsgType#INTERFACE_DESCRIPTION}
	 * messages from CTAT's Java components.
	 */
	private List<MessageObject> nonInteractiveInterfaceActionsFromInterface = new ArrayList<MessageObject>(); 
	
	/**
	 * Start-state messages from the interface whose type is not 
	 * {@value MsgType#INTERFACE_DESCRIPTION}. Maintained in the order received.
	 */
	private List<MessageObject> otherMsgsFromInterface = new ArrayList<MessageObject>();

	/**
	 * Messages of type {@value MsgType#INTERFACE_DESCRIPTION} that the author has chosen
	 * to add to the start state. Key is commName in lower case.
	 */
	private LinkedHashMap<String, MessageObject> interfaceDescriptionsToAdd =
			new LinkedHashMap<String, MessageObject>();

	/**
	 * {@value MsgType#INTERFACE_DESCRIPTION} messages to remove from the graph's start state.
	 */
	private Set<MessageObject> interfaceDescriptionsToDiscard = new HashSet<MessageObject>();
	
	/** True when author initiates action to edit the start state; false when ready for edits. */  
	private boolean userBeganStartStateEdit = false;
	
	/** To generate events when the data in here change. */
	private PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	/** Fixed list of system selection names for, e.g., {@link #getComponentNames()}. */
	private final LinkedHashSet<String> systemSelectionNames;

	/** Fixed list of action names as default for {@link #getAllActionNames()}. */
	private final LinkedHashSet<String> defaultActionNames;

	/** Map from selection name to list of actions. */
	private Map<String, Set<String>> selectionToActionNamesMap = new LinkedHashMap<String, Set<String>>();

	/** Set of {@link MessageObject#summary()} strings for {@link #discardMessage(MessageObject)}. */
	private Set<String> otherMsgsToDiscard = new LinkedHashSet<String>();

	/**
	 * @param defaultActionNames value for #defaultActionNames 
	 */
	public StartStateModel(List<String> systemSelectionNames, List<String> defaultActionNames) {
		this.systemSelectionNames = new LinkedHashSet<String>(systemSelectionNames);
		this.defaultActionNames = new LinkedHashSet<String>(defaultActionNames);
	}

	/**
	 * Delegate to {@link #pcSupport}.
	 * @param listener listener to add
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		 pcSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * Delegate to {@link #pcSupport}.
	 * @param listener listener to remove
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		 pcSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Make #clone() public.
	 * @return super#clone()
	 */
	public StartStateModel clone() {
		try {
			return (StartStateModel) super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace();
			return null;
		}
	}

	/**
	 * Store a message from the student interface. Rejects message if
	 * {@link #isProperStartStateMessage(MessageObject)} returns false.
	 * Fires a {@link PropertyChangeEvent} if it stores the message.
	 * @param mo the message
	 * @return true if stored the message
	 */
	public boolean addStudentInterfaceMessage(MessageObject mo) {
		boolean result = addStudentInterfaceMessageInternal(mo, true);
		if(result)
			pcSupport.firePropertyChange("Student Interface message added", null, mo);
		return result;
	}

	/**
	 * Store a message from the student interface. Rejects message if
	 * {@link #isProperStartStateMessage(MessageObject)} returns false.
	 * @param mo the message
	 * @param interactive true if this message was from an interactive edit of the start state
	 * @return true if stored the message
	 */
	private boolean addStudentInterfaceMessageInternal(MessageObject mo, boolean interactive) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.addStuIntMsgInt("+mo.summary()+") isProper "+
					isProperStartStateMessage(mo));
		if(trace.getDebugCode("startstateverbose"))
			trace.out("startstate", "SSM.addStuIntMsgInt() msg:\n  "+mo.toXML());
		if(!isProperStartStateMessage(mo))
			return false;
		String msgType = mo.getMessageType();
		if(MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(msgType))
			addInterfaceDescriptionFromInterface(mo);
		else if(MsgType.INTERFACE_ACTION.equalsIgnoreCase(msgType)
				|| MsgType.UNTUTORED_ACTION.equalsIgnoreCase(msgType)) {
	    	if(trace.getDebugCode("startstate"))
	    		trace.out("startstate", "SSM.addStuIntMsgInt("+interactive+") "+mo.summary());
			addInterfaceActionFromInterface(mo, interactive);
		} else
			addOtherMessageFromInterface(mo);
		return true;
	}

	/**
	 * Store a start-state message from the interface whose type is neither
	 * {@value MsgType#INTERFACE_DESCRIPTION} nor {@value MsgType#INTERFACE_ACTION} to
	 * {@link #otherMsgsFromInterface}.
	 * @param mo message to store
	 */
	private synchronized void addOtherMessageFromInterface(MessageObject mo) {
		otherMsgsFromInterface.add(mo);
	}

	/**
	 * Store an {@value MsgType#INTERFACE_ACTION} or {@value MsgType#UNTUTORED_ACTION} message
	 * in {@link #otherMsgsFromInterface} or {@link #nonInteractiveInterfaceActionsFromInterface}.
	 * @param mo message to store
	 * @param interactive true if this message was from an interactive edit of the start state
	 */
	private synchronized void addInterfaceActionFromInterface(MessageObject mo, boolean interactive) {
		if(interactive)
			otherMsgsFromInterface.add(mo);		
		else
			nonInteractiveInterfaceActionsFromInterface.add(mo);
	}

	/**
	 * Store an {@value MsgType#INTERFACE_DESCRIPTION} message in {@link #interfaceDescriptionsFromInterface}.
	 * @param mo message to store
	 */
	private synchronized void addInterfaceDescriptionFromInterface(MessageObject mo) {
		String name = getInstanceName(mo);
		if(name == null)
			trace.err("StartStateModel.addInterfaceDescFromInterface(): no instance name (commName) found; message:\n  "+mo);
		else {
			String key = name.toLowerCase();
			interfaceDescriptionsFromInterface.put(key, mo);
			selectionToActionNamesMap.put(key, getActionNamesFromInterfaceDescription(mo));
		}
	}

	/**
	 * Build a set of action names from the list of SAI elements in an InterfaceDescription message.
	 * @param mo {@value MsgType#INTERFACE_DESCRIPTION} message
	 * @return set of action names
	 */
	private Set<String> getActionNamesFromInterfaceDescription(MessageObject mo) {
		Element tier0 = (Element) mo.getProperty("serialized");  // from Flash components
		Set<String> result = new LinkedHashSet<String>();
		if(tier0 == null)
			return result;
		int i = 0, k = 0;
		for(Iterator<Element> iter1 = tier0.getChildren().iterator(); iter1.hasNext(); ++i) {
			Element tier1 = iter1.next();
			if(!("SAIs".equals(tier1.getName())))
				continue;
			List<Element> list2 = tier1.getChildren();
			if(list2.size() < 1)
				continue;
			Element tier2 = list2.get(0);               // interested only in first child
			k = 0;
			for(Iterator<Element> iter3 = tier2.getChildren().iterator(); iter3.hasNext(); ++k) {
				Element saiElt = iter3.next();
				String action = getActionFromSAIElement(saiElt);
				if(action != null)
					result.add(action);
			}
		}
		return result;
	}

	/**
	 * Retrieve the Action name from an SAI element.
	 * @param saiElt
	 * @return action name
	 */
	private String getActionFromSAIElement(Element saiElt) {
		int i = 0;
		for(Iterator<Element> saiIter = saiElt.getChildren().iterator(); saiIter.hasNext(); ++i) {
			Element actionElt = saiIter.next();
			if(!("action".equals(actionElt.getName())))
				continue;
			String action = actionElt.getValue();
			return action;
		}
		return null;
	}

	/**
	 * Get the component instance name from either "CommName" or the "DorminName" property.
	 * @param mo
	 * @return property value; null if none found or empty
	 */
	private String getInstanceName(MessageObject mo) {
		String commName = (String) mo.getProperty("CommName");
		if(commName == null || commName.length() < 1) {
			commName = (String) mo.getProperty("DorminName");
			if(commName == null || commName.length() < 1)
				return null;
		}
		return commName;
	}

	/**
	 * Compare the {@value MsgType#INTERFACE_DESCRIPTION} messages in {@link #interfaceDescriptionsFromInterface}
	 * against those in the {@link ProblemModel}.
	 * @param pm
	 * @return table of results
	 */
	public String compareStartStateMessages(ProblemModel pm) {
		if(pm == null)
			return "";
		List<CompareInterfaceDescriptions> compareList = compareInterfaceDescriptionMessages(pm);
		return compareListToHtmlTable(compareList);
	}

	/**
	 * Generate an HTML table from the given list.
	 * @param compareList
	 * @return string within "&lt;html&gt;" tag
	 */
	private String compareListToHtmlTable(List<CompareInterfaceDescriptions> compareList) {
		if(compareList == null)
			return null;
		Collections.sort(compareList);
		StringBuilder sb = new StringBuilder("<html><p>Comparing component settings between the graph and user interface:</p>");
		sb.append("<br />\n<table cellpadding=\"1\" cell spacing=\"1\" border=\"1\">");
		sb.append("\n").append((new CompareInterfaceDescriptions(null)).toHtmlHeader());
		for(CompareInterfaceDescriptions cid : compareList)
			sb.append("\n").append(cid.toHtmlRow());
		sb.append("\n").append("</table></html>");
		return sb.toString();
	}

	/**
	 * Compare the {@value MsgType#INTERFACE_DESCRIPTION} messages in {@link #interfaceDescriptionsFromInterface}
	 * against those in the {@link ProblemModel}.
	 * @param pm
	 * @return list of {@link CompareInterfaceDescriptions} results
	 */
	public List<CompareInterfaceDescriptions> compareInterfaceDescriptionMessages(ProblemModel pm) {

		if(interfaceDescriptionsFromInterface == null)
			return null;    // no interface settings to compare

		List<CompareInterfaceDescriptions> result = new ArrayList<CompareInterfaceDescriptions>();
		Map<String, MessageObject> siDescs = (Map<String, MessageObject>) interfaceDescriptionsFromInterface.clone();
		
		Iterator<MessageObject> it = pm.startNodeMessagesIteratorForStartStateModel();
		while(it.hasNext()) {                                 // checked hasNext() above
			MessageObject msg = it.next();
			if(!(MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(msg.getMessageType())))
				continue;
			CompareInterfaceDescriptions cid = new CompareInterfaceDescriptions(msg);
			if(cid.pmCommName != null) {
				MessageObject siDesc = siDescs.remove(cid.pmCommName.toLowerCase());
				try {
					cid.setStudentInterfaceFields(siDesc);
				} catch(WidgetTypeException wte) {
					trace.err("Component type mismatch in "+pm.getProblemName()+": "+wte);
					cid.setAlert("A component with this name has a different type in the student interface.");
					CompareInterfaceDescriptions cidSI = new CompareInterfaceDescriptions(null);
					cidSI.setStudentInterfaceFields(siDesc);
					cidSI.setAlert("A component setting for this name has a different type in the graph.");
					result.add(cidSI);
				}
			}
			result.add(cid);
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.compareIDMsgs() from graph "+cid);
		}

		// except for cases where there's a name collision but a type difference between pm and si, 
		// siDescs now has difference of sets from problemModel and interfaceDescriptionsFromInterface
		for(String siCommName : siDescs.keySet()) {
			CompareInterfaceDescriptions cid = new CompareInterfaceDescriptions(null);
			cid.setStudentInterfaceFields(siDescs.get(siCommName));
			result.add(cid);
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.compareIDMsgs() from iface "+cid);
		}
		return result;
	}

	/**
	 * Provide an iterator on the list of messages to send to the interface in the start state.
	 * @param ProblemModel to query
	 * @return iterator on result of {@link #createStartStateMessageList()}
	 */
	public Iterator<MessageObject> startNodeMessagesIterator(ProblemModel pm) {
		List<MessageObject> result = createStartStateMessageListInternal(pm, false);
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.startNodeMessagesIterator() list size "+result.size());
		return result.iterator();
	}

	/**
	 * Create the list of messages to send to the interface in the start state:<ol>
	 *   <li>starts with messages from {@link ProblemModel#startNodeMessagesIteratorForStartStateModel()};</li>
	 *   <li>removes any that match {@link #toBeDiscarded(MessageObject)};</li>
	 *   <li>adds all from {@link #interfaceDescriptionsToAdd};</li>
	 *   <li>adds all from {@link #otherMsgsFromInterface};</li>
	 *   <li>removes {@value MsgType#INTERFACE_ACTION} messages whose result would overwritten.</li>
	 * </ol>
	 * The algorithm is implemented in {@link #createStartStateMessageListInternal(ProblemModel)}.
	 * @param pm ProblemModel to query
	 * @param wantInterfaceDescriptions if true, we want all the InterfaceDescription messages
	 * @return {@link ArrayList} result from {@link #createStartStateMessageListInternal(ProblemModel)}
	 */
	public Vector<MessageObject> createStartStateMessageList(ProblemModel pm, boolean wantInterfaceDescriptions) {
		Vector<MessageObject> result =
				new Vector<MessageObject>(createStartStateMessageListInternal(pm, wantInterfaceDescriptions));
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.createStartStateMessageList() list size "+result.size());
		return result;
	}

	/**
	 * Create the list of messages to send to the interface in the start state. The algorithm is
	 * described in the comment for {@link #createStartStateMessageList(ProblemModel)}.
	 * @param pm ProblemModel to query
	 * @param wantInterfaceDescriptions if true, we want all the InterfaceDescription messages
	 * @return {@link LinkedList} of messages to send
	 */
	private synchronized LinkedList<MessageObject> createStartStateMessageListInternal(ProblemModel pm,
			boolean wantInterfaceDescriptions) {

		if(wantInterfaceDescriptions)
			chooseAllInterfaceDescriptionsFromInterface();
		else if(!pm.getStartNodeCreatedFlag()) // was (pm.getStartNode() == null), but node created before sslist
			preloadDefaultInterfaceDescriptions(pm);
		
		Set<String> interfaceActionKeys = new LinkedHashSet<String>();
		Set<String> untutoredActionKeys = new LinkedHashSet<String>();
		Map<String, MessageObject> startAndEnd = new HashMap<String, MessageObject>();

		LinkedList<MessageObject> result = new LinkedList<MessageObject>();

		for(MessageObject mo : nonInteractiveInterfaceActionsFromInterface) {
			String key = getInterfaceActionKey(mo);
			if(key != null)
				interfaceActionKeys.add(key);
			else if((key = getUntutoredActionKey(mo)) != null)
				untutoredActionKeys.add(key);
			result.add(mo);
		}

		Iterator<MessageObject> pmIter = pm.startNodeMessagesIteratorForStartStateModel();

		while(pmIter.hasNext()) {
			
			MessageObject mo = pmIter.next();
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.createSSMsgListInt() from pmIter: "+mo.summary());
			dumpSerialized(mo);
			
			if(toBeDiscarded(mo, true))
				continue;
			String key = getInterfaceActionKey(mo);
			if(key != null)
				interfaceActionKeys.add(key);
			else if((key = getUntutoredActionKey(mo)) != null)
				untutoredActionKeys.add(key);
			result.add(mo);
		}

		for(Iterator<String> it = interfaceDescriptionsToAdd.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			MessageObject mo = interfaceDescriptionsToAdd.get(key);
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.createSSMsgListInt() from intDescsToAdd: "+mo.summary());
			result.add(mo);
			it.remove();  // don't add it again later
		}

		for(MessageObject mo : otherMsgsFromInterface) {
			String key = getInterfaceActionKey(mo);
			if(key != null)
				interfaceActionKeys.add(key);
			else if((key = getUntutoredActionKey(mo)) != null)
				untutoredActionKeys.add(key);
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.createSSMsgListInt() from othMsgsToAdd: "+mo.summary());
			result.add(mo);
		}

		int i = 0, j = 0;
		for(ListIterator<MessageObject> li = result.listIterator(result.size()); li.hasPrevious(); ++i) {
			MessageObject mo = li.previous();
			if(recordLastSingles(mo, startAndEnd)) {  // retains last msg passed: keep first of each
				li.remove();
				++j;
				continue;
			}
			String key = getInterfaceActionKey(mo);
			if(key != null && !interfaceActionKeys.remove(key)) {  // remove() true only the first time
				++j;
				li.remove();                             // keep only last InterfaceAction for each key
				if(trace.getDebugCode("startstate"))
					trace.out("startstate", "SSM.createSSMsgListInt() removing IntAct "+mo.summary());
				continue;
			}
			key = getUntutoredActionKey(mo);
			if(key != null && !untutoredActionKeys.remove(key)) {  // remove() true only the first time
				++j;
				li.remove();                             // keep only last InterfaceAction for each key
				if(trace.getDebugCode("startstate"))
					trace.out("startstate", "SSM.createSSMsgListInt() removing UntutoredAct "+mo.summary());
				continue;
			}
			if(otherMsgsToDiscard.contains(mo.summary())) {
				li.remove();
				++j;
				if(trace.getDebugCode("startstate"))
					trace.out("startstate", "SSM.createSSMsgListInt() discarding other msg "+mo.summary());
				continue;
			}
		}
		
		j -= addSingles(result, startAndEnd, pm);  // decrement deletion count by no. msgs added

		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.createSSMsgListInt() removed "+j+" of "+i+" msgs");
		Collections.sort(result, msgComparator);

		if(trace.getDebugCode("startstate")) {
			int k = 0;
			trace.out("startstate", String.format("SSM.createSSMsgListInt() returns %d msgs:\n", result.size()));
			for(MessageObject mo : result)
				System.out.printf("  [%2d] %s\n", k++, mo.summary());
		}
		return result;
	}

	/**
	 * Dump the attributes of the 1st child of a <serialized> element.
	 * @param mo message to scan
	 */
	private void dumpSerialized(MessageObject mo) {
		if(!trace.getDebugCode("startstate"))
			return;
		Object srzObj = mo.getProperty("serialized");
		if(!(srzObj instanceof Element))
			trace.out("startstate", "dumpSerialized: missing <serialized> element in "+mo.summary());
		else {
			Element srzElt = (Element) srzObj;
			List<Element> srzChildren = (List<Element>) srzElt.getChildren();
			trace.out("startstate", String.format("dumpSerialized: serialized: <%s>, nChildren = %d",
					srzElt.getName(), (srzChildren == null ? -1 : srzChildren.size())));
			Iterator<Attribute> attrs = ((List<Attribute>) srzElt.getAttributes()).iterator();
			for(int i = 0; attrs.hasNext(); ++i) {
				Attribute attr = attrs.next();
				trace.out("startstate", String.format("  [%2d] %-17s = \"%s\"",
						i, attr.getName(), attr.getValue()));
			}
		}
	}

	/**
	 * Ensure that the result sequence includes one {@value MsgType#START_PROBLEM} and one
	 * {@value MsgType#START_STATE_END} message.
	 * @param result
	 * @param startAndEnd
	 * @return number of messages added
	 */
	private int addSingles(LinkedList<MessageObject> result,
			Map<String, MessageObject> startAndEnd, ProblemModel pm) {

		for(Map.Entry<String, Boolean> singleType : SinglesTypes.entrySet()) {
			if(!singleType.getValue().booleanValue())
				continue;                                                  // not required
			if(startAndEnd.containsKey(singleType.getKey()))
				continue;                                                  // already present

			MessageObject mo = MessageObject.create(singleType.getKey());
			if(MsgType.START_PROBLEM.equalsIgnoreCase(singleType.getKey()))
				mo.setProperty(MsgType.PROBLEM_NAME, pm.getProblemName());

			startAndEnd.put(singleType.getKey(), mo);
		}
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.addStartAndEnd() msgs to add: "+startAndEnd.values());
		result.addAll(startAndEnd.values());
		return startAndEnd.size();
	}

	/**
	 * Copy any {@value MsgType#INTERFACE_DESCRIPTION} messages having a 
	 * {@link #interfaceDescriptionsFromInterface}
	 * @param pm access to {@link ProblemModel.getStoreAllInterfaceDescriptions()}
	 */
	private int preloadDefaultInterfaceDescriptions(ProblemModel pm) {
		int m = interfaceDescriptionsToAdd.size();
		boolean getAll = pm.getController().getUniversalToolProxy().getStoreAllInterfaceDescriptions();
		for(Map.Entry<String, MessageObject> entry : interfaceDescriptionsFromInterface.entrySet()) {
			if(getAll) {
				interfaceDescriptionsToAdd.put(entry.getKey(), entry.getValue());
				continue;
			}
			String moStr = entry.getValue().toXML();
			boolean interpolatable = CTATFunctions.interpolatable(moStr);
			boolean hasMassProdRef = ProblemModel.hasMassProductionVarPattern(moStr);
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("SSM.preloadDefaultIntDescs(): %s interpolatable %b hasMassProdRef %b",
						entry.getValue().summary(), interpolatable, hasMassProdRef));
			if(interpolatable || hasMassProdRef)
				interfaceDescriptionsToAdd.put(entry.getKey(), entry.getValue());
		}
		int n = interfaceDescriptionsToAdd.size();
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.preloadDefaultIntDescs(): UTP.storeAllInterfaceDescriptions "+
					getAll+ "; interfaceDescriptionsToAdd.size() was "+m+", now "+n);
		return n;
	}

	/**
	 * Message types that must appear at most once in the final sequence of start state messages.
	 * This list is used by {@link #recordLastSingles(MessageObject, Set)} and
	 * {@link #addStartAndEnd(LinkedList, Map)}.
	 */
	private static final Map<String, Boolean> SinglesTypes = new HashMap<String, Boolean>();

	static {
		SinglesTypes.put(MsgType.START_PROBLEM,   Boolean.TRUE);
		SinglesTypes.put(MsgType.START_STATE_END, Boolean.TRUE);
	}

	/**
	 * Method to help remove duplicates that might arise among the single messages. These include
	 * {@value MsgType#START_PROBLEM} and {@value MsgType#START_STATE_END}. If the message type is
	 * in {@link #SinglesTypes}, save the message to the map.
	 * @param mo
	 * @param startAndEnd save message type here if not already present
	 * @return true if saved; false if not
	 */
	private boolean recordLastSingles(MessageObject mo, Map<String, MessageObject> startAndEnd) {
		String msgType = mo.getMessageType();
		if(!SinglesTypes.containsKey(msgType))
			return false;
		startAndEnd.put(msgType, mo);
		return true;
	}

	/**
	 * Sort start state messages by {@link MessageObject#getMessageType()} as follows:<ol>
	 * <li>{@value MsgType#START_PROBLEM}</li>
	 * <li>any values not in this list</li>
	 * <li>{@value MsgType#INTERFACE_DESCRIPTION}</li>
	 * <li>{@value MsgType#INTERFACE_ACTION}</li>
	 * <li>{@value MsgType#START_STATE_END}</li>
	 * </ol>
	 */
	static class MsgComparator implements Comparator<MessageObject> {
		
		static Map<String, Integer>types = new LinkedHashMap<String, Integer>();
		static {
			types.put(MsgType.START_PROBLEM.toLowerCase(),         Integer.valueOf(0));
			types.put("",                                          Integer.valueOf(100));
			types.put(MsgType.INTERFACE_DESCRIPTION.toLowerCase(), Integer.valueOf(200));
			types.put(MsgType.INTERFACE_ACTION.toLowerCase(),      Integer.valueOf(300));
			types.put(MsgType.START_STATE_END.toLowerCase(),       Integer.valueOf(400));
		}

		/**
		 * Order the 2 messages as they should be in the start state. 
		 * @param o1 1st message
		 * @param o2 2nd message
		 * @return -1 if o1 precedes o2; 1 if o2 precedes o1; 0 if it doesn't matter
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(MessageObject o1, MessageObject o2) {

			if(o1 == null)                  // nulls go last
				return (o2 == null ? 0 : 1);
			else if(o2 == null)
				return -1;
			
			String t1 = o1.getMessageType().toLowerCase();
			String t2 = o2.getMessageType().toLowerCase();

			if(t1 == null)                  // nulls go last
				return (t2 == null ? 0 : 1);
			else if(t2 == null)
				return -1;
			
			if(t1.equals(t2))     // same type
				return 0;

			Integer i1 = types.get(t1); if(i1 == null) i1 = types.get("");
			Integer i2 = types.get(t2); if(i2 == null) i2 = types.get("");
			return i1.compareTo(i2);
		}
	}

	/** Single instance for all callers. */
	private static MsgComparator msgComparator = new MsgComparator();
	
	/**
	 * Return the selection and action elements of the SAI in an 
	 * {@value MsgType#UNTUTORED_ACTION} message, unless the action indicates that the result of
	 * processing a sequence of several such messages is different from that of processing just
	 * the last message in the sequence. 
	 * @param mo message to scan
	 * @return null if not of type {@value MsgType#UNTUTORED_ACTION} or special case above;
	 *         else selection and action vectors stringified and set to lower case
	 */
	private String getUntutoredActionKey(MessageObject mo) {
		return getKeepOnlyLastActionKey(mo, MsgType.UNTUTORED_ACTION);
	}
	
	/**
	 * Return the selection and action elements of the SAI in an 
	 * {@value MsgType#INTERFACE_ACTION} message, unless the action indicates that the result of
	 * processing a sequence of several such messages is different from that of processing just
	 * the last message in the sequence. 
	 * @param mo message to scan
	 * @return null if not of type {@value MsgType#INTERFACE_ACTION} or special case above;
	 *         else selection and action vectors stringified and set to lower case
	 */
	private String getInterfaceActionKey(MessageObject mo) {
		return getKeepOnlyLastActionKey(mo, MsgType.INTERFACE_ACTION);
	}
	
	/**
	 * Return the selection and action elements of the SAI in a message of the 
	 * given message type, unless the action indicates that the result of
	 * processing a sequence of several such messages is different from that of processing just
	 * the last message in the sequence. 
	 * @param mo message to scan
	 * @param message type to match
	 * @return null if not of given type or special case above;
	 *         else selection and action vectors stringified and set to lower case
	 */
	private String getKeepOnlyLastActionKey(MessageObject mo, String msgType) {
		if(!(msgType.equalsIgnoreCase(mo.getMessageType())))
			return null;
		List<String> action = mo.getAction();
		if(action == null || action.size() < 1)
			return null;

		if("AddPoint".equalsIgnoreCase(action.get(0)))  // preserve all NumberLine.AddPoint
			return null;                                // add here any others to preserve

		List<String> selection = mo.getSelection();
		if(selection == null || selection.size() < 1)
			return null;

		return selection.toString().toLowerCase() + ' ' + action.toString().toLowerCase(); 		
	}

	/**
	 * Tell whether a message should be discarded.
	 * @param mo
	 * @param fromProblemModel false if message was from the interface; true if from the ProblemModel
	 * @return
	 */
	private boolean toBeDiscarded(MessageObject mo, boolean fromProblemModel) {
		if(fromProblemModel) {
			if(MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(mo.getMessageType())) {
				if(interfaceDescriptionsToDiscard.contains(mo))
					return true;					
			}
		}
		return false;
	}

	/**
	 * Record whether the {@link ProblemModel} now has the current start state. If so,
	 * clear the contents of the message lists saved in this instance.
	 * @param updated true means {@link ProblemModel} is current; no-op if false 
	 */
	public void problemModelUpdated(boolean updated) {
		if(!updated)
			return;
		nonInteractiveInterfaceActionsFromInterface.clear();
		otherMsgsFromInterface.clear();
		interfaceDescriptionsFromInterface.clear();
		selectionToActionNamesMap.clear();
	}

	/**
	 * Call this with argument author is about to edit the start state.
	 * Sets {@link #userBeganStartStateEdit}.
	 * @param beginning true when author initiates action; false when ready for edits
	 * @return previous value of {@link #userBeganStartStateEdit}
	 */
	public boolean setUserBeganStartStateEdit(boolean beginning) {
		boolean result = userBeganStartStateEdit;
		userBeganStartStateEdit = beginning;
		return result;
	}

	/**
	 * Apply changes to InterfaceDescription messages and copy our start state messages
	 * from {@link #createStartStateMessageList(ProblemModel)} to the graph.
	 * @param pm {@link ProblemModel} instance to change
	 * @param cidList if not null, InterfaceDescription changes to apply
	 *                via {@link #applyInterfaceDescriptionEdits(List)}
	 * @param undoActionName name for the undo action to register
	 */
	public void applyEditsToProblemModel(ProblemModel pm, List<CompareInterfaceDescriptions> cidList,
			String undoActionName) {
		applyEditsToProblemModel(pm, cidList, null, undoActionName);
	}

	/**
	 * Delete messages listed in otherMsgsToDiscard and copy our start state messages
	 * from {@link #createStartStateMessageList(ProblemModel)} to the graph.
	 * @param pm {@link ProblemModel} instance to change
	 * @param otherMsgsToDiscard if not null, messages to discard via {@link #discardMessages(Set)}
	 * @param undoActionName name for the undo action to register
	 */
	public void applyEditsToProblemModel(ProblemModel pm, Set<MessageObject> otherMsgsToDiscard, 
			String undoActionName) {
		applyEditsToProblemModel(pm, null, otherMsgsToDiscard, undoActionName);
	}

	/**
	 * Common code for {@link #applyEditsToProblemModel(ProblemModel, List, String)} and
	 * {@link #applyEditsToProblemModel(ProblemModel, Set, String)}. At end, calls {@link #clearEdits()}.
	 * @param pm {@link ProblemModel} instance to change
	 * @param cidList if not null, InterfaceDescription changes to apply
	 *                via {@link #applyInterfaceDescriptionEdits(List)}
	 * @param otherMsgsToDiscard if not null, messages to discard via {@link #discardMessages(Set)}
	 * @param undoActionName name for the undo action to register
	 */
	private void applyEditsToProblemModel(ProblemModel pm, List<CompareInterfaceDescriptions> cidList,
			Set<MessageObject> otherMsgsToDiscard, String undoActionName) {
		if(pm == null)
			return;
		
		applyInterfaceDescriptionEdits(cidList);
		discardMessages(otherMsgsToDiscard);
		pm.setStartNodeMessageVector(createStartStateMessageList(pm, false));
		
		ActionEvent ae = new ActionEvent(this, 0, undoActionName);
		pm.getController().getUndoPacket().getCheckpointAction().actionPerformed(ae);
		
		clearEdits();		
	}

	/**
	 * Clear {@link #interfaceDescriptionsToDiscard}, {@link #interfaceDescriptionsToAdd},
	 * {@link #otherMsgsToDiscard}.
	 */
	private void clearEdits() {
		interfaceDescriptionsToDiscard.clear();
		interfaceDescriptionsToAdd.clear();
		otherMsgsToDiscard.clear();
	}

	/**
	 * Set up {@link #interfaceDescriptionsToAdd}, etc. from the given list. 
	 * @param cidList list of changes to effect
	 */
	private void applyInterfaceDescriptionEdits(List<CompareInterfaceDescriptions> cidList) {
		if(cidList == null)
			return;
		interfaceDescriptionsToDiscard.clear();
		interfaceDescriptionsToAdd.clear();
		for(CompareInterfaceDescriptions cid : cidList) {
			if(cid.getPmCommName() != null && cid.getPmCommName().length() > 0) {
				if(cid.getChoice() != Choice.keepPM)
					interfaceDescriptionsToDiscard.add(cid.pmMsg);
			}
			if(cid.getSiCommName() != null && cid.getSiCommName().length() > 0) {
				if(cid.getChoice() == Choice.saveSI) 
					interfaceDescriptionsToAdd.put(cid.getSiCommName().toLowerCase(), cid.siMsg);
			}
		}
	}

	/**
	 * Tell whether any edits have been made.
	 * @return true if any of these is nonempty: {@link #interfaceDescriptionsToAdd},
	 *         {@link #interfaceDescriptionsToDiscard} or {@link #otherMsgsFromInterface}
	 */
	public boolean isStartStateModified() {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.isStartStateModified() nToDiscard "+interfaceDescriptionsToDiscard.size()+
					", nToAdd "+interfaceDescriptionsToAdd.size()+
					", nOthers "+otherMsgsFromInterface.size());
		return  (interfaceDescriptionsToDiscard.size() > 0) ||
				(interfaceDescriptionsToAdd.size() > 0) ||
				(otherMsgsFromInterface.size() > 0);
	}

	/**
	 * Act as if the author had chosen the student interface settings for this component in the
	 * start state.
	 * @param siMsg {@value MsgType#INTERFACE_DESCRIPTION} msg from student interface
	 * @param pm where to commit the change
	 */
	public void commitSISettings(MessageObject siMsg, ProblemModel pm) {
		CompareInterfaceDescriptions cid = new CompareInterfaceDescriptions(null);
		cid.setStudentInterfaceFields(siMsg);
		cid.setChoice(Choice.saveSI);
		if(trace.getDebugCode("startstate"))
			trace.printStack("startstate", "commitSISettings() cid "+cid);
		commitCID(cid, pm);
	}

	/**
	 * Apply a {@link CompareInterfaceDescriptions} instance directly to the start state. 
	 * @param cid instance to apply
	 * @param pm where to commit the change
	 */
	private void commitCID(CompareInterfaceDescriptions cid, ProblemModel pm) {
	}

	/**
	 * @return {@link #interfaceDescriptionsFromInterface}.size()
	 */
	public int nInterfaceDescriptionsFromInterface() {
		int result = interfaceDescriptionsFromInterface.size();
		if(trace.getDebugCode("startstate"))
			trace.printStack("startstate", "SSM.nInterfaceDescriptionsFromInterface() returns "+result);
		return result;
	}

	/**
	 * Copy all in {@link #interfaceDescriptionsFromInterface} to {@link #interfaceDescriptionsToAdd}.
	 * @return new size of {@link #interfaceDescriptionsToAdd}
	 */
	public int chooseAllInterfaceDescriptionsFromInterface() {
		int m = interfaceDescriptionsToAdd.size();
		for(Map.Entry<String, MessageObject> entry : interfaceDescriptionsFromInterface.entrySet())
			interfaceDescriptionsToAdd.put(entry.getKey(), entry.getValue());
		int n = interfaceDescriptionsToAdd.size();
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "SSM.chooseAllIntDescsFrInt.interfaceDescriptionsToBeAdded.size() was "+
					m+", now "+n);
		return n;
	}
	
	/**
	 * Process a list of {@value MsgType#INTERFACE_DESCRIPTION} msgs. Fire just one event for
	 * the entire update.
	 * @param messages the list
	 */
	public void addStudentInterfaceMessageBundle(List<MessageObject> messages) {
		int i = 0;
		for(MessageObject msg : messages) {
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.addAllStuIntMsgs["+i+"] "+msg.summary());
			addStudentInterfaceMessageInternal(msg, false);  // false == noninteractive since from bundle
			++i;
		}
		pcSupport.firePropertyChange("Student Interface messages added", null, messages);
	}

	/** Set of MessageTypes that should not be in the start state. */
	private static Set<String> msgTypesToOmitFromStartState = new HashSet<String>();
	static {
		msgTypesToOmitFromStartState.add(MsgType.GET_ALL_INTERFACE_DESCRIPTIONS.toLowerCase());
		msgTypesToOmitFromStartState.add(MsgType.INTERFACE_IDENTIFICATION.toLowerCase());
		msgTypesToOmitFromStartState.add(MsgType.SET_PREFERENCES.toLowerCase());
	}
    
    /**
     * Filter to prevent certain messages from being in the set of saved start state messages.
     * @param mo candidate start state message
     * @return true if {@link #msgTypesToOmitFromStartState} does <i>not</i> contain msg's MessageType;
     *         false if {@link MessageObject#getMessageType()} is null
     */
	public static boolean isProperStartStateMessage(MessageObject mo) {
		String msgType = mo.getMessageType();
		if (msgType == null)
			return false;
		return !(msgTypesToOmitFromStartState.contains(msgType.toLowerCase()));
	}

	/**
	 * @return all the component names, as a set of strings.
	 */
	public Set<String> getComponentNames() {
		Set<String> result = new LinkedHashSet<String>();
		for(MessageObject mo : interfaceDescriptionsFromInterface.values()) {
			String instName = getInstanceName(mo);
			if(instName != null)
				result.add(instName);
			String groupName = getGroupName(mo);
			if(groupName != null)
				result.add(groupName);
			result.addAll(getTableCellNames(mo));
			result.addAll(systemSelectionNames);
		}
		return result;
	}

	/**
	 * Generate a list of table cell names from a {@value MsgType#INTERFACE_DESCRIPTION} from a
	 * table component. 
	 * @param msg {@value MsgType#INTERFACE_DESCRIPTION} message
	 * @return cell names calculated from "Columns" and "Rows" elements; empty list if msg is not
	 *         correct type or is not from a ".*Table" component
	 */
	private List<String> getTableCellNames(MessageObject msg) {
		List<String> cellNames = new LinkedList<String>();
		if(!MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(msg.getMessageType()))
			return cellNames;
		
		Object widgetType = msg.getProperty(WIDGET_TYPE);
		if(!(widgetType instanceof String) || !((String) widgetType).endsWith("Table"))
			return cellNames;

		Object tblName = getInstanceName(msg);
		if(!(tblName instanceof String) || ((String) tblName).length() < 1) {
			trace.err(String.format("Error reading %s message for Table component:"+
					" invalid table name \"%s\"; msg:\n  %s",
					MsgType.INTERFACE_DESCRIPTION, tblName, msg));
			return cellNames;			
		}
		Object rows = msg.getProperty("Rows"), cols = msg.getProperty("Columns");
		try {
			int nR = Integer.parseInt(rows.toString()), nC = Integer.parseInt(cols.toString());
			for(int c = 1; c <= nC; ++c) {
				for(int r = 1; r <= nR; ++r)
					cellNames.add(String.format("%s_C%dR%d", tblName, c, r));
			}
		} catch(Exception e) {
			trace.err(String.format("Error reading %s message for Table component: cannot parse"+
					" number of rows \"%s\" or columns \"%s\" as integer: %s, cause %s; msg:\n  %s",
					MsgType.INTERFACE_DESCRIPTION, rows, cols, e, e.getCause(), msg));
		}
		return cellNames;
	}

	/**
	 * Extract the group name parameter from a {@value MsgType#INTERFACE_DESCRIPTION} message
	 * using the XML path
	 * &lt;serialized&gt;&lt;<i>type</i>&gt;&lt;Parameters&gt;&lt;selection&gt;&lt;CTATComponentParameter&gt;&lt;value&gt;
	 * @param mo {@value MsgType#INTERFACE_DESCRIPTION} message
	 * @return content of &lt;value&gt; child of CTATComponentParameter element, if present;
	 *         returns null instead of empty string
	 */
	private String getGroupName(MessageObject mo) {
		Object serialized = mo.getProperty(SERIALIZED);
		Element typeRoot = null, parameters = null, selection = null, componentParameter = null, name = null, value = null;
		try {
			if(!(serialized instanceof Element))
				return null;
			typeRoot = (Element) serialized;
			parameters = typeRoot.getChild("Parameters");
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "SSM.getGroupName("+mo.summary()+") typeRoot "+trace.nh(typeRoot)+
						", parameters "+trace.nh(parameters));
			if(parameters == null)
				return null;
			selection = parameters.getChild("selection");
			List<Element> selectionChildren = (List<Element>) selection.getChildren("CTATComponentParameter");
			for(Iterator<Element> it = selectionChildren.iterator(); it.hasNext(); ) {
				componentParameter = it.next();
				name = componentParameter.getChild("name");
				if(!(name instanceof Element) || !GROUP.equalsIgnoreCase(name.getText()))
					continue;
				value = componentParameter.getChild("value");
				String result = value.getText();
				if(result == null || result.length() < 1)
					return null;
				return result;
			}			
		} catch(Exception e) {
			trace.err(String.format("Error getting group name from %s element: "+
					"typeRoot %s, parameters %s, selection %s, componentParameter %s, name %s, value %s",
					trace.nh(serialized), typeRoot, parameters, selection, componentParameter, name, value));
		}
		return null;
	}

	/**
	 * Return the set of actions supported by the given component.
	 * @param selection name of component to query
	 * @return supported actions from component's {@value MsgType#INTERFACE_DESCRIPTION} message
	 */
	public Set<String> getActionNames(String selection) {
		if(trace.getDebugCode("editstudentinput"))
			trace.out("editstudentinput", "SSM.getActionNames("+selection+")");
		if(selection == null || selection.trim().length() < 1)
			return new HashSet<String>();
		Set<String> result = selectionToActionNamesMap.get(selection.toLowerCase());
		if(result == null)
			return new HashSet<String>();
		else
			return result;
	}

	/**
	 * @return {@link #defaultActionNames}, copied into a list
	 */
	public List<String> getAllActionNames() {
		return new ArrayList<String>(defaultActionNames);
	}

	/**
	 * Schedule any {@value MsgType#INTERFACE_DESCRIPTION} messages in the start state that lack
	 * mass production or formula references for deletion. Adds them to {@link #interfaceDescriptionsToDiscard}.
	 * @param pm no-op if null
	 * @param execute if true, actually schedule the messages for deletion;
	 *                if false, just count how many would be deleted
	 * @return number of messages marked for deletion
	 */
	public int pruneInterfaceDescriptions(ProblemModel pm, boolean execute) {
		int result = 0;
		if(pm == null)
			return result;
		if(UniversalToolProxy.JAVA.equalsIgnoreCase(
				pm.getController().getUniversalToolProxy().getStudentInterfacePlatform()))
			return result;
		Iterator<MessageObject> pmIter = pm.startNodeMessagesIteratorForStartStateModel();
		while(pmIter.hasNext()) {
			MessageObject mo = pmIter.next();
			boolean isIntDesc = MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(mo.getMessageType());
			boolean interpolatable = true;
			boolean hasMassProdRef = true;
			if(isIntDesc) {
				String moStr = mo.toXML();
				interpolatable = CTATFunctions.interpolatable(moStr);
				hasMassProdRef = ProblemModel.hasMassProductionVarPattern(moStr);
			}
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("SSM.pruneInterfaceDescriptions(): %s interpolatable %b hasMassProdRef %b",
						mo.summary(), interpolatable, hasMassProdRef));
			if(!isIntDesc || interpolatable || hasMassProdRef)
				continue;
			if(execute)
				interfaceDescriptionsToDiscard.add(mo);
			result++;
		}
		return result;
	}

	/**
	 * From {@link #interfaceDescriptionsFromInterface}, build a map whose keys are component
	 * instance names and values are the instance's type. 
	 * @return map instance name => instance type
	 */
	public Map<String, String> getInterfaceComponentsMap() {
		Map<String, String> result = new LinkedHashMap<String, String>();
		for(String name : interfaceDescriptionsFromInterface.keySet()) {
			MessageObject mo = interfaceDescriptionsFromInterface.get(name);
			if(!MsgType.INTERFACE_DESCRIPTION.equals(mo.getMessageType()))
				continue;
			String instName = getInstanceName(mo);
			if((instName) == null)
				continue;
			Object type = mo.getProperty(WIDGET_TYPE);
			result.put(instName, type == null ? "" : type.toString());
			String groupName = getGroupName(mo);
			if(groupName != null)
				result.put(groupName, GROUP);
			for(String cellName : getTableCellNames(mo))
				result.put(cellName, CELL);
			for(String systemComponent : systemSelectionNames)
				result.put(systemComponent, SYSTEM);
		}
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "SSM.getInterfaceComponentsMap() returns\n    "+result);
		return result;
	}

	/**
	 * @param msgs messages to add to {@link #otherMsgsToDiscard}
	 */
	public void discardMessages(Set<MessageObject> msgs) {
		if(msgs == null)
			return;
		for(MessageObject msg : msgs)
			otherMsgsToDiscard.add(msg.summary());
	}
	
	/** Output generator for compact string output without preserving whitespace. */
	private static final XMLOutputter xmlOutputter = new XMLOutputter();
	static {
		Format fmt = Format.getCompactFormat();
		fmt.setExpandEmptyElements(false);
		fmt.setOmitDeclaration(true);
		fmt.setLineSeparator("\r\n");
		fmt.setIndent("  ");
		fmt.setTextMode(Format.TextMode.TRIM);
		xmlOutputter.setFormat(fmt);
	}
	
	/**
	 * Try to save the student interface to the given file.
	 * @param chosenFile
	 * @throws Exception
	 */
	public void saveStudentInterfaceFile(File chosenFile) throws Exception {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(chosenFile));

		Element root = new Element(MsgType.StartStateMessages);
		for(MessageObject mo : interfaceDescriptionsFromInterface.values())
			root.addContent(mo.toElement());

		Document doc = new Document(root);
		xmlOutputter.output(doc, bos);
		
		bos.close();
	}
}
