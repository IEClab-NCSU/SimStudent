package pact.CommWidgets;


//////////////////////////////////////////////////////
/**
	This class is used to track which widget has the
	focus.
	
	
*/
//////////////////////////////////////////////////////

public class FocusModel {
	
	protected static String previousFocus, currentFocus;
	
	public static String getLastFocus() {
		return previousFocus;
	}
	
	public static void tookFocus (JCommWidget w) {
		previousFocus = currentFocus;
		currentFocus = w.getCommNameToSend();
//		if (currentFocus != null && previousFocus != null)
//			trace.out (5, "FocusModel", "Current focus = " + currentFocus + 
//				" Previous Focus = " + previousFocus);
	}
	

}
