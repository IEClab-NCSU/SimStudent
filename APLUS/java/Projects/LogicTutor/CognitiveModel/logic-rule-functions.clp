;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;

;;  This file contains utility functions for the Logic Cognitive Tutor. 

;;

;;  Written By:  Bruce McLaren

;;  Date:        July 25, 2003

;;

;;  Modified By:  Chang-Hsin Chang

;;  Date:        Aprile 10, 2007

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;





;; Global used to match characters to letters of the alphabet; No "V" allowed to avoid conflict with the disjunction op ("v")



;; (defglobal ?*letters* = (create$ A B C D E F G H I J K L M N O P Q R S T U W X Y Z))          

(defglobal ?*letters* = (create$ "A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M" "N" "O" "P" "Q" "R" "S" "T" "U" "W" "X" "Y" "Z"))

;;  Max variables allowed in an expression based on interface size





(defglobal ?*max-variables* = 4)    



;;  Variables allowed in an expression



(defglobal ?*valid-variables* = (create$ "P" "Q" "R" "S"))



;;  Variables used in the current expression



    

  (defglobal ?*current-variables* = (create$ ))

    

 ;; (defglobal ?*current-variables* = (create$ P Q))





 (deffunction remove (?substring ?string)    

  (bind ?start (str-index ?substring ?string))

  (bind ?sub-length (str-length ?substring))

  (while ?start

     (bind ?second-end (str-length ?string))

     (bind ?first-end (- ?start 1))

     (bind ?second-start (+ ?start ?sub-length))

     (bind ?string  (str-cat (sub-string 0 ?first-end ?string)(sub-string ?second-start ?second-end ?string)))

     (bind ?start (str-index ?substring ?string))

  )

 (return ?string) 

 ) 





 (deffunction remove-space (?string)    

  (bind ?StringLength (str-length ?string))

  (bind ?new-string "")

  (bind ?i 1)

  

  (while  (<= ?i ?StringLength)  

    (bind ?char (sub-string ?i ?i ?string))

    (if (neq ?char " " ) then

      (bind ?new-string (str-cat ?new-string ?char)))

     (bind ?i (+ ?i 1))

   )

 (return ?new-string) 

 ) 

(deffunction remove-unary-operator-parens (?string)    
  (bind ?StringLength (str-length ?string))
  (bind ?new-string "")
  (bind ?i 1)

  (while  (<= ?i ?StringLength) 

    (bind ?char (sub-string ?i ?i ?string))
    (if (neq ?char "(" ) then (bind ?new-string (str-cat ?new-string ?char))
     else 
       (bind ?j (+ ?i 1))
       (bind ?next-char (sub-string ?j ?j ?string))
       (bind ?k (+ ?i 3))
       (bind ?next-paren-char (sub-string ?k ?k ?string))
       (if (and (eq ?next-char "~" )(eq ?next-paren-char ")")) then 

          (bind ?new-string (str-cat ?new-string ?next-char))
          (bind ?j (+ ?j 1))
          (bind ?operand (sub-string ?j ?j ?string))
          (bind ?new-string (str-cat ?new-string ?operand))
          (bind ?i (+ ?j 1))
       else (bind ?new-string (str-cat ?new-string ?char))
       )
     )       
     (bind ?i (+ ?i 1))
   )

 (return ?new-string) 
) 




(deffunction Ignore-Blanks-String-Equal (?string1 ?string2)     

;;   (printout t crlf "Ignore-Blanks-String-Equal: ?tutor-input = " ?string1 " with ?student-input = " ?string2 crlf)

     (eq (remove " " ?string1) (remove " " ?string2))

 )    



