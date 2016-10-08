;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;																			    ;;
;;  This file contains Jess / SimStudent production rules to tutor effectively  ;;
;;  (MetaCognitive Model) when teaching SimStudent.							    ;;
;;																				;;	
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; First define templates for the model classes so we can use them in our behavior
;; rules. This doesn't create any model objects --it just tells Jess to examine the
;; classes and set up templates using their properties.

(import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory)
(import edu.cmu.pact.miss.SimSt)

;; Define the templates for the working memory in the MAIN module of Jess
;; Working Memory is composed of Invisible WME + Interface WME
;; Invisible WME (shadow fact) follows the student actions and non-dormin widget components
(deftemplate MAIN::ModelTraceWorkingMemory (declare (from-class ModelTraceWorkingMemory)))

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

;; Now define the behavior rules themselves. Each rule matches a set
;; of conditions and then predicts the student SAI, the hint message
;; and updates the working memory if the student SAI matches the rule
;; SAI.

;;(watch all)

;;
;; If [APlusLaunched == "true"]
;; Then
;; [Press Quiz Button]
(defrule simst-take-quiz
	"If APlusLaunched then take the quiz"
	(declare (salience 100)) ;; salience determines the priority of the rules : higher the salience, higher the priority
	?wm <- (ModelTraceWorkingMemory (APlusLaunched "true"))
	=>
	(predicted-sai quiz ButtonPressed -1) ;; predicts-the-ruleSAI
	;;(update-wm-if-ruleSAI-equals-studentSAI ?wm "(quizTaken true)" "(APlusLaunched false)") ;; updates the working memory when the rule's predicted values matches the student's values
	(modify ?wm (quizTaken "true") (APlusLaunched "false"))
	(construct-tutor-hint-message
		"[ Before you start teaching its a good strategy to quiz SimStName. ]"
		"[ This would give you an idea of what SimStName already knows. ]"
		"[ Click the Quiz SimStName button. ]"))


;;
;; If [quizTaken == "true"] And [quizOutcome == "pass"]
;; Then
;; [Press Quiz Button]
(defrule simst-quiz-pass-take-quiz
	"If student took the quiz and passed it then take the quiz again"
	(declare (salience 150))
	?wm <- (ModelTraceWorkingMemory (quizTaken "true") (quizOutcome "pass"))
	=>
	(predicted-sai quiz ButtonPressed -1)
	;;(update-wm-if-ruleSAI-equals-studentSAI ?wm "(quizTaken true)")
	(modify ?wm (quizTaken "true"))
	(construct-tutor-hint-message
		"[  Why don't you quiz SimStName again so that SimStName works on next section of the quiz. ]" ))


;;
;; If [quizTaken == "true"] And [quizOutcome == "fail"]
;; Then
;; [Press Quiz Button]
(defrule BUG-simst-quiz-fail-take-quiz
	"If student took the quiz and failed it then take the quiz again"
	(declare (salience 50))
	?wm <- (ModelTraceWorkingMemory (quizTaken "true") (quizOutcome "fail"))
	=>
	(predicted-sai quiz ButtonPressed -1))


;;
;; If [quizTaken == "true"] And [quizOutcome == "fail"]
;; Then
;; [Enter failed quiz problem type]
;;(defrule simst-quiz-fail-tutor-quiz-problem-lhs
;;	(declare (salience 150))
;;	?wm <- (ModelTraceWorkingMemory (quizTaken "true") (quizOutcome "fail") (quizProblemsFailedList ?suggestedProblems) 
;;		(studentEnteredProblem ?enteredProblem&:(eq ?enteredProblem nil)))
;;	=>
;;	(bind ?enteredProblem (get-a-problem ?suggestedProblems))
;;	(predicted-sai dorminTable1_C1R2 UpdateTable ?enteredProblem problem-list-matcher ?suggestedProblems)
;;	(update-wm-if-ruleSAI-equals-studentSAI ?wm "(studentEnteredProblem ?enteredProblem)")
;;	(construct-tutor-hint-message
;;		"[ ]"))

