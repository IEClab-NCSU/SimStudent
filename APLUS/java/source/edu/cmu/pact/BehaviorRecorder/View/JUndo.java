/**@author Kevin Zhang **/

package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import edu.cmu.pact.Utilities.trace;

public class JUndo 
{
	/**
	 * @param J JTextComponent to which undo feature is added
	 * @return JTextUndoPacket (Client should use this to interface
	 * with the undo mechanism)
	 */
	public static JTextUndoPacket makeTextUndoable(JTextComponent J) 
	{
		//New JTextUndoPacket to be returned
		final JTextUndoPacket undoPack = new JTextUndoPacket(J);

		//InputMap for assigning key bindings
		InputMap map = J.getInputMap();

        int ctrlKeyMask = Event.CTRL_MASK;
        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
            ctrlKeyMask = ActionEvent.META_MASK;

        //Assign CTRL-Z to undoAction
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrlKeyMask);
		map.put(key, new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				undoPack.getUndoAction().actionPerformed(e);
			}
		});

		//Assign CTRL-Y to redoAction
		key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrlKeyMask);
		map.put(key, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				undoPack.getRedoAction().actionPerformed(e);
			}
		});

		return undoPack;
	}

	/**
	 * 
	 * @param S Object whose state is "undoable"
	 * @param n Size of undoable history. Negative value indicates
	 * unbounded size.
	 * @return JAbstractUndoPacket: Client should use this object to
	 * interface with the underlying undo mechanism. See individual
	 * methods for more detail.
	 */
	public static JAbstractUndoPacket makeAbstractUndoable(Undoable S, int n)
	{
		final JAbstractUndoPacket undoPack = new JAbstractUndoPacket(S, n);
		return undoPack;
	}

	public static class JTextUndoPacket
	{
		//JTextComponent being managed by this Packet
		private JTextComponent textComponent;

		//Delimiters allow for edit-groups
		private char[] delimiters = {' ', '\n', ',', '%'};

		private MyTextUndoManager undo;

		//These extend AbstractAction and allow client to
		//make calls to MyTextUndoManager
		private UndoAction undoAction;
		private RedoAction redoAction;

		public JTextUndoPacket(JTextComponent J) 
		{
			textComponent = J;

			undo = new MyTextUndoManager();

			undoAction = new UndoAction();
			undoAction.putValue(Action.NAME, Actions.Undo.toString());

			redoAction = new RedoAction();
			redoAction.putValue(Action.NAME, Actions.Redo.toString());

			//Retrieve listener from packet; add to Document
			Document doc = J.getDocument();
			doc.addUndoableEditListener((UndoableEditListener)undo);
		}

		/** Sets delimiters for grouping edits */
		public void setDelimiters(char[] c)
		{
			delimiters = c;
		}

		/** 
		 * 	Extends AbstractAction and implements ActionListener.
		 * 	Make call to .actionPerformed() on the
		 *  UndoAction object to perform undo action.
		 */
		public UndoAction getUndoAction()
		{
			return undoAction;
		}

		/** 
		 * 	Extends AbstractAction and implements ActionListener.
		 * 	Make call to .actionPerformed() on the
		 *  RedoAction object to perform redo action.
		 */
		public RedoAction getRedoAction() 
		{
			return redoAction;
		}

		/**
		 * Listener which is added to the Document of
		 * the JTextComponent of interest
		 */
		public UndoableEditListener getUndoListener()
		{
			return undo;
		}

		/**
		 * @return JTextComponent being managed by this
		 * JTextUndoPacket
		 */
		public JTextComponent getJTextComponent()
		{
			return textComponent;
		}

		@SuppressWarnings("serial")
		private class MyTextUndoManager extends UndoManager implements UndoableEditListener 
		{

			//Previous edit made
			private DocumentEvent prevEdit;
			//Location of previous edit
			//private int prevOffset;

			public void undoableEditHappened(UndoableEditEvent e) 
			{
				/* e originated from a Document and should be of type
				 * DefaultDocumentEvent
				 */
				DefaultDocumentEvent changed = (DefaultDocumentEvent) e.getEdit();
				DocumentEvent.EventType thisType = changed.getType();

				//offset: index of change in Document
				//length: size of change in Document
				int offset = changed.getOffset();
				int length = changed.getLength();

				//A flag for whether or not a new edit group should be created.
				//Upon undo, every sub-edit of the most recent group is undone.
				boolean newGroupRequired = false;

				/* if manager is unable to undo */
				if (!canUndo())
				{
					newGroupRequired = true;
				}

				/* if previous edit does not exist or if no valid undo is
				 * seen by manager */
				else if (prevEdit == null || this.editToBeUndone() == null)
				{
					newGroupRequired = true;
				} 
				/** CURRENT EDIT IS NOT FIRST EDIT; ASSUME PREVIOUS EDIT EXISTS **/

				/*
				 * if a new type of action is detected or if
				 * action is of length greater than 1 (bulk action)
				 */
				else if (!prevEdit.getType().equals(thisType) || length > 1)
				{
					newGroupRequired = true;
				}
				/** NO CHANGE DETECTED, ASSUME PREV EDIT IS SAME TYPE AS CURRENT EDIT **/

				else
				{
					/*  CASE: edit of type INSERT
					 * -Extract string representation of edit
					 * -Check for existence of delimiters
					 */
					if (thisType.equals(DocumentEvent.EventType.INSERT))
					{
						try 
						{
							//Examine nature of INSERT
							String delta = changed.getDocument().getText(offset, length);

							//Check for delimiters in INSERT
							for (char c : delimiters)
							{
								if (delta.contains(""+c))
								{
									newGroupRequired = true;
								}
							}
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}

						/* If this INSERT is not continuous with previous INSERT */
						if (prevEdit.getOffset()!= offset-1)
						{
							newGroupRequired = true;
						}
					}
					else if (thisType.equals(DocumentEvent.EventType.REMOVE))
					{
						/* If this REMOVE is not continuous with previous REMOVE */
						if (prevEdit.getOffset() != offset+1)
						{
							newGroupRequired = true;
						}
					} 
				}

				//current GroupEdit under consideration
				UndoableEdit currentGroup = this.editToBeUndone();
				prevEdit = changed;

				/* If no new group is required, add current edit to most
				 * recent group. Otherwise, create a new group and add it
				 * to the manager's list of groups.
				 */
				if (!newGroupRequired)
				{
					currentGroup.addEdit(changed);
				}
				else
				{
					//New group added
					MyCompoundGroupEdit newGroup = new MyCompoundGroupEdit();
					newGroup.addEdit(changed);
					this.addEdit(newGroup);
				}

				//Keep the UndoAction and RedoAction in sync with each other
				undoAction.updateUndoAction();
				redoAction.updateRedoAction();
			}

			private class MyCompoundGroupEdit extends CompoundEdit
			{
				//Has been performed (and has not been undone)
				private boolean isDone;

				public MyCompoundGroupEdit()
				{
					isDone = true;
				}

				/**
				 * @return number of sub-edits in this group
				 */
				public int getLength() 
				{
					return edits.size();
				}

				/**
				 * @return true iff this group of edits has been
				 * performed (and has not been undone)
				 */
				public boolean isDone()
				{
					return isDone;
				}

				/**
				 * Adds a sub-edit to this group. 
				 * @return true if addition was successful
				 */
				public boolean addEdit(UndoableEdit anEdit)
				{
					/** DO NOT ALLOW MANAGER TO ADD GROUP TO ITSELF **/
					if (anEdit instanceof MyCompoundGroupEdit)
						return false;
					else
						return super.addEdit(anEdit);
				}

				public boolean isSignificant()
				{
					return true;
				}

				/**
				 * Performs undo on this group of sub-edits
				 */
				public void undo() throws CannotUndoException 
				{
					super.undo();
					isDone = false;
				}

				/**
				 * Performs redo on this group of sub-edits
				 */
				public void redo() throws CannotUndoException
				{
					super.redo();
					isDone = true;
				}

				public boolean canUndo() 
				{
					return edits.size()>0 && isDone;
				}

				public boolean canRedo() 
				{
					return edits.size()>0 && !isDone;
				}
			}
		}
		/********** END OF MyTextUndoManager ***********/

		public class UndoAction extends AbstractAction implements ActionListener
		{
			public UndoAction()
			{
				super(Actions.Undo.toString());
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent e) 
			{
				try {
					undo.undo();
				} catch (CannotUndoException ex) {
					trace.errStack("Error on event "+e+" trying to undo "+undo.getPresentationName()+
							", limit "+undo.getLimit()+", undoPresName "+undo.getUndoPresentationName()+
							", canUndo "+undo.canUndo()+", inProgress "+undo.isInProgress(), ex);
				}

				updateUndoAction();
				redoAction.updateRedoAction();
			}

			private void updateUndoAction() 
			{
				if (undo.canUndo()) 
				{
					setEnabled(true);
					putValue(Action.NAME, undo.getUndoPresentationName());
				} else 
				{
					setEnabled(false);
					putValue(Action.NAME, Actions.Undo.toString());
				}
			}
		}

		public class RedoAction extends AbstractAction 
		{
			public RedoAction() 
			{
				super(Actions.Redo.toString());
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent e) 
			{
				try {
					undo.redo();
				} catch (CannotRedoException ex) {
					trace.out("Unable to redo: " + ex);
					// ex.printStackTrace();
				}
				updateRedoAction();
				undoAction.updateUndoAction();
			}

			private void updateRedoAction()
			{
				if (undo.canRedo()) 
				{
					setEnabled(true);
					putValue(Action.NAME, undo.getRedoPresentationName());
				} else
				{
					setEnabled(false);
					putValue(Action.NAME, Actions.Redo.toString());
				}
			}
		}
	}
	/*** END of JTextUndoPacket ***/

	static interface Undoable
	{
		public byte[] saveState();
		/**
		 * This method should perform all necessary operates to restore
		 * state of the current object. For example, this should include
		 * repainting if JAVA swing components are involved.
		 * 
		 * @param b byte array containing the image from which the state
		 * of the current object is restored
		 */
		public void derive(byte[] b);
	}
	
	public static enum Actions {
		Checkpoint,
		Clear,
		Undo,
		Redo,
		Validate,
		Initialize;
	};

	public static class JAbstractUndoPacket
	{
		private Undoable mainComponent;
		private MyAbstractUndoManager undo;

		//Size of undoable history
		private int historySize;

		/**** BEGIN: CLIENT-ACCESSIBLE OBJECTS **/
			//Action used to indicate that the current state of mainComponent
			//should be saved for later undo.
			private CheckpointAction checkpointAction;
	
			//Action used to reset current Packet 
			//(including but not limited to clearing undoable history)
			private ClearAction clearAction;
			
			/** Action used to reset the current packet and load a new initial state. */
			private InitializeAction initializeAction;
	
			//Extends AbstractAction, implements ActionListener
			//Allows client to interact with undo mechanisms
			private UndoAction undoAction;
			private RedoAction redoAction;
			
			
			//***Testing Purposes***//
			private ValidateAction validateAction;
	
			/**** BEGIN: JList components **/
				private Vector<String> historyStringList;
				private JList historyJList;
				//Number of successful undo operations performed since last
				//undo-edit-happened.
				private int undoDepth;
			/**\\\ END: JList components  **/
			
		/**\\\ END: CLIENT-ACCESSIBLE OBJECTS **/
		
		private final String JLIST_CURRENT_STATE_NAME = "Current";

		public JAbstractUndoPacket(Undoable S, int n) 
		{
			mainComponent = S;
			historySize = n;
			undo = new MyAbstractUndoManager();

			checkpointAction = new CheckpointAction();
			checkpointAction.putValue(Action.NAME, Actions.Checkpoint.toString());

			clearAction = new ClearAction();
			clearAction.putValue(Action.NAME, Actions.Clear.toString());
			
			initializeAction = new InitializeAction();
			initializeAction.putValue(Action.NAME, Actions.Initialize.toString());
			
			undoAction = new UndoAction();
			undoAction.putValue(Action.NAME, Actions.Undo.toString());

			redoAction = new RedoAction();
			redoAction.putValue(Action.NAME, Actions.Redo.toString());


			validateAction = new ValidateAction();
			validateAction.putValue(Action.NAME, Actions.Validate.toString());

			//Implementing interactive JList///
			historyStringList = new Vector<String>();
			historyJList = new JList();
			undoDepth = 0;

			//Only allow at most 1 selection on the historyJList
			historyJList.getSelectionModel()
			.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//Synchronize historyStringList with historyJList
			historyJList.setListData(historyStringList);
			//Do not let JList ever receive focus (still clickable)
			historyJList.setFocusable(false);
			//At anytime, the selected item on the JList should correspond
			//to the current undoDepth
			historyJList.setSelectedIndex(undoDepth);
			historyJList.addListSelectionListener(new ListSelectionListener(){
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					//Ensures list always has at least 1 selection (stateSelected != -1)
					//(ListSelectionModel further limits this to at most 1 selection)
					if (historyJList.getSelectedIndex() == -1)
					{
						historyJList.setSelectedIndex(e.getFirstIndex());
					}

					//Only perform appropriate undo/redo action if this is
					//finalizing event of a series of events.
					else if(!e.getValueIsAdjusting() && historyJList.getSelectedIndex() != -1)
					{
						int stateSelected = historyJList.getSelectedIndex();

						/** INVARIANT: stateSelected != -1 **/
						while (stateSelected < undoDepth)
						{
							undo.forwardstep();
						}
						while (stateSelected > undoDepth)
						{
							undo.backstep();
						}
						undo.derive();
					}
				}

			});
		}

		/**
		 * Extends Abstract Action.
		 * Make call to .actionPerformed() on the
		 * CheckpointAction object to save current state
		 * in undoable history.
		 */
		public CheckpointAction getCheckpointAction()
		{
			return checkpointAction;
		}

		/**
		 * Extends Abstract Action.
		 * Call {@link InitializeAction#actionPerformed(ActionEvent)} to save current state.
		 */
		public InitializeAction getInitializeAction()
		{
			return initializeAction;
		}

		public ClearAction getClearAction()
		{
			return clearAction;
		}

		/** 
		 * 	Extends AbstractAction and implements ActionListener.
		 * 	Make call to .actionPerformed() on the
		 *  UndoAction object to perform undo action.
		 */
		public UndoAction getUndoAction()
		{
			return undoAction;
		}

		/** 
		 * 	Extends AbstractAction and implements ActionListener.
		 * 	Make call to .actionPerformed() on the
		 *  RedoAction object to perform redo action.
		 */
		public RedoAction getRedoAction() 
		{
			return redoAction;
		}
		
		/**
		 * For Testing Purposes Only.
		 * Serializes the current state of mainComponent and compares it
		 * against currentState stored in MyAbstractUndoManager. 
		 * Prints results to standard out.
		 * @return
		 */
		public ValidateAction getValidateAction()
		{
			return validateAction;
		}
		
		/**
		 * Gets the name of the next action that can be undone.
		 * @return 
		 *         null if no state or action doesn't have a name
		 */
		String getNextUndoActionName() {
			if (historyStringList == null || undoDepth < 1 || historyStringList.size() <= undoDepth) {
				if (trace.getDebugCode("undo"))
					trace.out("undo", "getNextUndoActionName(): historyStringList.size "+historyStringList.size()+
							" while undoDepth "+undoDepth);
				return Actions.Undo.toString();
			}
			String undoName = historyStringList.get(undoDepth);
			return Actions.Undo.toString()+" "+undoName;
		}
		
		/**
		 * Gets the name of the next action that can be redone.
		 * @return name of state at top of {@link JUndo.MyAbstractUndoManager#prevStates};
		 *         null if no state or action doesn't have a name
		 */
		String getNextRedoActionName() {
			if (historyStringList == null || undoDepth < 2 || historyStringList.size() < undoDepth) {
				if (trace.getDebugCode("undo"))
					trace.out("undo", "getNextRedoActionName(): historyStringList.size "+historyStringList.size()+
							" while undoDepth "+undoDepth);
				return Actions.Redo.toString();
			}
			String redoName = historyStringList.get(undoDepth-1);
			return Actions.Redo.toString()+" "+redoName;
		}

		/**
		 * @return An interactive JList which contains a history
		 * of undoable edits. The user may click on any element in
		 * the history to revert to the selected state. Synchronicity
		 * of this list is maintained by the JAbstractUndoPacket.
		 */
		//public JList getActionList()
		//{
		//	return historyJList;
		//}

		/**
		 * Maintains the current state and lists of previous states (to restore upon undo)
		 * and undone states (to restore upon redo).
		 * @author Kevin Zhang, 2011
		 */
		private class MyAbstractUndoManager
		{
			/* "Double-stack" implementation */
			private LinkedList<byte[]> prevStates;
			private LinkedList<byte[]> undidStates;
			private byte[] currentState;

			private MyAbstractUndoManager()
			{
				prevStates = new LinkedList<byte[]>();
				undidStates = new LinkedList<byte[]>();
				currentState = null;
			}
			
			/**
			 * Call this to set a current state from the {@link JAbstractUndoPacket#mainComponent}.
			 * @param name used for debugging
			 */
			private void initCurrentState(String name) {
				if (trace.getDebugCode("undo"))
					trace.out("undo", "initCurrentState("+name+")");
				byte[] saveState = mainComponent.saveState();
				undidStates.clear();
				prevStates.clear();
				currentState = saveState;
				
				historyStringList.clear();
				historyStringList.add(JLIST_CURRENT_STATE_NAME);
				historyJList.setListData(historyStringList);
				undoDepth = 0;
				
				// See valueChanged() in JAbstractUndoPacket(): valueIsAdjusting true
				// suppresses reaction to listSelection change in setSelectedIndex().
				historyJList.getSelectionModel().setValueIsAdjusting(true);  
				historyJList.setSelectedIndex(undoDepth);
				
				undoAction.updateUndoAction();
				redoAction.updateRedoAction();
			}

			/**
			 * Simulates UndoableEditEvent
			 * @param name Name of edit to be stored in historyJList
			 */
			private void undoableEditHappened(String name) 
			{
				//Retrieve byte-array image of current state of mainComponent
				//Uses .saveState of Undoable
				byte[] saveState = mainComponent.saveState();
				undidStates.clear();  // A state change makes previous undid states irrelevant
				
				/*
				 * If no state is currently stored by the manager,
				 * store image of state in currentState which lies
				 * in the "middle" of the 2 stacks (LinkedLists).
				 * Else, push currentState to "left" stack
				 * (undoable history) and set currentState as
				 * the image of the current state.
				 */
				if (currentState != null)
					prevStates.push(currentState);
				currentState = saveState;

				//Maintains size of undoable history
				while (historySize > 0 && prevStates.size()>historySize)
					prevStates.removeLast();

				/** Maintains historyJList **/
				if (trace.getDebugCode("undo"))
					trace.out("undo", "undoableEditHappened[0] undoDepth "+undoDepth+
							",\n  historyStringList "+historyStringList);
				for (int i = 0; i < undoDepth-1; i ++)
					historyStringList.remove(1);
				if (trace.getDebugCode("undo"))
					trace.out("undo", "undoableEditHappened[1] undoDepth "+undoDepth+
							",\n  historyStringList "+historyStringList);

				if (historyStringList.isEmpty())
					historyStringList.add(JLIST_CURRENT_STATE_NAME);
				else {
					if (name == null)
						name = "";
					else if (name.endsWith("..."))          // remove ... suffixed to dialog-
						name = name.substring(0, name.length()-3);  // introducing menu items
					historyStringList.insertElementAt(name, 1);
				}

				historyJList.getSelectionModel().setValueIsAdjusting(true);
				historyJList.setListData(historyStringList);

				undoDepth = (historyStringList.size() > 1 ? 1 : 0);
				historyJList.setSelectedIndex(undoDepth);

				//Keeps UndoAction and RedoAction in sync
				undoAction.updateUndoAction();
				redoAction.updateRedoAction();
			}

			private boolean canUndo()
			{
				return prevStates.size() > 0;
			}

			private boolean canRedo()
			{
				return undidStates.size() > 0;
			}

			/**
			 * .backstep() has the same functionality as undo() except
			 * in that it does not call the .derive function.
			 * @return The byte[] that would otherwise be used by
			 * .derive. This should not be used by an outside client.
			 */
			private byte[] backstep()
			{
				if (!canUndo())	
					throw new CannotUndoException();

				undoDepth++;

				byte[] revert = prevStates.pop();
				undidStates.push(currentState);
				currentState = revert;

				return revert;
			}

			/**
			 * .forwardstep() has the same functionality as redo() except
			 * in that it does not call the .derive function.
			 * @return The byte[] that would otherwise be used by
			 * .derive. This should not be used by an outside client.
			 */
			private byte[] forwardstep()
			{
				if (!canRedo())
					throw new CannotRedoException();

				undoDepth--;

				byte[] forward = undidStates.pop();
				prevStates.push(currentState);
				currentState = forward;

				return forward;
			}

			/**
			 * Calls .derive(currentState) on the mainComponent.
			 * Typically used after backstep() or forwardstep().
			 * Calling backstep() then immediately derive() is the
			 * identical effect to calling undo(). A similar case
			 * applies to forwardstep() and derive().
			 */
			private void derive()
			{
				mainComponent.derive(currentState);
				historyJList.getSelectionModel().setValueIsAdjusting(true);
				historyJList.setSelectedIndex(undoDepth);
			}

			private void undo() throws CannotUndoException
			{
				//uses .derive of Undoable
				backstep();
				derive();
				historyJList.getSelectionModel().setValueIsAdjusting(true);
				historyJList.setSelectedIndex(undoDepth);
			}

			private void redo() throws CannotRedoException
			{
				forwardstep();
				derive();
				historyJList.getSelectionModel().setValueIsAdjusting(true);
				historyJList.setSelectedIndex(undoDepth);
			}
		}

		public class CheckpointAction extends AbstractAction
		{
			public void actionPerformed(ActionEvent e) 
			{
				//Simulates an UndoableEditEvent
				if (trace.getDebugCode("undo"))
					trace.out("undo", "Checkpoint("+e+")");
				if (e != null)
					undo.undoableEditHappened(e.getActionCommand());
				else {
					trace.errStack("null ActionEvent to CheckpointAction.actionPerformed()",
							new IllegalArgumentException());
					undo.undoableEditHappened(null);
				}
				
			}
		}

		public class ClearAction extends AbstractAction
		{
			public void actionPerformed(ActionEvent e)
			{
				undo = new MyAbstractUndoManager();
				//Reseting JList components
				historyStringList = new Vector<String>();
				historyJList.setListData(historyStringList);
				undoDepth = 0;
				historyJList.setSelectedIndex(undoDepth);
			}
		}

		public class InitializeAction extends ClearAction
		{
			public void actionPerformed(ActionEvent e)
			{
				super.actionPerformed(e);
				undo.initCurrentState(e.getActionCommand());
			}
		}

		public class UndoAction extends AbstractAction
		{
			public UndoAction()
			{
				super(Actions.Undo.toString());
				setEnabled(false);
	            int ctrlKeyMask = KeyEvent.CTRL_MASK;
		        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
		            ctrlKeyMask = KeyEvent.META_MASK;
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrlKeyMask));
				putValue(SHORT_DESCRIPTION, "Undo last change");
			}

			public void actionPerformed(ActionEvent e) 
			{
				try {
					undo.undo();
				} catch (CannotUndoException ex) {
					trace.errStack("Error on event "+e+" trying to undo "+undo.getClass().getName()+
							", canUndo "+undo.canUndo(), ex);
				}

				updateUndoAction();
				redoAction.updateRedoAction();
			}

			private void updateUndoAction() 
			{
				boolean canUndo = undo.canUndo();
				setEnabled(canUndo);
				putValue(NAME, getNextUndoActionName());
				if (trace.getDebugCode("undo"))
					trace.out("undo", "updateUndoAction() canUndo "+canUndo+", undoDepth "+undoDepth+
							",\n  historyStringList "+historyStringList);
			}
		}

		public class RedoAction extends AbstractAction 
		{
			public RedoAction() 
			{
				super(Actions.Redo.toString());
				setEnabled(false);
	            int ctrlKeyMask = KeyEvent.CTRL_MASK;
		        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
		            ctrlKeyMask = KeyEvent.META_MASK;
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrlKeyMask));
				putValue(SHORT_DESCRIPTION, "Redo last change undone");
			}

			public void actionPerformed(ActionEvent e) 
			{
				try {
					undo.redo();
				} catch (CannotRedoException ex) {
					trace.out("Unable to redo: " + ex);
					// ex.printStackTrace();
				}
				updateRedoAction();
				undoAction.updateUndoAction();
			}

			private void updateRedoAction()
			{
				boolean canRedo = undo.canRedo();
				setEnabled(canRedo);
				putValue(NAME, getNextRedoActionName());
				if (trace.getDebugCode("undo"))
					trace.out("undo", "updateRedoAction() canRedo "+canRedo+", undoDepth "+undoDepth+
							",\n  historyStringList "+historyStringList);
			}
		}
		public class ValidateAction extends AbstractAction 
		{
			public ValidateAction() 
			{
				super(Actions.Validate.toString());
				setEnabled(true);
			}

			public void actionPerformed(ActionEvent e) 
			{

				byte[] current = undo.currentState;
				byte[] compareAgainst = mainComponent.saveState();

				int length1 = current.length;
				int length2 = current.length;

				if (length1 != length2)
					printError("size");
				else
				{
					for (int i = 0; i < length1; i++)
					{
						if (current[i] != compareAgainst[i])
						{
							printError("data");
							return;
						}
					}
					printSuccess();
				}
			}

			private void printError(String s)
			{
				trace.out("*********************************************");
				trace.out("****************  "+s+" ERROR  ********************");
				trace.out("*********************************************");
			}

			private void printSuccess()
			{
				trace.out("++++++++++++ VALIDATION SUCCESSFULL +++++++++++");
			}
		}
	}
	/*** END OF JAbstractUndoPacket ***/
}

