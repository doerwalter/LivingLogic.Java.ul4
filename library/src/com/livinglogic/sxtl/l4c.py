# -*- coding: utf-8 -*-

## Copyright 2008 by LivingLogic AG, Bayreuth/Germany
## Copyright 2008 by Walter Dörwald
##
## All Rights Reserved
##
## See ll/__init__.py for the license


import com.livinglogic.sxtl.L4CompilerType as L4CompilerType
import com.livinglogic.sxtl.Template as L4Template
Location = L4Template.Location
Opcode = L4Template.Opcode

from com.livinglogic.sxtl import Registers

import sys, re, StringIO

import spark


###
### Exceptions
###

class Error(Exception):
	"""
	base class of all exceptions.
	"""
	def __init__(self, exception=None):
		self.location = None
		self.exception = exception

	def __str__(self):
		if self.exception is not None:
			return self.format(str(self.exception))
		else:
			return self.format("error")

	def decorate(self, location):
		self.location = location
		return self

	def format(self, message):
		if self.exception is not None:
			name = self.exception.__class__.__name__
			module = self.exception.__class__.__module__
			if module != "exceptions":
				name = "%s.%s" % (module, name)
			if self.location is not None:
				return "%s in %s: %s" % (name, self.location, message)
			else:
				return "%s: %s" % (name, message)
		else:
			if self.location is not None:
				return "in %s: %s" % (self.location, message)
			else:
				return message


class LexicalError(Error):
	def __init__(self, input):
		Error.__init__(self)
		self.input = input

	def __str__(self):
		return self.format("Unmatched input %r" % self.input)


class SyntaxError(Error):
	def __init__(self, token):
		Error.__init__(self)
		self.token = token

	def __str__(self):
		return self.format("Lexical error near %r" % str(self.token))


class UnterminatedStringError(Error):
	"""
	Exception that is raised when a string constant is not terminated.
	"""
	def __str__(self):
		return self.format("Unterminated string")


class BlockError(Error):
	"""
	Exception that is raised when an illegal block structure is detected (e.g.
	an ``endif`` without a previous ``if``).
	"""

	def __init__(self, message):
		Error.__init__(self)
		self.message = message

	def __str__(self):
		return self.format(self.message)


class UnknownFunctionError(Error):
	"""
	Exception that is raised the function to be executed by the ``callfunc0``,
	``callfunc1`` or ``callfunc2`` opcodes is unknown to the renderer.
	"""

	def __init__(self, funcname):
		Error.__init__(self)
		self.funcname = funcname

	def __str__(self):
		return self.format("function %r unknown" % self.funcname)


class UnknownMethodError(Error):
	"""
	Exception that is raised the method to be executed by the ``callmeth0``,
	``callmeth1``, ``callmeth2``  or ``callmeth3`` opcodes is unknown to the
	renderer.
	"""

	def __init__(self, methname):
		Error.__init__(self)
		self.methname = methname

	def __str__(self):
		return self.format("method %r unknown" % self.methname)


class UnknownOpcodeError(Error):
	"""
	Exception that is raised when an unknown opcode is encountered.
	"""

	def __init__(self, opcode):
		Error.__init__(self)
		self.opcode = opcode

	def __str__(self):
		return self.format("opcode %r unknown" % self.opcode)


class OutOfRegistersError(Error):
	"""
	Exception that is raised when there are no more free registers
	(can't happen)
	"""

	def __str__(self):
		return self.format("out of registers")


###
### helper functions for compiling
###

def _tokenize(source, startdelim, enddelim):
	tokens = []
	pattern = "%s(print|code|for|if|elif|else|end|render)(\s*((.|\\n)*?)\s*)?%s" % (re.escape(startdelim), re.escape(enddelim))
	pattern = re.compile(pattern)
	pos = 0
	while True:
		match = pattern.search(source, pos)
		if match is None:
			break
		if match.start() != pos:
			tokens.append(Location(source, None, pos, match.start(), pos, match.start()))
		tokens.append(Location(source, source[match.start(1):match.end(1)], match.start(), match.end(), match.start(3), match.end(3)))
		pos = match.end()
	end = len(source)
	if pos != end:
		tokens.append(Location(source, None, pos, end, pos, end))
	return tokens