;;
;; If [APlusLaunched]
;; Then
;; [Switch to SimSt Tab]
;;(defrule simst-switch-tab
;;	(declare (salience 100)) ;; salience determines the priority of the rules : higher the salience, higher the priority
;;	?var <- (ModelTraceWorkingMemory (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "APlusLaunched"))
;;	=>
;;	(predict-tutor-action SimStName TabClicked -1)
;;	(update-mt-working-memory ?var SimStName)
;;	(construct-tutor-hint-message
;;		"[ You should read through the Unit Overview. ]"
;;		"[ Then switch to the SimStName tab and give SimStName the quiz ]"))



;;
;; If [SimSt Tab]
;; Then
;; [Quiz]
;;(defrule simst-take-quiz
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) (get-member SimSt SimStName)))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(update-mt-working-memory ?var quiz)
;;	(construct-tutor-hint-message
;;		"[ Before you start teaching its a good strategy to quiz SimStName. ]"
;;		"[ This would give you an idea of what SimStName already knows. ]"
;;		"[ Click the Quiz SimStName button. ]"))



;;
;; If [Quiz] And not[Quiz Section Passed]
;; Then
;; enter {failed problem on Quiz}
;;(defrule simst-quiz-fail-tutor-quiz-problem
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory {quizOutcome == "fail"} (modelTracedEvents ?eventsList) (currentProblem ?problemEntered)(quizProblemsFailed ?problemList))
;;	(test (eq (get-first-event ?var) "quiz"))
;;	=>
;;	(bind ?problem (check-problem-started ?problemEntered ?problemList))
;;	(bind ?firstProblem (get-first-quiz-failed-problem ?problemList)) 
;;	;;(predict-tutor-action ?firstProblem StartProblem -1)
;;	(update-mt-working-memory ?var ?problem quizProblem) 	
;;	(construct-tutor-hint-message 
;;		"[ SimStName got some problems wrong on the quiz. ]"
;;		"[ Its a good strategy to teach those problems. This would help SimStName correct the mistakes. ]"
;;		"[ You should give " ?firstProblem " . ]"))



;;
;; If [Quiz] And not[Quiz Section Passed]
;; Then
;; enter {second failed problem on Quiz}
;;(defrule simst-quiz-fail-second
;;	(declare (salience 75))
;;	?var <- (ModelTraceWorkingMemory {quizOutcome == "fail"} (modelTracedEvents ?eventsList) (quizProblemsFailed ?problemList))
;;	(test (eq (get-first-event ?var) "quiz"))
;;	=>
;;	(bind ?secondProblem (get-second-quiz-failed-problem ?problemList))
;;	(predict-tutor-action StartStateElements StartProblem ?secondProblem)
;;	(update-mt-working-memory ?var ?secondProblem))



