package edu.cmu.old_pact.cmu.messageInterface;



import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.dorminbutton.DorminButton;
import edu.cmu.old_pact.htmlPanel.HtmlPanel;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.settings.ImageCanvas;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.pact.Utilities.trace;

/**
 * A class that displays the user's messages.
 */

public class UserMessageWindow extends DorminToolFrame implements  Runnable{
	/**
     * Number of messages to be displayed.
     */
	protected int numOfMess;	
	/**
     * The number of current message.
     */
	protected int currMess = -1;
	
	/**
	* A font of a messagePanel.
	*/
	protected Font messageFont; 
	
	/**
	* A foregroung color of a messagePanel.
	*/ 
	protected Color messageColor = Color.black;
	
	/**
     * The navigation buttons.
     */
	protected DorminButton next, prev, cancel;
	
	/**
     * The panels that are updated for each set of messages to be displayed.
     */
	protected Panel messagePanel, prevNextPanel;
	
	/**
     * A panel to display the images.
     */
	protected ImagePanel imagePan;
	/**
     * A a image to be set in imagePanel.
     */
	protected Image image;
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
	UserMessage[] userMessage;	
	/**
	* A number used in delay culculations
	**/
	private int seenMessages = 0;
	/**
	* A panel to highlight the next button delay
	*/
	private Panel delayPanel;
	/**
	* Delay panel color
	**/
	private Color delayColor = Settings.delayColor;
	/**
	* A delay interval
	**/
	long delay = 0;
	/**
	* A delay thread
	**/
	private Thread  theThread;	
	/**
	* refresh for for window
	**/
	private int delta=1;
	/** 
	 * four actual font sizes to correspond to 
  	 * "small, normal, big, bigger" respectively in the preferences setting
  	 */
  	 private int[] fontSizes = {10, 12, 14, 18};
	private String UrlBase;
	
	protected ImageCanvas imageCanvas;
	
	//private boolean closeThisWindow = false;

	/**
 	 * Constructs a UserMessageWindow 
 	 */
	public UserMessageWindow(String UrlBase) {
		this (UrlBase, null);
	}

	public UserMessageWindow(String UrlBase, String imageName) {
		super("UserMessageWindow");

		this.UrlBase = UrlBase;
		
		//DORMIN.trace.out (5, this, "CREATING user message window");

		//String vendor = System.getProperty("java.vendor");

		
		try {
			String delayStr = System.getProperty("NextMessageDelay");
			if(delayStr != null)
				delay = Long.valueOf(delayStr).longValue();
		} catch (Exception e) {
		
		}
		
		
		curFontSizeIndex = 1;  // defaults to "normal"
        setLayout(new BorderLayout(0,1));
    	setBackground(Color.white);
    	setURLBase(UrlBase);
    	

   		messageFont = new Font("arial", Font.PLAIN, 10);


		try {
	    	if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
	    		messageFont = new Font("geneva", Font.PLAIN, 10);
	    	else
	    		messageFont = new Font("arial", Font.PLAIN, 10);
		} catch (Exception e) {

			// Default to Windows font
    		messageFont = new Font("arial", Font.PLAIN, 10);
		}

		createHTMLPanel();
		
		initialize();
		
		if (imageName != null)
			addImage (imageName);
		
		pack();
		setCurrentWidth(420);
		setCurrentHeight(170);
		setCurrentLocation(new Point(200, 200));
		setSize(420, 170);
		setLocation(200, 200);
		
		// update font size if different from the stored or global value
		setFontSize(ObjectRegistry.getWindowFontSize("UserMessageWindow"));	
		
    }


	public void createHTMLPanel () {
		//trace.out (5, this, "create new html panel");
		if (htmlPanel != null)
			remove(htmlPanel);
			
		htmlPanel = new HtmlPanel(this, 420, 120);
		htmlPanel.setFgColor(messageColor);
		htmlPanel.setTopMargin(4);
		add("Center",htmlPanel);     
		htmlPanel.setURLBase(UrlBase);
		pack();
		trace.out (5, this, "html panel = " + htmlPanel);
	}

	public void addImage (String imageName) {    
//		trace.out (5, this, "ADDING IMAGE " + imageName + " !!!");
		Image i = edu.cmu.old_pact.settings.Settings.loadImage (this, imageName);
		if (imageCanvas != null)
			remove (imageCanvas);
			
		imageCanvas = new edu.cmu.old_pact.settings.ImageCanvas (i);
		add ("West", imageCanvas);
		pack();
	}	

