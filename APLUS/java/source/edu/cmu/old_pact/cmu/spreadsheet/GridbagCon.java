package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class GridbagCon {

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

        ((GridBagLayout)cont.getLayout()).setConstraints(obj, c);
        cont.add(obj);
    }
}