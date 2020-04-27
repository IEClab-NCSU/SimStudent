V1: 3x, 12, x+3, 7, x+5, 10, x, 4, -1, x/3+1, 7/3, 5, 3v+2.
V4: divide 3, subtract 3, subtract 5, subtract 2.

divide-typein(V1, V4) ##
3x, divide 3
12, divide 3
;
x+3, subtract 3
7, subtract 3
x+5, subtract 5
10, subtract 5
7, subtract 2
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
10
x+5
5
3v+2
.
*IsDenominatorOf(V1, V1) ##
;
3x, x
x, 3x
3x, 12
12, 3x
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
x+3, 4
x/3+1, 3x
4, x/3+1
12, x/3+1
3x, x/3+1
x, x/3+1
x+3, 3x
x/3+1, 4
x/3+1, 12
x+3, x/3+1
x/3+1, x+3
4, x+3
12, x+3
3x, x+3
x/3+1, x
x, x+3
x+3, x
x+3, 12
7, x/3+1
7/3, x+3
7/3, 12
7/3, 7
7, 7/3
7, x
7, 12
7, 4
7/3, x/3+1
7, 3x
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
.
*HasConstTerm(V1) #
12
4
-1
x+3
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
3x, x
x, 3x
3x, 12
12, 3x
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
x+3, 4
x/3+1, 3x
4, x/3+1
12, x/3+1
3x, x/3+1
x, x/3+1
x+3, 3x
x/3+1, 4
x/3+1, 12
x+3, x/3+1
x/3+1, x+3
4, x+3
12, x+3
3x, x+3
x/3+1, x
x, x+3
x+3, x
x+3, 12
7, x/3+1
7/3, x+3
7/3, 12
7/3, 7
7, x
7, 12
7, 4
7/3, x/3+1
7, 3x
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
10
x+5
5
3v+2
.
*Homogeneous(V1) #
3x
12
x
4
-1
7
7/3
10
5
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
3x
12
x
4
-1
7
7/3
10
5
.
*IsSkillDivide(V4) #
divide 3
;
subtract 3
subtract 5
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
10
5
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
;
12
x
4
-1
x+3
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
;
12
4
-1
x+3
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
3x
12
x
4
-1
7
7/3
10
5
.
*IsConstant(V1) #
12
4
-1
7
7/3
10
5
;
3x
x
x+3
x/3+1
x+5
3v+2
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
10
x+5
5
3v+2
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
10
x+5
5
3v+2
.

