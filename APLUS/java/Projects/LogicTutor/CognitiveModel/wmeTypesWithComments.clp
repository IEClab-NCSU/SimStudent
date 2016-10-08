;;
;;  Special Selection-Action-Input widgets
;;

(deftemplate MAIN::selection-action-input extends MAIN::__fact 
 (slot selection) 
 (slot action) 
 (slot input))

(deftemplate MAIN::special-tutor-fact extends MAIN::__fact 
 (slot selection) 
 (slot action) 
 (slot input))

(deftemplate MAIN::special-tutor-fact-buggy extends MAIN::special-tutor-fact 
 (slot selection) 
 (slot action) 
 (slot input) 
 (multislot buggy-message))

(deftemplate MAIN::special-tutor-fact-correct extends MAIN::special-tutor-fact 
 (slot selection) 
 (slot action) 
 (slot input) 
 (multislot hint-message))

(deftemplate MAIN::special-wme extends MAIN::__fact 
 (slot selection) 
 (slot action) 
 (slot input) 
 (multislot hint-message) 
 (multislot buggy-message))

(deftemplate MAIN::studentValues extends MAIN::__fact 
 (slot selection) 
 (slot action) 
 (slot input))

;;
;;  Templates to define GUI widgets
;;

(deftemplate MAIN::button extends MAIN::__fact 
 (slot name))

(deftemplate MAIN::radioButton extends MAIN::__fact 
 (slot name) 
 (slot value))

(deftemplate MAIN::cell extends MAIN::__fact 
 (slot name) 
 (slot value) 
 (slot description) 
 (slot row-number)
 (slot column-number)
 (slot col-number))


(deftemplate MAIN::textField extends MAIN::__fact 
 (slot name) 
 (slot value)
 (slot description))

(deftemplate MAIN::column extends MAIN::__fact 
 (slot name) 
 (multislot cells) 
 (slot position) 
 (slot description))

(deftemplate MAIN::table extends MAIN::__fact 
 (slot name) 
 (multislot columns))

;;
;;  Special problem template
;;

(deftemplate MAIN::problem extends MAIN::__fact 
 (slot name) 
 (multislot interface-elements) 
 (multislot subgoals)
 (multislot decomposition-alternatives) 
 (slot done-p) 
 (slot description) 
 (slot answer-table))

;;
;;  Subgoal templates
;;

(deftemplate MAIN::apply-logic-operators-goal extends MAIN::__fact 
 (slot description))

(deftemplate MAIN::assign-expression-headings-goal extends MAIN::__fact 
 (slot description)
 (slot just-assigned-heading-p))

(deftemplate MAIN::assign-variable-headings-goal extends MAIN::__fact 
 (slot description)
 (slot just-assigned-heading-p))

(deftemplate MAIN::check-parse-results-goal extends MAIN::__fact 
 (slot description)
 (slot parse-expression-results))

(deftemplate MAIN::create-expression-goal extends MAIN::__fact 
 (slot description)
 (slot parse-expression-result))

(deftemplate MAIN::enter-expression-goal extends MAIN::__fact 
 (slot description))

;;
;;  Other special templates
;;

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

(deftemplate MAIN::answer-table extends MAIN::__fact
  (slot name)
  (multislot variable-header-alternatives)
  (multislot variable-columns)
  (multislot answer-rows)
  (multislot answer-columns)
  (slot description))

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
