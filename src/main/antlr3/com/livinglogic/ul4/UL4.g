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

none returns [AST node]
	: NONE { $node = new Const(location, getStart($NONE), getEnd($NONE), null); }
	;

true_ returns [AST node]
	: TRUE { $node = new Const(location, getStart($TRUE), getEnd($TRUE), true); }
	;

false_ returns [AST node]
	: FALSE { $node = new Const(location, getStart($FALSE), getEnd($FALSE), false); }
	;

int_ returns [AST node]
	: INT { $node = new Const(location, getStart($INT), getEnd($INT), Utils.parseUL4Int($INT.text)); }
	;

float_ returns [AST node]
	: FLOAT { $node = new Const(location, getStart($FLOAT), getEnd($FLOAT), Double.parseDouble($FLOAT.text)); }
	;

string returns [AST node]
	: STRING { $node = new Const(location, getStart($STRING), getEnd($STRING), Utils.unescapeUL4String($STRING.text.substring(1, $STRING.text.length()-1))); }
	| STRING3 { $node = new Const(location, getStart($STRING3), getEnd($STRING3), Utils.unescapeUL4String($STRING3.text.substring(3, $STRING3.text.length()-3))); }
	;

date returns [AST node]
	: DATE { $node = new Const(location, getStart($DATE), getEnd($DATE), Utils.isoparse($DATE.text.substring(2, $DATE.text.length()-1))); }
	;

color returns [AST node]
	: COLOR { $node = new Const(location, getStart($COLOR), getEnd($COLOR), Color.fromrepr($COLOR.text)); }
	;

name returns [Var node]
	: NAME { $node = new Var(location, getStart($NAME), getEnd($NAME), $NAME.text); }
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
list returns [com.livinglogic.ul4.List node]
	:
		open='['
		close=']' { $node = new com.livinglogic.ul4.List(location, getStart($open), getEnd($close)); }
	|
		open='[' {$node = new com.livinglogic.ul4.List(location, getStart($open), -1); }
		e1=expr1 { $node.append($e1.node); }
		(
			','
			e2=expr1 { $node.append($e2.node); }
		)*
		','?
		close=']' { $node.setEnd(getEnd($close)); }
	;

listcomprehension returns [ListComprehension node]
	@init
	{
		AST _condition = null;
	}
	:
		open='['
		item=expr1
		'for'
		n=nestedname
		'in'
		container=expr1
		(
			'if'
			condition=expr1 { _condition = $condition.node; }
		)?
		close=']' { $node = new ListComprehension(location, getStart($open), getEnd($close), $item.node, $n.varname, $container.node, _condition); }
	;

/* Dict literal */
fragment
dictitem returns [DictItem node]
	:
		k=expr1
		':'
		v=expr1 { $node = new DictItemKeyValue($k.node, $v.node); }
	|
		'**'
		d=expr1 { $node = new DictItemDict($d.node); }
	;

dict returns [Dict node]
	:
		open='{'
		close='}' { $node = new Dict(location, getStart($open), getEnd($close)); }
	|
		open='{' { $node = new Dict(location, getStart($open), -1); }
		i1=dictitem { $node.append($i1.node); }
		(
			','
			i2=dictitem { $node.append($i2.node); }
		)*
		','?
		close='}' { $node.setEnd(getEnd($close)); }
	;

dictcomprehension returns [DictComprehension node]
	@init
	{
		AST _condition = null;
	}
	:
		open='{'
		key=expr1
		':'
		value=expr1
		'for'
		n=nestedname
		'in'
		container=expr1
		(
			'if'
			condition=expr1 { _condition = $condition.node; }
		)?
		close='}' { $node = new DictComprehension(location, getStart($open), getEnd($close), $key.node, $value.node, $n.varname, $container.node, _condition); }
	;

generatorexpression returns [GeneratorExpression node]
	@init
	{
		AST _condition = null;
		int _end = -1;
	}
	:
		item=expr1
		'for'
		n=nestedname
		'in'
		container=expr1 { _end = $container.node.getEnd(); }
		(
			'if'
			condition=expr1 { _condition = $condition.node; _end = $condition.node.getEnd(); }
		)? { $node = new GeneratorExpression(location, $item.node.getStart(), _end, $item.node, $n.varname, $container.node, _condition); }
	;

