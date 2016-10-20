//========================================================================
//Copyright 2015 David Yu
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.jetg.parser.code;

import static com.dyuproject.jetg.JetUtil.repeat;

/**
 * TODO
 * 
 * @author David Yu
 * @created Nov 27, 2015
 */
public class EmitCode extends Code
{
    
    private static final char[] INDENT = repeat(' ', 512);
    
    final int indent;
    final String text;

    public EmitCode(int indent, String text)
    {
        this.indent = indent;
        this.text = text;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(indent + text.length())
                .append(INDENT, 0, indent)
                .append(text)
                .toString();
    }
    
}
