/**
 * jetg
 * Copyright 2015-2016 David Yu
 *
 * Copyright 2010-2013 Guoqiang Chen. All rights reserved.
 * Email: subchen@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
lexer grammar JetTemplateLexer;

/*
@header {
package com.dyuproject.jetg.parser;
}
*/

// *******************************************************************
// ------- DEFAULT mode for Plain Text -------------------------------
COMMENT_LINE            : '##' ~[\r\n]* NEWLINE            -> skip ;
COMMENT_BLOCK           : '#--' .*? '--#'                  -> skip ;
fragment NEWLINE        : ('\r'? '\n' | EOF)              ;

TEXT_PLAIN              : ~('«'|'»'|'#'|'\\'|'\n')+       ;
VALUE_CLOSE             : '»'                             ;
TEXT_NEWLINE            : [\r]?[\n]                       ;
TEXT_CDATA              : '#[[' .*? ']]#'                 ;
TEXT_ESCAPED_CHAR       : ('\\#'|'\\«'|'\\»'|'\\\\')      ;
TEXT_ESCAPED_NEWLINE    : '\\n'                           ;
TEXT_SINGLE_BACKSLASH   : '\\'                            ;
TEXT_SINGLE_HASH        : '#'                             ;


// doT style conditionals
V_ELSE                  : '«else»'                        ;
V_ENDIF                 : '«endif»'                       ;
V_ENDFOR                : '«endfor»'                      ;
V_EMIT                  : '«#emit»'                       ;
V_END                   : '«#»'                           ;

V_ELSEIF                : '«elseif('                      -> pushMode(INSIDE) ;
V_IF                    : '«if('                          -> pushMode(INSIDE) ;
V_FOR                   : '«for('                         -> pushMode(INSIDE) ;

VALUE_ESCAPED_OPEN      : '«;'                            -> pushMode(INSIDE) ;
VALUE_OPEN              : '«'                             -> pushMode(INSIDE) ;

DIRECTIVE_OPEN_IF       : '#if'       ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_ELSEIF   : '#elseif'   ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_FOR      : '#for'      ARGUMENT_START      -> pushMode(INSIDE) ;

// controls
DIRECTIVE_OPEN_BREAK    : '#break'    ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_CONTINUE : '#continue' ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_STOP     : '#stop'     ARGUMENT_START      -> pushMode(INSIDE) ;

// context dependent
DIRECTIVE_OPEN_DEFINE   : '#define'   ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_SET      : '#set'      ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_PUT      : '#put'      ARGUMENT_START      -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_INCLUDE  : '#include'  ARGUMENT_START      -> pushMode(INSIDE) ;

DIRECTIVE_OPEN_CALL     : '#call'     [ \t]+ ID ARGUMENT_START -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_TAG      : '#tag'      [ \t]+ ID ARGUMENT_START -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_MACRO    : '#macro'    [ \t]+ ID ARGUMENT_START -> pushMode(INSIDE) ;
DIRECTIVE_OPEN_PROC     : '#'                ID ARGUMENT_START -> pushMode(INSIDE) ;

fragment ID             : [_a-zA-Z$][_a-zA-Z$0-9]*        ;
fragment ARGUMENT_START : [ \t]* '('                      ;

DIRECTIVE_IF            : '#if'                           ;
DIRECTIVE_ELSEIF        : '#elseif'                       ;
DIRECTIVE_FOR           : '#for'                          ;

DIRECTIVE_BREAK         : '#break'                        ;
DIRECTIVE_CONTINUE      : '#continue'                     ;
DIRECTIVE_STOP          : '#stop'                         ;

DIRECTIVE_ELSE          : '#else'     '()'?               ;
DIRECTIVE_END           : '#end'      '()'?               ;
DIRECTIVE_ENDIF         : '#endif'    '()'?               ;
DIRECTIVE_ENDFOR        : '#endfor'   '()'?               ;

// context dependent
DIRECTIVE_DEFINE        : '#define'                       ;
DIRECTIVE_SET           : '#set'                          ;
DIRECTIVE_PUT           : '#put'                          ;
DIRECTIVE_INCLUDE       : '#include'                      ;

DIRECTIVE_CALL          : '#call'                         ;
DIRECTIVE_TAG           : '#tag'                          ;
DIRECTIVE_MACRO         : '#macro'                        ;

