/*
 * HintMessgaesManager.java
 *
 * Created on July 13, 2004, 10:46 AM
 */

package edu.cmu.pact.BehaviorRecorder.Controller;

/**
 * 
 * @author zzhang
 */

import java.util.Vector;

import javax.swing.border.Border;

import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.ObjectProxy;
import edu.cmu.pact.ctat.TutorController;

public class HintMessagesManagerImpl extends HintMessagesManagerForClient {


    /** Creates a new instance of HintMessgaesManager */
    public HintMessagesManagerImpl(TutorController controller) {
        super(controller);
    }

	/** parsing hint messages */
    public boolean parseMessages() {
        // process buggy msg
        type = hintsMessageObject.getMessageType();
       // super.
        boolean result = super.parseMessages();
        /** parsing highlight widgets */
        if(result==false)
        	if (type.equalsIgnoreCase(SHOW_HINTS_MESSAGE_FROM_LISP)) {
        		processShowHintsMessageFromLisp();
        	}
        return true;
    }

	private void processShowHintsMessageFromLisp() {
        Object obj = getHintMessageProperty("Selection");

        // obj is null: no widget needs highlight
        if (obj == null) {
            trace.out(5, this, "Selection obj is null, don't need highlight");
            highlightFlag = false;

            return;
        }

        // based on Chang's message patern
        // obj is Boolean
        if (obj instanceof Boolean) {
            trace.out(5, this, "seclection objBoolean.booleanValue() = " + ((Boolean)obj).booleanValue());
            highlightFlag = ((Boolean)obj).booleanValue();
            return;
        }

        // obj is Vector
        Vector selectionPart = getHintMessageSelection();
        Vector actionPart = getHintMessageAction();

        Vector currentSelectionNames = new Vector();
        Vector currentSelectionBlock = new Vector();

        for (int i = 0; i < selectionPart.size(); i++) {
            currentSelectionNames = new Vector();

            obj = selectionPart.elementAt(i);
            if (obj instanceof Vector) {
                currentSelectionBlock = (Vector)selectionPart.elementAt(i);
                for (int j = 0; j < currentSelectionBlock.size(); j++) {
                    obj = currentSelectionBlock.elementAt(j);

                    if (obj == null)
                        continue;

                    process_CurrentSelection_for_Type_String_ObjectProxy(obj, actionPart, i, currentSelectionNames);
                }
            } else
                process_CurrentSelection_for_Type_String_ObjectProxy(obj, actionPart, i, currentSelectionNames);

            highlightWidgetNames.addElement(currentSelectionNames);
        }

        highlightWidgetNames = new Vector();

        // parse color part
        Vector colorList = getHintMessageVector("ColorList");
        Vector colorBlockList;
        Vector colorBlock;
        String colorString;

        trace.out(5, this, "colorList.size() = " + colorList.size());

        for (int i = 0; i < colorList.size(); i++) {
        	highlightWidgetNames = new Vector();
            obj = colorList.elementAt(i);

            if (obj instanceof Vector) {
                trace.out(5, this, "obj is a Vector");
                colorBlockList = (Vector) colorList.elementAt(i);
                
                trace.out(5, this, "colorBlockList.size() = " + colorBlockList.size());
                for (int j = 0; j < colorBlockList.size(); j++) {
                    colorBlock = (Vector) colorBlockList.elementAt(j);
                    selectionName = (String) colorBlock.elementAt(0);
                    trace.out(5, this, "selectionName = " + selectionName);
                    if (selectedWidget()!=null) {
                        colorString = (String)colorBlock.elementAt(1);
                        trace.out(5, this, "colorString = " + colorString);
                        Border border = getHighlightBorder(colorString);
                        currentHighlightNames.addElement(new HighlightWidget(selectionName, border, null));
                    }
                }
                trace.out(5, this, "currentHighlightNames.size() = " + currentHighlightNames.size());
            } else
                trace.out(5, this, "obj is not a Vector, don't need highlight.");

            highlightWidgetNames.addElement(currentHighlightNames);
        }

        trace.out(5, this, "highlightWidgetNames.size() = "
                + highlightWidgetNames.size());

        /** highlight 1st widget */
        // if (highlightFlag)
        // setCurrentHighlights();
    }

	// /////////////////////////////////////////////////////////////
    /**
     * parsing selectionName from obj
     */
    // /////////////////////////////////////////////////////////////
    private void process_CurrentSelection_for_Type_String_ObjectProxy(
            Object obj, Vector actionV, int i, Vector currentSelectionNames) {
        if (obj instanceof String) {
            // String widgetName = (String) currentSelectionBlock.elementAt(j);
            String widgetName = (String) obj;

            widgetName = widgetName.trim();
            // trace.out(5, this, "parsing selection from the Object obj and its
            // String type value = " + widgetName);
            if (!widgetName.equals("") && !widgetName.equalsIgnoreCase("null")) {
                highlightWidgetPair = new HighlightWidget(widgetName,
                		defaultBorder, actionV, i);
                currentSelectionNames.addElement(highlightWidgetPair.getSelection());
            }
        } else {
            // trace.out(5, this, "parsing selection from the Object obj and
            // it's an ObjectProxy");

            ObjectProxy msgBlock = (ObjectProxy) obj;

            // trace.out(5, this, "its String value msgBlock.toString() = " +
            // msgBlock.toString());

            if (msgBlock != null) {
                highlightWidgetPair = new HighlightWidget(msgBlock.getName(),
                		defaultBorder, actionV, i);
                currentSelectionNames.addElement(highlightWidgetPair.getSelection());
            }
        }

        return;
    }


}
