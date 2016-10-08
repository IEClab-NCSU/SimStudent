;;;; Fact assertions: slot assignments are below.

(bind ?var1 (assert(MAIN::initial-fact)))
(bind ?var2 (assert(MAIN::selection-action-input)))
(bind ?var3 (assert(MAIN::special-wme)))
(bind ?var4 (assert(MAIN::special-tutor-fact)))
(bind ?var5 (assert(MAIN::special-tutor-fact-correct)))
(bind ?var6 (assert(MAIN::special-tutor-fact-buggy)))
(bind ?var7 (assert(MAIN::cell (name commTable1_C1R1))))
(bind ?var8 (assert(MAIN::cell (name commTable1_C1R2))))
(bind ?var9 (assert(MAIN::cell (name commTable1_C1R3))))
(bind ?var10 (assert(MAIN::cell (name commTable1_C1R4))))
(bind ?var11 (assert(MAIN::cell (name commTable1_C1R5))))
(bind ?var12 (assert(MAIN::cell (name commTable1_C1R6))))
(bind ?var13 (assert(MAIN::column (name commTable1_Column1))))
(bind ?var14 (assert(MAIN::cell (name commTable1_C2R1))))
(bind ?var15 (assert(MAIN::cell (name commTable1_C2R2))))
(bind ?var16 (assert(MAIN::cell (name commTable1_C2R3))))
(bind ?var17 (assert(MAIN::cell (name commTable1_C2R4))))
(bind ?var18 (assert(MAIN::cell (name commTable1_C2R5))))
(bind ?var19 (assert(MAIN::cell (name commTable1_C2R6))))
(bind ?var20 (assert(MAIN::column (name commTable1_Column2))))
(bind ?var21 (assert(MAIN::table (name commTable1))))
(bind ?var22 (assert(MAIN::button (name done))))
(bind ?var23 (assert(MAIN::button (name hint))))
(bind ?var24 (assert(MAIN::problem (name empty))))

;;;; Slot assignments

; MAIN::selection-action-input
(modify ?var2
    (selection NotSpecified)
    (action NotSpecified)
    (input NotSpecified)
)
; MAIN::special-wme
(modify ?var3
    (selection NotSpecified)
    (action NotSpecified)
    (input NotSpecified)
    (hint-message)
    (buggy-message)
)
; MAIN::special-tutor-fact
(modify ?var4
    (selection NotSpecified)
    (action NotSpecified)
    (input NotSpecified)
)
; MAIN::special-tutor-fact-correct
(modify ?var5
    (selection NotSpecified)
    (action NotSpecified)
    (input NotSpecified)
    (hint-message)
)
; MAIN::special-tutor-fact-buggy
(modify ?var6
    (selection NotSpecified)
    (action NotSpecified)
    (input NotSpecified)
    (buggy-message)
)
; MAIN::cell
(modify ?var7
    (name commTable1_C1R1)
    (value nil)
    (description nil)
    (row-number 1)
)
; MAIN::cell
(modify ?var8
    (name commTable1_C1R2)
    (value nil)
    (description nil)
    (row-number 2)
)
; MAIN::cell
(modify ?var9
    (name commTable1_C1R3)
    (value nil)
    (description nil)
    (row-number 3)
)
; MAIN::cell
(modify ?var10
    (name commTable1_C1R4)
    (value nil)
    (description nil)
    (row-number 4)
)
; MAIN::cell
(modify ?var11
    (name commTable1_C1R5)
    (value nil)
    (description nil)
    (row-number 5)
)
; MAIN::cell
(modify ?var12
    (name commTable1_C1R6)
    (value nil)
    (description nil)
    (row-number 6)
)
; MAIN::column
(modify ?var13
    (name commTable1_Column1)
    (cells ?var7 ?var8 ?var9 ?var10 ?var11 ?var12)
    (position 1)
    (description nil)
)
; MAIN::cell
(modify ?var14
    (name commTable1_C2R1)
    (value nil)
    (description nil)
    (row-number 1)
)
; MAIN::cell
(modify ?var15
    (name commTable1_C2R2)
    (value nil)
    (description nil)
    (row-number 2)
)
; MAIN::cell
(modify ?var16
    (name commTable1_C2R3)
    (value nil)
    (description nil)
    (row-number 3)
)
; MAIN::cell
(modify ?var17
    (name commTable1_C2R4)
    (value nil)
    (description nil)
    (row-number 4)
)
; MAIN::cell
(modify ?var18
    (name commTable1_C2R5)
    (value nil)
    (description nil)
    (row-number 5)
)
; MAIN::cell
(modify ?var19
    (name commTable1_C2R6)
    (value nil)
    (description nil)
    (row-number 6)
)
; MAIN::column
(modify ?var20
    (name commTable1_Column2)
    (cells ?var14 ?var15 ?var16 ?var17 ?var18 ?var19)
    (position 2)
    (description nil)
)
; MAIN::table
(modify ?var21
    (name commTable1)
    (columns ?var13 ?var20)
)
; MAIN::button
(modify ?var22
    (name done)
)
; MAIN::button
(modify ?var23
    (name hint)
)
; MAIN::problem
(modify ?var24
    (name empty)
    (interface-elements ?var21 ?var22 ?var23)
    (subgoals)
    (done nil)
    (description nil)
    (turn nil)
)
