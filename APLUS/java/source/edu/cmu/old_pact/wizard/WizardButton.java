package edu.cmu.old_pact.wizard;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dorminbutton.DorminButton;

public class WizardButton extends DorminButton implements Sharable{
	
	public WizardButton(ObjectProxy parent){
		super(parent);
	}
	public void createProxy(ObjectProxy parent){
		if(parent != null){
			butProxy = new WizardProxy(parent, "Button");

			butProxy.setRealObject(this);
		}
	}
	/* (non-Javadoc)
	 * @see dormin.Sharable#getObjectProxy()
	 */
	public ObjectProxy getObjectProxy() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see dormin.Sharable#setProxyInRealObject(dormin.ObjectProxy)
	 */
	public void setProxyInRealObject(ObjectProxy op) {
		// TODO Auto-generated method stub
		
	}
}