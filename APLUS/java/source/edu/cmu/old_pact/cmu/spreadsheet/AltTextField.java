package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.fastbeanssupport.FastVetoBeansSupport;
import edu.cmu.old_pact.html.library.WebEqImage;
import edu.cmu.pact.Utilities.trace;



public class AltTextField extends Canvas implements Gridable, PropertyChangeListener{
													//FocusListener, 
													//MouseListener, MouseMotionListener{	
	public final static int ALIGN_TOP    = 0;
	public final static int ALIGN_CENTER = 1;
	public final static int ALIGN_BOTTOM = 2;
	
	public final static int ALIGN_LEFT   = 0;
	public final static int ALIGN_MIDDLE = 1;
	public final static int ALIGN_RIGHT  = 2;
	
	public final static int NO_BOUND=0;
	public final static int BOUND_EMPTY_TEXT=1;
	public final static int BOUND_MODIFIED_TEXT=2;
	
	//public final static int NO_VISUAL_BOUNDS = 0;
	//public final static int VISUAL_BOUNDS = 1;
	
	public final static int NO_GROW = 0;
	public final static int VERTICAL_GROW = 1;
	public final static int HORIZONTAL_GROW = 2;
	
	boolean m_horBoundary	=true;
	boolean m_vertBoundary	=true;
	boolean m_visualBounds	=false;
	
	boolean isResized = false;
	
	/**
	*  userAction[0] = "CurrentValue"  (String)
	*  userAction[1] = "CaretPosition" (Integer);
	**/
	private Object[] userAction;
	boolean traceUserAction = false;
	private boolean wasUserAction = false;
	
	Color hiliting = Color.lightGray;
	
	int hInset = 2; //4;
	int vInset=2;
	int vAlignment = ALIGN_TOP;
	int hAlignment = ALIGN_LEFT;
	
	protected char text[] = new char[20];
	protected int textLength = 0;
	protected int selStart = 0;
	protected int selEnd = 0;
	protected int yCaret = 0;
	protected int xCaret = 0;
	protected int textLengthLimit = -1;
	
	//Image displayImage = null;
	WebEqImage 	displayImage = null;

	int lineBreaks[];
	
	protected boolean editable 		= true;
	protected boolean hasFocus 		= false;
	protected boolean highlighted		= false;
	protected boolean justSelected 	= false;
	protected boolean modified		= false;
	protected boolean touched			= false;
	protected boolean selected		= false;
	protected boolean calculatable 	= false;
	protected boolean numeric			= false;
	protected boolean internal_selected = false;
	protected boolean editMode 		= false;
	boolean hasClipboardEvents = true;
	boolean paintBLTR		= false;
	boolean displayWebEq    = false;
	boolean canGrowHorizontally = true; 
	boolean canBeSelected 	= false;
		// this parameter is relevant only for cells that can grow vertically;
		// when set to false a cell never chages its width; 
	
	public boolean showCaret = false;
	
	protected int m_boundingStyle=NO_BOUND;
	protected int m_grow = NO_GROW;
	
	Frame frame = null;

	int minWidth = 70;
	int minHeight = 20;
	
	Font defaultFont = new Font("Arial",Font.PLAIN, 12);
	
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	protected FastVetoBeansSupport vetos = new FastVetoBeansSupport(this);
	
	protected AltTextFieldListener listener;
	
	
	public AltTextField() {
		setFont(defaultFont);
		CaretThread.initCaretThread();
		this.enableEvents(	AWTEvent.FOCUS_EVENT_MASK |
							AWTEvent.KEY_EVENT_MASK   |
							AWTEvent.MOUSE_EVENT_MASK |
							AWTEvent.MOUSE_MOTION_EVENT_MASK );
		userAction = new Object[2];
	}
	
	public void addListener (AltTextFieldListener listener) {
		this.listener = listener;
	}

	public boolean hasFocus(){
		return hasFocus;
	}
/*	
	public void addNotify() {
		super.addNotify();
		frame = getFrame();
	}
*/	
	
	public boolean isEditable(){
		return editable;
	}
	
	public void setHasClipboardEvents(boolean b){
		hasClipboardEvents = b;
	}
	
	public void setOwnProperty(String pro_name, Object pro_val) throws NoSuchFieldException{
	}
	
	public Hashtable getOwnProperty(Vector v) throws NoSuchFieldException{
		throw new NoSuchFieldException();
	}
	
	public void setColor(String whereColor, String colorStr){
		
	}
	
	public void setCalculate(boolean c){
		calculatable = c;
	}
	
	public void setNumeric(boolean n){
		numeric = n;
	}
	
	public void setSelected(boolean s){
		selected = s;
		repaint();
	}
	
	public void setInternalSelected(boolean s){
		internal_selected = s;
		repaint();
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public boolean isInternalSelected(){
		return internal_selected;
	}
	
	public void writeToCell(){
	}
	
	public Frame getFrame() {
		if(frame != null)
			return frame;
		Component parent = getParent();
		Component root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		frame = (Frame) root;
		return frame;
	}
	
	// initial min size
	public int getMinWidth(){
		return minWidth;
	}
	
	public void setMinWidth(int w){
		minWidth = w;
	}
		
	public int getMinHeight(){
		return minHeight;
	}

	public void setMinHeight(int h){
		minHeight = h;
	}
	
	    // bean support
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
		if(hasFocus)
			changes.firePropertyChange("FocusGained", null, this);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		if(changes != null)
			changes.removePropertyChangeListener(l);
	}
	public void addVetoableChangeListener(VetoableChangeListener l){
		vetos.addVetoableChangeListener(l);
	}
	
	public void removeVetoableChangeListener(VetoableChangeListener l){
		vetos.removeVetoableChangeListener(l);
	}
	

	public boolean mouseEnter (Event e, int x, int y) {
		getFrame().setCursor(Frame.TEXT_CURSOR);
		return true;
	}
	
	public void showMessage(UserMessage[] userMessages, String imageBase,String title, int startFrom){
	}
	
	int mouseStart = 0;
	int mouseLast = 0;
	