    public UserMessageWindow(){
		this(null);
	}
/*	
	public void refresh(){
		Dimension d = getSize();
		setSize(d.width, d.height+delta);
		delta = (-1)*delta;
	}
*/	

    private void initialize() {
        Panel buttonsPanel = new Panel();
        
        buttonsPanel.setLayout(new FlowLayout(1));
        prevNextPanel = new Panel();
        prevNextPanel.setLayout(new CardLayout());
        
        prev = createButton("<<<");
//        prev.addActionListener(new ActionListener() {
//      		public void actionPerformed(ActionEvent e) {
//      			doPrevious();
//      		}
//    	});
//        
        next = createButton(">>>");
//        next.addActionListener(new ActionListener() {
//      		public void actionPerformed(ActionEvent e) { 
//       			doNext();
//      		}
//    	});

        cancel = createButton("OK");
        cancel.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) { 
      			closeWindow();
      		}
    	});
    				
    	Panel[] support = new Panel[2];
        for(int h=0; h<2; h++)  {
        	support[h] = new Panel();
            support[h].setLayout(new FlowLayout(1));
            if(h == 0)  {
         		support[h].add(prev);
         		support[h].add((new Label("    ")));
         		delayPanel = new Panel();
         		delayPanel.add(next);
         		support[h].add(delayPanel);
         		//support[h].add(next);
         		support[h].add((new Label("    ")));
            }
            if(h == 1)  
                 support[h].add((new Label("")));
         	prevNextPanel.add(String.valueOf(h), support[h]);
        }
        buttonsPanel.add(prevNextPanel);
        buttonsPanel.add(cancel);
        add("South", buttonsPanel); 
        setResizable(true);
        ((CardLayout)prevNextPanel.getLayout()).first(prevNextPanel);
        this.enableEvents(	AWTEvent.WINDOW_EVENT_MASK );	
        
       // addKeyListener(this);       
	}
	
	public void delete(){
		messageFont = null;
		messageColor = null;
		if(messagePanel != null)
			messagePanel.removeAll();
		messagePanel = null;
		if(prevNextPanel != null)
			prevNextPanel.removeAll();
		prevNextPanel = null;
		imagePan = null;
		image = null;
		base = null;
		htmlPanel = null;
		userMessage = null;
		//removeKeyListener(this);
		super.delete();
	}
	
	public void keyTyped(KeyEvent e){ }
	
    public void keyReleased(KeyEvent e){ }
	
	public void keyPressed(KeyEvent evt){
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_ENTER)
			closeWindow();
         else super.keyPressed(evt);
	}
	
	
	public void createButtonProxy(){
//		prev.createProxy(getObjectProxy());
//		next.createProxy(getObjectProxy());
//		cancel.createProxy(getObjectProxy());
	}
	
	public boolean doPrevious(){
		trace.out(5, this, "previous clicked");
		if(currMess-1>=0) {
        	userMessage[currMess].unPoint();
        	currMess = currMess-1;
        	resetView();
        	return true;
        }
        return false;
    }
    
    public boolean doNext(){
		trace.out(5, this, "next clicked");
       	if(currMess+1<numOfMess) {
        	userMessage[currMess].unPoint();
        	currMess = currMess+1;
        	resetView();
        	if(delay > 0)
        		delayNext();
        	return true;
       	}
       	return false;	
    }
    
    protected void delayNext(){
    	if(currMess > 0 && seenMessages < currMess+1 && currMess != numOfMess-1) {
    		next.setEnabled(false);
    		delayPanel.setBackground(delayColor);
   			delayPanel.repaint(); 
    		if(theThread != null && theThread.isAlive())
    			theThread = null;
    		
    		theThread = new Thread(this);
    		theThread.start();
    	}
    }
   
    public void run(){
    	long cur = System.currentTimeMillis();
		while(System.currentTimeMillis() - cur < delay){
		}
    	delayPanel.setBackground(getBackground());
   		delayPanel.repaint(); 
    	next.setEnabled(true);
    	seenMessages++;
    }
   
    protected void resetView(){
    	changeView(userMessage[currMess].getText());
        checkButtons();
        userMessage[currMess].point();	
    }
	
	
	private DorminButton createButton(String label){
		DorminButton toret = new DorminButton(getObjectProxy());
		toret.setLabel(label);
		toret.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent evt){
				if (evt.getKeyCode() == KeyEvent.VK_ENTER)
					closeWindow();
				else 
					super.keyPressed(evt);
			}
		});
		toret.setWidth(60);
		toret.setHeight(20);
		return toret;
