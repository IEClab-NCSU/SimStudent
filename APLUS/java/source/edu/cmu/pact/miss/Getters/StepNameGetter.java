package edu.cmu.pact.miss.Getters;

import cl.utilities.TestableTutor.SAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

import java.util.List;
import java.util.Vector;

/**
 * Defines the contract for a StepNameGetter.
 */
public interface StepNameGetter {
    /**
     * Implementations of this method must provide a problem-specific step name
     * <p>
     * The generated step name must:
     * <ol>
     * <li> Be unique for each step.</li>
     * <li> Convey semantic meaning within the context of the tutor.</li>
     * </ol>
     *
     * @param pathEdges Sequence of SAI (Selection-Action-Input) objects representing the path from the
     *                `startNode` to the current node.
     * @param startNode The start node of the problem graph. Contains the problem entered by the user.
     * @return The step name
     */
    public String getStepName(Vector<ProblemEdge> pathEdges, ProblemNode startNode);
}