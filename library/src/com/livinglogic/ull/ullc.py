# -*- coding: utf-8 -*-

## Copyright 2008 by LivingLogic AG, Bayreuth/Germany
## Copyright 2008 by Walter Dörwald
##
## All Rights Reserved
##
## See ll/__init__.py for the license


import sys, re, StringIO

import spark

from com.livinglogic import ull


###
### helper functions for compiling
###

def _compile(template, tags):
	opcodes = []
	parseexpr = ExprParser().compile
	parsestmt = StmtParser().compile
	parsefor = ForParser().compile
	parserender = RenderParser().compile

	# This stack stores for each nested for/foritem/if/elif/else the following information:
	# 1) Which construct we're in (i.e. "if" or "for")
	# For ifs:
	# 2) How many if's or elif's we have seen (this is used for simulating elif's via nested if's, for each additional elif, we have one more endif to add)
	# 3) Whether we've already seen the else
	stack = []
	for location in tags:
		try:
			if location.type is None:
				template.opcode(ull.Opcode.OC_TEXT, location)
			elif location.type == "print":
				r = parseexpr(template, location)
				template.opcode(ull.Opcode.OC_PRINT, r, location)
			elif location.type == "code":
				parsestmt(template, location)
			elif location.type == "if":
				r = parseexpr(template, location)
				template.opcode(ull.Opcode.OC_IF, r, location)
				stack.append(("if", 1, False))
			elif location.type == "elif":
				if not stack or stack[-1][0] != "if":
					raise ull.BlockException("elif doesn't match any if")
				elif stack[-1][2]:
					raise ull.BlockException("else already seen in elif")
				template.opcode(ull.Opcode.OC_ELSE, location)
				r = parseexpr(template, location)
				template.opcode(ull.Opcode.OC_IF, r, location)
				stack[-1] = ("if", stack[-1][1]+1, False)
			elif location.type == "else":
				if not stack or stack[-1][0] != "if":
					raise ull.BlockException("else doesn't match any if")
				elif stack[-1][2]:
					raise ull.BlockException("duplicate else")
				template.opcode(ull.Opcode.OC_ELSE, location)
				stack[-1] = ("if", stack[-1][1], True)
			elif location.type == "end":
				if not stack:
					raise ull.BlockException("not in any block")
				code = location.code
				if code:
					if code == "if":
						if stack[-1][0] != "if":
							raise ull.BlockException("endif doesn't match any if")
					elif code == "for":
						if stack[-1][0] != "for":
							raise ull.BlockException("endfor doesn't match any for")
					else:
						raise ull.BlockException("illegal end value %r" % code)
				last = stack.pop()
				if last[0] == "if":
					for i in xrange(last[1]):
						template.opcode(ull.Opcode.OC_ENDIF, location)
				else: # last[0] == "for":
					template.opcode(ull.Opcode.OC_ENDFOR, location)
			elif location.type == "for":
				parsefor(template, location)
				stack.append(("for",))
			elif location.type == "render":
				parserender(template, location)
			else: # Can't happen
				raise ValueError("unknown tag %r" % location.type)
		except ull.LocationException, exc:
			raise
		except java.lang.Exception, exc:
			raise ull.LocationException(exc, location)
	if stack:
		raise ull.BlockException("unclosed blocks")
	return opcodes


###
### Parsers for different types of code
###