	public void mousePressed(MouseEvent evt){
		//trace.out (10, this, "mouse pressed");
		if(!editable && canBeSelected){
			setSelected(!selected);
			if(hasFocus)
				setBackground(getBackgroundColor());
		}
		if(!hasFocus)
			requestFocus();
		if (evt.getClickCount() > 1) {
			selectAll();
			mouseStart = 0;
		} else {
			if(justSelected)
				selectAll();
			else {
				setSelected (true);
				editMode = true;
				mouseStart = point2Index(evt.getX(), evt.getY());
				selStart = mouseStart;
				selEnd = mouseStart;
			}
		}
		//justSelected=false;
		//requestFocus();	
		repaint();
	}
	
	public boolean getSelected(){
		return selected;
	}
	
	public void sendIsSelected(boolean b){
		//"isselected"
	}	
	
	public void processMouseEvent(MouseEvent evt){
    	super.processMouseEvent(evt);  // Needed for MouseListeners on this to work.
		if (evt.getID() == MouseEvent.MOUSE_PRESSED)
			mousePressed(evt);
		else if(evt.getID() == MouseEvent.MOUSE_EXITED)
			mouseExited(evt);
		else if(evt.getID() == MouseEvent.MOUSE_ENTERED)
			mouseEntered(evt);
		else if(evt.getID() == MouseEvent.MOUSE_RELEASED) {
			mouseReleased(evt);
		}
		else if(evt.getID() == MouseEvent.MOUSE_MOVED) {
			mouseMoved(evt);
		}
  //        else
  //                super.processMouseEvent(evt);
	}
		
	public void mouseClicked(MouseEvent evt){ 
		mousePressed(evt);
	}
	
	public void mouseReleased(MouseEvent evt) {
		if(justSelected)
			justSelected=false;
	}
	
	public void mouseExited(MouseEvent evt) { 
		getFrame().setCursor(Frame.DEFAULT_CURSOR);
	}
	
	public void mouseEntered(MouseEvent evt) { }
	
	public void processMouseMotionEvent(MouseEvent evt){
  		super.processMouseMotionEvent(evt);  // Needed for MouseMotionListeners on this to work.
		if(evt.getID() == MouseEvent.MOUSE_DRAGGED)
			mouseDragged(evt);
  //        else
  //                super.processMouseMotionEvent(evt);
	}
	
	public void mouseDragged(MouseEvent evt) {
		if(!justSelected & isVisible()){
			int mouseCurrent = point2Index(evt.getX(), evt.getY());
		
			if (mouseCurrent != mouseLast) {
				mouseLast = mouseCurrent;
				if (mouseCurrent < mouseStart) {
					selStart = mouseCurrent;
					selEnd = mouseStart;
				} else {
					selStart = mouseStart;
					selEnd = mouseCurrent;
				}
				repaint();
			}
		}
	}
	
	public void mouseMoved(MouseEvent evt) { }

    public void enableBoundaries(boolean horBoundary,boolean vertBoundary)
    {
    	m_horBoundary=horBoundary;
    	m_vertBoundary=vertBoundary;
    }	
    
    public void setEditable(boolean editable) {
    	this.editable = editable;
    }
    
    public void setAlignment(int horz, int vert) {
		hAlignment = horz;
        vAlignment = vert;
   	}

	synchronized public String getText() {
    	return new String(text, 0, textLength);
    }
    
    synchronized public void setText(String t) {
    	select(0, textLength);
    	replaceSelection(t);
    }
    
    synchronized public String getSelectedText() {
    	String textStr = getText();
    	return textStr.substring(getSelectionStart(), getSelectionEnd());
    }
    
    synchronized public int getSelectionStart() {
    	return selStart;
    }
    
    synchronized public int getSelectionEnd() {
    	return selEnd;
    }
    
   synchronized  public void select(int start, int end) {
    	selStart = start;
    	selEnd   = end;
    }
    
   	synchronized  public void selectAll() {
    	if (!editable)
    		return;
    	select(0,textLength);
    }
    
   
    public void setWidth(int w){

    	Dimension d = getSize();
    	setSize(w, d.height);
    	if(getParent() != null){
    		getParent().setSize(w, d.height);
	    	repaint();
	    }
    }
    
    public void setHeight(int h){
    	try{
    		Dimension d = getSize();
    		setSize(d.width, h);
    
    		if(getParent() != null){
	    		getParent().setSize(d.width, h);
	    		repaint();
	    	}
	   
	    } catch (NullPointerException e) { 
	    	//System.out.println("AltTextField setHeight "+e.toString());
	    }
    }
   
    
    public void propertyChange(PropertyChangeEvent evt){
    	if(evt.getPropertyName().equalsIgnoreCase("HEIGHT")) {
			int newsize = ((Integer)evt.getNewValue()).intValue();
			setHeight(newsize);
		}
		else if(evt.getPropertyName().equalsIgnoreCase("WIDTH")) {
			int newsize = ((Integer)evt.getNewValue()).intValue();
			setWidth(newsize);
		}
		else if(evt.getPropertyName().equalsIgnoreCase("ISSELECTED")) 
			setSelected(((Boolean)evt.getNewValue()).booleanValue());
		else if(evt.getPropertyName().equalsIgnoreCase("INTERNALSELECTED")) 
			setInternalSelected(((Boolean)evt.getNewValue()).booleanValue());
		
	}
	
	
	public int getWidth(){
		return getSize().width;
	}
    
    public int getHeight(){
		return getSize().height;
	}
	
	// return minimal actual height unless its less than min initial height
	public int getMinimumHeight(){
		try{
			return Math.max(getBoundingBox().height,getMinHeight()) ;
		} catch (NullPointerException e){
			return getHeight();
		}
	}
    
    public boolean getCanGrowHorizontally() {
    	return canGrowHorizontally;
    }
    
    public void setCanGrowHorizontally(boolean val) {
    	canGrowHorizontally = val;
    }
    
    // return minimal actual width unless its less than min initial width
    public synchronized int getMinimumWidth(){
   	try{
			return Math.max(getBoundingBox().width,getMinWidth());
		} catch (NullPointerException e){
			return getWidth();
		}
	}
     	
 	public void setValue(Object v){
 		setText((String)v);
 	}
 	
 	public void clear(){
 		changes = null;
 		vetos = null;
 		offScreenImage = null;
 		offScreenGraphics = null;
 		frame = null;
 		userAction = null;
 		if( displayImage != null)
			displayImage.delete(); 	
 	}
 	
 	public void setBugMessage(UserMessage[] mess){
 	}
 	
 	public void setName(String n){
 	}
 	public String getName(){
 	return "";
 	}
 	
