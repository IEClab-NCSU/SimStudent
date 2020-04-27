V1: x+3, x+5, x, 7, 15, 4, 10, -1, 3x, 12, x/3+1, 7/3.
V4: subtract 3, subtract 5, divide 3.

subtract(V1, V1) ##
x+3, 7
x+5, 15
;
x, 4
x, 10
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
x+5
15
10
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
x+3, 15
7, x+5
15, x+3
15, x+5
x+5, x+3
7, 15
15, 7
x+5, 15
x+5, 7
x+3, x+5
x+5, x
4, x+5
x+5, 4
x, x+5
10, 15
15, x
15, 10
15, 4
10, 4
4, 10
10, -1
10, x
x, 10
-1, 10
.
*HasConstTerm(V1) #
-1
x+3
12
4
7
x/3+1
7/3
x+5
15
10
;
3x
x
.
*IsSkillSubtract(V4) #
subtract 3
subtract 5
;
divide 3
.
*IsSkillMultiply(V4) #
;
divide 3
subtract 3
subtract 5
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
x+3, 15
7, x+5
15, x+3
15, x+5
x+5, x+3
7, 15
15, 7
x+5, 15
x+5, 7
x+3, x+5
x+5, x
4, x+5
x+5, 4
x, x+5
10, 15
15, x
15, 10
15, 4
10, 4
4, 10
10, -1
10, x
x, 10
-1, 10
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
x+5
15
10
.
*Homogeneous(V1) #
-1
3x
12
4
x
7
7/3
15
10
;
x+3
x/3+1
x+5
.
*HasVarTerm(V1) #
x+3
x/3+1
x+5
;
-1
3x
12
4
x
7
7/3
15
10
.
*IsSkillDivide(V4) #
divide 3
;
subtract 3
subtract 5
.
*Monomial(V1) #
-1
3x
12
4
x
7
7/3
15
10
;
x+3
x/3+1
x+5
.
*IsSkillAdd(V4) #
;
divide 3
subtract 3
subtract 5
.
*HasCoefficient(V1) #
3x
;
-1
x+3
12
4
x
7
x/3+1
7/3
x+5
15
10
.
*IsAVarTerm(V1) #
3x
x
;
-1
x+3
12
4
7
x/3+1
7/3
x+5
15
10
.
*IsPolynomial(V1) #
x+3
x/3+1
x+5
;
-1
3x
12
4
x
7
7/3
15
10
.
*IsConstant(V1) #
-1
12
4
7
7/3
15
10
;
x+3
3x
x
x/3+1
x+5
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
x+5
15
10
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
x+5
15
10
.

