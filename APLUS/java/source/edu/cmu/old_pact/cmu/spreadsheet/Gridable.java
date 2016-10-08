package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.UserMessage;

public interface Gridable{
	
    
    public void setWidth(int w);
    public void setHeight(int h);
    public void setMinWidth(int w);
    public void setMinHeight(int h);
    public void setValue(Object v);
    public void setName(String n);
    public void setLocation(int x, int y);
    public void setEditable(boolean b);
    public void setFont(Font f);
    public void setBugMessage(UserMessage[] mess);
    public void setColor(String whereColor, String colorStr);
    public void setHasBounds(boolean b);
    public void setCalculate(boolean c);
    public void setSelected(boolean s);
    public void setCanBeSelected(boolean c);
    public void setHighlighted(boolean h);
    public void setTraceUserAction(boolean b);
    public void setInternalSelected(boolean s);
    public void setNumeric(boolean n);
    public void setOwnProperty(String proName, Object pro_value)  throws NoSuchFieldException;
    public Hashtable getOwnProperty(Vector proNames) throws NoSuchFieldException;
    
    public void showMessage(UserMessage[] userMessages, String imageBase,String title, int startFrom);
    
    // current dimension
    public int getHeight();
    public int getWidth();
    // dimension during initialization
    public int getMinHeight();
    public int getMinWidth();
    // real minimal dimension
    public int getMinimumHeight();
    public int getMinimumWidth();
    
    public String getName();
    
    public boolean isEditable();
    public boolean hasFocus();
    public Font getFont();
    
    
    public void requestFocus();
    public void writeToCell();
    public void setVisible(boolean b);
    
    public void clear();
    
    public void cut();
    public void copy();
    public void paste();
    public void askForHint();
    public void setHasClipboardEvents(boolean b);
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
    public void propertyChange(PropertyChangeEvent evt);
    public void addVetoableChangeListener(VetoableChangeListener l);
    public void removeVetoableChangeListener(VetoableChangeListener l);
} 
    
    