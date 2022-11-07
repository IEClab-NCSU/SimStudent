/*
 * Created on Jul 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.Utilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;


/**
 * @author mpschnei
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LaunchHelp {
    /** Agent to display the help window */
	private HelpBroker hb;
    /** Locator for helpset */
    private static final String helpHS = "jhelpset.hs";
    /** Listener on Swing event that represents the user's help request */
    private ActionListener buttonListener = null;
    
    /** 
     * Instantiates and retains {@link #hb} from {@link #helpHS} 
     */
    public LaunchHelp() {
        HelpSet hs;
        ClassLoader cl = LaunchHelp.class.getClassLoader();
        try {
            URL hsURL = HelpSet.findHelpSet(cl, helpHS);
            hs = new HelpSet(null, hsURL);
           // hs.setHomeID("blah");
        } catch (Exception ee) {
            //Say what the exception really is
            trace.out( "HelpSet " + ee.getMessage());
            trace.out("HelpSet "+ helpHS +" not found");
            return;
         }
//      Create a HelpBroker object:
        hb = hs.createHelpBroker();
        if (trace.getDebugCode("br")) trace.out("br", "Created help broker!");
        buttonListener = new CSH.DisplayHelpFromSource( hb );
    }
    
    /**
     * 
     * @param evt
     */
	public void showHelp(ActionEvent evt) {
        if (trace.getDebugCode("br")) trace.out("br", "To call DisplayHelpFromSource.ActionListener");
        buttonListener.actionPerformed(evt);
	}
}
