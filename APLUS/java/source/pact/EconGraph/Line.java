package pact.EconGraph;

import java.awt.BasicStroke;
import java.awt.Color;

public class Line {
    public Point [] point; //turning points on the line
    public Color color;
    public String name;
    boolean moving;
    public BasicStroke stroke;
    
    Line(String lname, Point start, Point end, int handles,
                 Color lcolor, boolean moveable, BasicStroke s) {
        
        name = lname;
        point = new Point[handles];
        point[0] = start;
        if (handles > 1) point[handles-1] = end;
        color = lcolor;
        moving = moveable;
        stroke=s;
        
        this.initialize();
        
        for (int pt = 0; pt < point.length; pt++) {
            point[pt].setMoveable(moveable);
            point[pt].setColor(lcolor);
        }
        
        //point[0].setMoveable(false);//*********check
    }
    
    public void reset () {
        for (int i = 0; i < point.length; i++) 
            point[i].reset();
    }
    
    public String getName() {
        return name;
    }
    
    void initialize()//evenly spread out the points between st and finish
    {
        for (int pt = 1; pt < point.length-1; pt++) {
            point[pt] = new Point(
            pt*(point[point.length-1].getx()-point[0].getx())/
            (point.length-1)+point[0].getx(),
            pt*(point[point.length-1].gety()-point[0].gety())/
            (point.length-1)+point[0].gety(),
            name, pt);
        }
    }
    
    boolean getMoving() {
        return moving;
    }
    
    public Color getColor() {
        return color;
    }
    
    
    //summarizes line in a compact format for output
    //changing format has implications for external users!
    //currently #LXidXhandlesXpt1_xXpt1_yXpt2_xXpt2_yX...Xptn_xXptn_y
    //where X is replaced by the chosen delimiter
    public String summary(int id, String delim) {
        int p;
        String summary = "#L"+delim+id+delim+point.length;
        
        for (p = 0; p < point.length; p++) {
            summary += delim +point[p].getx()+ delim+point[p].gety();
        }
        
        return summary;
    }
    
}
