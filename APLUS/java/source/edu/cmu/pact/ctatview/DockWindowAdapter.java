package edu.cmu.pact.ctatview;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.DockManager;

import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;

/**
 * Listener for keeping track of window visibility status.
 * @author Stephanie
 *
 */
public class DockWindowAdapter extends DockingWindowAdapter implements WindowListener, DockingWindowListener {
	private final DockManager dockManager;
	private final int showWindowMenuId;
	private static final boolean TESTING = false;
	
	public DockWindowAdapter(DockManager dockManager, int viewCount) {
		this.dockManager = dockManager;
		this.showWindowMenuId = viewCount;
	}

	/**
	 * If the window is being hidden via the top-right hand corner "X" button
	 * or right click -> close, tell the dock manager to update the Windows menu.
	 */
	@Override
	public void windowRemoved(DockingWindow removedFromWindow,
            DockingWindow removedWindow) {
		/* windowRemoved is called when hiding a window and when opening a
		 * previously hidden window. We only want to fire the menu update in
		 * the first case and removedFromWindow.isShowing() is only true in
		 * the first case, so this is a convenient way to check before
		 * calling for an update.
		 */
		if((removedFromWindow.isShowing())
				&& (this.showWindowMenuId != dockManager.getGraphEditorId())) {
			// mark as hidden on the Window menu
			this.dockManager.setMenuVisibilityMarker(this.showWindowMenuId, false);
		}
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowRemoved): HERE, id = " + this.showWindowMenuId);
			trace.out("mg", "DockWindowAdapter (windowRemoved): removedWindow");
			int id = dockManager.findView(removedWindow);
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\tid = " + (id < 0 ? removedWindow.hashCode() : id));
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\tshowing = " + removedWindow.isShowing());
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\tclosable = " + removedWindow.isClosable());
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\trestorable = " + removedWindow.isRestorable());
		}
		// handle any children windows, in case we have views inside views
		handleChildren(removedWindow, 1, Boolean.valueOf(removedWindow.isClosable()));
		
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowRemoved): removedFromWindow");
			int id = dockManager.findView(removedFromWindow);
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\tid = " + (id < 0 ? removedFromWindow.hashCode() : id));
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\tshowing = " + removedFromWindow.isShowing());
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\tclosable = " + removedFromWindow.isClosable());
			trace.out("mg", "DockWindowAdapter (windowRemoved):\t\trestorable = " + removedFromWindow.isRestorable());
			handleChildren(removedWindow, 1, null);
		}
	}
	
	/**
	 * Look through children of a given window and mark menu-displayed views
	 * (conflict tree, Jess console, etc.) as shown or not shown.
	 * 
	 * @param dw		The window whose children we are marking
	 * @param depth		For debugging purposes
	 * @param add		<code>true</code> if marking children as shown,
	 * 					<code>false</code> if marking as unshown,
	 * 					<code>null</code> if doing nothing (for debugging)
	 */
	private void handleChildren(DockingWindow dw, int depth, Boolean add) {
		String tabs;
		int n = dw.getChildWindowCount();
		if(TESTING) {
			tabs = "\t\t";
			for(int i = 0; i < depth; i++) {
				tabs = tabs.concat("\t");
			}
		}
		for(int i = 0; i < n; i++) {
			// check for children that are stored by the dock manager
			DockingWindow cw = dw.getChildWindow(i);
			int cid = dockManager.findView(cw);
			if(TESTING) {
				trace.out("mg", "DockWindowAdapter (handleChildren):" + tabs
						+ (cid < 0 ? cw.hashCode() : cid));
			}
			if((add != null) && (cid > 0)) {
				boolean mark = add.booleanValue();
				/* When bringing up a child of another window, make sure
				 * the child actually needs to be displayed (may not be
				 * true if two views are contained within another view)
				 */
				if(mark == true) {
					mark = mark & cw.isClosable();
				}
				if(TESTING) {
					trace.out("mg", "DockWindowAdapter (handleChildren): setting view " + cid + " to " + (mark ? "(x)" : "( )"));
				}
				// mark as shown/unshown
				dockManager.setMenuVisibilityMarker(cid, mark);
			}
			// if there are any further children, handle those too
			if(cw.getChildWindowCount() > 0) {
				handleChildren(cw, depth+1, add);
			}
		}
	}
	
	@Override
	public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
		/* If the window will be shown onscreen, mark it in the menu. */		
		if((addedWindow.getWidth() > 0)
				&& (this.showWindowMenuId != dockManager.getGraphEditorId())) {
			// mark as visible on the Window menu
			this.dockManager.setMenuVisibilityMarker(this.showWindowMenuId, true);
			// let the dock manager handle any refreshing that needs to be done (if any)
			this.dockManager.refreshWindow(this.showWindowMenuId);
		}
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowAdded): HERE, id = " + this.showWindowMenuId);
			trace.out("mg", "DockWindowAdapter (windowAdded): addedWindow " + addedWindow.getTitle());
			int id = dockManager.findView(addedWindow);
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\tid = " + (id < 0 ? addedWindow.hashCode() : id));
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\tshowing = " + addedWindow.isShowing());
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\tclosable = " + addedWindow.isClosable());
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\trestorable = " + addedWindow.isRestorable());
		}
		handleChildren(addedWindow, 1, Boolean.valueOf(true));
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowAdded): addedToWindow " + addedToWindow.getTitle());
			int id = dockManager.findView(addedToWindow);
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\tid = " + (id < 0 ? addedToWindow.hashCode() : id));
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\tshowing = " + addedToWindow.isShowing());
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\tclosable = " + addedToWindow.isClosable());
			trace.out("mg", "DockWindowAdapter (windowAdded):\t\trestorable = " + addedToWindow.isRestorable());
		}
		handleChildren(addedToWindow, 1, Boolean.valueOf(true));
	}
	
	@Override
	public void windowRestoring(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowRestoring): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowRestored(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowRestored): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowUndocking(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowUndocking): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowUndocked(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowUndocked): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowDocking(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowDocking): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowDocked(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowDocked): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowMinimized(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowMinimized): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowMinimizing(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowMinimizing): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowMaximized(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowMaximized): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowMaximizing(DockingWindow window) {
		if(TESTING) {
			trace.printStack("mg", "DockWindowAdapter (windowMaximizing): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowClosing): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowClosed): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowDeactivated): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowDeiconified): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowActivated): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowOpened(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowOpened): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowIconified(WindowEvent arg0) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowIconified): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowHidden(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowHidden): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void windowShown(DockingWindow window) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (windowShown): HERE, id = " + this.showWindowMenuId);
		}
	}
	
	@Override
	public void viewFocusChanged(View previouslyFocusedView, View focusedView) {
		if(TESTING) {
			trace.out("mg", "DockWindowAdapter (viewFocusChanged): HERE, id = " + this.showWindowMenuId);
			if(previouslyFocusedView != null) {
				trace.out("mg", "DockWindowAdapter (viewFocusChanged): previouslyFocusedView");
				int id = dockManager.findView(previouslyFocusedView);
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tid = " + (id < 0 ? previouslyFocusedView.hashCode() : id));
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tshowing = " + previouslyFocusedView.isShowing());
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tclosable = " + previouslyFocusedView.isClosable());
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\trestorable = " + previouslyFocusedView.isRestorable());
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tvisible = " + previouslyFocusedView.isVisible());
			}
			else {
				trace.out("mg", "DockWindowAdapter (viewFocusChanged): previouslyFocusedView  = NONE");
			}
			if(focusedView != null) {
				trace.out("mg", "DockWindowAdapter (viewFocusChanged): focusedView");
				int id = dockManager.findView(focusedView);
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tid = " + (id < 0 ? focusedView.hashCode() : id));
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tshowing = " + focusedView.isShowing());
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tclosable = " + focusedView.isClosable());
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\trestorable = " + focusedView.isRestorable());
				trace.out("mg", "DockWindowAdapter (viewFocusChanged):\t\tvisible = " + focusedView.isVisible());
			}
			else {
				trace.out("mg", "DockWindowAdapter (viewFocusChanged): focusedView = NONE");
			}
		}
	}

}
