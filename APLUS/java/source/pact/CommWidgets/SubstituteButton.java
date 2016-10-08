/**
 * 
 */
package pact.CommWidgets;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.trace;

/**
 * Class to provide the behavior of a button substitute.  The substitute
 * hides the given button and inserts a similar button in its place.  When
 * a user clicks the substitute, we check the validity of the action and
 * either show an error or call {@link JButton#doClick()} on the original
 * button.
 */
public class SubstituteButton extends JButton implements ActionListener {

	private AbstractButton doneButton;
	private HasAllQuestionsAnswered parent;

	public SubstituteButton(JCommButton doneWidget, JButton doneButton,
			HasAllQuestionsAnswered parent) {
		this.doneButton = doneButton;
		this.parent = parent;
		setName(doneWidget.getCommName());
		setText(doneWidget.getText());
		addActionListener(this);
		doneButton.setVisible(false);
        
		doneWidget.add(this);
		setVisible(true);
		doneWidget.validate();
	}

	public void actionPerformed(ActionEvent e) {
		if (trace.getDebugCode("inter")) trace.out("inter", "got event "+e);
                    
		if (parent.allQuestionsAnswered())
			doneButton.doClick();
		else
			JOptionPane.showMessageDialog(null,
			        "Make sure all tabs are green before pressing 'Done'.\n" +
			        "A green tab means you've finished all questions for that problem.",
			        "Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	public Dimension getSize() {
		return doneButton.getSize();
	}
	
	public Dimension getMinimumSize() {
		return doneButton.getMinimumSize();
	}
	
	public Dimension getPreferredSize() {
		return doneButton.getPreferredSize();
	}
	
	public Dimension getMaximumSize() {
		return doneButton.getMaximumSize();
	}
}
