V1: x+3, 7, 3x+2, x/3+1, -1, 3x, 12, x, 4, 7/3, 5.
V4: subtract 3, subtract 2, divide 3.

subtract-typein(V1, V4) ##
x+3, subtract 3
7, subtract 3
3x+2, subtract 2
7, subtract 2
.
*NotNull(V1) #
x/3+1
-1
3x
x+3
12
x
4
7
7/3
3x+2
5
.
*IsDenominatorOf(V1, V1) ##
;
7, x/3+1
7/3, x+3
7/3, 12
7/3, 7
7, 7/3
7, x
7, x+3
7, 12
7, 4
7/3, x/3+1
x, x+3
x+3, x
7, 3x
x+3, 4
x+3, 7
4, x
4, x+3
4, 7
x, 4
x, 7
4, -1
x, -1
-1, x
-1, 4
3x+2, x+3
7, 3x+2
3x+2, 7
x+3, 3x+2
3x+2, 3x
x, 3x
3x+2, x
x+3, 3x
3x, x
4, 3x+2
3x, x+3
3x+2, 4
3x, 3x+2
3x, 4
3x, 7
x, 3x+2
4, 3x
5, 3x
5, 3x+2
5, 7
12, 3x
-1, 3x
3x, -1
5, -1
4, 5
12, 5
5, x
3x, 12
3x, 5
x, 5
-1, 5
5, 12
5, 4
.
*HasConstTerm(V1) #
x/3+1
-1
x+3
12
4
7
7/3
3x+2
5
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
7, x/3+1
7/3, x+3
7/3, 12
7/3, 7
7, x
7, x+3
7, 12
7, 4
7/3, x/3+1
x, x+3
x+3, x
7, 3x
x+3, 4
x+3, 7
4, x
4, x+3
4, 7
x, 4
x, 7
4, -1
x, -1
-1, x
-1, 4
3x+2, x+3
7, 3x+2
3x+2, 7
x+3, 3x+2
3x+2, 3x
x, 3x
3x+2, x
x+3, 3x
3x, x
4, 3x+2
3x, x+3
3x+2, 4
3x, 3x+2
3x, 4
3x, 7
x, 3x+2
4, 3x
5, 3x
5, 3x+2
5, 7
12, 3x
-1, 3x
3x, -1
5, -1
4, 5
12, 5
5, x
3x, 12
3x, 5
x, 5
-1, 5
5, 12
5, 4
.
*IsFractionTerm(V1) #
7/3
;
x/3+1
-1
3x
x+3
12
x
4
7
3x+2
5
.
*Homogeneous(V1) #
-1
3x
12
x
4
7
7/3
5
;
x/3+1
x+3
3x+2
.
*HasVarTerm(V1) #
x/3+1
x+3
3x+2
;
-1
3x
12
x
4
7
7/3
5
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
x
4
7
7/3
5
;
x/3+1
x+3
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
x/3+1
-1
x+3
12
x
4
7
7/3
3x+2
5
.
*IsAVarTerm(V1) #
3x
x
;
x/3+1
-1
x+3
12
4
7
7/3
3x+2
5
.
*IsPolynomial(V1) #
x/3+1
x+3
3x+2
;
-1
3x
12
x
4
7
7/3
5
.
*IsConstant(V1) #
-1
12
4
7
7/3
5
;
x/3+1
3x
x+3
x
3x+2
.
*IsLastConstTermNegative(V1) #
;
x/3+1
-1
3x
x+3
12
x
4
7
7/3
3x+2
5
.
*HasParentheses(V1) #
;
x/3+1
-1
3x
x+3
12
x
4
7
7/3
3x+2
5
.

