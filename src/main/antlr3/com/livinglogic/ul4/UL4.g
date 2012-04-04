grammar UL4;

options
{
	output=AST;
	language=Java;
	ASTLabelType=CommonTree;
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

INT
	: ('+'|'-')? DIGIT+
	| ('+'|'-')? '0' ('b'|'B') BIN_DIGIT+
	| ('+'|'-')? '0' ('o'|'O') OCT_DIGIT+
	| ('+'|'-')? '0' ('x'|'X') HEX_DIGIT+
	;

fragment
EXPONENT
	: ('e'|'E') ('+'|'-')? DIGIT+
	;

FLOAT
	: ('+'|'-')? DIGIT+ '.' DIGIT* EXPONENT?
	| ('+'|'-')? '.' DIGIT+ EXPONENT?
	| ('+'|'-')? DIGIT+ EXPONENT
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

atom returns [AST node]
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
	| '[' {$node = new com.livinglogic.ul4.List(); } e1=expr0 { $node.append($e1.node); } (',' e2=expr0 { $node.append($e2.node); } )* ','? ']'
	;

fragment
dictitem returns [DictItem node]
	: k=expr0 ':' v=expr0 { $node = new DictItem($k.node, $v.node); }
	| '**' d=expr0 { $node = new DictItem($d.node); }
	;

dict returns [Dict node]
	: '{' '}' { $node = new Dict(); }
	| '{' { $node = new Dict(); } i1=dictitem { $node.append($i1.node); } (',' i2=dictitem { $node.append($i2.node); } )* ','? '}'
	;

expr11 returns [AST node]
	: atom { $node = $atom.node; }
	| list { $node = $list.node; }
	| dict { $node = $dict.node; }
	| '(' expr0 ')' { $node = $expr0.node; }
	;

callfunc returns [CallFunc node]
	: name '(' ')' { $node = new CallFunc($name.text); }
	| name { $node = new CallFunc($name.text); } '(' a1=expr0 { $node.append($a1.node); } (',' a2=expr0 { $node.append($a2.node); } )* ','? ')'
	;

expr10 returns [AST node]
	: e1=callfunc { $node = $e1.node; }
	| e2=expr11 { $node = $e2.node; }
	;

/*
getattr
	: expr9 '.' name
	;

callmeth
	: expr9 '.' name '(' expr0 ')'
	| expr9 '.' name '(' expr0 ',' expr0 ')'
	| expr9 '.' name '(' expr0 ',' expr0 ',' expr0 ')'
	;

fragment
namedarg
	: name '=' expr0
	| '**' expr0
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
	: expr9 '[' expr0 ']'
	;

getslice
	: expr9 '[' expr0 ':' expr0 ']'
	| expr9 '[' ':' expr0 ']'
	| expr9 '[' expr0 ':' ']'
	;

expr8
	: getitem
	| getslice
	| expr9
	;

expr7
	: '-' expr7
	| expr8
	;

expr6
	: expr6 '*' expr6
	| expr6 '/' expr6
	| expr6 '//' expr6
	| expr6 '%' expr6
	| expr7
	;

expr5
	: expr5 '+' expr5
	| expr5 '-' expr5
	| expr6
	;

expr4
	: expr4 '==' expr4
	| expr4 '!=' expr4
	| expr4 '<' expr4
	| expr4 '<=' expr4
	| expr4 '>' expr4
	| expr4 '>=' expr4
	| expr5
	;

expr3
	: expr3 'in' expr3
	| expr3 'not' 'in' expr3
	| expr4
	;

expr2
	: 'not' expr2
	| expr3
	;

expr1
	: expr1 'and' expr1
	| expr2
	;

expr0
	: expr0 'or' expr0
	| expr1
	;
*/

expr0 returns [AST node]
	: e=expr10 {$node = $e.node; }
	;


/* Additional rules for "for" tag */

for_ returns [For node]
	: n=name 'in' e=expr0 { $node = new For($n.text, $e.node); }
	| '(' n1=name ',' ')' 'in' e=expr0 { $node = new For($e.node); $node.append($n1.text); }
	| '(' n1=name ',' n2=name ','? ')' 'in' e=expr0 { $node = new For($e.node); $node.append($n1.text); $node.append($n2.text); }
	| '(' n1=name ',' n2=name ',' n3=name ','? ')' 'in' e=expr0 { $node = new For($e.node); $node.append($n1.text); $node.append($n2.text); $node.append($n3.text); }
	| '(' n1=name ',' n2=name ',' n3=name ',' n4=name ','? ')' 'in' e=expr0 { $node = new For($e.node); $node.append($n1.text); $node.append($n2.text); $node.append($n3.text); $node.append($n4.text); }
	;


/* Additional rules for "code" tag */

stmt returns [AST node]
	: name '=' expr0 { $node = new ChangeVar(Opcode.OC_STOREVAR, $name.node, $expr0.node); }
	| name '+=' expr0 { $node = new ChangeVar(Opcode.OC_ADDVAR, $name.node, $expr0.node); }
	| name '-=' expr0 { $node = new ChangeVar(Opcode.OC_SUBVAR, $name.node, $expr0.node); }
	| name '*=' expr0 { $node = new ChangeVar(Opcode.OC_MULVAR, $name.node, $expr0.node); }
	| name '/=' expr0 { $node = new ChangeVar(Opcode.OC_TRUEDIVVAR, $name.node, $expr0.node); }
	| name '//=' expr0 { $node = new ChangeVar(Opcode.OC_FLOORDIVVAR, $name.node, $expr0.node); }
	| name '%=' expr0 { $node = new ChangeVar(Opcode.OC_MODVAR, $name.node, $expr0.node); }
	| 'del' name { $node = new DelVar($name.node); }
	;


/* Additional rules for "render" tag */

fragment
renderarg returns [KeywordArg node]
	: name '=' expr0 { $node = new KeywordArg($name.text, $expr0.node); }
	| '**' expr0 { $node = new KeywordArg($expr0.node); }
	;

render returns [Render node]
	: t=expr0 { $node = new Render($t.node); } '(' a1=renderarg { $node.append($a1.node); } (',' a2=renderarg { $node.append($a2.node); } )* ','? ')'
	;
