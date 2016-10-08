/**
 * 
 */
package edu.cmu.hcii.android;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import edu.cmu.hcii.ctat.CTATCLBridge;
import edu.cmu.hcii.ctat.CTATCS2NDVD;
import edu.cmu.hcii.ctat.CTATLink;
import edu.cmu.hcii.ctat.ProblemEndHandler;
import edu.cmu.hcii.ctat.ProblemSetEndHandler;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class AndroidTutorShop extends CTATCS2NDVD
		implements ProblemSetEndHandler, ProblemEndHandler, Utils.AlertDialog, Runnable
{

	/**
	 * Factory to mimic action of {@link CTATCLBridge#main(String[])}.
	 * @param myActivity
	 * @return
	 */
	public static AndroidTutorShop create(Activity myActivity) {
//		Utils.setSuppressDialogs(true);  // FIXME revise to show error messages to user

		AndroidTutorShop service=new AndroidTutorShop(myActivity);
    	return service;
	}
	
	/** Access to Android facilities and environment. */
	private final Activity myActivity;

	/**
	 * Set {@link #myActivity}.
	 * @param myActivity 
	 */
	public AndroidTutorShop(Activity myActivity) {
		super();
    	ctatBase.setClassName("AndroidTutorShop");			
		this.myActivity = myActivity;		
        Utils.setAlertDialog(this);
	}

    /**
     * Display an alert dialog to the user.
     * @param e exception
     * @param message message body
     * @param title dialog title
     */
	public void showMessage(Throwable e, String message, String title) {
		CTATAlert cAlert = new CTATAlert(myActivity, e, message, title);
		cAlert.show();
	}
	
	/**
	 * Run the service.
	 * @param username
	 */
	public void runService(String username) {

		trace.addDebugCodes("android,ll,tsltsp,tsltstp,br,util");
		debug ("runService ("+username+")");
    	
		CTATLink.requirePredefinedUserid = true;
    	CTATLink.mountedFileSystem = "/sdcard/CTAT";
    	CTATLink.processMount();
		
    	runDVDContent(username);  // replace this code with the DVD service
	}

	/**
	 * Send an Intent to invoke the browser.
	 */
	protected void invokeBrowserOnLocalWebServer() {
		openBrowser("http://" + CTATLink.hostName + ":" + CTATLink.wwwPort);
	}

    /**
     * Open the default browser on the given URI.
     * @param uri
     */
    private void openBrowser(String uri) {
    	Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    	myActivity.startActivity(bi);
	}

	public boolean problemSetEnd(List<String> arg0) {
		debug("problemSetEnd("+arg0+")");
		return false;
	}

	public boolean problemEnd(String arg0) {
		debug("problemEnd("+arg0+")");
		return false;
	}
}
