package edu.cmu.pact.ctatview;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableModel;

/**
 * A {@link JTable} extension to better support horizontal scrolling for a (horizontally)
 * dynamically sized table.
 * <br />
 * Taken directly from:
 * http://stackoverflow.com/questions/3857807/java-swing-problems-with-horizontal-scroll-for-a-jtable-embedded-in-a-jscrolled
 */
public class JHorizontalTable extends JTable {
		private static final long serialVersionUID = 1733423788041842977L;
		
		public JHorizontalTable(TableModel model) {
			super(model);
		}

		@Override
		  public Dimension getPreferredSize() {
		    if (getParent() instanceof JViewport) {
		      if ( ((JViewport) getParent()).getWidth() > super.getPreferredSize().width) {
		        return getMinimumSize();
		      }
		    }
		    return super.getPreferredSize(); 
		  }

		  @Override
		  public boolean getScrollableTracksViewportWidth () {
		    if (autoResizeMode != AUTO_RESIZE_OFF) {
		      if (getParent() instanceof JViewport) {
		        return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
		      }
		      return true;
		    }
		    return false;
		  }
}
