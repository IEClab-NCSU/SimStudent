/*
 BR_Frame.java

 Author:			zzhang
 Description:
 */

package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Font;
import java.awt.Point;

import javax.swing.JComponent;

// ////////////////////////////////////////////////////
/**
 * The original graph view class
 * (formerly known as brFrame)
 */
// ////////////////////////////////////////////////////
public class BRPanel extends JComponent {


    public static final String EXAMPLE_TRACING_MODE = "Example-Tracing Mode";
    
    public static final String EXAMPLE_TRACING_MODE_ORDERED = EXAMPLE_TRACING_MODE + " (Ordered)";
    
    public static final String EXAMPLE_TRACING_MODE_UNORDERED = EXAMPLE_TRACING_MODE + " (Unordered)";

    public static final String DEMONSTRATE_MODE = "Demonstrate Mode";
    
    public static final String PRODUCTION_SYSTEM_MODE = "Prod. System Mode";

    public static final String HOME_URL = "http://ctat.pact.cs.cmu.edu";

    public static final int NOT_SET = 0;

    public static final int SET_KEEP_SAME = 1;

    public static final int SET_SEPARATE_THEM = 2;

    public static final Font VISITED_EDGE_FONT = new Font("", Font.BOLD
            | Font.ITALIC, 13);

    public static final Font BOLD_FONT = new Font("", Font.BOLD | Font.ITALIC, 14);

    public static final Font NORMAL_FONT = new Font("", Font.PLAIN, 12);

    public static final Font SMALL_FONT = new Font ("Dialog", Font.PLAIN, 9);


    // //////////////////////////////////////////////////////////////////////
    // 
    // Calclulate where to place the next Vertex box the user has just created.
    //
    // //////////////////////////////////////////////////////////////////////
    public static Point getNewVertexLocation(Point parentLocation, int childCount) {

        double length = 130;

        if (childCount == 0)
            length = 110;

        double angle = 0.0;

        double baseAngle = Math.PI / 5;

        if (childCount < 5)
            angle = (childCount + 1) / 2 * baseAngle;
        else if (childCount < 7) {
            angle = (childCount + 1) / 2 * baseAngle - baseAngle / 2;
        } else {
            angle = baseAngle * 1.5;
            angle += (childCount - 7) / 2 * baseAngle / 2;
            length = 180;
        }

        int x, y;

        if (childCount % 2 == 1)
            angle = angle * -1;

        x = (int) (length * Math.sin(angle));
        y = (int) (length * Math.cos(angle));
        angle = angle / Math.PI * 180;
        return new Point(parentLocation.x + x, parentLocation.y + y);
    }

}
