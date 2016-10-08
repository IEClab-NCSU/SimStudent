//d:/Pact-CVS-Tree/Tutor_Java/./src/Utilities/Java/DorminMenu/MenuItemProxy.java
package edu.cmu.old_pact.dormin.menu;

import java.awt.Menu;
import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;

public class MenuItemProxy extends ToolProxy{
	
	public MenuItemProxy(ObjectProxy parent){
		super(parent, "MenuItem");
	}
	
	public void create(MessageObject mo) throws DorminException{
		DorminMenuItem dmi = null;
		try{
			String childType = mo.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("MENUITEM")) {
				
				DorminMenu menu = (DorminMenu)getRealParent();
				dmi = menu.createMenuItem();
				dmi.setFrame(menu.getFrame());
				dmi.setMenu(menu);
                                // must get menu action command for hierarchical menus
				Vector propertyNames;
                                Vector propertyValues;
                                propertyNames = mo.extractListValue("PROPERTYNAMES");
                                propertyValues = mo.extractListValue("PROPERTYVALUES");
                                int size = propertyNames.size();
                                int i=0;
                                String curr_name;
                                int found = -1;
                                while (i<size)
                                {
                                    curr_name = (String)propertyNames.elementAt(i);
                                    if(curr_name.equalsIgnoreCase("NAME")) {
                                        found = i;
                                        i = size;
                                    }
                                    else 
                                        i++;
                                }
                                if (found != -1)
                                    dmi.setActionCommand((String)propertyValues.elementAt(found));

                                setRealObject(dmi);
				dmi.setProxyInRealObject(this);
				setRealObjectProperties((Sharable)dmi, mo);

                                // smiller - moved menu.add line from above setRealObject(dmi) to fix
                                // a MAC bug that caused menu not to update when label was changed
                                menu.add(dmi);

			}
			else
				super.create(mo);
		}
		catch (DorminException e) { 
			if(dmi != null)
				((Menu)getRealParent()).remove(dmi);
			throw e; 
		}
	}
	
	public void delete(MessageObject mo){ 
		this.deleteProxy();
	}
	
	public  void setProperty(MessageObject inEvent) throws DorminException{
		Sharable realObj = (Sharable)getObject();
		try{
			setRealObjectProperties(realObj, inEvent);
		} catch (DorminException e) { throw e;}
	}
}
		
