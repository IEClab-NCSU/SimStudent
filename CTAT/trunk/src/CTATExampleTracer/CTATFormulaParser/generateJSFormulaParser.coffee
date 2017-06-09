global.util = require 'util'
require '../../goog_stub.js'
require '../../set.js'
require '../../polyfills.js'
global.sprintf = require('../../sprintf.js').sprintf
global.useDebuggingBasic = false
global.useDebugging = false
global.CTATBase = require '../../CTATComponentHierarchy/CTATBase.js'
global.CTATVariableTable = require '../CTATVariableTable.js'
global.parser = require '../CTATAlgebraParser/CTATAlgebraGrammar'
global.CTATTreeNode = require '../CTATAlgebraParser/CTATTreeNode'
global.CTATRelationNode = require '../CTATAlgebraParser/CTATRelationNode'
global.CTATAdditionNode = require '../CTATAlgebraParser/CTATAdditionNode'
global.CTATMultiplicationNode = require '../CTATAlgebraParser/CTATMultiplicationNode'
global.CTATIntDivisionNode = require '../CTATAlgebraParser/CTATIntDivisionNode'
global.CTATUnaryNode = require '../CTATAlgebraParser/CTATUnaryNode'
global.CTATPowerNode = require '../CTATAlgebraParser/CTATPowerNode'
global.CTATVariableNode = require '../CTATAlgebraParser/CTATVariableNode'
global.CTATConstantNode = require '../CTATAlgebraParser/CTATconstantNode'
global.CTATAlgebraParser = require '../CTATAlgebraParser/CTATAlgebraParser'
global.CTATFormulaParser = require './CTATJSFormulaParser'
global.CTATFormulaFunctions = require './CTATFormulaFunctions'
global.CTATFormulaActions = require './CTATFormulaActions'
vt = new CTATVariableTable()
vt.put(key, value) for key, value of {'link.selection': 's1', 'link.action': 'a1', 'link.input': 'i1', x: 2.0, y: 3.0, z: 4, w: 8, q: 5, str: 'abcdefg'}
global.formulaParser = new CTATFormulaParser vt

module.exports = global.formulaParser if module?
