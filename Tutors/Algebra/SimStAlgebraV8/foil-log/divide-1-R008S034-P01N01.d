V1: x+7, 1+7/x, 3, 3/x.
V4: divide x, multiply x.

divide-1(V1, V1) ##
x+7, 3
;
1+7/x, 3/x
.
*HasVarTerm(V1) #
x+7
1+7/x
;
3
3/x
.
*IsNumeratorOf(V1, V1) ##
;
x+7, 3
3, x+7
1+7/x, 3/x
3/x, 1+7/x
x+7, 1+7/x
1+7/x, x+7
.
*IsFractionTerm(V1) #
3/x
;
3
x+7
1+7/x
.
*IsAVarTerm(V1) #
3/x
;
3
x+7
1+7/x
.
*IsSkillAdd(V4) #
;
divide x
multiply x
.
*IsLastConstTermNegative(V1) #
;
3
x+7
1+7/x
3/x
.
*HasCoefficient(V1) #
;
3
x+7
1+7/x
3/x
.
*IsConstant(V1) #
3
;
x+7
1+7/x
3/x
.
*IsPolynomial(V1) #
x+7
1+7/x
;
3
3/x
.
*IsDenominatorOf(V1, V1) ##
;
x+7, 3
3, x+7
1+7/x, 3/x
3/x, 1+7/x
x+7, 1+7/x
1+7/x, x+7
.
*HasParentheses(V1) #
;
3
x+7
1+7/x
3/x
.
*IsSkillSubtract(V4) #
;
divide x
multiply x
.
*IsSkillMultiply(V4) #
multiply x
;
divide x
.
*Homogeneous(V1) #
3
;
x+7
1+7/x
3/x
.
*NotNull(V1) #
3
x+7
1+7/x
3/x
.
*Monomial(V1) #
3
3/x
;
x+7
1+7/x
.
*HasConstTerm(V1) #
3
x+7
1+7/x
;
3/x
.
*IsSkillDivide(V4) #
divide x
;
multiply x
.

