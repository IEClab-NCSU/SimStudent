package SimStFractionAdditionV1;

import java.util.Vector;
import edu.cmu.pact.miss.*;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;
import edu.cmu.pact.miss.minerva_3_1.StepAbstractor;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class FractionAdditionAssessor extends AlgebraProblemAssessor {

	@Override
	public boolean isProblemComplete(String problem, Vector<ProblemEdge> solutionPath) {
			
			for (ProblemEdge edge : solutionPath) {
				if(edge.getSelection().equalsIgnoreCase(Rule.DONE_NAME) && edge.isCorrect())
        		{
					return true;
        		}
				if (!edge.isCorrect()) return false;	
			}
			return false;
		
	}
	
	

}
