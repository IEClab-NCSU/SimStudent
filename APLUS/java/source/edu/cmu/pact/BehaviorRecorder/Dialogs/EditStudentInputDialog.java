/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.VectorMatcherPanel;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EditStudentInputDialog extends JDialog implements MouseListener {

	private static final long serialVersionUID = 201402281645L;

	private static final String EDIT_STUDENT_INPUT_MATCHING = "Edit Student Input Matching";

	private EdgeData edgeData;
	
	private BR_Controller controller;
	
    /* We use a modified JLabel to switch between simple and complex views */
	private String viewType; //see VectorMatcherPanel.SIMPLE_VIEW and COMPLEX_VIEW
	
	/* Prettiness for switching views */
	private UnderlinedJLabel concat;
	private UnderlinedJLabel simple;
    private UnderlinedJLabel complex;
    private UnderlinedJLabel current;
    
    private static final String CONCAT_VIEW_TEXT = VectorMatcherPanel.CONCAT_VIEW;
    private static final String SIMPLE_VIEW_TEXT = VectorMatcherPanel.SIMPLE_VIEW;
    private static final String COMPLEX_VIEW_TEXT = VectorMatcherPanel.COMPLEX_VIEW;
    
    private static final Color LINK_COLOR = Color.BLUE;
    
    private boolean multipleVectorsEnabled;
    
    /** Holds simple or complex view and keeps them related */
    private VectorMatcherPanel vectorMatcherPanel;

    /** Edge information before edits. */
    private final String serializedBeforeEdit;

    /** True if window closing was initiated by OK or Cancel button in the displayed {@link VectorMatcherPanel}. */
	private boolean closedFromMatcherPanel = false;

	/**
	 * @param edgeData
	 */
	public EditStudentInputDialog(EdgeData edgeData, BR_Controller controller) {
		super(controller.getActiveWindow(), false);
        this.controller = controller;
		this.edgeData = edgeData;
		serializedBeforeEdit = edgeData.getEdge().toXMLString();
		initUI();
	}
	
	/**
	 * 
	 */
	private void initUI() {
		
		setTitle(EDIT_STUDENT_INPUT_MATCHING);
		
		JPanel gbPane = new JPanel(new GridBagLayout()); //gridbag holds everything except buttons (wouldn't line up right otherwise
		gbPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();
		
//		sewall 2010/05/19: removed to shrink vertical height for wider-aspect-ratio displays
//
//		String vertexName1 = edgeData.getSourceProblemNode().getNodeView().getText();
//		String vertexName2 = edgeData.getEndProblemNode().getNodeView().getText();
//		JLabel text1 = new JLabel(
//				"<html>"
//						+ "Use this window to edit the matching options for example-tracing<br>"
//						+ "actions between the nodes \"<b>"
//						+ vertexName1
//						+ "</b>\" and \"<b>"
//						+ vertexName2
//						+ "</b>\"<br><br>"
//						+ "The Selection, Action, and Input define the "
//						+ "user's inputs for this step in the graph."
//						+ "<br>"
//						+ "There are several methods for matching against each of these inputs.<br><br></html>");
//		
//		c.gridx = 0;
//		c.gridy = 0;
//		gbPane.add(text1, c);
		
		viewType = VectorMatcherPanel.CONCAT_VIEW;
		concat = new UnderlinedJLabel(CONCAT_VIEW_TEXT);
		concat.addMouseListener(this);
		current = concat;
		
		complex = new UnderlinedJLabel(COMPLEX_VIEW_TEXT);
		complex.setForeground(LINK_COLOR);
		complex.setUnderlined(true);
		complex.addMouseListener(this);
		
		simple = new UnderlinedJLabel(SIMPLE_VIEW_TEXT);
		simple.setForeground(LINK_COLOR);
		simple.setUnderlined(true);
		simple.addMouseListener(this);
		
		Box viewOptions = new Box(BoxLayout.X_AXIS);
		viewOptions.add(concat);
		viewOptions.add(Box.createHorizontalStrut(12));
		viewOptions.add(simple);
		viewOptions.add(Box.createHorizontalStrut(12));
		viewOptions.add(complex);
		c.gridy = 1;
		//gbPane.add(viewOptions, c);
		
		/**
		 * TODO: if we ever want the other views, uncomment the above these
		 */
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = c.gridy = 0;
		vectorMatcherPanel = new VectorMatcherPanel(this, edgeData, 
				controller.getPreferencesModel().getBooleanValue(BR_Controller.ALLOW_TOOL_REPORTED_ACTIONS),
        		controller.getPreferencesModel().getIntegerValue(BR_Controller.MAX_STUDENT));
//		c.gridy = 2;
		gbPane.add(vectorMatcherPanel, c);
		
		setFocusTraversalPolicy(vectorMatcherPanel.getCustomFocusTraversalPolicy());
		if(trace.getDebugCode("editstudentinput"))
			trace.out("editstudentinput", "VMP.focusPolicy "+
					trace.nh(vectorMatcherPanel.getCustomFocusTraversalPolicy())+", dialog.focusPolicy "+
					getFocusTraversalPolicy());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			/**
			 * Check for changes and permit user to save before closing. Calls
			 * {@link VectorMatcherPanel#closeViaOkCancel()} which either calls {@link #close()}
			 * or is no-op.
			 * @param e not used
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			public void windowClosing(WindowEvent e) {
				if(!closedFromMatcherPanel)
					vectorMatcherPanel.closeViaOkCancel();  // may call close()
				closedFromMatcherPanel = false;         // reset for next call
			}
		});
		
		getContentPane().add(gbPane);
		pack();
	}

	/**
	 * @param edgeData
	 */
	public static void show(EdgeData edgeData, BR_Controller controller) {
		EditStudentInputDialog d = new EditStudentInputDialog(edgeData, controller);
		d.setVisible(true);
	}
    
	/**
	 * Close the dialog. Create an undo checkpoint if {@link #edgeData} changed.
	 */
	public void close() {
		closedFromMatcherPanel = true;
		this.setVisible(false);
		this.dispose();
        String serializedAfterEdit = edgeData.getEdge().toXMLString();
        if (!(serializedBeforeEdit.equals(serializedAfterEdit))) {
        	if (trace.getDebugCode("undo"))
				trace.out("undo", "EditStudentInputDialog.close() XML before:\n"+
        				serializedBeforeEdit+"\nXML after:\n"+serializedAfterEdit);
        			
			//Undo checkpoint for Changing Action Type ID: 1337
        	ActionEvent ae = new ActionEvent(this, 0, EDIT_STUDENT_INPUT_MATCHING);
			controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
			if (trace.getDebugCode("undo"))
				trace.out("undo", "Checkpoint: Change Student Input (1)");
        }
	}

	/**
	 * Switched between simple and complex
	 */
	public void mouseClicked(MouseEvent me) {
		Object src = me.getSource();
		current.setUnderlined(true);
		current.setForeground(Color.BLUE);
				
		if(src == concat)
		{
			if(viewType == VectorMatcherPanel.CONCAT_VIEW)
				return;
			viewType = VectorMatcherPanel.CONCAT_VIEW;	
		}
		else if(src == simple)
		{
			if(viewType == VectorMatcherPanel.SIMPLE_VIEW)
				return;
			viewType = VectorMatcherPanel.SIMPLE_VIEW;
		}
		else if(src == complex)
		{
			if(viewType == VectorMatcherPanel.COMPLEX_VIEW)
				return;
			viewType = VectorMatcherPanel.COMPLEX_VIEW;
		}
		
		current = (UnderlinedJLabel)src;
		current.setForeground(getForeground()); //should return to fg color of the dialog
		current.setUnderlined(false);
		
		vectorMatcherPanel.switchView(viewType);
		
		pack();
		validate();
		repaint();
		
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * We can make the rest fancy later (apparently underlining requires overriding the repaint method ...)
	 * See http://forum.java.sun.com/thread.jspa?threadID=527199&messageID=2530618
	 */
	public void mouseEntered(MouseEvent me) {
		if(me.getSource() != current)
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public void mouseExited(MouseEvent me) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void mousePressed(MouseEvent me) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	
	/**
	 * JLabel is not convenient for underlined text, hence we subclass
	 * See: http://forum.java.sun.com/thread.jspa?threadID=529801&messageID=2667179
	 * @author wko2
	 *
	 */
	private class UnderlinedJLabel extends JLabel
	{
		private boolean drawUnderline = false;
		
		public void setUnderlined(boolean d)
		{
			drawUnderline = d;
		}
		
		public UnderlinedJLabel(String text)
		{
			super(text);
		}
		
		public void paint(Graphics g)
	    {
	        super.paint(g);
	        if (drawUnderline)
	        {
	            Color underline = getForeground();
	 
	             // really all this size stuff below only needs to be recalculated if font or text changes
	            Rectangle2D textBounds =  getFontMetrics(getFont()).getStringBounds(getText(), g);
	            
	             //this layout stuff assumes the icon is to the left, or null
	            int y = getHeight()/2 + (int)(textBounds.getHeight()/2);
	            int w = (int)textBounds.getWidth();
	            int x = (getIcon()==null ? 0 : getIcon().getIconWidth() + getIconTextGap()); 
	 
	            g.setColor(underline);
	            g.drawLine(0, y, w, y);
	        }
	    }
	}

	/**
	 * @return {@link #controller}.{@link BR_Controller#getActiveWindow() getActiveWindow()}
	 */
	public Component getActiveWindow() {
		return controller.getActiveWindow();
	}
}
