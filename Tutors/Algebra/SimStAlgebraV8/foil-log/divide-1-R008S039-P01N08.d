V1: x+7, 1+7/x, x+3, x+5, 3v+2, x, 3v, 3, 3/x, 7, 10, 4, 5, -4.
V4: divide x, multiply x.

divide-1(V1, V1) ##
x+7, 3
;
1+7/x, 3/x
x+3, 7
x+5, 10
3v+2, 7
x, 4
x, 5
3v, 5
x, -4
.
*HasVarTerm(V1) #
x+7
1+7/x
;
3
3/x
.
*IsNumeratorOf(V1, V1) ##
3, 3/x
;
x+7, 3
3, x+7
1+7/x, 3/x
3/x, 1+7/x
x+7, 1+7/x
1+7/x, x+7
x+7, 3/x
3, 1+7/x
1+7/x, 3
3/x, 3
3/x, x+7
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
3, 3/x
;
x+7, 3
3, x+7
1+7/x, 3/x
3/x, 1+7/x
x+7, 1+7/x
1+7/x, x+7
x+7, 3/x
3, 1+7/x
1+7/x, 3
3/x, 3
3/x, x+7
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

