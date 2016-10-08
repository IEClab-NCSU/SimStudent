package edu.cmu.old_pact.cmu.uiwidgets;


import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.jal.String.BinaryPredicate;
import edu.cmu.old_pact.jal.String.Sorting;

public class EquationDialog extends CommandLineOkCancelDialog{
    String targetVar = null;
    Label tvLabel;

    public EquationDialog(ModalDialogListener parent,String title,boolean modal) {
        super(parent,title,modal);
        init();
    }

    public EquationDialog(ModalDialogListener parent,String title,boolean modal, String cmd) {
        super(parent,title,modal,cmd);
        init();
    }

    public EquationDialog(ModalDialogListener parent,String title,boolean modal,
                          String cmd, String text) {
        this(parent,title,modal,cmd);
        tf.setText(text);
    }

    private void init(){
        //throw away the components added by the superclass and start fresh
        removeAll();
        setLayout(new GridBagLayout());
        
        //setBackground(new Color(204, 204, 204));
        setBackground(Color.lightGray);

        tf = WidgetFactory.makeTextField(15);
        //targetVar = WidgetFactory.makeTextField(2);
        
        promptLabel = WidgetFactory.makeLabel("Enter equation:");
        //tvLabel = WidgetFactory.makeLabel("Solve for:");

        GridbagCon.viewset(this,promptLabel,0,0,1,1,20,25,0,0);
        GridbagCon.viewset(this,tf,1,0,2,1,20,0,0,25); 
        /*GridbagCon.viewset(this,tvLabel,0,1,1,1,20,25,0,0);
          GridbagCon.viewset(this,targetVar,1,1,1,1,20,0,0,25);*/
        
        Panel bottom = WidgetFactory.okCancelPanel();
        GridbagCon.viewset(this,bottom,1,2,2,1,20,0,15,25);
        
        pack();
        //setVisible(true);
    }

    /*if there is more than one variable in the entered equation, we
      pop up a VariableChoiceDialog to see which one the user wishes
      to have as the target*/
    public void finishModalDialog(boolean key)
    {
        if(!key){
            /*cancel, so we don't care if there is a well-defined
              variable or not*/
            super.finishModalDialog(key);
        }
        else if(!(tf.getText().trim()).equals("")){
            targetVar = getTargetVar(tf.getText());
            super.finishModalDialog(key);
        }
    }

    /*parses the equation 'eqn'.  If it only contains one variable,
      returns that variable.  If it contains more than one, queries
      the user to choose one and returns that choice.  If it contains
      no variables or does not parse, returns null.*/
    private String getTargetVar(String eqn){
        String[] oldVarList = SymbolManipulator.getVarList();;

        try{
            Vector vars = new Vector();

            /*we want everything to count as a variable for this parse*/
            SymbolManipulator.forgetVarList();
            Equation e = new Equation(eqn);

            /*first, get the vars on the left*/
            Expression ls = (Expression)e.getProperty("left");
            Queryable[] lvars = (Queryable[])(ls.getProperty("variables").getArrayValue());
            for(int i=0;i<lvars.length;i++){
                vars.addElement(lvars[i]);
            }

            /*then the right side*/
            Expression rs = (Expression)e.getProperty("right");
            if(rs == null){
                /*'eqn' only parsed because it's a well-formed
                  expression, but it's not an equation*/
                /*put the variable settings back the way we found them*/
                SymbolManipulator.setVarList(oldVarList);
                return null;
            }
            Queryable[] rvars = (Queryable[])(rs.getProperty("variables").getArrayValue());
            for(int i=0;i<rvars.length;i++){
                vars.addElement(rvars[i]);
            }

            /*put the variable settings back the way we found them*/
            SymbolManipulator.setVarList(oldVarList);

            if(vars.size() == 0){
                /*no variables in the equation*/
                return null;
            }
            else if(vars.size() == 1){
                /*exactly one variable in the equation: return it*/
                return ((Queryable)vars.elementAt(0)).getStringValue();
            }
            else{
                /*more than one variable: ask the user*/
                /*we need a string array to sort & pass to the dialog*/
                String[] varStrings = new String[vars.size()];
                for(int i=0;i<vars.size();i++){
                    varStrings[i] = ((Queryable)vars.elementAt(i)).getStringValue();
                }
                Sorting.sort(varStrings,0,varStrings.length,new StringComparer());
                varStrings = StringComparer.removeDupes(varStrings);

				/*removeDupes() might have left us with only one variable ...*/
				if(varStrings.length > 1){
					VariableChoiceDialog vcd = new VariableChoiceDialog((Frame)parent,varStrings);
					vcd.show();
					return vcd.getChoice();
				}
				else{
					return varStrings[0];
				}
            }

            //if(v.getStringValue().equals(vars[i].getStringValue())){
        }
        catch(NoSuchFieldException nsfe){
            /*put the variable settings back the way we found them*/
            SymbolManipulator.setVarList(oldVarList);

            System.out.println("EquationDialog.getTargetVar failed: " + nsfe);
            return null;
        }
        catch(BadExpressionError bee){
            /*put the variable settings back the way we found them*/
            SymbolManipulator.setVarList(oldVarList);

            return null;
        }
    }

    public String getArgument(){
        if(targetVar != null){
            return getText() + ";" + targetVar;
        }
        else{
            return getText();
        }
    }
}

class StringComparer implements BinaryPredicate{
    public StringComparer(){;}

    public boolean apply(String x,String y){
        if(x == null || y == null){
            return false;
        }

        if(x.compareTo(y) < 0){
            return true;
        }
        else{
            return false;
        }
    }

    /*remove duplicates from the sorted array s; returns a new (&
      probably shorter) copy of s*/
    public static String[] removeDupes(String[] s){
        Vector v = new Vector();

        v.addElement(s[0]);
        int j=0;
        for(int i=1;i<s.length;i++){
            if(!s[i].equals((String)(v.elementAt(j)))){
                v.insertElementAt(s[i],++j);
            }
        }

        String[] ret = new String[v.size()];
        v.copyInto(ret);

        return ret;
    }
}
