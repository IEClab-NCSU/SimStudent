package edu.cmu.old_pact.cmu.solver.uiwidgets;

import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.cmu.uiwidgets.SolverMenu;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.menu.DorminMenu;
import edu.cmu.old_pact.dormin.menu.DorminMenuItem;

public class TransformationMenu extends DorminMenu
{
    private String[] legalActions = new String[]{	"add","subtract","multiply","divide", 
                                                        "squareroot","cm","clt","mt","rf","sc",
                                                        "distribute","fact","erase","hint","done","new",
                                                        "DoneNoSolution", "DoneInfiniteSolutions"};

    SolverMenu parent_menu;

    public TransformationMenu(ObjectProxy parent)
    {
        super("Transformation", parent, SolverFrame.getSelf());

        //parent_menu = (SolverMenu)parent.getObject();
    }

    public SolverMenu getSolverMenu()
    {
        return parent_menu;
    }

    public DorminMenuItem createMenuItem()
    {
        SolverMenuItem smi = new SolverMenuItem();
        smi.setMenu(this);
        setLegalActions(legalActions);
        return smi;
    }
        
}
