V1: 1+7/x, 3/x, x+3, 7, x+5, 10, 3v+2, x+7, 3.
V4: multiply x, subtract 3, subtract 5, subtract 2, subtract 7, divide x.

multiply-typein(V1, V4) ##
1+7/x, multiply x
3/x, multiply x
;
x+3, subtract 3
7, subtract 3
x+5, subtract 5
10, subtract 5
7, subtract 2
3v+2, subtract 2
x+7, subtract 7
3, subtract 7
.
*HasVarTerm(V1) #
1+7/x
x+7
;
3
3/x
.
*IsNumeratorOf(V1, V1) ##
3, 3/x
;
x+7, 1+7/x
x+7, 3
1+7/x, x+7
1+7/x, 3/x
3, x+7
3/x, 1+7/x
x+7, 3/x
3, 1+7/x
1+7/x, 3
3/x, 3
3/x, x+7
.
*IsFractionTerm(V1) #
3/x
;
1+7/x
3
x+7
.
*IsAVarTerm(V1) #
3/x
;
1+7/x
3
x+7
.
*IsSkillAdd(V4) #
;
divide x
multiply x
.
*IsLastConstTermNegative(V1) #
;
1+7/x
3
x+7
3/x
.
*HasCoefficient(V1) #
;
1+7/x
3
x+7
3/x
.
*IsConstant(V1) #
3
;
1+7/x
x+7
3/x
.
*IsPolynomial(V1) #
1+7/x
x+7
;
3
3/x
.
*IsDenominatorOf(V1, V1) ##
3, 3/x
;
x+7, 1+7/x
x+7, 3
1+7/x, x+7
1+7/x, 3/x
3, x+7
3/x, 1+7/x
x+7, 3/x
3, 1+7/x
1+7/x, 3
3/x, 3
3/x, x+7
.
*HasParentheses(V1) #
;
1+7/x
3
x+7
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
1+7/x
x+7
3/x
.
*NotNull(V1) #
1+7/x
3
x+7
3/x
.
*Monomial(V1) #
3
3/x
;
1+7/x
x+7
.
*HasConstTerm(V1) #
1+7/x
3
x+7
;
3/x
.
*IsSkillDivide(V4) #
divide x
;
multiply x
.

