(deftemplate MAIN::button 
   (slot name)
   (slot value))
(deftemplate MAIN::hint 
   (slot now))
(deftemplate MAIN::problem 
   (slot name) 
   (multislot interface-elements) 
   (multislot subgoals) 
   (slot done) 
   (slot description))
(deftemplate MAIN::textField 
   (slot name) 
   (slot value))

; tell productionRules file that templates have been parsed
(provide wmeTypes)
