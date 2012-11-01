grammar UL4;

options
{
	language=Java;
	backtrack=true;
}

@header
{
	package com.livinglogic.ul4;

	import java.util.Date;

	import com.livinglogic.ul4.Utils;
	import com.livinglogic.ul4.Color;
	import com.livinglogic.ul4.CallArg;
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
		super(input);
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
		super(input);
		this.location = location;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		String message = getErrorMessage(e, tokenNames) + " (at index " + e.index + ")";
		throw new SyntaxException(message, e);
	}
}

UNDEFINED
	: 'Undefined'
	;

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

undefined returns [AST node]
	: UNDEFINED { $node = new Const(location, Undefined.undefined); }
	;

none returns [AST node]
	: NONE { $node = new Const(location, null); }
	;

true_ returns [AST node]
	: TRUE { $node = new Const(location, true); }
	;

false_ returns [AST node]
	: FALSE { $node = new Const(location, false); }
	;

int_ returns [AST node]
	: INT { $node = new Const(location, Utils.parseUL4Int($INT.text)); }
	;

float_ returns [AST node]
	: FLOAT { $node = new Const(location, Double.parseDouble($FLOAT.text)); }
	;

string returns [AST node]
	: STRING { $node = new Const(location, Utils.unescapeUL4String($STRING.text.substring(1, $STRING.text.length()-1))); }
	;

date returns [AST node]
	: DATE { $node = new Const(location, Utils.isoparse($DATE.text.substring(2, $DATE.text.length()-1))); }
	;

color returns [AST node]
	: COLOR { $node = new Const(location, Color.fromrepr($COLOR.text)); }
	;

name returns [Var node]
	: NAME { $node = new Var(location, $NAME.text); }
	;

literal returns [AST node]
	: e_undefined=undefined { $node = $e_undefined.node; }
	| e_none=none { $node = $e_none.node; }
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
		'['
		']' { $node = new com.livinglogic.ul4.List(location); }
	|
		'[' {$node = new com.livinglogic.ul4.List(location); }
		e1=expr1 { $node.append($e1.node); }
		(
			','
			e2=expr1 { $node.append($e2.node); }
		)*
		','?
		']'
	;

listcomprehension returns [ListComprehension node]
	@init
	{
		AST _condition = null;
	}
	:
		'['
		item=expr1
		'for'
		n=nestedname
		'in'
		container=expr1
		(
			'if'
			condition=expr1 { _condition = $condition.node; }
		)?
		']' { $node = new ListComprehension(location, $item.node, $n.varname, $container.node, _condition); }
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
		'{'
		'}' { $node = new Dict(location); }
	|
		'{' { $node = new Dict(location); }
		i1=dictitem { $node.append($i1.node); }
		(
			','
			i2=dictitem { $node.append($i2.node); }
		)*
		','?
		'}'
	;

dictcomprehension returns [DictComprehension node]
	@init
	{
		AST _condition = null;
	}
	:
		'{'
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
		'}' { $node = new DictComprehension(location, $key.node, $value.node, $n.varname, $container.node, _condition); }
	;

generatorexpression returns [GeneratorExpression node]
	@init
	{
		AST _condition = null;
	}
	:
		item=expr1
		'for'
		n=nestedname
		'in'
		container=expr1
		(
			'if'
			condition=expr1 { _condition = $condition.node; }
		)? { $node = new GeneratorExpression(location, $item.node, $n.varname, $container.node, _condition); }
	;

atom returns [AST node]
	: e_literal=literal { $node = $e_literal.node; }
	| e_list=list { $node = $e_list.node; }
	| e_listcomp=listcomprehension { $node = $e_listcomp.node; }
	| e_dict=dict { $node = $e_dict.node; }
	| e_dictcomp=dictcomprehension { $node = $e_dictcomp.node; }
	| '(' e_genexpr=generatorexpression ')' { $node = $e_genexpr.node; }
	| '(' e_bracket=expr1 ')' { $node = $e_bracket.node; }
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

/* Function call */
expr10 returns [AST node]
	: a=atom { $node = $a.node; }
	| n=name '(' ')' { $node = new CallFunc(location, $n.text); }
	|
		n=name { $node = new CallFunc(location, $n.text); }
		'('
		a1=exprarg { ((CallFunc)$node).append($a1.node); }
		(
			','
			a2=exprarg { ((CallFunc)$node).append($a2.node); }
		)*
		','?
		')'
	;

/* Attribute access, method call, item access, slice access */
fragment
callarg returns [CallArg node]
	:
		n=name
		'='
		e=exprarg { $node = new CallArgNamed($n.text, $e.node); }
	|
		'**'
		e=exprarg { $node = new CallArgDict($e.node); }
	;

