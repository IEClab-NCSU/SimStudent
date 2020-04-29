V1: 3x, -3x, x, x+3, 12, 6, 4, 7, -1, x/3+1, 7/3.
V4: divide 3, subtract 3, divide -3.

divide(V1, V1) ##
3x, 12
-3x, 6
;
x, 4
x+3, 7
.
*IsAVarTerm(V1) #
3x
x
-3x
;
12
4
-1
x+3
7
x/3+1
7/3
6
.
*Homogeneous(V1) #
3x
12
x
4
-1
7
7/3
-3x
6
;
x+3
x/3+1
.
*IsDenominatorOf(V1, V1) ##
;
3x, 12
12, 3x
3x, x
x, 3x
4, x
4, 12
3x, 4
x, 12
12, x
x, 4
12, 4
4, 3x
-1, x
-1, 4
x, -1
4, -1
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
.
*IsPolynomial(V1) #
x+3
x/3+1
;
3x
12
x
4
-1
7
7/3
-3x
6
.
*IsConstant(V1) #
12
4
-1
7
7/3
6
;
3x
x
x+3
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
3x
12
x
4
-1
7
7/3
-3x
6
;
x+3
x/3+1
.
*IsFractionTerm(V1) #
7/3
;
3x
12
x
4
-1
x+3
7
x/3+1
-3x
6
.
*HasConstTerm(V1) #
12
4
-1
x+3
7
x/3+1
7/3
6
;
3x
x
-3x
.
*HasParentheses(V1) #
;
3x
12
x
4
-1
x+3
7
x/3+1
7/3
-3x
6
.
*HasCoefficient(V1) #
3x
-3x
;
12
x
4
-1
x+3
7
x/3+1
7/3
6
.
*IsLastConstTermNegative(V1) #
;
3x
12
x
4
-1
x+3
7
x/3+1
7/3
-3x
6
.
*HasVarTerm(V1) #
x+3
x/3+1
;
3x
12
x
4
-1
7
7/3
-3x
6
.
*NotNull(V1) #
3x
12
x
4
-1
x+3
7
x/3+1
7/3
-3x
6
.
*IsNumeratorOf(V1, V1) ##
7, 7/3
;
3x, 12
12, 3x
3x, x
x, 3x
4, x
4, 12
3x, 4
x, 12
12, x
x, 4
12, 4
4, 3x
-1, x
-1, 4
x, -1
4, -1
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

