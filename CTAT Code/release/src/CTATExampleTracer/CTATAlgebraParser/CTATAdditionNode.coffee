# Parse tree node for additions & subtractions

goog.provide('CTATAdditionNode')
goog.require('CTATTreeNode')

class CTATAdditionNode extends CTATTreeNode
  constructor: (@operator, @terms, @parens = 0, @sign = 1, @exp = 1) ->
    @terms[1].negate() if @subtraction(); @operator = 'PLUS'
  clone: -> new CTATAdditionNode @operator, @terms.map((term) -> term.clone()), @parens, @sign, @exp
  toString: -> super @terms.reduce (result, term) =>
    term.setParens @operator
    "#{result}#{if result and not term.negated() then '+' else ''}#{term.toString()}"
  evaluate: -> super @terms.reduce ((result, term) -> result + term.evaluate()), 0
  equals: (node) -> super(node) and @terms.length is node.terms.length and
    @terms.every((term, index) -> term.equals node.terms[index])
  simplify: (@methods) -> @terms = @terms.map((term) => term.simplify(@methods)); super

  simpleFlatten: ->
    @terms = @terms.reduce ((result, term) ->
      if term.inverted() or term.parented() or not term.addition() then result.push term
      else result.push term.pushNegation().terms...
      result), []
    @
  flatten: -> super; @simpleFlatten(); @pushNegation()

  computeConstants: -> 
    constant = 0
    @terms = @terms.filter (term) ->
      if term.constant() then constant += term.evaluate(); false else true
    if constant isnt 0 or @terms.length is 0 then @terms.unshift(new CTATConstantNode constant)
    if @terms.length > 1 then @ else @pushInversion().terms[0]

  combineSimilar: ->
    groups = []
    @terms.forEach (term) ->
      splitPair = if term.constant() then [null, term]
      else term = term.multiplyOne(); [term, term.factors.shift()]
      if group = groups.find((group) -> group[0]?.equals(splitPair[0]))
        group[1] += splitPair[1].evaluate()
      else groups.push [splitPair[0], splitPair[1].evaluate()]
    @terms = groups.reduce ((result, group) ->
      unless group[1] is 0
        group[1] = new CTATConstantNode(group[1]).popNegation()
        unless group[0] then result.push group[1]
        else group[0].factors.unshift(group[1]); result.push group[0].removeIdentity()
      result), []
    if @terms.length > 1 then @
    else if @terms.length is 1 then @pushInversion().terms[0]
    else @terms[0] = new CTATConstantNode(0); @pushInversion().terms[0]

  removeIdentity: ->
    terms = @terms.filter (term) -> not term.constant(0)
    @terms = if terms.length then terms else @terms[..0]
    if @terms.length > 1 then @ else @pushInversion().terms[0]

  sort: ->
    @spreadIdentity()
    @terms = @terms.sort((node1, node2) -> -node1.compare(node2, true))
    @stripIdentity()
  spreadIdentity: -> @terms = @terms.map((term) -> term.multiplyOne()); @
  stripIdentity: -> @terms = @terms.map((term) -> term.removeIdentity()); @
  compare: (node, reverse) ->
    (value = super) or @countVariables() - node.countVariables() or
    @terms.some((term, index) -> value = term.compare node.terms[index], reverse) and value or
    @compareSigns(node, reverse)
  countVariables: -> @terms.reduce ((result, term) -> result + term.countVariables()), 0

  pushNegation: -> (@negate(); @terms.forEach (term) -> term.negate()) if @negated(); @
  popNegation: -> (@negate(); @terms.forEach (term) -> term.negate()) if @terms[0].negated(); @
  pushInversion: -> (@invert(); @terms[0].invert()) if @inverted(); @
  even: -> not @inverted and @terms.every (term) -> term.even()

if module? then module.exports = CTATAdditionNode else @CTATAdditionNode = CTATAdditionNode
