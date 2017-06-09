# Parse tree node for integer division & remainder

goog.provide('CTATIntDivisionNode')
goog.require('CTATTreeNode')

class CTATIntDivisionNode extends CTATTreeNode
  constructor: (@operator, @dividend, @divisor, @parens = 0, @sign = 1, @exp = 1) ->
  clone: -> new CTATIntDivisionNode @operator, @dividend.clone(), @divisor.clone(), @parens, @sign, @exp
  toString: ->
    @dividend.setParens @operator; @divisor.setParens @operator, true
    super "#{@dividend.toString()}#{CTATTreeNode.toOperatorString(@operator)}#{@divisor.toString()}"
  evaluate: -> super switch @operator
    when 'REM' then @dividend.evaluate() % @divisor.evaluate()
    when 'IDIVIDE' then @dividend.evaluate() // @divisor.evaluate()
  equals: (node) -> super(node) and @dividend.equals(node.dividend) and @divisor.equals node.divisor
  simplify: (@methods) ->
    @dividend = @dividend.simplify(@methods); @divisor = @divisor.simplify(@methods); super

  computeConstants: ->
    if @dividend.constant() and @divisor.constant() then new CTATConstantNode @evaluate() else @

  removeIdentity: ->
    if @divisor.constant(1)
      @dividend = new CTATConstantNode 0 unless @intDivision()
      @pushNegation().pushInversion().dividend
    else @popNegation()

  compare: (node, reverse) ->
    super or @dividend.compare(node.dividend, reverse) or
    @divisor.compare(node.divisor, reverse) or
    @operator isnt node.operator and (@intDivision() and -1 or 1) or @compareSigns(node, reverse)
  countVariables: -> @dividend.countVariables() + divisor.countVariables()

  pushNegation: -> (@negate(); @dividend.negate()) if @intDivision() and @negated(); @
  pushInversion: -> (@invert(); @dividend.invert()) if @intDivision() and @inverted(); @
  popNegation: -> (@divisor.negate(); @dividend.negate() if @intDivision()) if @divisor.negated(); @
  even: -> not @inverted and @operator is 'REM' and @dividend.even() and @divisor.even()

if module? then module.exports = CTATIntDivisionNode else @CTATIntDivisionNode = CTATIntDivisionNode
