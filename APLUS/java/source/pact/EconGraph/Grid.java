package pact.EconGraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

public class Grid  {
    
    int ymin = 0; //minimum y on graph
    int ymax = 10;
    int yinc = 2;//major increment spacing
    String ylabel = "y-axis";
    
    int xmin = 0;
    int xmax = 10;
    int xinc = 2;
    String xlabel = "x-axis";
    
    Color tempColor=new Color(0,0,0);
    Graphics ag;
    int counter=0;
    boolean printed=false;
    //should probably set these on our own to maximize space use
    int xcorner = 50;//top left x-loc in pixels
    int ycorner = 50;
    int xsizepixels = 300;//size of x dim in pixels
    int ysizepixels = 300;
    int pixperx; //pixels per one unit of x increment
    int pixpery; //pixels per one unit of y increment
    int boxsize; //marking box size of moveable handles
    
    Font smallestFont = new Font("Monospaced",Font.BOLD,8);
    Font smallFont = new Font("Monospaced",Font.BOLD,12);
    Font bigFont = new Font("Monospaced",Font.BOLD,18);
    
    Grid(int txc, int tyc, int txsp, int tysp,
    int txmin, int txinc, int txmax, String txlabel,
    int tymin, int tyinc, int tymax, String tylabel) {
        ymin = tymin;
        ymax = tymax;
        yinc = tyinc;
        ylabel = tylabel;
        
        xmin = txmin;
        xmax = txmax;
        xinc = txinc;
        xlabel = txlabel;
        
        xcorner = txc;
        ycorner = tyc;
        xsizepixels = txsp;
        ysizepixels = tysp;
        
        this.finalizepixelsizes();
    }
    
    //adjust drawing size and sizepixels to make it work
    void finalizepixelsizes() {
        double boxscale = 0.4;
        
        pixperx = (int) (float) 1.0*xsizepixels/(xmax - xmin);
        pixpery = (int) (float) 1.0*ysizepixels/(ymax - ymin);
        boxsize = (int) Math.min(boxscale*pixperx,boxscale*pixpery);
        
        //adjust size to match pixpers
        xsizepixels = pixperx*(xmax-xmin);
        ysizepixels = pixpery*(ymax-ymin);
    }
    
    //maximize size of graph given applet size
    void maximizeGrid(Graphics g, int width, int height) {
        if (boxsize < 5) boxsize = 5;
        g.setFont(bigFont);
        FontMetrics fm = g.getFontMetrics(bigFont);
        //xcorner = boxsize+2*fm.stringWidth(""+ymax)+2*fm.charWidth('X');
        //ycorner = boxsize+fm.getHeight();
        xsizepixels = width;// - xcorner - fm.stringWidth("XXXXXXXXXX");
        ysizepixels = height;// - ycorner - boxsize - 2*fm.getAscent();
        finalizepixelsizes();//boxsize will shift...
        if (boxsize < 5) boxsize = 5;
    }
    
    //return true if point is in graph proper using pixel measures
    //allows a boxsize border around the edges of the graph
    boolean pixelInGraphPlusBorder(Point p) {
        return ( (p.getx() >= (xcorner-boxsize)) &&
        (p.getx() <= (xcorner+xsizepixels+boxsize)) &&
        (p.gety() >= (ycorner-boxsize)) &&
        (p.gety() <= (ycorner+ysizepixels+boxsize))
        );
    }
    
    //return true if point is in graph proper using pixel measures--no border
    boolean pixelInGraph(Point p) {
        return ( (p.getx() >= xcorner) &&
        (p.getx() <= (xcorner+xsizepixels)) &&
        (p.gety() >= ycorner) &&
        (p.gety() <= (ycorner+ysizepixels))
        );
    }
    
    //true if relative point (in the x/y coord system) is in the graph
    boolean relativeInGraph(Point p) {
        return ( (p.getx() >= xmin) &&
        (p.getx() <= xmax) &&
        (p.gety() >= ymin) &&
        (p.gety() <= ymax)
        );
    }
    