def _compile(template, source, startdelim, enddelim):
	opcodes = []
	scanner = Scanner()
	parseexpr = ExprParser(scanner).compile
	parsestmt = StmtParser(scanner).compile
	parsefor = ForParser(scanner).compile
	parserender = RenderParser(scanner).compile

	# This stack stores for each nested for/foritem/if/elif/else the following information:
	# 1) Which construct we're in (i.e. "if" or "for")
	# For ifs:
	# 2) How many if's or elif's we have seen (this is used for simulating elif's via nested if's, for each additional elif, we have one more endif to add)
	# 3) Whether we've already seen the else
	stack = []
	for location in _tokenize(source, startdelim, enddelim):
		try:
			if location.type is None:
				template.opcode(None, location)
			elif location.type == "print":
				r = parseexpr(template, location)
				template.opcode("print", r, location)
			elif location.type == "code":
				parsestmt(template, location)
			elif location.type == "if":
				r = parseexpr(template, location)
				template.opcode("if", r, location)
				stack.append(("if", 1, False))
			elif location.type == "elif":
				if not stack or stack[-1][0] != "if":
					raise BlockError("elif doesn't match any if")
				elif stack[-1][2]:
					raise BlockError("else already seen in elif")
				template.opcode("else", location)
				r = parseexpr(template, location)
				template.opcode("if", r, location)
				stack[-1] = ("if", stack[-1][1]+1, False)
			elif location.type == "else":
				if not stack or stack[-1][0] != "if":
					raise BlockError("else doesn't match any if")
				elif stack[-1][2]:
					raise BlockError("duplicate else")
				template.opcode("else", location)
				stack[-1] = ("if", stack[-1][1], True)
			elif location.type == "end":
				if not stack:
					raise BlockError("not in any block")
				code = location.code
				if code:
					if code == "if":
						if stack[-1][0] != "if":
							raise BlockError("endif doesn't match any if")
					elif code == "for":
						if stack[-1][0] != "for":
							raise BlockError("endfor doesn't match any for")
					else:
						raise BlockError("illegal end value %r" % code)
				last = stack.pop()
				if last[0] == "if":
					for i in xrange(last[1]):
						template.opcode("endif", location)
				else: # last[0] == "for":
					template.opcode("endfor", location)
			elif location.type == "for":
				parsefor(template, location)
				stack.append(("for",))
			elif location.type == "render":
				parserender(template, location)
			else: # Can't happen
				raise ValueError("unknown tag %r" % location.type)
		except Error, exc:
			exc.decorate(location)
			raise
		except Exception, exc:
			raise Error(exc).decorate(location)
	if stack:
		raise BlockError("unclosed blocks")
	return opcodes


###
### Tokens and nodes for the AST
###

class Token(object):
	def __init__(self, start, end, type):
		self.start = start
		self.end = end
		self.type = type

	def __repr__(self):
		return "%s(%r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.type)

	def __str__(self):
		return self.type

	def getType(self):
		return self.type


class AST(object):
	def __init__(self, start, end):
		self.start = start
		self.end = end

	def getType(self):
		return self.type


from com.livinglogic.sxtl import Const, None as None_, True as True_, False as False_, Int, Float, Str, Name, GetSlice, Not, Neg


class For(AST):
	def __init__(self, start, end, iter, cont):
		AST.__init__(self, start, end)
		self.iter = iter
		self.cont = cont

	def __repr__(self):
		return "%s(%r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.iter, self.cont)

	def compile(self, template, registers, location):
		rc = self.cont.compile(template, registers, location)
		ri = registers.alloc()
		template.opcode("for", ri, rc, location)
		if isinstance(self.iter, list):
			for (i, iter) in enumerate(self.iter):
				rii = registers.alloc()
				template.opcode("loadint", rii, str(i), location)
				template.opcode("getitem", rii, ri, rii, location)
				template.opcode("storevar", rii, iter.value, location)
				registers.free(rii)
		else:
			template.opcode("storevar", ri, self.iter.value, location)
		registers.free(ri)
		registers.free(rc)


