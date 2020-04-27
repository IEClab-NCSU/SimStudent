V1: x+3, x+5, 3v+2, x, 3x, 3v, 7, 10, 4, 5, 12, -1, x/3+1, 7/3.
V4: subtract 3, subtract 5, subtract 2, divide 3.

subtract(V1, V1) ##
x+3, 7
x+5, 10
3v+2, 7
;
x, 4
x, 5
3x, 12
3v, 5
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
10
x+5
5
3v+2
3v
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
10, 7
7, x+5
7, 10
x+5, x+3
x+5, 10
x+5, 7
10, x+3
10, x+5
x+3, x+5
x+3, 10
x+5, x
4, x+5
x+5, 4
x, x+5
10, 4
10, 5
10, x
5, 10
5, -1
4, 5
5, x
x, 5
-1, 5
5, 4
7, 3v+2
3v+2, x+3
x+5, 3v+2
3v+2, x+5
3v+2, 10
3v+2, 7
10, 3v+2
x+3, 3v+2
x+3, 5
4, 10
7, 5
x+5, 5
x, 10
5, x+3
5, x+5
5, 7
x+5, 3v
3v, x+5
3v, 10
3v, 4
3v, 5
3v, 7
3v+2, x
5, 3v
10, 3v
3v+2, 4
3v+2, 5
5, 3v+2
3v, x
3v, 3v+2
3v+2, 3v
3v, x+3
3v, 12
3x, 3v
5, 3x
3v, 3x
12, 5
3x, 5
5, 12
12, 3v
.
*HasConstTerm(V1) #
-1
x+3
12
4
7
x/3+1
7/3
10
x+5
5
3v+2
;
3x
x
3v
.
*IsSkillSubtract(V4) #
subtract 3
subtract 5
subtract 2
;
divide 3
.
*IsSkillMultiply(V4) #
;
divide 3
subtract 3
subtract 5
subtract 2
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
10, 7
7, x+5
7, 10
x+5, x+3
x+5, 10
x+5, 7
10, x+3
10, x+5
x+3, x+5
x+3, 10
x+5, x
4, x+5
x+5, 4
x, x+5
10, 4
10, 5
10, x
5, 10
5, -1
4, 5
5, x
x, 5
-1, 5
5, 4
7, 3v+2
3v+2, x+3
x+5, 3v+2
3v+2, x+5
3v+2, 10
3v+2, 7
10, 3v+2
x+3, 3v+2
x+3, 5
4, 10
7, 5
x+5, 5
x, 10
5, x+3
5, x+5
5, 7
x+5, 3v
3v, x+5
3v, 10
3v, 4
3v, 5
3v, 7
3v+2, x
5, 3v
10, 3v
3v+2, 4
3v+2, 5
5, 3v+2
3v, x
3v, 3v+2
3v+2, 3v
3v, x+3
3v, 12
3x, 3v
5, 3x
3v, 3x
12, 5
3x, 5
5, 12
12, 3v
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
10
x+5
5
3v+2
3v
.
*Homogeneous(V1) #
-1
3x
12
4
x
7
7/3
10
5
3v
;
x+3
x/3+1
x+5
3v+2
.
*HasVarTerm(V1) #
x+3
x/3+1
x+5
3v+2
;
-1
3x
12
4
x
7
7/3
10
5
3v
.
*IsSkillDivide(V4) #
divide 3
;
subtract 3
subtract 5
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
10
5
3v
;
x+3
x/3+1
x+5
3v+2
.
*IsSkillAdd(V4) #
;
divide 3
subtract 3
subtract 5
subtract 2
.
*HasCoefficient(V1) #
3x
3v
;
-1
x+3
12
4
x
7
x/3+1
7/3
10
x+5
5
3v+2
.
*IsAVarTerm(V1) #
3x
x
3v
;
-1
x+3
12
4
7
x/3+1
7/3
10
x+5
5
3v+2
.
*IsPolynomial(V1) #
x+3
x/3+1
x+5
3v+2
;
-1
3x
12
4
x
7
7/3
10
5
3v
.
*IsConstant(V1) #
-1
12
4
7
7/3
10
5
;
x+3
3x
x
x/3+1
x+5
3v+2
3v
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
10
x+5
5
3v+2
3v
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
10
x+5
5
3v+2
3v
.