    //return true if p1 and p2 are closer than boxsize
    //pixelpt is in absolute pixels, while relativept is in x/y coord system
    boolean near(Point pixelpt, Point relativept) {
        //convert relativept to pixels
        int x = xcorner + (relativept.getx()-xmin)*pixperx;
        int y = ycorner + ysizepixels - (relativept.gety()-ymin)*pixpery;
        return ( (Math.abs(pixelpt.getx()-x) <= boxsize) &&
        (Math.abs(pixelpt.gety()-y) <= boxsize)
        );
    }
    
    //on moving line see if pixelpt is within the boxsize of any moveable point
    //return null if it isnt, otherwise return first such point encountered
    Point closeMoveablePoint(Point pixelpt, Line line) {
        if (!line.getMoving()) return null;
        for (int p = 0; p < line.point.length; p++)
            if ((line.point[p].getMoveable()) && near(pixelpt,line.point[p]))
                return line.point[p];
        return null;
    }
    
    //convert pixel location to location in x/y coord system
    //******should check to make sure it is inbound...******
    Point convertPixel(Point pixelpt) {
        return new Point( (pixelpt.getx()-xcorner+pixperx/2)/pixperx + xmin,
        
        ymin-(pixelpt.gety()-ycorner-ysizepixels-pixpery/2)/pixpery);
    }
    
    
    //convert pixel location to location in x/y coord system
    //******should check to make sure it is inbound...******
    Point convertPixelBack(Point locpt) {
        return new Point( ((locpt.getx()-xmin)*pixperx + xcorner+pixperx/2),
        
        (ycorner + ysizepixels + (pixpery/2) - ((locpt.gety()-ymin)*pixpery)) );
    }
    
    
    
    
    //draw the basic graph background
    void drawGraphPaper(Graphics g) {
        Color labelColor = Color.black;//graph labels
        Color interiorColor = Color.white;
        Color borderColor = Color.black;
        int borderThickness = 1;
        Color mainlineColor = Color.black;
        int mainlineThickness = 0;
        Color sublineColor = Color.lightGray;
        int sublineThickness = 0;
        
        int ticksize = boxsize/2;
        
        g.setColor(interiorColor);
        g.fillRect(xcorner,ycorner,xsizepixels,ysizepixels);
        
        //draw the border
        g.setColor(borderColor);
        for (int b = 0; b < borderThickness; b++)
            g.drawRect(xcorner-b,ycorner-b,xsizepixels+2*b,ysizepixels+2*b);
        
        //draw the sublines
        g.setColor(sublineColor);
        //verticals
        for (int x = xmin; x <= xmax; x++) {
            for (int b = -sublineThickness; b <= sublineThickness; b++) {
                g.drawLine(xcorner+(x-xmin)*pixperx+b,ycorner,
                xcorner+(x-xmin)*pixperx+b,ycorner+ysizepixels);
            }
        }
        //horizontals
        for (int y = ymin; y <= ymax; y++) {
            for (int b = -sublineThickness; b <= sublineThickness; b++) {
                g.drawLine(xcorner,ycorner+ysizepixels-(y-ymin)*pixpery+b,
                
                xcorner+xsizepixels,ycorner+ysizepixels-(y-ymin)*pixpery+b);
            }
        }
        
        //draw and label the mainlines..with tick extensions
        g.setColor(mainlineColor);
        g.setFont(smallFont);
        FontMetrics fm = g.getFontMetrics(smallFont);
        //verticals
        for (int x = xmin; x <= xmax; x++) {
            if (((x % xinc) == 0) || (x == xmin) || (x == xmax)) {
                for (int b = -mainlineThickness; b <= mainlineThickness; b++) {
                    g.drawLine(xcorner+(x-xmin)*pixperx+b,ycorner,
                    
                    xcorner+(x-xmin)*pixperx+b,ycorner+ysizepixels+ticksize);
                }
                g.drawString(""+x,xcorner+(x-xmin)*pixperx-fm.stringWidth(""+x)/2,
                ycorner+ysizepixels+fm.getAscent()+ticksize);
            }
        }
        //horizontals
        for (int y = ymin; y <= ymax; y++) {
            if (((y % yinc) == 0) || (y == ymin) || (y == ymax) ) {
                for (int b = -mainlineThickness; b <= mainlineThickness; b++) {
                    
                    g.drawLine(xcorner-ticksize,ycorner+ysizepixels-(y-ymin)*pixpery+b,
                    
                    xcorner+xsizepixels,ycorner+ysizepixels-(y-ymin)*pixpery+b);
                }
                g.drawString(""+y,xcorner-2*ticksize-fm.stringWidth(""+y),
                
                ycorner+ysizepixels-(y-ymin)*pixpery+fm.getAscent()/2);
            }
        }
        
        //label the axis
        int widthmaxy = fm.stringWidth(""+ymax);
        g.setFont(bigFont);
        fm = g.getFontMetrics(bigFont);
        g.setColor(labelColor);
        //x-axis...easy
        g.drawString(xlabel,xcorner+xsizepixels/2-fm.stringWidth(xlabel)/2,
        ycorner+ysizepixels+2*fm.getAscent()+ticksize);
        //y-axis...a bit trickier
        for (int y = 0; y < ylabel.length(); y++) {
            g.drawString(""+ylabel.charAt(y),xcorner-2*ticksize-2*widthmaxy-
            fm.stringWidth(""+ylabel.charAt(y))/2,
            ycorner+(ysizepixels-ylabel.length()*fm.getAscent())/2
            +(y+1)*fm.getAscent());
        }
    }
    
