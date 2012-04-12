grammar UL4;

options
{
	k=4;
	output=AST;
	language=Java;
}

@header
{
	package com.livinglogic.ul4;

	import java.util.Date;

	import com.livinglogic.ul4.Utils;
	import com.livinglogic.ul4.Color;
	import com.livinglogic.ul4.Render;
	import com.livinglogic.ul4.KeywordArg;
}

@lexer::header
{
	package com.livinglogic.ul4;
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
	: '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
	| '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
	;

fragment
ESC_SEQ
	: '\\' ('a'|'b'|'e'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
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
	: NONE { $node = new LoadNone(); }
	;

true_ returns [AST node]
	: TRUE { $node = new LoadTrue(); }
	;

false_ returns [AST node]
	: FALSE { $node = new LoadFalse(); }
	;

name returns [Name node]
	: NAME { $node = new Name($NAME.text); }
	;

int_ returns [AST node]
	: INT { $node = new LoadInt(Utils.parseUL4Int($INT.text)); }
	;

float_ returns [AST node]
	: FLOAT { $node = new LoadFloat(Double.parseDouble($FLOAT.text)); }
	;

string returns [AST node]
	: STRING { $node = new LoadStr(Utils.unescapeUL4String($STRING.text.substring(1, $STRING.text.length()-1))); }
	;

date returns [AST node]
	: DATE { $node = new LoadDate(Utils.isoparse($DATE.text.substring(2, $DATE.text.length()-1))); }
	;

color returns [AST node]
	: COLOR { $node = new LoadColor(Color.fromrepr($COLOR.text)); }
	;

literal returns [AST node]
	: e_none=none { $node = e_none.node; }
	| e_false=false_ { $node = e_false.node; }
	| e_true=true_ { $node = e_true.node; }
	| e_name=name { $node = e_name.node; }
	| e_int=int_ { $node = e_int.node; }
	| e_float=float_ { $node = e_float.node; }
	| e_string=string { $node = e_string.node; }
	| e_date=date { $node = e_date.node; }
	| e_color=color { $node = e_color.node; }
	;

/* List literals */
list returns [com.livinglogic.ul4.List node]
	:
		'['
		']' { $node = new com.livinglogic.ul4.List(); }
	|
		'[' {$node = new com.livinglogic.ul4.List(); }
		e1=expr1 { $node.append($e1.node); }
		(
			','
			e2=expr1 { $node.append($e2.node); }
		)*
		','?
		']'
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
		'}' { $node = new Dict(); }
	|
		'{' { $node = new Dict(); }
		i1=dictitem { $node.append($i1.node); }
		(
			','
			i2=dictitem { $node.append($i2.node); }
		)*
		','?
		'}'
	;

atom returns [AST node]
	: e_literal=literal { $node = $e_literal.node; }
	| e_list=list { $node = $e_list.node; }
	| e_dict=dict { $node = $e_dict.node; }
	| '(' e_bracket=expr1 ')' { $node = $e_bracket.node; }
	;

/*
expr10 returns [AST node]
	: e1=callfunc { $node = $e1.node; }
	| e2=expr11 { $node = $e2.node; }
	;

callmeth
	: expr9 '.' name '(' expr9 ')'
	| expr9 '.' name '(' expr9 ',' expr9 ')'
	| expr9 '.' name '(' expr9 ',' expr9 ',' expr9 ')'
	;

fragment
namedarg
	: name '=' expr9
	| '**' expr9
	;

callmethkw
	: expr9 '.' name '(' namedarg (',' namedarg)* ','? ')'
	;

*/

/* Function call */
expr10 returns [AST node]
	: a=atom { $node = a.node; }
	| n=name '(' ')' { $node = new CallFunc($n.text); }
	|
		n=name { $node = new CallFunc($n.text); }
		'('
		a1=expr1 { ((CallFunc)$node).append($a1.node); }
		(
			','
			a2=expr1 { ((CallFunc)$node).append($a2.node); }
		)*
		','?
		')'
	;

/* Attribute access, method call, item acces, slice access */
expr9 returns [AST node]
	:
		e1=expr10 { $node = $e1.node; }
		(
			'.'
			n=name { boolean callmeth = false; }
			(
				'(' { callmeth = true; $node = new CallMeth($node, $n.node.getValue()); }
				(
					a1=expr1 { ((CallMeth)$node).append($a1.node); }
					(
						','
						a2=expr1 { ((CallMeth)$node).append($a2.node); }
					)*
					','?
				)?
				')'
			)? { if (!callmeth) $node = new GetAttr($node, $n.node.getValue()); }
		|
			'['
			(
				':' { AST index2 = null; }
				(
					e2=expr1 { index2 = $e2.node; }
				)? { $node = new GetSlice($node, null, index2); }
				| { boolean slice = false; }
				e2=expr1 { AST index1 = $e2.node; AST index2 = null; }
				(
					':' { slice = true; }
					(
						e3=expr1 { index2 = $e3.node; }
					)?
				)? { $node = slice ? new GetSlice($node, index1, index2) : new GetItem($node, index1); }
			)
			']'
		)*
	;

/* Negation */
expr8 returns [AST node]
	:
		{int count = 0; }
		(
			'-' { ++count; }
		)*
		e=expr9 { $node = e.node; while (count-- != 0) { $node = new Neg($node); } }
	;

/* Multiplication, division, modulo */
expr7 returns [AST node]
	:
		e1=expr8 { $node = $e1.node; }
		(
			{ int opcode = -1; }
			(
				'*' { opcode = 0; }
			|
				'/' { opcode = 1; }
			|
				'//' { opcode = 2; }
			|
				'%' { opcode = 3; }
			)
			e2=expr8 { switch (opcode) { case 0: $node = new Mul($node, $e2.node); break; case 1: $node = new TrueDiv($node, $e2.node); break; case 2: $node = new FloorDiv($node, $e2.node); break; case 3: $node = new Mod($node, $e2.node); break; } }
		)*
	;

/* Addition, substraction */
expr6 returns [AST node]
	:
		e1=expr7 { $node = $e1.node; }
		(
			{ boolean add = false; }
			(
				'+' { add = true; }
			|
				'-' { add = false; }
			)
			e2=expr7 { $node = add ? new Add($node, $e2.node) : new Sub($node, $e2.node); }
		)*
	;

/* Comparisons */
expr5 returns [AST node]
	:
		e1=expr6 { $node = $e1.node; }
		(
			{ int opcode = -1; }
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
			e2=expr6 { switch (opcode) { case 0: $node = new EQ($node, $e2.node); break; case 1: $node = new NE($node, $e2.node); break; case 2: $node = new LT($node, $e2.node); break; case 3: $node = new LE($node, $e2.node); break; case 4: $node = new GT($node, $e2.node); break; case 5: $node = new GE($node, $e2.node); break; } }
		)*
	;

/* "in"/"not in" operator */
expr4 returns [AST node]
	:
		e1=expr5 { $node = $e1.node; }
		(
			{ boolean not = false; }
			(
				'not' { not = true; }
			)?
			'in'
			e2=expr5 { $node = not ? new NotContains($node, $e2.node) : new Contains($node, $e2.node); }
		)?
	;

/* Not operator */
expr3 returns [AST node]
	:
		'not'
		e=expr4 { $node = new Not($e.node); }
	|
		e=expr4 { $node = $e.node; }
	;


/* And operator */
expr2 returns [AST node]
	:
		e1=expr3 { $node = $e1.node; }
		(
			'and'
			e2=expr3 { $node = new And($node, $e2.node); }
		)*
	;

/* Or operator */
expr1 returns [AST node]
	:
		e1=expr2 { $node = $e1.node; }
		(
			'or'
			e2=expr2 { $node = new Or($node, $e2.node); }
		)*
	;


/* Additional rules for "for" tag */

for_ returns [AST node]
	:
		n=name
		'in'
		e=expr1 { $node = new For($n.text, $e.node); }
	|
		'('
		n1=name
		','
		')'
		'in'
		e=expr1 { $node = new ForUnpack($e.node); ((ForUnpack)$node).append($n1.text); }
	|
		'(' { $node = new ForUnpack(); }
		n1=name { ((ForUnpack)$node).append($n1.text); }
		(
			','
			n2=name { ((ForUnpack)$node).append($n2.text); }
		)+
		','?
		')'
		'in'
		e=expr1 { ((ForUnpack)$node).setContainer($e.node); }
	;


/* Additional rules for "code" tag */

stmt returns [AST node]
	: n=name '=' e=expr1 { $node = new StoreVar($n.node.getValue(), $e.node); }
	| n=name '+=' e=expr1 { $node = new AddVar($n.node.getValue(), $e.node); }
	| n=name '-=' e=expr1 { $node = new SubVar($n.node.getValue(), $e.node); }
	| n=name '*=' e=expr1 { $node = new MulVar($n.node.getValue(), $e.node); }
	| n=name '/=' e=expr1 { $node = new TrueDivVar($n.node.getValue(), $e.node); }
	| n=name '//=' e=expr1 { $node = new FloorDivVar($n.node.getValue(), $e.node); }
	| n=name '%=' e=expr1 { $node = new ModVar($n.node.getValue(), $e.node); }
	| 'del' n=name { $node = new DelVar($n.node.getValue()); }
	;


/* Additional rules for "render" tag */

fragment
renderarg returns [KeywordArg node]
	: n=name '=' e=expr1 { $node = new KeywordArg($n.text, $e.node); }
	| '**' e=expr1 { $node = new KeywordArg($e.node); }
	;

render returns [Render node]
	:
		t=expr1 { $node = new Render($t.node); }
		'('
		a1=renderarg { $node.append($a1.node); }
		(
			','
			a2=renderarg { $node.append($a2.node); }
		)*
		','?
		')'
	;
