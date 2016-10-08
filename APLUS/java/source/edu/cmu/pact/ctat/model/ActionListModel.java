/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.text.JTextComponent;

import edu.cmu.pact.Utilities.trace;

/**
 * Customized list model for Action names, which can be sensitive to a chosen component.
 */
public class ActionListModel extends CtatListModel implements
		PropertyChangeListener {

	private static final long serialVersionUID = 201403142005L;

	/** Source of component information. */
	private StartStateModel startStateModel;
	
	/** Currently-chosen component name. */
	private String selection;
	
	/** Actions supported by currently-chosen component. */
	private Set<String> selectionActions = new HashSet<String>();

	public ActionListModel(Collection<String> items, StartStateModel startStateModel) {
		super(items);
		this.startStateModel = startStateModel;
	}

	/**
	 * Tell whether the given action name is known to be supported by the current {@link #selection}.
	 * @param actionName
	 * @return true if {@link #selectionActions} contains actionName.
	 */
	public boolean isActionForSelection(String actionName) {
		if(actionName == null)
			return false;
		return selectionActions.contains(actionName);
	}

	/**
	 * If the changed property's name is "text," set {@link #selection} to the result of
	 * {@link PropertyChangeEvent#getNewValue()} and pass it to
	 * {@link StartStateModel#getActionNames(String)} to populate {@link #selectionActions}
	 * with the actions proper to that selection. Then merge that set with
	 * {@link StartStateModel#getAllActionNames()} and sort the result, ordering the actions
	 * proper to this selection first in the list.
	 * @param evt no-op if source not a {@link JTextComponent} or propertyName not "text"
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(trace.getDebugCode("editstudentinput"))
			trace.out("editstudentinput", "ActionListModel.propertyChange("+evt.toString()+")");
		if(startStateModel == null)
			return;
		if(!(evt.getSource() instanceof JTextComponent))
			return;
		if(evt.getPropertyName() != "text")
			return;
		selection = ((JTextComponent) evt.getSource()).getText();
		reviseActionList(startStateModel);
	}
	
	/**
	 * Update {@link #selectionActions} and call {@link #addAll(Collection)} passing
	 * {@link StartStateModel#getAllActionNames()} merged with {@link #selectionActions}.
	 * @param startStateModel
	 */
	private void reviseActionList(StartStateModel startStateModel) {
		if(selection == null || selection.trim().length() < 1) {  // user might have blanked the selection
			selectionActions.clear();
			addAll(startStateModel.getAllActionNames());
		} else {
			selectionActions = startStateModel.getActionNames(selection);
			Set<String> resultSet = new LinkedHashSet<String>(startStateModel.getAllActionNames());
			resultSet.addAll(selectionActions);
			List<String> result = new ArrayList<String>(resultSet);
			Collections.sort(result, new SelectionBiasedComparator(selectionActions));
			addAll(result, false);   // false: don't sort again; generates event to listeners
		}
	}
	
	/**
	 * Calls {@link #reviseActionList(StartStateModel)} using source from event.
	 * @param evt {@link EventObject#getSource()} should be a {@link StartStateModel}
	 * @see edu.cmu.pact.ctat.model.StartStateModel.Listener#startStateReceived(java.util.EventObject)
	 */
	public void startStateReceived(EventObject evt) {
		if(!(evt.getSource() instanceof StartStateModel)) {
			trace.err("CtatListModel.startStateReceived("+evt+") caller not StartStateModel");
			return;			
		}
		reviseActionList((StartStateModel) evt.getSource());
	}
	

	/**
	 * @return {@link CtatListModel#toString()} plus {@link #selection}, {@link #selectionActions}
	 * @see edu.cmu.pact.model.CtatListModel#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		if(sb.charAt(sb.length()-1) == ']')
			sb.deleteCharAt(sb.length()-1);
		sb.append(", selection ").append(selection);
		sb.append(", selectionActions ").append(selectionActions);
		sb.append(']');
		return sb.toString();
	}
}

/**
 * Sort strings in a given set ahead of other strings. 
 */
class SelectionBiasedComparator implements Comparator<String> {
	
	/** The name of the component whose actions to sort first. */
	private final Set<String> selectionActions;

	/** Workhorse. */
	private final Comparator<String> defaultComparator = String.CASE_INSENSITIVE_ORDER;
	
	/**
	 * Populate {@link #selectionActions}.
	 * @param selection argument to {@link StartStateModel#getActionNames(String)}
	 */
	SelectionBiasedComparator(Set<String> selectionActions) {
		this.selectionActions = selectionActions;
	}

	/**
	 * Order members of {@link #selectionActions} ahead of nonmembers.
	 * @param o1
	 * @param o2
	 * @return -1 if o1 a member and o2 not; 1 if vice versa; otherwise
	 *         result from {@link #defaultComparator}
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String o1, String o2) {
		if(selectionActions == null)
			return defaultComparator.compare(o1, o2);
		boolean i1 = selectionActions.contains(o1);
		boolean i2 = selectionActions.contains(o2);
		if(i1 == i2)
			return defaultComparator.compare(o1, o2);  // both in or both out
		else
			return (i1 ? -1 : 1);                      // one in and one out
	}
}
