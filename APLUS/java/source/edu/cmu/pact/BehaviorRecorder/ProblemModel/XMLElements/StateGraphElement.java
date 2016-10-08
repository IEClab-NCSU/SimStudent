package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.HintPolicyEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReader;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.DefaultGroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.GroupModel;

public class StateGraphElement {
    private String elementName = "stateGraph";

    private String attrfirstCheckAllStates;

    private String attrcaseInsensitive;

    private String attrunordered;

    private String attrlockWidget;
    
    private HintPolicyEnum attrHintBiasPolicy;

    private String attrversion;

    private ArrayList nodesList;

    private GroupModel groupModel;

    private ArrayList edgesList;

    private ArrayList productionRulesList;

    private StartNodeMessagesElement startNodeMessages;

    private FeedbackEnum suppressFeedback;

    private Boolean highlightRightSelection;

	private String studentBeginsHereStateName;

	private Boolean confirmDone;

	private String tutorType;

	private String outOfOrderMessage;

    public StateGraphElement() {
        nodesList = new ArrayList();
        edgesList = new ArrayList();
        productionRulesList = new ArrayList();
    }

    // print in XML format
    public void printXML(DataWriter w) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        if (attrfirstCheckAllStates != null)
            atts.addAttribute("", "firstCheckAllStates", "", "String",
                    attrfirstCheckAllStates);
        if (attrcaseInsensitive != null)
            atts.addAttribute("", "caseInsensitive", "", "String",
                    attrcaseInsensitive);
        if (attrunordered != null)
            atts.addAttribute("", "unordered", "", "String", attrunordered);
        if (attrlockWidget != null)
            atts.addAttribute("", "lockWidget", "", "String", attrlockWidget);
        if (attrHintBiasPolicy != null)
        	atts.addAttribute("", BR_Controller.HINT_POLICY, "", "String", attrHintBiasPolicy.toString());
        if (attrversion != null)
            atts.addAttribute("", ProblemStateReader.VERSION_ATTR, "", "String", attrversion);
        if (suppressFeedback != null)
            atts.addAttribute("", BR_Controller.SUPPRESS_STUDENT_FEEDBACK, "", "String", suppressFeedback.toString());
        // trace.out ("write brd: suppress feedback = " + suppressFeedback);
        if (highlightRightSelection != null)
            atts.addAttribute("", "highlightRightSelection", "", "String", highlightRightSelection.toString());
        if (confirmDone != null)
        	atts.addAttribute("", "confirmDone", "", "String", confirmDone.toString());
        if (studentBeginsHereStateName != null && studentBeginsHereStateName.length() > 0)
            atts.addAttribute("", ProblemModel.STUDENT_BEGINS_HERE, "", "String", studentBeginsHereStateName);
        if (tutorType != null && tutorType.length() > 0)
        	atts.addAttribute("", "tutorType", "", "String", tutorType);
        if (outOfOrderMessage != null && outOfOrderMessage.length() > 0)
            atts.addAttribute("", ProblemModel.OUT_OF_ORDER_MESSAGE, "", "String", outOfOrderMessage);
        
        w.startElement("", elementName, "", atts);
        if (startNodeMessages != null)
            startNodeMessages.printXML(w);

        for (int i = 0; i < nodesList.size(); i++)
            ((NodeElement) nodesList.get(i)).printXML(w);

        for (int i = 0; i < edgesList.size(); i++)
            ((EdgeElement) edgesList.get(i)).printXML(w);

        for (int i = 0; i < productionRulesList.size(); i++)
            ((ProductionRuleElement) productionRulesList.get(i)).printXML(w);


        if(groupModel==null)
	    	groupModel = new DefaultGroupModel();
        
        groupModel.printXML(w);

        w.endElement(elementName);
    }

    // methods to add sub elements to stateGraph
    public void addfirstCheckAllStates(boolean in) {
        attrfirstCheckAllStates = (new Boolean(in)).toString();
    }

    public void addunordered(boolean in) {
        attrunordered = (new Boolean(in)).toString();
    }

    public void addcaseInsensitive(boolean in) {
        attrcaseInsensitive = (new Boolean(in)).toString();
    }

    public void addlockWidget(boolean in) {
        attrlockWidget = (new Boolean(in)).toString();
    }
    
    public void addHintPolicy(HintPolicyEnum hbe) {
    	attrHintBiasPolicy = hbe;
    }

    public void addversion(String in) {
        attrversion = in;
    }

    public void addStartNodeMessages(StartNodeMessagesElement in) {
        startNodeMessages = in;
    }

    public void addNode(NodeElement in) {
        nodesList.add(in);
    }

    public void addGroupModel(GroupModel groupModel) {
    	this.groupModel = groupModel;
    }

    public void addEdge(EdgeElement in) {
        edgesList.add(in);
    }
    
    public void addTutorType(BR_Controller controller) {
        this.tutorType = controller.getCtatModeModel().getCurrentMode();
    }

    /**
     * Create a &lt;{@value ProductionRuleElement#PRODUCTION_RULE_ELEMENT_NAME}&gt; element.
     * @param ruleName simple skill or rule name
     * @param productionSet name of skill group or rule set 
     * @param hintMessages default hints for this rule 
     * @param opportunityCount number of times student has an opportunity to demonstrate
     * 		  this skill while solving this problem
     */
    public void addProductionRule(String ruleName, String productionSet,
            List<String> hintMessages, Integer opportunityCount,
            String label, String description) {
        ProductionRuleElement tmp = new ProductionRuleElement();
        tmp.setOpportunityCount(opportunityCount);
        tmp.addruleName(ruleName);
        tmp.addproductionSet(productionSet);
        tmp.addLabel(label);
        tmp.addDescription(description);
        for (String hintMessage : hintMessages)
            tmp.addhintMessage(hintMessage);
        productionRulesList.add(tmp);
    }

    public void addSuppressStudentFeedback(FeedbackEnum suppressStudentFeedback) {
        this.suppressFeedback = suppressStudentFeedback;
    }

    public void addHighlightRightSelection(boolean highlightRightSelection) {
        this.highlightRightSelection = new Boolean (highlightRightSelection);
    }

	public void addStudentBeginsHereStateName(String studentBeginsHereStateName) {
		this.studentBeginsHereStateName = studentBeginsHereStateName;
	}

	public void addConfirmDone(Boolean confirmDone) {
		this.confirmDone = confirmDone;  // can be null
	}

	public void addOutOfOrderMessage(String outOfOrderMessage) {
		if (ProblemModel.DEFAULT_OUT_OF_ORDER_MESSAGE.equals(outOfOrderMessage))
			this.outOfOrderMessage = null;
		else
			this.outOfOrderMessage = outOfOrderMessage;
	}
}
