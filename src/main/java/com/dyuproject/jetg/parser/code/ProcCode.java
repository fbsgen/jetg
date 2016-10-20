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

import java.util.List;

/**
 * TODO
 * 
 * @author David Yu
 * @created Nov 23, 2015
 */
public class ProcCode extends MacroCode
{
    
    public String returnType;

    public ProcCode()
    {
        super(new MethodCode(new ScopeCode(null, ""), "    ", false, true));
    }
    
    public boolean hasArgs()
    {
        return /*defineListCode != null && */defineListCode.size() != 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(512)
            .append("  // line: ").append(line).append('\n');
        
        List<SegmentCode> children = defineListCode.getChildren();
        int offset = 0, limit = children.size();
        if (returnType != null) {
            sb.append("  public static ")
                .append(returnType).append(' ').append(name).append('(')
                .append(children.get(0).toString());
            
            offset = 1;
        } else {
            sb.append("  public static void ")
                .append(name).append("(final JetWriter $out");
        }

        if (offset != limit) {
            sb.append(',').append(' ').append(defineListCode.toString(children, offset, limit));
        }
        
        if (returnType != null)
            sb.append(") { // line: ");
        else
            sb.append(") throws Throwable { // line: ");
        
        sb.append(line).append('\n')
            .append(methodCode.toString());
        
        if (returnType != null)
            sb.append('\n');
        
        sb.append("  }\n");
        
        int optionalCount = defineListCode.optionalCount;
        if (optionalCount != 0)
            addOverloadTo(sb, children, optionalCount);
        
        return sb.toString();
    }
    
    private void appendTo(StringBuilder sb, String[] items, int offset, int limit) {
        while (offset != limit) {
            sb.append(", ").append(items[offset++]);
        }
    }
    
    private void addOverloadTo(final StringBuilder sb, 
            final List<SegmentCode> children, final int optionalCount) {
        final String[] exprs = new String[optionalCount],
                vars = new String[children.size()];
        final int limit = children.size() - optionalCount;
        String source;
        for (int i = 0, j = 0, start = limit, len = vars.length; i < len; i++) {
            source = children.get(i).toString();
            vars[i] = source.substring(source.indexOf(' ')+1);
            if (i >= limit)
                exprs[j++] = ((DefineExpressionCode)children.get(start++)).expr.toString();
        }
        
        for (int i = 0; i < optionalCount; i++) {
            if (returnType != null) {
                sb.append("\n  public static ")
                    .append(returnType).append(' ').append(name).append('(');
            } else {
                sb.append("\n  public static void ")
                    .append(name).append("(final JetWriter $out, ");
            }
            
            sb.append(defineListCode.toString(children, 0, limit + i));
            
            if (returnType != null)
                sb.append(") { // line: ");
            else
                sb.append(") throws Throwable { // line: ");
            
            sb.append(line).append("\n    ");
            
            if (returnType != null)
                sb.append("return ").append(name).append('(').append(vars[0]);
            else
                sb.append(name).append("($out, ").append(vars[0]);
            
            appendTo(sb, vars, 1, limit + i);
            appendTo(sb, exprs, i, optionalCount);
            
            sb.append(");\n  }\n");
        }
    }

}
