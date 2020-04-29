V1: x+3, 7, 3x, x, x/3+1, -1, 12, 4, 7/3, 3v+2.
V4: subtract 3, divide 3, subtract 2.

subtract-typein(V1, V4) ##
x+3, subtract 3
7, subtract 3
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
3v+2
.
*Homogeneous(V1) #
-1
3x
12
x
4
7
7/3
;
x/3+1
x+3
3v+2
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
7, 3v+2
3v+2, x+3
3v+2, 7
x+3, 3v+2
.
*IsPolynomial(V1) #
x/3+1
x+3
3v+2
;
-1
3x
12
x
4
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
x/3+1
3x
x+3
x
3v+2
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
x
4
7
7/3
;
x/3+1
x+3
3v+2
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
3v+2
.
*HasConstTerm(V1) #
x/3+1
-1
x+3
12
4
7
7/3
3v+2
;
3x
x
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
3v+2
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
3v+2
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
3v+2
.
*HasVarTerm(V1) #
x/3+1
x+3
3v+2
;
-1
3x
12
x
4
7
7/3
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
3v+2
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
7, 3v+2
3v+2, x+3
3v+2, 7
x+3, 3v+2
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

