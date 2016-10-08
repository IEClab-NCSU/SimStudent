//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/DummyCanvas.java
package edu.cmu.old_pact.dormin.toolframe;

import java.awt.Canvas;
import java.awt.Dimension;

class DummyCanvas extends Canvas {
   DorminToolFrame frame;
   
   public DummyCanvas(DorminToolFrame frame){
         this.frame = frame;
         }
    public Dimension preferredSize() {
		return frame.preferredSize();
    }

    public Dimension minimumSize() {
		return preferredSize();
   }
   
}   