class GetAttr(AST):
	def __init__(self, start, end, obj, attr):
		AST.__init__(self, start, end)
		self.obj = obj
		self.attr = attr

	def __repr__(self):
		return "%s(%r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.obj, self.attr)

	def compile(self, template, registers, location):
		r = self.obj.compile(template, registers, location)
		template.opcode("getattr", r, r, self.attr.value, location)
		return r


class GetSlice12(AST):
	def __init__(self, start, end, obj, index1, index2):
		AST.__init__(self, start, end)
		self.obj = obj
		self.index1 = index1
		self.index2 = index2

	def __repr__(self):
		return "%s(%r, %r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.obj, self.index1, self.index2)

	def compile(self, template, registers, location):
		r1 = self.obj.compile(template, registers, location)
		r2 = self.index1.compile(template, registers, location)
		r3 = self.index2.compile(template, registers, location)
		template.opcode("getslice12", r1, r1, r2, r3, location)
		registers.free(r2)
		registers.free(r3)
		return r1


class Binary(AST):
	opcode = None

	def __init__(self, start, end, obj1, obj2):
		AST.__init__(self, start, end)
		self.obj1 = obj1
		self.obj2 = obj2

	def __repr__(self):
		return "%s(%r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.obj1, self.obj2)

	def compile(self, template, registers, location):
		r1 = self.obj1.compile(template, registers, location)
		r2 = self.obj2.compile(template, registers, location)
		template.opcode(self.opcode, r1, r1, r2, location)
		registers.free(r2)
		return r1


class GetItem(Binary):
	opcode = "getitem"


class GetSlice1(Binary):
	opcode = "getslice1"


class GetSlice2(Binary):
	opcode = "getslice2"


class Equal(Binary):
	opcode = "equals"


class NotEqual(Binary):
	opcode = "notequals"


class Contains(Binary):
	opcode = "contains"


class NotContains(Binary):
	opcode = "notcontains"


class Add(Binary):
	opcode = "add"


class Sub(Binary):
	opcode = "sub"


class Mul(Binary):
	opcode = "mul"


class FloorDiv(Binary):
	opcode = "floordiv"


class TrueDiv(Binary):
	opcode = "truediv"


class Or(Binary):
	opcode = "or"


class And(Binary):
	opcode = "and"


class Mod(Binary):
	opcode = "mod"


class ChangeVar(AST):
	opcode = None

	def __init__(self, start, end, name, value):
		AST.__init__(self, start, end)
		self.name = name
		self.value = value

	def __repr__(self):
		return "%s(%r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.name, self.value)

	def compile(self, template, registers, location):
		r = self.value.compile(template, registers, location)
		template.opcode(self.opcode, r, self.name.value, location)
		registers.free(r)


class StoreVar(ChangeVar):
	opcode = "storevar"


class AddVar(ChangeVar):
	opcode = "addvar"


class SubVar(ChangeVar):
	opcode = "subvar"


class MulVar(ChangeVar):
	opcode = "mulvar"


class TrueDivVar(ChangeVar):
	opcode = "truedivvar"


class FloorDivVar(ChangeVar):
	opcode = "floordivvar"


class ModVar(ChangeVar):
	opcode = "modvar"


class DelVar(AST):
	def __init__(self, start, end, name):
		AST.__init__(self, start, end)
		self.name = name

	def __repr__(self):
		return "%s(%r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.name)

	def compile(self, template, registers, location):
		template.opcode("delvar", self.name.value, location)


