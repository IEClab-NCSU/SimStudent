package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.solver.SolverTutor;
import edu.cmu.old_pact.cmu.solver.ruleset.RuleMatchInfo;
import edu.cmu.old_pact.cmu.solver.uiwidgets.EquationPlusStepPanel;
import edu.cmu.old_pact.cmu.solver.uiwidgets.GoalPanel;
import edu.cmu.old_pact.cmu.solver.uiwidgets.PanelParameters;
import edu.cmu.old_pact.cmu.solver.uiwidgets.SolverPanelFactory;
import edu.cmu.old_pact.cmu.tutor.TranslatorProxy;
import edu.cmu.old_pact.cmu.tutor.TutoredTool;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;
import edu.cmu.pact.Utilities.trace;

public class SolverFrame extends DorminToolFrame implements 	Sharable, 
														TutoredTool,
														ModalDialogListener {

    // also used on Tutor side TRESolverTutor
    static String SEND_NEW_EQUATION_PROP = "SendNewEquation";
    static String SEND_SKILLS = "SendSkills";
    static String ALLOW_USER_NEW_EQUATION = "AllowUserNewEquation";
    // STUB add property for whether or not to request new problem
    static String UPDATE_SKILL = "UpdateSkill";
    static String SUBTYPE_PROP = "Subtype";

	MessageString m_Message=new MessageString();
	MessageString m_WarningMessage=new MessageString();
	
	Panel m_mainPanel=null;
	ScrollPanelClient proofPanel = null;
	GoalPanel goalPanel = null;
	private SolverMenuBar m_MenuBar=null;
	
	private Hashtable matchOperations;
	
	SymbolManipulator sm=null;
	Vector equations = new Vector();
	private boolean validEquation = false;
	private boolean isDone = false;
	
	int currentStepNumber = 0;
	private boolean useTypein = false;
	private boolean lastWasDone = false;
	
	public static int STEPCOMPLETED = 0;
	public static int LEFTNOTSET = 1;
	public static int RIGHTNOTSET = 2;
	
	public static String codeBase=null;
	
    public static java.awt.Color stdColor = new Color(50, 70, 50); //new Color(143, 166, 154);
    //public static Color stdColor = Color.white;//new Color(0,128,0); //new Color(143, 166, 154);
    //public static Color stdColor = new Color(51,102,51);//web safe
	
	private static final Color goalBgColor = stdColor;
	//private static final Color goalFgColor = new Color(200,200,100);//yellow chalk
	private static final Color goalFgColor = new Color(159,202,223);//blue chalk

	private int maxExLength = 10; //max number of characters in expression (OK if exceeded -- just for display of equation)
    // internal proxy to talk to SolverTutor, not to TRE side
	private TranslatorProxy trans;
	
	private SolverProxy sProxy;
	SolverMenu dMenu = null;
	private static SolverFrame mySelf=null; //there's only one of me

    // property that tells whether or not to include the new equation button
    private boolean allow_new_equation = false;
    // property that determines whether or not to send new equation messages to TRE tutor.   
    private boolean send_new_equation_message = false;
    // property that determines whether or not to send solver skills back to TRE
    private boolean send_skills = false;
    // STUB need property for whether or not to request new problem.

	public SolverFrame (String name) {
		super(name);
		trace.out (5, this, "new solverframe");
        System.out.println("New SolverFrame");
        System.out.println("Current thread: " + Thread.currentThread().getName());
		mySelf=this;
		setLayout(new BorderLayout());
		setBackground(stdColor);
		
		setResizable(true);
		proofPanel = new ScrollPanelClient();
		proofPanel.setLayout(new StackLayout(2));
		proofPanel.setBackground(SolverFrame.stdColor);
		LightComponentScroller lcs = new LightComponentScroller(proofPanel);

		PanelParameters goalParams = new PanelParameters(400,50,
														 new Font("Georgia", Font.PLAIN, 18),
														 goalFgColor,goalBgColor);
		goalPanel = new GoalPanel(goalParams);
		goalPanel.setInstructions("Awaiting equation ...");
		/*goalPanel.setSize(goalPanel.getMaximumSize().width,
		  goalPanel.getMinimumSize().height);*/

		m_mainPanel = new Panel();
		GridBagLayout gbl = new GridBagLayout();
		m_mainPanel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		gbl.setConstraints(goalPanel,gbc);
		m_mainPanel.add(goalPanel);

		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.weighty = 1;
		gbl.setConstraints(lcs,gbc);
		m_mainPanel.add(lcs);

		m_mainPanel.setBackground(stdColor);
		proofPanel.setBackground(stdColor);
		
		add("West",m_ToolBarPanel);
		setupToolBar(m_ToolBarPanel); 
		add("Center",m_mainPanel);
		

		sm = new SymbolManipulator();
                /*settings as specified by Rox, 12/20/00*/
                sm.autoStandardize = false;
                sm.autoSimplify = true;
                sm.autoDistribute = false;
                sm.autoSort = true;
		sm.distributeDenominator = true;
		sm.setOutputType(SymbolManipulator.intermediateOutput); //produce ascii with implied parens
		
		m_MenuBar = new SolverMenuBar();
		setMenuBar(m_MenuBar);

		matchOperations = new Hashtable();
		matchOperations.put("clt", "Combine like terms");
		matchOperations.put("mt", "Perform multiplication");
		matchOperations.put("rf", "Reduce fractions");
                matchOperations.put("fact", "Factor");
                matchOperations.put("sc", "Substitute Constants");
		

                setProperty("isApplication", Boolean.valueOf("false"));
		            setProperty(SUBTYPE_PROP, "STANDALONE");
		            setProperty("TYPEINMODE", "false");
		            setProperty("AutoSimplify", "false");
		
		/*pack();
		  setLocation(50,50);*/
		
		pack();
		setCurrentWidth(400);
		setCurrentHeight(320);
		Point p = new Point(50, 50);
		setCurrentLocation(p);
		
		setLocation(p);
		setSize(400,320);		
		updateSizeAndLocation(name);
	}
	
	public void setRealObject() {
		sProxy.setRealObject(this);
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		sProxy = (SolverProxy)op;
		setToolFrameProxy(op);
	}
	
	public void setupToolBar(ToolBarPanel tb) {
		tb.setBackground(Settings.solverToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
		tb.addButton(Settings.help,"Hint", true);
		tb.addSeparator();
                
                tb.addButton(Settings.undo,"Erase", true);
                /*Olga's change               
                tb.addButton(Settings.equation,"New Equation", true);
                tb.addSeparator();
                tb.addButton(Settings.undo,"Erase", true);
                */
                tb.addSeparator();

		tb.addToolBarImage(Settings.solverLabel,Settings.solverLabelSize);
	}	
		
	public void addMenu(String menuName){
            	if(menuName.equalsIgnoreCase("PREFERENCES"))
                    m_MenuBar.addDebugMenuItems();
		else {
		    dMenu = new SolverMenu(menuName, sProxy, this);
                    m_MenuBar.add((Menu)dMenu);
		}
	}
	
	public MenuBar getMenuBar(){
		return m_MenuBar;
	}
	
	public ObjectProxy getObjectProxy() {
		return sProxy;
	}

	public void setCodeBase(String cb){
		codeBase = cb;
	}
	
	public void delete(){
		clearAll();
		sProxy = null;
		sm = null;
		equations = null;
		validEquation = false;
		matchOperations.clear();
		matchOperations = null;
		remove(m_mainPanel);
		m_mainPanel.removeAll();
		m_mainPanel = null;
		proofPanel.removeAll();
		proofPanel = null;
		goalPanel.removeAll();
		goalPanel = null;
		m_MenuBar.delete();
		m_MenuBar = null;
		dMenu = null;
		super.delete();
		mySelf=null;
		trans = null;
		
	}
	
	public void requestFocus(){
		super.requestFocus();
		m_mainPanel.requestFocus();
	}
	
	public void actionPerformed(ActionEvent e){
            System.out.println("SolverFrame actionPerformed: " + e.getActionCommand());
		String command = e.getActionCommand();
		if(command.equalsIgnoreCase("HELP") ||
                   command.equalsIgnoreCase("HINT")){
                    performAction("hint", null);}
		else if(command.equalsIgnoreCase("QUIT") ){
			boolean isApp = false;
			isApp = ((Boolean)getProperty("isApplication")).booleanValue();
			if(isApp)
				System.exit(0);
		}
                else if(command.equalsIgnoreCase("EQUATION") || 
                		command.equalsIgnoreCase("NEW EQUATION"))
                {
                    EquationDialog dlog = new EquationDialog(this,
                                                             "Enter the new equation",true,"new");
                    dlog.setPromptLabel("Enter equation:");
                    dlog.show(); 
                }
                else if(command.equalsIgnoreCase("ERASE"))
                {
					performAction(command,null);
                }
		else
			super.actionPerformed(e);
	}		

	public void performAction(String command, String msg){
		if(performActionInternal(command,msg)){
			/*can't do this inside of performActionInternal because
              it's synchronized, and this calls back into
              performAction*/
                    
                    //SMILLER maybe this should do a sendDone() instead?
                                        sendDone();
                    
                    //    suggestNewProblem();
		}
		repaint();
	}

	/*returns true if a done action is successful -- i.e., if we need
      to ask the user for a new equation*/
	private synchronized boolean performActionInternal(String command, String msg) {
		boolean ret = false;
		trans.hideMessage();
		
		String currentLeft="";
		String currentRight="";
		String newLeft="";
		String newRight="";

		if(equations.size() != 0 && !command.equalsIgnoreCase("left") && !command.equalsIgnoreCase("right")){
			EquationPlusStepPanel currentEquation = (EquationPlusStepPanel)(equations.lastElement());
			currentLeft = currentEquation.leftSide();
			currentRight = currentEquation.rightSide();
			newLeft = currentLeft;
			newRight = currentRight;
		}

		if(!validEquation && !command.equalsIgnoreCase("new")){
			/*if there is no current equation, the only allowable action is "new"*/
			trans.showMessages("",new String[] {"You must enter an equation to solve before you can perform any operations.",
												"Select 'New Equation ...' from the menu."},"Error");
			trans.startNextStep(command);
			return ret;
		}

		if(isDone && !(command.equalsIgnoreCase("new") ||
					   command.equalsIgnoreCase("erase"))){
			/*once you've solved an equation, all you can do is start
              a new one.  ...and I suppose you can undo your
              declaration that you're done, if you're feeling
              masochistic*/
			trans.showMessages("",
							   new String[] {"You have already solved the equation.",
											 "To start solving another equation, select 'New Equation ...' from the menu."},
							   "Error");
			trans.startNextStep(command);
			return ret;
		}

		/*don't allow any 'done' actions when we're in the middle of a
          typein step.  (normal transformations are disabled on the
          menu, so we don't need to check for those)*/
		if(equations.size() > 0){
			if((((EquationPlusStepPanel)(equations.lastElement())).getStepState() != STEPCOMPLETED) &&
			   (command.length() >= 4 && command.substring(0,4).equalsIgnoreCase("done"))){
				trans.showMessages("",
								   new String[] {"You must complete the current step before moving on."},
								   "Error");
				trans.startNextStep(command);
				return ret;
			}
		}

		if(command.equalsIgnoreCase("multiply") ||
		   command.equalsIgnoreCase("divide")){
			try{
				/*explicitly disallow multiplication and division by
                  zero*/
				if(sm.algebraicEqual(msg,"0")){
					if(msg.equals("0")){
						trans.showMessages("",
										   new String[] {"You cannot " +
														 command.toLowerCase() +
														 " by zero"},
										   "Error");
					}
					else{
						trans.showMessages("",
										   new String[] {msg +
														 " is equal to 0.  You cannot " +
														 command.toLowerCase() +
														 " by zero"},
										   "Error");
					}
					trans.startNextStep(command);
					return ret;
				}
				/*also disallow division by an expression containing
                  the target var, because it might be == 0*/
				else if((command.equalsIgnoreCase("divide")) &&
						(Integer.valueOf(sm.runScript("length of variables",msg)).intValue() > 0)){
					trans.showMessages("",
									   new String[] {"Division by zero is undefined. Since <expression>" +
													 msg +
													 "</expression> might be equal to zero, choose a different operation."},
									   "Error");
					trans.startNextStep(command);
					return ret;
				}
			}
			catch(BadExpressionError bee){
				System.out.println("Bad argument to " + command + " ...");
			}
			catch(NoSuchFieldException nsfe){
				System.out.println("SolverFrame.performAction (div by zero check): " + nsfe);
			}
		}

		// Run the tutor on the user's input.
		boolean match = false;
		String usermsg = msg; // Save the user's typed input for display in step commentary
		RuleMatchInfo a = null;
		if (!(command.equalsIgnoreCase("left")) && !(command.equalsIgnoreCase("right"))) {
			a = trans.runTutor(String.valueOf(currentStepNumber),command,msg);
			if (a != null && a.getBoolean() == true) { // we matched a rule
				match = true;
				if ((a.getAction()).equalsIgnoreCase(command)) { // only the argument changed, so use it in calculation
					msg = a.getInput();
				}
				if(command.length() >= 4 && command.substring(0,4).equalsIgnoreCase("done")){
					ret = true;
				}
			}
		}
		
		if (command.equalsIgnoreCase("new")) {
                    // if the equation (i.e. msg) is blank, then the equation that was sent 
                    // was not the correct one and the tutor sent back a message
                    // to set the equation to nothing. In this case we don't want
                    // to do any solving, we just want the solver to be in a blank
                    // state so the student can enter the correct equation. 
                    if (msg.equals(""))
                    {
                        clearAll();
                    }
                    else
                    {
						boolean goodeq=false;
						try{
							int semicolonPos = msg.indexOf(';');
							String eq = msg;
							if(semicolonPos > 0){
								eq = msg.substring(0,semicolonPos);
							}
							Equation e = new Equation(eq);
							if(e.getRight() != null){
								goodeq = true;
							}
						}
						catch(BadExpressionError bee){}
						if(goodeq){
							/*startProblem() and
							  startSolvingEquation() have to come in
							  this order so that when we display the
							  equation here on the frame side, the
							  tutor has already told the parser the
							  variable for which we are solving.  That
							  way any special formatting that comes
							  from the toMathML() methods in the
							  symbolmanipulator is appropriate by the
							  time it is used to actually display the
							  equation.*/
							trans.startProblem(msg);

							// if we're to check this equation with the tutor
							// then send the equation property over
							if (send_new_equation_message)
								{
									// strip off the ;var
									String equation;
									int semicolon_pos = msg.indexOf(';');
									
									if (semicolon_pos != -1)
										equation = msg.substring(0,semicolon_pos);
									else
										equation = msg;
									
									sendNotePropertySet("Equation", equation);
								}
						}
						else{
							System.out.println("new equation " + msg + " is invalid");
						}
						/*this will pop up another new equation dialog
                          if the equation does not parse*/
						startSolvingEquation(msg);
					}
		}
		else if (command.equalsIgnoreCase("add")) {
			try {
				newLeft = sm.add(currentLeft,msg);
				newRight = sm.add(currentRight,msg);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad argument to add...");
			}
		}
		else if (command.equalsIgnoreCase("subtract")) {
			try {
				newLeft = sm.subtract(currentLeft,msg);
				newRight = sm.subtract(currentRight,msg);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad argument to subtract...");
			}
		}
		else if (command.equalsIgnoreCase("multiply")) {
			try {
				newLeft = sm.multiply(currentLeft,msg);
				newRight = sm.multiply(currentRight,msg);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad argument to multiply...");
			}
		}
		else if (command.equalsIgnoreCase("divide")) {
			try {
				newLeft = sm.divide(currentLeft,msg);
				newRight = sm.divide(currentRight,msg);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad argument to divide...");
			}
		}
		else if (command.equalsIgnoreCase("squareroot")) {
			try {
				newLeft = sm.squareroot(currentLeft);
				newRight = sm.squareroot(currentRight);
		//		System.out.println("in squareroot command: "+newLeft+" "+newRight);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad expression in squareroot...");
			}
		}
		
		// ALLEN
		else if (command.equalsIgnoreCase("cm")) {
			try {
				if (match) {  // matched the cross multiply rule, so perform cross multiply
					newLeft = sm.crossMultiplyLeft(currentLeft,currentRight);
					newRight = sm.crossMultiplyRight(currentLeft,currentRight);
					updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
				}
				else {  // did not match the cross multiply rule, so do not alter the equation
					newLeft = currentLeft;
					newRight = currentRight;
					updateDisplayForStep(command,"noOp",usermsg,newLeft,newRight);
				}
			}
			catch (BadExpressionError err) {
				System.out.println("Bad argument in cross multiply...");
			}
		}
		// end ALLEN

		else if (command.equalsIgnoreCase("clt")) {
			try {
		//		System.out.println("combining terms on "+msg);
				if (msg.equals("left") || msg.equals("both"))
					newLeft = sm.combineLikeTerms(currentLeft);
				if (msg.equals("right") || msg.equals("both"))
					newRight = sm.combineLikeTerms(currentRight);
		//		System.out.println("in CLT, "+newLeft+"="+newRight);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad expression in CLT...");
			}
		}

		else if (command.equalsIgnoreCase("mt")) {
			try {
			//	System.out.println("multiplying through on "+msg);
				if (msg.equals("left") || msg.equals("both")){
					if(sm.canDistribute(currentLeft))
						newLeft = sm.distribute(currentLeft,true);
					else
						newLeft = sm.multiplyThrough(currentLeft);
				}
				if (msg.equals("right") || msg.equals("both")) {
					if(sm.canDistribute(currentLeft))
						newRight = sm.distribute(currentRight,true);
					else
						newRight = sm.multiplyThrough(currentRight);
				}
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad expression in MT...");
			}
		}

		else if (command.equalsIgnoreCase("rf")) {
			try {
			//	System.out.println("reducing fractions on "+msg);
				if (msg.equals("left") || msg.equals("both"))
					newLeft = sm.reduceFractions(currentLeft);
				if (msg.equals("right") || msg.equals("both"))
					newRight = sm.reduceFractions(currentRight);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad expression in ReduceFractions...");
			}
		}

                else if (command.equalsIgnoreCase("sc")){
                    try{
                        newLeft = sm.substConstants(currentLeft);
                        newRight = sm.substConstants(currentRight);
                        updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
                    }
                    catch(BadExpressionError err){
                        System.out.println("Bad expression in SubstConstants...");
                    }
                }

		else if (command.equalsIgnoreCase("distribute")) {
			/*we have to temporarily turn off autoCLT, because if it's
              on then the distribution we're about to perform is in
              danger of being undone*/
			String oldSMState = sm.getState();
			if(sm.autoStandardize){
				sm.setIndividualAutoStand(true);
			}
			else if(sm.autoSimplify){
				sm.setIndividualAutoSimp(true);
			}
			sm.autoStandardize = false;
			sm.autoSimplify = false;
			sm.autoCombineLikeTerms = false;
			try {
				String 	newLeftDisp = newLeft, 
						newRightDisp = newRight;
				//System.out.println("distributing on "+msg);
				if (msg.equals("left") || msg.equals("both")) {
					newLeft = sm.distribute(currentLeft,true);
					newLeftDisp = sm.distributeOne(currentLeft,true);
				}
				if (msg.equals("right") || msg.equals("both")){
					newRight = sm.distribute(currentRight,true);
					newRightDisp = sm.distributeOne(currentRight,true);
				}
				EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.lastElement());
				lastEq.setInternalEquationString(newLeftDisp, newRightDisp);
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
				lastEq.setInternalEquationString(currentLeft, currentRight);
			}
			catch (BadExpressionError err) {
				System.out.println("Bad expression in Distribute...");
			}
			finally{
				sm.setState(oldSMState);
			}
		}
		else if (command.equalsIgnoreCase("fact")) {
			try{
				if(sm.canFactorPiecemeal(currentLeft,msg)){
					newLeft = sm.factorPiecemeal(currentLeft,msg);
				}
				else{
					newLeft = currentLeft;
				}
				if(sm.canFactorPiecemeal(currentRight,msg)){
					newRight = sm.factorPiecemeal(currentRight,msg);
				}
				else{
					newRight = currentRight;
				}
				updateDisplayForStep(command,msg,usermsg,newLeft,newRight);
			}
			catch(BadExpressionError err){
				System.out.println("Bad expression in Factor...");
			}
		}
		else if (command.equalsIgnoreCase("erase")) {
			clearLast();
		}
		else if (command.equalsIgnoreCase("Left")) 
			setSideExpression(msg,"left");
		
		else if (command.equalsIgnoreCase("Right")) 
			setSideExpression(msg,"right");
			
		else if (	command.equalsIgnoreCase("done") || 
					command.equalsIgnoreCase("DoneNoSolution") ||
					command.equalsIgnoreCase("DoneInfiniteSolutions")) {
                             if(lastStepState() == STEPCOMPLETED){
                                 insertStep(command,msg,usermsg);
                                 validate();
                                 lastWasDone = true;
			}
		}
		else if (command.equalsIgnoreCase("hint")) {
		}
		else 
			System.out.println("Don't understand command: "+command);

		if (command.equalsIgnoreCase("Left") || command.equalsIgnoreCase("Right")) 
			trans.recordStep(command+" of "+String.valueOf(currentStepNumber),"typein",msg);
		else {
			if(	command.equalsIgnoreCase("done") ||
				command.equalsIgnoreCase("DoneNoSolution") ||
				command.equalsIgnoreCase("DoneInfiniteSolutions")){
				trans.recordStep(String.valueOf(currentStepNumber),command,msg);
				return ret;
			}
			if(lastStepState() == STEPCOMPLETED ||
				(lastStepState() != STEPCOMPLETED &&
				(!command.equalsIgnoreCase("new") &&
				!command.equalsIgnoreCase("done") &&
				!command.equalsIgnoreCase("DoneNoSolution") &&
				!command.equalsIgnoreCase("DoneInfiniteSolutions")))){
				trans.recordStep(String.valueOf(currentStepNumber-1),command,msg);
				if(!command.equalsIgnoreCase("hint")) 
					setCurrentAction(command, msg);
			}
			else 
				trans.recordStep(String.valueOf(currentStepNumber-2),"stepNotCompleted",msg);
		}
		if (!command.equalsIgnoreCase("Left") && !command.equalsIgnoreCase("Right") && !command.equalsIgnoreCase("hint")) {
			trans.startNextStep(command);
		}

		return ret;
	}

	/*msg is a semicolon-separated list of 1 to 3 strings.  The first
      (required) string is the equation to solve.  The second
      (optional) string is the variable to solve for.  The third
      (optional) string is the text to display as the goal of the
      current problem.  The second string is ignored here unless the
      third string is absent, in which case the variable is used to
      construct the generic prompt "Solve for [variable]".  If both
      the second and third strings are absent, the target variable is
      guessed (by calling a function in SolverTutor) and that guess is
      used to construct the "Solve for [variable]" prompt*/
	public void startSolvingEquation(String msg){
		clearAll();
		String goalText = "";

		int equalPos = msg.indexOf('=');

		int semicolonPos = msg.indexOf(';');
		if(semicolonPos > 0){
			int semicolonPos2 = msg.indexOf(';',semicolonPos+1);
			if(semicolonPos2 == -1){
				goalText = "Solve for " + msg.substring(semicolonPos+1);
			}
			else{
				goalText = msg.substring(semicolonPos2+1);
			}
		}
		else{
			try{
				Equation test = new Equation(msg);
				String var = SolverTutor.guessTargetVar(test);
				goalText = "Solve for " + var;
			}
			catch(BadExpressionError bee){
				;
			}
			semicolonPos = msg.length();
		}

		/*display the goalText*/
		goalPanel.setInstructions(goalText);
		validate();
		setValidEquation(msg.substring(0,semicolonPos));

		if (equalPos > 0) 
			startSolvingEquation(msg.substring(0,equalPos),msg.substring(equalPos+1,semicolonPos));
		else
			//System.out.println("MUST GIVE EQUATION"); //should show error msg to user
			startSolvingEquation(msg.substring(0,semicolonPos),"?");
	}

	private void setValidEquation(String eq){
		try{
			Equation e = new Equation(eq);
			if(e.getRight() == null){
				validEquation = false;
			}
			else{
				validEquation = true;
			}
		}
		catch(BadExpressionError bee){
			validEquation = false;
		}
	}
	
	private void setCurrentAction(String command, String msg){
		String currAction = reMatch(command);
		String currActionFormat;
		if( currAction.equalsIgnoreCase("divide") ||
			currAction.equalsIgnoreCase("multiply") )
			currAction = currAction+" by";
		currActionFormat = currAction;
		if(	msg != null && 
			!msg.equalsIgnoreCase("left") && 
			!msg.equalsIgnoreCase("right")){
			currAction = currAction+" "+msg;
			currActionFormat += " <expression>" + msg + "</expression>";
		}
		setProperty("current action", currAction );
		setProperty("current action formatted", currActionFormat);
	}
	
	public void setNextEquation(String msg){
		EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.elementAt(currentStepNumber-1));
		try{
			Equation newEq = new Equation(msg); 
			lastEq.setInternalEquationString(newEq.getLeft().toString(),newEq.getRight().toString());
			trans.startNextStep("finalizetypein");
		}
		catch(BadExpressionError bee){
			System.out.println("SolverFrame.setNextEquation: error parsing equation: " + bee);
		}
	}
	
	public void disableMenuOperations(){
		m_MenuBar.disableOperations();
	}
	
	public void enableMenuOperations(){
		m_MenuBar.enableOperations();
	}
	
	private int lastStepState(){
		if(!useTypein || equations.size() <= 1) return STEPCOMPLETED; 
		EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.elementAt(currentStepNumber-1));
		return lastEq.getStepState();		
	}	
	
	private void setSideExpression(String msg,String side){
		EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.elementAt(currentStepNumber-1));
		lastEq.setTypeinExpressionString(msg,side);
	}
	
	public int getCurrentStepNumber(){
		return currentStepNumber;
	}
	
	public void suggestNewProblem(){
		clearAll();	
		EquationDialog dlog = new EquationDialog(this,"Enter the new equation",true,"new");
		dlog.setVisible(true);
		dlog.toFront();	
	}			
	
	public void displayCompletionMessage(){
		EquationPlusStepPanel eq = (EquationPlusStepPanel)(equations.lastElement());
		eq.displayCompletionMessage();
		isDone = true;
	}

	public void modalDialogPerformed (ModalDialogEvent event, Dialog eventHolder) {
		String command = event.getActionCommand();
		eventHolder.hide();
		eventHolder.dispose();
		this.repaint();
		if (event.isOK()){
			event.getArgument();
			performAction(command,event.getArgument());
		}
		if(!trans.messageVisible()){
			requestFocus();
		}
	}
	
	public synchronized void startSolvingEquation(String left,String right) {
		insertEquation(left,right);
		currentStepNumber = 1;
		lastWasDone = false;
		isDone = false;
	}
	
	private void updateDisplayForStep(String command, String arg, String userarg, String newLeft, String newRight) {
		if(lastWasDone){
			lastWasDone=false;
			((EquationPlusStepPanel)(equations.lastElement())).clearStep();
		}
		if (useTypein) {
			if(lastStepState() == STEPCOMPLETED){
				insertStep(command,arg,userarg);	
				startTypeinMode(newLeft,newRight);
				if(	arg != null){
					String unTouchedExp = "", unTouchedSide="";
					if(	arg.equalsIgnoreCase("left")){
					unTouchedExp = newRight;
					unTouchedSide = "right";
					}
					else if(arg.equalsIgnoreCase("right")){
					unTouchedExp = newLeft;
					unTouchedSide = "left";
					}
			
					if(!unTouchedExp.equals("")){
						int s = equations.size();
						EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.elementAt(s-1));
						lastEq.displaySide(unTouchedSide, unTouchedExp);
						validate();
					}
				}
			}
				
		}
		else {
                    insertStep(command,arg,userarg);
                    insertEquation(newLeft,newRight);
                }
	}
	
	private EquationPlusStepPanel addEquationStepPanel(boolean typein) {
            EquationPlusStepPanel thePanel = new EquationPlusStepPanel(typein);
		proofPanel.add(thePanel);
		currentStepNumber++;
		equations.addElement(thePanel);
		proofPanel.scrollToBottom();
                return thePanel;
	}	
	
	private void updateForNewEquation(Panel eqPanel) {
		proofPanel.setBounds(0,0,getSize().width,getSize().height);
			//proofPanel.setBounds(0,0,preferredSize().width,getSize().height);
		LightComponentScroller.getScroller(proofPanel).validate();

		proofPanel.scrollToBottom();
	}
	
	//insertEquation adds an equation to the display
	private EquationPlusStepPanel insertEquation(String left,String right) {
		EquationPlusStepPanel eq = addEquationStepPanel(useTypein);
		eq.addEquation(left,right);
		updateForNewEquation(eq);
System.gc();
		return eq;
	}
	
	private void startTypeinMode(String newLeft, String newRight) {
		EquationPlusStepPanel eAndSPanel = addEquationStepPanel(true);
/////	eAndSPanel.setInstructions("Click on left and right to enter the resulting equation");
		updateForNewEquation(eAndSPanel);
		eAndSPanel.setInternalEquationString(newLeft,newRight); //when in typein mode, leftASCII and rightASCII are the *expected* equation
	}
	
	private void insertStep(String action, String arg, String userarg) {
		action = reMatch(action);
                EquationPlusStepPanel lastEq;
                if(equations.isEmpty()){
                    /*we're processing a done action which was
                      correct, so the equations have already been
                      cleared.  Thus, we don't need to update them at
                      all*/
                    lastEq = null;
                }
                else{
                    lastEq = (EquationPlusStepPanel)(equations.lastElement());
                    lastEq.setStep(action,arg,userarg);
                }
			//need to recalculate layout
		updateForNewEquation(lastEq);
                System.gc();
	}
	
	private String reMatch(String command){
		if(	!matchOperations.containsKey(command.toLowerCase()))
			return command;
		return (String)matchOperations.get(command.toLowerCase());
	}
	
	public void undoString(int num)
	{
		int i;
		
		for (i=0;i<num;i++)
			undoString();
	}		
	
	
	public void undoString() {
		int LastStringStartPosition;
//		String txt=m_textArea.getText();
//		
//		LastStringStartPosition=txt.lastIndexOf('\n',txt.length()-2);
//		m_textArea.replaceText("",LastStringStartPosition+1,txt.length());
		System.out.println("undo");
	}
	
	//clearProof clears all but the initial equation
	public void clearProof()
	{
		for (int i=1;i<equations.size();++i) {
			proofPanel.remove((EquationPlusStepPanel)(equations.elementAt(i)));
			equations.removeElementAt(1);
		}
		proofPanel.scrollToBottom();
	}
	
	public void clearAll() {
		goalPanel.removeAll();
		for (int i=0;i<equations.size();++i){
			((EquationPlusStepPanel)equations.elementAt(i)).clearAll();
			proofPanel.remove((EquationPlusStepPanel)(equations.elementAt(i)));
		}
		equations = new Vector();
		validEquation = false;
		try{
		proofPanel.scrollToBottom();
		} catch (ArrayIndexOutOfBoundsException e) { }
		currentStepNumber = 0;
		enableMenuOperations();
	}
	
	public void clearLast() {
		isDone = false;
		if(equations.size() == 1 ){
			EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.lastElement());
			if(lastEq.stepIsSet() )
				lastEq.clearStep();
			else
				System.out.println("Can't remove only equation");
		}
		else if (equations.size() > 1) {
			EquationPlusStepPanel lastEq = (EquationPlusStepPanel)(equations.lastElement());
			if(!lastWasDone) {
				proofPanel.remove(lastEq);
				equations.removeElementAt(equations.size()-1);
				((EquationPlusStepPanel)(equations.lastElement())).clearStep();
				currentStepNumber--;
			}
			else {
				lastWasDone=false;
				lastEq.clearStep();
			}
			proofPanel.scrollToBottom();
		}
		enableMenuOperations();
	}
	
	public void scrollToBottom(){
		proofPanel.scrollToBottom();
	}

	public void setTranslator(TranslatorProxy tr) {
		trans = tr;
	}
	
	//getStepFromSelection finds the EquationPlusStepPanel specified in the selection
	//This expects the selection to be either a number (for strategic steps) or a string
	//like "left of 1" or "right of 4"
	private EquationPlusStepPanel getStepFromSelection(String selection) {
		EquationPlusStepPanel step = null;
		int selectionNum = -1;
		int spacePos = selection.lastIndexOf(' ');
		try {
			selectionNum = Integer.parseInt(selection.substring(spacePos+1));
		}
		catch (NumberFormatException err) { //no integer in selection
		}
		if (selectionNum >= 0)
			step = getEquation(selectionNum);
		//System.out.println("Getting step from *"+selection+"*:"+selectionNum+" "+step);
		return step;
	}
	
	public void flag(String selection) {
		EquationPlusStepPanel step = getStepFromSelection(selection);
		if (selection.length() > 4 && selection.substring(0,4).equalsIgnoreCase("left")) {
			if (step != null)
				step.flag("left");
		}
		else if (selection.length() > 5 && selection.substring(0,5).equalsIgnoreCase("right")) {
			if (step != null)
				step.flag("right");
		}
		else {	
			if (step != null)
				step.flag();
		}
	}
	
	public void unflag(String selection) {
		EquationPlusStepPanel step = getStepFromSelection(selection);
		if (selection.length() > 4 && selection.substring(0,4).equalsIgnoreCase("left")) {
			if (step != null)
				step.unflag("left");
		}
		else if (selection.length() > 5 && selection.substring(0,5).equalsIgnoreCase("right")) {
			if (step != null)
				step.unflag("right");
		}
		else {	
			if (step != null)
				step.unflag();
		}
	}
	
	public void tutorResponseComplete(String selection, String action, String input) {
		//if this is a typein event, pass it on to the step
		if (action.equalsIgnoreCase("typein")) {
			EquationPlusStepPanel step = getStepFromSelection(selection);
			if (step != null) {
				String side = selection.substring(0,selection.indexOf(' '));
				step.completeTypeinAction(side,input);
				updateForNewEquation(step);
			}
		}
		/*we don't want to request focus if we just responded to a
          done event, because that pops up the "new equation" dialog,
          and it needs to remain visible*/
		if(!(action.length() >= 4 && action.substring(0,4).equalsIgnoreCase("done") &&
			 equations.size() == 0)){
			this.requestFocus();
		}
	}
	
	//getEquation returns the EquationPlusStepPanel whose number is given. Numbers are 1-based
	public EquationPlusStepPanel getEquation(int number) {
		if (number <= equations.size())
			return (EquationPlusStepPanel)(equations.elementAt(number-1));
		else
			return null;
	}
	
	
	public Object getProperty(String prop) {
		if (prop.equalsIgnoreCase("current equation")) {
                    if (!equations.isEmpty()) {
			EquationPlusStepPanel eq = (EquationPlusStepPanel)(equations.lastElement());
			if (eq != null) {
				return eq.getEquationString();
			}
                    }
                    return ""; //should throw exception?
		}
		else if (prop.equalsIgnoreCase("actions")) { //return the actions as a serialized array
			String[] actions = m_MenuBar.getActions();
			String result="";
			try {
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(bo);
				objOut.writeObject(actions);
				result = new String(bo.toByteArray());
			}
			catch (IOException e) {
				System.out.println("Can't serialize actions: "+e);
			}
			return result;
		}
		else if (prop.equalsIgnoreCase("current state")) 
			return String.valueOf(lastStepState());
		else if(prop.equalsIgnoreCase("left")) 
			return ((EquationPlusStepPanel)(equations.lastElement())).leftSide();
		else if(prop.equalsIgnoreCase("right")) 
			return ((EquationPlusStepPanel)(equations.lastElement())).rightSide();
		else if(prop.equalsIgnoreCase("current sides")) {
			EquationPlusStepPanel eq = (EquationPlusStepPanel)(equations.lastElement());
			if (eq != null) {
				return eq.getTypeInString();
			} else
				try {
					throw new NoSuchPropertyException("getProperty: SolverFrame has no field: "+prop);
				} catch (NoSuchPropertyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		} 
                //SMILLER
                else if(prop.equalsIgnoreCase(SEND_NEW_EQUATION_PROP)) 
                    return new Boolean(send_new_equation_message);
                else if(prop.equalsIgnoreCase(SEND_SKILLS))
                    return new Boolean(send_skills);
		else {
			try {
				return super.getProperty(prop);
			} catch (NoSuchPropertyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prop;
	}
/*	
	public void setProperty(String prop,String toValue) throws NoSuchFieldException {
		//throw new NoSuchFieldException("setProperty: SolverFrame does not have property: "+prop);
		properties.put(prop, toValue);
	} */
	public void setProperty(String propertyName,Object propertyValue){
            getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
		if(propertyName.equalsIgnoreCase("IMAGEBASE"))
			setCodeBase((String)propertyValue);
		else if(propertyName.equalsIgnoreCase("SHOWSKILLS"))
			trans.setShowSkills(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("SETREFERENCEPROXY"))
			trans.setReferenceProxy(sProxy);
		else if(propertyName.equalsIgnoreCase("SETREFERENCETARGET"))
			trans.setRefTarget((String)propertyValue);
		else if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			setLocation(((Integer)loc.elementAt(0)).intValue(), ((Integer)loc.elementAt(1)).intValue());
		}	
		else if(propertyName.equalsIgnoreCase("TYPEINMODE")){
			setTypeinMode(DataConverter.getBooleanValue(propertyName,propertyValue));
		}
		else if(propertyName.equalsIgnoreCase("EQUATION")) {
			performAction("new",(String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("SUBTYPE")) {
		// just don't throw the exception
		}
		else if(propertyName.equalsIgnoreCase("ISAPPLICATION")){
			if(DataConverter.getBooleanValue(propertyName,propertyValue)){
				Menu fileMenu = MenuFactory.getFileMenu();
				// it's only "Quit" menuItem in a "File" menu
				MenuItem mi = fileMenu.getItem(0);
				mi.addActionListener(this);
				getMenuBar().add(fileMenu);
			}
		}
		else if(propertyName.equalsIgnoreCase("USEWEBEQ")) {
			if(!DataConverter.getBooleanValue(propertyName,propertyValue))
				SolverPanelFactory.setPanelType("Plain");
			else
				SolverPanelFactory.setPanelType("WebEq");
			repaint();
		}
		else if(propertyName.equalsIgnoreCase("Standardize")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.autoStandardize = true;
			else
				sm.autoStandardize = false;
		}
		else if(propertyName.equalsIgnoreCase("AutoSimplify")) {
			sm.autoSimplify = DataConverter.getBooleanValue(propertyName,propertyValue);
			
		}
		else if(propertyName.equalsIgnoreCase("Combine Like Terms")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.autoCombineLikeTerms = true;
			else
				sm.autoCombineLikeTerms = false;
		}
		else if(propertyName.equalsIgnoreCase("Reduce Fractions")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.autoReduceFractions = true;
			else
				sm.autoReduceFractions = false;
		}
		else if(propertyName.equalsIgnoreCase("Multiply through") ||
				propertyName.equalsIgnoreCase("Perform Multiplication")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.autoMultiplyThrough = true;
			else
				sm.autoMultiplyThrough = false;
		}
		else if(propertyName.equalsIgnoreCase("Expand Exponents")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.autoExpandExponent = true;
			else
				sm.autoExpandExponent = false;
		}
		else if(propertyName.equalsIgnoreCase("Distribute")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.autoDistribute = true;
			else
				sm.autoDistribute = false;
		}
		else if(propertyName.equalsIgnoreCase("Distribute Division")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.distributeDenominator = true;
			else
				sm.distributeDenominator = false;
		}
		else if(propertyName.equalsIgnoreCase("Remove Double Signs")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.allowDoubleSigns = false;
			else
				sm.allowDoubleSigns = true;
		}
		else if(propertyName.equalsIgnoreCase("Remove Extra Parentheses")) {
			if(DataConverter.getBooleanValue(propertyName,propertyValue))
				sm.allowExtraParens = false;
			else
				sm.allowExtraParens = true;
		}
                //SMILLER
                /*
                else if (propertyName.equalsIgnoreCase(SUBTYPE_PROP))
                    {
                        //DO NOTHING
                    }
                    */
		else if(propertyName.equalsIgnoreCase(SEND_NEW_EQUATION_PROP))
                    send_new_equation_message = DataConverter.getBooleanValue(propertyName,propertyValue);
                else if(propertyName.equalsIgnoreCase(SEND_SKILLS))
                    send_skills = DataConverter.getBooleanValue(propertyName,propertyValue);
                else if(propertyName.equalsIgnoreCase(UPDATE_SKILL))
                    sendNotePropertySet(propertyName, propertyValue);
                else if(propertyName.equalsIgnoreCase(ALLOW_USER_NEW_EQUATION))
                {
                    if (DataConverter.getBooleanValue(propertyName, propertyValue))
                        {
                            m_ToolBarPanel.addButton(Settings.equation,"New Equation", true, 3);
                            m_ToolBarPanel.addSeparator();
                        }
                }
                //END SMILLER else
					try {
						super.setProperty(propertyName, propertyValue);
					} catch (DorminException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		} catch(DataFormattingException ex){
			ex.printStackTrace();
		}
	}
	
	public void setTypeinMode(boolean newValue) {
		useTypein = newValue;
		if(newValue)
		 	setModeLine("TypeIn mode ON");
		 else
		 	setModeLine("TypeIn mode OFF");
	}
	
	public void setVisible(boolean v){
		if(v)
			setTypeinMode(useTypein);
		super.setVisible(v);
	}
	
	public boolean getTypeInMode() {
		return useTypein;
	}
	
	public static SolverFrame getSelf() {
		return mySelf;
	}
	
	public SymbolManipulator getSM(){
		return sm;
	}

	/*public void showSizes(){
	  System.out.println("  this.getBounds():        " + this.getBounds());
	  System.out.println("  m_mainPanel.getBounds(): " + m_mainPanel.getBounds());
	  System.out.println("  proofPanel.getBounds():  " + proofPanel.getBounds());
	  System.out.println("  goalPanel.getBounds():   " + goalPanel.getBounds());
	  //System.out.println("  showSizes temporarily disabled");
	  }*/

	public void paint(Graphics g){
		validate();
		super.paint(g);
	}

	public void layout(){
		super.layout();
	}
	
	/**
	 * Added by Kim K.C. temp fix
	 * @return
	 */
	public static boolean debug() {
		return false;
	}

	/**
	 * Added by Kim K.C. Mar 18 2005
	 * @param b
	 */
	public void setStandalone(boolean b) {
		// does nothing
		
	}
}

/*a panel with a sub-panel that gets re-sized whenever this panel is
  paint()ed*/
class fooPanel extends Panel{
	Panel subPanel;

	public fooPanel(Panel p){
		super();
		subPanel = p;
	}

	public void paint(Graphics g){
		super.paint(g);

		/*re-size the sub-panel*/
		Dimension d = subPanel.getPreferredSize();
		d.width = this.getSize().width;
		subPanel.setSize(d);

		/*repaint the sub-panel*/
		subPanel.repaint();
	}

	public Dimension getPreferredSize(){
		return super.getPreferredSize();
	}

	public void layout(){
		super.layout();
	}
}
