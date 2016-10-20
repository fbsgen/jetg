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
parser grammar JetTemplateParser;

options {
    tokenVocab = JetTemplateLexer; // use tokens from JetTemplateLexer.g4
}

/*
@header {
package com.dyuproject.jetg.parser;
}
*/

// -------- rule ---------------------------------------
template    :   header_directive* macro_directive* block proc_directive* EOF
            ;

header_directive
            :   TEXT_NEWLINE* TEXT_DIRECTIVE_LIKE TEXT_PLAIN TEXT_SINGLE_HASH? TEXT_NEWLINE
            ;

macro_directive
            :   DIRECTIVE_OPEN_MACRO define_expression_list? ')' content_block DIRECTIVE_END TEXT_NEWLINE
            ;
            
content_block
            :   (text | text_newline | value | directive)*
            ;

block       :   (text | text_newline | value | directive)*
            ;

proc_directive
            :   DIRECTIVE_OPEN_PROC arg_decl_expression_list? (proc_emit_block | proc_ignore_newline_block | proc_block)
                DIRECTIVE_END TEXT_NEWLINE*
            ;

proc_emit_block
            :   RETURN_TYPE_START TEXT_PLAIN TEXT_NEWLINE (text | text_newline)*
            ;

proc_ignore_newline_block
            :   BLOCK_IGNORE_NEWLINE TEXT_NEWLINE (text | text_newline | value | proc_content_directive)*
            ;

proc_block  :   ')' (text | text_newline | value | proc_content_directive)*
            ;

emit_directive
            :   V_EMIT emit_block V_END
            ;

emit_block
            :   (text | text_newline)*
            ;

proc_content_directive
            :   alt_block_directive
            |   emit_directive
            |   block_directive
            |   control_directive
            |   set_directive
            |   invalid_context_directive
            |   misplaced_directive
            ;

text        :   TEXT_PLAIN
            |   TEXT_CDATA
            |   TEXT_SINGLE_BACKSLASH
            |   TEXT_SINGLE_HASH
            |   TEXT_ESCAPED_CHAR
            |   TEXT_ESCAPED_NEWLINE
            |   TEXT_DIRECTIVE_LIKE
            |   VALUE_CLOSE
            |   VALUE_OPEN
            ;
            
text_newline:   TEXT_NEWLINE
            ;

value       :   (VALUE_ESCAPED_OPEN|VALUE_OPEN) expression value_iteration? value_options? VALUE_CLOSE
            ;

value_iteration
            :   COLON type COLON expression
            ;

value_options
            :   SEMI_COLON O_KEY OP_ASSIGN expression
            ;

directive   :   alt_block_directive  
            |   block_directive
            |   control_directive
            |   context_directive
            |   misplaced_directive
            ;

context_directive
            :   define_directive
            |   put_directive
            |   set_directive
            |   include_directive
            |   call_directive
            |   tag_directive
            |   invalid_context_directive   
            ;

control_directive
            :   stop_directive
            |   break_directive
            |   continue_directive
            |   invalid_control_directive
            ;

alt_block_directive
            :   alt_if_directive
            |   alt_for_directive
            ;

block_directive
            :   if_directive
            |   for_directive
            |   invalid_block_directive
            ;

define_directive
            :   DIRECTIVE_OPEN_DEFINE define_expression_list ')'
            ;
define_expression_list
            :   define_expression (',' define_expression)*
            ;
define_expression
            :   type IDENTIFIER
            ;

set_directive
            :   DIRECTIVE_OPEN_SET set_expression (',' set_expression)* ')'
            ;
set_expression
            :   type? IDENTIFIER OP_ASSIGN expression
            ;

put_directive
            :   DIRECTIVE_OPEN_PUT expression (',' expression)* ')'
            ;

arg_decl_expression_list
            :   define_expression (',' optional_define_expression)*
            ;

optional_define_expression
            :   assign_expression
            |   define_expression
            ;
            
assign_expression
            :   type? IDENTIFIER OP_ASSIGN expression
            ;

// -------------------------------

alt_if_directive
            :   V_IF expression ')' VALUE_CLOSE block alt_elseif_directive* alt_else_directive? V_ENDIF TEXT_NEWLINE?
            ;
alt_elseif_directive
            :   V_ELSEIF expression ')' VALUE_CLOSE block
            ;
alt_else_directive
            :   V_ELSE block
            ;
alt_for_directive
            :   V_FOR for_expression ')' VALUE_CLOSE block alt_else_directive? V_ENDFOR
            ;

// -------------------------------

if_directive
            :   DIRECTIVE_OPEN_IF expression ')' block elseif_directive* else_directive? DIRECTIVE_ENDIF TEXT_NEWLINE?
            ;
elseif_directive
            :   DIRECTIVE_OPEN_ELSEIF expression ')' block
            ;
else_directive
            :   DIRECTIVE_ELSE block
            ;

