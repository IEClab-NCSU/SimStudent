package pact.EconGraph;


import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;

import edu.cmu.pact.Utilities.trace;


public class GraphPanel extends JPanel implements Runnable  {
    
    Color backgroundColor = Color.yellow;
    //abby
    boolean dragLine=false;
    String copyright = "v1.0 (c)2000, J.H. Miller, All Rights Reserved";
    boolean maximizeSize = true; //ignore exact placement and max graph size
    
    Thread runner;
    ParameterProvider parameterProvider;
    
    //for double buffering
    Image offim;
    Graphics offg;
    int width;
    int height;
    Point pointToMove = null; //point that is being moved...
    
    public Grid plot; // = new Grid(100,50,300,300,10,5,20,"x axis",10,5,30,"y axis");
    public Line [] line; // tracks lines...keep since output is dependent = new Line[2];
    
    Vector lineDrawOrder = new Vector(); //tracks order of lines for drawing
    
    String temp;//*********
    
    private Applet applet;
    public Hashtable hashtable = new Hashtable();
    
    public boolean getDragLine(){
        return dragLine;
    }
    
    public void run() {
        makeGraph(offg,plot);
    }
    
    public void setApplet(Applet applet) {
	trace.out (5, this, "setApplet: appet = " + applet);
        this.applet = applet;
        
    }
    
    public void setParameterProvider(ParameterProvider p) {
        parameterProvider = p;
    }
    
    public void reset() {
        try {
            for (int i = 0; i < line.length; i++)
                line[i].reset();
            makeGraph(offg,plot);
        } catch (NullPointerException e) {}
    }
    
    public Point convertPixelBack(Point p){
        
        return (plot.convertPixelBack(p));
        
    }
    
    //abby
    public void dragLine(Point cpoint, Point ptm, boolean BRD){
        Point newpoint=cpoint;
        Point pointToMove = ptm;
        
        //   if(BRD){
        //  if(dragLine){
        //  pointToMove = plot.convertPixel(ptm);
        //     pointToMove.setName(ptm.getName());
        //    }
        /// }
        if(!BRD){
            newpoint = plot.convertPixel(cpoint);
        }
        
        // trace.out("in dragline, newpoint (the point youre going to) is: " + newpoint);
        //  trace.out("in dragline, pointToMove (the point youre moving from) ls" + pointToMove);
        
        if ((pointToMove != null) ){// && plot.pixelInGraph(cpoint)) {
            
            // trace.out(" in graphPanel.dragLine and the pointToMove is: " + pointToMove);
            //   trace.out(" in graphPanel.dragLine and the cpoint is: " + cpoint);
            //   trace.out(" in graphPanel.dragLine and the newPoint is: " + newpoint);
            
            //abby
            if(dragLine){
                
                int oldX=pointToMove.getx();
                int oldY=pointToMove.gety();
                int Xdif=newpoint.x - oldX;
                int Ydif=newpoint.y - oldY;
                
                //alter all points on the line by the amount that the cpoint was moved.
                
                String linename = pointToMove.lineName;
                int lineIndex=0;
                
                if (Xdif != 0){
                    // trace.out("in the Xdif loop,  so the point has been moved along the x axis.");
                    //send the x difference to all the points on the line
                    for(int i=0; i<line.length; i++){
                        //      trace.out("length of the line array: " + line.length + "line name " + pointToMove.lineName);
                        //   trace.out("line[i].name: " + line[i].name + "linename: " + linename);
                        if(line[i].name.equals(linename)){//find the line where the point is on
                            //       trace.out("hellp");
                            lineIndex=i;
                            for(int j=0; j<line[i].point.length; j++){//add Xdif to each point
                                // if(!(line[i].point[j].pointNum==pointToMove.pointNum))//dont add it to the point that you
                                //           trace.out("line[i].point[j].x = " + line[i].point[j].x + " i= " + i + "newpoint.x = " + newpoint.x + " Xdif " + Xdif);
                                line[i].point[j].x = line[i].point[j].x + Xdif;
                            }
                        }
                    }
                }
                
                if (Ydif != 0){
                    //  trace.out("the point has been moved along the y axis.");
                    //send the difference to all the points on the line
                    //send the y difference to all the points on the line
                    for(int i=0; i<line.length; i++){
                        //    trace.out("length of the line array: " + line.length);
                        if(line[i].name.equals(linename)){//find the line where the point is on
                            for(int j=0; j<line[i].point.length; j++){//add Ydif to each point
                                // if(!(line[i].point[j].pointNum==pointToMove.pointNum))//dont add it to the point that you
                                line[i].point[j].y = line[i].point[j].y + Ydif;
                            }
                        }
                    }
                }
                /*    for(int a=0; a<line.length; a++){
                        line[lineIndex].point[a].move(line[lineIndex].point[a]);*/
                //   makeGraph(offg,plot);
                
                // }
                
                
                
            }
            else
                
            {//without else thats the old code
                
                // && plot.pixelInGraph(cpoint)) {
                //trace.out("here, so pointTomove isnt null");
                //  newpoint = plot.convertPixel(cpoint);
                
                
                if (plot.inBounds(newpoint) && !pointToMove.isGreen()){
                  //  trace.out("POint to move is green? " + pointToMove.isGreen());
                    //  trace.out("thenew point is in boudns");
                    pointToMove.move(newpoint);
                    
                }
                
            }
            
        }
    }

    
    
