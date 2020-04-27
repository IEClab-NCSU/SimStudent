package edu.cmu.pact.miss;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStGraphNavigator;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStNode;
import edu.cmu.pact.miss.ProblemModel.Graph.SimStProblemGraph;

public class ProblemChecker {
	
	/***
	 * 
	 * Let to finish
	 * @param oracleClass
	 * @param problemName
	 * @return
	 */
		public boolean isSolvable(String oracleClass,String problemName,BR_Controller brController){
			boolean answer = true;
			SimStProblemGraph brGraph = new SimStProblemGraph();
			SimStNode problemNode = new SimStNode(SimSt.convertToSafeProblemName(problemName),brGraph);
			brGraph.setStartNode(problemNode);
			brGraph.addSSNode(problemNode);
			
					
			Class[] parameters = new Class[3];
			parameters[0] = String.class;
			parameters[1] = SimStNode.class;
			parameters[2] = BR_Controller.class;
			
			
			try {
				Class oracle = Class.forName(oracleClass);
				Object oracleObj = oracle.newInstance();
				Method askMethod = oracle.getMethod("askNextStep",parameters);
				//System.out.println(" before while loop : "+answer);
				while(answer){
					//System.out.println("Inside the while loop");
					Sai nextStep = (Sai)askMethod.invoke(oracleObj,problemName,problemNode,brController);
					//System.out.println("Next Step  : "+nextStep);
					if(nextStep == null)
						return false;
					else if(nextStep.getA().equalsIgnoreCase("done"))
						break;
					else
						problemNode = new SimStGraphNavigator().simulatePerformingStep(problemNode,nextStep);				
				}
				//System.out.println(" After the while loop ");
				
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			
			return answer;
		}
		
		
}
