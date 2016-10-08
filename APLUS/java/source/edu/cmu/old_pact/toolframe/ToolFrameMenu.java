package edu.cmu.old_pact.toolframe;


import java.awt.Event;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//a ToolFrameMenu is a menu that sends events to a ToolFrame that is *not* its parent
//we use this so that we can have the same menus associated with different frames

//Although the ToolFrameMenu uses the 1.1 event handling, it assumes that the Frame uses
//1.0 event handling (though this might be upward-compatible).

public class ToolFrameMenu extends Menu implements ActionListener {
	Frame target; //the frame that handles selections from this menu
	
	public ToolFrameMenu(String name,Frame frame) {
		super(name);
		target = frame;
	}
	
	public ToolFrameMenu(Menu copyMe,Frame frame) {
		this(copyMe.getLabel(),frame);
		for (int i=0;i<copyMe.getItemCount();++i) {
			MenuItem thisItem = copyMe.getItem(i);
			MenuItem newItem = new MenuItem(thisItem.getLabel());
			newItem.addActionListener(this);
			add(newItem);
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		//Need to convert the 1.1 ActionEvent into a 1.0 Event
		Event eventToPost = new Event(this,Event.ACTION_EVENT,event.getActionCommand());
		target.postEvent(eventToPost); //pass the event to the target frame
	}
}

	
	