class ExprParser(spark.GenericParser):
	emptyerror = "expression required"

	def __init__(self, start="expr0"):
		spark.GenericParser.__init__(self, start)

	def compile(self, template, location):
		if not location.code:
			raise ValueError(self.emptyerror)
		try:
			ast = self.parse(ull.Template.tokenizeCode(location))
			registers = ull.Registers()
			return ast.compile(template, registers, location)
		except ull.LocationException, exc:
			raise
		except java.lang.Exception, exc:
			raise ull.LocationException(exc, location)

	def typestring(self, token):
		return token.getTokenType()

	def error(self, token):
		raise ull.SyntaxException(token)

	def makeconst(self, start, end, value):
		if value is None:
			return ull.None(start, end)
		elif value is True:
			return ull.True(start, end)
		elif value is False:
			return ull.False(start, end)
		elif isinstance(value, int):
			return ull.Int(start, end, value)
		elif isinstance(value, float):
			return ull.Float(start, end, value)
		elif isinstance(value, basestring):
			return ull.Str(start, end, value)
		else:
			raise TypeError("can't convert %r" % value)

	# To implement operator precedence, each expression rule has the precedence in its name. The highest precedence is 11 for atomic expressions.
	# Each expression can have only expressions as parts, which have the some or a higher precedence with two exceptions:
	#    1) Expressions where there's no ambiguity, like the index for a getitem/getslice or function/method arguments;
	#    2) Brackets, which can be used to boost the precedence of an expression to the level of an atomic expression.

	def expr_atomic(self, (atom,)):
		return atom
	expr_atomic.spark = [
		'expr11 ::= none',
		'expr11 ::= true',
		'expr11 ::= false',
		'expr11 ::= str',
		'expr11 ::= int',
		'expr11 ::= float',
		'expr11 ::= name',
	]

	def expr_bracket(self, (_0, expr, _1)):
		return expr
	expr_bracket.spark = ['expr11 ::= ( expr0 )']

	def expr_callfunc0(self, (name, _0, _1)):
		return ull.CallFunc(name.start, _1.end, name)
	expr_callfunc0.spark = ['expr10 ::= name ( )']

	def expr_callfunc1(self, (name, _0, arg0, _1)):
		return ull.CallFunc(name.start, _1.end, name, arg0)
	expr_callfunc1.spark = ['expr10 ::= name ( expr0 )']

	def expr_callfunc2(self, (name, _0, arg0, _1, arg1, _2)):
		return ull.CallFunc(name.start, _2.end, name, arg0, arg1)
	expr_callfunc2.spark = ['expr10 ::= name ( expr0 , expr0 )']

	def expr_callfunc3(self, (name, _0, arg0, _1, arg1, _2, arg2, _3)):
		return ull.CallFunc(name.start, _3.end, name, arg0, arg1, arg2)
	expr_callfunc3.spark = ['expr10 ::= name ( expr0 , expr0 , expr0 )']

	def expr_getattr(self, (expr, _0, name)):
		return ull.GetAttr(expr.start, name.end, expr, name)
	expr_getattr.spark = ['expr9 ::= expr9 . name']

	def expr_callmeth0(self, (expr, _0, name, _1, _2)):
		return ull.CallMeth(expr.start, _2.end, expr, name)
	expr_callmeth0.spark = ['expr9 ::= expr9 . name ( )']

	def expr_callmeth1(self, (expr, _0, name, _1, arg1, _2)):
		return ull.CallMeth(expr.start, _2.end, expr, name, arg1)
	expr_callmeth1.spark = ['expr9 ::= expr9 . name ( expr0 )']

	def expr_callmeth2(self, (expr, _0, name, _1, arg1, _2, arg2, _3)):
		return ull.CallMeth(expr.start, _3.end, expr, name, arg1, arg2)
	expr_callmeth2.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 )']

	def expr_callmeth3(self, (expr, _0, name, _1, arg1, _2, arg2, _3, arg3, _4)):
		return ull.CallMeth(expr.start, _4.end, expr, name, arg1, arg2, arg3)
	expr_callmeth3.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 , expr0 )']

	def expr_getitem(self, (expr, _0, key, _1)):
		if isinstance(expr, ull.Const) and isinstance(key, ull.Const): # Constant folding
			return self.makeconst(expr.start, _1.end, expr.value[key.value])
		return ull.GetItem(expr.start, _1.end, expr, key)
	expr_getitem.spark = ['expr9 ::= expr9 [ expr0 ]']

	def expr_getslice12(self, (expr, _0, index1, _1, index2, _2)):
		if isinstance(expr, ull.Const) and isinstance(index1, ull.Const) and isinstance(index2, ull.Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[index1.value:index1.value])
		return ull.GetSlice12(expr.start, _2.end, expr, index1, index2)
	expr_getslice12.spark = ['expr8 ::= expr8 [ expr0 : expr0 ]']

	def expr_getslice1(self, (expr, _0, index1, _1, _2)):
		if isinstance(expr, ull.Const) and isinstance(index1, ull.Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[index1.value:])
		return ull.GetSlice1(expr.start, _2.end, expr, index1)
	expr_getslice1.spark = ['expr8 ::= expr8 [ expr0 : ]']

	def expr_getslice2(self, (expr, _0, _1, index2, _2)):
		if isinstance(expr, ull.Const) and isinstance(index2, ull.Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[:index2.value])
		return ull.GetSlice2(expr.start, _2.end, expr, index2)
	expr_getslice2.spark = ['expr8 ::= expr8 [ : expr0 ]']

	def expr_getslice(self, (expr, _0, _1, _2)):
		if isinstance(expr, ull.Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[:])
		return ull.GetSlice(expr.start, _2.end, expr)
	expr_getslice.spark = ['expr8 ::= expr8 [ : ]']

	def expr_neg(self, (_0, expr)):
		if isinstance(expr, ull.Const): # Constant folding
			return self.makeconst(_0.start, expr.end, -expr.value)
		return ull.Neg(_0.start, expr.end, expr)
	expr_neg.spark = ['expr7 ::= - expr7']

	def expr_mul(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value * obj2.value)
		return ull.Mul(obj1.start, obj2.end, obj1, obj2)
	expr_mul.spark = ['expr6 ::= expr6 * expr6']

	def expr_floordiv(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value // obj2.value)
		return ull.FloorDiv(obj1.start, obj2.end, obj1, obj2)
	expr_floordiv.spark = ['expr6 ::= expr6 // expr6']

	def expr_truediv(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value / obj2.value)
		return ull.TrueDiv(obj1.start, obj2.end, obj1, obj2)
	expr_truediv.spark = ['expr6 ::= expr6 / expr6']

	def expr_mod(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value % obj2.value)
		return ull.Mod(obj1.start, obj2.end, obj1, obj2)
	expr_mod.spark = ['expr6 ::= expr6 % expr6']

	def expr_add(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value + obj2.value)
		return ull.Add(obj1.start, obj2.end, obj1, obj2)
	expr_add.spark = ['expr5 ::= expr5 + expr5']

	def expr_sub(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value - obj2.value)
		return ull.Sub(obj1.start, obj2.end, obj1, obj2)
	expr_sub.spark = ['expr5 ::= expr5 - expr5']

	def expr_equal(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value == obj2.value)
		return ull.Equal(obj1.start, obj2.end, obj1, obj2)
	expr_equal.spark = ['expr4 ::= expr4 == expr4']

	def expr_notequal(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value != obj2.value)
		return ull.NotEqual(obj1.start, obj2.end, obj1, obj2)
	expr_notequal.spark = ['expr4 ::= expr4 != expr4']

	def expr_contains(self, (obj, _0, container)):
		if isinstance(obj, ull.Const) and isinstance(container, ull.Const): # Constant folding
			return self.makeconst(obj.start, container.end, obj.value in container.value)
		return ull.Contains(obj.start, container.end, obj, container)
	expr_contains.spark = ['expr3 ::= expr3 in expr3']

	def expr_notcontains(self, (obj, _0, _1, container)):
		if isinstance(obj, ull.Const) and isinstance(container, ull.Const): # Constant folding
			return self.makeconst(obj.start, container.end, obj.value not in container.value)
		return ull.NotContains(obj.start, container.end, obj, container)
	expr_notcontains.spark = ['expr3 ::= expr3 not in expr3']

	def expr_not(self, (_0, expr)):
		if isinstance(expr, ull.Const): # Constant folding
			return self.makeconst(_0.start, expr.end, not expr.value)
		return ull.Not(_0.start, expr.end, expr)
	expr_not.spark = ['expr2 ::= not expr2']

	def expr_and(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, bool(obj1.value and obj2.value))
		return ull.And(obj1.start, obj2.end, obj1, obj2)
	expr_and.spark = ['expr1 ::= expr1 and expr1']

	def expr_or(self, (obj1, _0, obj2)):
		if isinstance(obj1, ull.Const) and isinstance(obj2, ull.Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, bool(obj1.value or obj2.value))
		return ull.Or(obj1.start, obj2.end, obj1, obj2)
	expr_or.spark = ['expr0 ::= expr0 or expr0']

	# These rules make operators of different precedences interoperable, by allowing an expression to "drop" its precedence.
	def expr_dropprecedence(self, (expr, )):
		return expr
	expr_dropprecedence.spark = [
		'expr10 ::= expr11',
		'expr9 ::= expr10',
		'expr8 ::= expr9',
		'expr7 ::= expr8',
		'expr6 ::= expr7',
		'expr5 ::= expr6',
		'expr4 ::= expr5',
		'expr3 ::= expr4',
		'expr2 ::= expr3',
		'expr1 ::= expr2',
		'expr0 ::= expr1',
	]


class ForParser(ExprParser):
	emptyerror = "loop expression required"

	def __init__(self, start="for"):
		ExprParser.__init__(self, start)

	def for0(self, (iter, _0, cont)):
		return ull.For(iter.start, cont.end, iter, cont)
	for0.spark = ['for ::= name in expr0']

	def for1(self, (_0, iter, _1, _2, _3, cont)):
		return ull.For1(_0.start, cont.end, iter, cont)
	for1.spark = ['for ::= ( name , ) in expr0']

	def for2a(self, (_0, iter1, _1, iter2, _2, _3, cont)):
		return ull.For2(_0.start, cont.end, iter1, iter2, cont)
	for2a.spark = ['for ::= ( name , name ) in expr0']

	def for2b(self, (_0, iter1, _1, iter2, _2, _3, _4, cont)):
		return ull.For2(_0.start, cont.end, iter1, iter2, cont)
	for2a.spark = ['for ::= ( name , name , ) in expr0']


class StmtParser(ExprParser):
	emptyerror = "statement required"

	def __init__(self, start="stmt"):
		ExprParser.__init__(self, start)

	def stmt_assign(self, (name, _0, value)):
		return ull.StoreVar(name.start, value.end, name, value)
	stmt_assign.spark = ['stmt ::= name = expr0']

	def stmt_iadd(self, (name, _0, value)):
		return ull.AddVar(name.start, value.end, name, value)
	stmt_iadd.spark = ['stmt ::= name += expr0']

	def stmt_isub(self, (name, _0, value)):
		return ull.SubVar(name.start, value.end, name, value)
	stmt_isub.spark = ['stmt ::= name -= expr0']

	def stmt_imul(self, (name, _0, value)):
		return ull.MulVar(name.start, value.end, name, value)
	stmt_imul.spark = ['stmt ::= name *= expr0']

	def stmt_itruediv(self, (name, _0, value)):
		return ull.TrueDivVar(name.start, value.end, name, value)
	stmt_itruediv.spark = ['stmt ::= name /= expr0']

	def stmt_ifloordiv(self, (name, _0, value)):
		return ull.FloorDivVar(name.start, value.end, name, value)
	stmt_ifloordiv.spark = ['stmt ::= name //= expr0']

	def stmt_imod(self, (name, _0, value)):
		return ull.ModVar(name.start, value.end, name, value)
	stmt_imod.spark = ['stmt ::= name %= expr0']

	def stmt_del(self, (_0, name)):
		return ull.DelVar(_0.start, name.end, name)
	stmt_del.spark = ['stmt ::= del name']


class RenderParser(ExprParser):
	emptyerror = "render statement required"

	def __init__(self, start="render"):
		ExprParser.__init__(self, start)

	def render(self, (name, _1, expr, _2)):
		return ull.Render(name.start, _2.end, name, expr)
	render.spark = ['render ::= name ( expr0 )']


class Compiler(ull.CompilerType):
	def compile(self, source, tags, startdelim, enddelim):
		template = ull.Template()
		template.startdelim = startdelim
		template.enddelim = enddelim
		template.source = source
		_compile(template, tags)
		return template
