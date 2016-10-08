;; These templates were generated automatically by CTAT
(deftemplate problem  
    (slot name) 
    (multislot interface-elements) 
    (multislot subgoals) 
    (slot done))
(deftemplate table  
    (slot name) 
    (multislot columns))
(deftemplate column  
    (slot name) 
    (multislot cells) 
    (slot position))
(deftemplate cell  
    (slot name) 
    (slot value) 
    (slot row-number) 
    (slot column-number))
(deftemplate button
    (slot name)
    (slot value))

;; These templates were later added "manually"
(deftemplate process-column-goal  
    (slot carry) 
    (slot column) 
    (slot first-addend) 
    (slot second-addend) 
    (slot sum))
(deftemplate write-sum-goal
    (slot carry)
    (slot sum)
    (slot column))
(deftemplate write-carry-goal  
    (slot carry) 
    (slot column))

; tell productionRules file that templates have been parsed
(provide wmeTypes)