;; Does this string enclosed in parentheses

 (deffunction Outer-Parens? (?string)    

  (bind ?i 1)

  (bind ?left-paren (str-index "(" ?string))

  (if (neq ?left-paren 1) then (return FALSE)) ;; without left parenthesis

  (bind ?More-L-Parens 0)

  (bind ?StringLength   (str-length ?string))

  (while  (< ?i ?StringLength)  

     (if (eq "(" (sub-string ?i ?i ?string)) 

      then (bind ?More-L-Parens (+ ?More-L-Parens 1))

      else

        (if  (eq ")" (sub-string ?i ?i ?string))

          then (bind ?More-L-Parens (- ?More-L-Parens 1)))

     )

     (if (= ?More-L-Parens 0) then (return FALSE)) ;; found the right parenthesis to match the first left parenthesis before the end of the string

     (bind ?i (+ ?i 1))

  )  

  (if (eq ")" (sub-string ?i ?i ?string)) 

     then (return TRUE)  ;; Yes, find the matched right parenthesis at the end of the string

     else (return FALSE))

 )   



 (deffunction remove-Outer-Parens (?string)    

  (bind ?left-paren (str-index "(" ?string))

  (bind ?right-paren (str-index ")" ?string))

  (bind ?end (str-length ?string))

  (while  (Outer-Parens? ?string)

  ;;  (printout t crlf ?string crlf)

     (bind ?string (sub-string 2 (- ?end 1) ?string))

     (bind ?left-paren (str-index "(" ?string))

     (bind ?right-paren (str-index ")" ?string))

     (bind ?end (str-length ?string))

  )

 (return ?string) 

 ) 



(deffunction Ignore-Blanks-Outer-Parens-String (?string)    

     (remove-Outer-Parens (remove " " ?string))

  ) 



 (deffunction equals-Ignore-Case (?tutor-input ?student-input) 

   (printout t crlf "equals-Ignore-Case: ?tutor-input = " ?tutor-input " with ?student-input = " ?student-input crlf)

   (eq (upcase ?tutor-input) (upcase ?student-input))

  )  

;;  (remove-unary-operator-parens (remove-Outer-Parens  (remove-space "~p&~q")))
;;  (remove-unary-operator-parens (remove-Outer-Parens  (remove-space "((~P)&(~Q))")))
;; (Ignore-SpaceOuterUnaryParens-String-Equal "~p&~q" "((~P)&(~Q))")
(deffunction Ignore-SpaceOuterUnaryParens-String-Equal (?string1 ?string2)    

   (printout t crlf "Ignore-Blanks-Outer-Parens-String-Equal: ?tutor-input = " ?string1 " with ?student-input = " ?string2 crlf)

     (equals-Ignore-Case (remove-unary-operator-parens (remove-Outer-Parens  (remove-space ?string1)))
                         (remove-unary-operator-parens (remove-Outer-Parens (remove-space  ?string2)))
     )

  )   

     

(deffunction ModifyInputString ()

 (bind ?*sInput* (Ignore-Blanks-Outer-Parens-String ?*sInput*))

;; (printout t crlf "?*sInput* = " ?*sInput* crlf) 

(return TRUE)

	)





(deffunction GenVariableValue (?col-index ?row-index)

  (bind ?interval (** 2 ?col-index))

  (bind ?middle   (** 2 (- ?col-index 1)))

  (if (< (mod (- ?row-index 1) ?interval) ?middle) 

    then T

    else F

  ))







;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;

;;   Predicate Functions used on the LHS of rules

;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;

;;  This predicate function checks whether its argument is a variable (i.e., a letter).

;;

(deffunction variable-p (?operand)

  "Predicate to determine if the given operand is a variable (i.e., a letter)."

 ;; (printout t crlf "variable-p: "  ?operand " "  (member$ ?operand ?*letters*) crlf)

  (member$ ?operand ?*letters*))

  

(deffunction variable-eq (?operand1 ?operand2)

  "Predicate to determine if the given operand is a variable (i.e., a letter)."

  (printout t crlf "variable-eq: "  ?operand1 " " ?operand2 (eq ?operand1 (sym-cat ?operand2)) crlf)

;;  (eq ?operand1 ?operand2))

  (eq ?operand1 (sym-cat ?operand2)))

 

(deffunction Int-Sym-eq (?operand1 ?operand2)

  "Predicate to determine if the given operand is a variable (i.e., a letter)."

 ;; (printout t crlf "Int-Sym-eq: "  ?operand1 " " ?operand2 " " (call Integer parseInt (str-cat ?operand2)) crlf)

  (eq ?operand1 (call Integer parseInt (str-cat ?operand2))))

 