atom returns [AST node]
	: e_literal=literal { $node = $e_literal.node; }
	| e_list=list { $node = $e_list.node; }
	| e_listcomp=listcomprehension { $node = $e_listcomp.node; }
	| e_dict=dict { $node = $e_dict.node; }
	| e_dictcomp=dictcomprehension { $node = $e_dictcomp.node; }
	| open='(' e_genexpr=generatorexpression close=')' { $node = $e_genexpr.node; $node.setStart(getStart($open)); $node.setEnd(getEnd($close)); }
	| open='(' e_bracket=expr1 close=')' { $node = $e_bracket.node; $node.setStart(getStart($open)); $node.setEnd(getEnd($close)); }
	;

/* For variable unpacking in assignments and for loops */
nestedname returns [Object varname]
	:
		n=name { $varname = $n.text; }
	|
		'(' n0=nestedname ',' ')' { $varname = java.util.Arrays.asList($n0.varname); }
	|
		'('
		n1=nestedname
		','
		n2=nestedname { $varname = new ArrayList(2); ((ArrayList)$varname).add($n1.varname); ((ArrayList)$varname).add($n2.varname); }
		(
			','
			n3=nestedname { ((ArrayList)$varname).add($n3.varname); }
		)*
		','?
		')' 
	;

/* Function/method call, attribute access, item access, slice access */
expr9 returns [AST node]
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
			n=name { $node = new GetAttr(location, $e1.node.getStart(), $n.node.getEnd(), $node, $n.text); }
		|
			/* Function/method call */
			'(' { $node = ($node instanceof GetAttr) ? ((GetAttr)$node).makeCallMeth() : new CallFunc(location, $e1.node.getStart(), -1, $node); }
			(
				/* No arguments */
			|
				/* "**" argument only */
				'**' rkwargs=exprarg { ((Callable)$node).setRemainingKeywordArguments($rkwargs.node); }
				','?
			|
				/* "*" argument only (and maybe **) */
				'*' rargs=exprarg { ((Callable)$node).setRemainingArguments($rargs.node); }
				(
					','
					'**' rkwargs=exprarg { ((Callable)$node).setRemainingKeywordArguments($rkwargs.node); }
				)?
				','?
			|
				/* At least one positional argument */
				a1=exprarg { ((Callable)$node).append($a1.node); }
				(
					','
					a2=exprarg { ((Callable)$node).append($a2.node); }
				)*
				(
					','
					an3=name '=' av3=exprarg { ((Callable)$node).append($an3.text, $av3.node); }
				)*
				(
					','
					'*' rargs=exprarg { ((Callable)$node).setRemainingArguments($rargs.node); }
				)?
				(
					','
					'**' rkwargs=exprarg { ((Callable)$node).setRemainingKeywordArguments($rkwargs.node); }
				)?
				','?
			|
				/* Keyword arguments only */
				an1=name '=' av1=exprarg { ((Callable)$node).append($an1.text, $av1.node); }
				(
					','
					an2=name '=' av2=exprarg { ((Callable)$node).append($an2.text, $av2.node); }
				)*
				(
					','
					'*' rargs=exprarg { ((Callable)$node).setRemainingArguments($rargs.node); }
				)?
				(
					','
					'**' rkwargs=exprarg { ((Callable)$node).setRemainingKeywordArguments($rkwargs.node); }
				)?
				','?
			)
			close=')' { $node.setEnd(getEnd($close)); }
		|
			/* Item/slice access */
			'['
			(
				':'
				(
					e2=expr1 { index2 = $e2.node; }
				)? { $node = GetSlice.make(location, $e1.node.getStart(), -1, $node, null, index2); }
			|
				e2=expr1 { index1 = $e2.node; }
				(
					':' { slice = true; }
					(
						e3=expr1 { index2 = $e3.node; }
					)?
				)? { $node = slice ? GetSlice.make(location, $e1.node.getStart(), -1, $node, index1, index2) : GetItem.make(location, $e1.node.getStart(), -1, $node, index1); }
			)
			close=']' { $node.setEnd(getEnd($close)); }
		)*
	;