    public void init() {
        
        readInParams();
        
        //initialize the line drawing
        for (int ln = 0; ln < line.length; ln++)
            lineDrawOrder.addElement(line[ln]);
        
        //line[0] = new Line("Line 0",new Point(10,10), new Point(20,20), 4,
        //                   Color.blue,true);
        //line[1] = new Line("Line 1",new Point(10,20), new Point(20,10), 6,
        //                    Color.red,false);
        
        //Uses 1.1 event handling via anonymous inner classes
        //define, instantiate and register a MouseListener object
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                Point downpt = new Point(x,y);
                
                //*******test save and restore...****remove for production
                //trace.out(downpt.showLabel());
                /**
                 * if (downpt.getx() == 0)
                 * {
                 * temp = report();
                 * trace.out(temp.substring(temp.indexOf("@")));
                 * }
                 * if((temp != null) && (downpt.getx() == plot.xcorner))
                 * restore(temp.substring(temp.indexOf("@")+1));
                 **/
                //trace.out(report());
                //*********
                
                if (pointToMove != null) pointToMove = null;
                
                // if (plot.pixelInGraphPlusBorder(downpt)) //in interior of the graph
                {
                    int index = lineDrawOrder.size()-1;
                    do {
                        Line line = (Line) lineDrawOrder.elementAt(index);
                        pointToMove = plot.closeMoveablePoint(downpt,line);
                        if (pointToMove != null) {
                            //move the line to the back of the display queue
                            lineDrawOrder.removeElement(line);
                            lineDrawOrder.addElement(line);
                        }
                        index--;
                    }
                    while ((pointToMove == null) && (index >= 0));
                    
                }
                makeGraph(offg,plot);
            }
        }
        
        
        
        
        );
        
        /**
         * //define, instantiate and register a MouseListener object
         * this.addMouseListener(new MouseAdapter()
         * {
         * public void mouseReleased(MouseEvent e)
         * {
         * if ((pointToMove != null))
         * {
         * //pointToMove.setLabel(false);
         * //pointToMove = null;
         * makeGraph(offg,plot);
         * }
         * }
         * }
         * );
         **/
        
        //define, instantiate and register a MouseListener object
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                
                Point cpoint = new Point(x,y);
                //abby
                dragLine(cpoint, pointToMove, false);
                makeGraph(offg,plot);
            }
        }
        );
        
        //define, instantiate and register a MouseListener object
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                Point cpoint = new Point(x,y);
                if ((pointToMove != null))// && plot.pixelInGraph(cpoint))
                    pointToMove.updateListeners();
            }
        }
        );
        trace.out (5, this, "this.getSize().width " + getSize().width);
        trace.out (5, this, "width = " + getSize().width + " height " + getSize().height);
        trace.out(5,this, "applet = " + applet);
        
        //trace.out("disp? " + applet.is ); //isDisplayable());
	if (applet != null) {
	    trace.out(5,this, "applet width: " + applet.getWidth());
	    offim = createImage(applet.getSize().width,applet.getSize().height);
	} else {
	    offim = createImage (getSize().width, getSize().height);
	}
	trace.out (5, this, "image width = " + offim.getWidth((java.awt.image.ImageObserver) this));
	trace.out (5, this, "image height = " + offim.getHeight((java.awt.image.ImageObserver) this));
		   
	trace.out (5, this, "this size = " + this.getSize());

        // trace.out("offim" + offim);
        offg = offim.getGraphics();
        
        
