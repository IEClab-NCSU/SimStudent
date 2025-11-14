package SimStAlgebraV8;

import cl.utilities.TestableTutor.SAI;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.Getters.StepNameGetter;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

import java.util.Vector;


/**
 * An implementation of {@link StepNameGetter} for the Algebra V8 Tutor.
 */
public class AlgebraV8AdhocStepNameGetter implements StepNameGetter {
    public static final String START_STEP = "NA";
    public static final String EQUALITY = "=";

    /**
     * Generates a problem-specific step name for the Algebra V8 Tutor
     * based on the provided list of {@link SAI} objects.
     * <p>
     * The resulting step name follows the format:
     * <pre>
     *     Equation_[Transformation: optional]_[Type-in: optional, only if transformation present]
     * </pre>
     * For example, consider the last two rows of the problem interface:
     * <pre>
     * 16 = -4y   |  divide -4
     * -4 = __    |  __
     * </pre>
     * Step name = 16=-4y_[divide -4]_[-4]
     * <ul>
     *   <li><code>Equation</code> — corresponds to <code>16 = -4y</code>.</li>
     *   <li><code>Transformation</code> — corresponds to <code>divide -4</code>.</li>
     *   <li><code>Type-in</code> — corresponds to <code>-4</code>.</li>
     * </ul>
     */
    @Override
    public String getStepName(Vector<ProblemEdge> pathEdges, ProblemNode startNode) {
        if (startNode == null)
            return START_STEP;

        String stepName = lastEquation(pathEdges, startNode);
        if (isUseTransformation(pathEdges)) {
            // Returns transformation with type-in if present
            stepName += findLastTransformationWithTypeIn(pathEdges);
        }
        return stepName;
    }

    /**
     * @return True if transformation needs to be added in the step name
     */
    private static boolean isUseTransformation(Vector<ProblemEdge> pathEdges) {
        if (pathEdges == null) {
            return false;
        }

        boolean useTransformation = false;
        int edgeLength = pathEdges.size();

        ProblemNode currentNode = pathEdges.get(edgeLength - 1).getDest();
        int currentNodeInDegree = currentNode.getInDegree();

        if (currentNodeInDegree > 0) {
            ProblemEdge edge = currentNode.getIncomingEdges().get(currentNodeInDegree - 1);
            int edgeInDegree = edge.getSource().getInDegree();

            if (edgeInDegree > 0) {
                ProblemEdge prevEdge = edge.getSource().getIncomingEdges().get(edgeInDegree - 1);
                if (edge.getSelection().contains("dorminTable3_") || prevEdge.getSelection().contains("dorminTable3_"))
                    useTransformation = true;
            } else {
                useTransformation = true;
            }
        }
        return useTransformation;
    }

    /**
     * Returns the last valid equation from the sequence of problem edges.
     * An equation is valid if it contains both LHS and RHS.
     * Format: LHS=RHS
     */
    private String lastEquation(Vector<ProblemEdge> pathEdges, ProblemNode startNode) {
        if (pathEdges == null || pathEdges.size() < 3) {
            return convertProblemToEquation(startNode.getName());
        }
        String lastEquation = null;
        int edgeCount = 0;
        ProblemEdge[] edgeQueue = new ProblemEdge[3];

        for (ProblemEdge edgeData : pathEdges) {
            edgeQueue[edgeCount++] = edgeData;

            if (edgeCount == 3) {
                String[] eqSide = new String[2];
                for (int j = 0; j < 2; j++) {
                    String input = String.valueOf(edgeQueue[j + 1].getEdgeData().getInput().get(0));
                    eqSide[j] = input;
                }

                lastEquation = eqSide[0] + EQUALITY + eqSide[1];
                edgeCount = 0;
                for (int k = 0; k < 3; k++) {
                    edgeQueue[k] = null;
                }
            }
        }
        return lastEquation;
    }

    /**
     * Convert the given problem into readable equations string.
     *
     * @param problem The given problem LHS_RHS
     * @return Equation: LHS=RHS
     */
    private String convertProblemToEquation(String problem) {
        return problem.replace("_", EQUALITY);
    }

    /**
     * Returns the last transformation and type-in if only a single type-in is performed after the transformation
     * Format: _[Transformation]_[Type-in]
     */
    private String findLastTransformationWithTypeIn(Vector<ProblemEdge> pathEdges) {
        String lastTransformationWithTypeIn = "";
        if (pathEdges == null) return lastTransformationWithTypeIn;

        int edgeCount = 0;
        ProblemEdge[] edgeQueue = new ProblemEdge[3];

        for (int i = 0; i < pathEdges.size(); i++) {
            ProblemEdge edgeData = pathEdges.get(i);
            edgeQueue[edgeCount++] = edgeData;

            if (edgeCount == 1) {
                String lastSkill = String.valueOf(edgeQueue[edgeCount - 1].getEdgeData().getInput().get(0));
                if (EqFeaturePredicate.isValidSimpleSkill(lastSkill.split(" ")[0])) {
                    lastTransformationWithTypeIn = "_[" + lastSkill + "]";

                    // A single type-in after transformation exists. This can be either LHS or RHS.
                    if (i == pathEdges.size() - 2) {
                        ProblemEdge typedEdge = pathEdges.get(i + 1);
                        lastTransformationWithTypeIn += "_[" + typedEdge.getEdgeData().getInput().get(0) + "]";
                    }
                }
            }
            if (edgeCount == 3) {
                edgeCount = 0;
                for (int k = 0; k < 3; k++) {
                    edgeQueue[k] = null;
                }
            }

        }
        return lastTransformationWithTypeIn;
    }
}
