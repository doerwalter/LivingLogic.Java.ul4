# -*- coding: utf-8 -*-

## Copyright 2008 by LivingLogic AG, Bayreuth/Germany
## Copyright 2008 by Walter Dörwald
##
## All Rights Reserved
##
## See ll/__init__.py for the license


import sys, re, StringIO

import spark

from com.livinglogic import ul4

from java import lang

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
	# 2) The start location of the construct
	# For ifs:
	# 3) How many if's or elif's we have seen (this is used for simulating elif's via nested if's, for each additional elif, we have one more endif to add)
	# 4) Whether we've already seen the else
	stack = []
	for location in tags:
		try:
			if location.type is None:
				template.opcode(ul4.Opcode.OC_TEXT, location)
			elif location.type == "print":
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_PRINT, r, location)
			elif location.type == "printx":
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_PRINTX, r, location)
			elif location.type == "code":
				parsestmt(template, location)
			elif location.type == "if":
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_IF, r, location)
				stack.append(("if", location, 1, False))
			elif location.type == "elif":
				if not stack or stack[-1][0] != "if":
					raise ul4.BlockException("elif doesn't match any if")
				elif stack[-1][2]:
					raise ul4.BlockException("else already seen in elif")
				template.opcode(ul4.Opcode.OC_ELSE, location)
				r = parseexpr(template, location)
				template.opcode(ul4.Opcode.OC_IF, r, location)
				stack[-1] = ("if", stack[-1][1]+1, stack[-1][2], False)
			elif location.type == "else":
				if not stack or stack[-1][0] != "if":
					raise ul4.BlockException("else doesn't match any if")
				elif stack[-1][3]:
					raise ul4.BlockException("duplicate else")
				template.opcode(ul4.Opcode.OC_ELSE, location)
				stack[-1] = ("if", stack[-1][1], stack[-1][2], True)
			elif location.type == "end":
				if not stack:
					raise ul4.BlockException("not in any block")
				code = location.code
				if code:
					if code == "if":
						if stack[-1][0] != "if":
							raise ul4.BlockException("endif doesn't match any if")
					elif code == "for":
						if stack[-1][0] != "for":
							raise ul4.BlockException("endfor doesn't match any for")
					elif code == "def":
						if stack[-1][0] != "def":
							raise ul4.BlockException("enddef doesn't match any def")
					else:
						raise ul4.BlockException("illegal end value %r" % code)
				last = stack.pop()
				if last[0] == "if":
					for i in xrange(last[2]):
						template.opcode(ul4.Opcode.OC_ENDIF, location)
				elif last[0] == "for":
					template.opcode(ul4.Opcode.OC_ENDFOR, location)
				else: # last[0] == "def":
					template.opcode(ul4.Opcode.OC_ENDDEF, location)
			elif location.type == "for":
				parsefor(template, location)
				stack.append(("for", location))
			elif location.type == "break":
				for entry in stack:
					if entry[0] == "for":
						break
				else:
					raise BlockException("break outside of for loop")
				template.opcode(ul4.Opcode.OC_BREAK, location)
			elif location.type == "continue":
				for entry in stack:
					if entry[0] == "for":
						break
				else:
					raise BlockException("continue outside of for loop")
				template.opcode(ul4.Opcode.OC_CONTINUE, location)
			elif location.type == "render":
				parserender(template, location)
			elif location.type == "def":
				template.opcode(ul4.Opcode.OC_DEF, location.code, location)
				stack.append(("def", location))
			else: # Can't happen
				raise ValueError("unknown tag %r" % location.type)
		except ul4.LocationException, exc:
			raise
		except lang.Exception, exc:
			raise ul4.LocationException(exc, location)
	if stack:
		raise ul4.LocationException(ul4.BlockException("block unclosed"), stack[-1][1])
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
			ast = self.parse(ul4.InterpretedTemplate.tokenizeCode(location))
			registers = ul4.Registers()
			return ast.compile(template, registers, location)
		except ul4.LocationException, exc:
			raise
		except lang.Exception, exc:
			raise ul4.LocationException(exc, location)

	def typestring(self, token):
		return token.getTokenType()

	def error(self, token):
		raise ul4.SyntaxException(token)

	def makeconst(self, start, end, value):
		if value is None:
			return ul4.LoadNone(start, end)
		elif value is True:
			return ul4.LoadTrue(start, end)
		elif value is False:
			return ul4.LoadFalse(start, end)
		elif isinstance(value, int):
			return ul4.LoadInt(start, end, value)
		elif isinstance(value, float):
			return ul4.LoadFloat(start, end, value)
		elif isinstance(value, basestring):
			return ul4.LoadStr(start, end, value)
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
		'expr11 ::= date',
		'expr11 ::= color',
		'expr11 ::= name',
	]

	def expr_emptylist(self, (_0, _1)):
		return ul4.List(_0.start, _1.end)
	expr_emptylist.spark = ['expr11 ::= [ ]']

	def expr_buildlist(self, (_0, expr)):
		list = ul4.List(_0.start, expr.end)
		list.append(expr)
		return list
	expr_buildlist.spark = ['buildlist ::= [ expr0']

	def expr_addlist(self, (list, _0, expr)):
		list.append(expr)
		list.end = expr.end
		return list
	expr_addlist.spark = ['buildlist ::= buildlist , expr0']

	def expr_finishlist(self, (list, _0)):
		list.end = _0.end
		return list
	expr_finishlist.spark = ['expr11 ::= buildlist ]']

	def expr_finishlist1(self, (list, _0, _1)):
		list.end = _1.end
		return list
	expr_finishlist1.spark = ['expr11 ::= buildlist , ]']

	def expr_emptydict(self, (_0, _1)):
		return ul4.Dict(_0.start, _1.end)
	expr_emptydict.spark = ['expr11 ::= { }']

	def expr_builddict(self, (_0, key, _1, value)):
		dict = ul4.Dict(_0.start, value.end)
		dict.append(key, value)
		return dict
	expr_builddict.spark = ['builddict ::= { expr0 : expr0']

	def expr_builddictupdate(self, (_0, _1, value)):
		dict = ul4.Dict(_0.start, value.end)
		dict.append(value)
		return dict
	expr_builddictupdate.spark = ['builddict ::= { ** expr0']

	def expr_adddict(self, (dict, _0, key, _1, value)):
		dict.append(key, value)
		dict.end = value.end
		return dict
	expr_adddict.spark = ['builddict ::= builddict , expr0 : expr0']

	def expr_updatedict(self, (dict, _0, _1, value)):
		dict.append(value)
		dict.end = value.end
		return dict
	expr_updatedict.spark = ['builddict ::= builddict , ** expr0']

	def expr_finishdict(self, (dict, _0)):
		dict.end = _0.end
		return dict
	expr_finishdict.spark = ['expr11 ::= builddict }']

	def expr_finishdict1(self, (dict, _0, _1)):
		dict.end = _1.end
		return dict
	expr_finishdict1.spark = ['expr11 ::= builddict , }']

	def expr_bracket(self, (_0, expr, _1)):
		return expr
	expr_bracket.spark = ['expr11 ::= ( expr0 )']

	def expr_callfunc0(self, (name, _0, _1)):
		return ul4.CallFunc(name.start, _1.end, name)
	expr_callfunc0.spark = ['expr10 ::= name ( )']

	def expr_callfunc1(self, (name, _0, arg0, _1)):
		return ul4.CallFunc(name.start, _1.end, name, arg0)
	expr_callfunc1.spark = ['expr10 ::= name ( expr0 )']

	def expr_callfunc2(self, (name, _0, arg0, _1, arg1, _2)):
		return ul4.CallFunc(name.start, _2.end, name, arg0, arg1)
	expr_callfunc2.spark = ['expr10 ::= name ( expr0 , expr0 )']

	def expr_callfunc3(self, (name, _0, arg0, _1, arg1, _2, arg2, _3)):
		return ul4.CallFunc(name.start, _3.end, name, arg0, arg1, arg2)
	expr_callfunc3.spark = ['expr10 ::= name ( expr0 , expr0 , expr0 )']

	def expr_callfunc4(self, (name, _0, arg0, _1, arg1, _2, arg2, _3, arg3, _4)):
		return ul4.CallFunc(name.start, _4.end, name, arg0, arg1, arg2, arg3)
	expr_callfunc4.spark = ['expr10 ::= name ( expr0 , expr0 , expr0 , expr0 )']

	def expr_getattr(self, (expr, _0, name)):
		return ul4.GetAttr(expr.start, name.end, expr, name)
	expr_getattr.spark = ['expr9 ::= expr9 . name']

	def expr_callmeth0(self, (expr, _0, name, _1, _2)):
		return ul4.CallMeth(expr.start, _2.end, expr, name)
	expr_callmeth0.spark = ['expr9 ::= expr9 . name ( )']

	def expr_callmeth1(self, (expr, _0, name, _1, arg1, _2)):
		return ul4.CallMeth(expr.start, _2.end, expr, name, arg1)
	expr_callmeth1.spark = ['expr9 ::= expr9 . name ( expr0 )']

	def expr_callmeth2(self, (expr, _0, name, _1, arg1, _2, arg2, _3)):
		return ul4.CallMeth(expr.start, _3.end, expr, name, arg1, arg2)
	expr_callmeth2.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 )']

	def expr_callmeth3(self, (expr, _0, name, _1, arg1, _2, arg2, _3, arg3, _4)):
		return ul4.CallMeth(expr.start, _4.end, expr, name, arg1, arg2, arg3)
	expr_callmeth3.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 , expr0 )']

	def methkw_startname(self, (expr, _0, methname, _1, argname, _2, argvalue)):
		call = ul4.CallMethKeywords(expr.start, argvalue.end, methname, expr)
		call.append(argname.value, argvalue)
		return call
	methkw_startname.spark = ['callmethkw ::= expr9 . name ( name = expr0']

	def methkw_startdict(self, (expr, _0, methname, _1, _2, argvalue)):
		call = ul4.CallMethKeywords(expr.start, argvalue.end, methname, expr)
		call.append(argvalue)
		return call
	methkw_startdict.spark = ['callmethkw ::= expr9 . name ( ** expr0']

	def methkw_buildname(self, (call, _0, argname, _1, argvalue)):
		call.args.append(argname.value, argvalue)
		call.end = argvalue.end
		return call
	methkw_buildname.spark = ['callmethkw ::= callmethkw , name = expr0']

	def methkw_builddict(self, (call, _0, _1, argvalue)):
		call.args.append(argvalue)
		call.end = argvalue.end
		return call
	methkw_builddict.spark = ['callmethkw ::= callmethkw , ** expr0']

	def methkw_finish(self, (call, _0)):
		call.end = _0.end
		return call
	methkw_finish.spark = ['expr9 ::= callmethkw )']

	def expr_getitem(self, (expr, _0, key, _1)):
		if isinstance(expr, ul4.LoadConst) and isinstance(key, ul4.LoadConst): # Constant folding
			return self.makeconst(expr.start, _1.end, expr.value[key.value])
		return ul4.GetItem(expr.start, _1.end, expr, key)
	expr_getitem.spark = ['expr9 ::= expr9 [ expr0 ]']

	def expr_getslice12(self, (expr, _0, index1, _1, index2, _2)):
		if isinstance(expr, ul4.LoadConst) and isinstance(index1, ul4.LoadConst) and isinstance(index2, ul4.LoadConst): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[index1.value:index1.value])
		return ul4.GetSlice12(expr.start, _2.end, expr, index1, index2)
	expr_getslice12.spark = ['expr8 ::= expr8 [ expr0 : expr0 ]']

	def expr_getslice1(self, (expr, _0, index1, _1, _2)):
		if isinstance(expr, ul4.LoadConst) and isinstance(index1, ul4.LoadConst): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[index1.value:])
		return ul4.GetSlice1(expr.start, _2.end, expr, index1)
	expr_getslice1.spark = ['expr8 ::= expr8 [ expr0 : ]']

	def expr_getslice2(self, (expr, _0, _1, index2, _2)):
		if isinstance(expr, ul4.LoadConst) and isinstance(index2, ul4.LoadConst): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[:index2.value])
		return ul4.GetSlice2(expr.start, _2.end, expr, index2)
	expr_getslice2.spark = ['expr8 ::= expr8 [ : expr0 ]']

	def expr_neg(self, (_0, expr)):
		if isinstance(expr, ul4.LoadConst): # Constant folding
			return self.makeconst(_0.start, expr.end, -expr.value)
		return ul4.Neg(_0.start, expr.end, expr)
	expr_neg.spark = ['expr7 ::= - expr7']

	def expr_mul(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value * obj2.value)
		return ul4.Mul(obj1.start, obj2.end, obj1, obj2)
	expr_mul.spark = ['expr6 ::= expr6 * expr6']

	def expr_floordiv(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value // obj2.value)
		return ul4.FloorDiv(obj1.start, obj2.end, obj1, obj2)
	expr_floordiv.spark = ['expr6 ::= expr6 // expr6']

	def expr_truediv(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value / obj2.value)
		return ul4.TrueDiv(obj1.start, obj2.end, obj1, obj2)
	expr_truediv.spark = ['expr6 ::= expr6 / expr6']

	def expr_mod(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value % obj2.value)
		return ul4.Mod(obj1.start, obj2.end, obj1, obj2)
	expr_mod.spark = ['expr6 ::= expr6 % expr6']

	def expr_add(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value + obj2.value)
		return ul4.Add(obj1.start, obj2.end, obj1, obj2)
	expr_add.spark = ['expr5 ::= expr5 + expr5']

	def expr_sub(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value - obj2.value)
		return ul4.Sub(obj1.start, obj2.end, obj1, obj2)
	expr_sub.spark = ['expr5 ::= expr5 - expr5']

	def expr_eq(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value == obj2.value)
		return ul4.EQ(obj1.start, obj2.end, obj1, obj2)
	expr_eq.spark = ['expr4 ::= expr4 == expr4']

	def expr_ne(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value != obj2.value)
		return ul4.NE(obj1.start, obj2.end, obj1, obj2)
	expr_ne.spark = ['expr4 ::= expr4 != expr4']

	def expr_lt(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value < obj2.value)
		return ul4.LT(obj1.start, obj2.end, obj1, obj2)
	expr_lt.spark = ['expr4 ::= expr4 < expr4']

	def expr_le(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value <= obj2.value)
		return ul4.LE(obj1.start, obj2.end, obj1, obj2)
	expr_le.spark = ['expr4 ::= expr4 <= expr4']

	def expr_gt(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value > obj2.value)
		return ul4.GT(obj1.start, obj2.end, obj1, obj2)
	expr_gt.spark = ['expr4 ::= expr4 > expr4']

	def expr_ge(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value >= obj2.value)
		return ul4.GE(obj1.start, obj2.end, obj1, obj2)
	expr_ge.spark = ['expr4 ::= expr4 >= expr4']

	def expr_contains(self, (obj, _0, container)):
		if isinstance(obj, ul4.LoadConst) and isinstance(container, ul4.LoadConst): # Constant folding
			return self.makeconst(obj.start, container.end, obj.value in container.value)
		return ul4.Contains(obj.start, container.end, obj, container)
	expr_contains.spark = ['expr3 ::= expr3 in expr3']

	def expr_notcontains(self, (obj, _0, _1, container)):
		if isinstance(obj, ul4.LoadConst) and isinstance(container, ul4.LoadConst): # Constant folding
			return self.makeconst(obj.start, container.end, obj.value not in container.value)
		return ul4.NotContains(obj.start, container.end, obj, container)
	expr_notcontains.spark = ['expr3 ::= expr3 not in expr3']

	def expr_not(self, (_0, expr)):
		if isinstance(expr, ul4.LoadConst): # Constant folding
			return self.makeconst(_0.start, expr.end, not expr.value)
		return ul4.Not(_0.start, expr.end, expr)
	expr_not.spark = ['expr2 ::= not expr2']

	def expr_and(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, bool(obj1.value and obj2.value))
		return ul4.And(obj1.start, obj2.end, obj1, obj2)
	expr_and.spark = ['expr1 ::= expr1 and expr1']

	def expr_or(self, (obj1, _0, obj2)):
		if isinstance(obj1, ul4.LoadConst) and isinstance(obj2, ul4.LoadConst): # Constant folding
			return self.makeconst(obj1.start, obj2.end, bool(obj1.value or obj2.value))
		return ul4.Or(obj1.start, obj2.end, obj1, obj2)
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
		return ul4.For(iter.start, cont.end, iter, cont)
	for0.spark = ['for ::= name in expr0']

	def for1(self, (_0, iter, _1, _2, _3, cont)):
		return ul4.For1(_0.start, cont.end, iter, cont)
	for1.spark = ['for ::= ( name , ) in expr0']

	def for2a(self, (_0, iter1, _1, iter2, _2, _3, cont)):
		return ul4.For2(_0.start, cont.end, iter1, iter2, cont)
	for2a.spark = ['for ::= ( name , name ) in expr0']

	def for2b(self, (_0, iter1, _1, iter2, _2, _3, _4, cont)):
		return ul4.For2(_0.start, cont.end, iter1, iter2, cont)
	for2b.spark = ['for ::= ( name , name , ) in expr0']

	def for3a(self, (_0, iter1, _1, iter2, _2, iter3, _3, _4, cont)):
		return ul4.For3(_0.start, cont.end, iter1, iter2, iter3, cont)
	for3a.spark = ['for ::= ( name , name , name ) in expr0']

	def for3b(self, (_0, iter1, _1, iter2, _2, iter3, _3, _4, _5, cont)):
		return ul4.For3(_0.start, cont.end, iter1, iter2, iter3, cont)
	for3b.spark = ['for ::= ( name , name , name , ) in expr0']


class StmtParser(ExprParser):
	emptyerror = "statement required"

	def __init__(self, start="stmt"):
		ExprParser.__init__(self, start)

	def stmt_assign(self, (name, _0, value)):
		return ul4.StoreVar(name.start, value.end, name, value)
	stmt_assign.spark = ['stmt ::= name = expr0']

	def stmt_iadd(self, (name, _0, value)):
		return ul4.AddVar(name.start, value.end, name, value)
	stmt_iadd.spark = ['stmt ::= name += expr0']

	def stmt_isub(self, (name, _0, value)):
		return ul4.SubVar(name.start, value.end, name, value)
	stmt_isub.spark = ['stmt ::= name -= expr0']

	def stmt_imul(self, (name, _0, value)):
		return ul4.MulVar(name.start, value.end, name, value)
	stmt_imul.spark = ['stmt ::= name *= expr0']

	def stmt_itruediv(self, (name, _0, value)):
		return ul4.TrueDivVar(name.start, value.end, name, value)
	stmt_itruediv.spark = ['stmt ::= name /= expr0']

	def stmt_ifloordiv(self, (name, _0, value)):
		return ul4.FloorDivVar(name.start, value.end, name, value)
	stmt_ifloordiv.spark = ['stmt ::= name //= expr0']

	def stmt_imod(self, (name, _0, value)):
		return ul4.ModVar(name.start, value.end, name, value)
	stmt_imod.spark = ['stmt ::= name %= expr0']

	def stmt_del(self, (_0, name)):
		return ul4.DelVar(_0.start, name.end, name)
	stmt_del.spark = ['stmt ::= del name']


class RenderParser(ExprParser):
	emptyerror = "render statement required"

	def __init__(self, start="render"):
		ExprParser.__init__(self, start)

	def emptyrender(self, (template, _1, _2)):
		return ul4.Render(template.start, _2.end, template)
	emptyrender.spark = ['render ::= expr0 ( )']

	def startrender(self, (template, _1, argname, _2, argexpr)):
		render = ul4.Render(template.start, argexpr.end, template)
		render.append(argname.value, argexpr)
		return render
	startrender.spark = ['buildrender ::= expr0 ( name = expr0 ']

	def startrenderupdate(self, (template, _0, _1, arg)):
		render = ul4.Render(template.start, arg.end, template)
		render.append(arg)
		return render
	startrenderupdate.spark = ['buildrender ::= expr0 ( ** expr0']

	def buildrender(self, (render, _1, argname, _2, argexpr)):
		render.append(argname.value, argexpr)
		render.end = argexpr.end
		return render
	buildrender.spark = ['buildrender ::= buildrender , name = expr0']

	def buildrenderupdate(self, (render, _0, _1, arg)):
		render.append(arg)
		render.end = arg.end
		return render
	buildrenderupdate.spark = ['buildrender ::= buildrender , ** expr0']

	def finishrender(self, (render, _0)):
		render.end = _0.end
		return render
	finishrender.spark = ['render ::= buildrender )']

	def finishrender1(self, (render, _0, _1)):
		render.end = _1.end
		return render
	finishrender1.spark = ['render ::= buildrender , )']


class Compiler(ul4.CompilerType):
	def compile(self, source, tags, startdelim, enddelim):
		template = ul4.InterpretedTemplate()
		template.startdelim = startdelim
		template.enddelim = enddelim
		template.source = source
		_compile(template, tags)
		return template
