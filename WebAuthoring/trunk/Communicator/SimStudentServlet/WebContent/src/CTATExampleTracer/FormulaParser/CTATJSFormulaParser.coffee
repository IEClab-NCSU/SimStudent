window.global = window if window?

class CTATFormulaParser
  constructor: (@variableTable) ->
    @keys = []; @values = []
    (@keys.push key; @values.push Number[key]) for key in [
      'EPSILON', 'MAX_VALUE', 'MIN_VALUE', 'POSITIVE_INFINITY', 'NEGATIVE_INFINITY',
      'isFinite', 'isInteger', 'isNaN', 'parseFloat', 'parseInt']
    (@keys.push key; @values.push ((number, args...) -> Number.prototype[key].apply number, args)) for key in [
      'toExponential', 'toFixed', 'toLocaleString', 'toPrecision', 'toString', 'valueOf']
    (@keys.push key; @values.push Math[key]) for key in [
      'E', 'LN10', 'LN2', 'LOG10E', 'LOG2E', 'PI', 'SQRT1_2', 'SQRT2',
      'abs', 'acos', 'acosh', 'asin', 'asinh', 'atan', 'atanh', 'atan2', 'cbrt', 'ceil', 'clz32', 'cos', 'cosh', 'exp',
      'expm1', 'floor', 'fround', 'hypot', 'imul', 'log', 'log10', 'log1p', 'log2', 'max', 'min', 'pow',
      'random', 'round', 'sign', 'sin', 'sinh', 'sqrt', 'tan', 'tanh', 'trunc']
    (@keys.push key; @values.push String[key]) for key in ['fromCharCode', 'fromCodePoint']
    (@keys.push key; @values.push ((string, args...) -> String.prototype[key].apply string, args)) for key in [
      'charAt', 'charCodeAt', 'codePointAt', 'concat', 'contains', 'endsWith', 'indexOf', 'lastIndexOf',
      'localeCompare', 'match', 'repeat', 'replace', 'search', 'slice', 'split', 'startsWith', 'substr', 'substring',
      'toLocaleLowerCase', 'toLocaleUpperCase', 'toLowerCase', 'toUpperCase', 'trim', 'valueOf']
    @keys.push 'length'; @values.push (string) -> string.length
    (@keys.push key; @values.push value) for key, value of CTATFormulaFunctions

  evaluate: (expression, selection, action, input) ->
    try
      globals = (key for key of global)
      (new Function('selection', 'action', 'input', @keys..., "with (this) {return #{expression}}")).
        apply @variableTable.getTable(), [selection, action, input].concat @values
    catch error
      null
    finally
      @variableTable.put(key, global[key]) for key of global when key not in globals

  interpolateSplitPattern: /<%=|%>/
  interpolate: (message, selection, action, input) ->
    ((if index % 2 then item else @evaluate(item, selection, action, input)) for item, index in message.split(@interpolateSplitPattern)).join('')
  # interpolate: (message, selection, action, input) ->
  #   message.split(@interpolateSplitPattern).map((item, index) => if index % 2 then @evaluate(item, selection, action, input) else item).join('')

if module? then module.exports = CTATFormulaParser else @CTATFormulaParser = CTATFormulaParser
