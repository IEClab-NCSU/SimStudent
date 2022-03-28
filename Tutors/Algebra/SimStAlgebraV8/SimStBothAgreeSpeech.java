package SimStAlgebraV8;

import java.util.Random;

import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.BothAgreeSpeechGetter;

public class SimStBothAgreeSpeech extends BothAgreeSpeechGetter {
	public String /* Object */ agreeSpeechGetter(RuleActivationNode ran, Sai s, SimStConversation conv)
    {
		String message = "";
		if((s.getI().contains("add")|| s.getI().contains("subtract")) && hasVariableTermOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_ADD_OR_SUBTRACT_VARIABLE_TERM_OPERATOR, s, true);
		}
		// variable on both sides
		/*else if((s.getI().contains("add")|| s.getI().contains("subtract")) && hasConstTermOperator(s.getI()) &&) {
			String[] sentences = {
					"Why cannot we get rid of the variable term first? Can you provide an example where I could get rid of the variable term first?",
					"Does it matter which sides' constant term we are getting rid of first?",
					"How do you select which side to start cancelling out the constant terms?"
					};
			Random rand = new Random();
			return sentences[rand.nextInt(sentences.length)];
		}*/
		// add
		else if(s.getI().contains("add") && hasPositiveOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_ADD_POSITIVE_OPERATOR, s, true);
		}
		else if(s.getI().contains("add") && hasWholeNumberOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_ADD_WHOLE_NUMBER_OPERATOR, s, true);
		}
		else if(s.getI().contains("add")) {
			message = conv.getMessage(SimStConversation.AGREE_ADD, s, true);
		}
		// subtract
		else if(s.getI().contains("subtract") && hasPositiveOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_SUBTRACT_POSITIVE_OPERATOR, s, true);
		}
		else if(s.getI().contains("subtract") && hasWholeNumberOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_SUBTRACT_WHOLE_NUMBER_OPERATOR, s, true);
		}
		else if(s.getI().contains("subtract")) {
			message = conv.getMessage(SimStConversation.AGREE_SUBTRACT, s, true);
		}
		// divide
		/*else if(s.getI().contains("divide") && hasPositiveOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_DIVIDE_POSITIVE_OPERATOR, s, true);
		}
		else if(s.getI().contains("divide") && hasWholeNumberOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_DIVIDE_WHOLE_NUMBER_OPERATOR, s, true);
		}*/
		else if(s.getI().contains("divide")) {
			message = conv.getMessage(SimStConversation.AGREE_DIVIDE, s, true);
		}
		// multiply
		/*else if(s.getI().contains("multiply") && hasPositiveOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_MULTIPLY_POSITIVE_OPERATOR, s, true);
		}
		else if(s.getI().contains("multiply") && hasWholeNumberOperator(s.getI())) {
			message = conv.getMessage(SimStConversation.AGREE_MULTIPLY_WHOLE_NUMBER_OPERATOR, s, true);
		}*/
		else if(s.getI().contains("multiply")) {
			message = conv.getMessage(SimStConversation.AGREE_MULTIPLY, s, true);
		}
		else if(s.getI().contains("DONE")) {
			message = conv.getMessage(SimStConversation.AGREE_DONE, s, true);
		}
		else {
			/*String[] sentences = {
					"Is <i> the only right action that can be taken at this state of equation?",
					"Tutor, is <i> the only correct transformation to be applied in the equation?",
					"Can you present some other examples for which <i> can be applied?",
					"Can you provide some other example of equations where I can apply <i>?",
					"Please tell me some other example of equations where I can apply <i>?",
					"Can you state some example equations where I can safely apply <i>?",
					"Can you state another equation for which I could have applied <i>?"
					};
			Random rand = new Random();
			String selected = sentences[rand.nextInt(sentences.length)];
			selected = selected.replace("<i>", s.getI());
			return selected;*/
			message = conv.getMessage(SimStConversation.AGREE_ANY, s, true);
		}

		return message;
    }

	public boolean hasPositiveOperator(String input) {
		if(input.contains(" ")) {
			String[] operator = input.split(" ");
			try {
			    double operator_value = Double.parseDouble(operator[1].trim());
			    if(operator_value<0)
			       return false;
			    else
			       return true;
			} catch (NumberFormatException e) {
			    return false;
			}
		}
		return false;
	}

	public boolean hasVariableTermOperator(String input) {
		if(input.contains(" ")) {
			String[] operator = input.split(" ");
			if (operator[1].trim().matches(".*[a-z].*")) return true;
		}
		return false;
	}

	public boolean hasWholeNumberOperator(String input) {
		if(input.contains(" ")) {
			String[] operator = input.split(" ");
			try {
			    int operator_value = Integer.parseInt(operator[1].trim());
			    return true;
			} catch (NumberFormatException e) {
			    // operator is not a integer number
				return false;
			}
		}
		return false;
	}
}