 	public void setHighlighted(boolean h){
 	}
 	
 	public void setHInset(int i){
 		hInset = i;
 	}


	public void calculateLineBreaks(boolean horBoundary,
     											 boolean vertBoundary) {
     	calculateLineBreaks(horBoundary,vertBoundary,false);
     }
     											    											  
    synchronized public void calculateLineBreaks(boolean horBoundary,
     											  boolean vertBoundary,
     											  boolean typingMode){  
     	isResized = false;
    	if (horBoundary)
	    	calculateLineBreaks(typingMode);
		
	    else {    // this cell can grow horizontally
	    	lineBreaks=null;
	    	try{
	    		Rectangle rect=getBoundingBox();
	    		//rect.grow(1,2);
	    		if(rect != null)
	    			rect.grow(0,2);
	    	} catch (NullPointerException e) {
	    		//System.out.println("AltTextField calculateLineBreaks "+e.toString());
	    	}
	    }	
    }
    	
    // for cells that can gow vertically (and horizontally within long words!)
    public void calculateLineBreaks(boolean typingMode) {
    	synchronized(this){
    	Font f = getFont();
    	FontMetrics fm = getFontMetrics(getFont());
    	int currentWidth = hInset;
    	int lastPotentialBreak = 0;
    	int lastBreak = 0;
    	Vector breaks = new Vector();
    	int longestWordWidth = 0;
    	int curWordWidth = 0;
    	int nextWordWidth = 0;
    	int maxWidth = 0;
    	   	
    	longestWordWidth = getLongestWordWidth();
    	maxWidth = Math.max(minWidth, (longestWordWidth + 2*hInset));
    	
//System.out.println("=== minWidth="+minWidth+" longestWordWidth="+longestWordWidth+
//" canGrowHorizontally="+canGrowHorizontally +" maxWidth="+maxWidth+" currentWidth="+currentWidth);
 
 
    	for (int i = 0; i<textLength; i++) {
    		char c = text[i];
    		switch (c) {
    		case ' ': 
    		  if(!canGrowHorizontally)  // when can't grow horizontally, will have to break words
    		     lastPotentialBreak = i;
    		  else {			
    			if (currentWidth > maxWidth) {
    				if(lastPotentialBreak == 0 && lastBreak != i) { // space after the first word on the line  
    					breaks.addElement(Integer.valueOf(String.valueOf(i)));
    					currentWidth = hInset;
    					lastBreak = i;
    					lastPotentialBreak = 0;
    				} else {
    						// current word in not the first on the line; 
    						// make a line break before the current word  
    				   if(lastPotentialBreak != lastBreak) {
    					  breaks.addElement(Integer.valueOf(String.valueOf(lastPotentialBreak)));
    					  currentWidth = hInset + curWordWidth;
    					  lastBreak = lastPotentialBreak;
    					  lastPotentialBreak = 0;
    					}
    				}
    			} else {
    				nextWordWidth = getNextWordWidth(i);   	
    				if(nextWordWidth >= minWidth && i != lastBreak) {     
    					breaks.addElement(Integer.valueOf(String.valueOf(i)));
    					currentWidth = hInset;
    					lastBreak = i;
    			 	} else
    				   currentWidth += fm.charWidth(text[i]);
    			}	   	
    			// reset values
    		    curWordWidth = 0;
    		    lastPotentialBreak = i;    		  	
    		  }	
    		default:
    		  if(canGrowHorizontally) {
    			currentWidth += fm.charWidth(text[i]);
    			curWordWidth += fm.charWidth(text[i]);
    		  } else {
    		      if (currentWidth + fm.charWidth(c) >= getSize().width - 2*hInset) {
    				if (lastPotentialBreak == 0) {
    					breaks.addElement(Integer.valueOf(String.valueOf(i)));
    					currentWidth = hInset;
    					lastBreak = i;
    					lastPotentialBreak = 0;
    				} else {
    					breaks.addElement(Integer.valueOf(String.valueOf(lastPotentialBreak)));
    					currentWidth = hInset + fm.charsWidth(text,lastPotentialBreak,
											  i-lastPotentialBreak);
    					lastBreak = lastPotentialBreak;
    					lastPotentialBreak = 0;
    				}
    			 } else {
    				currentWidth += fm.charWidth(text[i]);
    			}	  
    		  
    		  }
    		}
    	}
			// at the end check if the last line needs to be split
			// NOT in the typing mode!
		if(currentWidth > maxWidth && 			//!typingMode && currentWidth > maxWidth && 
		   lastPotentialBreak > 0 && lastPotentialBreak != lastBreak)  
    		  breaks.addElement(Integer.valueOf(String.valueOf(lastPotentialBreak)));
    				
		if (breaks.size() > 0) {			
			int s = breaks.size();
    		lineBreaks = new int[s];
    		Enumeration elems = breaks.elements();
    		for (int i=0; elems.hasMoreElements(); i++) {
    		  lineBreaks[i] = ((Integer)elems.nextElement()).intValue();
    		}
    	} else 
    		lineBreaks = null;
    	}
    }
	
	
	private int getNextWordWidth(int start){
	  int nextSpaceInd = -1;
	  FontMetrics fm = getFontMetrics(getFont());
	  
	  if(start > textLength-2)
	    return 0; 
	  for(int i=start+1; i<textLength; i++){ 
		 if(text[i] == ' ') {
		    nextSpaceInd = i;
		    break;
		  }	
	  }
	  if(nextSpaceInd != -1)
	    return fm.charsWidth(text, start+1, nextSpaceInd-start-1);
	  if(textLength > start+2) 
	  	return fm.charsWidth(text, start+1, textLength-start-1);
	  
	  return 0;
	}
	
