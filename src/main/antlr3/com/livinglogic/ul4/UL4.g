grammar UL4;

options
{
	language=Java;
	backtrack=true;
	TokenLabelType=CommonToken;
}

@header
{
	package com.livinglogic.ul4;

	import java.util.Date;

	import com.livinglogic.ul4.Utils;
	import com.livinglogic.ul4.Color;
}

@lexer::header
{
	package com.livinglogic.ul4;
}


@lexer::members
{
	private Location location;

	public UL4Lexer(Location location, CharStream input)
	{
		this(input);
		this.location = location;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		String message = getErrorMessage(e, tokenNames) + " (at index " + e.index + ")";
		throw new SyntaxException(message, e);
	}
}

@parser::members
{
	private Location location;

	public UL4Parser(Location location, TokenStream input)
	{
		this(input);
		this.location = location;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		String message = getErrorMessage(e, tokenNames) + " (at index " + e.index + ")";
		throw new SyntaxException(message, e);
	}

	public int getStart(CommonToken token)
	{
		return location.startcode + token.getStartIndex();
	}

	public int getEnd(CommonToken token)
	{
		return location.startcode + token.getStopIndex() + 1;
	}
}

NONE
	: 'None'
	;

TRUE
	: 'True'
	;

FALSE
	: 'False'
	;

NAME
	: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
	;

fragment
DIGIT
	: '0'..'9'
	;

fragment
BIN_DIGIT
	: ('0'|'1')
	;

fragment
OCT_DIGIT
	: '0'..'7'
	;

fragment
HEX_DIGIT
	: ('0'..'9'|'a'..'f'|'A'..'F')
	;

/* We don't have negative ints (as this would lex "1-2" wrong) */
INT
	: DIGIT+
	| '0' ('b'|'B') BIN_DIGIT+
	| '0' ('o'|'O') OCT_DIGIT+
	| '0' ('x'|'X') HEX_DIGIT+
	;

fragment
EXPONENT
	: ('e'|'E') ('+'|'-')? DIGIT+
	;

FLOAT
	: DIGIT+ '.' DIGIT* EXPONENT?
	| '.' DIGIT+ EXPONENT?
	| DIGIT+ EXPONENT
	;

fragment
TIME
	: DIGIT DIGIT ':' DIGIT DIGIT ( ':' DIGIT DIGIT ( '.' DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT)?)?;

DATE
	: '@' '(' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT ('T' TIME?)? ')';

COLOR
	: '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT
	| '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	| '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	| '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

WS
	: (' '|'\t'|'\r'|'\n') { $channel=HIDDEN; }
	;

STRING
	: '"' ( ESC_SEQ | ~('\\'|'"'|'\r'|'\n') )* '"'
	| '\'' ( ESC_SEQ | ~('\\'|'\''|'\r'|'\n') )* '\''
	;

STRING3
	: '"""' (options {greedy=false;}:TRIQUOTE)* '"""'
	|  '\'\'\'' (options {greedy=false;}:TRIAPOS)* '\'\'\''
	;

fragment
TRIQUOTE
	: ('"'|'""')? (ESC_SEQ|~('\\'|'"'))+
	;

fragment
TRIAPOS
	: ('\''|'\'\'')? (ESC_SEQ|~('\\'|'\''))+
	;