for_directive
            :   DIRECTIVE_OPEN_FOR for_expression ')' block else_directive? DIRECTIVE_ENDFOR
            ;
for_expression
            :   type? IDENTIFIER ':' expression
            ;

break_directive
            :   DIRECTIVE_OPEN_BREAK expression? ')'
            |   DIRECTIVE_BREAK
            ;
continue_directive
            :   DIRECTIVE_OPEN_CONTINUE expression? ')'
            |   DIRECTIVE_CONTINUE
            ;
stop_directive
            :   DIRECTIVE_OPEN_STOP expression? ')'
            |   DIRECTIVE_STOP
            ;

include_directive
            :   DIRECTIVE_OPEN_INCLUDE expression_list ')'
            ;

call_directive
            :   DIRECTIVE_OPEN_CALL expression_list? ')'
            ;

tag_directive
            :   DIRECTIVE_OPEN_TAG expression_list? ')' content_block DIRECTIVE_END
            ;

invalid_context_directive
            :   DIRECTIVE_DEFINE
            |   DIRECTIVE_PUT
            |   DIRECTIVE_SET
            |   DIRECTIVE_INCLUDE
            |   DIRECTIVE_CALL
            |   DIRECTIVE_TAG
            ;

invalid_control_directive
            :   DIRECTIVE_STOP
            |   DIRECTIVE_BREAK
            |   DIRECTIVE_CONTINUE
            ;

invalid_block_directive
            :   DIRECTIVE_IF
            |   DIRECTIVE_ELSEIF
            |   DIRECTIVE_FOR
            ;

misplaced_directive
            :   DIRECTIVE_MACRO
            ;

expression  :   '(' expression ')'                                           # expr_group
            |   constant                                                     # expr_constant
            |   IDENTIFIER                                                   # expr_identifier
            |   '[' expression_list? ']'                                     # expr_array_list
            |   '{' hash_map_entry_list? '}'                                 # expr_hash_map
            |   expression ('.'|'?.') IDENTIFIER                             # expr_field_access
            |   expression ('.'|'?.') IDENTIFIER '(' expression_list? ')'    # expr_method_invocation
            |   IMPORT_REF? IDENTIFIER '(' expression_list? ')'              # expr_function_call
            |   static_type_name '.' IDENTIFIER                              # expr_static_field_access
            |   static_type_name '.' IDENTIFIER  '(' expression_list? ')'    # expr_static_method_invocation
            |   expression ('?')? '[' expression ']'                         # expr_array_get
            |   expression ('++'|'--')                                       # expr_math_unary_suffix
            |   ('+' |'-' )     expression                                   # expr_math_unary_prefix
            |   ('++'|'--')     expression                                   # expr_math_unary_prefix
            |   '~'             expression                                   # expr_math_unary_prefix
            |   '!'             expression                                   # expr_compare_not
            |   '(' type ')'    expression                                   # expr_class_cast
            |   'new' type '('  expression_list? ')'                         # expr_new_object
            |   'new' type ('[' expression ']')+                             # expr_new_array
            |   expression ('*'|'/'|'%')  expression                         # expr_math_binary_basic
            |   expression ('+'|'-')      expression                         # expr_math_binary_basic
            |   expression ('<<'|'>' '>'|'>' '>' '>') expression             # expr_math_binary_shift
            |   expression ('>='|'<='|'>'|'<') expression                    # expr_compare_relational
            |   expression OP_INSTANCEOF type                                # expr_instanceof
            |   expression ('=='|'!=') expression                            # expr_compare_equality
            |   expression '&'  expression                                   # expr_math_binary_bitwise
            |   <assoc=right> expression '^' expression                      # expr_math_binary_bitwise
            |   expression '|'  expression                                   # expr_math_binary_bitwise
            |   expression '&&' expression                                   # expr_compare_condition
            |   expression '||' expression                                   # expr_compare_condition
            |   <assoc=right> expression '?' expression ':' expression       # expr_conditional_ternary
            ;

constant    :   STRING_DOUBLE
            |   STRING_SINGLE
            |   INTEGER
            |   INTEGER_HEX
            |   FLOATING_POINT
            |   KEYWORD_TRUE
            |   KEYWORD_FALSE
            |   KEYWORD_NULL
            ;

expression_list
            :   expression (',' expression)*
            ;

hash_map_entry_list
            :   expression ':' expression (',' expression ':' expression)*
            ;

static_type_name
            : '@' IDENTIFIER
            | '@' '(' IDENTIFIER ('.' IDENTIFIER)* ')'
            ;

type        :   IDENTIFIER ('.' IDENTIFIER)* type_arguments? type_array_suffix*
            ;

type_array_suffix
            :   '[' ']'
            ;

type_arguments
            :   '<' type_list '>'
            ;
type_list   :   type_name (',' type_name)*
            ;

type_name   :   type | '?'
            ;



