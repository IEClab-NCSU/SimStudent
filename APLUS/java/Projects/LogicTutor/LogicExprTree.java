package LogicTutor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;



import fri.patterns.interpreter.parsergenerator.semantics.*;



/**

 * A tree of {@link LogicExprTree.Node} instances representing a boolean

 * logical expression; e.g. "((p->q)&~(p|r))<->(r->p)".

 */

public class LogicExprTree implements Serializable {



	/** Syntax token. */

	public static final String LEFT_PARENTHESIS = "(";

	/** Syntax token. */

	public static final String RIGHT_PARENTHESIS = ")";



	/**

	 * A node in the logical expression tree.

	 */

	public class Node implements Serializable {



		/** Variable name. */

		private final String variableName;



		/** Operator. */

		private final String operator;



		/** List of operands. Each element is a {@link Node} instance. */

		private final List operands;



		/** Whether this expression is enclosed in a pair of parentheses. */

		private boolean inParentheses = false;



		/** Index in subexpression list. */

		private int subExprColumn = -1;



		/**

		 * Private constructor for internal use only.

		 */

		private Node(String variableName) {

			this.variableName = variableName;

			operator = null;

			operands = null;

		}



		/**

		 * Private constructor for internal use only.

		 */

		private Node(String operator, List operands) {

			variableName = null;

			this.operator = operator;

			this.operands = operands;

		}



		/**

		 * Tell whether this node is a simple variable.

		 * @return true if {@link #variableName} is not null

		 */

		public boolean isVariable() {

			return variableName != null;

		}



		/**

		 * Access to variable name.

		 * @return {@link #variableName}; null if not a variable

		 */

		public String getVariable() {

			return isVariable() ? variableName : null;

		}

		

		/**

		 * Number of operands.

		 * @return {@link #operands}.size(); zero if is a variable.

		 */

		public int getArity() {

			if (operands == null)

				return 0;

			else

				return operands.size();

		}

		

		/**

		 * Access to operands.

		 * @param i operand index, 0-based, left-to-right

		 * @return operand for given index

		 */

		public Node getOperand(int i) {

			return (Node) operands.get(i);

		}



	        /**

		 * 

		 */

	        public String getSubExprColumn() {

		    if (isVariable())

			return variableName;

		    else

			return (new Integer(subExprColumn)).toString();

		}



	    public String[] getLogicEvaluation() {

		String[] result;

		if (isVariable()) {

		    result = new String[1];

		    result[0] = variableName;

		} else {

		    result = new String[operands.size() + 1];

		    int i = 0;

		    result[i++] =operator;

		    for (; i < operands.size()+1; ++i)

			result[i] = getOperand(i-1).getSubExprColumn();

		}

		return result;

	    }





		/**

		 * Override to print expression with no white space.  Prints unary

		 * operators prefixed; prints other operators after the first operand.

		 * @return subtree expression as string on one line

		 */

		public String toString() {

		    return toString(false);

		}



		/**

		 * Override to print expression with no white space.  Prints unary

		 * operators prefixed; prints other operators after the first operand.

		 * @return subtree expression as string on one line

		 */

		public String toString(boolean alwaysParenthesize) {

			StringBuffer sb = new StringBuffer();

			if (isVariable()) {

				if (inParentheses)

					sb.append("(");

				sb.append(variableName);

				if (inParentheses)

					sb.append(")");

				return sb.toString();

			}
// chc 04/24/2007 single operand doesn't need Parenthesize
//			if (inParentheses || alwaysParenthesize)
//
//				sb.append("(");

			if (getArity() <= 1) { // single operand: show prefix operator

				sb.append(operator);

				for (int i = 0; i < operands.size(); ++i)

					sb.append(getOperand(i).toString(alwaysParenthesize));

			} else {

				if (inParentheses || alwaysParenthesize)

					sb.append("(");
				
				int i = 0;

				sb.append(getOperand(i++).toString(alwaysParenthesize));

				sb.append(operator);

				for (; i < operands.size(); ++i)

					sb.append(getOperand(i).toString(alwaysParenthesize));
				
				if (inParentheses || alwaysParenthesize)

					sb.append(")");


			}

//			if (inParentheses || alwaysParenthesize)
//
//				sb.append(")");

			return sb.toString();

		}

			

		

