package interaction;
import interaction.InterfaceAttribute.Style;

import java.util.ArrayList;
import java.util.List;
/**
 * This class serves as a demo implemetation of Backend. Because .wme file parsing
 * is not done in this class, we assume a specific interface consisting of:
 * 5 text boxes - rVal, gVal,bVal, fontVal, messageBox
 * 4 labels - border, background, font, fontSize
 * 
 * This backend interprets the numbers sent via SAI by rVal,gVal,bVal as
 * RGB values and stores them. It also interprets the SAI sent by fontVal as
 * a given font size between 8 and 20 and stores it. This backend, upon
 * receiving the SAIs, sends back an SAI to update the text of messageBox,
 * informing the user what the color or font size currently is.
 * 
 * This backend cases the SAIs sent by border, background,font,fontSize. If
 * border is clicked, this backend modifies the border color attribute of
 * messageBox using the stored color. Similarly, it modifies the background
 * color and font color when background and font are pressed respectively.
 * When fontSize is pressed, this backend modifies the font size attribute
 * of messageBox to the one stored.
 * 
 * When a textbox is double clicked, this backend modifies the border-width
 * and border style of that textbox. It randomly selects a number between
 * 1 and 10 and sets that as the size of the border-width, in pixels. It
 * also randomly chooses one of 5 available border styles to modify messageBox to.
 * These styles can be found in the Style enum, their names being self-descriptive.
 * 
 * When this backend receives the .wme files, it simply prints them to the console.
 * Parsing them is beyond me because I am not familiar with Jess.
 * 
 * @author Patrick Nguyen
 *
 */

public class SimBackend extends Backend{
	private Color c;
	private int fontSize;
	
	//Added paramter to match the parent constructor - Shruti
	public SimBackend(String[] argV){
		super(argV);
		c = new Color();//default color is totally black
		fontSize = 9;//some arbitrary font size between 8 and 20
	}
	
	public void processInterfaceEvent(InterfaceEvent ie){
		switch(ie.getType()){
			case SAI:
				processSAI(ie.getEvent());
				break;
			case DOUBLE_CLICK:
				processDoubleClick(ie.getEvent().getSelection().get(0));
				break;
		}
	}
	
	/**
	 * This method is overriden in order to receive and process SAIs sent
	 * by the interface. In this specific method, we case on the action
	 * and parse those separately.
	 */
	private void processSAI(SAI sai){
		String action = sai.getAction().get(0);//action performed on interface
		if(action.equals("UpdateTextArea")){//text area typed into
			processUpdateTextArea(sai);
		}else if(action.equals("ButtonPressed")){//button is pressed
			processButtonPressed(sai);
		}
	}
	
	/**
	 * This methods is overriden in order to receive and process a double click
	 *  on a textbox. In this specific method, we choose a random number from 
	 *  1 to 10 and a random border style out of 5 available, and modify messageBox's
	 *  border width and border style using those values. 
	 */
	private void processDoubleClick(String selection){
		InterfaceAttribute ia = getComponent(selection);//represents textbox's attributes
		ia.setBorderWidth((int)(Math.random()*10)+1);//set random border width
		int borderStyle = (int)(Math.random()*5);//choose random style
		if(borderStyle == 0){
			ia.setBorderStyle(Style.HIDDEN);//set border style
		}else if(borderStyle == 1){
			ia.setBorderStyle(Style.DASHED);
		}else if(borderStyle == 2){
			ia.setBorderStyle(Style.DOTTED);
		}else if(borderStyle == 3){
			ia.setBorderStyle(Style.DOUBLE);
		}else{
			ia.setBorderStyle(Style.SOLID);
		}
		modifyInterface(ia);//send object to interface in order to modify the component
	}
	
	/**
	 * This method processes specifically an SAI sent over by a text area.
	 * In all cases, we parse the (numerical) input and save it in the appropriate
	 * place. We also send back a message, to be disaplyed in messageBox, informing
	 * the user about the current color or font, or if their input is valid.
	 * 
	 * @param sai SAI sent by a text area
	 */
	private void processUpdateTextArea(SAI sai){
		String selection = sai.getSelection().get(0);//name of textbox
		if(selection.equals("messageBox"))//don't care about messageBox
			return;
		
		//preparing SAI to modify messageBox
		List<String> sel = new ArrayList<String>();
		sel.add("messageBox");
		List<String> act = new ArrayList<String>();
		act.add("UpdateTextArea");
		List<String> inp = new ArrayList<String>();
		SAI message = new SAI(sel,act,inp);
		
		
		String val = sai.getInput().get(0);
		int x = -1;
		try{
			x = Integer.parseInt(val);
		}catch(NumberFormatException e){
			inp.add("Please enter a number");//message if input not a number
			sendSAI(message);//send sai message back to interface
			return;
		}
		
		//case on which text area sent the sai
		//they all mostly do the same thing, which is perform a valid bounds check
		//and, if valid, saves the value
		if(selection.equals("rVal")){
			int r = x;
			if(0 <= r && r <= 255){//valid RGB value
				c.setR(r);//save value
				inp.add(String.format("Color %d %d %d", c.getR(),c.getG(),c.getB()));//message on messageBox
			}else{
				inp.add("Please enter a color value between 0 and 255");//message on messageBox
			}
		}else if(selection.equals("gVal")){//same as rVal
			int g = x;
			if(0 <= g && g <= 255){
				c.setG(g);
				inp.add(String.format("Color %d %d %d", c.getR(),c.getG(),c.getB()));
			}else{
				inp.add("Please enter a color value between 0 and 255");
			}
		}else if(selection.equals("bVal")){//same as rVal
			int b = x;
			if(0 <= b && b <= 255){
				c.setB(b);
				inp.add(String.format("Color %d %d %d", c.getR(),c.getG(),c.getB()));
			}else{
				inp.add("Please enter a color value between 0 and 255");
			}
		}else if(selection.equals("fontVal")){//similar to rVal
			int f = x;
			if(8 <= f && f<= 20){
				fontSize = f;
				inp.add(String.format("Font size  %d", fontSize));
			}else{
				inp.add("Please enter a font size between 8 and 20");
			}
		}
		sendSAI(message);//send SAI to send back to inteface to modify messageBox
	}
	/**
	 * This method specifically processes SAIs sent by a button. It first gets
	 * the InterfaceAttribute object corresponding to messageBox and then modifies
	 * it according to which button was pressed. Finally, it sends the modification
	 * back to the interface using modifyInterface
	 * @param sai
	 */
	private void processButtonPressed(SAI sai){
		String selection = sai.getSelection().get(0);//get name of button
		InterfaceAttribute ia = getComponent("messageBox");//get attributes of messageBox
		//case on which button was pressed and set appropriate field
		if(selection.equals("border")){
			ia.setBorderColor(c);
		}else if(selection.equals("background")){
			ia.setBackgroundColor(c);
		}else if(selection.equals("font")){
			ia.setFontColor(c);
		}else if(selection.equals("fontSize")){
			ia.setFontSize(fontSize);
		}
		modifyInterface(ia);//send back to interface to modify
	}
	
	/**
	 * This method must parse the Argument field values
	 * @author SHRUTI
	 */
	@Override
	public void parseArgument(String[] arg) {
		// TODO Auto-generated method stub
		
	}

}
