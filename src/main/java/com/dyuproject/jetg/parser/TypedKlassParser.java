/**
 * jetg
 * Copyright 2015-2016 David Yu
 *
 * Copyright 2010-2014 Guoqiang Chen. All rights reserved.
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
package com.dyuproject.jetg.parser;

import java.util.ArrayList;
import java.util.List;
import com.dyuproject.jetg.parser.support.TypedKlass;

/**
 * 将带有泛型的类声明转化为 TypedKlass。
 * 采用 LL(1) 递归下降的语法解析器， Lexer 简化为 char[]， 其中 Token 简化为 char
 */
public class TypedKlassParser {
    private static final char EOF = '\0';
    private static final char SPACE = ' ';

    private final VariableResolver resolver;
    private final char[] input;
    private int pointer = 0;
    private char lookahead;

    protected TypedKlassParser(char[] input, VariableResolver resolver) {
        this.input = input;
        this.resolver = resolver;
        consume(true);
    }

    protected TypedKlass asTypedKlass() {
        TypedKlass type = type();
        match(EOF);
        return type;
    }

    // type: name type_arguments? type_array_suffix?
    private TypedKlass type() {
        String name = name();
        TypedKlass[] typeArgs = null;

        if (lookahead == '<') {
            typeArgs = type_arguments();
        }
        while (lookahead == '[') {
            type_array_suffix();
            name = name + "[]";
        }

        Class<?> klass = resolver.resolveClass(name);
        if (klass == null) return null;
        return TypedKlass.create(klass, typeArgs);
    }

    // name: label ("." label)*
    private String name() {
        StringBuilder sb = new StringBuilder();
        sb.append(label());
        while (lookahead == '.') {
            consume(true);
            sb.append('.');
            sb.append(label());
        }
        return sb.toString();
    }

    // label: [_a-zA-Z0-9]+
    private String label() {
        StringBuilder sb = new StringBuilder();
        do {
            if (lookahead >= 'a' && lookahead <= 'z') {
                sb.append(lookahead);
                consume(false);
            } else if (lookahead >= 'A' && lookahead <= 'Z') {
                sb.append(lookahead);
                consume(false);
            } else if (lookahead >= '0' && lookahead <= '9') {
                sb.append(lookahead);
                consume(false);
            } else if (lookahead == '_') {
                sb.append(lookahead);
                consume(false);
            } else if (lookahead == SPACE) {
                consume(true);
                if (sb.length() > 0) {
                    break;
                }
            } else {
                break;
            }
        } while (true);

        if (sb.length() == 0) {
            throw new SyntaxErrorException("Syntax error, no lable found.");
        }
        return sb.toString();
    }

    // type_arguments: '<' type_list '>'
    private TypedKlass[] type_arguments() {
        match('<');
        TypedKlass[] typeArgs = type_list();
        match('>');
        return typeArgs;
    }

    // type_list: type_name (',' type_name)*
    private TypedKlass[] type_list() {
        List<TypedKlass> typeArgs = new ArrayList<TypedKlass>();
        typeArgs.add(type());
        while (lookahead == ',') {
            consume(true);
            typeArgs.add(type_name());
        }
        return (TypedKlass[]) typeArgs.toArray(TypedKlass.EMPTY_TYPE_ARGS);
    }

    // type_name: type | '?'
    private TypedKlass type_name() {
        if (lookahead == '?') {
            consume(true);
            return TypedKlass.WildcharTypedKlass;
        } else {
            return type();
        }
    }

    // type_array_suffix: '[' ']'
    private void type_array_suffix() {
        match('[');
        match(']');
    }

    private void match(char c) {
        if (lookahead == SPACE) {
            consume(true);
        }
        if (lookahead == c) {
            consume(true);
        } else {
            throw new SyntaxErrorException("Syntax error. match '" + lookahead + "', expected '" + c + "'");
        }
    }

    private void consume(boolean skipSpace) {
        do {
            if (pointer >= input.length) {
                lookahead = EOF;
                return;
            } else {
                lookahead = input[pointer++];
            }

            if (lookahead == ' ' || lookahead == '\t') {
                lookahead = SPACE;
            }

            if (lookahead != SPACE || skipSpace) {
                return;
            }
        } while (true);
    }
}