;;

;;  This predicate function checks whether its argument is a dependent value.

;;  A dependent value is a string containing an integer.  If the argument is

;;  not a variable, it must be a dependent value.

;;

(deffunction dependent-value-p (?operand)

  "Predicate to determine if the given operand is a dependent value."

  (not (variable-p ?operand)))



;;

;;  This predicate function checks whether the given cell should be completed, given the currently selected row and

;;  number of variables in the expression.

;; 

(deffunction cell-must-be-filled-p (?row-index)

  (>= (** 2 (length$ ?*current-variables*))      ;;  This is the number of rows in the current table, given the # of variables

      ?row-index)                                ;;  This is the row number of the currently selected cell

)



;;

;;  This predicate function tests that none of the cells in the given column 

;;  are empty.

;;  test : (all-cells-have-been-filled-p (fact-id 38 getFactId))



(deffunction all-cells-have-been-filled-p (?column)
 ;; (printout t crlf "all-cells-have-been-filled-p check column " ?column  crlf)
  (bind ?cells (fact-slot-value ?column cells))

  (foreach ?cell ?cells
     (bind ?cell-answered-p (fact-slot-value ?cell value))
     (if (eq ?cell-answered-p nil)  then (return FALSE))
  )
 ;;   (printout t crlf "all-cells-have-been-filled-p = TRUE")
 (return TRUE)
)



(deffunction reverse-boolean-value (?answer)

   (if (eq ?answer "T") then "F" else "T"))



;;

;;  This predicate function tests that none of the elements in the given list of 

;;  variable-header-alternatives are (already-used-p nil) and (value ~nil).

;;  It there are none, there is no expression that has not yet been used.

;;

(deffunction all-alternatives-used-p (?alternatives)

(printout t crlf "[Alternatives] " ?alternatives crlf)

  (foreach ?alternative ?alternatives

        (bind ?already-used-p (fact-slot-value ?alternative already-used-p))

	(bind ?value (fact-slot-value ?alternative value))

(bind ?name (fact-slot-value ?alternative name))

 ;; (printout t crlf "Alternative: " ?name " has already-used-p = '" ?already-used-p "' and value = '" ?value "'" crlf)

	(if (and (eq  ?already-used-p nil)

		 (neq ?value nil)) then

	    (return FALSE)))

  (return TRUE)

)



;; (deffunction all-alternatives-used-p (?alternatives) (return FALSE))

;; (deffunction all-alternatives-used-p (?alternatives) (return TRUE))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;

;;   Functions used on the RHS of rules

;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;

;;  This function applies the logic operator ~ to the given truth value.

;;  A truth value of "T" or "F" is returned.

;;

(deffunction apply-negation-operator (?operand1)

  "Apply the logic operator ~ to the operand."

  (if (eq ?operand1 T) then (return F) else (return T)))

 

;;

;;  This function applies the logic operators &, v, ->, and <-> to given truth values.

;;  A truth value of "T" or "F" is returned.

;;

(deffunction apply-logic-operator-old (?op ?operand1 ?operand2)

  "Apply logic operators &, v, ->, and <-> to given truth values."

  (if (eq ?op "&") then

    (if (and (eq ?operand1 "T") (eq ?operand2 "T")) then "T" else "F")

  else

  (if (eq ?op "|") then

    (if (and (eq ?operand1 "F") (eq ?operand2 "F")) then "F" else "T")

  else

  (if (eq ?op "->") then

    (if (and (eq ?operand1 "T") (eq ?operand2 "F")) then "F" else "T")

  else

  (if (eq ?op "<->") then

    (if (or (and (eq ?operand1 "T") (eq ?operand2 "T"))

	    (and (eq ?operand1 "F") (eq ?operand2 "F"))) then "T" else "F"))))))



