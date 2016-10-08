/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.text.JTextComponent;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.StartStateModel;

/**
 * The superclass is parametrized in Java 7, but not in Java 6.
 */
public class CtatListModel extends AbstractListModel
		implements StartStateModel.Listener {

	/**
	 * Possible states of the list.
	 */
	enum State {
		
		/** List has been populated. */   complete("complete"),
		/** Waiting on entries. */        waiting("... waiting ..."),
		/** Cannot fill the list. */      cannotFill("<interface disconnected>");
		
		/** String to display. */         private final String display;
		
		/** 
		 * @param display value for {@link #display}
		 */
		State(String display) { this.display = display; }

		/**
		 * @return {@link #display}
		 * @see java.lang.Enum#toString()
		 */
		public String toString() { return display; }
	}

	/** For serialization. */
	private static final long serialVersionUID = 201403051800L;
	
	/** The items themselves: assumes no duplicates. */
	protected List<String> delegate = new ArrayList<String>();
	
	/** State of the list data. */
	private State state = State.waiting;

	/**
	 * Populate the underlying list or call {@link #setAwaitingItems(boolean) setAwaitingItems(true)}.
	 */
	public CtatListModel(Collection<String> items) {
		if(items == null)
			setState(State.waiting);
		else
			addAll(items);
	}

	/**
	 * @return "[<i>class size state</i>]"
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder('[');
		sb.append(getClass().getSimpleName());
		sb.append(", size ").append(getSize());
		sb.append(", state ").append(state);
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * @return true if {@link #state} is {@value State#complete}
	 */
	public boolean hasData() {
		return (state == State.complete);
	}

	/**
	 * @return true if {@link #state} is {@value State#cannotFill}
	 */
	public boolean getCannotFill() {
		return (state == State.cannotFill);
	}

	/**
	 * @param cannotFill if true, set {@link #state} to {@value State#cannotFill}; no-op if false
	 * @return prior value of {@link #state}, as a String
	 */
	public String setCannotFill(boolean cannotFill) {
		State result = state;
		if(cannotFill)
			setState(State.cannotFill);
		return result.toString();
	}

	/**
	 * If argument is not {@value State#complete}, clears {@link #delegate} and inserts
	 * the single item given by {@link State#toString()}. If state is {@value State#complete},
	 * calls {@link #notifyAll()}.
	 * @param state new value for {@link #state}
	 */
	private synchronized void setState(State state) {
		this.state = state;
		if(hasData())
			notifyAll();
		else {
			delegate.clear();
			delegate.add(state.toString());
		}
	}

	/**
	 * Call {@link #addAll(Collection, boolean) addAll(items, true)} to get a sorted list.
	 * @param items list to copy in
	 * @param sort whether to sort the list
	 */
	public void addAll(Collection<String> items) {
		addAll(items, true);
	}

	/**
	 * Clear the contents of {@link #delegate} and insert all items in the given list.
	 * Calls {@link #setAwaitingItems(boolean) setAwaitingItems(false)}.
	 * Generates a single {@link ListDataEvent#CONTENTS_CHANGED} event.
	 * @param items list to copy in
	 * @param sort whether to sort the list
	 */
	protected synchronized void addAll(Collection<String> items, boolean sort) {
		if(trace.getDebugCode("editstudentinput"))
			trace.printStack("editstudentinput", "CtatListModel.addAll("+trace.nh(items)+") size "+
					items.size()+"; listenerCount "+listenerList.getListenerCount());
		delegate.clear();
		delegate.addAll(items);
		if(sort)
			Collections.sort(delegate, String.CASE_INSENSITIVE_ORDER);
		setState(State.complete);
		fireContentsChanged(this, 0, getSize()-1);  // ListModel specifies inclusive indices
	}
	
	/**
	 * @return {@link #delegate}.{@link ArrayList#size() size()}
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return delegate.size();
	}

	/**
	 * @param index
	 * @return {@link #delegateList}.{@link ArrayList#get(int) get(index)}
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return delegate.get(index);
	}

	/**
	 * Calls {@link #addAll(Collection)} with result from {@link StartStateModel#getComponentNames()}.
	 * @param evt
	 * @see edu.cmu.pact.ctat.model.StartStateModel.Listener#startStateReceived(java.util.EventObject)
	 */
	public void startStateReceived(EventObject evt) {
		if(!(evt.getSource() instanceof StartStateModel)) {
			trace.err("CtatListModel.startStateReceived("+evt+") caller not StartStateModel");
			return;			
		}
		StartStateModel ssm = (StartStateModel) evt.getSource();
		addAll(ssm.getComponentNames());
	}

	/**
	 * @return {@link #delegate}.{@link List#iterator() iterator()}
	 */
	public Iterator<String> iterator() {
		return delegate.iterator();
	}
}
