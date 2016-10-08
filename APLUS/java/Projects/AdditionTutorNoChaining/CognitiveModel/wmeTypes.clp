(deftemplate MAIN::button extends MAIN::__fact 
 (slot name))
(deftemplate MAIN::carry-goal extends MAIN::__fact "(Implied)" 
 (slot carry)
 (slot column))
(deftemplate MAIN::cell extends MAIN::__fact 
 (slot name) 
 (slot value) 
 (slot description) 
 (slot row-number)
 (slot column-number))
(deftemplate MAIN::column extends MAIN::__fact 
 (slot name) 
 (multislot cells) 
 (slot position) 
 (slot description))
(deftemplate MAIN::problem extends MAIN::__fact 
 (slot name) 
 (multislot interface-elements) 
 (multislot subgoals) 
 (slot done) 
 (slot description) 
 (slot turn))
(deftemplate MAIN::selection-action-input extends MAIN::__fact 
 (slot selection) 
 (slot action) 
 (slot input))
(deftemplate MAIN::table extends MAIN::__fact 
 (slot name) 
 (multislot columns))
(deftemplate MAIN::test extends MAIN::__fact "(Implied)" 
 (multislot __data))