    boolean inBounds(Point p) {
        return ( (p.getx() >= xmin) && (p.getx() <= xmax)
        && (p.gety() >= ymin) && (p.gety() <= ymax) );
    }
    
    int absoluteXLoc(Point p) {
        return xcorner+(p.getx()-xmin)*pixperx;
    }
    
    int absoluteYLoc(Point p) {
        return ycorner+ysizepixels-(p.gety()-ymin)*pixpery;
    }
    
    int absoluteXLoc(int x) {
        return xcorner+(x-xmin)*pixperx;
    }
    
    int absoluteYLoc(int y) {
        return ycorner+ysizepixels-(y-ymin)*pixpery;
    }
    
    
    void plotPoint(Graphics g, Point p) {
        Color labelColor = Color.black;
        
        //make sure that it is inbounds
        if (inBounds(p)) {
            int x = absoluteXLoc(p);
            int y = absoluteYLoc(p);

            int box = (int) (p.getMoveable()? boxsize: boxsize*0.50);
            
            if(p.useTempColor())
                g.setColor(p.tempColor);
            else
            g.setColor(p.getColor());
            g.drawRect(x-box,y-box,2*box,2*box);
            //contract it by 1
            g.drawRect(x-box+1,y-box+1,2*box-2,2*box-2);
            
            g.drawLine(x-box,y-box,x+box,y+box);
            g.drawLine(x-box,y+box,x+box,y-box);
            
            //label
            // if (p.getLabel()) labelPoint(g,p);
             /*
             {
                g.setColor(labelColor);
                g.setFont(bigFont);
                g.drawString(p.showLabel(),x+box,y-box);
             }
              */
        }
    }
    
    void labelPoint(Graphics g, Point p) {
        Color labelColor = Color.black;
        int pointOffset = 2*boxsize;
        
        //make sure that it is inbounds
        if (inBounds(p)) {
            int x = absoluteXLoc(p);
            int y = absoluteYLoc(p);
            
            g.setFont(bigFont);
            FontMetrics fm = g.getFontMetrics(bigFont);
            int textheight = fm.getAscent()+fm.getDescent();
            
            g.setColor(Color.white);
            
            g.fillRect(x+pointOffset,y-pointOffset-textheight,fm.stringWidth(p.showLabel
            ()),textheight);
            g.setColor(Color.black);
            
            g.drawRect(x+pointOffset,y-pointOffset-textheight,fm.stringWidth(p.showLabel
            ()),textheight);
            
            g.drawLine(x,y,x+pointOffset,y-pointOffset);
            g.drawLine(x-1,y,x+pointOffset,y-pointOffset-1);
            g.drawLine(x,y+1,x+pointOffset+1,y-pointOffset);
            
            g.drawString(p.showLabel(),x+pointOffset,y-pointOffset-fm.getDescent());
        }
    }

