global.util = require 'util'
fs = require 'fs'
coffee = require '/usr/local/share/npm/lib/node_modules/coffee-script'
jison = require '/usr/local/share/npm/lib/node_modules/jison'
require '../../goog_stub.js'
require '../../set.js'
require '../../polyfills.js'
CTATVariableTable = require '../CTATVariableTable.js'
#global.algebraGrammar = fs.readFileSync 'AlgebraGrammar.jison', 'utf8'
#global.algebraParser = new jison.Parser algebraGrammar
global.parser = require './CTATAlgebraGrammar'
global.CTATTreeNode = require './CTATTreeNode'
global.CTATRelationNode = require './CTATRelationNode'
global.CTATAdditionNode = require './CTATAdditionNode'
global.CTATMultiplicationNode = require './CTATMultiplicationNode'
global.CTATIntDivisionNode = require './CTATIntDivisionNode'
global.CTATUnaryNode = require './CTATUnaryNode'
global.CTATPowerNode = require './CTATPowerNode'
global.CTATVariableNode = require './CTATVariableNode'
global.CTATConstantNode = require './CTATconstantNode'
global.CTATAlgebraParser = require './CTATAlgebraParser'
global.CTATAlgebraParser = require './CTATAlgebraParser'
vt = new CTATVariableTable()
vt.put(key, value) for key, value of {'link.selection': 's1', 'link.action': 'a1', 'link.input': 'i1', x: 2.0, y: 3.0, z: 4, w: 8, str: 'abcdefg'}
global.algebraParser = new CTATAlgebraParser vt

module.exports = global.algebraParser if module?
