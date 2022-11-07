/**
 * 
 */
package edu.cmu.pact.miss;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.Utilities.trace;

/**
 * @author mazda
 *
 */
public class InquiryRuleActivationBRD {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Class Fields 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // List of XML Documents for problem files 
    static Hashtable /* <String, Document> */ brdDocs = new Hashtable();
    private static Document getBrdDocFor(String problemName) {
        Document brdDoc = (Document)brdDocs.get(problemName);
        if (brdDoc == null) {
            brdDoc = createBrdDocFor(problemName);
            if (brdDoc != null) {
                brdDocs.put(problemName, brdDoc);
            }
        }
        return brdDoc;
    }

    private static Document createBrdDocFor(String problemName) {
        
        Document brdDoc = null;
        SAXBuilder saxBuilder = new SAXBuilder(); 
        try {
            brdDoc = saxBuilder.build(problemName);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return brdDoc;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public InquiryRuleActivationBRD() {
        // TODO Auto-generated constructor stub
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public static boolean isValidSAI(String selection, String action, String input, String parentName, String problemName) {
        
        Document brdDoc = getBrdDocFor(problemName);
        return isValidSAI(selection, action, input, parentName, brdDoc);
    }

    //original
    public static boolean isValidSAI(String selection, String action, String input, String problemName) {
        
        Document brdDoc = getBrdDocFor(problemName);
        return isValidSAI(selection, action, input, brdDoc);
    }

    
    private static boolean isValidSAI(String selection, String action, String input, String parentName, Document brdDoc) {
        
        boolean isValidSAI = false;
        
        Element rootElement = brdDoc.getRootElement();
        List brdElements = rootElement.getChildren();
        
        for (int i = 0; i < brdElements.size(); i++) {
            Element element = (Element)brdElements.get(i);
            if (isEdgeElemenet(element)) {
                Element actionLabel = element.getChild("actionLabel");
                Element properties = actionLabel.getChild("message").getChild("properties");
                String modelSelection = properties.getChild("Selection").getChild("value").getValue();
                String modelAction = properties.getChild("Action").getChild("value").getValue();
                String modelInput = properties.getChild("Input").getChild("value").getValue();
                
                //Gustavo 24 May 2007: look at the edge's parentNode, to check
                String edgeParentId = element.getChild("sourceID").getText();
                Element itParent = getNodeElementById(brdElements,edgeParentId);
                String itParentName = itParent.getChild("text").getText();
                if (modelSelection.equals(selection) &&
                        modelAction.equals(action) &&
                        modelInput.equals(input)
                        && parentName.equals(itParentName)
                        ) {
                    isValidSAI = true;
                    if(trace.getDebugCode("gusIL"))trace.out("gusIL", "isValidSAI: parentName = " + parentName);
                    break;
                }
            }
        }
        return isValidSAI;
    }

    //original version
    private static boolean isValidSAI(String selection, String action, String input, Document brdDoc) {
        
        boolean isValidSAI = false;
        
        Element rootElement = brdDoc.getRootElement();
        List brdElements = rootElement.getChildren();
        
        for (int i = 0; i < brdElements.size(); i++) {
            Element element = (Element)brdElements.get(i);
            if (isEdgeElemenet(element)) {
                Element actionLabel = element.getChild("actionLabel");
                Element properties = actionLabel.getChild("message").getChild("properties");
                String modelSelection = properties.getChild("Selection").getChild("value").getValue();
                String modelAction = properties.getChild("Action").getChild("value").getValue();
                String modelInput = properties.getChild("Input").getChild("value").getValue();
                
                if (modelSelection.equals(selection) &&
                        modelAction.equals(action) &&
                        modelInput.equals(input)
                        ) {
                    isValidSAI = true;
                    break;
                }
            }
        }
        return isValidSAI;
    }

    
    
    private static Element getNodeElementById(List brdElements, String edgeParentId) {
        Element el = null;
        for (int i = 0; i < brdElements.size(); i++) {
            Element element = (Element)brdElements.get(i);
            if (isNodeElement(element)) {
                String itId = element.getChild("uniqueID").getText();                
                if (edgeParentId.equals(itId)){
                    el = element;
                    break;
                }
            }            
        }
        return el;
    }

    private static boolean isEdgeElemenet(Element element) {
        return element.getName().equals("edge");
    }

    private static boolean isNodeElement(Element element) {
        return element.getName().equals("node");
    }

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String path = "/Users/mazda/mazda-on-Mac/Project/CTAT/CVS-TREE/Tutors/SimSt/Bootstrapping/Stoichiometry/BRD";
        String brd = "ChemPT_1T_01_IU.brd";
        
        String selection = "Term0Definition";
        String action = "UpdateComboBox";
        String input = "Given Value";
        
        trace.out(String.valueOf(isValidSAI(selection, action, input, path+"/"+brd)));
        
        selection = "done";
        action = "ButtonPressed";
        input = "1";
        
        trace.out(String.valueOf(isValidSAI(selection, action, input, path+"/"+brd)));
    }
}
