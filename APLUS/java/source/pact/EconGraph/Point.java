package pact.EconGraph;

import java.awt.Color;
import java.util.Vector;

public class Point //2d points for graphing
{
    public int x;
    public int y;
    public boolean moveable, isCorrect;
    public boolean useTemp=false;
    public Color color, tempColor;
    String lineName;
    int pointNum;
    static Vector pointListeners = new Vector();
    int startX, startY;
    public boolean hasBeenMoved = false;
    
    Point(int xv, int yv, boolean move, Color tcolor, String lineName, int pointNum) {
        x = xv;
        y = yv;
        startX = x;
        startY = y;
        moveable = move;
        color = tcolor;
        this.lineName = lineName;
        this.pointNum = pointNum;
 //       trace.out (5, this, "point created, line = " + lineName + ", pointnum = " + pointNum
   //         + ", x = " + x + ", y = " + y);
    }
    
    
     public void setName(String newName){
        lineName=newName;
    }
     public String getName(){
         return lineName;
     }
        
    Point(int xv, int yv, boolean move, Color tcolor) {
        this(xv, yv, move, tcolor, "no name", -1);
    }
    
    Point(int x, int y, String lineName, int lineNum) {
        this(x, y, false, Color.black, lineName, lineNum);
    }
    
    public Point(int xv, int yv) {
        this(xv, yv, false, Color.black);
    }
    
    
    
    public static void addPointListener(PointListener p) {
        pointListeners.addElement(p);
    }
    
    public void move(Point p) {
        x = p.x;
        y = p.y;
        hasBeenMoved=true;
    }
    
    public boolean useTempColor(){
       return useTemp;
    }
    
    public void reset () {
        x = startX;
        y = startY;
    }
    
    public void saveResetState () {
        startX = x;
        startY = y;
    }
    
    public void updateListeners() {
        for (int i = 0; i < pointListeners.size(); i++) {
            PointListener p =  (PointListener) pointListeners.elementAt(i);
            p.updatePointListener(lineName, pointNum, x, y);
        }
    }
    public void setx(int x) {
        this.x = x;
    }
    
    public void sety (int y) {
        this.y = y;
    }
    
    int getx() {
        return x;
    }
    
    int gety() {
        return y;
    }
    
    void setMoveable(boolean move) {
        moveable = move;
    }
    
    boolean getMoveable() {
        return moveable;
    }
    
    public void setColor(Color tcolor) {
        color = tcolor;
    }
    
    public void setTempColor(Color tempC){
        tempColor=tempC;
    }
    
    Color getColor() {
        return color;
    }
    
    public boolean isGreen(){
        if(isCorrect)
            return true;
        else
            return false;
    }
    
    String showLabel() {
        return "("+x+","+y+")";
    }
    
    public String toString() {
        String s = "[pt]"+ ( moveable ? "M":"s");
        return s + "("+x+","+y+")";
    }
    
}//class



