//d:/Pact-CVS-Tree/Tutor_Java/./src/Utilities/Java/DorminMenu/MenuProxy.java
package edu.cmu.old_pact.dormin.menu;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;

public class MenuProxy extends ToolProxy {

	public MenuProxy(ObjectProxy parent, String proxyName){
		super("Menu",proxyName, parent);
	}
	
	public  void delete(MessageObject mo){ 
		this.deleteProxy();
	}
	
	public void create(MessageObject mo) throws DorminException{
		try{
			String childType = mo.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("MENUITEM")) {
                            MenuItemProxy mip = new MenuItemProxy(this);
				try{
				 	mip.mailToProxy(mo, (new Vector()));
				 } 
				 catch (DorminException e) {
				 	throw e;
				 }
			}
                        //SMILLER submenu
                        else if(childType.equalsIgnoreCase("MENU"))
                        {
                            try{
                                Vector propertyNames = mo.extractListValue("PROPERTYNAMES");
                                Vector propertyValues = mo.extractListValue("PROPERTYVALUES");
                                int namePos = indexOfStr(propertyNames, "Name");
                                String menuLabel = (String)propertyValues.elementAt(namePos);
                                DorminMenu menu = (DorminMenu)getObject();
                                DorminMenu submenu = menu.createSubMenu(menuLabel, this, menu.getFrame());
                                menu.add(submenu);
                                propertyNames = null;
                                propertyValues = null;
                            } catch(DorminException e){throw e;}
                        }
			else
                            super.create(mo);
		}
		catch (DorminException e) { 
			throw e; 
		}
	}

    int indexOfStr(Vector v,String str){
        int s = v.size();
        int toret = -1;
        for(int i=0; i<s; i++){
            if(((String)v.elementAt(i)).equalsIgnoreCase(str))
                return i;
        }
        return toret;
    }
}	
	
