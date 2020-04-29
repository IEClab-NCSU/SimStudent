V1: 3x, 3v, x, x+3, 3v+2, 12, 5, 4, 7, v, -1, x/3+1, 7/3, 5/3.
V4: divide 3, subtract 3, subtract 2.

divide(V1, V1) ##
3x, 12
3v, 5
;
x, 4
x+3, 7
3v+2, 7
.
*IsAVarTerm(V1) #
3x
x
3v
v
;
12
4
-1
x+3
7
x/3+1
7/3
3v+2
5
5/3
.
*Homogeneous(V1) #
3x
12
x
4
-1
7
7/3
3v
5
v
5/3
;
x+3
x/3+1
3v+2
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
7, 3v+2
3v+2, x+3
3v+2, 7
x+3, 3v+2
3v, 4
3v, 7
x, 3v
3v+2, x
x+3, 3v
4, 3v+2
3v+2, 4
x, 3v+2
3v, x
3v, 3v+2
3v+2, 3v
7, 3v
4, 3v
3v, x+3
5, 3v
5, 3v+2
5, 4
5, 7
3v, 12
3x, 3v
3v, 5
-1, 3v
5, 3x
3v, 3x
5, -1
4, 5
12, 5
5, x
3v, -1
3x, 5
x, 5
-1, 5
5, 12
12, 3v
v, x+3
v, 12
v, 4
v, 7
7, v
7/3, 3v
v, 3v
v, 3x
3v, x/3+1
v, x/3+1
3v, 7/3
3v, v
7/3, v
v, 7/3
v, x
5/3, 5
5/3, 7
5/3, 3v
5/3, 3x
5, x/3+1
5, 5/3
5/3, x/3+1
3v, 5/3
5, 7/3
5, v
5/3, 7/3
5/3, v
5/3, x
5, x+3
5/3, x+3
5/3, 12
5/3, 4
.
*IsPolynomial(V1) #
x+3
x/3+1
3v+2
;
3x
12
x
4
-1
7
7/3
3v
5
v
5/3
.
*IsConstant(V1) #
12
4
-1
7
7/3
5
5/3
;
3x
x
x+3
x/3+1
3v+2
3v
v
.
*IsSkillAdd(V4) #
;
divide 3
subtract 3
subtract 2
.
*IsSkillDivide(V4) #
divide 3
;
subtract 3
subtract 2
.
*Monomial(V1) #
3x
12
x
4
-1
7
7/3
3v
5
v
5/3
;
x+3
x/3+1
3v+2
.
*IsFractionTerm(V1) #
7/3
5/3
;
3x
12
x
4
-1
x+3
7
x/3+1
3v+2
3v
5
v
.
*HasConstTerm(V1) #
12
4
-1
x+3
7
x/3+1
7/3
3v+2
5
5/3
;
3x
x
3v
v
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
3v+2
3v
5
v
5/3
.
*HasCoefficient(V1) #
3x
3v
;
12
x
4
-1
x+3
7
x/3+1
7/3
3v+2
5
v
5/3
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
3v+2
3v
5
v
5/3
.
*HasVarTerm(V1) #
x+3
x/3+1
3v+2
;
3x
12
x
4
-1
7
7/3
3v
5
v
5/3
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
3v+2
3v
5
v
5/3
.
*IsNumeratorOf(V1, V1) ##
7, 7/3
5, 5/3
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
7, 3v+2
3v+2, x+3
3v+2, 7
x+3, 3v+2
3v, 4
3v, 7
x, 3v
3v+2, x
x+3, 3v
4, 3v+2
3v+2, 4
x, 3v+2
3v, x
3v, 3v+2
3v+2, 3v
7, 3v
4, 3v
3v, x+3
5, 3v
5, 3v+2
5, 4
5, 7
3v, 12
3x, 3v
3v, 5
-1, 3v
5, 3x
3v, 3x
5, -1
4, 5
12, 5
5, x
3v, -1
3x, 5
x, 5
-1, 5
5, 12
12, 3v
v, x+3
v, 12
v, 4
v, 7
7, v
7/3, 3v
v, 3v
v, 3x
3v, x/3+1
v, x/3+1
3v, 7/3
3v, v
7/3, v
v, 7/3
v, x
5/3, 5
5/3, 7
5/3, 3v
5/3, 3x
5, x/3+1
5/3, x/3+1
3v, 5/3
5, 7/3
5, v
5/3, 7/3
5/3, v
5/3, x
5, x+3
5/3, x+3
5/3, 12
5/3, 4
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

