package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.uiwidgets.CommandLineOkCancelDialog;
import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.menu.DorminMenuItem;

public class SimplificationItem extends DorminMenuItem implements ActionListener
{
    Menu submenu;
    private String action_name;
    MenuItem right;
    MenuItem left;
    MenuItem both;

    public SimplificationItem()
    {
        submenu = new Menu();

        right = new MenuItem("Right side");
        left = new MenuItem("Left side");
        both = new MenuItem("Both sides");
        
        right.setActionCommand("right");
        left.setActionCommand("left");
        both.setActionCommand("both");

        right.addActionListener(this);
        left.addActionListener(this);
        both.addActionListener(this);
        addActionListener(this);

        submenu.add(right);
        submenu.add(left);
        submenu.add(both);
    }

    public MenuItem getRealItem()
    {
        if((action_name.equalsIgnoreCase("fact"))||(action_name.equalsIgnoreCase("sc")))
            return this;
        else
            return submenu;
    }

    public void setProperty(String propertyName, Object propertyValue) throws DorminException
    {
        try{
            if(propertyName.equalsIgnoreCase("Label"))
            {
                String label = (String)propertyValue;
                submenu.setLabel(label);
                setLabel(label);
            }
            if(propertyName.equalsIgnoreCase("Name"))
            {
                action_name = ((String)propertyValue).toLowerCase();
                submenu.setActionCommand(action_name);
                super.setProperty(propertyName, propertyValue);
            }
            else
                super.setProperty(propertyName, propertyValue);
        } catch(DorminException e) {
            throw e;
        }
    }
        
    /*    protected void setOtherParam()
    {
    }*/

    public Menu getMenu()
    {
        return submenu;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (action_name.equalsIgnoreCase("fact"))
        {
            CommandLineOkCancelDialog dlog = new CommandLineOkCancelDialog((SolverFrame)getFrame(),
                                                                           "Factor out what expression?",true,action_name);
            dlog.show();  
        }
        else if (action_name.equalsIgnoreCase("sc"))
        {
            ((SolverFrame)getFrame()).performAction(action_name,null);
        }
        else
            SolverFrame.getSelf().performAction(action_name, e.getActionCommand()); 
    }

}
