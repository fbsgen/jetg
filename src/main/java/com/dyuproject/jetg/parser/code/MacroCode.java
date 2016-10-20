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

public class MacroCode extends Code {
    protected final MethodCode methodCode;
    protected String name;
    protected SegmentListCode defineListCode;
    protected int line;
    
    public MacroCode(MethodCode methodCode) {
        this.methodCode = methodCode;
    }

    public MacroCode(ScopeCode scopeCode) {
        this(new MethodCode(scopeCode, "    ", false));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefineListCode(SegmentListCode defineListCode) {
        this.defineListCode = defineListCode;
    }

    public SegmentListCode getDefineListCode() {
        return defineListCode;
    }

    public MethodCode getMethodCode() {
        return methodCode;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(512);
        sb.append("  // line: ").append(line).append('\n');
        sb.append("  protected static void $macro_");
        sb.append(name);
        sb.append("(final JetPageContext $ctx");
        if (defineListCode != null && defineListCode.size() > 0) {
            sb.append(',').append(' ').append(defineListCode.toString());
        }
        sb.append(") throws Throwable { // line: ");
        sb.append(line).append('\n');
        sb.append(methodCode.toString());
        sb.append("  }\n");
        return sb.toString();
    }
}