		/**

		 * Indented print for debugging. Begins output with a newline.

		 * Puts operands on separate lines.

		 * @param indent number of spaces to skip after newline

		 */

		public String indentedString(int indent) {

			StringBuffer sb = new StringBuffer("\n");

			for (int i = 0; i < indent; ++i)

				sb.append(' ');

			if (isVariable())

				sb.append(variableName);

			else {

				sb.append(operator);

				for (Iterator it = operands.iterator(); it.hasNext(); )

					sb.append(((Node) it.next()).indentedString(indent+1));

			}

			return sb.toString();

		}

	}



	/**

	 * The variable table. Keys are variable names; e.g., P, Q, R or S.

	 * Values are {@link LogicExprTree.Node} instances.

	 */

	private Map variableTable = new HashMap();

	private Set variableSet = new LinkedHashSet();

	/**

	 * The tree node list. Indices in this list map to column numbers for

	 * subexpression in a truth table representation of the logical expression.

	 * The columns for variables are represented separately.

	 */

	private List subExprList = new ArrayList();

	private String[] subExprArray = null;

	private Node[] ExprTreeNode = null;

	/**

	 * The root node of the tree.

	 */

	private final Node root;

	

	/**

	 * Create a tree given a root node from the {@link LogicExprParser}.

	 * @param tbsnRoot root node from the parser

	 */

	public LogicExprTree(TreeBuilderSemantic.Node tbsnRoot) {

		this.root = createSubtree(tbsnRoot);

	}

	

	/**

	 * Print the tree on indented lines. 

	 * @return one line per node

	 */

	public String toString() {

		return root.indentedString(0);

	}



	/**

	 * Factory. Converts a {@link TreeBuilderSemantic.Node} into

	 * a subtree of nodes of this type.

	 * @param tbsn parser node at root of subtree to convert

	 */

	private Node createSubtree(TreeBuilderSemantic.Node tbsn) {

		return createSubtree(tbsn, false);

	}



	/**

	 * Private workhorse for public factory. Converts a

	 * {@link TreeBuilderSemantic.Node} into a subtree of nodes of this type.

	 * 

	 * @param tbsn parser node at root of subtree to convert

	 */

	private Node createSubtree(TreeBuilderSemantic.Node tbsn,

			boolean inParentheses) {

		List inputTokens = tbsn.getInputTokens();

		if (inputTokens.size() < 1)

			throw new IllegalArgumentException("empty inputTokens List in"+

					" TreeBuilderSemantic.Node "+tbsn);

		else if (inputTokens.size() > 1) {

			Object token0 = inputTokens.get(0);

			if (LEFT_PARENTHESIS.equals(inputTokens.get(0)))

				return createSubtree((TreeBuilderSemantic.Node)inputTokens.get(1), true);

			else

				return createOperator(inputTokens, inParentheses);

		} else {

			Object token = inputTokens.get(0);

			if (token instanceof String) 

				return createVariable((String) token, inParentheses);

			else if (token instanceof TreeBuilderSemantic.Node)

				return createSubtree((TreeBuilderSemantic.Node)token);  // node is a grammar artifact

			else

				throw new IllegalArgumentException("invalid type in inputTokens List: "+

					token.getClass().getName());

		}

	}

	

	/**

	 * Create an operator (tree) node. Evaluates each operand with 

	 * {@link #createSubtree(TreeBuilderSemantic.Node, Map}.

	 * @param inputTokens List from TreeBuilderSemantic.Node; exactly one

	 *        element should be the operator, of type String

	 * @param inParentheses whether the expression is enclosed in parentheses

	 * @return tree node created

	 */

