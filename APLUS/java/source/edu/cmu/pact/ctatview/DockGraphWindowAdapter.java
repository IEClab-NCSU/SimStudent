package edu.cmu.pact.ctatview;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTab;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.Utilities.trace;

public class DockGraphWindowAdapter extends DockingWindowAdapter implements DockingWindowListener {
	private final DockManager dockManager;
	private final CTATTabManager tabManager;
	private final int tabNumber;
	
	public DockGraphWindowAdapter(DockManager dockManager, CTATTabManager tabManager, int tabNumber) {
		this.dockManager = dockManager;
		this.tabManager = tabManager;
		this.tabNumber = tabNumber;
	}

	@Override
	public void windowShown(DockingWindow window) {
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "DockGraphWindowAdapter (windowShown): HERE, tab = " + this.tabNumber);
			trace.out("mg", "DockGraphWindowAdapter (windowShown): window = " + window.getTitle());
		}
		// focus on the window only if it will actually show
		if(window.getHeight() > 0)
			this.tabManager.updateIfNewTabFocus(this.tabNumber);
		tabManager.setTabVisibility(tabNumber, true);
		if (tabManager.numVisibleTabs() > 0) dockManager.showGraphPlaceHolder(false);
	}
	
	@Override
	public void windowClosing(DockingWindow window)
			throws OperationAbortedException {
		if(trace.getDebugCode("mg"))
			trace.out("mg", "DockGraphWindowAdapter (windowClosing): " + window.getTitle());
		if(trace.getDebugCode("mg"))
			trace.out("mg", "DockGraphWindowAdapter (windowClosing): tabNumber " + this.tabNumber);
		tabManager.closeTab(this.tabNumber);
	}

	@Override
	public void windowClosed(DockingWindow window) {
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "DockGraphWindowAdapter (windowClosed): HERE, tab = " + this.tabNumber);
		}
		tabManager.setTabVisibility(tabNumber, false);
		if (tabManager.numVisibleTabs() < 1) {
			dockManager.showGraphPlaceHolder(true);
		} else {
			tabManager.setFocusedTab(tabManager.chooseVisibleTab(), true);
		}
						
	}
	
	@Override
	public void windowRestored(DockingWindow window) {
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "DockGraphWindowAdapter (windowRestored): window = " + window.getTitle());
			trace.out("mg", "DockGraphWindowAdapter (windowRestored): HERE, tab = " + this.tabNumber);
		}
		// focus on the window only if it will actually show
		if(window.getHeight() > 0)
			this.tabManager.updateIfNewTabFocus(this.tabNumber);
		tabManager.setTabVisibility(tabNumber, true);
		if (tabManager.numVisibleTabs() > 0) dockManager.showGraphPlaceHolder(false);
	}
	

	
	@Override
	public void viewFocusChanged(View previouslyFocusedView, View focusedView) {
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "DockGraphWindowAdapter (viewFocusChanged): HERE, tab = " + this.tabNumber);
		}
		
		if(focusedView != null) {
			dockManager.graphTabFocused(this.tabNumber);
		}
	}
}
