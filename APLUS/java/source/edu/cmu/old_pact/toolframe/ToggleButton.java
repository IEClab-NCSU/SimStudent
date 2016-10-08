package edu.cmu.old_pact.toolframe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.cmu.pact.Utilities.trace;


public class ToggleButton implements PropertyChangeListener{
	ImageButton[] imageButtons;
	
	public ToggleButton(ImageButton[] imageButtons){
		trace.out (10, this, "toggle button constructor: ImageButtons = " + imageButtons);
		this.imageButtons = imageButtons;
		int s = imageButtons.length;
		for(int i=0; i<s; i++)
			imageButtons[i].addPropertyChangeListener((PropertyChangeListener)this);
	}
	
	public ToggleButton(ImageButton imageButton){
		trace.out (10, this, "toggle button constructor: ImageButton = " + imageButton);
		imageButtons = new ImageButton[1];
		imageButtons[0] = imageButton;
		imageButtons[0].addPropertyChangeListener((PropertyChangeListener)this);
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		String proName = evt.getPropertyName();
		if(proName.equalsIgnoreCase("PRESSED")){
			ImageButton currButton = (ImageButton)evt.getNewValue();
			int s = imageButtons.length;
			for(int i=0; i<s; i++){
				if(imageButtons[i] == currButton)
					imageButtons[i].setPressed(true);
				else
					imageButtons[i].setPressed(false);
			}
		}
	}
	
	public void setPressedButton(ImageButton button){
		int s = imageButtons.length;
		for(int i=0; i<s; i++){
			if(imageButtons[i] == button)
				imageButtons[i].setPressed(true);
			else
				imageButtons[i].setPressed(false);
		}
	}
	
	public void setPressedButton(String button_name){
		int s = imageButtons.length;
		for(int i=0; i<s; i++){
			if((imageButtons[i].getName()).equalsIgnoreCase(button_name)) 
				imageButtons[i].setPressed(true);
			else 
				imageButtons[i].setPressed(false); 
		}
	}
	
	public void addButton(ImageButton button){
		int s = imageButtons.length;
		ImageButton[] dest = new ImageButton[s+1];
		System.arraycopy(imageButtons, 0, dest, 0, s);
		dest[s] = button;
		button.addPropertyChangeListener((PropertyChangeListener)this);
    	imageButtons = dest;
    }
    	
	public void removeAll(){
		trace.out (10, "ToggleButton.java", "remove all buttons");
		int s = imageButtons.length;
		for(int i=0; i<s; i++){
			imageButtons[i].removePropertyChangeListener(this);
			imageButtons[i].clear();
		}
		imageButtons = null;
	}	
		
}
		