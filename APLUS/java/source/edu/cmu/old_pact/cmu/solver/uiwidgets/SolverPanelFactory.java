package edu.cmu.old_pact.cmu.solver.uiwidgets;


//SolverPanelFactory is a factory class that allows us to easily switch between
//different ways of displaying equations

public class SolverPanelFactory {
	private static String panelType="WebEq";

	public SolverPanelFactory() {
	}
	
	public static void setPanelType(String type) {
		panelType = type;
	}
	
	public static String getPanelType() {
		return panelType;
	}
	
	public static EquationPanel makeEquationPanel(PanelParameters parms,boolean typein) {
		if (panelType.equalsIgnoreCase("WebEq"))
			return new WebEqEquationPanel(parms,typein);
		else
			return new PlainEquationPanel(parms,typein);		
	}
	
	public static TransformationPanel makeTransformationPanel(PanelParameters parms) {
		if (panelType.equalsIgnoreCase("WebEq"))
			return new WebEqTransformationPanel(parms);
		else
			return new PlainTransformationPanel(parms);
	}
	
	public static InstructionsPanel makeInstructionsPanel(PanelParameters parms) {
		return new InstructionsPanel(parms);
	}
	
	public static StepCommentaryPanel makeStepCommentaryPanel(PanelParameters parms) {
		return new StepCommentaryPanel(parms);
	}
}