	private int getLongestWordWidth(){
		int wordW = 0;
		int longestWordW = 0;
		FontMetrics fm = getFontMetrics(getFont());
		
		for(int i=0; i<textLength; i++){
		  if(text[i] == ' '){
		    longestWordW = Math.max(longestWordW,wordW);
		    wordW=0;
		  } else wordW += fm.charWidth(text[i]);
		}	
		return Math.max(longestWordW,wordW);
	}

	
    synchronized public void paint(Graphics g)  throws NullPointerException{ 
      	
        if(!m_visualBounds && m_boundingStyle != 0) {
        		//if a cell doesn't have visual bounds and has an empty box
        		// (or modified text box) then use current background color
        		// only inside that box; set cell background to defaultBG
        	fillBoundingBox(g);
        	setBackground(getDefaultBgColor());      
        }
		
    	if(m_visualBounds){
    		int width = getSize().width - 1;
    		int height = getSize().height - 1;
    		g.drawLine(0, height, width - 1, height); // Bottomleft to bottomright
    		g.drawLine(width, 0, width, height);      // Topright to bottomright
    		g.drawLine(0, 0, 0, height - 1); // Topleft to bottomleft
    		g.drawLine(0, 0, width - 1, 0);  // Topleft to topright
    	}
    	
    	if (!displayWebEq)
     	  hiliteSelection(g); 
     	
     	if(paintBLTR){
     		int width = getSize().width - 1;
    		int height = getSize().height - 1;
     		g.drawLine(1, height-1, width - 1, 1);//Bottomleft to topright
     	}
     	    		
		if (textLength > 0) {   		     
   		  touched=true; 
   		    
   		  if (displayWebEq) 
		    paintWebEqImage(g);	
		    	  	 
		   else {
   			  	FontMetrics fm = g.getFontMetrics();
    			int height = fm.getHeight();
    			int vPos   = calcVstart(fm.getAscent(), height);
    			LineEnumeration le = new LineEnumeration(this);
    	
    			while (le.hasMoreElements()) {
    				StartEndPair sep = (StartEndPair) le.nextElement();
    				paintText(g, sep.start, sep.end, vPos);
    				vPos += height;
    	  		}
    			if (modified) 
    				drawBoundingBox(g);
    	      }	
    	} else 
    		drawBoundingBox(g);
	}
	
	
	public void paintWebEqImage(Graphics g) { }
	
	protected Color getDefaultBgColor() {
		return Color.white;
    }
    
	protected Color getBackgroundColor(){
		return new Color(235,235,235);
	}
	
	void paintText(Graphics g, int start, int end, int vPos)  throws NullPointerException{
   		int w = g.getFontMetrics().charsWidth(text, start, end - start);
    	g.drawChars(text, start, end - start, calcHstart(w), vPos);
    }

    public void setFont(Font font) {
		super.setFont(font);
		int locMinHeight = getFontMetrics(getFont()).getHeight()*2-6;
		
		if(minHeight < locMinHeight ||
			(minHeight > locMinHeight && locMinHeight >=20)){
			minHeight = locMinHeight;
		}
		isResized = false;

		if(offScreenGraphics != null){
			offScreenGraphics.setFont(font);

			if(displayWebEq) {
			  if( displayImage != null)
			  	displayImage.delete();
			  displayImage = null;
			  if(getText() != "") 
			  	displayImage = CellWebEqHelper.createWebEqImage(getText(),font,getForeground(),this);
			}
			paintAll(offScreenGraphics);
						
					//don't recalculate line breaks when changing font size
			  		//setText(getText());
			updateSize();	
			
			if(getParent() != null)	
				getParent().validate();
				
			repaint();
		}
	}	
	 
	protected int calcVstart(int ascent, int height) {
   		switch (vAlignment) {
    	case ALIGN_TOP:
    		return ascent+vInset;
    		
    	case ALIGN_CENTER:
    		if (lineBreaks == null) {
    			return ((getSize().height - height)/2 + ascent);
    		} else {
    			return (((getSize().height - (lineBreaks.length + 1) * height)/2) + ascent);
    		}
    	case ALIGN_BOTTOM:
    	    if (lineBreaks == null) {
    			return ((getSize().height - height) + ascent);
    		} else {
    			return ((getSize().height - (lineBreaks.length + 1) * height) + ascent-vInset);
    		}
    	default:
    		return ascent;
    	}
    }

	protected int calcHstart(int width) {
		int hStart = 0;
   		switch (hAlignment) {
    	case ALIGN_LEFT:
    		//hStart = hInset;
    		hStart = hInset+2;
    		break;
    	case ALIGN_MIDDLE:
    		hStart = (getSize().width - width)/2;
    		break;
    	case ALIGN_RIGHT:
    	    hStart = (getSize().width - hInset - width-1);
    	    //hStart = (getSize().width - 1 - width);
    		break;
    	default:
    		hStart = hInset;
    		break;
    	}
    	return hStart;
    }
    
    Point index2leftEdge (int index) {
    	if (textLength > index) {
    		FontMetrics fm = getFontMetrics(getFont());
    		int height = fm.getHeight();
    		int vPos   = calcVstart(fm.getAscent(), height);
    		LineEnumeration le = new LineEnumeration(this);
    		
    		while (le.hasMoreElements()) {
    			StartEndPair sep = (StartEndPair) le.nextElement();
    			if (index < sep.end) {
    			//if (index <= sep.end) {
    				index = Math.max(index, sep.start);
    				int h = calcHstart(fm.charsWidth(text,sep.start,sep.end-sep.start));
    				h += fm.charsWidth(text,sep.start,index-sep.start);
    				return (new Point(h, vPos));
    			} else {
    				vPos += height;
    			}
    		}
    		return null;
		}
		return null;
	}
	
    Point index2rightEdge (int index) {
    	Point pos = index2leftEdge(index);
    	FontMetrics fm = getFontMetrics(getFont());
		try{
    		if (pos != null) 
				pos.x += fm.charWidth(text[index]);
		}catch (ArrayIndexOutOfBoundsException e) {
			//System.out.println("AltTextField  index2rightEdge "+e.toString());
		}
		return pos;
	}
	
	int point2Index (int x, int y) {
    	FontMetrics fm = getFontMetrics(getFont());
    	int height  = fm.getHeight();
    	int ascent  = fm.getAscent();
    	int descent = fm.getDescent();
    	
    	int top = calcVstart(ascent, height) - ascent;
    	int bottom = top + nLines() * height;
    	if (y < top) {
    		//yCaret = top;
    		return 0;
    	} else if (y >= bottom) {
    		//yCaret = bottom;
    		return textLength;
    	} else {
    		//yCaret = (y - top);
    		return point2IndexAux (fm, x, (y - top)/height);
    	}
    }
    
