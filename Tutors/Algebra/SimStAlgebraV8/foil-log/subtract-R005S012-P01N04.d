V1: x+3, x, 3x, -3x, 7, 4, 12, 6, -2, -1, x/3+1, 7/3.
V4: divide 3, subtract 3, divide -3.

subtract(V1, V1) ##
x+3, 7
;
x, 4
3x, 12
-3x, 6
x, -2
.
*IsAVarTerm(V1) #
3x
x
-3x
;
-1
x+3
12
4
7
x/3+1
7/3
6
-2
.
*Homogeneous(V1) #
-1
3x
12
4
x
7
7/3
-3x
6
-2
;
x+3
x/3+1
.
*IsDenominatorOf(V1, V1) ##
;
4, 12
4, x
x, -1
-1, x
12, 4
4, -1
3x, 4
12, 3x
x+3, 7
x, 4
3x, 12
x, 3x
7, x+3
12, x
3x, x
4, 3x
-1, 4
x, 12
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
12, -3x
-3x, 12
6, 12
-3x, 6
3x, -3x
-3x, 3x
6, 3x
12, 6
3x, 6
6, -3x
-3x, x+3
7, -3x
-3x, 4
-3x, 7
-3x, x/3+1
-3x, 7/3
-3x, x
6, x
-2, x+3
-2, 12
-2, 4
6, x+3
-2, 6
-2, 7
7, -2
6, 4
6, 7
-2, 3x
7, 6
-2, x/3+1
6, x/3+1
-2, -3x
-2, 7/3
-2, x
-3x, -2
6, -2
6, 7/3
4, -2
x, -2
-1, -2
-2, -1
.
*IsPolynomial(V1) #
x+3
x/3+1
;
-1
3x
12
4
x
7
7/3
-3x
6
-2
.
*IsConstant(V1) #
-1
12
4
7
7/3
6
-2
;
x+3
3x
x
x/3+1
-3x
.
*IsSkillAdd(V4) #
;
divide 3
subtract 3
divide -3
.
*IsSkillDivide(V4) #
divide 3
divide -3
;
subtract 3
.
*Monomial(V1) #
-1
3x
12
4
x
7
7/3
-3x
6
-2
;
x+3
x/3+1
.
*IsFractionTerm(V1) #
7/3
;
-1
x+3
3x
12
4
x
7
x/3+1
-3x
6
-2
.
*HasConstTerm(V1) #
-1
x+3
12
4
7
x/3+1
7/3
6
-2
;
3x
x
-3x
.
*HasParentheses(V1) #
;
-1
x+3
3x
12
4
x
7
x/3+1
7/3
-3x
6
-2
.
*HasCoefficient(V1) #
3x
-3x
;
-1
x+3
12
4
x
7
x/3+1
7/3
6
-2
.
*IsLastConstTermNegative(V1) #
;
-1
x+3
3x
12
4
x
7
x/3+1
7/3
-3x
6
-2
.
*HasVarTerm(V1) #
x+3
x/3+1
;
-1
3x
12
4
x
7
7/3
-3x
6
-2
.
*NotNull(V1) #
-1
x+3
3x
12
4
x
7
x/3+1
7/3
-3x
6
-2
.
*IsNumeratorOf(V1, V1) ##
7, 7/3
;
4, 12
4, x
x, -1
-1, x
12, 4
4, -1
3x, 4
12, 3x
x+3, 7
x, 4
3x, 12
x, 3x
7, x+3
12, x
3x, x
4, 3x
-1, 4
x, 12
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
12, -3x
-3x, 12
6, 12
-3x, 6
3x, -3x
-3x, 3x
6, 3x
12, 6
3x, 6
6, -3x
-3x, x+3
7, -3x
-3x, 4
-3x, 7
-3x, x/3+1
-3x, 7/3
-3x, x
6, x
-2, x+3
-2, 12
-2, 4
6, x+3
-2, 6
-2, 7
7, -2
6, 4
6, 7
-2, 3x
7, 6
-2, x/3+1
6, x/3+1
-2, -3x
-2, 7/3
-2, x
-3x, -2
6, -2
6, 7/3
4, -2
x, -2
-1, -2
-2, -1
.
*IsSkillSubtract(V4) #
subtract 3
;
divide 3
divide -3
.
*IsSkillMultiply(V4) #
;
divide 3
subtract 3
divide -3
.

