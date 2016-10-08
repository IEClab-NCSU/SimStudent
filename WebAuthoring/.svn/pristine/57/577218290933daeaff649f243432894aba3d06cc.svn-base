constantsDollarPattern = /^([+\-*/^()=<>\sA-Za-z]+|\d+(\.\d\d)?(?![\deE]))*$/
numberPattern = /^\s*[+-]?(NaN|Infinity|(\d+)\.?(\d+)?([Ee]([+-]?\d+))?|\.(\d+)([Ee]([+-]?\d+))?)\s*$/
pluralIesPattern = /([bcdfghjklmnpqrstvwxz]|qu)y$/
pluralEsPattern = /([bcdfghjklmnpqrstvwxz]o|[sc]h|s)$/

gcf = (integer1, integer2) -> (temp = integer2; integer2 = integer1 % integer2; integer1 = temp) while integer2 > 0; Math.abs integer1
toFixed = (number, precision = 2) -> (+number).toFixed Math.round(+precision)
significantDigits = (match, found = false) -> (digit for digit in (match[6] ? match[2] + (match[3] || '')) when found ||= digit isnt '0').length
decimalDigits = (match) -> (match[6] || match[3] || '').length - +(match[5] || match[8] || '0')
isNumber = (value, stringOk, requireInt) -> (stringOk || typeof value == 'number') && !isNaN(value = +value) && (!requireInt || value == Math.round(value))
compare = (value1, value2, predicate) ->
  if isNaN(number1 = +value1) or isNaN(number2 = +value2) then predicate value1.toLowerCase(), value2.toLowerCase() else predicate number1, number2

CTATFormulaFunctions =
  sum: (numbers...) -> numbers.reduce ((sum, number) -> sum + number), 0
  mod: (number1, number2) -> Math.round(number1) % Math.round(number2)
  modf: (number1, number2) -> number1 % number2
  gcf: gcf
  lcm: (integer1, integer2) -> Math.abs(integer1 * integer2) / gcf(integer1, integer2)
  integerInRange: (integer, floor = -Infinity, ceiling = Infinity) -> floor <= integer <= ceiling

  printf: () -> sprintf arguments...
  fmtDecimal: toFixed
  fmtDouble: toFixed
  fmtNormal: (number, precision = 6) -> toFixed(number, precision).replace /\.0+$/, ''
  fmtDollar: (number, flags = '') ->
    number = toFixed number
    if flags.indexOf('i') >= 0 then number = number.replace /\.00$/, ''
    if flags.indexOf('d') >= 0 then '$' + number else number
  matchWithPrecision: (input, number) ->
    (matchInput = String(input).match numberPattern) and (matchNumber = String(number).match numberPattern) and
    significantDigits(matchInput) == significantDigits(matchNumber) and
    compare(input, number, (value1, value2) -> value1 == value2)
  matchWithoutPrecision: (input, number) ->
    (matchInput = String(input).match numberPattern) and (matchNumber = String(number).match numberPattern) and
    (input = if significantDigits(matchInput) > significantDigits(matchNumber) then (+input).toFixed(decimalDigits matchNumber) else String input) and
    compare(input, number, (value1, value2) -> value1 == value2)

  isNumber: isNumber
  isInteger: (value, stringOk) -> isNumber value, stringOk, true

  concat: (strings...) -> strings.reduce ((result, string) -> result + string), ''
  quote: (string) -> "\"#{string}\""
  getDenominator: (string) -> string.split('/')[1]
  getNumerator: (string) -> string.split('/')[0]
  setMatches: (string1, string2, delim) -> new Set(string1.split delim) == new Set(string2.split delim)

  regExMatch: (regexp, string, flags) -> (new RegExp regexp, flags).test string
  rm1coeff: (string) -> string.replace(/^1([a-zA-Z])/g, '$1').replace(/([^0-9])1([a-zA-Z])/g, '$1$2') if string?
  constantsDollar: (string) -> contantsDollarPattern.test string

  plural: (count, word, strip, add) ->
    return word if !word or count < 2
    end = word.length - (strip ? if pluralIesPattern.test(lword = word.toLowerCase()) then 'y' else '').length
    add ?= if pluralIesPattern.test(lword) then 'ies' else if pluralEsPattern.test(lword) then 'es' else 's'
    add = add.toUpperCase() if /[A-Z]/.test word[end - 1]
    word.slice(0, end) + add

  first: (values...) -> values[0]
  last: (values...) -> values[values.length - 1]

  assign: (keyString, value) ->
    [keys..., lastKey] = keyString.split('.'); object = global
    object = (object[key] ?= {}) for key in keys
    object[lastKey] = value
  chooseRandomly: (values...) -> values[Math.round(Math.random() * values.length)]

  firstNonNull: (values...) -> values.some (value) -> value?
  hasValue: (values...) -> values.every (value) -> value? and value.toString().toLowerCase() isnt 'null'

  equals: (value, values...) ->
    if isNaN(number = +value) then values.every (newValue) -> value == newValue
    else values.every (newValue) -> number == +newValue
  memberOf: (value, values...) ->
    if isNaN(number = +value) then values.some (newValue) -> value == newValue
    else values.some (newValue) -> number == +newValue

  greaterThan: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 > value2
  lessThan: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 < value2
  greaterThanOrEqual: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 >= value2
  lessThanOrEqual: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 <= value2
  equal: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 == value2
  notEqual: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 != value2

  and: (booleans...) -> booleans.reduce ((result, boolean) -> result and boolean), true
  or: (booleans...) -> booleans.reduce ((result, boolean) -> result or boolean), false
  not: (boolean) -> !boolean
  ifThen: (boolean, thenValue, elseValue) -> if boolean then thenValue else elseValue

if module? then module.exports = CTATFormulaFunctions else @CTATFormulaFunctions = CTATFormulaFunctions