fragment
ESC_SEQ
	: '\\' ('a'|'b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
	| UNICODE1_ESC
	| UNICODE2_ESC
	| UNICODE4_ESC
	;

fragment
UNICODE1_ESC
	: '\\' 'x' HEX_DIGIT HEX_DIGIT
	;

fragment
UNICODE2_ESC
	: '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

fragment
UNICODE4_ESC
	: '\\' 'U' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;


/* Rules common to all tags */

none returns [ConstAST node]
	: NONE { $node = new ConstAST(location, getStart($NONE), getEnd($NONE), null); }
	;

true_ returns [ConstAST node]
	: TRUE { $node = new ConstAST(location, getStart($TRUE), getEnd($TRUE), true); }
	;

false_ returns [ConstAST node]
	: FALSE { $node = new ConstAST(location, getStart($FALSE), getEnd($FALSE), false); }
	;

int_ returns [ConstAST node]
	: INT { $node = new ConstAST(location, getStart($INT), getEnd($INT), Utils.parseUL4Int($INT.text)); }
	;

float_ returns [ConstAST node]
	: FLOAT { $node = new ConstAST(location, getStart($FLOAT), getEnd($FLOAT), Double.parseDouble($FLOAT.text)); }
	;

string returns [ConstAST node]
	: STRING { $node = new ConstAST(location, getStart($STRING), getEnd($STRING), Utils.unescapeUL4String($STRING.text.substring(1, $STRING.text.length()-1))); }
	| STRING3 { $node = new ConstAST(location, getStart($STRING3), getEnd($STRING3), Utils.unescapeUL4String($STRING3.text.substring(3, $STRING3.text.length()-3))); }
	;

date returns [ConstAST node]
	: DATE { $node = new ConstAST(location, getStart($DATE), getEnd($DATE), Utils.isoparse($DATE.text.substring(2, $DATE.text.length()-1))); }
	;

color returns [ConstAST node]
	: COLOR { $node = new ConstAST(location, getStart($COLOR), getEnd($COLOR), Color.fromrepr($COLOR.text)); }
	;

name returns [VarAST node]
	: NAME { $node = new VarAST(location, getStart($NAME), getEnd($NAME), $NAME.text); }
	;

literal returns [AST node]
	: e_none=none { $node = $e_none.node; }
	| e_false=false_ { $node = $e_false.node; }
	| e_true=true_ { $node = $e_true.node; }
	| e_int=int_ { $node = $e_int.node; }
	| e_float=float_ { $node = $e_float.node; }
	| e_string=string { $node = $e_string.node; }
	| e_date=date { $node = $e_date.node; }
	| e_color=color { $node = $e_color.node; }
	| e_name=name { $node = $e_name.node; }
	;

/* List literals */
list returns [ListAST node]
	:
		open='['
		close=']' { $node = new ListAST(location, getStart($open), getEnd($close)); }
	|
		open='[' {$node = new ListAST(location, getStart($open), -1); }
		e1=expr_if { $node.append($e1.node); }
		(
			','
			e2=expr_if { $node.append($e2.node); }
		)*
		','?
		close=']' { $node.setEnd(getEnd($close)); }
	;

listcomprehension returns [ListComprehensionAST node]
	@init
	{
		AST _condition = null;
	}
	:
		open='['
		item=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if
		(
			'if'
			condition=expr_if { _condition = $condition.node; }
		)?
		close=']' { $node = new ListComprehensionAST(location, getStart($open), getEnd($close), $item.node, $n.lvalue, $container.node, _condition); }
	;

/* Dict literal */
fragment
dictitem returns [DictItem node]
	:
		k=expr_if
		':'
		v=expr_if { $node = new DictItemKeyValue($k.node, $v.node); }
	|
		'**'
		d=expr_if { $node = new DictItemDict($d.node); }
	;

dict returns [DictAST node]
	:
		open='{'
		close='}' { $node = new DictAST(location, getStart($open), getEnd($close)); }
	|
		open='{' { $node = new DictAST(location, getStart($open), -1); }
		i1=dictitem { $node.append($i1.node); }
		(
			','
			i2=dictitem { $node.append($i2.node); }
		)*
		','?
		close='}' { $node.setEnd(getEnd($close)); }
	;

dictcomprehension returns [DictComprehensionAST node]
	@init
	{
		AST _condition = null;
	}
	:
		open='{'
		key=expr_if
		':'
		value=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if
		(
			'if'
			condition=expr_if { _condition = $condition.node; }
		)?
		close='}' { $node = new DictComprehensionAST(location, getStart($open), getEnd($close), $key.node, $value.node, $n.lvalue, $container.node, _condition); }
	;

generatorexpression returns [GeneratorExpressionAST node]
	@init
	{
		AST _condition = null;
		int _end = -1;
	}
	:
		item=expr_if
		'for'
		n=nestedlvalue
		'in'
		container=expr_if { _end = $container.node.getEnd(); }
		(
			'if'
			condition=expr_if { _condition = $condition.node; _end = $condition.node.getEnd(); }
		)? { $node = new GeneratorExpressionAST(location, $item.node.getStart(), _end, $item.node, $n.lvalue, $container.node, _condition); }
	;

atom returns [AST node]
	: e_literal=literal { $node = $e_literal.node; }
	| e_list=list { $node = $e_list.node; }
	| e_listcomp=listcomprehension { $node = $e_listcomp.node; }
	| e_dict=dict { $node = $e_dict.node; }
	| e_dictcomp=dictcomprehension { $node = $e_dictcomp.node; }
	| open='(' e_genexpr=generatorexpression close=')' { $node = $e_genexpr.node; $node.setStart(getStart($open)); $node.setEnd(getEnd($close)); }
	| open='(' e_bracket=expr_if close=')' { $node = $e_bracket.node; $node.setStart(getStart($open)); $node.setEnd(getEnd($close)); }
	;

/* For variable unpacking in assignments and for loops */
nestedlvalue returns [Object lvalue]
	:
		n=expr_subscript { $lvalue = $n.node; }
	|
		'(' n0=nestedlvalue ',' ')' { $lvalue = java.util.Arrays.asList($n0.lvalue); }
	|
		'('
		n1=nestedlvalue
		','
		n2=nestedlvalue { $lvalue = new ArrayList(2); ((ArrayList)$lvalue).add($n1.lvalue); ((ArrayList)$lvalue).add($n2.lvalue); }
		(
			','
			n3=nestedlvalue { ((ArrayList)$lvalue).add($n3.lvalue); }
		)*
		','?
		')'
	;


/* Slice/item expression */
index returns [AST node]
	@init
	{
		AST startIndex = null;
		AST stopIndex = null;
		int endPos = -1;
		boolean slice = false;
	}
	:
		colon=':' { endPos = getEnd($colon); }
		(
			e=expr_if { stopIndex = $e.node; endPos = $e.node.getEnd(); }
		)? { $node = new SliceAST(location, getStart($colon), endPos, null, stopIndex); }
	|
		e=expr_if { startIndex = $e.node; endPos = $e.node.getEnd(); }
		(
			colon=':' { slice = true; endPos = getEnd($colon); }
			(
				e2=expr_if { stopIndex = $e2.node; endPos = $e2.node.getEnd(); }
			)?
		)? { $node = slice ? new SliceAST(location, $e.node.getStart(), endPos, startIndex, stopIndex) : $e.node; }
	;

/* Function/method call, attribute access, item access, slice access */
expr_subscript returns [AST node]
	@init
	{
		AST index1 = null;
		AST index2 = null;
		boolean slice = false;
	}
	:
		e1=atom { $node = $e1.node; }
		(
			/* Attribute access */
			'.'
			n=name { $node = new AttrAST(location, $e1.node.getStart(), $n.node.getEnd(), $node, $n.text); }
		|
			/* Function/method call */
			'(' { $node = new CallAST(location, $e1.node.getStart(), -1, $node); }
			(
				/* No arguments */
			|
				/* "**" argument only */
				'**' rkwargs=exprarg { ((CallAST)$node).setRemainingKeywordArguments($rkwargs.node); }
				','?
			|
				/* "*" argument only (and maybe **) */
				'*' rargs=exprarg { ((CallAST)$node).setRemainingArguments($rargs.node); }
				(
					','
					'**' rkwargs=exprarg { ((CallAST)$node).setRemainingKeywordArguments($rkwargs.node); }
				)?
				','?
			|
				/* At least one positional argument */
				a1=exprarg { ((CallAST)$node).append($a1.node); }
				(
					','
					a2=exprarg { ((CallAST)$node).append($a2.node); }
				)*
				(
					','
					an3=name '=' av3=exprarg { ((CallAST)$node).append($an3.text, $av3.node); }
				)*
				(
					','
					'*' rargs=exprarg { ((CallAST)$node).setRemainingArguments($rargs.node); }
				)?
				(
					','
					'**' rkwargs=exprarg { ((CallAST)$node).setRemainingKeywordArguments($rkwargs.node); }
				)?
				','?
			|
				/* Keyword arguments only */
				an1=name '=' av1=exprarg { ((CallAST)$node).append($an1.text, $av1.node); }
				(
					','
					an2=name '=' av2=exprarg { ((CallAST)$node).append($an2.text, $av2.node); }
				)*
				(
					','
					'*' rargs=exprarg { ((CallAST)$node).setRemainingArguments($rargs.node); }
				)?
				(
					','
					'**' rkwargs=exprarg { ((CallAST)$node).setRemainingKeywordArguments($rkwargs.node); }
				)?
				','?
			)
			close=')' { $node.setEnd(getEnd($close)); }
		|
			/* Item/slice access */
			'['
			e2=index { $node = ItemAST.make(location, $e1.node.getStart(), -1, $node, $e2.node); }
			close=']' { $node.setEnd(getEnd($close)); }
		)*
	;

/* Negation/bitwise not */
expr_unary returns [AST node]
	:
		e1=expr_subscript { $node = $e1.node; }
	|
		minus='-' e2=expr_unary { $node = NegAST.make(location, getStart($minus), $e2.node.getEnd(), $e2.node); }
	|
		bitnot='~' e2=expr_unary { $node = BitNotAST.make(location, getStart($bitnot), $e2.node.getEnd(), $e2.node); }
	|
		n='not' e2=expr_unary { $node = NotAST.make(location, getStart($n), $e2.node.getEnd(), $e2.node); }
	;

/* Multiplication, division, modulo */
expr_mul returns [AST node]
	@init
	{
		int opcode = -1;
	}
	:
		e1=expr_unary { $node = $e1.node; }
		(
			(
				'*' { opcode = 0; }
			|
				'/' { opcode = 1; }
			|
				'//' { opcode = 2; }
			|
				'%' { opcode = 3; }
			)
			e2=expr_unary { switch (opcode) { case 0: $node = MulAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 1: $node = TrueDivAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 2: $node = FloorDivAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 3: $node = ModAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; } }
		)*
	;

/* Addition, substraction */
expr_add returns [AST node]
	@init
	{
		boolean add = false;
	}
	:
		e1=expr_mul { $node = $e1.node; }
		(
			(
				'+' { add = true; }
			|
				'-' { add = false; }
			)
			e2=expr_mul { $node = add ? AddAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node) : SubAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Binary shift */
expr_bitshift returns [AST node]
	@init
	{
		boolean left = false;
	}
	:
		e1=expr_add { $node = $e1.node; }
		(
			(
				'<<' { left = true; }
			|
				'>>' { left = false; }
			)
			e2=expr_add { $node = left ? ShiftLeftAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node) : ShiftRightAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Bitwise and */
expr_bitand returns [AST node]
	:
		e1=expr_bitshift { $node = $e1.node; }
		(
			'&'
			e2=expr_bitshift { $node = BitAndAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Bitwise exclusive or */
expr_bitxor returns [AST node]
	:
		e1=expr_bitand { $node = $e1.node; }
		(
			'^'
			e2=expr_bitand { $node = BitXOrAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Bitwise or */
expr_bitor returns [AST node]
	:
		e1=expr_bitxor { $node = $e1.node; }
		(
			'|'
			e2=expr_bitxor { $node = BitOrAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Comparisons */
expr_cmp returns [AST node]
	@init
	{
		int opcode = -1;
	}
	:
		e1=expr_bitor { $node = $e1.node; }
		(
			(
				'==' { opcode = 0; }
			|
				'!=' { opcode = 1; }
			|
				'<' { opcode = 2; }
			|
				'<=' { opcode = 3; }
			|
				'>' { opcode = 4; }
			|
				'>=' { opcode = 5; }
			)
			e2=expr_bitor { switch (opcode) { case 0: $node = EQAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 1: $node = NEAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 2: $node = LTAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 3: $node = LEAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 4: $node = GTAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 5: $node = GEAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; } }
		)*
	;

/* "in"/"not in" operator */
expr_contain returns [AST node]
	@init
	{
		boolean not = false;
	}
	:
		e1=expr_cmp { $node = $e1.node; }
		(
			(
				'not' { not = true; }
			)?
			'in'
			e2=expr_cmp { $node = not ? NotContainsAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node) : ContainsAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)?
	;

/* And operator */
expr_and returns [AST node]
	:
		e1=expr_contain { $node = $e1.node; }
		(
			'and'
			e2=expr_contain { $node = AndAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Or operator */
expr_or returns [AST node]
	:
		e1=expr_and { $node = $e1.node; }
		(
			'or'
			e2=expr_and { $node = OrAST.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* If expression operator */
expr_if returns [AST node]
	:
		e1=expr_or { $node = $e1.node; }
		(
			'if'
			e2=expr_or
			'else'
			e3=expr_or { $node = IfAST.make(location, $e1.node.getStart(), $e3.node.getEnd(), $e1.node, $e2.node, $e3.node); }
		)?
	;

exprarg returns [AST node]
	: ege=generatorexpression { $node = $ege.node; }
	| e1=expr_if { $node = $e1.node; }
	;

expression returns [AST node]
	: ege=generatorexpression EOF { $node = $ege.node; }
	| e=expr_if EOF { $node = $e.node; }
	;


/* Additional rules for "for" tag */

for_ returns [ForBlockAST node]
	:
		n=nestedlvalue
		'in'
		e=expr_if { $node = new ForBlockAST(location, location.getStartCode(), $e.node.getEnd(), $n.lvalue, $e.node); }
		EOF
	;


/* Additional rules for "code" tag */

stmt returns [AST node]
	: nn=nestedlvalue '=' e=expr_if EOF { $node = new SetVarAST(location, location.getStartCode(), $e.node.getEnd(), $nn.lvalue, $e.node); }
	| n=expr_subscript '+=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new AddVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '-=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new SubVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '*=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new MulVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '//=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new FloorDivVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '/=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new TrueDivVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '%=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new ModVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '<<=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new ShiftLeftVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '>>=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new ShiftRightVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '&=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new BitAndVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '^=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new BitXOrVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| n=expr_subscript '|=' e=expr_if EOF { if ($n.node instanceof LValue) $node = new BitOrVarAST(location, location.getStartCode(), $e.node.getEnd(), (LValue)$n.node, $e.node); else throw new RuntimeException("lvalue required"); }
	| e=expression EOF { $node = $e.node; }
	;