(deffunction apply-logic-operator (?op ?operand1 ?operand2)

  "Apply logic operators &, v, ->, and <-> to given truth values."

  (if (eq ?op (sym-cat &)) then

    (if (and (eq ?operand1 T) (eq ?operand2 T)) then T else F)

  else

  (if (eq ?op (sym-cat |)) then

    (if (and (eq ?operand1 F) (eq ?operand2 F)) then F else T)

  else

  (if (eq ?op ->) then

    (if (and (eq ?operand1 T) (eq ?operand2 F)) then F else T)

  else

  (if (eq ?op <->) then

    (if (or (and (eq ?operand1 T) (eq ?operand2 T))

	    (and (eq ?operand1 F) (eq ?operand2 F))) then T else F))))))



;;

;;  Given an element and a list, this function replaces an element of the same

;;  name in the list.

;;

(deffunction replace-element (?replacement-element ?element-list)

;(printout t crlf "List at beginning: " ?element-list crlf)

  (bind ?count 1)

  (bind ?replacement-element-name (fact-slot-value ?replacement-element name))

  (foreach ?element ?element-list

     (bind ?element-name (fact-slot-value ?element name))

;(printout t crlf "Comparing '" ?element-name "' and '" ?replacement-element-name "'" crlf)

     (if (eq ?element-name ?replacement-element-name) then

;(printout t crlf "      ***  MATCH!!!  '" ?element-name "' and '" ?replacement-element-name "'" crlf)

	(bind ?pointer ?count))

     (bind ?count (+ ?count 1)))

;(printout t crlf "Replacing '" ?element "' with '" ?replacement-element "'" crlf)

  (bind $?replacement-list (replace$ ?element-list ?pointer ?pointer ?replacement-element))

;(printout t crlf "List at end: " $?replacement-list crlf)

  (return $?replacement-list))



;;

;; Function to return max number of variables in the interface

;;

(deffunction get-max-variables ()

  ?*max-variables*

)



;;

;; Function to return valid variables

;;

(deffunction get-valid-variables ()

  ?*valid-variables*

)



;;

;; Function to return the first valid variable

;;

(deffunction get-first-valid-variable ()

  (nth$ 1 ?*valid-variables*)

)



;;

;; Function to return the second valid variable

;;

(deffunction get-second-valid-variable ()

  (nth$ 2 ?*valid-variables*)

)



;;

;; Function to return the third valid variable

;;

(deffunction get-third-valid-variable ()

  (nth$ 3 ?*valid-variables*)

)



;;

;; Function to return the fourth valid variable

;;

(deffunction get-fourth-valid-variable ()

  (nth$ 4 ?*valid-variables*)

)



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;

;;   Functions used on the RHS of rules to check Selection-Action-Input

;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;

;; Predicate that checks for two strings being the same, ignoring blanks and case.

;;

