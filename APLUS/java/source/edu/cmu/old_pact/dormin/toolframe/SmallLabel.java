//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/SmallLabel.java
package edu.cmu.old_pact.dormin.toolframe;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Label;

public class SmallLabel extends Label {

	public SmallLabel() {
		super();
	}
	
	public SmallLabel(String s) {
		super(s);
	}

	public SmallLabel(String s, int i) {
		super(s,i);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public Dimension getPreferredSize() {
		FontMetrics fm = getFontMetrics(getFont());
		
		int h =fm.getHeight();      //+fm.getDescent();
		return (new Dimension(600,h));
	}
}
