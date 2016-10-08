package edu.cmu.old_pact.toolframe;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.cmu.old_pact.foldertabpanel.FolderTabPanel;
import edu.cmu.old_pact.gridbagsupport.GridbagCon;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.settings.Settings;


public class PreferencesFrame extends Frame {
	Font labelFont; 
	Font buttonFont; 
	String[] items = {"Small","Normal","Large","Larger"};
	int currentIndex = 1;
	Choice ch;
	Color bgColor = new Color(204, 204, 204);
	Panel selectWindow;
	CheckboxGroup cbg;
	Checkbox curWindowCB;
	ToolFrame currentWindow = null;
	
		
	public PreferencesFrame (String title, ToolFrame currentWin, 
							 String windowType) {
		super(title);
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
		  labelFont = new Font("geneva", Font.BOLD, 12); 
		  buttonFont = new Font("geneva", Font.PLAIN, 12);
		} else {
			labelFont = new Font("arial", Font.BOLD, 12); 
		    buttonFont = new Font("arial", Font.PLAIN, 12);
		}
		currentWindow = currentWin;
		currentIndex = currentWin.getCurrentFontIndex();
		
		// if current font index equals -1 it means the current
		// window can't change its font size (e.g. Skillometer)
		// in this case select global font size (stored in ObjectRegistry) 
		// and disable the current window check box.
		
		setLayout(new BorderLayout());		
			// for now we only use one tab of this tabPanel,
			// but in the future we'll be using more tabs for
			// other customizable settings
		FolderTabPanel tabPanel = new FolderTabPanel();	
		tabPanel.setBackground(bgColor);	
		Panel fontPrefPanel = new Panel();
		tabPanel.addPanel(fontPrefPanel, "  Font Preferences  ");
		add("Center", tabPanel);
	
		fontPrefPanel.setLayout(new GridBagLayout());
		
		Label fsizeLabel = new Label("Font Size:");
		fsizeLabel.setFont(buttonFont);		
		GridbagCon.viewset(fontPrefPanel,fsizeLabel, 0,0,1,1,10,10,0,0);		

		ch = createChoice(items);
		GridbagCon.viewset(fontPrefPanel,ch, 1,0,2,1,10,10,0,0);		
		
		Label applyLabel = new Label("Apply to: ");
		applyLabel.setFont(buttonFont);		
		GridbagCon.viewset(fontPrefPanel,applyLabel, 0,1,1,1,10,10,0,0);		
	
		selectWindow = createCheckboxGroup(windowType, currentWin);
		GridbagCon.viewset(fontPrefPanel,selectWindow, 1,1,2,2,10,10,0,0);		

		Panel okCancelPanel = createOkCancelPanel();		
		GridbagCon.viewset(fontPrefPanel,okCancelPanel, 0,3,3,1,15,10,0,0);		
		
		// set CarnegieLearning logo as icon
		setIconImage(Settings.loadImage(this, Settings.cllogo40));

		pack();
		setSize(380, 230);
		setLocation(120,120);
	}
	
	private Panel createCheckboxGroup(String windowType, ToolFrame currentWin) {
		Panel cbgPanel = new Panel();
		Checkbox cb;
		cbgPanel.setLayout(new GridLayout(3, 1));
		cbg = new CheckboxGroup();
		cb = new Checkbox("all windows", cbg, false);
		cb.setFont(labelFont);
      	cbgPanel.add(cb);
      	String windowName = currentWin.getName();
      	
      		// set window label
      	if(windowType.equalsIgnoreCase("problemStatement"))
      	  windowName = "Scenario";
     
      	curWindowCB = new Checkbox(windowName+" window", cbg, true);
 		curWindowCB.setFont(labelFont);
 		
 		if(currentIndex==-1){
 			curWindowCB.setEnabled(false);
 			cb.setState(true);
 		}
       	cbgPanel.add(curWindowCB);
     	return cbgPanel;
    }
    
	public void cancel(){
		ch.select(currentIndex);
		setVisible(false);
	}
	
	private void setFontSizeAction(){
		Checkbox selected = cbg.getSelectedCheckbox();
		String selLabel = selected.getLabel();
		currentIndex = ch.getSelectedIndex();
		
			// if "all windows" selected then change font size in all
			// registred windows (that are instances of ToolFrame)
			// otherwise change font size of the current window
		if(selLabel.startsWith("all")) {
				// store new font size as a global parameter
			ObjectRegistry.setGlobalFontSizeIndex(currentIndex);

			Hashtable hash = ObjectRegistry.getAllObjects();
			Enumeration objects = hash.elements();
			Object obj;
			while(objects.hasMoreElements()) {
  				obj = objects.nextElement();
  				if((obj instanceof ToolFrame)){
  					((ToolFrame)obj).setFontSize(currentIndex); 					
  					((ToolFrame)obj).sendNoteFontsizeSet(currentIndex);
				}
			}
		} 
		else {	
			currentWindow.setFontSize(currentIndex);
			currentWindow.sendNoteFontsizeSet(currentIndex);
		}
		setVisible(false);

	}	
	
	private Panel createOkCancelPanel() {
		Panel bot = new Panel();
		bot.setLayout(new FlowLayout());
		 
		Button okButton = createButton("     Ok     ");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setFontSizeAction();
			}
		});
		
		Button cancelButton = createButton("  Cancel  ");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel();
			}
		});
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			bot.add(cancelButton);
			bot.add(new Label("     "));
			bot.add(okButton);
		} else {
			bot.add(okButton);
			bot.add(new Label("     "));
			bot.add(cancelButton);		
		} 
		return bot;
	}

	private Choice createChoice(String items[]) {
		Choice ch = new Choice();
		ch.setFont(labelFont);
		for (int i=0;i<items.length;++i) 
			ch.addItem(items[i]);
		
		if(currentIndex == -1)
		 	ch.select(ObjectRegistry.getGlobalFontSizeIndex());
		else
			ch.select(currentIndex);
		return ch;
	}
	
	public void setCurrentFontSizeInd(int ind) {
		currentIndex = ind;
		if(currentIndex == -1)
		 	ch.select(1); 
		else
			ch.select(currentIndex);
	}
	
	private Button createButton(String label){
		Button b = new Button(label);
		b.setFont(buttonFont);
		b.setSize(40,20);
		return b;
	}
	public void windowOpened(WindowEvent e) { }

    public void windowClosing(WindowEvent e) { 
    	cancel();
    }

    public void windowClosed(WindowEvent e) { 
    	cancel();
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e){
    }

    public void windowActivated(WindowEvent e){ }

    public void windowDeactivated(WindowEvent e){ }		
}