    int point2IndexAux (FontMetrics fm, int x, int lineNo) {
    	int start = nthStart(lineNo);
    	int end = nthEnd(lineNo);
    	
    	int width = fm.charsWidth(text, start, end - start);
    	int startx = calcHstart(width);
    	
    	if (x < startx) {
    		return start;
    	} else if (x > startx + width) {
    		return end;
    	} else {
			while (start < end) {
    			int cWidth = fm.charWidth(text[start]);
    			if (startx + (cWidth/2) > x) {
    				return start;
    			}
    			start++;
    			startx += cWidth;
    		}
    		return end;
    	}
    }

	protected void hiliteSelection(Graphics g) {
		if (!hasFocus) 
			return;

		if (selStart == selEnd) {
			drawCaret(g);
			return;
		}
		
		Color oldColor = g.getColor();
		g.setColor(hiliting);
		//trace.out (10, this, "Setting highlight color");
		LineEnumeration le = new LineEnumeration(this);
		while (le.hasMoreElements()) {
			StartEndPair sep = (StartEndPair) le.nextElement();
			int start = Math.max(selStart, sep.start);
			int end   = Math.min(selEnd,   sep.end);
			if (start < end) {
				boxit(g, start, end);
			}
		}
		g.setColor(oldColor);
	} 
	
	 public void setBoundingStyle(int boundingStyle) {
	 	m_boundingStyle=boundingStyle;
	 }	
	 
	 public void setGrow(int growStyle) {
	 	m_grow=growStyle;
	 	if(m_grow == HORIZONTAL_GROW)
	 		m_horBoundary = false;
	 	else
	 		m_horBoundary = true;
	 	if(m_grow == NO_GROW)
	 		m_vertBoundary = true;
	 }
	 
	 public int getGrow(){
	 	return m_grow;
	 }
	 
	 public void setCanBeSelected(boolean b){
	 	canBeSelected = b;
	 }
	 
	 
	 public void setDisplayWebEq(boolean d) {
	 	changes.firePropertyChange("displayWebEq", Boolean.valueOf(String.valueOf(displayWebEq)), 
	 								Boolean.valueOf(String.valueOf(d)));
	 	// lock the cell	
	 	if(d && isEditable()){
	 		try{
	 			setOwnProperty("isEditable", Boolean.valueOf("false"));
	 		} catch (NoSuchFieldException e) { }
	 	}
	 	
	 	displayWebEq =d;
	 	  // when turning ON - create a new image from the current text;
	 	  // when turning OFF - reset image
	 	if(displayWebEq) {
	 		String val = getText();	
	 	  	if(val!=null && !val.equals("")) {  // if there is value in the cell	 	  	
	 	  		replaceSelection("");
			if(displayImage != null)
	 				displayImage.delete(); 
	 			displayImage = null;
    			displayImage = CellWebEqHelper.createWebEqImage(val,getFont(),getForeground(),this);
   			}
	 	} else {
	 		if(displayImage != null)
	 			displayImage.delete();
	 		displayImage = null;
	 	}		
	 }
	 
	 public boolean getDisplayWebEq(){
	 	return displayWebEq;
	 }
	 
	 public void setHasBounds(boolean b){
	 	changes.firePropertyChange("hasBounds", Boolean.valueOf(String.valueOf(m_visualBounds)), 
	 								Boolean.valueOf(String.valueOf(b)));
	 	m_visualBounds = b;
	 	repaint();
	 }	
	 
	 public boolean getHasBounds(){
	 	return m_visualBounds;
	 }
	 
	
	 Rectangle getEmptyBox() {
     	FontMetrics fm = getFontMetrics(getFont());

    	int height 	= fm.getHeight();
    	int vPos   	= calcVstart(fm.getAscent(), height);
    	int hPos	= calcHstart(fm.charWidth('A'));
      
    	Rectangle rect=new Rectangle(hPos,vPos-height,fm.charWidth('W'),height);
    	rect.translate(0,2);
    	return rect;
    }
	
	public Rectangle getMaxBox() {
		Rectangle MaxBounds=bounds();
		MaxBounds.setLocation(0,0);
		MaxBounds.width--;
		MaxBounds.height--;
		return MaxBounds;
	}	
		

	Rectangle getImageBox() {
		Image im = displayImage.getEqImage();  
		//int w = displayImage.getWidth(this);
		//int h = displayImage.getHeight(this);
		int w = im.getWidth(this);
		int h = im.getHeight(this);
		Rectangle rect = new Rectangle(0,0, w, h);
		return rect;
	}	

	
	public synchronized  Rectangle getBoundingBox() throws NullPointerException{
	
		boolean start=true;
		LineEnumeration le=new LineEnumeration(this);
		Rectangle BoundingBox=null;
		if (textLength>0) {
		
				// if this cell contains a WebEq image
			if (displayWebEq && displayImage != null) {
			  BoundingBox= getImageBox();
			} 
			else {
			    while (le.hasMoreElements()) {
					StartEndPair sep = (StartEndPair) le.nextElement();
	    			if (start) {
	    		    	start=false;	    		    
	    				BoundingBox=getBox(sep.start, sep.end);
	 		   		} else {
	 					if(sep.start != sep.end) 
	    				BoundingBox=GraphicsElement.unionRect(BoundingBox,getBox(sep.start, sep.end));
	  				}	 		
	   			 } // end while
	   		}
	   	} else  // textLength == 0
	   		BoundingBox=getEmptyBox();
		 
		if(BoundingBox != null) {
			if(displayWebEq && displayImage != null) 
				BoundingBox.grow(2,2);
			else
	    		if(m_grow == HORIZONTAL_GROW){
	    			if( BoundingBox.width < minWidth)
	    				BoundingBox.width = minWidth;
	    				BoundingBox.grow(0,2);
	    		}
	    		else
	    			BoundingBox.grow(2,2);
	    	return BoundingBox;
	    } else {
	    	throw new NullPointerException();
	    }
	}
	    