// It is a text which like a directive.
// It must be put after directive defination to avoid confliction.
TEXT_DIRECTIVE_LIKE     : '#' [a-zA-Z0-9]+                ;

// option mode
mode OPTIONS;

O_WS                    : [ \t\r\n]+                       -> skip ;
O_KEY                   : ID                               ;
O_ASSIGN                : '='                              -> type(OP_ASSIGN), popMode ;

// *******************************************************************
// -------- INSIDE mode for directive --------------------------------
mode INSIDE;

V_CLOSE                 : '»'                              -> type(VALUE_CLOSE), popMode ;
V_OPEN                  : '«'                              -> type(VALUE_OPEN), popMode;

WHITESPACE              : [ \t\r\n]+                       -> skip ;

SEMI_COLON              : ';'                              -> pushMode(OPTIONS);

LEFT_PAREN              : '('                              -> pushMode(INSIDE) ;
RETURN_TYPE_START       : ')::'                            -> popMode ;
BLOCK_IGNORE_NEWLINE    : ')%%'                            -> popMode ;
RIGHT_PAREN             : ')'                              -> popMode ;
LEFT_BRACKET            : '['                              ;
RIGHT_BRACKET           : ']'                              ;
LEFT_BRACE              : '{'                              -> pushMode(INSIDE) ;
RIGHT_BRACE             : '}'                              -> popMode ;

OP_ASSIGN               : '='                              ;

OP_DOT_INVOCATION       : '.'                              ;
OP_DOT_INVOCATION_SAFE  : '?.'                             ;

OP_EQUALITY_EQ          : '=='                             ;
OP_EQUALITY_NE          : '!='                             ;

OP_RELATIONAL_GT        : '>'                              ;
OP_RELATIONAL_LT        : '<'                              ;
OP_RELATIONAL_GE        : '>='                             ;
OP_RELATIONAL_LE        : '<='                             ;

OP_CONDITIONAL_AND      : '&&'                             ;
OP_CONDITIONAL_OR       : '||'                             ;
OP_CONDITIONAL_NOT      : '!'                              ;

OP_MATH_PLUS            : '+'                              ;
OP_MATH_MINUS           : '-'                              ;
OP_MATH_MULTIPLICATION  : '*'                              ;
OP_MATH_DIVISION        : '/'                              ;
OP_MATH_REMAINDER       : '%'                              ;
OP_MATH_INCREMENT       : '++'                             ;
OP_MATH_DECREMENT       : '--'                             ;

OP_BITWISE_AND          : '&'                              ;
OP_BITWISE_OR           : '|'                              ;
OP_BITWISE_NOT          : '~'                              ;
OP_BITWISE_XOR          : '^'                              ;
OP_BITWISE_SHL          : '<<'                             ;

// Following operators are conflict with Java Generic Type definition.
// Sample: List<List<String>>
//OP_BITWISE_SHR        : '>>'                             ;
//OP_BITWISE_SHR_2      : '>>>'                            ;

OP_INSTANCEOF           : 'instanceof' | 'is'              ;
OP_NEW                  : 'new'                            ;
OP_CONDITIONAL_TERNARY  : '?'                              ;

COMMA                   : ','                              ;
COLON                   : ':'                              ;
AT                      : '@'                              ;

KEYWORD_TRUE            : 'true'                           ;
KEYWORD_FALSE           : 'false'                          ;
KEYWORD_NULL            : 'null'                           ;

IMPORT_REF              : ID '::'                          ;
IDENTIFIER              : ID                               ;

INTEGER                 : INT [lLfFdD]?                    ;
INTEGER_HEX             : '0x' HEX+ [lL]?                  ;
FLOATING_POINT          : INT ('.' FRAC)? EXP? [fFdD]?     ;
fragment INT            : '0' | [1-9] [0-9]*               ;
fragment FRAC           : [0-9]+                           ;
fragment EXP            : [Ee] [+\-]? INT                  ;

STRING_DOUBLE           : '"'  (ESC|OTHERS)*? '"'          ;
STRING_SINGLE           : '\'' (ESC|OTHERS)*? '\''         ;
fragment OTHERS         : ~('\\' | '\r' | '\n')            ;
fragment ESC            : '\\' ([btnfr"'\\]|UNICODE)       ;
fragment UNICODE        : 'u' HEX HEX HEX HEX              ;
fragment HEX            : [0-9a-fA-F]                      ;
