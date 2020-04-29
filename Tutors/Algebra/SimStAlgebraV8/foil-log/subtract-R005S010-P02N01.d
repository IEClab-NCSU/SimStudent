V1: x+3, 3v+2, x, 7, 4, 3x, 3v, -1, 12, x/3+1, 7/3.
V4: divide 3, subtract 3, subtract 2.

subtract(V1, V1) ##
x+3, 7
3v+2, 7
;
x, 4
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
3v+2
.
*Homogeneous(V1) #
-1
3x
12
4
x
7
7/3
3v
;
x+3
x/3+1
3v+2
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
.
*IsPolynomial(V1) #
x+3
x/3+1
3v+2
;
-1
3x
12
4
x
7
7/3
3v
.
*IsConstant(V1) #
-1
12
4
7
7/3
;
x+3
3x
x
x/3+1
3v+2
3v
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
-1
3x
12
4
x
7
7/3
3v
;
x+3
x/3+1
3v+2
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
3v+2
3v
.
*HasConstTerm(V1) #
-1
x+3
12
4
7
x/3+1
7/3
3v+2
;
3x
x
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
3v+2
3v
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
3v+2
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
3v+2
3v
.
*HasVarTerm(V1) #
x+3
x/3+1
3v+2
;
-1
3x
12
4
x
7
7/3
3v
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
3v+2
3v
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

