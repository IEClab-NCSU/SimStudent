# Algebra expression parser

goog.provide('CTATAlgebraParser')
goog.require('CTATAlgebraGrammar')
goog.require('CTATRelationNode')
goog.require('CTATAdditionNode')
goog.require('CTATMultiplicationNode')
goog.require('CTATIntDivisionNode')
goog.require('CTATUnaryNode')
goog.require('CTATPowerNode')
goog.require('CTATVariableNode')
goog.require('CTATConstantNode')
goog.require('CTATTreeNode')

class CTATAlgebraParser
  constructor: (variableTable) ->
    @parser = new parser.Parser()
    @parser.yy = {
      CTATRelationNode: CTATRelationNode, CTATAdditionNode: CTATAdditionNode,
      CTATMultiplicationNode: CTATMultiplicationNode, CTATIntDivisionNode: CTATIntDivisionNode,
      CTATUnaryNode: CTATUnaryNode, CTATPowerNode: CTATPowerNode, CTATVariableNode: CTATVariableNode,
      CTATConstantNode: CTATConstantNode, CTATTreeNode: CTATTreeNode}
    @parser.yy.variableTable = variableTable

  @none = ['simpleFlatten']
  @partial = ['flatten', 'removeIdentity', 'sort']
  @full = ['flatten', 'computeConstants', 'combineSimilar', 'expand', 'distribute', 'removeIdentity', 'sort']

  algParse: (expression) ->
    try @parser.parse(String(expression)).simplify(CTATAlgebraParser.none) catch then null
  algEvaluate: (expression) -> try @parser.parse(String(expression)).evaluate() catch then null
  algPartialSimplify: (expression) ->
    try @parser.parse(String(expression)).simplify(CTATAlgebraParser.partial).toString() catch then null
  algSimplify: (expression) ->
    try @parser.parse(String(expression)).simplify(CTATAlgebraParser.full).toString() catch then null

  algValid: (expression) -> @algParse(expression)?
  algValued: (expression) -> if (value = @algEvaluate expression)? then not isNaN(value) else null
  algPartialSimplified: (expression) ->
    if (tree1 = @algPartialSimplify expression)? and (tree2 = @algParse expression)?
      tree1.toString() is tree2.toString()
    else null
  algSimplified: (expression) ->
    if (tree1 = @algSimplify expression)? and (tree2 = @algParse expression)?
      tree1.toString() is tree2.toString()
    else null

  algIdentical: (expression1, expression2) ->
    if (tree1 = @algParse expression1)? and (tree2 = @algParse expression2)?
      tree1.equals tree2
    else null
  algEqual: (expression1, expression2) ->
    if (value1 = @algEvaluate expression1)? and (value2 = @algEvaluate expression2)?
      value1 is value2
    else null
  algPartialEquivalent: (expression1, expression2) ->
    if (string1 = @algPartialSimplify expression1)? and (string2 = @algPartialSimplify expression2)?
      string1 is string2
    else null
  algEquivalent: (expression1, expression2) ->
    if (string1 = @algSimplify expression1)? and (string2 = @algSimplify expression2)?
      string1 is string2
    else null

  isAlgValid: @::algValid
  algEval: @::algEvaluate
  algStrictEquivTermsSameOrder: @::algPartialEquivalent
  algEquivTermsSameOrder: @::algPartialEquivalent
  algStrictEquivTerms: @::algPartialEquivalent
  algEquivTerms: @::algPartialEquivalent
  algEquiv: @::algEquivalent

  isSimplified: @::algSimplified
  calc: @::algEvaluate
  calca: (expression) -> (+@algEvaluate(expression)).toFixed 2
  simplify: @::algSimplify
  algebraicEqual: @::algEqual
  patternMatches: @::algIdentical
  polyTermsEqual: (expression1, expression2) ->
    @parser.parse(expression1).simplify(CTATAlgebraParser.partial).
      equals @parser.parse(expression2).simplify(CTATAlgebraParser.partial)
  algebraicMatches: @::algEquivalent
  expressionMatches: @::algEquivalent

if module? then module.exports = CTATAlgebraParser else @CTATAlgebraParser = CTATAlgebraParser
