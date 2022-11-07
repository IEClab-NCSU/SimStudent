package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.pact.Utilities.trace;

/*when the user enters an equation with more than one variable, we pop
  up one of these dialogs to let the user specify which variable s/he
  wants to be the target.*/

public class VariableChoiceDialog extends Dialog{
    private String[] varList; /*the possible choices for the target
                                variable*/
    private String choice = null; /*the choice of the user*/

    private CheckboxGroup group;
    private Checkbox[] checks;

    public VariableChoiceDialog(Frame parent,String[] varChoices){
        super(parent,"Target Variable?",true);
        varList = varChoices;

        makeLayout();
        pack();
    }

    /*creates the needed radio buttons and places them in the dialog*/
    private void makeLayout(){
        /*set things up*/
        int numVars = varList.length;

        checks = new Checkbox[numVars];
        group = new CheckboxGroup();

        /*the radio buttons go in their own panel with its own
          flowlayout*/
        Panel bp = makeButtonPanel();

        setLayout(new GridBagLayout());

        /*prompt at the top*/
        GridbagCon.viewset(this,new Label("Please choose the variable for which to solve:",Label.CENTER),
                           1,0,1,1,5,5,0,5);

        /*then the choices*/
        GridbagCon.viewset(this,bp,
                           0,1,2,1,5,5,0,5);

        /*and the OK button at the bottom*/
        Button b = createOkButton();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        ((GridBagLayout)getLayout()).setConstraints(b,gbc);
        add(b);
        /*GridbagCon.viewset(this,createOkButton(),
          2,2,1,1,5,5,0,5);*/
    }

    /*this basically does line-wrapping of the variable buttons*/
    public Panel makeButtonPanel(){
        int ROWLENGTH = 4;
        int numRows = (varList.length / ROWLENGTH) + (((varList.length % ROWLENGTH) == 0) ? 0 : 1);
        Panel bp = new Panel();
        bp.setLayout(new GridLayout(numRows,1));
        for(int i=0;i<numRows;i++){
            Panel rowPanel = new Panel();
            for(int j=0;(j < ROWLENGTH) && (i*ROWLENGTH+j < varList.length);j++){
                checks[i*ROWLENGTH+j] = new Checkbox(varList[i*ROWLENGTH+j],group,i==0 && j==0);
                rowPanel.add(checks[i*ROWLENGTH+j]);
            }
            bp.add(rowPanel);
            }
        //for(int i=0;i<numVars;i++){
        /*default to the first choice*/
        //checks[i] = new Checkbox(varList[i],group,i==0);
        //bp.add(checks[i]);
        //}

        return bp;
    }

    public void show(){
        /*align our upper-left corner with our parent's lower-left
          corner*/
        
        Container parent = getParent();
        Rectangle parentLoc = parent.getBounds();
        //trace.out("VCD: parent location: " + parentLoc);

        float myY = parentLoc.y + parentLoc.height;
        float myX = parentLoc.x;
        setLocation((int)myX,(int)myY);

        trace.out("VCD: my location: " + getBounds());

        super.show();
    }

    public String getChoice(){
        return choice;
    }

    /*pull the user's choice out of the checkboxgroup and store it in
      the member var 'choice'*/
    private void finalizeChoice(){
        /*since we start out with a default choice, we know that
          getSelectedCheckbox will never return null*/
        choice = group.getSelectedCheckbox().getLabel();
    }

    /*a button that, when pressed, stores the current selection and
      closes the dialog*/
    private Button createOkButton(){
	Button theButton = new Button("OK");
        theButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    finalizeChoice();
                    hide();
                }
            });
        return theButton;
    }
}
