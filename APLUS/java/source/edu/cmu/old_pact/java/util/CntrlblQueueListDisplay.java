package edu.cmu.old_pact.java.util;

import java.awt.Button;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.toolagent.ControlledLispInterface;
import edu.cmu.old_pact.gridbagsupport.GridbagCon;

/*this class adds buttons which allow the user to move & remove
  messages in the queue*/

public class CntrlblQueueListDisplay extends QueueListDisplay{
    private Button moveUp,moveDown,remove,removeAll;

    public CntrlblQueueListDisplay(){
        this(FULL);
    }

    public CntrlblQueueListDisplay(int t){
        this("Quodlibet",t);
    }

    public CntrlblQueueListDisplay(String title){
        this(title,FULL);
    }

    public CntrlblQueueListDisplay(String title,int t){
        this(title,200,t);
    }

    public CntrlblQueueListDisplay(String title,int width,int t){
        this(title,width,8,t);
    }

    public CntrlblQueueListDisplay(String title,int width,int rows,int t){
        super(title,width,rows,t);

        createButtons();
    }

    private void createButtons(){
        moveUp = createMoveUpButton();
        moveDown = createMoveDownButton();
        remove = createRemoveButton();
        removeAll = createRemoveAllButton();
    }

    protected void doLayout(){
        switch(getLayoutType()){
        case MINIMAL:
            panel.removeAll();
            panel.setLayout(new GridBagLayout());

            GridbagCon.viewset(panel,title,          0,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(panel,count,          1,0, 1,1, 5,5,0,5);

            panel.validate();
            break;
        case FULL:
            panel.removeAll();
            panel.setLayout(new GridBagLayout());

            Panel buttonPanel = new Panel();
            buttonPanel.setLayout(new GridBagLayout());
            
            GridbagCon.viewset(buttonPanel,moveUp,   0,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(buttonPanel,remove,   0,1, 1,1, 5,5,0,5);
            GridbagCon.viewset(buttonPanel,removeAll,0,2, 1,1, 5,5,0,5);
            GridbagCon.viewset(buttonPanel,moveDown, 0,3, 1,1, 5,5,0,5);
            
            GridbagCon.viewset(panel,title,          0,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(panel,count,          1,0, 1,1, 5,5,0,5);
            GridbagCon.viewset(panel,list,           0,1, 2,1, 5,5,0,5);
            GridbagCon.viewset(panel,buttonPanel,    2,0, 1,2, 5,5,0,5);

            panel.validate();
            break;
        }
    }

    private Button createMoveUpButton(){
        Button theButton = new Button("Move Up");
        theButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    int index = list.getSelectedIndex();
                    if(index > 0){
                        String s1 = list.getItem(index);
                        String s2 = list.getItem(index-1);
                        list.replaceItem(s2,index);
                        list.replaceItem(s1,index-1);
                        list.select(index-1);

                        ControlledLispInterface.swapEls(CntrlblQueueListDisplay.this,
                                                        index,index-1);
                    }
                }
            });
        return theButton;
    }

    private Button createMoveDownButton(){
        Button theButton = new Button("Move Down");
        theButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    int index = list.getSelectedIndex();
                    if((index != -1) && (index < size()-1)){
                        String s1 = list.getItem(index);
                        String s2 = list.getItem(index+1);
                        list.replaceItem(s2,index);
                        list.replaceItem(s1,index+1);
                        list.select(index+1);
                        
                        ControlledLispInterface.swapEls(CntrlblQueueListDisplay.this,
                                                        index,index+1);
                    }
                }
            });
        return theButton;
    }
    
    private Button createRemoveButton(){
        Button theButton = new Button("Remove");
        theButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    int index = list.getSelectedIndex();
                    if(index != -1){
                        list.remove(index);
                        removeElementAt(index);
                    }
                }
            });

        return theButton;
    }

    private Button createRemoveAllButton(){
        Button theButton = new Button("Remove All");
        theButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    list.removeAll();
                    removeAllElements();
                }
            });

        return theButton;
    }
}
