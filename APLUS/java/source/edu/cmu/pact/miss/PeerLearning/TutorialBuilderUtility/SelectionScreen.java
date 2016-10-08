package edu.cmu.pact.miss.PeerLearning.TutorialBuilderUtility;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class SelectionScreen extends JFrame {

	/**
	 * Creates a new selection tree GUI from a given DOMTree
	 * @param tree DOMTree to create graphical representation for
	 */
	public SelectionScreen(DOMTree tree)
	{		
		//scroll pane to access the entire tree, no matter how big it may be
		JScrollPane scroll = new JScrollPane(tree.getJTree(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		for (int i = 0; i < tree.getJTree().getRowCount(); i++) //expand all rows for proper pack() behavior
			tree.getJTree().expandRow(i);

		BorderLayout layout = new BorderLayout();

		//set the layout so that the tree resizes with the window
		this.getContentPane().setLayout(layout);
		this.getContentPane().add(scroll, BorderLayout.CENTER);
				
		//adjust the frame to fit the tree
		pack();
		setVisible(true);
		
		((Component) tree.getHead().getContents()).addComponentListener(tree);
	}

	


}