    public Vector clipLine(Point start, Point end) {
        int x1, y1, x2, y2;
        x1 = start.x;
        y1 = start.y;
        x2 = end.x;
        y2 = end.y;
       // clip vertical lines
         if (start.getx() == end.getx()) {
            // off the edge; don't draw
            if (start.getx() < 0 || start.getx() > xmax)
                return null;
            if ((start.gety() < 0 && end.gety() < 0) ||
            (start.gety() > ymax && end.gety() > ymax))
                return null;
            // start is at the top
            if (start.gety() < end.gety()) {
                if (start.gety() < 0)
                    y1 = 0;
                if (end.gety() > ymax)
                    y2 = ymax;
            }
            // end is at the top
            else {
                if (end.gety() < 0)
                    y2 = 0;
                if (start.gety() > ymax)
                    y1 = ymax;
            }
        }
        // clip horizontal lines
        else {
            // off the edge; don't draw
            if (start.gety() < 0 || start.gety() > ymax)
                return null;
            if ((start.getx() < 0 && end.getx() < 0) ||
            (start.getx() > xmax && end.getx() > xmax))
                return null;
            // start is at the left
            if (start.getx() < end.getx()) {
                
                if (start.getx() < 0)
                    x1 = 0;
                if (end.getx() > xmax)
                    x2 = xmax;
            }
            // end is at the left
            else {
                if (end.getx() < 0)
                    x2 = 0;
                if (start.getx() > xmax)
                    x1 = xmax;
            }
            
        }
        Vector v = new Vector();
        v.addElement(new Integer(x1));
        v.addElement(new Integer(y1));
        v.addElement(new Integer(x2));
        v.addElement(new Integer(y2));
        return v;
    }
    
    public void drawLine(Graphics2D g2, Point start, Point end) {
        Vector v = clipLine(start, end);
        if (v == null){
            printed=false;
            return;
        }
        counter=counter+1;
        printed=true;
        int x1 = ((Integer) v.elementAt(0)).intValue();
        int y1 = ((Integer) v.elementAt(1)).intValue();
        int x2 = ((Integer) v.elementAt(2)).intValue();
        int y2 = ((Integer) v.elementAt(3)).intValue();
        g2.drawLine(absoluteXLoc(x1), absoluteYLoc(y1),
        absoluteXLoc(x2), absoluteYLoc(y2));
        
    }
    
