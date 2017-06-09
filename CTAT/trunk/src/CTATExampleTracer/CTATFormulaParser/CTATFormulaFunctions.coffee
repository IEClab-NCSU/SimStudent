# Functions callable from formula expressions

goog.provide('CTATFormulaFunctions')

# Regexp patterns
fractionPattern = /^(\d+)?(?:$|(?:\s|^)(\d+)\/(\d+)$)/
dollarPattern = /^\d+(\.\d\d)?$/
unsignedString = '((\\d+)\\.?(\\d+)?|\\.(\\d+))([Ee]([+-]?\\d+))?'
globalUnsignedPattern = new RegExp unsignedString, 'g'
signedPattern = new RegExp "^[+-]?#{unsignedString}$"
pointPattern = new RegExp "^\\s*\\(\\s*([+-]?#{unsignedString})\\s*\\,\\s*([+-]?#{unsignedString})\\s*\\)\\s*$"
pluralIesPattern = /([bcdfghjklmnpqrstvwxz]|qu)y$/
pluralEsPattern = /([bcdfghjklmnpqrstvwxz]o|[sc]h|s)$/

# Auxilliary
significantDigits = (match, found = false) ->
  (digit for digit in (match[4] ? match[2] + (match[3] || '')) when found ||= digit isnt '0').length
decimalDigits = (match) -> (match[4] || match[3] || '').length - +(match[6] || '0')
compare = (value1, value2, predicate) ->
  if isNaN(number1 = +value1) or isNaN(number2 = +value2)
    predicate String(value1).toLowerCase(), String(value2).toLowerCase()
  else predicate number1, number2

