V1: 3x, 12, -11x, 22, 3v, 5, x+3, 7, 3v+2, x, v, 4, -1, x/3+1, 7/3, -2, 5/3.
V4: divide 3, divide -11, subtract 3, subtract 2, divide 11.

divide-typein(V1, V4) ##
3x, divide 3
12, divide 3
-11x, divide -11
22, divide -11
3v, divide 3
5, divide 3
;
x+3, subtract 3
7, subtract 3
3v+2, subtract 2
7, subtract 2
.
*IsAVarTerm(V1) #
3x
x
-11x
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
22
-2
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
-11x
22
-2
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
3x, 22
-11x, 3x
22, 3x
12, -11x
3x, -11x
-11x, 12
22, -11x
22, 12
12, 22
-11x, 22
-11x, x/3+1
-11x, 7/3
-11x, x
7, -11x
-11x, x+3
-11x, 4
-11x, 7
-2, -11x
-2, x+3
-2, 12
-2, 4
-2, 7
22, x/3+1
7, -2
-11x, -2
22, -2
22, 7/3
-2, 22
22, x
-2, 3x
-2, x/3+1
22, x+3
22, 4
22, 7
-2, 7/3
7, 22
-2, x
4, -2
x, -2
-1, -2
-2, -1
7, 3v+2
3v+2, -11x
12, 3v+2
3v+2, x+3
-11x, 3v+2
3v+2, 12
3x, 3v+2
22, 3v+2
3v+2, 7
12, 7
3x, 7
x+3, 3v+2
3v+2, 22
3v+2, 3x
3v, 4
3v, 7
3v+2, x
3v+2, 4
3v, x
3v, 3v+2
3v+2, 3v
3v, x+3
x+3, 5
3v, 5
x, 3v
5, 3v
x+3, 3v
4, 3v+2
3v+2, 5
x, 3v+2
7, 5
4, 5
5, x
5, 3v+2
x, 5
5, x+3
7, 3v
5, 4
4, 3v
5, 7
-11x, 3v
3v, 12
22, 3v
5, 22
5, 3x
3v, 22
3v, 3x
12, 5
-11x, 5
22, 5
5, -11x
5, 12
3v, -11x
12, 3v
-2, 5
-1, 3v
-2, 3v
5, -1
5, -2
3v, -1
3v, -2
-1, 5
v, -11x
v, x+3
v, 12
v, 4
v, 7
v, 22
v, 3v
v, 3x
3v, x/3+1
v, x/3+1
3v, 7/3
3v, v
v, -2
v, 7/3
5, x/3+1
5, 5/3
5, 7/3
5, v
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
-11x
22
-2
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
22
-2
5
5/3
;
3x
x
x+3
x/3+1
-11x
3v+2
3v
v
.
*IsSkillAdd(V4) #
;
divide 3
subtract 3
divide 11
divide -11
subtract 2
.
*IsSkillDivide(V4) #
divide 3
divide 11
divide -11
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
-11x
22
-2
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
-11x
22
-2
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
22
-2
3v+2
5
5/3
;
3x
x
-11x
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
-11x
22
-2
3v+2
3v
5
v
5/3
.
*HasCoefficient(V1) #
3x
-11x
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
22
-2
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
-11x
22
-2
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
-11x
22
-2
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
-11x
22
-2
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
3x, 22
-11x, 3x
22, 3x
12, -11x
3x, -11x
-11x, 12
22, -11x
22, 12
12, 22
-11x, 22
-11x, x/3+1
-11x, 7/3
-11x, x
7, -11x
-11x, x+3
-11x, 4
-11x, 7
-2, -11x
-2, x+3
-2, 12
-2, 4
-2, 7
22, x/3+1
7, -2
-11x, -2
22, -2
22, 7/3
-2, 22
22, x
-2, 3x
-2, x/3+1
22, x+3
22, 4
22, 7
-2, 7/3
7, 22
-2, x
4, -2
x, -2
-1, -2
-2, -1
7, 3v+2
3v+2, -11x
12, 3v+2
3v+2, x+3
-11x, 3v+2
3v+2, 12
3x, 3v+2
22, 3v+2
3v+2, 7
12, 7
3x, 7
x+3, 3v+2
3v+2, 22
3v+2, 3x
3v, 4
3v, 7
3v+2, x
3v+2, 4
3v, x
3v, 3v+2
3v+2, 3v
3v, x+3
x+3, 5
3v, 5
x, 3v
5, 3v
x+3, 3v
4, 3v+2
3v+2, 5
x, 3v+2
7, 5
4, 5
5, x
5, 3v+2
x, 5
5, x+3
7, 3v
5, 4
4, 3v
5, 7
-11x, 3v
3v, 12
22, 3v
5, 22
5, 3x
3v, 22
3v, 3x
12, 5
-11x, 5
22, 5
5, -11x
5, 12
3v, -11x
12, 3v
-2, 5
-1, 3v
-2, 3v
5, -1
5, -2
3v, -1
3v, -2
-1, 5
v, -11x
v, x+3
v, 12
v, 4
v, 7
v, 22
v, 3v
v, 3x
3v, x/3+1
v, x/3+1
3v, 7/3
3v, v
v, -2
v, 7/3
5, x/3+1
5, 7/3
5, v
.
*IsSkillSubtract(V4) #
subtract 3
subtract 2
;
divide 3
divide 11
divide -11
.
*IsSkillMultiply(V4) #
;
divide 3
subtract 3
divide 11
divide -11
subtract 2
.