    void plotLine(Graphics g, Line ln) {
        int linewidth = 1;
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(ln.color);
        //g2.setStroke(new BasicStroke(3));
        g2.setStroke(ln.stroke);
        counter=0;
        for (int i = 1; i < ln.point.length; i++) {
            //           trace.out (5, this, "ln.point[" + i + "] = " + ln.point[i]);
            
            drawLine(g2, ln.point[i-1], ln.point[i]);
           

            
        }
            if(printed){
                g2.setFont(bigFont);
                FontMetrics fm = g2.getFontMetrics(bigFont); 
           g.drawString(ln.name,absoluteXLoc(ln.point[0])+2*boxsize+4,
              absoluteYLoc(ln.point[0])+fm.getAscent()/2 - 5);

            }
        g2.setStroke(new BasicStroke(1));
        
        
        
        
        
        /*
         
        boolean lastPointInBounds = false;
        for (int p = 0; p < ln.point.length; p++) {
            if (inBounds(ln.point[p])) {
                if (lastPointInBounds) //connect the dots
                {
                    for (int i = -linewidth; i <= linewidth; i++) {
                        g.drawLine(absoluteXLoc(ln.point[p])+i,
                            absoluteYLoc(ln.point[p]),
                            absoluteXLoc(ln.point[p-1])+i,
                            absoluteYLoc(ln.point[p-1]));
                        g.drawLine(absoluteXLoc(ln.point[p]),
                            absoluteYLoc(ln.point[p])+i,
                            absoluteXLoc(ln.point[p-1]),
                            absoluteYLoc(ln.point[p-1])+i);
                    }
                }
                plotPoint(g,ln.point[p]);
                g.setColor(ln.color);
                lastPointInBounds = true;
            }
            else if (!inBounds(ln.point[p]) && p>0){
         
              //  lastPointInBounds = false;
                Point temppoint;
                boolean dontdraw=false;
               // if(!inBounds(ln.point[p])){
                {   int tempx=ln.point[p].x;
                    int tempy=ln.point[p].y;
         
                    Point testerPt = convertPixel(ln.point[p]);
         
         
                  //  System.out.println("the ln.point[p].x = " + ln.point[p].x + " and the xmax is: " + xmax);
                  //  System.out.println("the ln.point[p].y = " + ln.point[p].y + " and the ymax is: " + ymax);
         
                    if (ln.point[p].x < xmin){
                        System.out.println("the ln.point[p].x = " +ln.point[p].x + " and the xmin is: " + xmin);
         
                        tempx=xmin;
                      //  dontdraw=true;
                    }
         
         
                    if (ln.point[p].x > xmax){
                        System.out.println("the ln.point[p].x = " + ln.point[p].x + " and the xmax is: " + xmax);
         
                        tempx=xmax;
                      //  dontdraw=true;
                    }
                    if (ln.point[p].y < ymin){
                        System.out.println("the ln.point[p].y = " + ln.point[p].y + " and the ymin is: " + ymin);
         
                        tempy=ymin;
                    //    dontdraw=true;
                   }
         
                    if (ln.point[p].y> ymax){
                        System.out.println("the ln.point[p].y = " + ln.point[p].y+ " and the ymax is: " + ymax);
         
                        tempy=ymax;
                    //    dontdraw=true;
                    }
         
         
         
         
                    if(inBounds(ln.point[p-1]))
                    {//all aspects of the point weren't off
                         temppoint=new Point(tempx, tempy);
                                         plotPoint(g,temppoint);
                                     g.setColor(ln.color);
         
                        System.out.println("temppoint: " + temppoint);
                      //  System.out.println("absoluteLocation of temppoint: " + absoluteXLoc(temppoint)+ ", " +absoluteYLoc(temppoint));
                        for (int i = -linewidth; i <= linewidth; i++) {
                        //    System.out.println("graph.grid.plotline line 417: " + absoluteXLoc(temppoint) + "temppoint by itself: " + temppoint);
                        //    System.out.println("ln.point.size = " + ln.point.length + " p = " + p);
                           System.out.println("the point from: " + (absoluteXLoc(temppoint)+i) + " , "
                            + (absoluteYLoc(temppoint)) + " and going to: " + (absoluteXLoc(ln.point[p-1])+i) + " , "
                            + (absoluteYLoc(ln.point[p-1])));
         
                            g.drawLine(absoluteXLoc(temppoint)+i,
                            absoluteYLoc(temppoint),
                            absoluteXLoc(ln.point[p-1])+i,
                            absoluteYLoc(ln.point[p-1]));
                            g.drawLine(absoluteXLoc(temppoint),
                            absoluteYLoc(temppoint)+i,
                            absoluteXLoc(ln.point[p-1]),
                            absoluteYLoc(ln.point[p-1])+i);
                        }//abby
                    } break;
                }//abby
         
         
            }
        }
        //label
        if ( (ln.name.length() > 0) && lastPointInBounds) {
            g.setFont(bigFont);
            FontMetrics fm = g.getFontMetrics(bigFont);
         
            g.drawString(ln.name,absoluteXLoc(ln.point[ln.point.length-1])+2*boxsize,
         
            absoluteYLoc(ln.point[ln.point.length-1])+fm.getAscent()/2);
        }
         */
    }
    
    void showCopyright(Graphics g, String cr) {
        g.setColor(Color.black);
        g.setFont(smallestFont);
        FontMetrics fm = g.getFontMetrics(smallestFont);
        g.drawString(cr,1,fm.getAscent());
    }
}


