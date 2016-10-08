
package pact.CommWidgets;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;

import pact.EconGraph.GraphPanel;
import pact.EconGraph.Line;
import pact.EconGraph.ParameterProvider;
import pact.EconGraph.Point;
import pact.EconGraph.PointListener;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;


public class EconGraphingTool extends JCommWidget implements PointListener, ParameterProvider {
    
    protected GraphPanel graphPanel;
    protected String imageName;
    private Color oldColor;
    private int lastX, lastY, lastPointNum;
    private String lastLine, parameterString;
    private boolean initialized;
    private String[] parameters;
    private Hashtable parameterTable = new Hashtable();
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void updatePointListener(String lineName, int pointNum, int newX, int newY) {
        
        lastLine = lineName;
        lastX = newX;
        lastY = newY;
        lastPointNum = pointNum;
        dirty = true;
        sendValue();
        dirty = false;
    };
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public String getCommNameToSend() {
        //System.out.println(" in getCommNameToSend.  the commName is : " + commName + " the last Line is : " + lastLine + "and the last pointnum is" + lastPointNum);
        return new String(commName + "_" + lastLine + "_" + lastPointNum);
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void reset(TutorController controller) {
        
     /*   for(int a=0; a<graphPanel.line.length; a++){
            for(int b=0; b<graphPanel.line[a].point.length; b++){
                            graphPanel.line[a].point[b].useTemp=false;
            }
        }*/
        graphPanel.reset();
        
    }
    
    //////////////////////////////////////////////////////
    /**
     * Constructor
     */
    //////////////////////////////////////////////////////
    public EconGraphingTool() {
        super();
        setLayout(new java.awt.GridLayout(1,1));
        graphPanel = new GraphPanel();
        add(graphPanel);
        setBorder(BorderFactory.createEtchedBorder());
       /* try {
            trace.out(5, this, "setup parameter     = " + TutorApplet.instance().getParameter("label"));
        } catch (NullPointerException e) {
            trace.out(5, this, "error: can't find TutorApplet");
        }*/
        actionName = "UpdateEGT";
        Point.addPointListener(this);
    }
    
    //////////////////////////////////////////////////////
    /**
     * Returns a comm message which describes this interface
     * element.
     */
    //////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		if (!initialize()) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "EconGraphingTool");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));

		serializeGraphicalProperties(mo);

		return mo;
	}
    
