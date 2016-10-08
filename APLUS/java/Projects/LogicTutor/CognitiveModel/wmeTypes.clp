(deftemplate MAIN::problem extends MAIN::__fact 
 (slot name) 
 (multislot interface-elements) 
 (multislot subgoals) 
 (multislot decomposition-alternatives) 
 (slot done-p) 
 (slot description) 
 (slot answer-table))

(deftemplate MAIN::answer-table extends MAIN::__fact 
 (slot name)
 (multislot variable-header-alternatives) 
 (multislot variable-columns) 
 (multislot answer-rows) 
 (multislot answer-columns) 
 (slot description))

(deftemplate MAIN::answer-column extends MAIN::__fact 
 (slot name)
 (slot col-number) 
 (slot selected-alternative) 
 (slot column) 
 (slot column-header) 
 (slot radio-button) 
 (multislot alternate-col-logic) 
 (multislot alternate-col-values) 
 (slot description))

(deftemplate MAIN::answer-row extends MAIN::__fact
 (slot name) 
 (multislot cells) 
 (slot description))

(deftemplate MAIN::table extends MAIN::__fact 
 (slot name) 
 (multislot columns))

(deftemplate MAIN::textField extends MAIN::__fact 
 (slot name) 
 (slot value) 
 (slot description))

(deftemplate MAIN::column extends MAIN::__fact 
 (slot name) 
 (multislot cells) 
 (slot position) 
 (slot description))

(deftemplate MAIN::cell extends MAIN::__fact 
 (slot name) 
 (slot value) 
 (slot description) 
 (slot row-number)
 (slot column-number)
 (slot col-number))

(deftemplate MAIN::button extends MAIN::__fact 
 (slot name))


(deftemplate MAIN::decomposition-alternative extends MAIN::__fact
 (slot name) 
 (slot decomposition-name) 
 (slot selected-alternative-p) 
 (multislot logic-evaluations) 
 (multislot header-values) 
 (slot description))

(deftemplate MAIN::logic-evaluation extends MAIN::__fact 
 (slot name)
 (slot answer-p) 
 (multislot expression) 
 (slot description))

(deftemplate MAIN::logic-header-alternative extends MAIN::__fact 
 (slot name)
 (slot value) 
 (slot already-used-p) 
 (slot logic-evaluation) 
 (slot description))

(deftemplate MAIN::logic-variable-column extends MAIN::__fact 
 (slot name)
 (multislot cells) 
 (slot description))

(deftemplate MAIN::variable-column extends MAIN::__fact 
 (slot name)
 (multislot cells) 
 (slot description))

(deftemplate MAIN::variable-header-alternative extends MAIN::__fact 
 (slot name)
 (slot already-used-p) 
 (slot value) 
 (slot description))

(deftemplate MAIN::variable-value extends MAIN::__fact 
 (slot name)
 (slot row-number) 
 (slot value) 
 (slot description))

(deftemplate MAIN::radioButton extends MAIN::__fact 
 (slot name) 
 (slot value))



(deftemplate MAIN::enter-expression-goal extends MAIN::__fact 
 (slot description))

(deftemplate MAIN::check-parse-results-goal extends MAIN::__fact 
 (slot description) 
 (slot parse-event)
 (slot parse-expression-results))

(deftemplate MAIN::assign-expression-headings-goal extends MAIN::__fact 
 (slot description) 
 (slot just-assigned-heading-p)
 (slot column-header)
 (slot column-header-name))

(deftemplate MAIN::assign-variable-headings-goal extends MAIN::__fact 
 (slot description) 
 (slot just-assigned-heading-p)
 (slot variable-header)
 (slot variable-header-name))

(deftemplate MAIN::apply-logic-operators-goal extends MAIN::__fact 
 (slot description))
 
 (deftemplate studentValues extends MAIN::__fact
    (slot selection) 
    (slot action) 
    (slot input))

(require* logic-rule-functions "LogicTutor/CognitiveModel/logic-rule-functions.clp")
(require logic-rule-functions "logic-rule-functions.clp")

;; tell .pr and .wme files that the wmeTypes are provided here
(provide wmeTypes)

