V1: x, x+3, 3x+2, 4, 7, -1, 3x, 12, x/3+1, 7/3.
V4: subtract 3, subtract 2, divide 3.

done(V1, V1) ##
x, 4
;
x+3, 7
3x+2, 7
.
*NotNull(V1) #
-1
3x
12
4
x
x+3
7
x/3+1
7/3
3x+2
.
*IsDenominatorOf(V1, V1) ##
;
4, x
4, 12
-1, x
-1, 4
x, -1
x, 3x
12, 3x
3x, x
3x, 12
4, -1
3x, 4
x, 12
12, x
x, 4
12, 4
4, 3x
x+3, 7
7, x+3
7, x/3+1
7/3, x+3
7/3, 12
7/3, 7
7, 7/3
7, x
7, 12
7, 4
7/3, x/3+1
x, x+3
x+3, x
7, 3x
x+3, 4
4, x+3
4, 7
x, 7
3x+2, x+3
7, 3x+2
3x+2, 7
x+3, 3x+2
3x+2, 3x
3x+2, x
x+3, 3x
4, 3x+2
3x, x+3
3x+2, 4
3x, 3x+2
3x, 7
x, 3x+2
.
*HasConstTerm(V1) #
-1
12
4
x+3
7
x/3+1
7/3
3x+2
;
3x
x
.
*IsSkillSubtract(V4) #
subtract 3
subtract 2
;
divide 3
.
*IsSkillMultiply(V4) #
;
divide 3
subtract 3
subtract 2
.
*IsNumeratorOf(V1, V1) ##
7, 7/3
;
4, x
4, 12
-1, x
-1, 4
x, -1
x, 3x
12, 3x
3x, x
3x, 12
4, -1
3x, 4
x, 12
12, x
x, 4
12, 4
4, 3x
x+3, 7
7, x+3
7, x/3+1
7/3, x+3
7/3, 12
7/3, 7
7, x
7, 12
7, 4
7/3, x/3+1
x, x+3
x+3, x
7, 3x
x+3, 4
4, x+3
4, 7
x, 7
3x+2, x+3
7, 3x+2
3x+2, 7
x+3, 3x+2
3x+2, 3x
3x+2, x
x+3, 3x
4, 3x+2
3x, x+3
3x+2, 4
3x, 3x+2
3x, 7
x, 3x+2
.
*IsFractionTerm(V1) #
7/3
;
-1
3x
12
4
x
x+3
7
x/3+1
3x+2
.
*Homogeneous(V1) #
-1
3x
12
4
x
7
7/3
;
x+3
x/3+1
3x+2
.
*HasVarTerm(V1) #
x+3
x/3+1
3x+2
;
-1
3x
12
4
x
7
7/3
.
*IsSkillDivide(V4) #
divide 3
;
subtract 3
subtract 2
.
*Monomial(V1) #
-1
3x
12
4
x
7
7/3
;
x+3
x/3+1
3x+2
.
*IsSkillAdd(V4) #
;
divide 3
subtract 3
subtract 2
.
*HasCoefficient(V1) #
3x
;
-1
12
4
x
x+3
7
x/3+1
7/3
3x+2
.
*IsAVarTerm(V1) #
3x
x
;
-1
12
4
x+3
7
x/3+1
7/3
3x+2
.
*IsPolynomial(V1) #
x+3
x/3+1
3x+2
;
-1
3x
12
4
x
7
7/3
.
*IsConstant(V1) #
-1
12
4
7
7/3
;
3x
x
x+3
x/3+1
3x+2
.
*IsLastConstTermNegative(V1) #
;
-1
3x
12
4
x
x+3
7
x/3+1
7/3
3x+2
.
*HasParentheses(V1) #
;
-1
3x
12
4
x
x+3
7
x/3+1
7/3
3x+2
.

