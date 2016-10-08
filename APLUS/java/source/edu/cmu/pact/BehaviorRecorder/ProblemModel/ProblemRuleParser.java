package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.ProductionRuleElement;

public class ProblemRuleParser extends DefaultHandler {

    private String currentElement;

    private String currentElementData;

    private String tempRuleName;

    private String tempProductionSet;

    private RuleProduction currentProduction;

    private HashMap<String, String> vars;    // never populated?

    private Vector<RuleProduction> problemRules;
    
    public ProblemRuleParser(Vector<RuleProduction> problemRules) {
        // TODO Auto-generated constructor stub
    	this.problemRules = problemRules;
    } 

    public void startDocument() {
        currentElement = null;
        currentProduction = null;
        tempRuleName = tempProductionSet = null;
        vars = new HashMap<String, String>();
    }

    public void endDocument() {
    }

    public void startElement(String namespaceUri, String localName,
            String qName, Attributes attributes) throws SAXException {
        if (qName.equals("stateGraph")) {
            currentElement = "stateGraph";
            String version = attributes.getValue("version");
            if (version != null && !version.equals("0.5") && !version.equals("1.0")
                    && !version.equals("2.1"))
                    throw new SAXException(
                            "Unrecognized CTAT problem file version number");
        } else if (qName.equals(ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME)) {
            currentElement = ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME;
        } else if (qName.equals("text")) {
            if (currentElement.equals(ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME)) {
                currentElement = "productionRuleText";
                currentElementData = new String();
            }
        } else if (qName.equals("hintMessage")) {
            if (currentElement.equals(ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME)) {
                currentElement = "productionRuleHintMessage";
                currentElementData = new String();
            }
        } else if (currentElement.equals(ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME)
                && (qName.equals("ruleName") || qName.equals("productionSet"))) {
            currentElement = new String(qName);
            currentElementData = new String();
        }
    }

    private String insertVars(String str) {
        Pattern varPattern = Pattern.compile("%([^ ]*?)%");
        Matcher varMatcher = varPattern.matcher(str);
        while (varMatcher.find()) {
            String value = null;
            if (varMatcher.group(1) != null) {
                value = (String) vars.get(varMatcher.group(1));
            }
            if (value != null) {
                str = varMatcher.replaceFirst(value);
                varMatcher = varPattern.matcher(str);
            }
        }
        return str;
    }

    public void endElement(String namespaceUri, String localName, String qName) {
        if (currentElement.equals("ruleName")) {
            tempRuleName = insertVars(currentElementData);
            if (tempProductionSet != null)
                    currentProduction = new RuleProduction(tempRuleName,
                            tempProductionSet);
            currentElement = ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME;
        } else if (currentElement.equals("productionSet")) {
            tempProductionSet = insertVars(currentElementData);
            if (tempRuleName != null)
                    currentProduction = new RuleProduction(tempRuleName,
                            tempProductionSet);
            currentElement = ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME;
        } else if (currentElement.equals("productionRuleHintMessage")
                && currentProduction != null) {
            currentProduction.addHintItem(currentElementData);
            currentElement = ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME;
        } else if (currentElement.equals("productionRuleText")
                && currentProduction != null) {
            currentProduction.setProductionRule(insertVars(currentElementData));
            currentElement = ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME;
        } else if (currentElement.equals(ProductionRuleElement.PRODUCTION_RULE_ELEMENT_NAME)
                && currentProduction != null) {
            if (!(tempRuleName.equals("unnamed") && tempProductionSet
                    .equals("rule"))) {
                problemRules.add(currentProduction);

            }
            currentProduction = null;
        }
        currentElementData = new String();

    }

    public void characters(char ch[], int start, int length) {

        if (currentElementData != null) {
            currentElementData += new String(ch, start, length);
        }
    }

}