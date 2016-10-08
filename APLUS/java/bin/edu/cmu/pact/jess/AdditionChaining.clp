
(deftemplate addition  (slot name (default nil)) (slot problem (default nil)))
(deftemplate button  (slot name (default nil)))
(deftemplate cell  (slot name (default nil)) (slot value (default nil)) (slot description (default nil)) (slot row-number (default nil)))
(deftemplate column  (slot name (default nil)) (multislot cells) (slot position (default nil)) (slot description (default nil)))
(deftemplate initial-fact )
(deftemplate problem  (slot name (default nil)) (multislot interface-elements) (multislot subgoals) (slot done (default nil)) (slot description (default nil)) (slot turn (default nil)))
(deftemplate process-column-goal  (slot carry (default nil)) (slot column (default nil)) (slot first-addend (default nil)) (slot second-addend (default nil)) (slot sum (default nil)) (slot description (default nil)))
(deftemplate table  (slot name (default nil)) (multislot columns))
(deftemplate test  (multislot __data))
(deftemplate write-carry-goal  (slot carry (default nil)) (slot column (default nil)) (slot description (default nil)))
(deftemplate finish-problem-goal (slot begin-rule ))

(provide templates)
