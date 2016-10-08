package edu.cmu.pact.jess;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.ctatview.JHorizontalTable;

public class RuleActivationTreePanel extends JRootPane {
	private static final long serialVersionUID = -1966295500520349947L;
	private CTAT_Launcher server;
	JScrollPane scrollPanel;
	JHorizontalTable currentTreeTable;
	private JPanel labelPanel;
	
	public RuleActivationTreePanel(CTAT_Launcher server) {
		this.server = server;
		
		this.labelPanel = constructDepthPanel();
		
		this.currentTreeTable = (JHorizontalTable)getFocusedTree().getTreeTable();
		this.scrollPanel = new JScrollPane(this.currentTreeTable);
		this.scrollPanel.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// for scrolling
		if (this.currentTreeTable.getPreferredScrollableViewportSize().getWidth() > 
		  ((JViewport)this.currentTreeTable.getParent()).getPreferredSize().getWidth())
		  {
			this.currentTreeTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			this.currentTreeTable.doLayout();
		}


		getContentPane().add(this.scrollPanel);
		getContentPane().add(this.labelPanel, BorderLayout.SOUTH);
		
		setSize (300, 300);
	}
	
	public void refresh() {
		
		this.scrollPanel.removeAll();
		getContentPane().remove(this.scrollPanel);
		getContentPane().remove(this.labelPanel);
		
		// get the current problem's table and recreate everything
		this.currentTreeTable = (JHorizontalTable)getFocusedTree().getTreeTable();
		this.scrollPanel = new JScrollPane(this.currentTreeTable);
		this.scrollPanel.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		this.labelPanel = constructDepthPanel();
		
		getContentPane().add(this.scrollPanel);
		getContentPane().add(this.labelPanel, BorderLayout.SOUTH);

		revalidate();
		repaint(getFocusedTree());
	}


	/**
	 * @return
	 */
	private JPanel constructDepthPanel() {
		JPanel pane = new JPanel();
		JLabel maxDepthLbl = getFocusedTree().getDepthLabel();
//	maxDepthLbl.setText("Max Depth: " +
//			(rete == null ? MTRete.DEFAULT_MAX_DEPTH : rete.getMaxDepth()));
		MTRete rete = getFocusedRete();
		if (rete != null) maxDepthLbl.setText("Max Depth: " +	rete.getMaxDepth());	
		else if (maxDepthLbl.getText() == "")
			maxDepthLbl.setText("Max Depth: " + MTRete.DEFAULT_MAX_DEPTH );
		pane.add(maxDepthLbl);
		return pane;
	}
	
	private RuleActivationTree getFocusedTree() {
		return getController().getRuleActivationTree();
	}
	
	private MTRete getFocusedRete() {
		return getController().getModelTracer().getRete();
	}
	
	private BR_Controller getController() {
		return this.server.getFocusedController();
	}
	
	public void repaint(RuleActivationTree tree) {
		if(tree.getTreeTable() != this.currentTreeTable) return;
		this.scrollPanel.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		// the table height resets to 16 (height of one row) for some reason,
		// so expand it to accommodate all rows
		this.currentTreeTable.setSize(this.currentTreeTable.getWidth(),
				this.currentTreeTable.getRowCount() * this.currentTreeTable.getRowHeight());
		
		// set dimensions for scrolling
		this.currentTreeTable.setMinimumSize(new Dimension(this.currentTreeTable.getWidth(),
				this.currentTreeTable.getHeight()));
		repaint();
	}
	
	public void validate(RuleActivationTree tree) {
		if(tree.getTreeTable() != this.currentTreeTable) return;
		validate();
	}
	
	public void show(RuleActivationTree tree) {
		if(tree.getTreeTable() != this.currentTreeTable) return;
		setVisible(true);
	}
	
	
	
	

}