;;
;; If [Quiz] And [Quiz Section Passed]
;; Then
;; [Quiz]
;;(defrule simst-quiz-pass
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory {quizOutcome == "pass"} (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quiz"))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(update-mt-working-memory ?var quiz)
;;	(construct-tutor-hint-message
;;		"[  Why don't you quiz SimStName again so that SimStName works on next section of the quiz. ]" ))



;;
;; (BUG) If [Quiz] And not[Quiz Section Passed]
;; Then
;; [Quiz]
;;(defrule BUG-simst-quiz-fail
;;	(declare (salience 50))
;;	?var <- (ModelTraceWorkingMemory {quizOutcome == "fail"} (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quiz"))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1))



;;
;; If [Failed Quiz Problem Solved]
;; Then
;; enter {failed quiz problem type}
;;(defrule simst-quiz-problem-solved-enter-problem-type
;;	(declare (salience 150))
;;	?var <- (ModelTraceWorkingMemory (quizProblemsFailed ?problemList) (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quizProblem"))
;;	=>
;;	(bind ?failedQuizProblem (get-second-event ?var))
;;	(bind ?failedProblemType (get-abstracted-problem ?failedQuizProblem))
;;	(predict-tutor-action ?failedProblemType StartProblem -1)
;;	(update-mt-working-memory ?var ?failedProblemType quizProblemType)
;;	(construct-tutor-hint-message
;;		"[	Why don't you teach a similar problem like" ?failedQuizProblem " to see if SimStName can solve it. ]" ))



;;
;; If [Failed Quiz Problem Solved]
;; Then
;; [Review Unit Overview]
;;(defrule simst-quiz-problem-solved-review-unit-overview
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (quizProblemsFailed ?problemList) (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quizProblem"))
;;	=>
;;	(predict-tutor-action UnitOverview TabClicked -1)
;;	(update-mt-working-memory ?var UnitOverview))



;;
;; If [Unit Overview]
;; Then
;; enter {failed quiz problem type}
;;(defrule unit-overview-do-failed-quiz-problem-type
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (quizProblemsFailed ?problemList) (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "UnitOverview"))
;;	=>
;;	)



;;
;; If [Failed Quiz Problem Solved]
;; Then
;; [Quiz]
;;(defrule simst-quiz-problem-solved-take-quiz
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (quizProblemsFailed ?problemList) (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quizProblem"))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(update-mt-working-memory ?var quiz))



;;
;; If [Failed Quiz Problem Solved] And [Quiz Items Left]
;; Then
;; enter {failed quiz problem}
;;(defrule simst-quiz-problem-solved-tutor-failed-problem
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (quizProblemsFailed ?problemList) (modelTracedEvents ?eventsList) (currentProblem ?problemEntered)
;;		(failedQuizProblemsSolved ?solvedList))
;;	(test (eq (get-first-event ?var) "quizProblem"))
;;	(test (quiz-items-left ?problemList ?solvedList))
;;	=>
;;	(bind ?problem (check-problem-started ?problemEntered ?problemList))
;;	(update-mt-working-memory ?var ?problem quizProblem))



;;
;; If [Failed Quiz Problem Solved] And [Quiz Items Left]
;; Then
;; enter {first failed quiz problem}
;;(defrule simst-quiz-problem-solved-tutor-first-failed-problem
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory {problemSolved == "true"} (quizProblemsFailed ?problemList) (failedQuizProblemsSolved ?solvedList)
;;		(completedProblem ?compProblem))
;;	(test (solved-problem-type ?problemList ?compProblem))
;;	(test (quiz-items-left ?problemList ?solvedList))
;;	=>
;;	(bind ?firstProblem (get-first-quiz-failed-problem ?problemList)) 
;;	(predict-tutor-action StartStateElements StartProblem ?firstProblem))



;;
;; If [Failed Quiz Problem Solved] And not[Quiz Items Left]
;; Then
;; [Quiz]
;;(defrule simst-quiz-problem-solved-no-quiz-items-left-take-quiz
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (quizProblemsFailed ?problemList) (modelTracedEvents ?eventsList) (failedQuizProblemsSolved ?solvedList))
;;	(test (eq (get-first-event ?var) "quizProblem"))
;;	(not (test (quiz-items-left ?problemList ?solvedList)))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(update-mt-working-memory ?var quiz))



;;
;; If [Failed Quiz Problem Type Solved] And not[Help given]
;; Then
;; [Quiz]
;;(defrule simst-failed-quiz-problem-type-solved-nohelp-do-quiz
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory {hintRequestOnQuizProblemType == "false"} (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quizProblemType"))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(update-mt-working-memory ?var quiz))



;;
;; If [Failed Quiz Problem Type Solved] And not[Help given] And [Quiz Items Left]
;; Then
;; enter {failed quiz problem}
;;(defrule simst-failed-quiz-problem-type-solved-nohelp-quizitemsleft-do-failed-quiz-problem
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory {hintRequestOnQuizProblemType == "false"} (quizProblemsFailed ?problemList) (failedQuizProblemsSolved ?solvedList) 
;;		(currentProblem ?problemEntered) (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quizProblemType"))
;;	(test (quiz-items-left ?problemList ?solvedList))
;;	=>
;;	(bind ?problem (check-problem-started ?problemEntered ?problemList))
;;	(update-mt-working-memory ?var ?problem quizProblem))



;;
;; If [Failed Quiz Problem Type Solved] And not[Help given] And not[Quiz Items Left]
;; Then
;; [Quiz]
;;(defrule simst-failed-quiz-problem-type-solved-nohelp-noquizitemsleft-do-quiz
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory {hintRequestOnQuizProblemType == "false"} (quizProblemsFailed ?problemList) (failedQuizProblemsSolved ?solvedList) 
;;		(modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "quizProblemType"))
;;	(not (test (quiz-items-left ?problemList ?solvedList)))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(update-mt-working-memory ?var quiz))



;;
;; If [Failed Quiz Problem Solved] And [Unit Overview Reviewed]
;; Then
;; enter {failed quiz problem type}
;;(defrule unit-overview-reviewed-tutor-failed-prob-type
;;	(declare (salience 200))
;;	(ModelTraceWorkingMemory {problemSolved == "true"} {unitOverviewTab == "TabClicked"} (completedProblem ?compProblem)
;;		(quizProblemsFailed ?problemList))
;;	(test (solved-problem-type ?problemList ?compProblem))
;;	=>
;;	(bind ?failedProblemType (get-abstracted-problem ?compProblem))
;;	(predict-tutor-action StartStateElements StartProblem ?failedProblemType)
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(construct-tutor-hint-message
;;		"[	]" ))



;;
;; If [Failed Quiz Problem Type Solved] And not[Help given for the solved problem]
;; Then
;; [Quiz]
;;(defrule failed-quiz-problem-solved-without-help
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {problemSolved == "true"} {hintRequestOnProblem == "false"} (completedProblem ?compProblem)
;;		(quizProblemsFailed ?problemList))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1))



;;
;; If [Problem Solved] And not[Help given for the previous problem]
;; Then
;; [Quiz]
;;(defrule problem-solved-without-help
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {problemSolved == "true"} {hintRequestOnProblem == "false"})
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(bind ?ssName (get-member SimSt SimStName))	
;;	(construct-tutor-hint-message
;;		"[ " ?ssName " did not get stuck. ]"
;;		"[ You should give a quiz now to see how" ?ssName " does on the quiz.]"
;;		"[ Click on Quiz " ?ssName " button. ]"))


	
;;
;; If [Tutor Next Problem] And [Quiz] is not before [Tutor Next Problem]
;; Then
;; [Review Unit Overview] 
;;(defrule review-unit-overview
;;	(declare (salience 75))
;;	?var <- (ModelTraceWorkingMemory {tutorNextProblem == "ButtonPressed"})
;;	(not (test (quiz-before-tutor-next-problem ?var)))
;;	=>
;;	(predict-tutor-action UnitOverview TabClicked -1)
;;	(construct-tutor-hint-message
;;		"[ You should look at the Unit Overview for problems. ]"
;;		"[ You should pick problems similar to the ones Stacy got wrong on the quiz.]"
;;		"[ Select the Unit Overview Tab above. ]"))
	
	

;;
;; If [Unit Overview Reviewed]
;; Then
;; {select first problem in the Unit Overview in sync with the [Quiz Level]}
;;(defrule tutor-problem
;;	(declare (salience 85))
;;	(ModelTraceWorkingMemory {unitOverviewTab == "TabClicked"})
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(construct-tutor-hint-message
;;		"[ You should give the following problem to" ?ssName ". ]" ))



;; USE-PROBLEM-BANK
;;
;; If a student should enter a new problem and did not just complete a quiz
;; Then
;; (Student tutor) should use the problem bank.
;;(defrule use-problem-bank
;;	"If a student should enter a new problem and did not just complete a quiz then use the problem bank"
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {enterProblem == "true"} {lastAction != "Quiz"} {lastAction != "ProblemBank"} )
;;	=>
;;	(predict-tutor-action ProblemBank TabClicked -1)
;;	(construct-tutor-hint-message
;;		"[	You should look at the problem bank. ]"))



;; TUTOR-PROBLEM
;;
;; If problem bank was just reviewed
;; Then
;; give a problem that has never been solved.
;;(defrule tutor-problem
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {enterProblem == "true"} {lastAction == "ProblemBank"} (problemEntered ?problemEntered) (suggestedProblem ?suggestedProblem))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(check-problem-started ?problemEntered ?suggestedProblem)
;;	(construct-tutor-hint-message
;;		"[ You should give the following problem" ?suggestedProblem " to" ?ssName ". ]"))



;; BUG-PROBLEM-SOLVED-ON-QUIZ
;;
;; If a student should enter a new problem
;; Then
;; (Student tutor) provides a exact problem which has been solved correctly on the quiz.
;;(defrule BUG-problem-solved-on-quiz
;;	"If a student should enter a new problem then provide an exact problem which has been solved correctly on the quiz"
;;	(declare (salience 50))
;;	(ModelTraceWorkingMemory {enterProblem == "true"} (problemEntered ?problemEntered) (quizProblemsPassed ?problemList))
;;	=>
;;	(check-problem-started ?problemEntered ?problemList)
;;	(construct-tutor-hint-message
;;		"[ ]"))



;; SIMST-QUIZ-SOLVED-CORRECTLY
;;
;; If SimStudent solves all the quiz items correctly
;; Then
;; (Student tutor) should quiz SimStudent again
;;(defrule simst-quiz-solved-correctly
;;	"If SimStudent solves all the quiz items correctly then quiz SimStudent again"
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {quizOutcome == "pass"})
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(construct-tutor-hint-message
;;		"[  Why don't you quiz" ?ssName " again so that" ?ssName " works on next section of the quiz. ]" ))
	


;; SIMST-QUIZ-FAIL
;;
;; If SimStudent fails on a quiz
;; Then
;; (Student tutor) should give type of problem which SimStudent got wrong on the quiz.
;;(defrule simst-quiz-fail
;;	"If SimStudent fails on a quiz, then tutor SimStudent type of problem which SimStudent got wrong on quiz"
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {quizOutcome == "fail"} (problemEntered ?problemEntered) (quizProblemsFailed ?problemList) (suggestedProblems ?suggestedList))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(check-problem-started ?problemEntered ?problemList)
;;	(construct-tutor-hint-message 
;;		"[ " ?ssName " got some problems wrong on the quiz. ]"
;;		"[ Its a good strategy to teach those problems. This would help" ?ssName " correct the mistakes. ]"
;;		"[ You should give " ?suggestedList " . ]"))

	

