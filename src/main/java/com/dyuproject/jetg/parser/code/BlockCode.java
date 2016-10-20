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
package com.dyuproject.jetg.parser.code;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于给支持作用域的块节点，支持将如多行代码，每行代码自动用"\n"分割
 */
public class BlockCode extends Code {
    private final List<Code> children;
    private final String indent;
    public boolean singlelineBlockWithEnd;

    public BlockCode(int initialCapacity, String indent) {
        this.children = new ArrayList<Code>(initialCapacity);
        this.indent = indent;
    }
    
    public boolean wrapsProc()
    {
        return children.size() == 2 && children.get(0).proc && 
                children.get(1) instanceof LineCode && 
                children.get(1).toString().endsWith("$out.println();\n");
    }

    public void addChild(Code code) {
        children.add(code);
    }
    
    public void removeLastChild() {
        children.remove(children.size()-1);
    }

    public void addLine(String source) {
        children.add(new LineCode(indent + source + "\n"));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(children.size() * 64);
        for (Code code : children) {
            sb.append(code.toString());
        }
        return sb.toString();
    }
}
