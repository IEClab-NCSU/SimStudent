package edu.cmu.pact.Utilities;

import java.io.IOException;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BrowserLauncher.BrowserLauncher;

/**
 * @author bleber
 */
public class LaunchCTATWebsite {

    public static void main(String[] args) {
        showWebPage(BRPanel.HOME_URL);
    }
    
    /**
     * Attempts to launch the CTAT website in a web browser
     */
    public static void showWebPage(String url) {
        try {
            BrowserLauncher.openURL(url);
        } catch (IOException e){
        	e.printStackTrace();
            JOptionPane.showMessageDialog(null, "<html>Visit the Authoring Tools help page at<br>" +
                    url + "</html>", "Help", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
