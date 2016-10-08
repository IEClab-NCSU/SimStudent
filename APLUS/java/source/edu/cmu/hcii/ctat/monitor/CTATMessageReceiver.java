/**
*
*/
package edu.cmu.hcii.ctat.monitor;

public interface CTATMessageReceiver
{    						
	/**
	 *
	 */
    public void handleIncomingData (String data);
    
	/**
	 *
	 */
    public void handleConnectionClosed ();
          
}