    //////////////////////////////////////////////////////
    /**
     * Used to process an Interface Action message
     */
    //////////////////////////////////////////////////////
    public void doInterfaceAction(String selection, String action, String input) {
        if (action.equalsIgnoreCase("UpdateEGT")) {
            doCorrectAction(selection, input);
        }
        
        
    }
    
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void doCorrectAction(String selection, String input) {
        
        trace.out(5, this, "in doCORRECT");
        String line = getLine(selection);
        int point = getPoint(selection);
        int x = getX(input);
        int y = getY(input);
        //Color color=getColor(input);
        Line l = (Line) graphPanel.hashtable.get(line);
        //trace.out(5, this, "doCorrectAction: line name = " + line + " point num = " + point + " x = " + x + " y = " + y);
        //trace.out(5, this, "doCorrectAction: line =" + l );
        //trace.out(5, this, "point = " + l.point[point] );
        //        System.out.println("from the brd side: x, y is: " + x + ", " + y + "l.point[point] is: " + l.point[point]);
        trace.out(5, this, "selection = " + selection);
        trace.out(5, this, "input = " + input);
        trace.out(5, this, "line l = " + l);
        Point oldPoint = l.point[point];
        trace.out(5, this, "oldPoint" + oldPoint);
        oldPoint.setName(line);
                oldPoint.isCorrect=true;
        // System.out.println("the selection " + selection);
        // System.out.println("l.point[point].getName(): " + l.point[point].getName());
        // System.out.println("the line name youre on: " + line);
        // System.out.println("the line name youre sending: " + oldPoint.getName());
        
        if(graphPanel.getDragLine()){
            //       l.color=correctColor;
//            System.out.println("in here, the color is" + correctColor);
            for(int a=0; a<l.point.length; a++)
                l.point[a].color=correctColor;
            graphPanel.dragLine((new Point(x, y)), l.point[point], true);
            
        }
        else{
            l.point[point].move(new Point(x, y));
            if(lastX==l.point[point].x && lastY==l.point[point].y){//so, this makes it so that when you hit the start state, everything doesnt turn back green
                //l.point[point].color=correctColor;
                l.point[point].useTemp=true;
                l.point[point].setTempColor(correctColor);
            }
        }
        
        graphPanel.makeGraph();
    }
    
    
    public void doIncorrectAction(String selection, String input) {
        trace.out(5, this, "in doINCORRECT");
        
        
        String line = getLine(selection);
        int point = getPoint(selection);
        int x = getX(input);
        int y = getY(input);
        Line l = (Line) graphPanel.hashtable.get(line);
        //trace.out(5, this, "doCorrectAction: line name = " + line + " point num = " + point + " x = " + x + " y = " + y);
        //trace.out(5, this, "doCorrectAction: line =" + l );
        //trace.out(5, this, "point = " + l.point[point] );
        //        System.out.println("from the brd side: x, y is: " + x + ", " + y + "l.point[point] is: " + l.point[point]);
        
        
        
        Point oldPoint = l.point[point];
        
        oldPoint.setName(line);
        oldPoint.isCorrect=false;
        // System.out.println("the selection " + selection);
        // System.out.println("l.point[point].getName(): " + l.point[point].getName());
        // System.out.println("the line name youre on: " + line);
        // System.out.println("the line name youre sending: " + oldPoint.getName());
        
        
        if(graphPanel.getDragLine()){
            
            //            l.color=incorrectColor;
            for(int a=0; a<l.point.length; a++)
                l.point[a].color=incorrectColor;
            graphPanel.dragLine((new Point(x, y)), l.point[point], true);
        }
        else{
            l.point[point].move(new Point(x, y));
            
            l.point[point].color=incorrectColor;
        }
        
       
        graphPanel.makeGraph();
    }
    
    
    
    //////////////////////////////////////////////////////////////////
    private int getX(String input) {
        return Integer.valueOf(input.substring(0, input.indexOf(","))).intValue();
    }
    
    private int getY(String input) {
        return Integer.valueOf(input.substring(input.indexOf(", ") + 2, input.indexOf(": ")) ).intValue();
    }
    
    private String getLine(String selection) {
        return selection.substring(selection.indexOf("_") + 1, selection.lastIndexOf("_"));
    }
    
    private int getPoint(String selection) {
        return Integer.valueOf(selection.substring(selection.lastIndexOf("_") + 1, selection.length())).intValue();
    }
    
