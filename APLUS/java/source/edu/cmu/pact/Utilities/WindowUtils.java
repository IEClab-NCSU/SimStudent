package edu.cmu.pact.Utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class WindowUtils {


    /**
     * Left- or right-justify or center this component in a JPanel.
     * @param component
     * @param right right-justify if true
     * @return JPanel with this component
     */
    public static JPanel justify(JComponent component, boolean right) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(component, (right ? BorderLayout.EAST : BorderLayout.WEST));
        return p;
    }

    private final static int CENTER = 0;
    private final static int RIGHT = 1;
    private final static int LEFT = 2;

    public static JPanel wrap (List components, int pad, int location) {
        JPanel p = new JPanel();
        Box b = new Box (BoxLayout.X_AXIS);
        if (location == RIGHT)
        	b.add(Box.createHorizontalStrut(pad));
        
        Iterator componentsIterator = components.iterator();
        while (componentsIterator.hasNext()) {
        	Object o = componentsIterator.next();
        	Component c;
        	if (o instanceof String)
        		c = new JLabel ((String) o);
        	else
        		c = (Component) o;
        	b.add(c);
        	b.add(Box.createHorizontalStrut(3));
        }
        if (location == RIGHT) {
	        p.setLayout(new BorderLayout());
        	p.add(b, BorderLayout.EAST);
        } else if (location == CENTER) {
	        p.setLayout(new FlowLayout());
        	p.add(b);
        } else if (location == LEFT) {
            p.setLayout(new BorderLayout());
            p.add(b, BorderLayout.WEST);
            
        }
		
        return p;

    }
    
    public static Component wrapCenter (List l) {
    	return wrap (l, 0, CENTER);
    }
    public static Component wrapCenter (JComponent c) {
    	ArrayList l = new ArrayList();
    	l.add(c);
    	return wrapCenter (l);
    }

    /**
     * @param instructions
     * @return
     */
    public static JPanel wrapRight(JComponent component) {
    
        return wrapRight (component, 0);
    }
    
    /**
     * @param instructions
     * @return
     */
    public static Component wrapRight(String label) {
        return wrapRight (new JLabel (label), 0);
    }

    /**
     * @param instructions
     * @return
     */
    public static Component wrapRight(String label, int padRight) {
        return wrapRight (new JLabel (label), padRight);
    }

    /**
     * @param instructions
     * @return
     */
    public static JPanel wrapRight(JComponent component, int padRight) {
    	ArrayList l = new ArrayList();
    	l.add(component);
    	return wrapRight (l, padRight);
    }

    /**
     * @param instructions
     * @return
     */
    public static Component wrapRight(List components) {
    	return wrapRight (components, 0);
    }
   	/**
     */
    public static JPanel wrapRight(List components, int padRight) {
    	return wrap(components, padRight, RIGHT);
    }

    public static JPanel wrapLeft(JComponent component) {
        List list = new ArrayList();
        list.add(component);
        return wrapLeft (list);
    }

    public static void setNativeLookAndFeel() {
    	// Get the native look and feel class name
    	String nativeLF = UIManager.getSystemLookAndFeelClassName();
    
    	// Install the native look and feel
    	try {
    		UIManager.setLookAndFeel(nativeLF);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public static JPanel wrapLeft(List components) {
        JPanel p = wrap(components, 0, LEFT);    
        p.setAlignmentX(0f);
        return p;
    }

}
