package edu.cmu.old_pact.java.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;

import edu.cmu.old_pact.gridbagsupport.GridbagCon;

/*this class encapsulates a List display and a Queue containing
  the Strings displayed in that list.  Changes to the queue are
  propagated automagically to the List display*/

public class QueueListDisplay extends Queue{
    public static final int FULL = 1;
    public static final int MINIMAL = 2;

    protected Label title,count;
    protected Panel panel;
    protected SizeableList list;
    private int layoutType;

    public QueueListDisplay(){
        this(FULL);
    }

    public QueueListDisplay(int t){
        this("Quodlibet",t);
    }

    public QueueListDisplay(String title){
        this(title,FULL);
    }

    public QueueListDisplay(String title,int t){
        this(title,200,t);
    }

    public QueueListDisplay(String title,int width,int t){
        this(title,width,8,t);
    }

    public QueueListDisplay(String title,int width, int rows, int t){
        layoutType = t;
        panel = new Panel();
        list = new SizeableList(width,rows);
	list.setFont(new Font("helvetica", Font.PLAIN, 9));
        this.title = new Label(title);
        count = new Label();
        updateCount();
    }

    public void setLayoutType(int t){
        layoutType = t;
        doLayout();
    }

    public int getLayoutType(){
        return layoutType;
    }

    protected void doLayout(){
        switch(layoutType){
        case MINIMAL:
            panel.removeAll();
            panel.setLayout(new GridBagLayout());

            GridbagCon.viewset(panel,title, 0,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(panel,count, 1,0, 1,1, 5,5,0,5);

            panel.validate();
            break;
        case FULL:
            panel.removeAll();
            panel.setLayout(new GridBagLayout());

            GridbagCon.viewset(panel,title, 0,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(panel,count, 1,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(panel,list,  0,1, 2,1, 5,5,0,5);

            panel.validate();
            break;
        }
    }

    private void updateCount(){
        count.setText("(" + size() + ")");
    }

    public void push(Object o){
        list.add((String)o);
        super.push(o);
        updateCount();
    }

    public Object pop(){
        list.remove(0);
        Object o = super.pop();
        updateCount();
        return o;
    }

    /*don't need to override these because they don't actually
      change the queue*/
    //public Object peek();
    //public boolean empty();
    //public int search(Object o);

    public void refreshDisplay(){
        int s = size();
        list.removeAll();
        for(int i=0;i<s;i++){
            list.add((String)elementAt(i));
        }

        updateCount();
    }

    public Panel getPanel(){
        doLayout();
        return panel;
    }
}

/*either sun didn't provide a way to specify the preferred width for
  their List class, or I can't figure out what it is.  Regardless,
  this seems to work.  :) */

class SizeableList extends java.awt.List{
    int w;

    public SizeableList(int width,int rows){
        this(width,rows,false);
    }

    public SizeableList(int width,int rows,boolean multi){
        super(rows,multi);
        w = width;
    }

    public void setPreferredWidth(int width){
        w = width;
    }

    public Dimension getPreferredSize(){
        Dimension d = super.getPreferredSize();
        d.width = w;

        return d;
    }
}
