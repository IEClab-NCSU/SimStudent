; -*- jess -*-

;; Jess standard script library
;; You can add functions to this file and they will be loaded in whenever
;; Jess starts up
;; (C) 1997 E.J.Friedman-Hill and Sandia National Laboratories


(deffunction show-deftemplates ()
  "Print deftemplates using Java reflection"
  (bind ?__e (call (engine) listDeftemplates))
  (while (call ?__e hasNext)    
      (printout t crlf
          (call (new jess.PrettyPrinter (call ?__e next)) toString) crlf)))

(deffunction show-jess-listeners ()
  "Print JessListeners using Java reflection"
  (bind ?__e (call (engine) listJessListeners))
  (while (call ?__e hasNext)    
    (printout t crlf (call (call ?__e next) toString) crlf)))

(deffunction show-deffacts ()
  "Print deffacts using Java reflection"
  (bind ?__e (call (engine) listDeffacts))
  (while (call ?__e hasNext)    
    (printout t crlf (call (new jess.PrettyPrinter (call ?__e next)) toString)
               crlf)))

(deffunction ppdefrule (?__rule)
  "Pretty-print a Jess rule"
  (bind ?__defrule (call (engine) findDefrule ?__rule))
  (if (neq ?__defrule nil) then
    (call (new jess.PrettyPrinter ?__defrule) toString)
    else
    (str-cat "No such rule: " ?__rule)))


(deffunction ppdeffacts (?__facts)
  "Pretty-print a Jess rule"
  (bind ?__deffacts (call (engine) findDeffacts ?__facts))
  (if (neq ?__deffacts nil) then
    (call (new jess.PrettyPrinter ?__deffacts) toString)
    else
    (str-cat "No such deffacts: " ?__facts)))

(deffunction ppdefquery (?__query)
  "Pretty-print a Jess query"
  (ppdefrule ?__query))

(deffunction ppdeffunction (?__function)
  "Pretty-print a Jess function"
  (bind ?__deffunction (call (engine) findUserfunction ?__function))
  (if (neq ?__deffunction nil) then
    (call (new jess.PrettyPrinter ?__deffunction) toString)
    else
    (str-cat "No such deffunction: " ?__function)))

(deffunction ppdefglobal (?__global)
  "Pretty-print a Jess global"
  (bind ?__defglobal (call (engine) findDefglobal ?__global))
  (if (neq ?__defglobal nil) then
    (call (new jess.PrettyPrinter ?__defglobal) toString)
    else
    (str-cat "No such defglobal: " ?__global)))

(deffunction ppdeftemplate (?__template)
  "Pretty-print a Jess template"
  (bind ?__deftemplate (call (engine) findDeftemplate ?__template))
  (if (neq ?__deftemplate nil) then
    (call (new jess.PrettyPrinter ?__deftemplate) toString)
    else
    (str-cat "No such deftemplate: " ?__template)))

(deffunction fact-slot-value (?__fact ?__name)
  "Fetch the value from the named slot of the given fact"
  (return (call ?__fact getSlotValue ?__name)))

(deffunction run-until-halt ()
  "Run until halt is called."
  (call (engine) runUntilHalt))

