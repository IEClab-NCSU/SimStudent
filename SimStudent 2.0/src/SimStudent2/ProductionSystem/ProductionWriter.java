/**
 * Generate a production rule object that can be output to a production rule file
 * 
 * 
 * Created: Apr 12, 2015 9:21:09 PM
 * @author mazda
 * 
 */
package SimStudent2.ProductionSystem;

import SimStudent2.LearningComponents.Production;

/**
 * @author mazda
 *
 */
public class ProductionWriter {

	/*
(defrule divide-typein

?var561 <- (problem  (interface-elements $?m596 ?var569 $?)   )
?var569 <- (table  (columns ?var570))
?var570 <- (column  (cells ?var571 ? ? ? ? ?)  )
?var571 <- (cell (name ?foa0) (value ?val0&~nil)   )

?var561a <- (problem  (interface-elements ? ? ? ? ? ? ? ? ? ?var585 ?)   )
?var585 <- (table  (columns ?var586))
?var586 <- (column  (cells ?var587 ? ? ? ? ?)  )
?var587 <- (cell (name ?foa1) (value ?val1&~nil)   )

?var561b <- (problem  (interface-elements $?m608 ?var569 $?)   )
?var569a <- (table  (columns ?var570))
?var570a <- (column  (cells ? ?var572 ? ? ? ?)  )
?var572 <- (cell  (name ?selection) (value ?input&nil)   )

(test (distinctive ?var571 ?var572))
(test (same-table ?var571 ?var572))
(test (consecutive-row ?var571 ?var572))
(test (distinctive ?var571 ?var587))
(test (same-row ?var571 ?var587))
(test (distinctive ?var587 ?var572))
(test (consecutive-row ?var587 ?var572))

(test (not (is-skill-subtract ?val1 )) )

=>

(bind ?val2 (get-operand ?val1))
(bind ?input (div-term ?val0 ?val2))
;; (here-is-the-list-of-foas ?foa0 ?foa1)
;; (predict-algebra-input ?selection UpdateTable ?input )
;; (modify ?var572 (value ?*sInput*))
(modify ?var572 (value ?input))
;; (construct-message "[ Enter" ?input ".]")

(printout t "div-typein " ?input crlf)

)
	 */
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// Production components
	//
	String productionName;
	
	String wmePath;
	
	String topologicalConstraints;
	
	String lhsConditions;
	
	String operatorSequence;
	
	// (modify ?var572 (value ?input))
	String modifyInput;

	// Production templates
	//
	// (defrule
	static final String DEFRULE = "defrule";
	// =>
	static final String LR_SEPARATOR = "=>";
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param production
	 */
	public ProductionWriter(Production production) {
		
		this.productionName = production.getName();
		this.wmePath = production.getFormattedLhsWmePath();
		// ToDo ProductionWriter: topologicalConstraints
		// this.topologicalConstraints = production.getFormatedTopologicalConstraints(); 
		this.lhsConditions = production.getFormattedLhsConditions();
		this.operatorSequence = production.getFormattedRhsOperators();
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * 
	 */
	public void printToFile() {
		
		String productionRule = "(" + DEFRULE + " ";
		productionRule += productionName + "\n";
		productionRule += "\n";
		productionRule += wmePath + "\n";
		productionRule += "\n";
		productionRule += lhsConditions + "\n";
		productionRule += "\n";
		productionRule += LR_SEPARATOR + "\n";
		productionRule += "\n";
		productionRule += operatorSequence + "\n";
		productionRule += ")";
		
		System.out.println(productionRule);
	}
}