    void fillBoundingBox(Graphics g){
    	Color oldColor = g.getColor();
        
        try{
        	g.setColor(getBackgroundColor());  
        	Rectangle box=getBoundingBox();       			
        	box=GraphicsElement.intersectRect(box,getMaxBox());
        	g.fillRect(box.x,box.y,box.width,box.height);
        	g.setColor(oldColor); 
        } catch (NullPointerException e) { }       			        			
    }
   
    
    protected void drawBoundingBox(Graphics g)
    {
    	//trace.out (10, this, "drawBoundingBox");
     	if (((textLength>0) && ((m_boundingStyle & BOUND_MODIFIED_TEXT)!=0) && modified)||
   			((textLength==0) && ((m_boundingStyle & BOUND_EMPTY_TEXT)!=0))||
   			((textLength==0) && ((m_boundingStyle & BOUND_MODIFIED_TEXT)!=0) && touched)) {
   			
   				try{
        			Rectangle box=getBoundingBox();       			
        			box=GraphicsElement.intersectRect(box,getMaxBox());
        			g.drawRect(box.x,box.y,box.width,box.height);
        		} catch (NullPointerException e) {
        			//System.out.println("AltTextField  drawBoundingBox "+e.toString()); 
        		}
        }	
    }
           
    
    synchronized void drawCaret(Graphics g) {
    	if (showCaret) {
    		//trace.out (10, this, "show caret");
    		FontMetrics fm = g.getFontMetrics();
    		int ascent  = fm.getAscent();
    		int descent = fm.getDescent();
    		int height  = fm.getHeight();
    		Point p;
    		
    		if (textLength == 0) {
    			p = new Point(calcHstart(0), calcVstart(ascent, height));
    		} else if (selStart == textLength) {
    			p = index2rightEdge(selStart-1);
    		} else {
    			p = index2leftEdge(selStart);
    		}
 	   		xCaret = p.x;
    		yCaret = p.y;	
 			g.drawLine(p.x, p.y - ascent, p.x, p.y + descent);
 		}
    }
    
    public void processFocusEvent(FocusEvent evt){
    super.processFocusEvent(evt);  // Needed for FocusListeners to work.
    	if(evt.getID() == FocusEvent.FOCUS_GAINED)
    		focusGained(evt);
    	else if(evt.getID() == FocusEvent.FOCUS_LOST)
    		focusLost(evt);
   } 	
       
   	public void focusGained(FocusEvent e) {
   		if(isEditable()){
   			CaretThread.takeCaretThread(this);
   			showCaret = true;
   		}
    	hasFocus = true;
    	justSelected=true;
   		selectAll();
    	repaint();
   	}
   
   	public void focusLost(FocusEvent e) {
   		if (hasFocus && listener != null)
   			listener.enterPressed();
   			
   		CaretThread.releaseCaretThread();
    	hasFocus = false;
    	showCaret = false;
    	modified = false;
    	editMode = false;
    	justSelected = false;
    	selStart = 0;
    	selEnd = 0;
    	setBackground(Color.white);
    	repaint();
    }
    
    public boolean getJustSelected(){
    	return justSelected;
    }
    
    public void requestFocus(){
    	//trace.out (10, this, "requesting focus");
    	super.requestFocus();
    	hasFocus = true;
    }
   
    public Rectangle getBox(int start, int end){
    	FontMetrics fm = getFontMetrics(getFont());
    	int ascent = fm.getAscent();
    	int height = fm.getHeight();
    	try{
 			Point s = index2leftEdge(start);
    		Point e = index2rightEdge(end-1);
    		if(m_grow == HORIZONTAL_GROW)
    			//return new Rectangle(s.x-2, s.y - ascent, e.x - s.x+hInset, height);
    			return new Rectangle(s.x-hInset, s.y - ascent, e.x - s.x+ 2*hInset, height);
    		    		
    		Rectangle rec = new Rectangle(s.x, s.y - ascent, e.x - s.x+hInset, height); 
    		return rec;
    		
   		} catch (NullPointerException e){
   			//System.out.println("AltTextField  getBox "+e.toString());
   		}
    	return null;
   }
    
    
    void boxit (Graphics g, int start, int end) {
    	Rectangle box=getBox(start,end);
    	g.fillRect(box.x,box.y,box.width,box.height);
    }
    
    // editing
    
    protected synchronized void deleteSelection () {
    	if (selStart < selEnd) {
    		System.arraycopy(text, selEnd, text, selStart, textLength - selEnd);
    		textLength -= selEnd - selStart;
    		selEnd = selStart;
    		calculateLineBreaks(m_horBoundary,m_vertBoundary);
    		repaint();
    	} else if (selStart != 0) {
    		System.arraycopy(text, selEnd, text, selEnd-1, textLength - selEnd);
    		textLength--;
    		selEnd--;
    		selStart--;
    		calculateLineBreaks(m_horBoundary,m_vertBoundary);
    		repaint();
    	}
   		// it should be a better way to resize a cell in case of deleting everything
    	if((getText().trim()).equals("")){
    	/*
    		Graphics g = getGraphics();
    		FontMetrics fm = g.getFontMetrics();
    		int height = fm.getHeight();
    		int vPos   = calcVstart(fm.getAscent(), height);
    		LineEnumeration le = new LineEnumeration(this);
    		StartEndPair sep = (StartEndPair) le.nextElement();
    		paintText(g, sep.start, sep.end, vPos);
    		*/
    		replaceSelection("");
    	
    	}
    	if(traceUserAction){
    		userAction[0] = getText();
    		userAction[1] = Integer.valueOf(String.valueOf(selStart));
    		changes.firePropertyChange("TracedUserAction", null, userAction);
    	}
    }
    
    synchronized void deleteSelectionForward () {
    	if (selStart != selEnd) 
    		return;
    	else if (selStart == selEnd) {
    		if(selStart == textLength){
    			(Toolkit.getDefaultToolkit()).beep();
    			return;
    		}
    		System.arraycopy(text, selEnd+1, text, selEnd, textLength - selEnd);
    		textLength--;
    		calculateLineBreaks(m_horBoundary,m_vertBoundary);
    		repaint();
    	}
    	if(traceUserAction){
    		userAction[0] = getText();
    		userAction[1] = Integer.valueOf(String.valueOf(selStart));
    		changes.firePropertyChange("TracedUserAction", null, userAction);
    	}
    }
    
    synchronized void replaceSelection (String s) { 
    	edu.cmu.pact.Utilities.trace.out (10, this, "replace selction.  String s = " + s);
    	int l = s.length();
    	makeSpace(l); 
    		//copy s into text
    	System.arraycopy(s.toCharArray(), 0, text, selStart, l);  
    	calculateLineBreaks(m_horBoundary,m_vertBoundary);
    	selStart += l;
    	selEnd = selStart;
  		updateSize();
   		if(traceUserAction && wasUserAction){
   			wasUserAction = false;
 			userAction[0] = getText();
    		userAction[1] = Integer.valueOf(String.valueOf(selStart));
    		changes.firePropertyChange("TracedUserAction", null, userAction);
   		}
    }
    