 /*   private Color getColor(String input){
        String x=input.substring(input.indexOf(": ") + 2, input.length());
        Color y=new Color( Integer.valueOf(x.substring(x.indexOf("[" + 1), x.indexOf(","))).intValue(),
                            Integer.valueOf(x.substring(x.indexOf(","), x.indexOf(",", x.indexOf(",")))).intValue() ,
                            Integer.valueOf( x.substring(x.indexOf(",", x.indexOf(",", x.indexOf(","))), x.indexOf("]"))).intValue()    );
        return y;
    }*/
    /**
     *
     */
    //////////////////////////////////////////////////////
    public String getParameter(String name) {
        /*
        if (TutorApplet.instance() != null) {
            String ret = TutorApplet.instance().getParameter(name);
            trace.out(5, this, "getParameter(" + name + ") = " + ret);
            return ret;
        }
         */
        if (parameters == null)
            throw new Error("Parameters property must be set.");
       
        return (String) parameterTable.get(name);
        
        
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public String getParameters() {
        return parameterString;
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
//////////////////////////////////////////////////////
    public void setParameters(String parameterString) {
        trace.out(5, this, "setting parameters to " + parameters);
        this.parameterString = parameterString;

    }
    
    public void parseParameters() {
        //parameterString="label:A1:setup:#orange,100,100,400,400,0,10,50,#testThing,0,10,50,#tewstthingto:nl:1:ln1:#supply,0,0,4,4,2,#pink,6,1,1,0";
        
        trace.out(5, this, "parameter string = " + parameterString);
       // if (TutorApplet.instance() != null)
       //     return;
        if (parameterString == null) {
        	trace.out (5, this, "Error: parameterString property of EconGraphingTool not set.  Cannot init this EconGraphingTool");
        	return;
        }
        
       // parameters =  parameterString.split(":");
        StringTokenizer tokenizer=new StringTokenizer(parameterString);
       parameters=new String[tokenizer.countTokens()];
        trace.out(5,this,"b " + tokenizer.countTokens());

//        for (int a=0; a<parameters.length; a++)
//        System.out.println(parameters[a]);
//        
//        System.out.println("klength" + parameters.length);
        
           while(tokenizer.hasMoreTokens() )
                 for(int k=0;k<parameters.length; k++){
                     parameters[k]=new String();
            parameters[k]=tokenizer.nextToken();    
            trace.out(5, this, "tokenizing " + parameters[k]);
           }
                
       
        System.out.print(parameters.length);
        for (int i = 0; i < parameters.length-1; i=i+2) {
            trace.out(5, this, "adding paramter " + parameters[i] + ", " + parameters[i+1]);
            parameterTable.put(parameters[i], parameters[i+1]);
                       
        }
        
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public boolean initialize() {
        
        if (initialized)
            return true;
        initialized = true;
        boolean b = super.initialize(getController());
        
        graphPanel.setParameterProvider(this);
        
        parseParameters();
        
        
        graphPanel.init();
        
        for (int i = 0; i < graphPanel.line.length; i++)
            for (int j = 0; j < graphPanel.line[i].point.length; j++)
                addCommWidgetName(commName + "_" + graphPanel.line[i].name + "_" + j);
      
                parseParameters();
        return b;
        
    }
    
    //////////////////////////////////////////////////////
    /**
     * Return true if the graphPanel has been moified by user
     */
    //////////////////////////////////////////////////////
    public boolean isChangedFromResetState() {
        
        return true;
    }
    
    //////////////////////////////////////////////////////
    /**
     * Called by the sendValue method.  Always returns the
     * value of the last point to get updated by the user.
     */
    //////////////////////////////////////////////////////
    public Object getValue() {
        //        System.out.println("get value, and last x is " + lastX + " and last y is : " + lastY);
        
        return new String("" + lastX + ", " + lastY +  ": " + oldColor);
    }
    
    //////////////////////////////////////////////////////
    /**
     * Creates a vector of comm messages which describe the
     * current state of this object relative to the start state
     */
    //////////////////////////////////////////////////////
    public Vector getCurrentState() {
        //        System.out.println("in getcurrentstate");
        
        Vector v = new Vector();
        
        for (int i = 0; i < graphPanel.line.length; i ++)
            for (int j = 0; j < graphPanel.line[i].point.length; j++) {
                //oldColor=graphPanel.line[i].getColor();
                
                if (graphPanel.line[i].point[j].hasBeenMoved) {
                    graphPanel.line[i].point[j].useTemp=false;
                    //System.out.println("PREUPDATE in getCurrentState.  the lastX is : " + lastX + " and the last y is: " + lastY +
                    //                    " and the lastLine is: " + lastLine + " and the lastPointNum is " + lastPointNum);
                    lastX=graphPanel.line[i].point[j].x;
                    lastY=graphPanel.line[i].point[j].y;
                    lastLine=graphPanel.line[i].name;
                    lastPointNum=j;
                    v.addElement(getCurrentStateMessage());
                    //                                        System.out.println("AFTER METHOD in getCurrentState.  the lastX is : " + lastX + " and the last y is: " + lastY +
                    //                                        " and the lastLine is: " + lastLine + " and the lastPointNum is " + lastPointNum);
                    break;
                }
            }
        
        return v;
    }
    
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void setToolTipText(String text) {
        graphPanel.setToolTipText(text);
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public String getToolTipText() {
        return graphPanel.getToolTipText();
    }
    
	public void mousePressed(MouseEvent e) {
	}
}
