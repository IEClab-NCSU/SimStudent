require '../../polyfills.js'
global.sprintf = require('../../sprintf.js').sprintf
global.CTATVariableTable = require '../CTATVariableTable.js'
global.JisonParser = require './CTATFormulaGrammar.js'
global.CTATFormulaTree = require './CTATFormulaTree.coffee'
global.CTATFormulaParser = require './CTATFormulaParser.coffee'
global.CTATFormulaFunctions = require './CTATFormulaFunctions.coffee'
global.vt = new CTATVariableTable {link: {selection: 's1', action: 'a1', input: 'i1'}, x: 2.0, y: 3.0, z: 4, w: 8, str: 'abcdefg'}
global.parser = new CTATFormulaParser global.vt
global.testExpression = (expression, result) -> parser.evaluate(expression).toString() == result
global.testBooleanExpression = (expression) -> parser.evaluate(expression).toString() == 'true'
global.testBooleanExpressionFalse = (expression) -> parser.evaluate(expression).toString() == 'false'
global.interpolateExpression = (expression, value, expected) -> parser.interpolate(expression, 'mySelection', 'myAction', value).toString() == expected
