;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;																			    ;;
;;  This file contains Jess / SimStudent production rules to provide Cognitive  ;;
;;  help (Cognitive Model) when teaching SimStudent.							;;	
;;																				;;					
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;		

;; First define templates for the model classes so we can use them
;; in our behavior rules. This doesn't create any model objects --
;; it just tells Jess to examine the classes and set up templates
;; using their properties

(import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory)
(import edu.cmu.pact.miss.SimSt)

(deftemplate ModelTraceWorkingMemory (declare (from-class ModelTraceWorkingMemory)))

;; Now define the behavior rules themselves. Each rule matches a set
;; of conditions.

;; SIMST-CORRECT-STEP-PERFORMED
;;
;; If SimStudent does a step correctly
;; Then
;; (Student tutor) should give a positive feedback.
;;
(defrule simst-correct-step
	"If SimStudent does a step correctly then give a positive feedback"
	(declare (salience 100))
	(ModelTraceWorkingMemory {stepCorrectness == "correct"} {actor ==  "simstudent"} (step ?step))
	=>
	(bind ?ssName (get-member SimSt SimStName))
	(predict-tutor-action yes ButtonPressed -1)
	(construct-cl-hint-message
		"[ " ?ssName "has performed the step" ?step "correctly. ]" ))
	

;; SIMST-INCORRECT-STEP-PERFORMED
;;
;; If SimStudent does a step incorrectly
;; Then
;; (Student tutor) should give a negative feedback.
;;
(defrule simst-incorrect-step
	"If SimStudent does a step incorrectly then give a negative feedback"
	(declare (salience 100))
	(ModelTraceWorkingMemory {stepCorrectness == "incorrect"} {actor == "simstudent"} (step ?step))
	=>
	(bind ?ssName (get-member SimSt SimStName))
	(predict-tutor-action no ButtonPressed -1)
	(construct-cl-hint-message
		"[ " ?ssName "has performed the step" ?step "incorrectly. ]" ))
	
	
;; SIMST-HELP-REQUEST
;;
;; If SimStudent makes a help request
;; Then
;; (Student tutor) should give a correct help.
(defrule simst-help-request
	"If SimStudent makes a help request then give a correct help"
	(declare (salience 100)) ;; Should be preferred over BUG-rule
	(ModelTraceWorkingMemory {helpRequested == "true"} {actor == "simstudent"} (problemNodeName ?name))
	=>
	(bind ?ssName (get-member SimSt SimStName))	
	(bind ?ruleSAI (get-correct-sai ?name))
	(predict-tutor-action ?ruleSAI  UpdateTable  ?ruleSAI)
	(construct-cl-hint-message 
		"[ " ?ruleSAI " ]" ))