class CallFunc(AST):
	def __init__(self, start, end, name, args):
		AST.__init__(self, start, end)
		self.name = name
		self.args = args

	def __repr__(self):
		if self.args:
			return "%s(%r, %r, %r, %s)" % (self.__class__.__name__, self.start, self.end, self.name, repr(self.args)[1:-1])
		else:
			return "%s(%r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.name)

	def compile(self, template, registers, location):
		if len(self.args) == 0:
			r = registers.alloc()
			template.opcode("callfunc0", r, self.name.name, location)
			return r
		elif len(self.args) == 1:
			r0 = self.args[0].compile(template, registers, location)
			template.opcode("callfunc1", r0, r0, self.name.value, location)
			return r0
		elif len(self.args) == 2:
			r0 = self.args[0].compile(template, registers, location)
			r1 = self.args[1].compile(template, registers, location)
			template.opcode("callfunc2", r0, r0, r1, self.name.value, location)
			registers.free(r1)
			return r0
		else:
			raise ValueError("%d arguments not supported" % len(self.args))


class CallMeth(AST):
	def __init__(self, start, end, name, obj, args):
		AST.__init__(self, start, end)
		self.name = name
		self.obj = obj
		self.args = args

	def __repr__(self):
		if self.args:
			return "%s(%r, %r, %r, %r, %s)" % (self.__class__.__name__, self.start, self.end, self.name, self.obj, repr(self.args)[1:-1])
		else:
			return "%s(%r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.name, self.obj)

	def compile(self, template, registers, location):
		if len(self.args) == 0:
			r = self.obj.compile(template, registers, location)
			template.opcode("callmeth0", r, r, self.name.value, location)
			return r
		elif len(self.args) == 1:
			r = self.obj.compile(template, registers, location)
			r0 = self.args[0].compile(template, registers, location)
			template.opcode("callmeth1", r, r, r0, self.name.value, location)
			registers.free(r0)
			return r
		elif len(self.args) == 2:
			r = self.obj.compile(template, registers, location)
			r0 = self.args[0].compile(template, registers, location)
			r1 = self.args[1].compile(template, registers, location)
			template.opcode("callmeth2", r, r, r0, r1, self.name.value, location)
			registers.free(r0)
			registers.free(r1)
			return r
		elif len(self.args) == 3:
			r = self.obj.compile(template, registers, location)
			r0 = self.args[0].compile(template, registers, location)
			r1 = self.args[1].compile(template, registers, location)
			r2 = self.args[2].compile(template, registers, location)
			template.opcode("callmeth3", r, r, r0, r1, r2, self.name.value, location)
			registers.free(r0)
			registers.free(r1)
			registers.free(r2)
			return r
		else:
			raise ValueError("%d arguments not supported" % len(self.args))


class Render(AST):
	def __init__(self, start, end, name, value):
		AST.__init__(self, start, end)
		self.name = name
		self.value = value

	def __repr__(self):
		return "%s(%r, %r, %r, %r)" % (self.__class__.__name__, self.start, self.end, self.name, self.value)

	def compile(self, template, registers, location):
		r = self.value.compile(template, registers, location)
		template.opcode("render", r, self.name.value, location)
		registers.free(r)


###
### Tokenizer
###

