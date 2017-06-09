global.util = require 'util'
require '../../goog_stub.js'
require '../../set.js'
require '../../polyfills.js'
global.sprintf = require('../../sprintf.js').sprintf
global.CTATVariableTable = require '../CTATVariableTable.js'
global.parser = require './CTATFormulaGrammar.js'
global.CTATFormulaTree = require './CTATFormulaTree'
global.CTATFormulaParser = require './CTATFormulaParser'
global.CTATFormulaFunctions = require './CTATFormulaFunctions'
vt = new CTATVariableTable()
vt.put(key, value) for key, value of {'link.selection': 's1', 'link.action': 'a1', 'link.input': 'i1', x: 2.0, y: 3.0, z: 4, w: 8, q: 5, str: 'abcdefg'}
global.formulaParser = new CTATFormulaParser vt

module.exports = global.formulaParser if module?