//		return null;
	} 
	
	public void processWindowEvent(WindowEvent e) {
		if(e.getID() == WindowEvent.WINDOW_CLOSING) {
			closeWindow();
		}
		else super.processWindowEvent(e);
	}
	/**
	* Dispalys a message in an html format.
	*/ 
	public void changeView(String body) {
		trace.out (5, this, "display html: " + body);

		//if (htmlPanel == null)
			createHTMLPanel();
			
		htmlPanel.displayHtml(body);
		//trace.out (5, this, "width = " + this.getSize().width);
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
	public void setMessageColor(Color color) {
		this.messageColor = color;
	}
	
	/**
	* Sets a specified font size for HTML viewer
	* @param sizeIndex - an index of the new font size from 
	* the fontSizes array.
	**/
	public void setFontSize(int sizeIndex){
		if(sizeIndex != curFontSizeIndex) {
  			curFontSizeIndex = sizeIndex;
  			htmlPanel.setFontSize(fontSizes[sizeIndex]);
  		}
	}

	/**
 	* Removes imagePan from layout, releases all Pointers and hides this window.
 	*/
	public void closeWindow() {
		//setCloseThisWindow(true);
		clearWindow();
		setVisible(false);
	}
	
	public void clearWindow(){
		if(imagePan != null)
			remove(imagePan);
		if(currMess != -1)
			userMessage[currMess].unPoint();
		currMess = -1;
	}
	
	/**
     * Sets the specified image in ImagePanel.
     * If image = null - ImagePanel will not be added.
     * @param imageBase - the path to the specified image.
     */
     
     /*
	public void setImage(String imageBase) {
      	imagePan = new ImagePanel();
   		imagePan.setImage(imageBase);
   		add("West", imagePan);
	}
	*/
	
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
     * Disables and enables prev and next buttons based upon the number 
     * of current message.
     */
	public void checkButtons() {
		if(currMess == 0) 
				prev.setEnabled(false);
		if(currMess>0)
			prev.setEnabled(true);
		if(currMess < (numOfMess-1))
			next.setEnabled(true);
		if(currMess == (numOfMess-1))
			next.setEnabled(false);
	}
	 		
	/**
     * Sets new set of messages to be displayed.
     * @param userMessage - an array of UserMessages .
     * @param  startFrom - the number of message to start to display. Usually startFrom = 1; 
     */
	public void presentMessages(UserMessage[] userMessage, int startFrom) {
		clearWindow();
		htmlPanel.resetProblem("hint"); // clear html panel
		if(currMess != -1)
			userMessage[currMess].unPoint();
		this.userMessage = userMessage;
		numOfMess = userMessage.length;
		currMess = startFrom-1;
		seenMessages = currMess;
		if(numOfMess <1) return;
        changeView(userMessage[currMess].getText());
        checkButtons();
        
        if(imagePan != null)
			remove(imagePan);
			
		if(numOfMess == 1)
			((CardLayout)prevNextPanel.getLayout()).last(prevNextPanel);
		else if(numOfMess > 1)
			((CardLayout)prevNextPanel.getLayout()).first(prevNextPanel);
				
        setVisible(true);
        requestFocus();
        
        //if(currMess+1<numOfMess) 
        
    		delayNext();
	}
	
	public void setVisible(boolean v){
		if(v){
			if(!isVisible()){
				try{
					userMessage[currMess].point();
				} catch (ArrayIndexOutOfBoundsException e ) { }
			}
		}
		else
			clearWindow();
		super.setVisible(v);
		if(v){
			toFront();
			requestFocus();
		}
	}
	
	/**
     * Sets new set of messages to be displayed without any image.
     * @param userMessage - an array of user messages in specified.
     */
	public void presentMessages(UserMessage[] userMessage) {
		this.presentMessages(userMessage, 1);
	}
	
	public void setTitle(String title){
		super.setTitle(title);
		if(htmlPanel != null)
			htmlPanel.setNewTitle(title);
	}
	
	// don't register this window for focus lost/gained events in ObjectRegistry
	public void windowActivated(WindowEvent e){ 
	}

/*	
	public void setCloseThisWindow(boolean closeFlag)
	{
		closeThisWindow = closeFlag;
	}
	
	public boolean getCloseThisWindow()
	{
		return this.closeThisWindow;
	}

	public void focusGained(FocusEvent e)
	{
		super.focusGained(e);
		setCloseThisWindow(false);
	}
	
	public void focusLost(FocusEvent e) 
 	{
 		if (!getCloseThisWindow())
 		{
 			setVisible(true);
 			this.toFront();
 			this.requestFocus();
 		}
 		else
 			super.focusLost(e);
 	}
 	*/	
 	
}	
	
			
		