//        trace.out("offg" + offg);
        // Turn on anti-aliased line drawing (to make the lines look smoother).  Only in Java 1.2 or higher
        String lineAA= getParameter("lineAntiAliasing");
        String fontAA= getParameter("fontAntiAliasing");
        try {
            if (lineAA != null && lineAA.equalsIgnoreCase("true"))
                ((Graphics2D) offg).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (fontAA != null && fontAA.equalsIgnoreCase("true"))
                ((Graphics2D) offg).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } catch (java.lang.NoClassDefFoundError e) {
            trace.out(5, this, "sorry, no anti-aliaising");
        }
        
        
        if (maximizeSize)
            plot.maximizeGrid(offg,width,height );//****
        
        
        makeGraph(offg,plot);
        
    }
    
    public void start() {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
        clearSurface(offg);
    }
    
    public void stop() {
        if (runner != null) {
            runner.stop();
            runner = null;
        }
    }
    
    public void clearSurface(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0,0,this.getSize().width,this.getSize().height);
    }
    
    public void makeGraph(){
        makeGraph(offg, plot);
    }
    
    
    void makeGraph(Graphics g,Grid gp) {
        //g.setPaintMode(); //overwrites
        clearSurface(g);
        gp.showCopyright(g,copyright);
        gp.drawGraphPaper(g);
        
        
        for (int ln = 0; ln < lineDrawOrder.size(); ln++) {
            Line l= (Line) lineDrawOrder.elementAt(ln);
            gp.plotLine(g,l);
            
            for(int i=0; i<l.point.length; i++ ){
                
                gp.plotPoint(g, l.point[i]);
                //
            }
            //trace.out("added THIS");
        }
        //redo the pointToMove to make sure label is ontop
        if (pointToMove != null) {
            gp.plotPoint(g,pointToMove);
            gp.labelPoint(g,pointToMove);
        }
        
        repaint();
    }
    
    //override for double buffering
    public void update(Graphics g) {
        paint(g);
    }
    
    public void paint(Graphics g) {
        
        if (g != null)
            if (offim != null)
                g.drawImage(offim,0,0,this);
            else {
                int width = getFontMetrics(g.getFont()).stringWidth("GraphTool");
                g.drawString("GraphTool", getSize().width / 2 - width / 2, getSize().height / 2);
            }
    }
    
    
    public String getParameter(String name) {
        if (applet == null && parameterProvider == null)
            throw (new Error("Graph.java: Can't find parameters."));
        
        //  if (applet == null)
        return parameterProvider.getParameter(name);
        //  else
        //    return applet.getParameter(name);
    }
    
    //read in applet parameters
    public void readInParams() {
        int [] ints = new int[100];
        String [] strs  = new String[20];
        String s;
        int numberoflines,i,pt,tpts;
        
        
        s = getParameter("setup"); //plot set up specification
        
        
        Parseappletstring.parseString(s,ints,strs);
        try {
        	trace.out ("mps", " value for setup = " + s);
        } catch (NullPointerException e) {
        	
        }
        for (int j = 0; j < 20; j++) {
            trace.out (5, "mps", "parameter = " + strs[j]);
        }
        //trace.out(s);
        backgroundColor = Parseappletstring.makeColor(strs[0]);
        //trace.out(strs[2]);
        if(strs[3] != null && strs[3].equalsIgnoreCase("line"))//abby
            dragLine=true;
        else
            dragLine=false;
        plot = new Grid(ints[0],ints[1],ints[2],ints[3],ints[4],ints[5],ints[6],
        strs[1],ints[7],ints[8],ints[9],strs[2]);
        //locx=ints[0];
        //locy=ints[1];
        width=ints[2];
        height=ints[3];
        
        
        s = getParameter("nl"); //number of lines
        Parseappletstring.parseString(s,ints,strs);
        numberoflines = ints[0];
        line = new Line[numberoflines];
        
        BasicStroke a=new BasicStroke();
        
        for (i = 0; i < numberoflines; i++) //grab the lines
        {
            switch(i){
                case (0):a=new BasicStroke( 3, BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] {2.0f,5.0f},  0);break;
                case (1):a=new BasicStroke( 3, BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] {10.0f,5.0f},  0);break;
                case (2):a=new BasicStroke(3); break;}
            
            s = getParameter("ln"+(i+1));
            Parseappletstring.parseString(s,ints,strs);
            //ignores ints[5] which was box size, ints[6] which was label
            
            //trace.out (5, this, "creating line: s = " + s);
            //trace.out (5, this, "creating line: strs = " + strs[0]);
            //trace.out (5, this, "creating line: ints = " + ints[0] + ", " + ints[1] +
            //            ", " + ints[2] + ", " + ints[3] + ", " + ints[4]);
            
            line[i] = new Line(strs[0],
            new Point(ints[0],ints[1], strs[0], 0),
            new Point(ints[2],ints[3], strs[0], ints[4] - 1),
            ints[4],
            Parseappletstring.makeColor(strs[1]),(ints[7] == 1), a);
            
            //trace.out (5, this, "add line " + strs[0] + " to hashtable");
            hashtable.put(strs[0], line[i]);
            
            if (ints[8] == 1) //initialization line follows for all handle values....
            {
                tpts = ints[4]; //total number of handle points
                s = getParameter("ln"+(i+1)+"init");
                Parseappletstring.parseString(s,ints,strs);
                for (pt = 0; pt < tpts; pt++) {
                    line[i].point[pt].move(new Point(ints[pt*2],ints[pt*2+1]));
                    line[i].point[pt].saveResetState();
                }
            }
        }
    }
    
    //provides a summary of all the moveable lines as a big string
    //currently"Glabel@id1@handles_id1@x0@y0@x1@y1@..@xn@yn@id2@handles_id2@...yz
    //may be called by submit applet....
    public String toString() {
        int ln;
        
        String name = getParameter("label");
        if (name == null) name = "G";
        else name = "G"+name;
        
        
        for (ln = 0; ln < line.length; ln++) {
            if (line[ln].getMoving())
                name += "@" + line[ln].summary(ln+1,"@");
            
        }
        
        return name;
    }
    
    //provides a summary of all the moveable lines for use by workbook answer
    //storage and grading, based on orignal order of lines received
    //Current format is:
    //"label@id1,handles_id1,x0,y0,x1,y1,..,xn,yn,id2,handles_id2,...yz
    //will be called from javascript code
    public String report() {
        int ln,ml;
        
        String name = getParameter("label");
        if (name == null) name = "A0@";
        else name += "@";
        
        //count moveable lines and put it in the front of the string
        ml = 0;
        for (ln = 0; ln < line.length; ln++) {if (line[ln].getMoving()) ml++;};
        name += ml + ",";
        
        for (ln = 0; ln < line.length; ln++) {
            if (line[ln].getMoving()) name += line[ln].summary(ln+1,",") + ",";
        }
        //remove the trailing "," if needed (should always be long enough)
        if (name.charAt(name.length()-1) == ',')
            name = name.substring(0,name.length()-1);
        
        return name;
    }
    
    //restores moveable lines according to the string generated by report()
    //(except, assumes the label@ has been removed from the string)
    public void restore(String s) {
        int [] ints = new int[100]; //should be able to do this without the hard array...
        String [] strs  = new String[10];
        int numberoflines; //number of lines
        int ln,tpts,c;
        
        Parseappletstring.parseString(s,ints,strs);
        numberoflines = ints[0];
        
        //trace.out("Number of lines to restore: "+numberoflines);
        c = 1; //counter for keeping track where we are in the array
        for (int i = 0; i < numberoflines; i++) //grab the lines
        {
            ln = ints[c++];
            tpts = ints[c++];
            //trace.out("Restoring ln"+ln+" with "+tpts+" total points");
            for (int pt = 0; pt < tpts; pt++) {
                //trace.out("("+ints[c]+","+ints[c+1]+") ");
                line[ln-1].point[pt].move(new Point(ints[c],ints[c+1]));
                c += 2;
            }
        }
        makeGraph(offg,plot);
    }
}


//applet

