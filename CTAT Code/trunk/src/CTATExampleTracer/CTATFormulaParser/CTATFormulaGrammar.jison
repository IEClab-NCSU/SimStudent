/* description: Parses end executes CTAT formula expressions. */

/* lexical grammar */

%lex

DIGIT [0-9]
BINDIGIT [0-1]
OCTDIGIT [0-7]
HEXDIGIT [0-9A-Fa-f]
ALPHA [A-Za-z_]
ALNUM [A-Za-z0-9_]

%%

\s+ /* skip whitespace */
"null" return 'NULL'
"true" return 'TRUE'
"false" return 'FALSE'
"," return 'COMMA'
"(" return 'LPAREN'
")" return 'RPAREN'
"*" return 'TIMES'
"/" return 'DIVIDE'
"%" return 'REM'
"-" return 'MINUS'
"+" return 'PLUS'
"<=" return 'LESSEQUAL'
">=" return 'GREATEREQUAL'
"<" return 'LESS'
">" return 'GREATER'
"==" return 'EQUAL'
"!=" return 'NOTEQUAL'
"=" return 'ASSIGN'
"!" return 'NOT'
"&&" return 'AND'
"||" return 'OR'
"&" return 'BITAND'
"|" return 'BITOR'
"^" return 'BITXOR'
"~" return 'BITNOT'
"?" return 'IF'
":" return 'ELSE'
({DIGIT}+"."?{DIGIT}*|"."{DIGIT}+)([Ee][+-]?{DIGIT}+)? return 'NUMBER'
(0[Bb]{BINDIGIT}+|0[Oo]{OCTDIGIT}+|0[Xx]{HEXDIGIT}+) return 'NUMBER'
{ALPHA}{ALNUM}* return 'IDENTIFIER'
"\""([^"\\]|"\\".)*"\"" return 'STRING'
<<EOF>> return 'EOF'

/lex

/* operator associations and precedence */

%left COMMA
%right ASSIGN
%right IF ELSE
%left OR
%left AND
%left BITOR
%left BITXOR
%left BITAND
%left EQUAL NOTEQUAL
%left LESS GREATER LESSEQUAL GREATEREQUAL
%left PLUS MINUS
%left TIMES DIVIDE REM
%right NOT
%right BITNOT
%right UMINUS UPLUS
%right EXP

%start formula

%%

/* language grammar */

formula:
  expression EOF {return $1} ;

expressions:
  expressions COMMA expression {$$ = new CTATFormulaTree.ListNode('COMMA', $1, $3)} |
  expression {$$ = new CTATFormulaTree.ListNode('COMMA', $1)} ;

expression:
  variable ASSIGN expression {$$ = new CTATFormulaTree.AssignNode(yy.variableTable, $1, $3)} |
  expression IF expression ELSE expression {$$ = new CTATFormulaTree.IfElseNode($1, $3, $5)} |
  expression OR expression {$$ = new CTATFormulaTree.LogicalNode('OR', $1, $3)} |
  expression AND expression {$$ = new CTATFormulaTree.LogicalNode('AND', $1, $3)} |
  NOT expression {$$ = new CTATFormulaTree.LogicalNode('NOT', $2)} |
  expression BITOR expression {$$ = new CTATFormulaTree.BitLogicalNode('BITOR', $1, $3)} |
  expression BITXOR expression {$$ = new CTATFormulaTree.BitLogicalNode('BITXOR', $1, $3)} |
  expression BITAND expression {$$ = new CTATFormulaTree.BitLogicalNode('BITAND', $1, $3)} |
  BITNOT expression {$$ = new CTATFormulaTree.BitLogicalNode('BITNOT', $2)} |
  expression LESS expression {$$ = new CTATFormulaTree.RelationNode('LESS', $1, $3)} |
  expression GREATER expression {$$ = new CTATFormulaTree.RelationNode('GREATER', $1, $3)} |
  expression LESSEQUAL expression {$$ = new CTATFormulaTree.RelationNode('LESSEQUAL', $1, $3)} |
  expression GREATEREQUAL expression {$$ = new CTATFormulaTree.RelationNode('GREATEREQUAL', $1, $3)} |
  expression EQUAL expression {$$ = new CTATFormulaTree.RelationNode('EQUAL', $1, $3)} |
  expression NOTEQUAL expression {$$ = new CTATFormulaTree.RelationNode('NOTEQUAL', $1, $3)} |
  expression PLUS expression {$$ = new CTATFormulaTree.AdditionNode('PLUS', $1, $3)} |
  expression MINUS expression {$$ = new CTATFormulaTree.AdditionNode('MINUS', $1, $3)} |
  expression TIMES expression {$$ = CTATFormulaTree.MultiplicationNode('TIMES', $1, $3)} |
  expression DIVIDE expression {$$ = CTATFormulaTree.MultiplicationNode('DIVIDE', $1, $3)} |
  expression REM expression {$$ = CTATFormulaTree.MultiplicationNode('REM', $1, $3)} |
  PLUS expression %prec UPLUS {$$ = new AdditionNode('PLUS', null, $2)} |
  MINUS expression %prec UMINUS {$$ = new AdditionNode('MINUS', null, $2)} |
  LPAREN expressions RPAREN {$$ = $2} |
  variable LPAREN expressions RPAREN {$$ = new CTATFormulaTree.FunctionNode(yy.functionTable, $1, $3)} |
  variable LPAREN RPAREN {$$ = new CTATFormulaTree.FunctionNode(yy.functionTable, $1, new CTATFormulaTree.ListNode('COMMA'))} |
  variable {$$ = new CTATFormulaTree.VariableNode(yy.variableTable, yy.sai, $1)} |
  string {$$ = new CTATFormulaTree.ConstantNode($1)} |
  number {$$ = new CTATFormulaTree.ConstantNode($1)} |
  boolean {$$ = new CTATFormulaTree.ConstantNode($1)} |
  null {$$ = new CTATFormulaTree.ConstantNode($1)} ;

variable:
  IDENTIFIER {$$ = yytext} ;

string:
  STRING {$$ = String(yytext)} ;

number:
  NUMBER {$$ = Number(yytext)} ;

boolean:
  TRUE {$$ = true} |
  FALSE {$$ = false} ;

null:
  NULL {$$ = null} ;
