package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.MenuItem;

import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.menu.DorminMenu;
import edu.cmu.old_pact.dormin.menu.DorminMenuItem;

public class SimplificationMenu extends DorminMenu
{
    private String[] legalActions = new String[]{"cm","clt","mt","rf","sc","distribute","fact"};

    public SimplificationMenu(ObjectProxy parent)
    {
        super("Simplification", parent, SolverFrame.getSelf());
    }

    public MenuItem add(MenuItem mi)
    {
        MenuItem realItem = ((SimplificationItem)mi).getRealItem();
        super.add(realItem);
        return mi;
    }

 
    public DorminMenuItem createMenuItem()
    {
        SimplificationItem simp = new SimplificationItem();
        simp.setMenu(this);
        setLegalActions(legalActions);
       
        return simp;
    }
}