class Scanner(spark.GenericScanner):
	def __init__(self):
		spark.GenericScanner.__init__(self, re.UNICODE)
		self.collectstr = []

	def tokenize(self, location):
		self.rv = []
		self.start = 0
		try:
			spark.GenericScanner.tokenize(self, location.code)
			if self.mode != "default":
				raise UnterminatedStringError()
		except Error, exc:
			exc.decorate(location)
			raise
		except Exception, exc:
			raise Error(exc).decorate(location)
		return self.rv

	def token(self, start, end, s):
		self.rv.append(Token(start, end, s))
	token.spark = {"default": ["\\(|\\)|\\[|\\]|\\.|,|==|\\!=|=|\\+=|\\-=|\\*=|/=|//=|%=|%|:|\\+|-|\\*|//|/"]}

	def none(self, start, end, s):
		self.rv.append(None_(start, end))
	none.spark = {"default": ["None"]}

	def true(self, start, end, s):
		self.rv.append(True_(start, end))
	true.spark = {"default": ["True"]}

	def false(self, start, end, s):
		self.rv.append(False_(start, end))
	false.spark = {"default": ["False"]}

	def name(self, start, end, s):
		if s in ("in", "not", "or", "and", "del"):
			self.rv.append(Token(start, end, s))
		else:
			self.rv.append(Name(start, end, s))
	name.spark = {"default": ["[a-zA-Z_][\\w]*"]}

	# We don't have negatve numbers, this is handled by constant folding in the AST for unary minus
	def float(self, start, end, s):
		self.rv.append(Float(start, end, float(s)))
	float.spark = {"default": ["\\d+(\\.\\d*)?[eE][+-]?\\d+", "\\d+\\.\\d*([eE][+-]?\\d+)?"]}

	def hexint(self, start, end, s):
		self.rv.append(Int(start, end, int(s[2:], 16)))
	hexint.spark = {"default": ["0[xX][\\da-fA-F]+"]}

	def octint(self, start, end, s):
		self.rv.append(Int(start, end, int(s[2:], 8)))
	octint.spark = {"default": ["0[oO][0-7]+"]}

	def binint(self, start, end, s):
		self.rv.append(Int(start, end, int(s[2:], 2)))
	binint.spark = {"default": ["0[bB][01]+"]}

	def int(self, start, end, s):
		self.rv.append(Int(start, end, int(s)))
	int.spark = {"default": ["\\d+"]}

	def beginstr1(self, start, end, s):
		self.mode = "str1"
		self.start = start
	beginstr1.spark = {"default": ["'"]}

	def beginstr2(self, start, end, s):
		self.mode = "str2"
		self.start = start
	beginstr2.spark = {"default": ['"']}

	def endstr(self, start, end, s):
		self.rv.append(Str(self.start, end, "".join(self.collectstr)))
		self.collectstr = []
		self.mode = "default"
	endstr.spark = {"str1": ["'"], "str2": ['"']}

	def whitespace(self, start, end, s):
		pass
	whitespace.spark = {"default": ["\\s+"]}

	def escapedbackslash(self, start, end, s):
		self.collectstr.append("\\")
	escapedbackslash.spark = {"str1": ["\\\\\\\\"], "str2": ["\\\\\\\\"]}

	def escapedapos(self, start, end, s):
		self.collectstr.append("'")
	escapedapos.spark = {"str1": ["\\\\'"], "str2": ["\\\\'"]}

	def escapedquot(self, start, end, s):
		self.collectstr.append('"')
	escapedapos.spark = {"str1": ['\\\\"'], "str2": ['\\\\"']}

	def escapedbell(self, start, end, s):
		self.collectstr.append("\a")
	escapedbell.spark = {"str1": ["\\\\a"], "str2": ["\\\\a"]}

	def escapedbackspace(self, start, end, s):
		self.collectstr.append("\b")
	escapedbackspace.spark = {"str1": ["\\\\b"], "str2": ["\\\\b"]}

	def escapedformfeed(self, start, end, s):
		self.collectstr.append("\f")
	escapedformfeed.spark = {"str1": ["\\\\f"], "str2": ["\\\\f"]}

	def escapedlinefeed(self, start, end, s):
		self.collectstr.append("\n")
	escapedlinefeed.spark = {"str1": ["\\\\n"], "str2": ["\\\\n"]}

	def escapedcarriagereturn(self, start, end, s):
		self.collectstr.append("\r")
	escapedcarriagereturn.spark = {"str1": ["\\\\r"], "str2": ["\\\\r"]}

	def escapedtab(self, start, end, s):
		self.collectstr.append("\t")
	escapedtab.spark = {"str1": ["\\\\t"], "str2": ["\\\\t"]}

	def escapedverticaltab(self, start, end, s):
		self.collectstr.append("\v")
	escapedverticaltab.spark = {"str1": ["\\\\v"], "str2": ["\\\\v"]}

	def escapedescape(self, start, end, s):
		self.collectstr.append("\x1b")
	escapedescape.spark = {"str1": ["\\\\e"], "str2": ["\\\\e"]}

	def escaped8bitchar(self, start, end, s):
		self.collectstr.append(unichr(int(s[2:], 16)))
	escaped8bitchar.spark = {"str1": ["\\\\x[0-9a-fA-F]{2}"], "str2": ["\\\\x[0-9a-fA-F]{2}"]}

	def escaped16bitchar(self, start, end, s):
		self.collectstr.append(unichr(int(s[2:], 16)))
	escaped16bitchar.spark = {"str1": ["\\\\u[0-9a-fA-F]{4}"], "str2": ["\\\\u[0-9a-fA-F]{4}"]}

	def text(self, start, end, s):
		self.collectstr.append(s)
	text.spark = {"str1": [".|\\n"], "str2": [".|\\n"]}

	def default(self, start, end, s):
		raise LexicalError(start, end, s)
	default.spark = {"default": ["(.|\\n)+"], "str1": ["(.|\\n)+"], "str2": ["(.|\\n)+"]}

	def error(self, start, end, s):
		raise LexicalError(start, end, s)


