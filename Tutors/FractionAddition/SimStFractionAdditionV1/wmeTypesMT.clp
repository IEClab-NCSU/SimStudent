;; First define templates for the model classes so we can use them in our behavior
;; rules. This doesn't create any model objects --it just tells Jess to examine the
;; classes and set up templates using their properties.

(import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory)

;; Define the templates for the working memory in the MAIN module of Jess
;; Working Memory is composed of Invisible WME + Interface WME
;; Invisible WME (shadow fact) follows the student actions and non-dormin widget components

(deftemplate MAIN::ModelTraceWorkingMemory (declare (from-class ModelTraceWorkingMemory)))
(deftemplate MAIN::studentValues
	(slot selection)
	(slot action)
	(slot input))

;; TODO: Convert the working memory to slots as shown below and get the production rules
;; to work with this format.
;;(deftemplate MAIN::status
;;	(slot name)
;;	(slot value))
;;(deftemplate MAIN::quiz
;;	(slot name)
;;	(slot value)
;;	(slot result)
;;	(multislot problems-failed)
;;	(multislot problems-passed))

;; Interface WME (unordered facts) are representative of the student interface mainly 
;; CTAT dormin widget components.

(deftemplate MAIN::button 
	(slot name)
	(slot value))
(deftemplate MAIN::cell 
	(slot name)
	(slot value)
	(slot description)
	(slot row-number)
	(slot column-number))
(deftemplate MAIN::column 
	(slot name)
	(multislot cells)
	(slot position)
	(slot description))
(deftemplate MAIN::label
	(slot name)
	(slot value))
(deftemplate MAIN::problem
	(slot name)
	(multislot interface-elements)
	(multislot subgoals)
	(slot done)
	(slot description))
(deftemplate MAIN::table
	(slot name)
	(multislot columns))