expr9 returns [AST node]
	@init
	{
		boolean callmeth = false;
		AST index1 = null;
		AST index2 = null;
		boolean slice = false;
	}
	:
		e1=expr10 { $node = $e1.node; }
		(
			/* Attribute access/function call */
			'.'
			n=name
			(
				/* Function call */
				(
					/* No arguments */
					'('
					')' { callmeth = true; $node = new CallMeth(location, $node, $n.text); }
				|
					/* Positional argument */
					'(' { callmeth = true; $node = new CallMeth(location, $node, $n.text); }
					pa1=exprarg { ((CallMeth)$node).append($pa1.node); }
					(
						','
						pa2=exprarg { ((CallMeth)$node).append($pa2.node); }
					)*
					','?
					')'
				|
					/* Keyword arguments */
					'(' { callmeth = true; $node = new CallMethKeywords(location, $node, $n.text); }
					kwa1=callarg { ((CallMethKeywords)$node).append($kwa1.node); }
					(
						','
						kwa2=callarg { ((CallMethKeywords)$node).append($kwa2.node); }
					)*
					','?
					')'
				)
			)? { if (!callmeth) $node = GetAttr.make(location, $node, $n.text); }
		|
			/* Item/slice access */
			'['
			(
				':'
				(
					e2=expr1 { index2 = $e2.node; }
				)? { $node = GetSlice.make(location, $node, null, index2); }
			|
				e2=expr1 { index1 = $e2.node; }
				(
					':' { slice = true; }
					(
						e3=expr1 { index2 = $e3.node; }
					)?
				)? { $node = slice ? GetSlice.make(location, $node, index1, index2) : GetItem.make(location, $node, index1); }
			)
			']'
		)*
	;

/* Negation */
expr8 returns [AST node]
	@init
	{
		int count = 0;
	}
	:
		(
			'-' { ++count; }
		)*
		e=expr9 { $node = $e.node; while (count-- != 0) { $node = Neg.make(location, $node); } }
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
			e2=expr8 { switch (opcode) { case 0: $node = Mul.make(location, $node, $e2.node); break; case 1: $node = TrueDiv.make(location, $node, $e2.node); break; case 2: $node = FloorDiv.make(location, $node, $e2.node); break; case 3: $node = Mod.make(location, $node, $e2.node); break; } }
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
			e2=expr7 { $node = add ? Add.make(location, $node, $e2.node) : Sub.make(location, $node, $e2.node); }
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
			e2=expr6 { switch (opcode) { case 0: $node = EQ.make(location, $node, $e2.node); break; case 1: $node = NE.make(location, $node, $e2.node); break; case 2: $node = LT.make(location, $node, $e2.node); break; case 3: $node = LE.make(location, $node, $e2.node); break; case 4: $node = GT.make(location, $node, $e2.node); break; case 5: $node = GE.make(location, $node, $e2.node); break; } }
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
			e2=expr5 { $node = not ? NotContains.make(location, $node, $e2.node) : Contains.make(location, $node, $e2.node); }
		)?
	;

/* Not operator */
expr3 returns [AST node]
	:
		'not'
		e=expr4 { $node = Not.make(location, $e.node); }
	|
		e=expr4 { $node = $e.node; }
	;


/* And operator */
expr2 returns [AST node]
	:
		e1=expr3 { $node = $e1.node; }
		(
			'and'
			e2=expr3 { $node = And.make(location, $node, $e2.node); }
		)*
	;

/* Or operator */
expr1 returns [AST node]
	:
		e1=expr2 { $node = $e1.node; }
		(
			'or'
			e2=expr2 { $node = Or.make(location, $node, $e2.node); }
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
		e=expr1 { $node = new For(location, $n.varname, $e.node); }
		EOF
	;


/* Additional rules for "code" tag */

stmt returns [AST node]
	: nn=nestedname '=' e=expr1 EOF { $node = new StoreVar(location, $nn.varname, $e.node); }
	| n=name '+=' e=expr1 EOF { $node = new AddVar(location, $n.text, $e.node); }
	| n=name '-=' e=expr1 EOF { $node = new SubVar(location, $n.text, $e.node); }
	| n=name '*=' e=expr1 EOF { $node = new MulVar(location, $n.text, $e.node); }
	| n=name '/=' e=expr1 EOF { $node = new TrueDivVar(location, $n.text, $e.node); }
	| n=name '//=' e=expr1 EOF { $node = new FloorDivVar(location, $n.text, $e.node); }
	| n=name '%=' e=expr1 EOF { $node = new ModVar(location, $n.text, $e.node); }
	;