;; BUG-SIMST-QUIZ-FAIL
;;
;; If SimStudent fails on a quiz
;; Then
;; (Student tutor) quizzes SimStudent again
;;(defrule BUG-simst-quiz-fail
;;	(declare (salience 50))
;;	(ModelTraceWorkingMemory {quizOutcome == "fail"})
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(construct-tutor-hint-message 
;;		"[  ]" ))



;; SIMST-LAUNCH-QUIZ
;;
;; IF SimStudent is launched and Student tutor has not yet started teaching SimStudent
;; Then
;; (Student tutor) should quiz SimStudent
;;(defrule simst-launch-quiz
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {tutoringActivityStarted == "false"})
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(construct-tutor-hint-message 
;;		"[ Before you start teaching its a good strategy to quiz" ?ssName " . ]" 
;;		"[ This would give you an idea of what" ?ssName " already knows. ]"
;;		"[ Click the Quiz " ?ssName " button. ]"))



;; QUIZ-SIMSTUDENT
;;
;; If SimStudent solves a problem without any hints from the Student tutor
;; Then
;; (Student tutor) should quiz SimStudent
;;(defrule quiz-simst
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {problemSolved == "true"} {hintRequestOnProblem == "false"})
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))	
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(construct-tutor-hint-message
;;		"[ " ?ssName " did not get stuck. ]"
;;		"[ You should give a quiz now to see how" ?ssName " does on the quiz.]"
;;		"[ Click on Quiz " ?ssName " button. ]"))