class CTATFormulaFunctions

  # From third party utility
  @printf: (window ? global).sprintf

  # Numbers
  @sum: (numbers...) -> numbers.reduce ((sum, number) -> sum + (+number)), 0
  @mod: (number1, number2) -> Math.round(number1) % Math.round(number2)
  @modf: (number1, number2) -> number1 % number2
  @IEEEremainder: @modf
  @isFactor: (integer1, integer2) -> +integer1 isnt 1 and integer2 % integer1 is 0
  @isMultiple: (integer1, integer2) -> integer1 % integer2 is 0
  @isUnit: (integer) -> +integer is 1
  @isAny: (integer1, integer2) ->
    @isFactor(integer1, integer2) or @isMultiple(integer1, integer2) or @isUnit(integer1)
  @gcf: (integer1, integer2) ->
    [integer1, integer2] = [Math.abs(integer1), Math.abs(integer2)]
    [integer1, integer2] = [integer2, integer1 % integer2] while integer2 > 0
    integer1
  @lcm: (integer1, integer2) -> Math.abs(integer1 * integer2) / @gcf(integer1, integer2)
  @integerInRange: (integer, floor = -Infinity, ceiling = Infinity) ->
    Math.round(floor) <= Math.round(integer) <= Math.round(ceiling)

  # Number string formatting
  @fmtDecimal: (number, precision = 2) -> (+number).toFixed Math.round(+precision)
  @fmtNormal: (number, precision = 6) -> @fmtDecimal(number, precision).replace /\.0+$|(\..+?)0+$/, '$1'
  @fmtDollar: (number, flags = '') ->
    number = @fmtDecimal number; flags = flags.toLowerCase()
    if flags.indexOf('i') >= 0 then number = number.replace /\.00$/, ''
    if flags.indexOf('d') >= 0 then '$' + number else number

  # Number string tests
  @dollarEquals: (number, values...) ->
    number = @fmtDollar number, 'i'
    values.some (value) => number is @fmtDollar(value, 'i')
  @matchWithPrecision: (number1, number2) ->
    (matchNumber1 = String(number1).match signedPattern) and
    (matchNumber2 = String(number2).match signedPattern) and
    significantDigits(matchNumber1) == significantDigits(matchNumber2) and
    compare(number1, number2, (value1, value2) -> value1 == value2)
  @matchWithoutPrecision: (number1, number2) ->
    (matchNumber1 = String(number1).match signedPattern) and
    (matchNumber2 = String(number2).match signedPattern) and
    (number1 = if significantDigits(matchNumber1) > significantDigits(matchNumber2)
    then (+number1).toFixed(decimalDigits matchNumber2) else String number1) and
    compare(number1, number2, (value1, value2) -> value1 == value2)
  @constantsConform: (expression, patterns...) ->
    patterns = patterns.map (pattern) ->
      if typeof pattern is 'string' then new RegExp "^#{pattern}$", 'i' else pattern
    (expression.match(globalUnsignedPattern) || []).every (number) ->
      patterns.some (pattern) -> pattern.test number
  @constantsDollar: (expression) -> @constantsConform expression, dollarPattern

  # String values
  @isNumber: (number, stringOk = true, requireInt) ->
    (stringOk || typeof number == 'number') and !isNaN(number = +number) and
    (!requireInt || number == Math.round(number))
  @isInteger: (number, stringOk) -> @isNumber number, stringOk, true
  @isVar: (string) -> /^\s*[a-zA-Z]\s*$/.test string

  # Fraction strings
  @makeFraction: (whole, numerator, denominator) ->
    [whole, numerator, denominator] = [0, whole, numerator] if numerator? and not denominator?
    whole = +whole || 0; numerator = +numerator || 0; denominator = +denominator || 1
    if @isInteger(whole) and @isInteger(numerator) and @isInteger(denominator)
      (if whole then whole + (if numerator then ' ' else '') else '') +
      (if numerator then numerator + (if denominator is 1 then '' else '/' + denominator) else '')
    else null
  @fractionMake: @makeFraction
  @getWhole: (fraction) -> if (fraction = fraction.match fractionPattern)? then fraction[1] || '0' else null
  @getNumerator: (fraction) -> if (fraction = fraction.match fractionPattern)? then fraction[2] || '0' else null
  @getDividend: @getNumerator
  @getDenominator: (fraction) -> if (fraction = fraction.match fractionPattern)? then fraction[3] || '1' else null
  @getDivisor: @getDenominator
  @convertToMixed: (fraction) ->
    if (fraction = fraction.match fractionPattern)?
      fraction[1] = (+fraction[1] || 0) + (+fraction[2] || 0) // (+fraction[3] || 1)
      fraction[2] = (+fraction[2] || 0) % (+fraction[3] || 1)
      @makeFraction(fraction[1], fraction[2], fraction[3])
    else null
  @convertToImproper: (fraction) ->
    if (fraction = fraction.match fractionPattern)?
      fraction[2] = (+fraction[1] || 0) * (+fraction[3] || 1) + (+fraction[2] || 0)
      fraction[1] = 0; fraction[3] ?= 1
      @makeFraction(fraction[1], fraction[2], fraction[3])
    else null
  @evaluateFraction: (fraction) ->
    if (fraction = fraction.match fractionPattern)?
      ((+fraction[1] || 0) * (+fraction[3] || 1) + (+fraction[2] || 0)) / (+fraction[3] || 1)
    else null
  @divEvaluate: @evaluateFraction
  @simplifyFraction: (fraction) ->
    if (fraction = fraction.match fractionPattern)?
      if fraction[1]? and fraction[2]?
        fraction[1] = +fraction[1] + +fraction[2] // +fraction[3]
        fraction[2] = +fraction[2] % +fraction[3]
      gcd = if fraction[2]? then @gcf +fraction[2], +fraction[3] else 1
      @makeFraction fraction[1], fraction[2]/gcd, fraction[3]/gcd
    else null
  @fractionsimplify: @simplifyFraction
  @simplifyMixed: @simplifyFraction
  @simplifiedFraction: (fraction) ->
    if (simplified = @simplifyFraction fraction)? then simplified is fraction else null
  @isSimplifiedMixed: @simplifiedFraction
  @fractionEquals: (fraction1, fraction2) ->
    if (value1 = @evaluateFraction fraction1)? and (value2 = @evaluateFraction fraction2)?
      value1 is value2
    else null
  @simplifiedFractionEquals: (fraction1, fraction2) ->
    if (value1 = @simplifiedFraction fraction1)? and (value2 = @simplifiedFraction fraction2)?
      value1 and value2 and @fractionEquals(fraction1, fraction2)
    else null
  @rationalEquals: (number1, number2) ->
    @matchWithoutPrecision +number2 || @evaluateFraction(number2), +number1 || @evaluateFraction(number1)

  # Strings
  @quote: (string) -> "\"#{string}\""
  @compact: (string) -> string.replace(/\s*/g, '')
  @equalsIgnoreCase: (string1, string2) -> string1 is string2 or
    string1.toUpperCase() is string2.toUpperCase() or string1.toLowerCase() is string2.toLowerCase()
  @replaceFirst: (string, pattern, replace) -> string.replace(new RegExp(pattern), replace)
  @replaceAll: (string, pattern, replace) -> string.replace(new RegExp(pattern, 'g'), replace)
  @regExMatch: (pattern, string, flags) ->
    (if typeof pattern is 'string' then new RegExp '^(' + pattern + ')$', flags else pattern).test string
  @rm1coeff: (string) -> string.replace(/(^|[^0-9])1([a-zA-Z])/g, '$1$2') if string?

  # Sets
  @setMatches: (string1, string2, delimiter) ->
    set1 = string1.split delimiter; set2 = string2.split delimiter
    set2.every((item) -> set1.includes item) and set1.every((item) -> set2.includes item)

  # Point strings
  @pointInRange: (point, x, y, xTolerance, yTolerance) ->
    [x, y, xTolerance, yTolerance] = [coords[1], coords[8], y, xTolerance] if coords = x?.match? pointPattern
    xTolerance ?= 0; yTolerance ?= xTolerance
    (coords = point.match pointPattern) and
    x - xTolerance <= coords[1] <= +x + +xTolerance and y - yTolerance <= coords[8] <= +y + +yTolerance

  # Words
  @plural: (count, word, strip, add) ->
    return word if !word or count < 2
    strip ?= if pluralIesPattern.test(lword = word.toLowerCase()) then 'y' else ''
    return word unless word.endsWith(strip)
    add ?= if pluralIesPattern.test(lword) then 'ies' else if pluralEsPattern.test(lword) then 'es' else 's'
    word = word.split(strip)[0] if strip.length
    word + (if /[A-Z]/.test word[word.length-1] then add.toUpperCase() else add)

  # Special
  @totalTableDec: (c1, c2, c3, c4) -> 1000 * (+c1 || 0) + 100 * (+c2 || 0) + 10 * (+c3 || 0) + (+c4 || 0)
  @columnCell: (s1, s2, s3, s4, v1, v2, v3, v4, column, returnType) ->
    switch column
      when 'r1c1', 'r2c1', 'r3c1' then (if returnType then s1 else v1)
      when 'r1c2', 'r2c2', 'r3c2' then (if returnType then s2 else v2)
      when 'r1c3', 'r2c3', 'r3c3' then (if returnType then s3 else v3)
      when 'r1c4', 'r2c4', 'r3c4' then (if returnType then s4 else v4)
      else 0

  # Value lists
  @first: (values...) -> values[0]
  @last: (values...) -> values[values.length - 1]
  @chooseRandomly: (values...) -> values[Math.floor(Math.random() * values.length)]
  @firstNonNull: (values...) -> values.find((value) -> value?) ? null
  @valueOrZero: (values...) -> values.find((value) -> value?) ? 0
  @hasValue: (values...) -> values.every (value) -> value?

  # Value tests
  @equals: (value, values...) ->
    value ?= undefined
    if isNaN(number = +value) then values.every (newValue) -> value is (newValue ?= undefined)
    else values.every (newValue) -> number is +(newValue ?= undefined)
  @memberOf: (value, values...) ->
    value ?= undefined
    if isNaN(number = +value) then values.some (newValue) -> value is (newValue ?= undefined)
    else values.some (newValue) -> number is +(newValue ?= undefined)

  # Relational
  @greaterThan: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 > value2
  @lessThan: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 < value2
  @greaterThanOrEqual: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 >= value2
  @lessThanOrEqual: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 <= value2
  @equal: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 == value2
  @notEqual: (value1, value2) -> compare value1, value2, (value1, value2) -> value1 != value2

  # Booleans
  @and: (booleans...) -> booleans.reduce ((result, boolean) -> result and boolean), true
  @or: (booleans...) -> booleans.reduce ((result, boolean) -> result or boolean), false
  @not: (boolean) -> !boolean
  @ifThen: (boolean, thenValue, elseValue) -> if boolean then thenValue else elseValue

  # Assignment
  @assign: (keyString, value) ->
    object = window ? global; [keys..., lastKey] = keyString.split('.')
    object = (object[key] ?= {}) for key in keys; object[lastKey] = value

if module? then module.exports = CTATFormulaFunctions else @CTATFormulaFunctions = CTATFormulaFunctions
