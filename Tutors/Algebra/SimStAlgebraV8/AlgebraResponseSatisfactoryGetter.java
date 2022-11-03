package SimStAlgebraV8;

import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.IsResponseSatisfactory;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStConversation;


public class AlgebraResponseSatisfactoryGetter extends IsResponseSatisfactory {
	public boolean isResponseSatosfactoryGetter(String label)    
	{	
		if(label.equals("R2&Y") || label.equals("R1&Y")) return true;
		return false;
		
    }
}
