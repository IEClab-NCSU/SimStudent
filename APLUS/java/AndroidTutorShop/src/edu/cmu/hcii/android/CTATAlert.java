/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.hcii.android;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author sewall
 *
 */
public class CTATAlert {
	
	/** Exception: will print stack trace in dialog. */
	private Throwable ex;
	
	/** Title for the dialog. */
	private String title;
	
	/** Message body to display. */
	private String message;
	
	/** Supervising Activity. */
	private final Activity myActivity;
	
	public CTATAlert(Activity myActivity, Throwable ex, String message, String title) {
		if(trace.getDebugCode("android"))
			trace.outNT("android", "CTATAlert("+myActivity+","+ex+","+message+","+title+")");
		this.myActivity = myActivity;
		this.ex = ex;
		this.message = message;
		this.title = title;
	}
	
	public void show() {
		DialogFragment newFragment = new MyAlertDialogFragment();
		if(trace.getDebugCode("android"))
			trace.outNT("android", "CTATAlert.show() newFragment "+newFragment);
	    newFragment.show(myActivity.getFragmentManager(), "dialog");
	}

	public class MyAlertDialogFragment extends DialogFragment {

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	        return new AlertDialog.Builder(myActivity)
	                .setTitle(title)
	                .setMessage(message)
	                .setPositiveButton(R.string.alert_dialog_ok,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                        	trace.err("AlertDialog("+title+") clicked OK; message:\n  "+message);
	                        }
	                    }
	                )
	                .create();
	    }
	}
}