;; SIMST-CORRECT-STEP-PERFORMED
;;
;; If SimStudent does a step correctly
;; Then
;; (Student tutor) should give a positive feedback.
;;
;;(defrule simst-correct-step
;;	"If SimStudent does a step correctly then give a positive feedback"
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {stepCorrectness == "correct"} {actor ==  "simstudent"} (step ?step))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action yes ButtonPressed -1)
;;	(construct-tutor-hint-message 
;;		"[ Do you know what to do when" ?ssName "does a step and asks for confirmation ? ]"
;;		"[ " ?ssName "performed the step. You should provide a feedback. ]"
;;		"[ You should click on Yes/No button. ]"))
;;	(construct-cl-hint-message
;;		"[ " ?ssName "has performed the step" ?step "correctly. ]" ))
	

;; SIMST-INCORRECT-STEP-PERFORMED
;;
;; If SimStudent does a step incorrectly
;; Then
;; (Student tutor) should give a negative feedback.
;;
;;(defrule simst-incorrect-step
;;	"If SimStudent does a step incorrectly then give a negative feedback"
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {stepCorrectness == "incorrect"} {actor == "simstudent"} (step ?step))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action no ButtonPressed -1)
;;	(construct-tutor-hint-message 
;;		"[ Do you know what to do when" ?ssName "does a step and asks for confirmation ? ]"
;;		"[ " ?ssName "performed the step. You should provide a feedback. ]"
;;		"[ You should click on Yes/No button. ]"))
;;	(construct-cl-hint-message
;;		"[ " ?ssName "has performed the step" ?step "incorrectly. ]" ))
	
	
;; SIMST-HELP-REQUEST
;;
;; If SimStudent makes a help request
;; Then
;; (Student tutor) should give a correct help.
;;(defrule simst-help-request
;;	"If SimStudent makes a help request then give a correct help"
;;	(declare (salience 100)) ;; Should be preferred over BUG-rule
;;	(ModelTraceWorkingMemory {helpRequested == "true"} {examplesTabClicked == "true"} {actor == "simstudent"} (problemNodeName ?name))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))	
;;	(bind ?ruleSAI (get-correct-sai ?name))
;;	(predict-tutor-action ?ruleSAI  UpdateTable  ?ruleSAI)
;;	(construct-tutor-hint-message 
;;		"[ " ?ssName " is requesting for help. Demonstrate the next step correctly so " ?ssName "can learn. ]" ))
;;	(construct-cl-hint-message 
;;		"[ " ?ruleSAI " ]" ))
	

