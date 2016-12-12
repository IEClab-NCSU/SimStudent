package TabbedTest;

import pact.CommWidgets.JCommButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import javax.swing.JOptionPane;
import java.awt.Color;

import pact.CommWidgets.event.HelpEvent;
import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditLabelNameDialog;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.Hints;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommMultipleChoice;
import pact.CommWidgets.JCommWidget;
import TabbedTest.recover.JCommComboBoxRecover;
import TabbedTest.recover.JCommMultipleChoiceRecover;
import TabbedTest.recover.JCommRecover;
import TabbedTest.recover.JCommTextAreaRecover;
import TabbedTest.recover.JCommTextFieldRecover;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;


public class TestDoneButton extends JCommButton {

	public Hashtable<String, JCommWidget> interfaceElements;
	BR_Controller controller;
	public TestDoneButton(){
	super();
			
	}
	
	
		public void setElements(Hashtable<String, JCommWidget> interfaceElements2){
		interfaceElements=new Hashtable<String, JCommWidget>();
		interfaceElements.putAll(interfaceElements2);
	}

	public void setController(BR_Controller br){
	this.controller=br;
	}


	boolean checkIfTestCompleted(){
	boolean completed=true;
		 for(String selection: interfaceElements.keySet()){
			//JOptionPane.showMessageDialog(null,selection);
            System.out.println("Value of "+selection+" is: "+interfaceElements.get(selection));
     	
     		JCommWidget obj=this.interfaceElements.get(selection);	
		  	if (obj instanceof JCommMultipleChoiceRecover){
		  		int input=((JCommMultipleChoiceRecover) obj).getSelectedChoiceIndex();
		  		if (input==-1){
		  		  //((JCommMultipleChoiceRecover) obj).setBackground(Color.RED);
		  		  completed=false;
		  		  break;
		  		 }
			}
		  	else if (obj instanceof JCommTextFieldRecover){
				String input=((JCommTextFieldRecover) obj).getText();
				if (input.length()<1){
				  //((JCommTextFieldRecover) obj).setBackground(Color.RED);
		  		  completed=false;
		  		  break;
		  		 }
		  	}
		  	else if (obj instanceof JCommTextAreaRecover){
				String input=((JCommTextAreaRecover) obj).getInputText();
				if (input.length()<1){
				  //((JCommTextAreaRecover) obj).setBackground(Color.RED);
		  		  completed=false;
		  		  break;
		  		 }
			
		  	}  
  
    }
	
	return completed;
	}
	
	
	
	void registerStudentAnswers(){
		 for(String selection: interfaceElements.keySet()){
			
     		JCommWidget obj=this.interfaceElements.get(selection);	
		  	if (obj instanceof JCommMultipleChoiceRecover){
		  		String input=((JCommMultipleChoiceRecover) obj).getInputText();
		  		System.out.println("Registering " + selection + " with value " + input);
		  		((JCommMultipleChoiceRecover) obj).recoverStudentAction(input);

			}
		  	else if (obj instanceof JCommTextFieldRecover){
				String input=((JCommTextFieldRecover) obj).getText();
				System.out.println("Registering " + selection + " with value " + input);
				((JCommTextFieldRecover) obj).recoverStudentAction(input);

		  	}
		  	else if (obj instanceof JCommTextAreaRecover){
				String input=((JCommTextAreaRecover) obj).getInputText();
				System.out.println("Registering " + selection + " with value " + input);
				((JCommTextAreaRecover) obj).recoverStudentAction(input);
		  	}  
  
    }
	
	}
	
	
	public void actionPerformed(ActionEvent e) {

	boolean completed=checkIfTestCompleted();
	System.out.println(" Completed : "+completed);
	if (!completed) {
		JOptionPane.showMessageDialog(null,"I'm sorry but you are not done yet. Please continue working.");
		return;
	}

	registerStudentAnswers();
	
	/*proceed with normal done procedure.*/
	 removeHighlight(commName);    
	    if (commName==null)
	    	commName=this.getName();
	    	
		if (locked)
			return;

			dirty = true;
			sendValue();
			if (this.commName.equalsIgnoreCase("hint") || this.commName.equalsIgnoreCase("help")) {
				this.fireHelpEvent(new HelpEvent(this));

		}
	
	}
	
}
