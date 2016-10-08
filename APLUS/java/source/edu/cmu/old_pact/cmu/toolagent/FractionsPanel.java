//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/FractionsPanel.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.linkvector.LinkVector;
import edu.cmu.old_pact.toolframe.Hintable;


public class FractionsPanel extends Panel implements Hintable{
	Fraction[] fractions;
	
	public FractionsPanel(ObjectProxy parent, LinkVector links){
		setLayout(new GridBagLayout());
		fractions = new Fraction[3];
		int delta = 0;
		for(int i=0; i<3; i++) {
			fractions[i] = new Fraction(parent, links);
			GridbagCon.viewset(this,fractions[i], i+delta, 0, 1, 1, 0, 0, 0 ,0);
			if(i != 2){
				Label eqLabel = new Label("=");
				delta++;
				GridbagCon.viewset(this,eqLabel, i+delta, 0, 1, 1, 0, 12, 0 ,0);
			}
		}
	}
	
	public boolean asksForHint(){
		for(int i=0; i<3;i++){
			if(fractions[i].asksForHint())
				return true;
		}
		return false;
	}
	
	public void removeAll(){
		for(int i=0; i<3; i++){
			fractions[i] = null;
		}
		fractions = null;
		super.removeAll();
	}
}
			