###
### Parsers for different types of code
###

class ExprParser(spark.GenericParser):
	emptyerror = "expression required"

	def __init__(self, scanner, start="expr0"):
		spark.GenericParser.__init__(self, start)
		self.scanner = scanner

	def compile(self, template, location):
		if not location.code:
			raise ValueError(self.emptyerror)
		try:
			ast = self.parse(self.scanner.tokenize(location))
			registers = Registers()
			return ast.compile(template, registers, location)
		except Error, exc:
			exc.decorate(location)
			raise
		except Exception, exc:
			raise Error(exc).decorate(location)

	def typestring(self, token):
		return token.getType()

	def error(self, token):
		raise SyntaxError(token)

	def makeconst(self, start, end, value):
		if value is None:
			return None_(start, end)
		elif value is True:
			return True_(start, end)
		elif value is False:
			return False_(start, end)
		elif isinstance(value, int):
			return Int(start, end, value)
		elif isinstance(value, float):
			return Float(start, end, value)
		elif isinstance(value, basestring):
			return Str(start, end, value)
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
		return CallFunc(name.start, _1.end, name, [])
	expr_callfunc0.spark = ['expr10 ::= name ( )']

	def expr_callfunc1(self, (name, _0, arg0, _1)):
		return CallFunc(name.start, _1.end, name, [arg0])
	expr_callfunc1.spark = ['expr10 ::= name ( expr0 )']

	def expr_callfunc2(self, (name, _0, arg0, _1, arg1, _2)):
		return CallFunc(name.start, _2.end, name, [arg0, arg1])
	expr_callfunc2.spark = ['expr10 ::= name ( expr0 , expr0 )']

	def expr_callfunc3(self, (name, _0, arg0, _1, arg1, _2, arg2, _3)):
		return CallFunc(name.start, _3.end, name, [arg0, arg1, arg2])
	expr_callfunc3.spark = ['expr10 ::= name ( expr0 , expr0 , expr0 )']

	def expr_getattr(self, (expr, _0, name)):
		return GetAttr(expr.start, name.end, expr, name)
	expr_getattr.spark = ['expr9 ::= expr9 . name']

	def expr_callmeth0(self, (expr, _0, name, _1, _2)):
		return CallMeth(expr.start, _2.end, name, expr, [])
	expr_callmeth0.spark = ['expr9 ::= expr9 . name ( )']

	def expr_callmeth1(self, (expr, _0, name, _1, arg1, _2)):
		return CallMeth(expr.start, _2.end, name, expr, [arg1])
	expr_callmeth1.spark = ['expr9 ::= expr9 . name ( expr0 )']

	def expr_callmeth2(self, (expr, _0, name, _1, arg1, _2, arg2, _3)):
		return CallMeth(expr.start, _3.end, name, expr, [arg1, arg2])
	expr_callmeth2.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 )']

	def expr_callmeth3(self, (expr, _0, name, _1, arg1, _2, arg2, _3, arg3, _4)):
		return CallMeth(expr.start, _4.end, name, expr, [arg1, arg2, arg3])
	expr_callmeth3.spark = ['expr9 ::= expr9 . name ( expr0 , expr0 , expr0 )']

	def expr_getitem(self, (expr, _0, key, _1)):
		if isinstance(expr, Const) and isinstance(key, Const): # Constant folding
			return self.makeconst(expr.start, _1.end, expr.value[key.value])
		return GetItem(expr.start, _1.end, expr, key)
	expr_getitem.spark = ['expr8 ::= expr8 [ expr0 ]']

	def expr_getslice12(self, (expr, _0, index1, _1, index2, _2)):
		if isinstance(expr, Const) and isinstance(index1, Const) and isinstance(index2, Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[index1.value:index1.value])
		return GetSlice12(expr.start, _2.end, expr, index1, index2)
	expr_getslice12.spark = ['expr8 ::= expr8 [ expr0 : expr0 ]']

	def expr_getslice1(self, (expr, _0, index1, _1, _2)):
		if isinstance(expr, Const) and isinstance(index1, Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[index1.value:])
		return GetSlice1(expr.start, _2.end, expr, index1)
	expr_getslice1.spark = ['expr8 ::= expr8 [ expr0 : ]']

	def expr_getslice2(self, (expr, _0, _1, index2, _2)):
		if isinstance(expr, Const) and isinstance(index2, Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[:index2.value])
		return GetSlice2(expr.start, _2.end, expr, index2)
	expr_getslice2.spark = ['expr8 ::= expr8 [ : expr0 ]']

	def expr_getslice(self, (expr, _0, _1, _2)):
		if isinstance(expr, Const): # Constant folding
			return self.makeconst(expr.start, _2.end, expr.value[:])
		return GetSlice(expr.start, _2.end, expr)
	expr_getslice.spark = ['expr8 ::= expr8 [ : ]']

	def expr_neg(self, (_0, expr)):
		if isinstance(expr, Const): # Constant folding
			return self.makeconst(_0.start, expr.end, -expr.value)
		return Neg(_0.start, expr.end, expr)
	expr_neg.spark = ['expr7 ::= - expr7']

	def expr_mul(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value * obj2.value)
		return Mul(obj1.start, obj2.end, obj1, obj2)
	expr_mul.spark = ['expr6 ::= expr6 * expr6']

	def expr_floordiv(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value // obj2.value)
		return FloorDiv(obj1.start, obj2.end, obj1, obj2)
	expr_floordiv.spark = ['expr6 ::= expr6 // expr6']

	def expr_truediv(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value / obj2.value)
		return TrueDiv(obj1.start, obj2.end, obj1, obj2)
	expr_truediv.spark = ['expr6 ::= expr6 / expr6']

	def expr_mod(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value % obj2.value)
		return Mod(obj1.start, obj2.end, obj1, obj2)
	expr_mod.spark = ['expr6 ::= expr6 % expr6']

	def expr_add(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value + obj2.value)
		return Add(obj1.start, obj2.end, obj1, obj2)
	expr_add.spark = ['expr5 ::= expr5 + expr5']

	def expr_sub(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value - obj2.value)
		return Sub(obj1.start, obj2.end, obj1, obj2)
	expr_sub.spark = ['expr5 ::= expr5 - expr5']

	def expr_equal(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value == obj2.value)
		return Equal(obj1.start, obj2.end, obj1, obj2)
	expr_equal.spark = ['expr4 ::= expr4 == expr4']

	def expr_notequal(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, obj1.value != obj2.value)
		return NotEqual(obj1.start, obj2.end, obj1, obj2)
	expr_notequal.spark = ['expr4 ::= expr4 != expr4']

	def expr_contains(self, (obj, _0, container)):
		if isinstance(obj, Const) and isinstance(container, Const): # Constant folding
			return self.makeconst(obj.start, container.end, obj.value in container.value)
		return Contains(obj.start, container.end, obj, container)
	expr_contains.spark = ['expr3 ::= expr3 in expr3']

	def expr_notcontains(self, (obj, _0, _1, container)):
		if isinstance(obj, Const) and isinstance(container, Const): # Constant folding
			return self.makeconst(obj.start, container.end, obj.value not in container.value)
		return NotContains(obj.start, container.end, obj, container)
	expr_notcontains.spark = ['expr3 ::= expr3 not in expr3']

	def expr_not(self, (_0, expr)):
		if isinstance(expr, Const): # Constant folding
			return self.makeconst(_0.start, expr.end, not expr.value)
		return Not(_0.start, expr.end, expr)
	expr_not.spark = ['expr2 ::= not expr2']

	def expr_and(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, bool(obj1.value and obj2.value))
		return And(obj1.start, obj2.end, obj1, obj2)
	expr_and.spark = ['expr1 ::= expr1 and expr1']

	def expr_or(self, (obj1, _0, obj2)):
		if isinstance(obj1, Const) and isinstance(obj2, Const): # Constant folding
			return self.makeconst(obj1.start, obj2.end, bool(obj1.value or obj2.value))
		return Or(obj1.start, obj2.end, obj1, obj2)
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

	def __init__(self, scanner, start="for"):
		ExprParser.__init__(self, scanner, start)

	def for0(self, (iter, _0, cont)):
		return For(iter.start, cont.end, iter, cont)
	for0.spark = ['for ::= name in expr0']

	def for1(self, (_0, iter, _1, _2, _3, cont)):
		return For(_0.start, cont.end, [iter], cont)
	for1.spark = ['for ::= ( name , ) in expr0']

	def for2a(self, (_0, iter1, _1, iter2, _2, _3, cont)):
		return For(_0.start, cont.end, [iter1, iter2], cont)
	for2a.spark = ['for ::= ( name , name ) in expr0']

	def for2b(self, (_0, iter1, _1, iter2, _2, _3, _4, cont)):
		return For(_0.start, cont.end, [iter1, iter2], cont)
	for2a.spark = ['for ::= ( name , name , ) in expr0']


class StmtParser(ExprParser):
	emptyerror = "statement required"

	def __init__(self, scanner, start="stmt"):
		ExprParser.__init__(self, scanner, start)

	def stmt_assign(self, (name, _0, value)):
		return StoreVar(name.start, value.end, name, value)
	stmt_assign.spark = ['stmt ::= name = expr0']

	def stmt_iadd(self, (name, _0, value)):
		return AddVar(name.start, value.end, name, value)
	stmt_iadd.spark = ['stmt ::= name += expr0']

	def stmt_isub(self, (name, _0, value)):
		return SubVar(name.start, value.end, name, value)
	stmt_isub.spark = ['stmt ::= name -= expr0']

	def stmt_imul(self, (name, _0, value)):
		return MulVar(name.start, value.end, name, value)
	stmt_imul.spark = ['stmt ::= name *= expr0']

	def stmt_itruediv(self, (name, _0, value)):
		return TrueDivVar(name.start, value.end, name, value)
	stmt_itruediv.spark = ['stmt ::= name /= expr0']

	def stmt_ifloordiv(self, (name, _0, value)):
		return FloorDivVar(name.start, value.end, name, value)
	stmt_ifloordiv.spark = ['stmt ::= name //= expr0']

	def stmt_imod(self, (name, _0, value)):
		return ModVar(name.start, value.end, name, value)
	stmt_imod.spark = ['stmt ::= name %= expr0']

	def stmt_del(self, (_0, name)):
		return DelVar(_0.start, name.end, name)
	stmt_del.spark = ['stmt ::= del name']


class RenderParser(ExprParser):
	emptyerror = "render statement required"

	def __init__(self, scanner, start="render"):
		ExprParser.__init__(self, scanner, start)

	def render(self, (name, _1, expr, _2)):
		return Render(name.start, _2.end, name, expr)
	render.spark = ['render ::= name ( expr0 )']


class Compiler(L4CompilerType):
	def compile(self, source, startdelim, enddelim):
		template = L4Template()
		template.source = source
		_compile(template, source, startdelim, enddelim)
		return template
