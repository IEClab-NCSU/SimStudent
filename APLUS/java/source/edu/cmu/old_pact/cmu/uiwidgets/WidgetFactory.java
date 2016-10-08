package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import edu.cmu.old_pact.cmu.spreadsheet.AltTextField;

//the widgetFactory provides a way to create standard widgets
//This is better than making the widgets directly from AWT, since we can customize the look

public class WidgetFactory {

	public static Button makeButton(String buttonname) {
		Button mInstance = new Button(buttonname);
		mInstance.setBackground(Color.lightGray);
		mInstance.setFont(new Font("Geneva", 0, 12));
		return mInstance;
	}
	
	public static TextField makeTextField(int w) {
		TextField mInstance = new TextField("", w);
		mInstance.setBackground(Color.white);
		mInstance.setFont(new Font("Geneva", 0, 12));
		mInstance.setEditable(true);
		return mInstance;
	}
	
	public static Label makeLabel (String text) {
		Label mInstance = new Label(text, 0);
		mInstance.setFont(new Font("Geneva", 0, 12));
		return mInstance;
	}
	
	public static Choice makeChoice(String items[]) {
		Choice mInstance = new Choice();
		mInstance.setFont(new Font("Geneva", 1, 12));
		int len = items.length;
		for (int i=0;i<len;++i) 
			mInstance.addItem(items[i]);
		
		mInstance.select(0);
		return mInstance;
	}
	
	public static AltTextField makeAltTextField(int x, int y, int width, int height) {
		AltTextField mInstance = new AltTextField();
		mInstance.reshape(x,y,width,height);
		mInstance.setForeground(new Color(0, 0, 0));
		mInstance.setBackground(new Color(255, 255, 255));
		mInstance.setFont(new Font("Geneva", 0, 12));
		//mInstance.show(true);
		//mInstance.enable(true);
		//mInstance.setEditable(true);
		return mInstance;
	}
	
	public static Panel okCancelPanel(){
		Panel bottom = new Panel();
		//bottom.setLayout(new GridLayout(1,3));
		bottom.setLayout(new FlowLayout(0));				
		Panel cancelPanel = new Panel();
		cancelPanel.add(WidgetFactory.makeButton("Cancel"));
				
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			bottom.add(cancelPanel);
			bottom.add(new Label("   "));
			bottom.add(new DefaultButton(WidgetFactory.makeButton("  Ok  "), DefaultButton.DOWN));
		} else {
			bottom.add(new DefaultButton(WidgetFactory.makeButton("  Ok  "), DefaultButton.DOWN));
			bottom.add(new Label("   "));
			bottom.add(cancelPanel);		
		} 
		return bottom;
	}

}
