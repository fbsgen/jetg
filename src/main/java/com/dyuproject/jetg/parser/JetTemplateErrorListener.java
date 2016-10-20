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

import com.dyuproject.jetg.utils.StringUtils;
import org.antlr.v4.runtime.*;

public class JetTemplateErrorListener extends BaseErrorListener {
    private static JetTemplateErrorListener instance = new JetTemplateErrorListener();

    public static JetTemplateErrorListener getInstance() {
        return instance;
    }

    private JetTemplateErrorListener() {
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        CharStream stream;
        if (recognizer instanceof Parser) {
            stream = ((Parser) recognizer).getInputStream().getTokenSource().getInputStream();
        } else if (recognizer instanceof Lexer) {
            stream = ((Lexer) recognizer).getInputStream();
        } else {
            throw new IllegalStateException();
        }

        String input = stream.toString();
        String filename = stream.getSourceName();

        String[] sourceLines = input.split("\r?\n", -1);
        Token offendingToken = (Token) offendingSymbol;

        StringBuilder sb = new StringBuilder(128);
        sb.append("Template: ");
        sb.append(filename);
        sb.append(": ");
        sb.append(line);
        sb.append(',');
        sb.append(charPositionInLine);
        sb.append("\nmessage: ");
        sb.append(msg);
        sb.append('\n');
        if (offendingToken != null)
            sb.append(StringUtils.getPrettyError(sourceLines, line, charPositionInLine + 1, offendingToken.getStartIndex(), offendingToken.getStopIndex(), 5));

        if (e != null) {
            throw new SyntaxErrorException(sb.toString(), e);
        } else {
            throw new SyntaxErrorException(sb.toString());
        }
    }
}