/* Negation */
expr8 returns [AST node]
	:
		e1=expr9 { $node = $e1.node; }
	|
		minus='-' e2=expr8 { $node = Neg.make(location, getStart($minus), $e2.node.getEnd(), $e2.node); }
	;

/* Multiplication, division, modulo */
expr7 returns [AST node]
	@init
	{
		int opcode = -1;
	}
	:
		e1=expr8 { $node = $e1.node; }
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
			e2=expr8 { switch (opcode) { case 0: $node = Mul.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 1: $node = TrueDiv.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 2: $node = FloorDiv.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 3: $node = Mod.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; } }
		)*
	;

/* Addition, substraction */
expr6 returns [AST node]
	@init
	{
		boolean add = false;
	}
	:
		e1=expr7 { $node = $e1.node; }
		(
			(
				'+' { add = true; }
			|
				'-' { add = false; }
			)
			e2=expr7 { $node = add ? Add.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node) : Sub.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Comparisons */
expr5 returns [AST node]
	@init
	{
		int opcode = -1;
	}
	:
		e1=expr6 { $node = $e1.node; }
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
			e2=expr6 { switch (opcode) { case 0: $node = EQ.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 1: $node = NE.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 2: $node = LT.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 3: $node = LE.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 4: $node = GT.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; case 5: $node = GE.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); break; } }
		)*
	;

/* "in"/"not in" operator */
expr4 returns [AST node]
	@init
	{
		boolean not = false;
	}
	:
		e1=expr5 { $node = $e1.node; }
		(
			
			(
				'not' { not = true; }
			)?
			'in'
			e2=expr5 { $node = not ? NotContains.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node) : Contains.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)?
	;

/* Not operator */
expr3 returns [AST node]
	:
		e1=expr4 { $node = $e1.node; }
	|
		n='not' e2=expr3 { $node = Not.make(location, getStart($n), $e2.node.getEnd(), $e2.node); }
	;


/* And operator */
expr2 returns [AST node]
	:
		e1=expr3 { $node = $e1.node; }
		(
			'and'
			e2=expr3 { $node = And.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

/* Or operator */
expr1 returns [AST node]
	:
		e1=expr2 { $node = $e1.node; }
		(
			'or'
			e2=expr2 { $node = Or.make(location, $node.getStart(), $e2.node.getEnd(), $node, $e2.node); }
		)*
	;

exprarg returns [AST node]
	: ege=generatorexpression { $node = $ege.node; }
	| e1=expr1 { $node = $e1.node; }
	;

expression returns [AST node]
	: ege=generatorexpression EOF { $node = $ege.node; }
	| e=expr1 EOF { $node = $e.node; }
	;


/* Additional rules for "for" tag */

for_ returns [For node]
	:
		n=nestedname
		'in'
		e=expr1 { $node = new For(location, location.getStartCode(), $e.node.getEnd(), $n.varname, $e.node); }
		EOF
	;


/* Additional rules for "code" tag */

stmt returns [AST node]
	: nn=nestedname '=' e=expr1 EOF { $node = new StoreVar(location, location.getStartCode(), $e.node.getEnd(), $nn.varname, $e.node); }
	| n=name '+=' e=expr1 EOF { $node = new AddVar(location, location.getStartCode(), $e.node.getEnd(), $n.text, $e.node); }
	| n=name '-=' e=expr1 EOF { $node = new SubVar(location, location.getStartCode(), $e.node.getEnd(), $n.text, $e.node); }
	| n=name '*=' e=expr1 EOF { $node = new MulVar(location, location.getStartCode(), $e.node.getEnd(), $n.text, $e.node); }
	| n=name '/=' e=expr1 EOF { $node = new TrueDivVar(location, location.getStartCode(), $e.node.getEnd(), $n.text, $e.node); }
	| n=name '//=' e=expr1 EOF { $node = new FloorDivVar(location, location.getStartCode(), $e.node.getEnd(), $n.text, $e.node); }
	| n=name '%=' e=expr1 EOF { $node = new ModVar(location, location.getStartCode(), $e.node.getEnd(), $n.text, $e.node); }
	| e=expression EOF { $node = $e.node; }
	;