;; BUG-SIMST-HELP-REQUEST
;;
;; If SimStudent makes a help request
;; Then
;; (Student tutor) gives an incorrect help.
;;(defrule BUG-simst-help-request
;;	"If SimStudent makes a help request then give an incorrect help"
;;	(declare (salience 50))
;;	(ModelTraceWorkingMemory {helpRequested == "true"} {actor == "simstudent"} (problemNodeName ?name))
;;	=>
;;	(bind ?ruleSAI (get-correct-sai ?name))
;;	(predict-tutor-action ?ruleSAI  UpdateTable  DONT-CARE))
	
	
;; BUG-SIMST-QUIZ-SOLVED-CORRECTLY
;;
;; If SimStudent solves all the quiz items correctly
;; Then
;; (Student tutor) tutors SimStudent type of problems gotten correct on the quiz
;;(defrule BUG-simst-quiz-solved-correctly
;;	"If SimStudent solves all the quiz items correctly then tutor SimStudent type of problems which it got correct on the quiz"
;;	(declare (salience 50))
;;	(ModelTraceWorkingMemory {quizOutcome == "pass"} (ssName ?ssName) (problemEntered ?problemEntered) (quizProblemsPassed ?problemList))
;;	=>
;;	(check-problem-started ?problemEntered ?problemList)
;;	(construct-tutor-hint-message
;;		"[	]" ))

	

