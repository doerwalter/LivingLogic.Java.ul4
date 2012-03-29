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

NONE	:	'None';

TRUE	:	'True';

FALSE	:	'False';

NAME  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

fragment
DIGIT	:	'0'..'9'
	;

fragment
BIN_DIGIT
	:	('0'|'1')
	;
fragment
OCT_DIGIT
	:	'0'..'7'
	;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F')
;

INT :	DIGIT+
	| '0' ('b'|'B') BIN_DIGIT+
	| '0' ('o'|'O') OCT_DIGIT+
	| '0' ('x'|'X') HEX_DIGIT+
    ;

FLOAT
    :   DIGIT+ '.' DIGIT* EXPONENT?
    |   '.' DIGIT+ EXPONENT?
    |   DIGIT+ EXPONENT
    ;

fragment
TIME	:	DIGIT DIGIT ':' DIGIT DIGIT (':' DIGIT DIGIT('.' DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT)?)?;

DATE	:	'@' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' TIME?;

COLOR	:	'#' HEX_DIGIT HEX_DIGIT HEX_DIGIT
	|	'#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	|	'#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	|	'#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    |  '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? DIGIT+ ;

fragment
ESC_SEQ
    :   '\\' ('a'|'b'|'e'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE1_ESC
    |   UNICODE2_ESC
    |   UNICODE4_ESC
    ;

fragment
UNICODE1_ESC
    :   '\\' 'x' HEX_DIGIT HEX_DIGIT
    ;

fragment
UNICODE2_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

fragment
UNICODE4_ESC
    :   '\\' 'U' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

parse	:	atom;

atom
  :  NONE
  |  FALSE
  |  TRUE
  |  NAME
  |  INT
  |  STRING
  |  DATE
  |  COLOR
  ;

expr	:	atom | list;

list	:	'[' (expr ',')* ']'
	|	'[' (expr ',')* expr ']'
	;
