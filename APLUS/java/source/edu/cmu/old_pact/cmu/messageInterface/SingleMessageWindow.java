package edu.cmu.old_pact.cmu.messageInterface;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;

import edu.cmu.old_pact.htmlPanel.HtmlPanel;
import edu.cmu.old_pact.settings.Settings;


/**
 * A class that displays the user's messages.
 */

public class SingleMessageWindow extends Window {
	/**
	* A minimum width of a Window.
	*/
	private int minWidth = 180;
	
	/**
	* A font of a messagePanel.
	*/
	protected Font messageFont; 
	
	/**
	* A foregroung color of a messagePanel.
	*/ 
	protected String messageColor = "#000000"; 
	/**
	* A backgroung color of a messagePanel.
	*/ 
	protected Color backColor = Settings.bugMessageBackground;

	/**
     * A panel that is updated for each set of messages to be displayed.
     */
	protected Panel messagePanel;
	
	/**
     * A panel to display the images.
     */
	protected ImagePanel imagePan;
	/**
     * A a image to be set in imagePanel.
     */
	private Image image;
	/**
	* A base for displaying images.
	*/
	private URL base = null;
	/**
	* An  html panel.
	*/ 
	protected HtmlPanel htmlPanel = null;
	
	/**
     * The userMessages to be displayed.
     */
	public UserMessage userMessage;
	
	/**
 	* Constructs a UserMessageWindow 
 	*/
	public SingleMessageWindow(Frame parent, String UrlBase) {
		super(parent);
		setLayout(new BorderLayout());
    	setBackground(backColor);
    	setURLBase(UrlBase);
    	
    	if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
    		messageFont = new Font("geneva", Font.PLAIN, 10);
    	else
    		messageFont = new Font("arial", Font.PLAIN, 10);
    		
    	  //create HtmlPanel without scroll bar
		htmlPanel = new HtmlPanel(150,60,backColor,false);
		htmlPanel.setFgColor(messageColor);
		add("Center",htmlPanel);     
		htmlPanel.setURLBase(UrlBase);

		pack();
		setSize(150,60);
	}  
	
	public SingleMessageWindow(Frame parent) {
		this(parent, null);
	}
	/**
	* Sets a specified font size for HTML viewer
	* @param s - a size to be set. Now it's a choice 
	* from 7 sizes : 10,11,12,20,26,28,32
	**/
	public void setFontSize(int s){
		htmlPanel.setFontSize(s);
		if(userMessage != null)
			changeView(userMessage.getText());
	}

	/**
	* Dispalys a message in an html format.
	*/ 
	private void changeView(String body) {
		htmlPanel.displayHtml(body);
	}
	
	/**
	* Sets a specified font for messagePanel.
	* @param font - a font to be set.
	*/
	public void setMessageFont(Font font) {
		this.messageFont = font;
	}
	
	/**
	* Sets a specified color for messagePanel.
	* @param color - a color to be set.
	*/
	public void setMessageColor(String color) {
		this.messageColor = color;
		htmlPanel.setFgColor(messageColor);
		repaint();
	}
	
	/**
	* Sets a specified background color for messagePanel.
	* @param color - a color to be set.
	*/
	public void setBackgroundColor(Color color) {	
		backColor = color;
		htmlPanel.setBgColor(color);
		htmlPanel.repaint();
		repaint();
	}

	public void setVisible(boolean v) {
		if(! v){
			if(imagePan != null)
				remove(imagePan);
			userMessage.unPoint();
		}
		super.setVisible(v);
	}
	
	/**
     * Sets the specified image in ImagePanel.
     * If image = null - ImagePanel will not be added.
     * @param imageBase - the path to the specified image.
     */
	public void setImage(String imageBase) {
      	imagePan = new ImagePanel();
   		imagePan.setImage(imageBase);
   		add("West", imagePan);
	}
	
	/**
	* Sets url base for displaying images in a html format.
	*/
	public void setURLBase(String b){
		if(base == null){
			try{
				base = new URL(b);
			}
			catch (MalformedURLException e) {
			}
		}
	}
	
	/**
     * Sets new set of messages to be displayed.
     * @param userMessage - an array of UserMessages .
     * @param title -  the title of this userMessageWindow.
     * @param  startFrom - the number of message to start to display. Usually startFrom = 1; 
     */
	public void presentMessages(UserMessage userMessage,String title) {
		this.userMessage = userMessage;
		htmlPanel.setNewTitle(title);
        changeView(userMessage.getText());
        
        if(imagePan != null)
			remove(imagePan);
        //if(imageBase != null)
			//setImage(imageBase);
			
        userMessage.point();
        setVisible(true);
	}
	
	/**
     * Sets new set of messages to be displayed without title.
     * @param userMessage - an array of user messages in specified.
     */
	public void presentMessages(UserMessage userMessage) {
		this.presentMessages(userMessage, "");
	}
	
	public Dimension preferredSize(){
		return new Dimension(minWidth, htmlPanel.preferredHeight());
	}
	
}		
			
		