;; STUDENT-VIEW-EXAMPLES
;; 
;; If SimStudent makes a help request and Student tutor has not seen any examples yet
;; Then
;; (Student tutor) should look at the examples
;;(defrule student-view-examples
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {helpRequested == "true"} {examplesTabClicked == "false"} (ssName ?ssName))
;;	=>
;;	(predict-tutor-action example TabClicked -1)
;;	(construct-tutor-hint-message
;;		"[ You should go through all the examples using the tab above.  Make sure you understand all the examples.]"))



;; TUTOR-SIMSTUDENT-PROBLEM
;; 
;; If SimStudent solves a problem using hints from the Student tutor
;; Then
;; (Student tutor) should give a similar type of problem
;;(defrule tutor-simst-problem
;;	(declare (salience 100))
;;	(ModelTraceWorkingMemory {problemSolved == "true"} {hintRequestOnProblem == "true"} (suggestedProblem ?problem))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action yes ButtonPressed -1)
;;	(construct-tutor-hint-message
;;		"[ Why don't you give" ?ssName " the problem" ?problem " to see if" ?ssName " can solve it.]"))



;; HELP-REQUEST-AFTER-UNDO-STEP
;;
;; If Student tutor asks for next step to help SimStudent after undoing a step
;; Then
;; (Student tutor) should be explained that SimStudent still has the previous knowledge
;; and (Student tutor) should demonstrate to SimStudent for it to replace the previous knowledge
;;(defrule help-request-after-undo-step
;;	(declare (salience 200))
;;	(ModelTraceWorkingMemory {stepUndone == "true"})
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))	
;;	(predict-tutor-action Selection UpdateTable Input)
;;	(construct-tutor-hint-message
;;		"[ After you undo a step" ?ssName " still remembers what you taught it previously. ]"
;;		"[ You should demonstrate to" ?ssName " so she can learn and forget what you taught it previously.]"))



;;
;; If [APlusLaunched] And not[SimSt Tab]
;; Then
;; [Switch To SimSt Tab]
;;(defrule simst-launch-switch-tab
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) "UnitOverview"))
;;	=>
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(predict-tutor-action ?ssName TabClicked -1)
;;	(construct-tutor-hint-message
;;		"[ You should read through the Unit Overview. ]"
;;		"[ Then switch to the" ?ssName " tab and give" ?ssName " the quiz ]"))



;;
;; If [APlusLaunched] And [SimSt Tab]
;; Then
;; [Quiz]
;;(defrule simst-launch-quiz
;;	(declare (salience 100))
;;	?var <- (ModelTraceWorkingMemory (modelTracedEvents ?eventsList))
;;	(test (eq (get-first-event ?var) (get-member SimSt SimStName)))
;;	=>
;;	(predict-tutor-action quiz ButtonPressed -1)
;;	(bind ?ssName (get-member SimSt SimStName))
;;	(construct-tutor-hint-message
;;		"[ Before you start teaching its a good strategy to quiz" ?ssName " . ]"
;;		"[ This would give you an idea of what" ?ssName " already knows. ]"
;;		"[ Click the Quiz" ?ssName "button. ]"))