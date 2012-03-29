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
	: DIGIT+
	| '0' ('b'|'B') BIN_DIGIT+
	| '0' ('o'|'O') OCT_DIGIT+
	| '0' ('x'|'X') HEX_DIGIT+
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
EXPONENT
	: ('e'|'E') ('+'|'-')? DIGIT+
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

none
	: NONE
	;

true
	: TRUE
	;

false
	: FALSE
	;

name
	: NAME
	;

int
	: INT
	;

string
	: STRING
	;

date
	: DATE
	;

color
	: COLOR
	;

atom
	: none
	| false
	| true
	| name
	| int
	| string
	| date
	| color
	;

list
	: '[' WS* ']'
	| '[' expr0 (',' expr0)* ','? ']'
	;

fragment
dictitem
	: expr0 ':' expr0
	| '**' expr0
	;

dict
	: '{' '}'
	| '{' dictitem (',' dictitem)* ','? '}'
	;

expr11
	: atom
	| list
	| dict
	| '(' expr0 ')'
	;

callfunc
	: name '(' ')'
	| name '(' expr0 ')'
	| name '(' expr0 ',' expr0 ')'
	| name '(' expr0 ',' expr0 ',' expr0 ')'
	| name '(' expr0 ',' expr0 ',' expr0 ',' expr0 ')'
	;

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

expr10
	: callfunc
	| expr11
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
	| expr1;


/* Additional rules for "for" tag */

for
	: name 'in' expr0
	| '(' name ',' ')' 'in' expr0
	| '(' name ',' name ','? ')' 'in' expr0
	| '(' name ',' name ',' name ','? ')' 'in' expr0
	| '(' name ',' name ',' name ',' name ','? ')' 'in' expr0
	;


/* Additional rules for "code" tag */

stmt
	: name '=' expr0
	| name '+=' expr0
	| name '-=' expr0
	| name '*=' expr0
	| name '/=' expr0
	| name '//=' expr0
	| name '%=' expr0
	| 'del' name
	;


/* Additional rules for "render" tag */

fragment
renderarg
	: name '=' expr0
	| '**' expr0
	;

render
	: name '(' renderarg (',' renderarg)* ','? ')'
	;