;;(deffunction Ignore-Blanks-String-Equal (string1 string2)

;;"Predicate that checks for equal strings, ignoring blanks and case"

;;  (string-equal

;;   (remove #\space string1)

;;   (remove #\space string2)))



;;

;; Predicate that checks for two strings NOT being the same, ignoring blanks and case.

;;

;;(deffunction Ignore-Blanks-String-Not-Equal (string1 string2)

;;  "Predicate that checks for unequal strings, ignoring blanks and case"

;;  (Not (Ignore-Blanks-String-Equal string1 string2)))



;;

;; Predicate that checks for two strings being the same, ignoring blanks, case, and outer parens.

;;

;;(deffunction Ignore-Blanks-Outer-Parens-String-Equal (string1 string2)

;;  "Predicate that checks for equal strings, ignoring blanks, case, and outer parens"

;;  (let* ((string1-no-blanks (remove #\space string1))

;;         (new-string1 string1-no-blanks)

;;         (left-paren1 (position #\( new-string1))

;;         (right-paren1 (position #\) new-string1 :from-end t))

;;         (string2-no-blanks (remove #\space string2))

;;         (new-string2 string2-no-blanks)

;;         (left-paren2 (position #\( new-string2))

;;         (right-paren2 (position #\) new-string2 :from-end t)))

;;    (when (and left-paren1 (= left-paren1 0) right-paren1 (= right-paren1 (- (length new-string1) 1)))

;;      ;;

;;      ;;  Remove outer parens

;;      ;;

;;      (setf new-string1 (remove #\) new-string1 :start (- (length new-string1) 1) :end (length new-string1)))

;;      (setf new-string1 (remove #\( new-string1 :start 0 :end 1))

;;      ;;

;;      ;;  If remaining parens are not balanced, we can't remove outer parens.  Return to original form.

;;      ;;

;;      (if (not (balanced-parens-p new-string1)) (setf new-string1 string1-no-blanks))

;;    )

;;    (when (and left-paren2 (= left-paren2 0) right-paren2 (= right-paren2 (- (length new-string2) 1)))

;;      ;;

;;      ;;  Remove outer parens

;;      ;;

;;      (setf new-string2 (remove #\) new-string2 :start (- (length new-string2) 1) :end (length new-string2)))

;;      (setf new-string2 (remove #\( new-string2 :start 0 :end 1))

;;      ;;

;;      ;;  If remaining parens are not balanced, we can't remove outer parens.  Return to original form.

;;      ;;

;;      (if (not (balanced-parens-p new-string2)) (setf new-string2 string2-no-blanks))

;;    )

;;    (string-equal new-string1 new-string2)))



;;

;; Predicate that checks for two strings NOT being the same, ignoring blanks, case, and outer parens.

;;

;;(deffunction Ignore-Blanks-Outer-Parens-String-Not-Equal (string1 string2)

;;  "Predicate that checks for unequal strings, ignoring blanks, case, and outer parens"

;;  (Not (Ignore-Blanks-Outer-Parens-String-Equal string1 string2)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;

;;   Utility functions used by other functions in this file.

;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;

;;  Checks whether the given string has balanced parentheses.

;;

;;(deffunction balanced-parens-p (string)

;;  "Checks whether the given string has balanced parentheses."

;;  (block check-parens

;;    (do* ((length-of-string (length string))        ;; Get length of the string.

;;          (i 0 (+ i 1))                             ;; Loop counter, points to successive chars in string.

;;          (paren-count 0)

;;          next-char)

;;         ((>= i length-of-string)                   ;; Stop loop when counter exceeds length of input sentence.

;;          (if (> paren-count 0)

;;            (return-from check-parens NIL)          ;; Unbalanced parens

;;            (return-from check-parens T)))          ;; Balanced parens

;;      (setf next-char (elt string i))               ;; Start the loop by setting next-char to current character in sentence.

;;      (if (char-equal next-char '#\()               ;;  "(" (left parenthesis) encountered

;;          (incf paren-count))

;;      (if (char-equal next-char '#\))               ;;  ")" (right parenthesis) encountered

;;          (decf paren-count))

;;      (if (< paren-count 0)

;;        (return-from check-parens NIL)))))          ;; Unbalanced parens

  

;;

;;  This predicate function checks whether its argument is a "T" or "F"

;;

(deffunction Answer-is-T-or-F (?answer)

  "Predicate to determine if the given answer is a T or F."

  (or (eq ?answer NIL) (eq ?answer "T") (eq ?answer "F"))) 



(deffunction SetUpCurrentVariablesHeader (?VariableList ?answer-table)

  (bind ?i 0)

  (bind ?vars (create$))

  (bind ?variable-header-alternatives (create$))

  (foreach ?v ?VariableList

   (bind ?vars (create$ ?vars (sym-cat ?v)))

   (bind ?i (+ ?i 1))

  

   (bind ?VarHeaderAlt1Name (sym-cat "variable-header-alternative" ?i))

   (bind ?VarHeaderAlt1Description (str-cat "VARIABLE-HEADER-ALTERNATIVE" ?i))

   (bind ?variable-header-alternative (assert(MAIN::variable-header-alternative (name ?VarHeaderAlt1Name)

                                                                                (already-used-p nil)

                                                                                (value (sym-cat ?v))

                                                                                (description ?VarHeaderAlt1Description)

  )))

  (bind ?variable-header-alternatives (create$ ?variable-header-alternatives ?variable-header-alternative))

  

  

 (modify ?answer-table

         (variable-header-alternatives ?variable-header-alternatives)

     )

 

  )

  (bind ?*current-variables*  ?vars)



)

  

  

  



(deffunction GenVariableValueRow (?VarNum ?rowNum)

  (bind ?var-index 1)

  (bind ?VariableRowList (create$ ))

  (while (<= ?var-index ?VarNum)

     (bind ?VarName (sym-cat "variable" ?var-index "-value" ?rowNum))

     (bind ?VV 

              (assert(MAIN::variable-value 

                        (name ?VarName)

                        (row-number ?rowNum)

                        (value (GenVariableValue (- (+ ?VarNum 1) ?var-index) ?rowNum))  ;; need to reverse the order, since the interface variable value assigned from Left to Right

     )))

     (bind ?VariableRowList (create$ ?VariableRowList ?VV))

   (bind ?var-index (+ ?var-index 1))

  )

 (return ?VariableRowList)

)





(deffunction create-wme (?parse-result-struct ?parse-result-VariableSet ?problem )



 (bind ?answer-table (fact-slot-value ?problem answer-table))

 (SetUpCurrentVariablesHeader ?parse-result-VariableSet  ?answer-table)

 ;; (printout t crlf "current-variables: " ?*current-variables* crlf)



 (bind ?SubTreeList (call ?parse-result-struct getSubExprArray))

 (bind ?nodes (call ?parse-result-struct getExprTreeNode))

 (bind ?VarNum (length$ ?*current-variables*))

 (bind ?table (nth$ 2 (fact-slot-value ?problem interface-elements)))

  

 (bind ?alternativeX (nth$ 1 (fact-slot-value ?problem decomposition-alternatives)))

  (bind ?ColSize (length$ ?SubTreeList))

  (bind ?RowSize (** 2 ?VarNum))

  (bind ?Col-index 1)

  (bind ?ColumnList (create$ ))

  (bind ?Alt1LogicList (create$ ))

  (bind ?Alt1CList (create$ ))

 ;; (printout t crlf "CreateTruthTableCell " ?ColSize crlf)

  (bind ?ColumnHeadCommNameList (create$ commTextField13 commTextField8 commTextField7 commTextField6 commTextField5 commTextField12 

                                           commTextField11 commTextField10 commTextField4 commTextField3 commTextField2))



  (bind ?VarHeadCommNameList (create$ commTextField16 commTextField15 commTextField9 commTextField14))



  (bind ?RadioButtonCommNameList (create$ commRadioButton15 commRadioButton12 commRadioButton13 commRadioButton14 commRadioButton1 

                                            commRadioButton8 commRadioButton6 commRadioButton7 commRadioButton3 commRadioButton9 commRadioButton5))



  (bind ?interface-elements (fact-slot-value ?problem interface-elements))

 ;; (printout t "interface-elements = " ?interface-elements crlf)

  (bind ?answer-columns (create$ ))

 ;;; (printout t "answer-rows = " ?answer-rows  crlf)

 ;; (bind  ?answer-row1 (nth$ 1 ?answer-rows))  

 ;; (printout t "answer-row 1 = " ?answer-row1  crlf)



   (bind ?Row-index 1)

   (bind ?answer-rows (create$ ))

   (while (<= ?Row-index ?RowSize)

     (bind ?AnswerRowName (sym-cat "answer-row" ?Row-index))

     (bind ?AnswerRowDescriptionName (str-cat "ANSWER-ROW" ?Row-index))

  ;;; (printout t "?AnswerRowName = " ?AnswerRowName " ?AnswerRowDescriptionName = " ?AnswerRowDescriptionName crlf)

     (bind ?answer-row  (assert(MAIN::answer-row (name ?AnswerRowName)

                               (cells  (GenVariableValueRow ?VarNum ?Row-index))

                               (description ?AnswerRowDescriptionName)

     )))

   (bind ?answer-rows (create$ ?answer-rows ?answer-row))

   (bind ?Row-index (+ ?Row-index 1))

   )

   (modify ?answer-table  (answer-rows  ?answer-rows))



 ;; (printout t "answer-rows contains = " (length$ ?answer-rows)  crlf)











   (bind ?variable-columns (create$ ))

   (bind ?variable-index 1)

   (while (<= ?variable-index ?VarNum)







      (bind ?VarHeadDescription (sym-cat "VARIABLE" ?variable-index "-HEADER"))

      (bind ?VariableHeader (assert (MAIN::textField  (name (nth$ (+ ?variable-index (- 4 ?VarNum)) ?VarHeadCommNameList))

                                                      (value nil)                                          

                                                      (description ?VarHeadDescription) ))) 



 ;; (printout t crlf "?VariableHeader = " ?VariableHeader crlf)



  ;;;   (bind ?interface-elements (create$ ?interface-elements ?VariableHeader))  ;;; looks like it doesn't needed, so close it out



      (bind ?VarColumnName        (sym-cat "variable" ?variable-index "-column"))

      (bind ?VarColumnDescription (str-cat "VARIABLE" ?variable-index "-COLUMN"))



      (bind ?variable-column (assert(MAIN::variable-column (name ?VarColumnName)  

                                                          (cells (create$ ?VariableHeader))

                                                          (description ?VarColumnDescription))))



 ;; (printout t crlf "?variable-column>" ?variable-index " = " ?variable-column crlf)





      (bind ?LogicVarColumnName        (sym-cat "logic-variable" ?variable-index "-column"))

      (bind ?LogicVarColumnDescription (str-cat "LOGIC-VARIABLE" ?variable-index "-COLUMN"))

      (bind ?logic-variable1-column (assert(MAIN::logic-variable-column (name ?LogicVarColumnName) 

                                                                        (cells (create$ ?VariableHeader ?variable-column))

                                                                        (description ?LogicVarColumnDescription))))



  ;; (printout t crlf "?logic-variable1-column>" ?variable-index " = " ?logic-variable1-column crlf)



      (bind ?Row-index 1)

      (while (<= ?Row-index ?RowSize)

       (bind  ?answer-row (nth$ ?Row-index ?answer-rows))

 ;; (printout t "answer-row= " ?answer-row  crlf)

       (bind  ?CEs (create$   )) 



       (bind ?row-cell (fact-id (call (nth$ ?variable-index (fact-slot-value  ?answer-row cells)) getFactId)))

 ;; (printout t "?row-cell = " ?row-cell  crlf)

 ;; (printout t "init variable cell list = " (fact-slot-value ?variable-column cells) crlf)

  ;; (printout t "variable header= " ?VHAED crlf)

       (bind  ?CEs (create$  (fact-slot-value ?variable-column cells) ?row-cell)) 

       (modify ?variable-column (cells ?CEs) ) 

       (bind ?Row-index (+ ?Row-index 1))

      )

 ;; (printout t crlf "?variable-column< " ?variable-column crlf)



   (bind ?variable-columns (create$ ?variable-columns ?variable-column))



   (bind ?variable-index (+ ?variable-index 1))

   )



   (modify ?answer-table  (variable-columns  ?variable-columns))





  

  

  (while (<= ?Col-index ?ColSize)



  

  (bind ?Alt1LogicName (sym-cat "alt1-logic" ?Col-index))

  (bind ?n (nth$ ?Col-index ?nodes))

     (bind ?le (call ?n getLogicEvaluation))

     (bind ?lev (create$))

     (foreach ?e ?le

         (bind ?lev (create$ ?lev (sym-cat ?e)))

         )

  ;;  (printout t crlf "node[" ?Col-index  ", lev " ?lev crlf)  

  

 (if (< ?Col-index ?ColSize) then

            (bind ?Alt1Logic (assert(MAIN::logic-evaluation (name ?Alt1LogicName) (answer-p nil) (expression  ?lev) (description nil))))

         else

            (bind ?Alt1Logic (assert(MAIN::logic-evaluation (name ?Alt1LogicName) (answer-p T  ) (expression  ?lev) (description nil))))

  )

   (bind ?Alt1LogicList (create$ ?Alt1LogicList ?Alt1Logic))



  ;; (printout t "Alt1LogicList = " ?Alt1LogicList crlf)

  

  (bind ?Alt1CName (sym-cat "alt1-c" ?Col-index))

  (bind ?Alt1C (assert(MAIN::logic-header-alternative (name ?Alt1CName)    

                                                      (value (sym-cat (nth$ ?Col-index ?SubTreeList)))

                                                      (already-used-p nil)

                                                      (logic-evaluation ?Alt1Logic)

                                                      (description nil))))

   (bind ?Alt1CList (create$ ?Alt1CList ?Alt1C))



  ;; (printout t "Alt1CList = " ?Alt1CList crlf)

   (bind ?Row-index 1)

     (bind ?TableColumnName (sym-cat "Table1_Column" ?Col-index))

     (bind ?ColumnCellList (create$ ))

     (while (<= ?Row-index ?RowSize)

       (bind ?CellName (sym-cat "commTable1_C" ?Col-index "R" ?Row-index))

       (bind ?CELL (assert(MAIN::cell (name ?CellName) (value nil) (row-number ?Row-index) (col-number ?Col-index))))



   ;;    (printout t ?CellName " " ?CELL crlf)

       (bind ?ColumnCellList (create$ ?ColumnCellList ?CELL))





  (bind  ?answer-row (nth$ ?Row-index ?answer-rows))

 ;; (printout t "answer-row = " ?answer-row crlf)

   (bind  ?Cells (create$  (fact-slot-value ?answer-row cells) ?CELL))

 ;;  (printout t "answer-row = " ?answer-row "Cells = "  ?Cells crlf)



   (modify ?answer-row (cells ?Cells) ) 

 ;;  (printout t "After added = " ?Cells crlf)





       (bind ?Row-index (+ ?Row-index 1))

     )



   (bind ?TableColumn (assert(MAIN::column (name ?TableColumnName) (cells ?ColumnCellList) (position ?Col-index) (description nil) )))

 ;;  (printout t "Column" ?Col-index crlf)



   (bind ?ColumnList (create$ ?ColumnList ?TableColumn))

   

  ;; (printout t "?answer-column" (nth$ ?Col-index ?answer-columns) "=" ?TableColumn crlf)



  (bind ?ColumnHeadDescription (str-cat "COLUMN" ?Col-index "HEADER"))

  (bind ?columnheader (assert(MAIN::textField (name (nth$ ?Col-index ?ColumnHeadCommNameList)) 

                                               (value nil) 

                                               (description ?ColumnHeadDescription))))

   

   (bind ?interface-elements (create$ ?interface-elements ?columnheader))  ;;; looks like it doesn't work, but it is OK. Why?

  ;; (printout t "interface-elements = " ?interface-elements crlf)

   

   

  (bind ?radiobutton (assert(MAIN::radioButton (name (nth$ ?Col-index ?RadioButtonCommNameList)))) (value nil))

   

  (bind ?AnswerColumnName (sym-cat "answer-column" ?Col-index))

  (bind ?answer-column (assert(MAIN::answer-column (name ?AnswerColumnName)    

                                                    (col-number ?Col-index)

                                                    (selected-alternative nil)

                                                    (column ?TableColumn)

                                                    (column-header ?columnheader)

                                                    (radio-button ?radiobutton)

                                                    (alternate-col-logic ?Alt1Logic)  

                                                    (alternate-col-values ?Alt1C)    

                                                    (description nil)

  )))

   

  (bind ?answer-columns (create$ ?answer-columns ?answer-column))

   

   (bind ?Col-index (+ ?Col-index 1))

   

   

  )

  (bind ?alternative1 (assert(MAIN::decomposition-alternative (name alternative1)

                             (logic-evaluations ?Alt1LogicList)

                             (header-values ?Alt1CList)

                             (description "ALTERNATIVE1")

  )))

  ;;  (printout t "ColumnList" ?ColumnList crlf)

   (modify ?table    (columns  ?ColumnList))

   (modify ?answer-table    (answer-columns  ?answer-columns))

   (modify ?problem  (interface-elements ?interface-elements))

   (modify ?problem  (decomposition-alternatives ?alternative1))

   

  

)

;; Tell the (require ...) calls in wmeTypes.clp that this feature is present  
(provide logic-rule-functions)   