	private Node createOperator(List inputTokens, boolean inParentheses) {

		List operands = new ArrayList();

		String operator = null;

		for (Iterator it = inputTokens.iterator(); it.hasNext(); ) {

			Object token = it.next();

			if (token instanceof String) {

				if (operator != null)

					throw new IllegalArgumentException("multiple operators in node: "+

							inputTokens);

				operator = (String) token;

			} else if (!(token instanceof TreeBuilderSemantic.Node)) {

				throw new IllegalArgumentException("unknown type "+

						token.getClass().getName()+" in token list ");

			} else {

				Node operand = createSubtree((TreeBuilderSemantic.Node) token);

				operands.add(operand);

			}

		}

		if (operator == null)

			throw new IllegalArgumentException("no operator in inputTokens: "+

					inputTokens);

		if (operands.size() < 1)

			throw new IllegalArgumentException("no operaands in inputTokens: "+

					inputTokens);

		Node node = new Node(operator, operands);

		node.inParentheses = inParentheses;

		subExprList.add(node);

		node.subExprColumn = subExprList.size();

		

		

		

		return node;

	}

	

	/**

	 * Create a variable (leaf) node. Inserts entry in

	 * {@link #variableTable} if none by this name is already there.

	 * @param name the variable's symbol

	 * @param inParentheses true if the expression is enclosed in parentheses

	 * @return leaf node found or created

	 */

	private Node createVariable(String name, boolean inParentheses) {

		variableSet.add(name);

		Node node = new Node(name);

		if (variableTable.get(name) == null)

			variableTable.put(name, node);

		node.inParentheses = inParentheses;

		return node;

	}

	/**

	 * @return Returns the root.

	 */

	public Node getRoot() {

		return root;

	}

	/**

	 * @return Returns the subExprList.

	 */

	public List getSubExprList() {

		return subExprList;

	}

	

 

	

	/**

	 * @return Returns the variableTable.

	 */

	public Map getVariableTable() {

		return variableTable;

	}

	



	public String[] Save_getSubExprArray() {

//		System.out.print("getSubExprArray "+subExprList);

//	    System.out.print("\n");

	    

	    subExprArray = new String[subExprList.size()];

	    

	    for (int j = 0; j < subExprList.size(); ++j) {

	    	   String[] le = ((LogicExprTree.Node) subExprList.get(j)).getLogicEvaluation();

	    	   System.out.println("le [" + j + "]" + le);

		   String SubExp = "";

		   

		   for (int i = 0; i < le.length; ++i) 

		   {   if (i > 0) SubExp = SubExp.concat(" ");

			   SubExp = SubExp.concat(le[i]);

	    	   System.out.println("le [" + j + ","+i+ "]" + le[i]);

			   

		   }

			   subExprArray[j] = SubExp;	

			   

			}



//	    for (int i = 0; i < subExprList.size(); ++i)

//	    {

//      	System.out.print("getSubExprArray[" + i + "]" + subExprArray[i]);

//	    System.out.print("\n");

//	    }

		return (subExprArray);

	}



	public String[] getSubExprArray() {

//		System.out.print("getSubExprArray "+subExprList);

//	    System.out.print("\n");

	    

	    subExprArray = new String[subExprList.size()];

	    

	    for (int j = 0; j < subExprList.size(); ++j) {

	    	subExprArray[j] = ((LogicExprTree.Node) subExprList.get(j)).toString(true);

	    	   System.out.println("le [" + j + "]" + subExprArray[j]);

			   

			}



//	    for (int i = 0; i < subExprList.size(); ++i)

//	    {

//      	System.out.print("getSubExprArray[" + i + "]" + subExprArray[i]);

//	    System.out.print("\n");

//	    }

		return (subExprArray);

	}







	

	public Node[] getExprTreeNode() {



	    

		Node[] nodes = new Node[subExprList.size()];

	    

	    for (int j = 0; j < subExprList.size(); ++j) {

	    	nodes[j] = (LogicExprTree.Node) subExprList.get(j);

	    	   

	   subExprList.toArray(nodes);





	    }

//	    for (int j = 0; j < subExprList.size(); ++j)

//	    {

//      	System.out.print("nodes[" + j + "]" + nodes[j]);

//	    System.out.print("\n");

//	    }	    

	   

		return (nodes);

	}

}





