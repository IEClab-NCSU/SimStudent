package pact.CommWidgets;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;
import edu.cmu.pact.SocketProxy.XMLConverter;
import edu.cmu.old_pact.dormin.DorminException;

public class JCommWidgetsToolProxy extends ToolProxy {
    UniversalToolProxy utp;
    String widgetName;
    
    /**
	 * @return the widgetName
	 */
	public String getWidgetName() {
		return widgetName;
	}

	public JCommWidgetsToolProxy(String name, String type, ObjectProxy parent) {
        super(name, type, parent);
    }
    
    public JCommWidgetsToolProxy(String name, String type, ObjectProxy parent, String widgetName) {
        super(name, type, parent);
        this.widgetName = widgetName;
    }
    
    public JCommWidgetsToolProxy(ObjectProxy parent, String applicationName, UniversalToolProxy utp) {
        super(parent, applicationName);
        //		trace.out (5, this, "parent = " + parent + " application name = " + applicationName);
        this.utp = utp;
    }
    
    public void setProperty(edu.cmu.old_pact.dormin.MessageObject mo)throws DorminException {

		edu.cmu.pact.ctat.MessageObject ctatMessage = XMLConverter.commToNewMO(mo);

		utp.setProperty(ctatMessage);

    } 
}
 