    public synchronized void replaceSelection (char c) {
    	edu.cmu.pact.Utilities.trace.out (10, this, "replace selction.  char c = " + c);
 
    	makeSpace(1);
    	text[selStart] = c;
     						// typing mode
   
    	calculateLineBreaks(m_horBoundary,m_vertBoundary,true);
   		if(textLengthLimit == -1 || textLength < textLengthLimit) 
   			selStart++;
   		else if(selStart == textLength)
   			(Toolkit.getDefaultToolkit()).beep();
   		else
   			selStart = textLength;
    	selEnd = selStart;
    	   
    	updateSize();
    	
 		if(traceUserAction && wasUserAction){
 			wasUserAction = false; 
 			userAction[0] = getText();
    		userAction[1] = Integer.valueOf(String.valueOf(selStart));
    		changes.firePropertyChange("TracedUserAction", null, userAction);
   		}   
   
	}
	
	    
    public void setTraceUserAction(boolean b){
    	traceUserAction = b;
    }
    
    public void updateSize(){ }
    

	synchronized void insert(String s){
		wasUserAction = true;
		replaceSelection(s);
	}
    
    void makeSpace (int size) {
     
    	char dest[] = text;
    	int delta = size - (selEnd - selStart);
   		if(textLengthLimit != -1 && textLength+delta > textLengthLimit-1){
   			delta = textLengthLimit-textLength;
   			size = delta + (selEnd - selStart);
   		}
   		
    	if (textLength + delta > text.length) {
    		dest = new char[textLength + delta + 20];
    		System.arraycopy(text, 0, dest, 0, selStart);
    	}
   		if(size != 0)
    		System.arraycopy(text, selEnd, dest, selStart + size, textLength - selEnd);
    	text = dest;
    	
    	textLength += delta;
    }
    
    // old method
    /*
    void makeSpace (int size) {
    	char dest[] = text;
    	int delta = size - (selEnd - selStart);
  
    	if (textLength + delta > text.length) {
    		dest = new char[textLength + delta + 20];
    		System.arraycopy(text, 0, dest, 0, selStart);
    	}
    	System.arraycopy(text, selEnd, dest, selStart + size, textLength - selEnd);
    	text = dest;
    	textLength += delta;
    }
    */
    
    public void processKeyEvent(KeyEvent evt) {
    	try{
    		if(evt.getID() == KeyEvent.KEY_PRESSED)
    			keyPressed(evt);
    		else
    			super.processKeyEvent(evt);
    	} catch (NullPointerException e) { }
    }
    
