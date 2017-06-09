# Base parse tree class

goog.provide('CTATTreeNode')

class CTATTreeNode
  @operators: [['CONST'], ['VAR'], ['EXP', 'SQRT'], ['UPLUS', 'UMINUS'],
    ['ITIMES', 'TIMES', 'DIVIDE'], ['IDIVIDE', 'REM'], ['PLUS', 'MINUS'],
    ['LESS', 'LESSEQUAL', 'EQUAL', 'NOTEQUAL', 'GREATEREQUAL', 'GREATER']]
  @relationalOperators = ['LESS', 'LESSEQUAL', 'EQUAL', 'NOTEQUAL', 'GREATEREQUAL', 'GREATER']
  @operatorPrecedence: (operator1, operator2) ->
    Math.sign (@operators.findIndex((group) -> group.includes operator1) -
      @operators.findIndex((group) -> group.includes operator2))
  @operatorStrings: {
    'EXP': '^', 'SQRT': '|', 'UPLUS': '+', 'UMINUS': '-', 'ITIMES': '', 'TIMES': '*', 'DIVIDE': '/',
    'IDIVIDE': '//', 'REM': '%', 'PLUS': '+', 'MINUS': '-',  'LESS': '<', 'LESSEQUAL': '<=',
    'EQUAL': '=', 'NOTEQUAL': '!=', 'GREATEREQUAL': '>=', 'GREATER': '>'}
  @toOperatorString: (operator) -> @operatorStrings[operator] || ''
  @diff: (list1, list2) -> list1.filter (item) -> not list2.includes(item)

  addParens: -> @parens++; @
  toString: (string) ->
    @setParens 'MINUS' if @negated(); @setParens 'DIVIDE' if @inverted()
    string = "1/#{string}" if @inverted(); string = "-#{string}" if @negated()
    string = "(#{string})" for i in [0...@parens]; string
  setParens: (operator1, right = false) ->
    operator2 = if @negated() then 'UMINUS' else if @inverted() then 'DIVIDE' else @operator
    precedence = CTATTreeNode.operatorPrecedence operator1, operator2
    @parens = 1 if precedence < 0 or precedence is 0 and right; @
  evaluate: (value) -> @sign * value ** @exp
  equals: (node) -> node and @operator is node.operator and @sign is node.sign and
    @exp is node.exp and @parens is node.parens

  simplify: (@methods) -> @simplifyNode(@methods)
  simplifyNode: (@methods) ->
    result = @
    (result = result[method].call(result); result.methods = @methods) for method in @methods
    delete result.methods; result

  simpleFlatten: -> @
  flatten: -> @parens = 0; @
  computeConstants: -> @
  combineSimilar: -> @
  expand: -> @
  distribute: -> @
  removeIdentity: -> @
  sort: -> @
  spreadIdentity: -> @
  stripIdentity: -> @
  multiplyOne: -> new CTATMultiplicationNode('TIMES', [new CTATConstantNode(1), @]).popNegation().pushNegation()
  powerOne: -> new CTATPowerNode('EXP', @, new CTATConstantNode(1)).popInversion().pushInversion().popNegation()
  compare: (node, reverse) -> CTATTreeNode.operatorPrecedence(@operator, node.operator)
  compareSigns: (node, reverse) -> (Math.sign(@sign - node.sign) or Math.sign(@exp - node.exp)) * reverse
  countVariables: -> 0

  pushNegation: -> @
  popNegation: -> @
  pushInversion: -> @
  popInversion: -> @
  negate: -> @sign = - @sign; @
  invert: -> @exp = - @exp; @
  addition: -> @operator is 'PLUS'
  subtraction: -> @operator is 'MINUS'
  multiplication: -> @operator is 'TIMES'
  division: -> @operator is 'DIVIDE'
  intDivision: -> @operator is 'IDIVIDE'
  power: -> @operator is 'EXP'
  root: -> @operator is 'SQRT'
  negation: -> @operator is 'UMINUS'
  constant: (value) -> false
  integer: -> false
  negated: -> @sign < 0
  inverted: -> @exp < 0
  parented: -> @parens > 0
  even: -> false

if module? then module.exports = CTATTreeNode else @CTATTreeNode = CTATTreeNode
