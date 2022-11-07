package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.util.Vector;

import edu.cmu.old_pact.cmu.solver.uiwidgets.SolverMenuItem;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.menu.DorminMenu;
import edu.cmu.old_pact.dormin.menu.DorminMenuItem;
import edu.cmu.old_pact.toolframe.ToolFrame;
import edu.cmu.pact.Utilities.trace;


public class SolverMenu extends DorminMenu{
	private Vector operations;
	
	static String[] names = {	"add","subtract","multiply","divide","squareroot","cm",
                                        "clt","mt","rf","sc","distribute","fact",
								"erase","hint","done","new",
								"donenosolution", "doneinfinitesolutions"};
	static String[] actionTypes = {"0","0","0","0","2","2",
                                       "1","1","1","2","1","0",
								   "2","2","2","0",
								   "2","2"};
	static String[] isOperation = {"true","true","true","true","true","true",
                                       "true","true","true","true","true","true",
								   "false","false","false","false",
								   "false", "false"};
	static String[] promptText =  {"Add what to both sides?","Subtract what from both sides?",
								   "Multiply both sides by what?","Divide both sides by what?",
								   "","",
                                       "Combine like terms on which side?","Perform multiplication on which side?","",
								   "Reduce fractions on which side?","Distribute on which side?",
                                       "Factor out what expression?",
								   "","","","Enter the new equation",
								   "",""};

	/*these are the names of the methods in the SymbolManipulator that
      correspond to the menu operations.  A value of null indicates
      that there is not a single corresponding method.*/
	static String[] smOpName = {"add","subtract","multiply","divide",
								"squareroot",null,"combineLikeTerms",
								"multiplyThrough","reduceFractions",
								"substConstants","distribute","factor",
								null,null,null,null,null,null};

	String defPrompt = "Enter value:";							   
	private String[] legalActions = new String[]{	"add","subtract","multiply","divide", 
                                                        "squareroot","cm","clt","mt","rf","sc",
													"distribute","fact","erase","hint","done","new",
													"DoneNoSolution", "DoneInfiniteSolutions"};

    private static String[] transformationItems = new String[]{"add","subtract","multiply","divide","squareroot"};
    private static String[] simplificationItems = new String[]{"clt","mt","rf","distribute","fact","cm","sc"};
    private static String[] doneItems = new String[]{"DoneNoSolution","done","DoneInfiniteSolutions"};
    private boolean transformation_menu_created;
    private boolean simplification_menu_created;
    private boolean done_menu_created;
    Menu transformation_menu;
    Menu simplification_menu;
    Menu done_menu;

	public SolverMenu(String menuName, ObjectProxy parent, ToolFrame frame){
		super(menuName,parent,frame);
		operations = new Vector();
                transformation_menu_created = false;
                simplification_menu_created = false;
                done_menu_created = false;
                
                transformation_menu = null;
                simplification_menu = null;
                done_menu = null;
	}
	
    //overridden so that the solver can decide where to put menuitems
    public MenuItem add(MenuItem mi)
    {
        boolean found = false;

        //submenu for operations done to both sides of the equation
        for(int i=0;i<transformationItems.length;i++)
        {
            if(mi.getActionCommand().equalsIgnoreCase(transformationItems[i]))
            {
                if(!transformation_menu_created)
                {
                    createTransformationMenu();
                    transformation_menu_created = true;
                }
                transformation_menu.add(mi);
                found = true;
            }
        }
       
        // submenu for simplification actions
        for (int i=0;i<simplificationItems.length;i++)
        {
            if(mi.getActionCommand().equalsIgnoreCase(simplificationItems[i]))
            {
                if(!simplification_menu_created)
                {
                    createSimplificationMenu();
                    simplification_menu_created = true;
                }
                simplification_menu.add(mi);
                found = true;
            }
        }

        // done menu items go in a submenu under the Tutor menu
        // instead of the solver menu 
        for (int i=0;i<doneItems.length;i++)
        {
            if(mi.getActionCommand().equalsIgnoreCase(doneItems[i]))
            {
                if(!done_menu_created)
                {
                    createDoneMenu();
                    done_menu_created = true;
                }
                done_menu.add(mi);
                found = true;
            }
        }

        // undo menu item goes under the Edit menu
        if (mi.getActionCommand().equalsIgnoreCase("erase") )
        {
            MenuBar parent_menubar = (MenuBar)getParent();
            Menu edit_menu = getMenu("Edit", parent_menubar);

            if(edit_menu == null)
            {
                edit_menu = new Menu("Edit");
                parent_menubar.add(edit_menu);
            }
            
            edit_menu.insert(mi, 0);
            found = true;
        }
                
        if(!found)
            super.add(mi);

        return mi;
    }
    
    private Menu getMenu(String name, MenuBar parent)
    {
        Menu ret_menu = null;

        int num_menus;

        num_menus = parent.getMenuCount();
        for (int i=0;i<num_menus;i++)
        {
            Menu current_menu = parent.getMenu(i);
            if (current_menu.getName().equalsIgnoreCase(name))
                ret_menu = current_menu;
        }        
        
        return ret_menu;
    }

	public DorminMenuItem createMenuItem(){
		SolverMenuItem smi = new SolverMenuItem();
		smi.setMenu(this);
		setLegalActions(legalActions);
		return smi;		
	}
	
	public void delete(){
		operations.removeAllElements();
		operations = null;
                transformation_menu.removeAll();
                simplification_menu.removeAll();
                transformation_menu = null;
                simplification_menu = null;
                transformation_menu_created = false;
                simplification_menu_created = false;
		super.delete();
	}
	
	public void addOperationItem(SolverMenuItem item){
		operations.addElement(item);
	}
	public Vector getOperationVector(){
		return operations;
	}
	
	public String[] getOtherParam(String n){
		String[] toret = new String[3];
		
		int s = names.length;
		int pos = -1;
		for(int i=0; i<s; i++){
			if(names[i].equalsIgnoreCase(n)) {
				pos = i;
				break;
			}
		}
		try{
			toret[0] = actionTypes[pos];
			toret[1] = isOperation[pos];
			toret[2] = promptText[pos];
			if(toret[2].equals(""))
				toret[2] = defPrompt;
		}catch (ArrayIndexOutOfBoundsException e){
			trace.out("in SolverMenu can't find "+n);
		}
		return toret;
	}

	public String getSmOp(String name){
		for(int i=0;i<names.length;i++){
			if(name.equalsIgnoreCase(names[i])){
				if(i >= smOpName.length){
					trace.out("SM.gSO: ERROR: array index out of bounds getting SM op for " + name);
					return null;
				}
				else{
					return smOpName[i];
				}
			}
		}

		return null;
	}

    private void createTransformationMenu()
    {
        transformation_menu = new Menu("Transformation");
        insert(transformation_menu,0);
    }

    private void createSimplificationMenu()
    {
        simplification_menu = new Menu("Simplification");
        insert(simplification_menu,1);
    }

    private void createDoneMenu()
    {
        MenuBar parent_menubar = (MenuBar)getParent();
        Menu tutor_menu = getMenu("TUTOR", parent_menubar);
        
        if (tutor_menu == null)
        {
            tutor_menu = new Menu("Tutor");
            parent_menubar.add(tutor_menu);
        }
        
        done_menu = new Menu("Done");
        done_menu.setActionCommand("DoneSubmenu");
        tutor_menu.add(done_menu);
    }
}

