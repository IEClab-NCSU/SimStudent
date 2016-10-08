package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;


public class EditListItemDialog extends JDialog
                      implements ListSelectionListener, FocusListener {
	
    private JList listsBuffer;
    private DefaultListModel listModel;

    private static final String DeleteString = "Delete";
    private static final String EditString = "Edit";
    private static final String ReplaceString = "Replace";
    private static final String MoveUpString = "Move Up";
    private static final String MoveDownString = "Move Down";
    private static final String AddString = "Add";
    private static final String CancelString = "Cancel";
    private static final String DoneString = "Done";
 
    private JCheckBox  InvisibleCheckBox;
    private JButton DeleteButton;
    private JButton EditReplaceButton;
    private JButton AddButton;
    private JButton MoveUpButton;
    private JButton MoveDownButton;
    private JButton CancelButton;
    private JButton DoneButton;
    
    private JTextField itemLineEditor;
    
    private String[] BackupitemList;   // Backup original lists if author cancel the modification.
	private boolean   invisible;
	
    public EditListItemDialog(JFrame parent, String title, boolean modal, String[] itemList, boolean invisible) {

		super(parent, title, modal);
		
		JPanel mainPane = new JPanel(new BorderLayout());


        listModel = new DefaultListModel();

        BackupitemList = new String[itemList.length];
        for (int i = 0; i < itemList.length; i++) {
        	listModel.addElement(itemList[i]);
        	BackupitemList[i] = itemList[i];
        }
        //Create the list and put it in a scroll pane.
        listsBuffer = new JList(listModel);
        listsBuffer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listsBuffer.setSelectedIndex(0);
        listsBuffer.addListSelectionListener(this);
        listsBuffer.setVisibleRowCount(10);
 //       listsBuffer.setSize(new Dimension(300,300));
        listsBuffer.setMinimumSize(new Dimension(500,500));

		mainPane.add(listsBuffer);
	
		mainPane.setOpaque(true);
 //       JScrollPane listScrollPane = new JScrollPane(mainPane);

        JScrollPane listScrollPane = new JScrollPane(listsBuffer);
        mainPane.add(listScrollPane, BorderLayout.PAGE_START);
        
        EditReplaceButton = new JButton(EditString);
        EditReplaceListener EditReplaceListener = new EditReplaceListener(EditReplaceButton);
        EditReplaceButton.setActionCommand(EditString);
        EditReplaceButton.addActionListener(EditReplaceListener);
        
        DeleteButton = new JButton(DeleteString);
        DeleteButton.setActionCommand(DeleteString);
        DeleteButton.addActionListener(new DeleteListener());

        CancelButton = new JButton(CancelString);
        CancelButton.setActionCommand(CancelString);
        CancelButton.addActionListener(new CancelListener());

        DoneButton = new JButton(DoneString);
        DoneButton.setActionCommand(DoneString);
        DoneButton.addActionListener(new DoneListener());



        AddButton = new JButton(AddString);
        AddListener AddListener = new AddListener(AddButton);
        AddButton.setActionCommand(AddString);
        AddButton.addActionListener(AddListener);
        AddButton.setEnabled(false);

        MoveUpButton = new JButton(MoveUpString);

        MoveUpButton.setActionCommand(MoveUpString);
        MoveUpButton.addActionListener(new MoveListener(MoveUpButton));
        MoveUpButton.setEnabled(true);
        
        MoveDownButton = new JButton(MoveDownString);
        MoveDownButton.setActionCommand(MoveDownString);
        MoveDownButton.addActionListener(new MoveListener(MoveDownButton));
        MoveDownButton.setEnabled(true);
        

        itemLineEditor = new JTextField(10);
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(itemLineEditor); }
        itemLineEditor.addActionListener(AddListener);
        itemLineEditor.addFocusListener(this);
        itemLineEditor.getDocument().addDocumentListener(AddListener);
   		itemLineEditor.getDocument().addDocumentListener(EditReplaceListener); 
   		
   		String name;
   		if (listsBuffer.getSelectedIndex() > -1)
         	name = listModel.getElementAt(
                              listsBuffer.getSelectedIndex()).toString();
  		else name = "";

		InvisibleCheckBox = new JCheckBox("Invisible", null, invisible);
		
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(InvisibleCheckBox);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        
        buttonPane.add(DeleteButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        
        buttonPane.add(EditReplaceButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        
 ;;       buttonPane.add(itemLineEditor);
        buttonPane.add(AddButton);
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        
        buttonPane.add(MoveUpButton);
        buttonPane.add(Box.createVerticalStrut(5));
        
        buttonPane.add(MoveDownButton);
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        
        buttonPane.add(CancelButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        buttonPane.add(DoneButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


		
	       JPanel itemPane = new JPanel(new BorderLayout());
	       itemLineEditor.setMinimumSize(new Dimension(200,200));
	       itemPane.add(itemLineEditor);
	       itemPane.setOpaque(true);
	        JScrollPane lineScrollPane = new JScrollPane(itemLineEditor);
//	       itemPane.setLayout(new BoxLayout(itemPane,
//	                                           BoxLayout.LINE_AXIS));
//	       itemPane.add(lineScrollPane, BorderLayout.CENTER);
//	        mainPane.add(new JSeparator(SwingConstants.HORIZONTAL));
	        mainPane.add(lineScrollPane, BorderLayout.PAGE_END);
	     
	        add(mainPane,BorderLayout.NORTH );
	        
	    add(new JSeparator(SwingConstants.HORIZONTAL));
	       
	       
		add(buttonPane,BorderLayout.SOUTH );
		
//		setContentPane(mainPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); //umm should this be relative to something else??
		pack();
		setVisible(true);
    }

//    class FocusListener implements ActionListener {
//        public void actionPerformed(ActionEvent e) {
//        	trace.out("inter", "Move Focus to line editor" );
//        }
//    }
    
    public void focusGained(FocusEvent e) {
    	if (trace.getDebugCode("inter")) trace.out("inter", " focusGained" );
        if ((e.getComponent() instanceof JTextField))
        	if (trace.getDebugCode("inter")) trace.out("inter", "itemLineEditor focusGained" );
    }
    
    public void focusLost(FocusEvent e) {
    	if (trace.getDebugCode("inter")) trace.out("inter", " focusLost" );
        if ((e.getComponent() instanceof JTextField))
        	if (trace.getDebugCode("inter")) trace.out("inter", "itemLineEditor focusLost" );
    }
    class DoneListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	if (trace.getDebugCode("dw")) trace.out("dw", "Done " + BackupitemList.length);
			setInvisible(InvisibleCheckBox.isSelected());
			setVisible(false);
			dispose();
        }
    }
    
    class CancelListener implements ActionListener {
    	
        public void actionPerformed(ActionEvent e) {
        	if (trace.getDebugCode("dw")) trace.out("dw", "Cancel Backupitem length =" + BackupitemList.length);
            listModel.setSize(BackupitemList.length);
            for (int i = 0; i < BackupitemList.length; i++) {
            	listModel.setElementAt(BackupitemList[i], i);
				if (trace.getDebugCode("dw")) trace.out("dw", "BackupitemList[" + i +"] =" + getList().getModel().getElementAt(i));
            }

  //         listsBuffer.getModel().
           
			setVisible(false);
			dispose();
        }
    }
 

    
    class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
        	if (trace.getDebugCode("dw")) trace.out("dw", "Delete " + BackupitemList.length);
            int index = listsBuffer.getSelectedIndex();
            listModel.remove(index);

            int size = listModel.getSize();

            if (size == 0) { //Nobody's left, disable firing.
                DeleteButton.setEnabled(false);

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                listsBuffer.setSelectedIndex(index);
                listsBuffer.ensureIndexIsVisible(index);
                listsBuffer.repaint();
                repaint();
            }
        }
    }

    class EditReplaceListener implements ActionListener , DocumentListener {
        private boolean alreadyEnabled = true;
        private JButton button;

        public EditReplaceListener(JButton button) {
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {

        //	EditReplaceButton.setText(ReplaceString);
        if (EditReplaceButton.getText().equals(EditString))
        	itemLineEditor.setText(listModel.getElementAt(listsBuffer.getSelectedIndex()).toString());
        else {

            String name = itemLineEditor.getText();

            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                itemLineEditor.requestFocusInWindow();
                itemLineEditor.selectAll();
                return;
            }

            int index = listsBuffer.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so no replacement
               return;
            } 
            if (trace.getDebugCode("inter")) trace.out("inter", "index = " + index);
           listModel.removeElementAt(index);

      //      listModel.remove(index);
            listModel.insertElementAt(itemLineEditor.getText(), index);
            //           listModel.setElementAt(itemLineEditor.getText(), index); 

            //If we just wanted to add to the end, we'd do this:
            //listModel.addElement(employeeName.getText());

            //Reset the text field.
            itemLineEditor.requestFocusInWindow();
            itemLineEditor.setText("");

            //Select the new item and make it visible.
            listsBuffer.setSelectedIndex(index);
            listsBuffer.ensureIndexIsVisible(index);
            listsBuffer.repaint();
            repaint();
        }
        	
        	
        }
        
        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.
        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }

        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
        	if (trace.getDebugCode("inter")) trace.out("inter", "EditReplaceListener insertUpdate" );
        	switchButton(ReplaceString);
        }

        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
        	if (trace.getDebugCode("inter")) trace.out("inter", "EditReplaceListener removeUpdate" );
            handleEmptyTextField(e);
        }

        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
        	if (trace.getDebugCode("inter")) trace.out("inter", "EditReplaceListener changedUpdate" );
            if (!handleEmptyTextField(e)) {
                switchButton(ReplaceString);
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }
        
        private void switchButton(String buttonName) {
            if (alreadyEnabled) {
                button.setText(buttonName);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
            	switchButton(EditString);

                return true;
            }
            return false;
        }
    }
    //This listener is shared by the text field and the Add button.
    class AddListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;

        public AddListener(JButton button) {
            this.button = button;
        }

        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
            String name = itemLineEditor.getText();

            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                itemLineEditor.requestFocusInWindow();
                itemLineEditor.selectAll();
                return;
            }

            int index = listsBuffer.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
                index++;
            }

            listModel.insertElementAt(itemLineEditor.getText(), index);
            //If we just wanted to add to the end, we'd do this:
            //listModel.addElement(employeeName.getText());

            //Reset the text field.
            itemLineEditor.requestFocusInWindow();
            itemLineEditor.setText("");

            //Select the new item and make it visible.
            listsBuffer.setSelectedIndex(index);
            listsBuffer.ensureIndexIsVisible(index);
        }

        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.
        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }

        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

    //This listener is shared by the text field and the Add button.
    class MoveListener implements ActionListener, ListSelectionListener {
        private JButton button;
        int nextIndex;
        
        public MoveListener(JButton button) {
            this.button = button;
        }

        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {


            int index = listsBuffer.getSelectedIndex(); //get selected index
            int prevIndex  = index;
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
              if (button.getText().equals(MoveDownString)) index++;
              else index--;
            }
            
            swapItems(prevIndex, index);

            //Select the new item and make it visible.
            listsBuffer.setSelectedIndex(index);
            listsBuffer.ensureIndexIsVisible(index);
            listsBuffer.requestFocusInWindow();
        }

        public void valueChanged(ListSelectionEvent e) {
           
       }
 
    }
 

    public void swapItems(int prevIndex, int currentIndex) {
    	Object temp = listModel.getElementAt(prevIndex);
    	listModel.setElementAt(listModel.getElementAt(currentIndex), prevIndex);
    	listModel.setElementAt(temp, currentIndex);
    }

    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
         if (e.getValueIsAdjusting() == false) {

            if (listsBuffer.getSelectedIndex() == -1) {
            //No selection, disable Delete button.
                DeleteButton.setEnabled(false);

            } else {
            //Selection, enable the Delete button.
                DeleteButton.setEnabled(true);
            }
        }
//       trace.out("inter", "current selected item = " + listModel.getElementAt(listsBuffer.getSelectedIndex()).toString());
        EditReplaceButton.setText(EditString);
        
    }

	public JList getList() {
		return listsBuffer;
	}

	public void setList(JList list) {
		this.listsBuffer = list;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}
}
