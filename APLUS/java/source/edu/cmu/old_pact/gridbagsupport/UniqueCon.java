package edu.cmu.old_pact.gridbagsupport;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

public class UniqueCon {

	 public static void viewset(Container cont,
	 					   		Component obj,
                           		int vgridx,
                           		int vgridy,
                           		int vgridwidth,
                           		int vgridheight,
                           		int vtop,
                           		int vleft,
                           		int vbottom,
                           		int vright
                           		)  { 
     
     	GridBagConstraints c = new GridBagConstraints();
        c.gridx = vgridx;
        c.gridy = vgridy;
        c.gridwidth = vgridwidth;
        c.gridheight = vgridheight;
        c.insets.top = vtop;
        c.insets.left = vleft;
        c.insets.bottom = vbottom;
        c.insets.right = vright;
        c.fill = 1;

        ((UniqueLayout)cont.getLayout()).setConstraints(obj, c);
        cont.add(obj);
    }
    
    public static void viewset(Container cont,
	 					   		Component obj,
                           		int vgridx,
                           		int vgridy,
                           		int vgridwidth,
                           		int vgridheight,
                           		int vtop,
                           		int vleft,
                           		int vbottom,
                           		int vright,
                           		int fill
                           		)  { 
     
     	GridBagConstraints c = new GridBagConstraints();
        c.gridx = vgridx;
        c.gridy = vgridy;
        c.gridwidth = vgridwidth;
        c.gridheight = vgridheight;
        c.insets.top = vtop;
        c.insets.left = vleft;
        c.insets.bottom = vbottom;
        c.insets.right = vright;
        c.fill = fill;
		//c.anchor = anchor; 
		
        ((UniqueLayout)cont.getLayout()).setConstraints(obj, c);
        cont.add(obj);
    }
    
    public static void viewset(Container cont,
	 					   		Component obj,
                           		int vgridx,
                           		int vgridy,
                           		int vgridwidth,
                           		int vgridheight,
                           		int vtop,
                           		int vleft,
                           		int vbottom,
                           		int vright,
                           		double vweightx,
                           		double vweighty
                           		)  { 
     
     	GridBagConstraints c = new GridBagConstraints();
        c.gridx = vgridx;
        c.gridy = vgridy;
        c.gridwidth = vgridwidth;
        c.gridheight = vgridheight;
        c.insets.top = vtop;
        c.insets.left = vleft;
        c.insets.bottom = vbottom;
        c.insets.right = vright;
        c.fill = 0;
        c.weightx = vweightx;
        c.weighty = vweighty;

        ((UniqueLayout)cont.getLayout()).setConstraints(obj, c);
        cont.add(obj);
    }
    
}
