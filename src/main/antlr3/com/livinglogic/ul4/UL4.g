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
	: DIGIT DIGIT ':' DIGIT DIGIT (':' DIGIT DIGIT('.' DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT)?)?;

DATE
	: '@' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' TIME?;

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
	: DATE { $node = new LoadDate(Utils.isoparse($DATE.text.substring(1))); }
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

list returns [com.livinglogic.ul4.List node]
	: '[' ']' { $node = new com.livinglogic.ul4.List(); }
	| '[' {$node = new com.livinglogic.ul4.List(); } e1=expr8 { $node.append($e1.node); } (',' e2=expr8 { $node.append($e2.node); } )* ','? ']'
	;

fragment
dictitem returns [DictItem node]
	: k=expr8 ':' v=expr8 { $node = new DictItem($k.node, $v.node); }
	| '**' d=expr8 { $node = new DictItem($d.node); }
	;

dict returns [Dict node]
	: '{' '}' { $node = new Dict(); }
	| '{' { $node = new Dict(); } i1=dictitem { $node.append($i1.node); } (',' i2=dictitem { $node.append($i2.node); } )* ','? '}'
	;

atom returns [AST node]
	: e_literal=literal { $node = $e_literal.node; }
	| e_list=list { $node = $e_list.node; }
	| e_dict=dict { $node = $e_dict.node; }
	| '(' e_bracket=expr8 ')' { $node = $e_bracket.node; }
	;

/*
callfunc returns [CallFunc node]
	: name '(' ')' { $node = new CallFunc($name.text); }
	| name { $node = new CallFunc($name.text); } '(' a1=expr8 { $node.append($a1.node); } (',' a2=expr8 { $node.append($a2.node); } )* ','? ')'
	;

expr10 returns [AST node]
	: e1=callfunc { $node = $e1.node; }
	| e2=expr11 { $node = $e2.node; }
	;

getattr
	: expr9 '.' name
	;

callmeth
	: expr9 '.' name '(' expr8 ')'
	| expr9 '.' name '(' expr8 ',' expr8 ')'
	| expr9 '.' name '(' expr8 ',' expr8 ',' expr8 ')'
	;

fragment
namedarg
	: name '=' expr8
	| '**' expr8
	;

callmethkw
	: expr9 '.' name '(' namedarg (',' namedarg)* ','? ')'
	;

expr9
	: getattr
	| callmeth
	| callmethkw
	| expr10
	;

getitem
	: expr9 '[' expr8 ']'
	;

getslice
	: expr9 '[' expr8 ':' expr8 ']'
	| expr9 '[' ':' expr8 ']'
	| expr9 '[' expr8 ':' ']'
	;

expr8
	: getitem
	| getslice
	| expr9
	;

*/

/*
expr9 returns [AST node]
	: e1=expr8 { $node = $e1.node; } ('[' ( ':' (e2=expr9)? | e2=expr9 ( ':' ( e3=expr9 )? )?) ']')*
	;
*/

expr8 returns [AST node]
	: '-' e=expr7 { $node = new Unary(Opcode.OC_NEG, $e.node); }
	| e=expr7 { $node = $e.node; }
	;

expr7 returns [AST node]
	: e1=expr6 { $node = $e1.node; } ( { int opcode = -1; } ( '*' { opcode = Opcode.OC_MUL; } | '/' { opcode = Opcode.OC_TRUEDIV; } | '//' { opcode = Opcode.OC_FLOORDIV; } | '%' { opcode = Opcode.OC_MOD; } ) e2=expr6 { $node = new Binary(opcode, $node, $e2.node); } )*
	;

expr6 returns [AST node]
	: e1=expr5 { $node = $e1.node; } ( { int opcode = -1; } ( '+' { opcode = Opcode.OC_ADD; } | '-' { opcode = Opcode.OC_SUB; } ) e2=expr5 { $node = new Binary(opcode, $node, $e2.node); } )*
	;

expr5 returns [AST node]
	: e1=expr4 { $node = $e1.node; } ( { int opcode = -1; } ( '==' { opcode = Opcode.OC_EQ; } | '!=' { opcode = Opcode.OC_NE; } | '<' { opcode = Opcode.OC_LT; } | '<=' { opcode = Opcode.OC_LE; } | '>' { opcode = Opcode.OC_GT; } | '>=' { opcode = Opcode.OC_GE; } ) e2=expr4 { $node = new Binary(opcode, $node, $e2.node); } )*
	;

expr4 returns [AST node]
	: e1=expr3 { $node = $e1.node; } ( { int opcode = Opcode.OC_CONTAINS; } ('not' { opcode = Opcode.OC_NOTCONTAINS; })? 'in' e2=expr3 { $node = new Binary(opcode, $node, $e2.node); } )?
	;

expr3 returns [AST node]
	: 'not' e=expr2 { $node = new Unary(Opcode.OC_NOT, $e.node); }
	| e=expr2 { $node = $e.node; }
	;

expr2 returns [AST node]
	: e1=expr1 { $node = $e1.node; } ( 'and' e2=expr1 { $node = new Binary(Opcode.OC_AND, $node, $e2.node); } )*
	;

expr1 returns [AST node]
	: e1=atom { $node = $e1.node; } ( 'or' e2=atom { $node = new Binary(Opcode.OC_OR, $node, $e2.node); } )*
	;


/* Additional rules for "for" tag */

for_ returns [For node]
	: n=name 'in' e=expr8 { $node = new For($n.text, $e.node); }
	| '(' n1=name ',' ')' 'in' e=expr8 { $node = new For($e.node); $node.append($n1.text); }
	| '(' { $node = new For(); } n1=name { $node.append($n1.text); } (',' n2=name { $node.append($n2.text); } )+ ','? ')' 'in' e=expr8 { $node.setContainer($e.node); }
	;


/* Additional rules for "code" tag */

stmt returns [AST node]
	: n=name '=' e=expr8 { $node = new ChangeVar(Opcode.OC_STOREVAR, $n.node, $e.node); }
	| n=name '+=' e=expr8 { $node = new ChangeVar(Opcode.OC_ADDVAR, $n.node, $e.node); }
	| n=name '-=' e=expr8 { $node = new ChangeVar(Opcode.OC_SUBVAR, $n.node, $e.node); }
	| n=name '*=' e=expr8 { $node = new ChangeVar(Opcode.OC_MULVAR, $n.node, $e.node); }
	| n=name '/=' e=expr8 { $node = new ChangeVar(Opcode.OC_TRUEDIVVAR, $n.node, $e.node); }
	| n=name '//=' e=expr8 { $node = new ChangeVar(Opcode.OC_FLOORDIVVAR, $n.node, $e.node); }
	| n=name '%=' e=expr8 { $node = new ChangeVar(Opcode.OC_MODVAR, $n.node, $e.node); }
	| 'del' n=name { $node = new DelVar($n.node); }
	;


/* Additional rules for "render" tag */

fragment
renderarg returns [KeywordArg node]
	: n=name '=' e=expr8 { $node = new KeywordArg($n.text, $e.node); }
	| '**' e=expr8 { $node = new KeywordArg($e.node); }
	;

render returns [Render node]
	: t=expr8 { $node = new Render($t.node); } '(' a1=renderarg { $node.append($a1.node); } (',' a2=renderarg { $node.append($a2.node); } )* ','? ')'
	;