    public void keyPressed(KeyEvent evt) {	
    	int key = evt.getKeyCode();

		//trace.out (5, this, "key event = " + evt);
		// Enter or Tab, respectively
		if (listener != null && (key == 10 || key == 9))
			listener.enterPressed();
			
	   	if (evt.isActionKey()) {
    		switch (key) {
    		case KeyEvent.VK_LEFT:
    			leftArrow(evt.getModifiers());
    			break;
    		case KeyEvent.VK_RIGHT:
    			rightArrow(evt.getModifiers());
    			break;
    		case KeyEvent.VK_DOWN:
    			downArrow();
    			break;
    	
    		case KeyEvent.VK_UP:
    			upArrow();
    			break;
    	
    		}
    	} else {
    		if((evt.isMetaDown() || evt.isControlDown()) ){
    			if(hasClipboardEvents){
    				switch (key){
    					case KeyEvent.VK_C:
    						copy();
    						break;
    					case KeyEvent.VK_V:
    						paste();
    						break;
    					case KeyEvent.VK_X:
    						copy();
    						cut();
    						break;
    				}
    			}
 			}    		
    		else {
    			modified=true;
    			editMode = true;
    			switch (key) { 
    			case 8:
    				deleteSelection();
    				break;
    			
    			case KeyEvent.VK_DELETE:
    				deleteSelectionForward();
    				break;
    			case 10:
    			case 13:
    				modified=false;
    				wasUserAction = true;
    				replaceSelection ((char) 13);
    				break;
    			// workaround jView 5.00.3240
    			case 16: 	//shift
		   			edu.cmu.pact.Utilities.trace.out (10, this, "shift pressed");
					wasUserAction = true;
					break;
    			case 18: 	// alt (option on MAC)
    			case 27:	// escape
    			//case 92:	// window
    			//case 93:	// main menu
    			default:
//    				DORMIN.trace.out (5, this, "key code = " + key + " key char = " + evt.getKeyChar());
    				// return if it's window/main menu key on Windows
    				if(((key == 92 && !(evt.isShiftDown())) || key == 93) && 
    					!(System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
    					break;
    				if (getFontMetrics(getFont()).charWidth(evt.getKeyChar()) > 0) {
    					char keyChar = evt.getKeyChar();
    	
    					if(numeric){
		    				//DORMIN.trace.out (10, this, "numeric");
    						switch (keyChar)  {
								case '0':
								case '1':
								case '2':
								case '3':
								case '4':
								case '5':
								case '6':
								case '7':
								case '8':
								case '9':
								case '.':
								case ',':
								case '-':
								case '+':
								case '/':
								case '*':
								case ' ':
								case '|':
									wasUserAction = true;
									replaceSelection (keyChar);
									break;
								default: 
									(Toolkit.getDefaultToolkit()).beep();
									break;
							}
						
    					}
    					else {
		    				//DORMIN.trace.out (10, this, "replace selection");
    						wasUserAction = true;
    						replaceSelection (keyChar);
    					}
    				}
    				break;
    			} 
    		}
    	}
    //changes.firePropertyChange("typingValue", "", text); // For those who need constant updates on what is in here.
    }
    
    public void cut(){
    	if(editable){
    		
  			String cutStr = getText().substring(selStart,selEnd);
  			toClipboard(cutStr);
  			wasUserAction = true;
    		//replaceSelection("");
    		String newStr = getText().substring(0,selStart)+getText().substring(selEnd);
    		select(0, textLength);
    		replaceSelection(newStr);
    	} else
    		(Toolkit.getDefaultToolkit()).beep();
    }
 
    public void copy()  {
    	toClipboard(getSelectedText());
  	}
  	
  	private void toClipboard(String clip){
  		StringSelection data = new StringSelection(clip);
    	Clipboard clipboard = (Toolkit.getDefaultToolkit()).getSystemClipboard();
    	clipboard.setContents(data, data);
    }
  	
  	public void askForHint(){ }
  	
  	public void paste()  {
  		if(editable){
    		Clipboard clipboard = (Toolkit.getDefaultToolkit()).getSystemClipboard();
    		Transferable data = clipboard.getContents(this);
    		if(data == null) {
    			(Toolkit.getDefaultToolkit()).beep();
    			return;
    		}
    		String s;
    		try {
      			s = (String) (data.getTransferData(DataFlavor.stringFlavor));
    		} 
    		catch (Exception e) {
      			s = data.toString();
    		}
    		modified=true;
    		insert(s);
    	}
    	else
    		(Toolkit.getDefaultToolkit()).beep();
  	}
    
    void leftArrow(int modifiers) {
    	if (selStart == selEnd) {
    		selStart = Math.max(selStart-1,0);
    		selEnd = selStart;
    	} else {
    		selStart = selEnd;
    	}
    	repaint();
    }
    
    void rightArrow(int modifiers) {
    	if (selStart == selEnd) {
    		selStart = Math.min(selStart+1,textLength);
    		selEnd = selStart;
    	} else {
    		selStart = selEnd;
    	}
    	repaint();
    }
    /*
    public void repaint(){
		super.repaint();
	}
    */
    void downArrow(){
    	if (selStart == selEnd) {
    		FontMetrics fm = getFontMetrics(getFont());
    		int height = fm.getHeight();
    		selStart = point2Index(xCaret, yCaret+height);
    		selEnd = selStart;
    	} else {
    		selStart = selEnd;
    	}
    	repaint();
    }
    
    void upArrow(){
    	if (selStart == selEnd) {
    		FontMetrics fm = getFontMetrics(getFont());
    		int height = fm.getHeight();
    		selStart = point2Index(xCaret, yCaret-height);
    		selEnd = selStart;
    	} else {
    		selStart = selEnd;
    	}
    	repaint();
    }
    
	public int nLines() {
		return ((lineBreaks == null) ? 1 : lineBreaks.length + 1);
	}
    
    public int nthStart (int lineNo) {
		if (lineNo >= nLines() || lineNo < 0) {
			return -1;
		} else if (lineNo == 0) {
			return trim(0);
		} else {
			return trim(lineBreaks[lineNo - 1]);
		}
	}
	
    public int nthEnd (int lineNo) {
		if (lineNo >= nLines() || lineNo < 0) {
			return -1;
		} else if (lineNo == nLines() - 1) {
			return textLength;
		} else {
			return (lineBreaks[lineNo]);
		}
	}
	
	int trim (int index) {
		while(true) {
			if (index == textLength) {
				return index;
			} else {
				switch (text[index]) {
				case ' ':
				case '\n':
				case '\r':
					index++;
					break;
				default:
					return index;
				}
			}
		}
   	}
   	
   	private Image offScreenImage;
	private Dimension offScreenSize;
	private Graphics offScreenGraphics;
	
	public synchronized void update (Graphics g) {
		Dimension d = getSize();
		//trace.out (10, this, "size = " + d);
      	try{	
    		if ((offScreenImage == null) ||
    			(d.width != offScreenSize.width) ||
    			(d.height != offScreenSize.height)) 
    		{
    			    offScreenImage = createImage(d.width, d.height);
					offScreenSize = d;
					offScreenGraphics = offScreenImage.getGraphics();
					offScreenGraphics.setFont(g.getFont());
			}		
			offScreenGraphics.setColor(getBackground());
			offScreenGraphics.fillRect(0, 0, d.width, d.height);
			offScreenGraphics.setColor(getForeground());
			
			offScreenGraphics.setClip(0, 0, d.width, d.height);
	
			paintAll(offScreenGraphics);
			g.drawImage(offScreenImage, 0, 0, null);
	 	
	  	} 
	 	catch (NullPointerException e) { }
		catch (IllegalArgumentException e) {
	  		trace.out (10, this, "exception = " + e + " size = " + d);
		}
	}
}
//
//final class StartEndPair {
//	public int lineNumber;
//	public int start;
//	public int end;
//	
//	public StartEndPair(int s, int e, int l) {
//		start = s;
//		end = e;
//		lineNumber = l;
//   	}
//   	
//   	public boolean inside (int i) {
//   		return ((start <= i) && (i<= end));
//   	}
//   	public boolean precedes (int i) {
//   		return ((start > i));
//   	}
//   	public boolean follows (int i) {
//   		return ((i > end));
//   	}
//}
//			
//
//final class LineEnumeration implements Enumeration {
//	int lineNo;
//	AltTextField atf;
//	
//	public LineEnumeration(AltTextField inAtf) {
//		lineNo = 0;
//		atf = inAtf;
//	}
//	
//	public boolean hasMoreElements() {
//		return (lineNo < atf.nLines());
//	}
//	
//	public Object nextElement () {
//		return (new StartEndPair (atf.nthStart(lineNo), atf.nthEnd(lineNo), lineNo++));
//	}
//}
////
////final class CaretThread implements Runnable {
////	static Thread theThread;
////	static AltTextField atf;
////	
////	static public void initCaretThread() {
////		if (theThread == null) {
////			theThread = new Thread(new CaretThread());
////			theThread.setDaemon(true);
////			theThread.start();
////		}
////	}
////	
//	static public void takeCaretThread(AltTextField in) {
//		synchronized (theThread) {
//			atf = in;
//			theThread.resume();
//			theThread.interrupt();
//		}
//	}
//	
//	static public void releaseCaretThread() {
//		synchronized (theThread) {
//			if (atf != null) {
//				atf.showCaret = false;
//				atf.repaint();
//			}
//			atf = null;
//			//theThread.suspend();  // keeping this here sometimes causes a bug on the line "synchronize(theThread) where all of Java dies waiting for it.
//		}
//	}
//	
//	
//	public void run() {
//		while (true) {
//			try {
//				theThread.sleep(500);
//			}
//			catch (InterruptedException e) {
//				//System.out.println("AltTextField  run "+e.toString());
//			}
//			synchronized (theThread) {
//				if (atf != null) {
//					atf.showCaret = !atf.showCaret;
//					atf.repaint();
//				}
//			}
//		}
//	}
//}
		
