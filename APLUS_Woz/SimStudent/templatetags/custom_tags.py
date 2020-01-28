from django import template

register = template.Library()

@register.filter
def remainder(value, arg):
	split_val = value.split(arg)
	return split_val[0]

#def split(value, key):
#  """
#    Returns the value turned into a list.
#  """
#  return value.split(key)


